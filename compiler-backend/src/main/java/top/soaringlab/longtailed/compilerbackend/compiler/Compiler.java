package top.soaringlab.longtailed.compilerbackend.compiler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import top.soaringlab.longtailed.compilerbackend.domain.PublicApi;
import top.soaringlab.longtailed.compilerbackend.dsl.simple.SimpleTranslator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String processEngine = "";

    public List<PublicApi> publicApiList = new ArrayList<>();

//    private String httpScript = "String HTTP(String url, String request) {\n" +
//            "    def response = \"\";\n" +
//            "    def post = new URL(url).openConnection();\n" +
//            "    def message = request;\n" +
//            "    post.setRequestMethod(\"POST\")\n" +
//            "    post.setDoOutput(true)\n" +
//            "    post.setRequestProperty(\"Content-Type\", \"application/json\")\n" +
//            "    post.getOutputStream().write(message.getBytes(\"UTF-8\"));\n" +
//            "    def postRC = post.getResponseCode();\n" +
//            "    if(postRC.equals(200)) {\n" +
//            "       response = post.getInputStream().getText();\n" +
//            "    }\n" +
//            "    return response;\n" +
//            "}\n";

    public String compile(String file) throws Exception {
        // parse the xml
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(file)));

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
        NodeList textAnnotationNodeList = document.getElementsByTagName(xmlnsBpmn + "textAnnotation");
        Map<String, String> textAnnotationIdTextContentMap = new HashMap<>();
        for (int i = 0; i < textAnnotationNodeList.getLength(); i++) {
            Element textAnnotation = (Element) textAnnotationNodeList.item(i);
            String textAnnotationId = textAnnotation.getAttribute("id");
            String textAnnotationTextContent = textAnnotation.getTextContent();
            textAnnotationIdTextContentMap.put(textAnnotationId, textAnnotationTextContent);
        }

        // find associations
        NodeList associationNodeList = document.getElementsByTagName(xmlnsBpmn + "association");
        Map<String, String> elementIdTextAnnotationIdMap = new HashMap<>();
        for (int i = 0; i < associationNodeList.getLength(); i++) {
            Element association = (Element) associationNodeList.item(i);
            String sourceRef = association.getAttribute("sourceRef");
            String targetRef = association.getAttribute("targetRef");
            String textContent = textAnnotationIdTextContentMap.get(targetRef);
            elementIdTextAnnotationIdMap.put(sourceRef, targetRef);
            elementIdTextContentMap.put(sourceRef, textContent);
        }

        // find participants
        NodeList participantNodeList = document.getElementsByTagName(xmlnsBpmn + "participant");
        for (int i = 0; i < participantNodeList.getLength(); i++) {
            Element participant = (Element) participantNodeList.item(i);
            String participantId = participant.getAttribute("id");
            String processRef = participant.getAttribute("processRef");
            if (elementIdTextContentMap.containsKey(participantId)) {
                String textContent = elementIdTextContentMap.get(participantId);
                elementIdTextContentMap.put(processRef, textContent);
            }
        }

        for (String taskName : taskNames) {
            convertTask(document, taskName);
        }

