package top.soaringlab.longtailed.compilerbackend.compiler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import top.soaringlab.longtailed.compilerbackend.domain.PublicApi;
import top.soaringlab.longtailed.compilerbackend.dsl.simple.SimpleTransformer;
import top.soaringlab.longtailed.compilerbackend.dsl.simple.SimpleTranslator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.*;

public class Compiler {

    private String xmlnsBpmn; //  = "bpmn:"

    private String xmlnsBpmndi = "bpmndi:";

    private String xmlnsDc = "dc:";

    private String xmlnsDi = "di:";

    private String[] taskNames;

    private String processName;

    private String scriptLanguage = "groovy";

    private String scriptContext = "execution";

    private String defaultScript = " ";

    private Map<String, String> elementIdTextContentMap = new HashMap<>();

    private List<Element> taskList = new ArrayList<>();

    private String newIdPrefix = "compiler_";

    private int newIdSuffix = 0;

    public String processEngine = "";

    public List<PublicApi> publicApiList = new ArrayList<>();

    public String compile(String file) throws Exception {
        // parse the xml
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(file)));
        document.getDocumentElement().setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        // name
        String tagName = document.getDocumentElement().getTagName();
        if (tagName.equals("bpmn:definitions")) {
            xmlnsBpmn = "bpmn:";
        } else if (tagName.equals("bpmn2:definitions")) {
            xmlnsBpmn = "bpmn2:";
        } else {
            xmlnsBpmn = tagName.split(":")[0] + ":";
        }
        taskNames = new String[]{
                xmlnsBpmn + "scriptTask",
                xmlnsBpmn + "task",
                xmlnsBpmn + "sendTask",
                xmlnsBpmn + "receiveTask",
                xmlnsBpmn + "userTask",
                xmlnsBpmn + "manualTask",
                xmlnsBpmn + "businessRuleTask",
                xmlnsBpmn + "serviceTask"
        };
        processName = xmlnsBpmn + "process";
        if (processEngine.equals("functional")) {
            scriptLanguage = "dsl";
        } else if (processEngine.equals("jbpm")) {
            scriptLanguage = "http://www.javascript.com/javascript";
            scriptContext = "kcontext";
        }

        // find textAnnotations
        List<Element> textAnnotationList = nodeListToElementList(document.getElementsByTagName(xmlnsBpmn + "textAnnotation"));
        Map<String, String> textAnnotationIdTextContentMap = new HashMap<>();
        for (Element textAnnotation : textAnnotationList) {
            String textAnnotationId = textAnnotation.getAttribute("id");
            String textAnnotationTextContent = textAnnotation.getTextContent();
            textAnnotationIdTextContentMap.put(textAnnotationId, textAnnotationTextContent);
        }

        // find associations
        List<Element> associationList = nodeListToElementList(document.getElementsByTagName(xmlnsBpmn + "association"));
        for (Element association : associationList) {
            String sourceRef = association.getAttribute("sourceRef");
            String targetRef = association.getAttribute("targetRef");
            String textContent = elementIdTextContentMap.getOrDefault(sourceRef, "");
            textContent += textAnnotationIdTextContentMap.get(targetRef) + "\n";
            elementIdTextContentMap.put(sourceRef, textContent);
        }

        // find participants
        List<Element> participantList = nodeListToElementList(document.getElementsByTagName(xmlnsBpmn + "participant"));
        for (Element participant : participantList) {
            String participantId = participant.getAttribute("id");
            String processRef = participant.getAttribute("processRef");
            if (elementIdTextContentMap.containsKey(participantId)) {
                String textContent = elementIdTextContentMap.get(participantId);
                elementIdTextContentMap.put(processRef, textContent);
            }
        }

        // find tasks
        for (String taskName : taskNames) {
            taskList.addAll(nodeListToElementList(document.getElementsByTagName(taskName)));
        }

        convertTask(document);