//        convertProcess(document, processName);

        // output the new xml
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));
        return byteArrayOutputStream.toString();
    }

    private void convertTask(Document document, String name) {
        String suffixBegin = "_begin";
        String suffixEnd = "_end";

        // find tasks
        NodeList taskNodeList = document.getElementsByTagName(name);
        Map<String, String> taskIdSequenceFlowInIdMap = new HashMap<>();
//        Map<String, String> taskIdSequenceFlowOutIdMap = new HashMap<>();
        for (int i = 0; i < taskNodeList.getLength(); i++) {
            Element task = (Element) taskNodeList.item(i);
            String taskId = task.getAttribute("id");
            String taskBeginId = taskId + suffixBegin;
            String taskEndId = taskId + suffixEnd;

            String textContent = elementIdTextContentMap.get(taskId);
            if (textContent != null) {
                Element incoming = (Element) task.getElementsByTagName(xmlnsBpmn + "incoming").item(0);
                String sequenceFlowInId = incoming.getTextContent();
                taskIdSequenceFlowInIdMap.put(taskId, sequenceFlowInId);
                String sequenceFlowBeginId = sequenceFlowInId + suffixBegin;
                incoming.setTextContent(sequenceFlowBeginId);

//                Element outgoing = (Element) task.getElementsByTagName(xmlnsBpmn + "outgoing").item(0);
//                String sequenceFlowOutId = outgoing.getTextContent();
//                taskIdSequenceFlowOutIdMap.put(taskId, sequenceFlowOutId);
//                String sequenceFlowEndId = sequenceFlowOutId + suffixEnd;
//                outgoing.setTextContent(sequenceFlowEndId);

                Element process = (Element) task.getParentNode();
                String scriptBegin = defaultScript + elementIdTextContentMap.getOrDefault(taskId, "");
                scriptBegin = convertScript(scriptBegin);
                Element taskBegin = createScriptTask(document, taskBeginId, sequenceFlowInId, sequenceFlowBeginId, scriptBegin);
                process.insertBefore(taskBegin, task);
//                String scriptEnd = defaultScript;
//                Element taskEnd = createScriptTask(document, taskEndId, sequenceFlowEndId, sequenceFlowOutId, scriptEnd);
//                process.insertBefore(taskEnd, task);

                if (name.equals(xmlnsBpmn + "scriptTask")) {
//                    i = i + 2;
                    i = i + 1;
                }
            }
        }

        // find sequenceFlows
        NodeList sequenceFlowNodeList = document.getElementsByTagName(xmlnsBpmn + "sequenceFlow");
        for (int i = 0; i < sequenceFlowNodeList.getLength(); i++) {
            Element sequenceFlow = (Element) sequenceFlowNodeList.item(i);
            String sequenceFlowId = sequenceFlow.getAttribute("id");

            String taskTargetId = sequenceFlow.getAttribute("targetRef");
            if (taskIdSequenceFlowInIdMap.containsKey(taskTargetId)) {
                String sequenceFlowBeginId = sequenceFlowId + suffixBegin;
                String taskBeginId = taskTargetId + suffixBegin;
                sequenceFlow.setAttribute("targetRef", taskBeginId);

                Element sequenceFlowBegin = createSequenceFlow(document, sequenceFlowBeginId, taskBeginId, taskTargetId);
                Element process = (Element) sequenceFlow.getParentNode();
                process.insertBefore(sequenceFlowBegin, sequenceFlow);

                i = i + 1;
            }

//            String taskSourceId = sequenceFlow.getAttribute("sourceRef");
//            if (taskIdSequenceFlowOutIdMap.containsKey(taskSourceId)) {
//                String sequenceFlowEndId = sequenceFlowId + suffixEnd;
//                String taskEndId = taskSourceId + suffixEnd;
//                sequenceFlow.setAttribute("sourceRef", taskEndId);
//
//                Element sequenceFlowEnd = createSequenceFlow(document, sequenceFlowEndId, taskSourceId, taskEndId);
//                Element process = (Element) sequenceFlow.getParentNode();
//                process.insertBefore(sequenceFlowEnd, sequenceFlow);
//
//                i = i + 1;
//            }
        }

        // find BPMNShapes
        NodeList BPMNShapeNodeList = document.getElementsByTagName(xmlnsBpmndi + "BPMNShape");
        for (int i = 0; i < BPMNShapeNodeList.getLength(); i++) {
            Element BPMNShape = (Element) BPMNShapeNodeList.item(i);
            String taskId = BPMNShape.getAttribute("bpmnElement");

            if (taskIdSequenceFlowInIdMap.containsKey(taskId)) {
                Element bounds = (Element) BPMNShape.getElementsByTagName(xmlnsDc + "Bounds").item(0);
                String x = bounds.getAttribute("x");
                String y = bounds.getAttribute("y");

                Element BPMNDiagram = (Element) BPMNShape.getParentNode();

                String taskBeginId = taskId + suffixBegin;
                Element BPMNShapeBegin = createBPMNShape(document, taskBeginId, String.valueOf(Integer.parseInt(x) + 10), y);
                BPMNDiagram.insertBefore(BPMNShapeBegin, BPMNShape);

//                String taskEndId = taskId + suffixEnd;
//                Element BPMNShapeEnd = createBPMNShape(document, taskEndId, String.valueOf(Integer.parseInt(x) + 10), y);
//                BPMNDiagram.insertBefore(BPMNShapeEnd, BPMNShape);

//                i = i + 2;
                i = i + 1;
            }
        }

        // find BPMNEdges
        NodeList BPMNEdgeNodeList = document.getElementsByTagName(xmlnsBpmndi + "BPMNEdge");
        for (int i = 0; i < BPMNEdgeNodeList.getLength(); i++) {
            Element BPMNEdge = (Element) BPMNEdgeNodeList.item(i);
            String sequenceFlowId = BPMNEdge.getAttribute("bpmnElement");

            if (taskIdSequenceFlowInIdMap.containsValue(sequenceFlowId)) {
                String sequenceFlowBegin = sequenceFlowId + suffixBegin;

                Element waypoint = (Element) BPMNEdge.getElementsByTagName(xmlnsDi + "waypoint").item(1);
                String x = waypoint.getAttribute("x");
                String y = waypoint.getAttribute("y");

                Element diagram = (Element) BPMNEdge.getParentNode();
                Element BPMNEdgeBegin = createBPMNEdge(document, sequenceFlowBegin, x, y);
                diagram.insertBefore(BPMNEdgeBegin, BPMNEdge);

                i = i + 1;
            }

//            if (taskIdSequenceFlowOutIdMap.containsValue(sequenceFlowId)) {
//                String sequenceFlowEnd = sequenceFlowId + suffixEnd;
//
//                Element waypoint = (Element) BPMNEdge.getElementsByTagName(xmlnsDi + "waypoint").item(0);
//                String x = waypoint.getAttribute("x");
//                String y = waypoint.getAttribute("y");
//
//                Element diagram = (Element) BPMNEdge.getParentNode();
//                Element BPMNEdgeEnd = createBPMNEdge(document, sequenceFlowEnd, x, y);
//                diagram.insertBefore(BPMNEdgeEnd, BPMNEdge);
//
//                i = i + 1;
//            }
        }
    }

    private void convertProcess(Document document, String name) {
        // find processes
        NodeList processNodeList = document.getElementsByTagName(name);
        for (int i = 0; i < processNodeList.getLength(); i++) {
            Element process = (Element) processNodeList.item(i);
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
            subProcess.appendChild(createScriptTask(document, scriptTaskId, sequenceFlow1Id, sequenceFlow2Id, script));
            subProcess.appendChild(createSequenceFlow(document, sequenceFlow2Id, scriptTaskId, endEventId));
            subProcess.appendChild(createEndEvent(document, endEventId, sequenceFlow2Id));

            process.insertBefore(subProcess, process.getElementsByTagName(xmlnsBpmn + "startEvent").item(0));
            Element message = createMessage(document, messageId, messageId);
            Element definitions = (Element) process.getParentNode();
            definitions.insertBefore(message, definitions.getElementsByTagName(xmlnsBpmn + "process").item(0));
        }
    }

    private Element createScriptTask(Document document, String id, String sequenceFlowInId, String sequenceFlowOutId, String scriptTextContent) {
        Element scriptTask = document.createElement(xmlnsBpmn + "scriptTask");
        scriptTask.setAttribute("id", id);
        scriptTask.setAttribute("scriptFormat", scriptLanguage);

        Element incoming = document.createElement(xmlnsBpmn + "incoming");
        incoming.setTextContent(sequenceFlowInId);
        scriptTask.appendChild(incoming);

        Element outgoing = document.createElement(xmlnsBpmn + "outgoing");
        outgoing.setTextContent(sequenceFlowOutId);
        scriptTask.appendChild(outgoing);

        Element script = document.createElement(xmlnsBpmn + "script");
        script.setTextContent(scriptTextContent);
        scriptTask.appendChild(script);

        return scriptTask;
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

    private Element createBPMNShape(Document document, String taskId, String x, String y) {
        Element BPMNShape = document.createElement(xmlnsBpmndi + "BPMNShape");
        BPMNShape.setAttribute("id", taskId + "_di");
        BPMNShape.setAttribute("bpmnElement", taskId);

        Element bounds = document.createElement(xmlnsDc + "Bounds");
        bounds.setAttribute("x", x);
        bounds.setAttribute("y", y);
        bounds.setAttribute("width", "1");
        bounds.setAttribute("height", "1");
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
            result += scriptContext + ".setVariable(\"" + publicApi.getOutputTos().get(i) + "\", result." + publicApi.getOutputFroms().get(i) + ");\n";
        }
        return result;
    }
}