//        convertProcess(document);

        // output the new xml
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));
        return byteArrayOutputStream.toString();
    }

    private List<Element> nodeListToElementList(NodeList nodeList) {
        List<Element> elementList = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            elementList.add(element);
        }
        return elementList;
    }

    private void convertTask(Document document) {
        for (Element task : taskList) {
            String taskId = task.getAttribute("id");
            if (elementIdTextContentMap.containsKey(taskId)) {
                String textContent = elementIdTextContentMap.get(taskId);
                Element process = (Element) task.getParentNode();

                Map<String, List<String>> insertBeforeMap = new HashMap<>();
                Map<String, List<String>> insertAfterMap = new HashMap<>();
                Map<String, List<String>> replaceMap = new HashMap<>();

                // preprocess
                {
                    List<String> result = transformScript(textContent);
                    String condition = "";
                    String type = "";
                    String value = "";
                    int state = 0; // 2 condition, 3 action type, 4 action value
                    for (String s : result) {
                        if (s.equals("input")) {
                            condition = "";
                        } else if (s.equals("condition")) {
                            state = 2;
                        } else if (s.equals("action")) {
                            state = 3;
                        } else if (state == 2) {
                            condition = s;
                        } else if (state == 3) {
                            type = s;
                            state = 4;
                        } else if (state == 4) {
                            value = s;
                            if (type.equals("INSERT BEFORE")) {
                                if (!insertBeforeMap.containsKey(condition)) {
                                    insertBeforeMap.put(condition, new ArrayList<>());
                                }
                                insertBeforeMap.get(condition).add(value);
                            } else if (type.equals("INSERT AFTER")) {
                                if (!insertAfterMap.containsKey(condition)) {
                                    insertAfterMap.put(condition, new ArrayList<>());
                                }
                                insertAfterMap.get(condition).add(value);
                            } else if (type.equals("SKIP")) {
                                if (!replaceMap.containsKey(condition)) {
                                    replaceMap.put(condition, new ArrayList<>());
                                }
                                replaceMap.get(condition).add("compiler_SKIP");
                            } else if (type.equals("REPLACE")) {
                                if (!replaceMap.containsKey(condition)) {
                                    replaceMap.put(condition, new ArrayList<>());
                                }
                                replaceMap.get(condition).add(value);
                            } else if (type.equals("ABORT")) {
                                if (!replaceMap.containsKey(condition)) {
                                    replaceMap.put(condition, new ArrayList<>());
                                }
                                replaceMap.get(condition).add("compiler_ABORT");
                            }
                        }
                    }
                }

                // modify variables
                {
                    String scriptTaskId = getNewId();
                    String endFlowId = getNewId();

                    List<String> sequenceFlowInIdList = insertBeforeNode(document, task, scriptTaskId, endFlowId);

                    String script = convertScript(defaultScript + textContent);
                    Element scriptTask = createScriptTask(document, scriptTaskId, sequenceFlowInIdList, List.of(endFlowId), script);
                    process.insertBefore(scriptTask, task);

                    Element endFlow = createSequenceFlow(document, endFlowId, scriptTaskId, taskId);
                    process.insertBefore(endFlow, task);

                    shapeReference(document, scriptTaskId, taskId);
                    edgeReference(document, endFlowId, taskId);
                }

                // insert before
                if (!insertBeforeMap.isEmpty()) {
                    String splitGatewayId = getNewId();
                    String defaultFlowId = getNewId();
                    String mergeGatewayId = getNewId();
                    String endFlowId = getNewId();

                    List<String> sequenceFlowInIdList = insertBeforeNode(document, task, splitGatewayId, endFlowId);

                    Element splitGateway = createSplitGateway(document, splitGatewayId, sequenceFlowInIdList, List.of(defaultFlowId));
                    splitGateway.setAttribute("default", defaultFlowId);
                    process.insertBefore(splitGateway, task);

                    Element defaultFlow = createSequenceFlow(document, defaultFlowId, splitGatewayId, mergeGatewayId);
                    process.insertBefore(defaultFlow, task);

                    Element mergeGateway = createMergeGateway(document, mergeGatewayId, List.of(defaultFlowId), List.of(endFlowId));
                    process.insertBefore(mergeGateway, task);

                    Element endFlow = createSequenceFlow(document, endFlowId, mergeGatewayId, taskId);
                    process.insertBefore(endFlow, task);

                    shapeReference(document, splitGatewayId, taskId);
                    edgeReference(document, defaultFlowId, taskId);
                    shapeReference(document, mergeGatewayId, taskId);
                    edgeReference(document, endFlowId, taskId);

                    for (Map.Entry<String, List<String>> entry : insertBeforeMap.entrySet()) {
                        String condition = entry.getKey();
                        List<String> taskNameList = entry.getValue();
                        addThread(document, splitGateway, mergeGateway, condition, taskNameList);
                    }
                }

                // insert after
                if (!insertAfterMap.isEmpty()) {
                    String beginFlowId = getNewId();
                    String splitGatewayId = getNewId();
                    String defaultFlowId = getNewId();
                    String mergeGatewayId = getNewId();

                    List<String> sequenceFlowOutIdList = insertAfterNode(document, task, beginFlowId, mergeGatewayId);

                    Element beginFlow = createSequenceFlow(document, beginFlowId, taskId, splitGatewayId);
                    process.insertBefore(beginFlow, task);

                    Element splitGateway = createSplitGateway(document, splitGatewayId, List.of(beginFlowId), List.of(defaultFlowId));
                    splitGateway.setAttribute("default", defaultFlowId);
                    process.insertBefore(splitGateway, task);

                    Element defaultFlow = createSequenceFlow(document, defaultFlowId, splitGatewayId, mergeGatewayId);
                    process.insertBefore(defaultFlow, task);

                    Element mergeGateway = createMergeGateway(document, mergeGatewayId, List.of(defaultFlowId), sequenceFlowOutIdList);
                    process.insertBefore(mergeGateway, task);

                    edgeReference(document, beginFlowId, taskId);
                    shapeReference(document, splitGatewayId, taskId);
                    edgeReference(document, defaultFlowId, taskId);
                    shapeReference(document, mergeGatewayId, taskId);

                    for (Map.Entry<String, List<String>> entry : insertAfterMap.entrySet()) {
                        String condition = entry.getKey();
                        List<String> taskNameList = entry.getValue();
                        addThread(document, splitGateway, mergeGateway, condition, taskNameList);
                    }
                }

                // replace
                if (!replaceMap.isEmpty()) {
                    String splitGatewayId = getNewId();
                    String inFlowId = getNewId();
                    String outFlowId = getNewId();
                    String mergeGatewayId = getNewId();

                    List<String> sequenceFlowInIdList = insertBeforeNode(document, task, splitGatewayId, inFlowId);
                    List<String> sequenceFlowOutIdList = insertAfterNode(document, task, outFlowId, mergeGatewayId);

                    Element splitGateway = createSplitGateway(document, splitGatewayId, sequenceFlowInIdList, List.of(inFlowId));
                    splitGateway.setAttribute("default", inFlowId);
                    process.insertBefore(splitGateway, task);

                    Element inFlow = createSequenceFlow(document, inFlowId, splitGatewayId, taskId);
                    process.insertBefore(inFlow, task);

                    Element outFlow = createSequenceFlow(document, outFlowId, taskId, mergeGatewayId);
                    process.insertBefore(outFlow, task);

                    Element mergeGateway = createMergeGateway(document, mergeGatewayId, List.of(outFlowId), sequenceFlowOutIdList);
                    process.insertBefore(mergeGateway, task);

                    shapeReference(document, splitGatewayId, taskId);
                    edgeReference(document, inFlowId, taskId);
                    edgeReference(document, outFlowId, taskId);
                    shapeReference(document, mergeGatewayId, taskId);

                    for (Map.Entry<String, List<String>> entry : replaceMap.entrySet()) {
                        String condition = entry.getKey();
                        List<String> taskNameList = entry.getValue();
                        addThread(document, splitGateway, mergeGateway, condition, taskNameList);
                    }
                }
            }
        }
    }

    private void convertProcess(Document document) {
        // find processes
        List<Element> processList = nodeListToElementList(document.getElementsByTagName(processName));
        for (Element process : processList) {
            String processId = process.getAttribute("id");

            String messageId = processId + "_message";
            String subProcessId = processId + "_subProcess";
            String startEventId = subProcessId + "_startEvent";
            String messageEventDefinitionId = subProcessId + "_messageEventDefinition";
            String sequenceFlow1Id = subProcessId + "_sequenceFlow1";
            String scriptTaskId = subProcessId + "_scriptTask";
            String sequenceFlow2Id = subProcessId + "_sequenceFlow2";
            String endEventId = subProcessId + "_endEvent";

            String script = defaultScript + elementIdTextContentMap.getOrDefault(processId, "");
            script = convertScript(script);

            Element subProcess = document.createElement(xmlnsBpmn + "subProcess");
            subProcess.setAttribute("id", processId + "_subProcess");
            subProcess.setAttribute("triggeredByEvent", "true");

            Element startEvent = createStartEvent(document, startEventId, sequenceFlow1Id);
            startEvent.appendChild(createMessageEventDefinition(document, messageEventDefinitionId, messageId));
            subProcess.appendChild(startEvent);
            subProcess.appendChild(createSequenceFlow(document, sequenceFlow1Id, startEventId, scriptTaskId));
            subProcess.appendChild(createScriptTask(document, scriptTaskId, List.of(sequenceFlow1Id), List.of(sequenceFlow2Id), script));
            subProcess.appendChild(createSequenceFlow(document, sequenceFlow2Id, scriptTaskId, endEventId));
            subProcess.appendChild(createEndEvent(document, endEventId, sequenceFlow2Id));

            process.insertBefore(subProcess, process.getElementsByTagName(xmlnsBpmn + "startEvent").item(0));
            Element message = createMessage(document, messageId, messageId);
            Element definitions = (Element) process.getParentNode();
            definitions.insertBefore(message, definitions.getElementsByTagName(xmlnsBpmn + "process").item(0));
        }
    }

    private List<String> insertBeforeNode(Document document, Element node, String beginId, String endId) {
        String nodeId = node.getAttribute("id");

        List<Element> sequenceFlowList = nodeListToElementList(document.getElementsByTagName(xmlnsBpmn + "sequenceFlow"));
        for (Element sequenceFlow : sequenceFlowList) {
            String sequenceFlowId = sequenceFlow.getAttribute("id");
            String targetRef = sequenceFlow.getAttribute("targetRef");
            if (targetRef.equals(nodeId)) {
                sequenceFlow.setAttribute("targetRef", beginId);
            }
        }

        List<String> sequenceFlowInIdList = new ArrayList<>();
        List<Element> incomingList = nodeListToElementList(node.getElementsByTagName(xmlnsBpmn + "incoming"));
        int i = 0;
        for (Element incoming : incomingList) {
            sequenceFlowInIdList.add(incoming.getTextContent());
            if (i++ == 0) {
                incoming.setTextContent(endId);
            } else {
                node.removeChild(incoming);
            }
        }

        return sequenceFlowInIdList;
    }

    private List<String> insertAfterNode(Document document, Element node, String beginId, String endId) {
        String nodeId = node.getAttribute("id");

        List<Element> sequenceFlowList = nodeListToElementList(document.getElementsByTagName(xmlnsBpmn + "sequenceFlow"));
        for (Element sequenceFlow : sequenceFlowList) {
            String sequenceFlowId = sequenceFlow.getAttribute("id");
            String sourceRef = sequenceFlow.getAttribute("sourceRef");
            if (sourceRef.equals(nodeId)) {
                sequenceFlow.setAttribute("sourceRef", endId);
            }
        }

        List<String> sequenceFlowOutIdList = new ArrayList<>();
        List<Element> outgoingList = nodeListToElementList(node.getElementsByTagName(xmlnsBpmn + "outgoing"));
        int i = 0;
        for (Element outgoing : outgoingList) {
            sequenceFlowOutIdList.add(outgoing.getTextContent());
            if (i++ == 0) {
                outgoing.setTextContent(beginId);
            } else {
                node.removeChild(outgoing);
            }
        }

        return sequenceFlowOutIdList;
    }

    private void addThread(Document document, Element splitGateway, Element mergeGateway, String condition, List<String> taskNameList) {
        Element process = (Element) splitGateway.getParentNode();
        String splitGatewayId = splitGateway.getAttribute("id");
        String mergeGatewayId = mergeGateway.getAttribute("id");
        String currentTaskId = splitGatewayId;
        String currentFlowId = getNewId();

        Element outgoing = document.createElement(xmlnsBpmn + "outgoing");
        outgoing.setTextContent(currentFlowId);
        splitGateway.appendChild(outgoing);

        int i = 0;
        for (String taskName : taskNameList) {
            String taskId = null;

            if (taskName.equals("compiler_SKIP")) {
                break;
            } else if (taskName.equals("compiler_ABORT")) {
            } else {
                taskId = findTask(taskName);
                if (taskId == null) {
                    continue;
                }
            }

            String newTaskId = getNewId();
            String newFlowId = getNewId();

            Element currentFlow = createSequenceFlow(document, currentFlowId, currentTaskId, newTaskId);
            if (i++ == 0) {
                Element conditionExpression = document.createElement(xmlnsBpmn + "conditionExpression");
                conditionExpression.setAttribute("xsi:type", xmlnsBpmn + "tFormalExpression");
                if (processEngine.equals("jbpm")) {
                    conditionExpression.setTextContent(condition);
                } else {
                    conditionExpression.setTextContent("${" + condition + "}");
                }
                currentFlow.appendChild(conditionExpression);
            }
            process.insertBefore(currentFlow, mergeGateway);
            edgeReference(document, currentFlowId, mergeGatewayId);

            Element newTask;
            if (taskName.equals("compiler_ABORT")) {
                String scirpt = "S.deleteProcessInstance(" + scriptContext + ");";
                newTask = createScriptTask(document, newTaskId, List.of(currentFlowId), List.of(newFlowId), scirpt);

                // Element endEvent = createEndEvent(document, newTaskId, currentFlowId);
                // process.insertBefore(endEvent, mergeGateway);
                // shapeReference(document, newTaskId, mergeGatewayId);
                // return;
            } else {
                newTask = cloneTask(document, taskId, currentFlowId, newFlowId);
            }
            newTask.setAttribute("id", newTaskId);
            process.insertBefore(newTask, mergeGateway);
            shapeReference(document, newTaskId, mergeGatewayId);

            currentTaskId = newTaskId;
            currentFlowId = newFlowId;
        }

        Element currentFlow = createSequenceFlow(document, currentFlowId, currentTaskId, mergeGatewayId);
        if (i++ == 0) {
            Element conditionExpression = document.createElement(xmlnsBpmn + "conditionExpression");
            conditionExpression.setAttribute("xsi:type", xmlnsBpmn + "tFormalExpression");
            conditionExpression.setTextContent(condition);
            currentFlow.appendChild(conditionExpression);
        }
        process.insertBefore(currentFlow, mergeGateway);
        edgeReference(document, currentFlowId, mergeGatewayId);

        Element incoming = document.createElement(xmlnsBpmn + "incoming");
        incoming.setTextContent(currentFlowId);
        Element ref = (Element) mergeGateway.getElementsByTagName(xmlnsBpmn + "outgoing").item(0);
        mergeGateway.insertBefore(incoming, ref);
    }

    private String findTask(String name) {
        for (Element task : taskList) {
            String taskId = task.getAttribute("id");
            String taskName = task.getAttribute("name");
            if (taskName.equals(name)) {
                return taskId;
            }
        }
        return null;
    }

    private Element cloneTask(Document document, String id, String sequenceFlowInId, String sequenceFlowOutId) {
        for (Element task : taskList) {
            String taskId = task.getAttribute("id");
            if (taskId.equals(id)) {
                Element newTask = (Element) task.cloneNode(true);

                List<Element> incomingList = nodeListToElementList(newTask.getElementsByTagName(xmlnsBpmn + "incoming"));
                int i = 0;
                for (Element incoming : incomingList) {
                    if (i++ == 0) {
                        incoming.setTextContent(sequenceFlowInId);
                    } else {
                        newTask.removeChild(incoming);
                    }
                }

                List<Element> outgoingList = nodeListToElementList(newTask.getElementsByTagName(xmlnsBpmn + "outgoing"));
                i = 0;
                for (Element outgoing : outgoingList) {
                    if (i++ == 0) {
                        outgoing.setTextContent(sequenceFlowOutId);
                    } else {
                        newTask.removeChild(outgoing);
                    }
                }

                return newTask;
            }
        }
        return null;
    }

    private Element createScriptTask(Document document, String id, List<String> sequenceFlowInIdList, List<String> sequenceFlowOutIdList, String scriptTextContent) {
        Element scriptTask = document.createElement(xmlnsBpmn + "scriptTask");
        scriptTask.setAttribute("id", id);
        scriptTask.setAttribute("scriptFormat", scriptLanguage);

        for (String sequenceFlowInId : sequenceFlowInIdList) {
            Element incoming = document.createElement(xmlnsBpmn + "incoming");
            incoming.setTextContent(sequenceFlowInId);
            scriptTask.appendChild(incoming);
        }

        for (String sequenceFlowOutId : sequenceFlowOutIdList) {
            Element outgoing = document.createElement(xmlnsBpmn + "outgoing");
            outgoing.setTextContent(sequenceFlowOutId);
            scriptTask.appendChild(outgoing);
        }

        Element script = document.createElement(xmlnsBpmn + "script");
        script.setTextContent(scriptTextContent);
        scriptTask.appendChild(script);

        return scriptTask;
    }

    private Element createSplitGateway(Document document, String id, List<String> sequenceFlowInIdList, List<String> sequenceFlowOutIdList) {
        Element splitGateway = createExclusiveGateway(document, id, sequenceFlowInIdList, sequenceFlowOutIdList);
        if (processEngine.equals("jbpm")) {
            splitGateway.setAttribute("gatewayDirection", "Diverging");
        }
        return splitGateway;
    }

    private Element createMergeGateway(Document document, String id, List<String> sequenceFlowInIdList, List<String> sequenceFlowOutIdList) {
        Element mergeGateway = createExclusiveGateway(document, id, sequenceFlowInIdList, sequenceFlowOutIdList);
        if (processEngine.equals("jbpm")) {
            mergeGateway.setAttribute("gatewayDirection", "Converging");
        }
        return mergeGateway;
    }

    private Element createExclusiveGateway(Document document, String id, List<String> sequenceFlowInIdList, List<String> sequenceFlowOutIdList) {
        Element exclusiveGateway = document.createElement(xmlnsBpmn + "exclusiveGateway");
        exclusiveGateway.setAttribute("id", id);

        for (String sequenceFlowInId : sequenceFlowInIdList) {
            Element incoming = document.createElement(xmlnsBpmn + "incoming");
            incoming.setTextContent(sequenceFlowInId);
            exclusiveGateway.appendChild(incoming);
        }

        for (String sequenceFlowOutId : sequenceFlowOutIdList) {
            Element outgoing = document.createElement(xmlnsBpmn + "outgoing");
            outgoing.setTextContent(sequenceFlowOutId);
            exclusiveGateway.appendChild(outgoing);
        }

        return exclusiveGateway;
    }

    private Element createSequenceFlow(Document document, String id, String taskSourceId, String taskTargetId) {
        Element sequenceFlow = document.createElement(xmlnsBpmn + "sequenceFlow");
        sequenceFlow.setAttribute("id", id);
        sequenceFlow.setAttribute("sourceRef", taskSourceId);
        sequenceFlow.setAttribute("targetRef", taskTargetId);

        return sequenceFlow;
    }

    private Element createStartEvent(Document document, String id, String sequenceFlowOutId) {
        Element startEvent = document.createElement(xmlnsBpmn + "startEvent");
        startEvent.setAttribute("id", id);
        startEvent.setAttribute("isInterrupting", "false");

        Element outgoing = document.createElement(xmlnsBpmn + "outgoing");
        outgoing.setTextContent(sequenceFlowOutId);
        startEvent.appendChild(outgoing);

        return startEvent;
    }

    private Element createEndEvent(Document document, String id, String sequenceFlowInId) {
        Element endEvent = document.createElement(xmlnsBpmn + "endEvent");
        endEvent.setAttribute("id", id);

        Element incoming = document.createElement(xmlnsBpmn + "incoming");
        incoming.setTextContent(sequenceFlowInId);
        endEvent.appendChild(incoming);

        return endEvent;
    }

    private Element createMessageEventDefinition(Document document, String id, String messageId) {
        Element messageEventDefinition = document.createElement(xmlnsBpmn + "messageEventDefinition");
        messageEventDefinition.setAttribute("id", id);
        messageEventDefinition.setAttribute("messageRef", messageId);

        return messageEventDefinition;
    }

    private Element createMessage(Document document, String id, String name) {
        Element message = document.createElement(xmlnsBpmn + "message");
        message.setAttribute("id", id);
        message.setAttribute("name", name);

        return message;
    }

    private void shapeReference(Document document, String nodeId, String referenceId) {
        List<Element> BPMNShapeList = nodeListToElementList(document.getElementsByTagName(xmlnsBpmndi + "BPMNShape"));
        for (Element BPMNShape : BPMNShapeList) {
            String bpmnElement = BPMNShape.getAttribute("bpmnElement");
            if (bpmnElement.equals(referenceId)) {
                Element bounds = (Element) BPMNShape.getElementsByTagName(xmlnsDc + "Bounds").item(0);
                String x = String.valueOf(Integer.parseInt(bounds.getAttribute("x")) + 10);
                String y = bounds.getAttribute("y");

                Element BPMNDiagram = (Element) BPMNShape.getParentNode();
                Element nodeBPMNShape = createBPMNShape(document, nodeId, x, y);
                BPMNDiagram.insertBefore(nodeBPMNShape, BPMNShape);
            }
        }
    }

    private void edgeReference(Document document, String nodeId, String referenceId) {
        List<Element> BPMNShapeList = nodeListToElementList(document.getElementsByTagName(xmlnsBpmndi + "BPMNShape"));
        for (Element BPMNShape : BPMNShapeList) {
            String bpmnElement = BPMNShape.getAttribute("bpmnElement");
            if (bpmnElement.equals(referenceId)) {
                Element bounds = (Element) BPMNShape.getElementsByTagName(xmlnsDc + "Bounds").item(0);
                String x = String.valueOf(Integer.parseInt(bounds.getAttribute("x")) + 10);
                String y = bounds.getAttribute("y");

                Element BPMNDiagram = (Element) BPMNShape.getParentNode();
                Element nodeBPMNEdge = createBPMNEdge(document, nodeId, x, y);
                BPMNDiagram.insertBefore(nodeBPMNEdge, BPMNShape);
            }
        }
    }

    private Element createBPMNShape(Document document, String taskId, String x, String y) {
        Element BPMNShape = document.createElement(xmlnsBpmndi + "BPMNShape");
        BPMNShape.setAttribute("id", taskId + "_di");
        BPMNShape.setAttribute("bpmnElement", taskId);

        Element bounds = document.createElement(xmlnsDc + "Bounds");
        bounds.setAttribute("x", x);
        bounds.setAttribute("y", y);
        bounds.setAttribute("width", "10");
        bounds.setAttribute("height", "10");
        BPMNShape.appendChild(bounds);

        return BPMNShape;
    }

    private Element createBPMNEdge(Document document, String sequenceFlowId, String x, String y) {
        Element BPMNEdge = document.createElement(xmlnsBpmndi + "BPMNEdge");
        BPMNEdge.setAttribute("id", sequenceFlowId + "_di");
        BPMNEdge.setAttribute("bpmnElement", sequenceFlowId);

        Element waypoint1 = document.createElement(xmlnsDi + "waypoint");
        waypoint1.setAttribute("x", x);
        waypoint1.setAttribute("y", y);
        BPMNEdge.appendChild(waypoint1);

        Element waypoint2 = document.createElement(xmlnsDi + "waypoint");
        waypoint2.setAttribute("x", x);
        waypoint2.setAttribute("y", y);
        BPMNEdge.appendChild(waypoint2);

        return BPMNEdge;
    }

    private String getNewId() {
        return newIdPrefix + newIdSuffix++;
    }

    private String convertScript(String script) {
//        CompilerScript compilerScript = new CompilerScript();
//        try {
//            return compilerScript.compileScript(script);
//        } catch (Exception e) {
//            return e.getMessage();
//        }
//        return GroceryStoreTranslator.translate(script);
        if (processEngine.equals("functional")) {
            return script;
        }
        if (script.equals(" ")) {
            return " ";
        }
        String result = "";
        for (PublicApi publicApi : publicApiList) {
            for (String outputTo : publicApi.getOutputTos()) {
                if (script.contains(outputTo)) {
                    if (processEngine.equals("jbpm")) {
                        result += callPublicApi(publicApi);
                    } else {
                        result += callPublicApiGroovy(publicApi);
                    }
                    break;
                }
            }
        }
        result += SimpleTranslator.translate(script).replaceAll("execution", scriptContext);
        return result;
    }

    private String callPublicApi(PublicApi publicApi) {
        String result = "S.callPublicApi(" + scriptContext + ", " + publicApi.getId() + ");\n";
        for (String variable : publicApi.getOutputFroms()) {
            result += variable + " = " + scriptContext + ".getVariable(\"" + variable + "\");\n";
            result += "S.info(" + scriptContext + ", \"Third-party API (" + variable + " = \" + " + variable + " + \")\");\n";
        }
        return result;
    }

    private String callPublicApiGroovy(PublicApi publicApi) {
        String result = "";
        result += "import groovy.json.*;\n" +
                "def slurper = new JsonSlurper();\n" +
                "def url = \"" + publicApi.getUrl() + "\";\n" +
                "def query = ";
        for (int i = 0; i < publicApi.getInputFroms().size(); i++) {
            if (i > 0) {
                result += " + \"&\" + ";
            }
            result += "\"" + publicApi.getInputFroms().get(i) + "=\" + " + publicApi.getInputTos().get(i);
        }
        result += ";\n";
        if (publicApi.getMethod().equals("GET")) {
            result += "def connection = (HttpURLConnection) new URL(url + \"?\" + query).openConnection();\n" +
                    "connection.requestMethod = \"GET\";\n" +
                    "connection.connect();\n";
        } else {
            result += "def connection = (HttpURLConnection) new URL(url).openConnection();\n" +
                    "connection.requestMethod = \"" + publicApi.getMethod() + "\";\n" +
                    "connection.doOutput = true;\n" +
                    "connection.doInput = true;\n" +
                    "connection.connect();\n" +
                    "def writer = new BufferedWriter(new OutputStreamWriter(connection.outputStream));\n" +
                    "writer.write(query)\n" +
                    "writer.close();\n";
        }
        result += "def result = slurper.parseText(connection.inputStream.getText());\n";
        for (int i = 0; i < publicApi.getOutputFroms().size(); i++) {
            result += "S.info(" + scriptContext + ", \"Third-party API (" + publicApi.getOutputTos().get(i) + " = \" + result." + publicApi.getOutputFroms().get(i) + " + \")\");\n";
            result += scriptContext + ".setVariable(\"" + publicApi.getOutputTos().get(i) + "\", result." + publicApi.getOutputFroms().get(i) + ");\n";
        }
        return result;
    }

    private List<String> transformScript(String script) {
        return SimpleTransformer.transform(script);
    }
}
