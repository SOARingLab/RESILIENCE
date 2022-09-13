package top.soaringlab.longtailed.compilerbackend.verifier;

import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessVariable;
import top.soaringlab.longtailed.compilerbackend.dsl.nusmvresult.NusmvResultReader;
import top.soaringlab.longtailed.compilerbackend.dsl.simple.SimpleVerifier;
import top.soaringlab.longtailed.compilerbackend.dto.FunctionalVerificationResult;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class NusmvVerifier {

    static class BpmnNode {
        String id = "";
        String name = "";
        String type = ""; // startEvent, intermediateEvent, endEvent, task, exclusiveGateway, parallelGateway
        List<String> sequenceInList = new ArrayList<>();
        List<String> sequenceOutList = new ArrayList<>();
        List<String> messageInList = new ArrayList<>();
        List<String> messageOutList = new ArrayList<>();
        String annotation = "";
    }

    static class BpmnFlow {
        String id = "";
        String name = "";
        String type = ""; // sequenceFlow, messageFlow
        String source = "";
        String target = "";
        String condition = "";
        String declarative = "";
    }

    static class LtsState {
        String id = ""; // concatenation of completions
        Map<String, Integer> completionMap = new HashMap<>();
        List<String> transitionList = new ArrayList<>();
    }

    static class LtsTransition {
        String id = ""; // concatenation of source and target
        String source = "";
        String target = "";
        String bpmnNodeId = "";
        String annotation = "";
        String condition = "";
    }

    static class ConditionValue {
        String condition = "";
        String value = "";
    }

    private String startBpmnNodeId = "";

    private String startBpmnNodeName = "";

    private Map<String, BpmnNode> bpmnNodeMap = new HashMap<>();

    private Map<String, BpmnFlow> bpmnFlowMap = new HashMap<>();

    private List<BpmnFlow> bpmnConstraintList = new ArrayList<>();

    private String startLtsStateId = "";

    private Map<String, LtsState> ltsStateMap = new HashMap<>();

    private Map<String, LtsTransition> ltsTransitionMap = new HashMap<>();

    private Map<String, List<String>> bpmnNodeLtsStateListMap = new HashMap<>();

    private Map<String, Map<String, List<ConditionValue>>> processVariableLtsTransitionConditionValueListMap = new HashMap<>();

    private String verificationOutput;

    public List<ProcessVariable> processVariableList = new ArrayList<>();

    public FunctionalVerificationResult functionalVerify(String fileAnnotation, String fileFunctional, String start) throws Exception {
        FunctionalVerificationResult functionalVerificationResult = new FunctionalVerificationResult();
        startBpmnNodeName = start;
        readBpmnXml(fileAnnotation);
        readConstraint(fileFunctional);
        reduceBpmnComplexity();
        if (!bpmnToLts()) {
            return functionalVerificationResult;
        }
        String inputFile = ltsToInputFile();
        if (!modelCheck(inputFile)) {
            functionalVerificationResult.setDetail(explainVerificationResult());
            return functionalVerificationResult;
        }
        functionalVerificationResult.setResult(true);
        return functionalVerificationResult;
    }

    private void readBpmnXml(String file) throws Exception {
        // parse xml
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(file)));

        // name
        String tagName = document.getDocumentElement().getTagName();
        String xmlnsBpmn;
        if (tagName.equals("bpmn:definitions")) {
            xmlnsBpmn = "bpmn:";
        } else if (tagName.equals("bpmn2:definitions")) {
            xmlnsBpmn = "bpmn2:";
        } else {
            xmlnsBpmn = tagName.split(":")[0] + ":";
        }
        String startEventName = xmlnsBpmn + "startEvent";
        String endEventName = xmlnsBpmn + "endEvent";
        List<String> intermediateEventNames = List.of(
                xmlnsBpmn + "intermediateCatchEvent",
                xmlnsBpmn + "intermediateThrowEvent"
        );
        List<String> taskNames = List.of(
                xmlnsBpmn + "task",
                xmlnsBpmn + "scriptTask",
                xmlnsBpmn + "sendTask",
                xmlnsBpmn + "receiveTask",
                xmlnsBpmn + "userTask",
                xmlnsBpmn + "manualTask",
                xmlnsBpmn + "businessRuleTask",
                xmlnsBpmn + "serviceTask"
        );
        String exclusiveGatewayName = xmlnsBpmn + "exclusiveGateway";
        String parallelGatewayName = xmlnsBpmn + "parallelGateway";
        String sequenceFlowName = xmlnsBpmn + "sequenceFlow";
        String messageFlowName = xmlnsBpmn + "messageFlow";
        List<String> nodeNames = new ArrayList<>();
        nodeNames.add(startEventName);
        nodeNames.add(endEventName);
        nodeNames.addAll(intermediateEventNames);
        nodeNames.addAll(taskNames);
        nodeNames.add(exclusiveGatewayName);
        nodeNames.add(parallelGatewayName);
        List<String> flowNames = new ArrayList<>();
        flowNames.add(sequenceFlowName);
        flowNames.add(messageFlowName);
        String scriptName = xmlnsBpmn + "script";
        String conditionExpressionName = xmlnsBpmn + "conditionExpression";
        String declarativeName = "constraint:declarative";

        // node
        for (String nodeName : nodeNames) {
            NodeList nodeList = document.getElementsByTagName(nodeName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                BpmnNode bpmnNode = new BpmnNode();
                bpmnNode.id = element.getAttribute("id");
                bpmnNode.name = element.getAttribute("name");
                if (nodeName.equals(startEventName)) {
                    bpmnNode.type = "startEvent";
                } else if (nodeName.equals(endEventName)) {
                    bpmnNode.type = "endEvent";
                } else if (intermediateEventNames.contains(nodeName)) {
                    bpmnNode.type = "intermediateEvent";
                } else if (taskNames.contains(nodeName)) {
                    bpmnNode.type = "task";
                } else if (nodeName.equals(exclusiveGatewayName)) {
                    bpmnNode.type = "exclusiveGateway";
                } else if (nodeName.equals(parallelGatewayName)) {
                    bpmnNode.type = "parallelGateway";
                }
                if (element.getAttribute("scriptFormat").equals("dsl")) {
                    NodeList scriptNodeList = element.getElementsByTagName(scriptName);
                    if (scriptNodeList.getLength() > 0) {
                        Element scriptElement = (Element) scriptNodeList.item(0);
                        bpmnNode.annotation = scriptElement.getTextContent();
                    }
                }
                bpmnNodeMap.put(bpmnNode.id, bpmnNode);
                if (bpmnNode.name.equals(startBpmnNodeName)) {
                    startBpmnNodeId = bpmnNode.id;
                }
            }
        }

        // flow
        for (String flowName : flowNames) {
            NodeList nodeList = document.getElementsByTagName(flowName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                BpmnFlow bpmnFlow = new BpmnFlow();
                bpmnFlow.id = element.getAttribute("id");
                bpmnFlow.name = element.getAttribute("name");
                bpmnFlow.source = element.getAttribute("sourceRef");
                bpmnFlow.target = element.getAttribute("targetRef");
                bpmnFlow.declarative = element.getAttribute(declarativeName);
                if (!bpmnFlow.declarative.isEmpty()) {
                    bpmnConstraintList.add(bpmnFlow);
                } else {
                    if (flowName.equals(sequenceFlowName)) {
                        bpmnFlow.type = "sequenceFlow";
                        bpmnNodeMap.get(bpmnFlow.source).sequenceOutList.add(bpmnFlow.id);
                        bpmnNodeMap.get(bpmnFlow.target).sequenceInList.add(bpmnFlow.id);
                    } else if (flowName.equals(messageFlowName)) {
                        bpmnFlow.type = "messageFlow";
                        bpmnNodeMap.get(bpmnFlow.source).messageOutList.add(bpmnFlow.id);
                        bpmnNodeMap.get(bpmnFlow.target).messageInList.add(bpmnFlow.id);
                    }
                    NodeList conditionExpressionNodeList = element.getElementsByTagName(conditionExpressionName);
                    if (conditionExpressionNodeList.getLength() > 0) {
                        Element conditionExpressionElement = (Element) conditionExpressionNodeList.item(0);
                        bpmnFlow.condition = conditionExpressionElement.getTextContent();
                    }
                    bpmnFlowMap.put(bpmnFlow.id, bpmnFlow);
                }
            }
        }
    }

    private void readConstraint(String file) throws Exception {
        // parse xml
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(file)));

        // name
        String tagName = document.getDocumentElement().getTagName();
        String xmlnsBpmn;
        if (tagName.equals("bpmn:definitions")) {
            xmlnsBpmn = "bpmn:";
        } else if (tagName.equals("bpmn2:definitions")) {
            xmlnsBpmn = "bpmn2:";
        } else {
            xmlnsBpmn = tagName.split(":")[0] + ":";
        }
        String sequenceFlowName = xmlnsBpmn + "sequenceFlow";
        String messageFlowName = xmlnsBpmn + "messageFlow";
        List<String> flowNames = new ArrayList<>();
        flowNames.add(sequenceFlowName);
        flowNames.add(messageFlowName);
        String declarativeName = "constraint:declarative";

        // flow
        for (String flowName : flowNames) {
            NodeList nodeList = document.getElementsByTagName(flowName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                BpmnFlow bpmnFlow = new BpmnFlow();
                bpmnFlow.id = element.getAttribute("id");
                bpmnFlow.name = element.getAttribute("name");
                bpmnFlow.source = element.getAttribute("sourceRef");
                bpmnFlow.target = element.getAttribute("targetRef");
                bpmnFlow.declarative = element.getAttribute(declarativeName);
                if (!bpmnFlow.declarative.isEmpty()) {
                    bpmnConstraintList.add(bpmnFlow);
                }
            }
        }
    }

    private void reduceBpmnComplexity() {
        List<String> bpmnNodeIdList = new ArrayList<>(bpmnNodeMap.keySet());
        for (String bpmnNodeId : bpmnNodeIdList) {
            BpmnNode bpmnNode = bpmnNodeMap.get(bpmnNodeId);
            if (bpmnNode.type.equals("intermediateEvent") || bpmnNode.type.equals("task")) {
                if (bpmnNode.annotation.isEmpty() && !bpmnNodeInConstraint(bpmnNodeId)) {
                    removeBpmnNode(bpmnNodeId);
                }
            }
        }
    }

    private boolean bpmnNodeInConstraint(String bpmnNodeId) {
        for (BpmnFlow bpmnConstraint : bpmnConstraintList) {
            if (bpmnConstraint.source.equals(bpmnNodeId) || bpmnConstraint.target.equals(bpmnNodeId)) {
                return true;
            }
        }
        return false;
    }

    private void removeBpmnNode(String bpmnNodeId) {
        BpmnNode bpmnNode = bpmnNodeMap.get(bpmnNodeId);
        {
            String sequenceIn = bpmnNode.sequenceInList.get(0);
            BpmnFlow sequenceInFlow = bpmnFlowMap.get(sequenceIn);
            String sequenceOut = bpmnNode.sequenceOutList.get(0);
            BpmnFlow sequenceOutFlow = bpmnFlowMap.get(sequenceOut);
            BpmnNode targetBpmnNode = bpmnNodeMap.get(sequenceOutFlow.target);

            sequenceInFlow.target = sequenceOutFlow.target;
            targetBpmnNode.sequenceInList.removeIf(s -> s.equals(sequenceOut));
            targetBpmnNode.sequenceInList.add(sequenceIn);
            bpmnFlowMap.remove(sequenceOut);
        }
//        for (String sequenceIn : bpmnNode.sequenceInList) {
//            BpmnFlow bpmnFlow = bpmnFlowMap.get(sequenceIn);
//            BpmnNode sourceBpmnNode = bpmnNodeMap.get(bpmnFlow.source);
//            sourceBpmnNode.sequenceOutList.removeIf(sequenceOut -> sequenceOut.equals(sequenceIn));
//            bpmnFlowMap.remove(sequenceIn);
//        }
//        for (String sequenceOut : bpmnNode.sequenceOutList) {
//            BpmnFlow bpmnFlow = bpmnFlowMap.get(sequenceOut);
//            BpmnNode targetBpmnNode = bpmnNodeMap.get(bpmnFlow.target);
//            targetBpmnNode.sequenceInList.removeIf(sequenceIn -> sequenceIn.equals(sequenceOut));
//            bpmnFlowMap.remove(sequenceOut);
//        }
        for (String messageIn : bpmnNode.messageInList) {
            BpmnFlow bpmnFlow = bpmnFlowMap.get(messageIn);
            BpmnNode sourceBpmnNode = bpmnNodeMap.get(bpmnFlow.source);
            sourceBpmnNode.messageOutList.removeIf(messageOut -> messageOut.equals(messageIn));
            bpmnFlowMap.remove(messageIn);
        }
        for (String messageOut : bpmnNode.messageOutList) {
            BpmnFlow bpmnFlow = bpmnFlowMap.get(messageOut);
            BpmnNode targetBpmnNode = bpmnNodeMap.get(bpmnFlow.target);
            targetBpmnNode.messageInList.removeIf(messageIn -> messageIn.equals(messageOut));
            bpmnFlowMap.remove(messageOut);
        }
        bpmnNodeMap.remove(bpmnNodeId);
    }

    private boolean bpmnToLts() {
        // initialization
        BpmnNode startBpmnNode = bpmnNodeMap.get(startBpmnNodeId);
        startLtsStateId = startBpmnNode.sequenceOutList.get(0); // assume start has one sequence out
        LtsState startLtsState = new LtsState();
        startLtsState.id = startLtsStateId;
        startLtsState.completionMap.put(startLtsStateId, 1);
        ltsStateMap.put(startLtsStateId, startLtsState);
        Queue<String> ltsStateQueue = new LinkedList<>();
        ltsStateQueue.add(startLtsStateId);

        // generate states and transitions
        int number = 0;
        while (!ltsStateQueue.isEmpty()) {
            if (number++ > 10000) {
                return false;
            }
            String currentLtsStateId = ltsStateQueue.remove();
            LtsState currentLtsState = ltsStateMap.get(currentLtsStateId);
            for (String currentCompletion : currentLtsState.completionMap.keySet()) {
                BpmnFlow currentBpmnFlow = bpmnFlowMap.get(currentCompletion);
                BpmnNode nextBpmnNode = bpmnNodeMap.get(currentBpmnFlow.target);
                if (nextBpmnNode.type.equals("endEvent")) {
                    LtsState nextLtsState = newLtsState(currentLtsState, List.of(currentCompletion), List.of());
                    if (nextLtsState.completionMap.isEmpty()) {
                        LtsTransition ltsTransition = newLtsTransition(currentLtsState.id, currentLtsState.id, nextBpmnNode.id);
                        if (!ltsTransitionMap.containsKey(ltsTransition.id)) {
                            ltsTransitionMap.put(ltsTransition.id, ltsTransition);
                            currentLtsState.transitionList.add(ltsTransition.id);
                        }
                    } else {
                        if (!ltsStateMap.containsKey(nextLtsState.id)) {
                            ltsStateMap.put(nextLtsState.id, nextLtsState);
                            ltsStateQueue.add(nextLtsState.id);
                        }
                        LtsTransition ltsTransition = newLtsTransition(currentLtsState.id, nextLtsState.id, nextBpmnNode.id);
                        if (!ltsTransitionMap.containsKey(ltsTransition.id)) {
                            ltsTransitionMap.put(ltsTransition.id, ltsTransition);
                            currentLtsState.transitionList.add(ltsTransition.id);
                        }
                    }
                } else if (nextBpmnNode.type.equals("intermediateEvent") || nextBpmnNode.type.equals("task")) {
                    LtsState nextLtsState = newLtsState(currentLtsState, List.of(currentCompletion), nextBpmnNode.sequenceOutList);
                    if (!ltsStateMap.containsKey(nextLtsState.id)) {
                        ltsStateMap.put(nextLtsState.id, nextLtsState);
                        ltsStateQueue.add(nextLtsState.id);
                    }
                    LtsTransition ltsTransition = newLtsTransition(currentLtsState.id, nextLtsState.id, nextBpmnNode.id);
                    ltsTransition.annotation = nextBpmnNode.annotation;
                    if (!ltsTransitionMap.containsKey(ltsTransition.id)) {
                        ltsTransitionMap.put(ltsTransition.id, ltsTransition);
                        currentLtsState.transitionList.add(ltsTransition.id);
                    }
                } else if (nextBpmnNode.type.equals("exclusiveGateway")) {
                    for (String sequenceOut : nextBpmnNode.sequenceOutList) {
                        BpmnFlow nextBpmnFlow = bpmnFlowMap.get(sequenceOut);
                        LtsState nextLtsState = newLtsState(currentLtsState, List.of(currentCompletion), List.of(sequenceOut));
                        if (!ltsStateMap.containsKey(nextLtsState.id)) {
                            ltsStateMap.put(nextLtsState.id, nextLtsState);
                            ltsStateQueue.add(nextLtsState.id);
                        }
                        LtsTransition ltsTransition = newLtsTransition(currentLtsState.id, nextLtsState.id, nextBpmnNode.id);
                        ltsTransition.condition = nextBpmnFlow.condition;
                        if (!ltsTransitionMap.containsKey(ltsTransition.id)) {
                            ltsTransitionMap.put(ltsTransition.id, ltsTransition);
                            currentLtsState.transitionList.add(ltsTransition.id);
                        }
                    }
                } else if (nextBpmnNode.type.equals("parallelGateway")) {
                    if (currentLtsState.completionMap.keySet().containsAll(nextBpmnNode.sequenceInList)) {
                        LtsState nextLtsState = newLtsState(currentLtsState, nextBpmnNode.sequenceInList, nextBpmnNode.sequenceOutList);
                        if (!ltsStateMap.containsKey(nextLtsState.id)) {
                            ltsStateMap.put(nextLtsState.id, nextLtsState);
                            ltsStateQueue.add(nextLtsState.id);
                        }
                        LtsTransition ltsTransition = newLtsTransition(currentLtsState.id, nextLtsState.id, nextBpmnNode.id);
                        if (!ltsTransitionMap.containsKey(ltsTransition.id)) {
                            ltsTransitionMap.put(ltsTransition.id, ltsTransition);
                            currentLtsState.transitionList.add(ltsTransition.id);
                        }
                    } else {
                        ltsStateQueue.add(currentLtsState.id);
                    }
                }
            }
        }

        // map bpmn node to lts state
        for (Map.Entry<String, LtsState> ltsStateEntry : ltsStateMap.entrySet()) {
            LtsState ltsState = ltsStateEntry.getValue();
            for (String completion : ltsState.completionMap.keySet()) {
                BpmnFlow bpmnFlow = bpmnFlowMap.get(completion);
                if (!bpmnNodeLtsStateListMap.containsKey(bpmnFlow.source)) {
                    bpmnNodeLtsStateListMap.put(bpmnFlow.source, new ArrayList<>());
                }
                bpmnNodeLtsStateListMap.get(bpmnFlow.source).add(ltsState.id);
            }
        }
        return true;
    }

    private LtsState newLtsState(LtsState currentLtsState, List<String> removeCompletionList, List<String> addCompletionList) {
        Map<String, Integer> completionMap = new HashMap<>(currentLtsState.completionMap);
        for (String removeCompletion : removeCompletionList) {
            if (completionMap.get(removeCompletion) <= 1) {
                completionMap.remove(removeCompletion);
            } else {
                completionMap.put(removeCompletion, completionMap.get(removeCompletion) - 1);
            }
        }
        for (String addCompletion : addCompletionList) {
            if (!completionMap.containsKey(addCompletion)) {
                completionMap.put(addCompletion, 1);
            } else {
                completionMap.put(addCompletion, completionMap.get(addCompletion) + 1);
            }
        }

        List<String> completionList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : completionMap.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                completionList.add(entry.getKey());
            }
        }
        Collections.sort(completionList);
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (String completion : completionList) {
            if (i++ > 0) {
                stringBuilder.append("__");
            }
            stringBuilder.append(completion);
        }
        LtsState ltsState = new LtsState();
        ltsState.id = stringBuilder.toString();
        ltsState.completionMap = completionMap;
        return ltsState;
    }

    private LtsTransition newLtsTransition(String source, String target, String bpmnNodeId) {
        LtsTransition ltsTransition = new LtsTransition();
        ltsTransition.id = source + "__" + target;
        ltsTransition.source = source;
        ltsTransition.target = target;
        ltsTransition.bpmnNodeId = bpmnNodeId;
        return ltsTransition;
    }

    private String ltsToInputFile() {
        // preprocess
        for (Map.Entry<String, LtsTransition> ltsTransitionEntry : ltsTransitionMap.entrySet()) {
            LtsTransition ltsTransition = ltsTransitionEntry.getValue();
            List<String> result = translateAnnotation(ltsTransition.annotation);
            String condition = "";
            String variable = "";
            String value = "";
            int state = 0; // 2 condition, 3 action variable, 4 action value
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
                    variable = s;
                    state = 4;
                } else if (state == 4) {
                    value = s;
                    ConditionValue conditionValue = new ConditionValue();
                    conditionValue.condition = condition;
                    conditionValue.value = value;
                    if (!processVariableLtsTransitionConditionValueListMap.containsKey(variable)) {
                        processVariableLtsTransitionConditionValueListMap.put(variable, new HashMap<>());
                    }
                    Map<String, List<ConditionValue>> ltsTransitionConditionValueListMap = processVariableLtsTransitionConditionValueListMap.get(variable);
                    if (!ltsTransitionConditionValueListMap.containsKey(ltsTransition.id)) {
                        ltsTransitionConditionValueListMap.put(ltsTransition.id, new ArrayList<>());
                    }
                    ltsTransitionConditionValueListMap.get(ltsTransition.id).add(conditionValue);
                }
            }
        }

        // module
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MODULE main\n");

        // var state
        stringBuilder.append("  VAR\n");
        List<String> ltsStateIdList = new ArrayList<>(ltsStateMap.keySet());
        ProcessVariable state = new ProcessVariable();
        state.setName("state");
        state.setValueRange(ltsStateIdList);
        stringBuilder.append(variableDefinition(state));

        // var process variable
        for (ProcessVariable processVariable : processVariableList) {
            stringBuilder.append(variableDefinition(processVariable));
        }

        // assign state
        stringBuilder.append("  ASSIGN\n");
        stringBuilder.append("    init(state) := ").append(startLtsStateId).append(";\n");
        stringBuilder.append("    next(state) :=\n");
        stringBuilder.append("      case\n");
        for (Map.Entry<String, LtsState> ltsStateEntry : ltsStateMap.entrySet()) {
            LtsState ltsState = ltsStateEntry.getValue();
            stringBuilder.append(stateTransition(ltsState));
        }
        stringBuilder.append("      esac;\n");

        // assign process variable
        for (ProcessVariable processVariable : processVariableList) {
            stringBuilder.append(processVariableAssignment(processVariable));
        }

        // ltl spec
        for (BpmnFlow bpmnConstraint : bpmnConstraintList) {
            stringBuilder.append("  LTLSPEC ");
            if (bpmnConstraint.declarative.equals("responded existence")) {
                stringBuilder.append("F (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.source));
                stringBuilder.append(") -> F (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.target));
                stringBuilder.append(")\n");
            } else if (bpmnConstraint.declarative.equals("co-existence")) {
                stringBuilder.append("(F (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.source));
                stringBuilder.append(") -> F (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.target));
                stringBuilder.append(")) & (F (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.target));
                stringBuilder.append(") -> F (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.source));
                stringBuilder.append("))\n");
            } else if (bpmnConstraint.declarative.equals("response")) {
                stringBuilder.append("G ((");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.source));
                stringBuilder.append(") -> F (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.target));
                stringBuilder.append("))\n");
            } else if (bpmnConstraint.declarative.equals("precedence")) {
                stringBuilder.append("F (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.target));
                stringBuilder.append(") -> (!(");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.target));
                stringBuilder.append(") U (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.source));
                stringBuilder.append("))\n");
            } else if (bpmnConstraint.declarative.equals("succession")) {
                stringBuilder.append("(G ((");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.source));
                stringBuilder.append(") -> F (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.target));
                stringBuilder.append("))) & (F (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.target));
                stringBuilder.append(") -> (!(");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.target));
                stringBuilder.append(") U (");
                stringBuilder.append(bpmnNodeToLtsStateList(bpmnConstraint.source));
                stringBuilder.append(")))\n");
            }
        }

        return stringBuilder.toString();
    }

    private String variableDefinition(ProcessVariable processVariable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("    ").append(processVariable.getName()).append(" :\n");
        if (processVariable.getType().equals("Boolean")) {
            stringBuilder.append("      ").append("boolean").append(";\n");
        } else if (processVariable.getType().equals("Number")) {
            stringBuilder.append("      ").append(processVariable.getMinimumValue()).append("..").append(processVariable.getMaximumValue()).append(";\n");
        } else {
            stringBuilder.append("      {\n");
            List<String> valueRange = processVariable.getValueRange();
            for (int i = 0; i < valueRange.size(); i++) {
                String value = removeQuotation(valueRange.get(i));
                stringBuilder.append("        ").append(value);
                if (i < valueRange.size() - 1) {
                    stringBuilder.append(",\n");
                } else {
                    stringBuilder.append("\n");
                }
            }
            stringBuilder.append("      };\n");
        }
        return stringBuilder.toString();
    }

    private String stateTransition(LtsState ltsState) {
        Map<String, List<String>> bpmnNodeLtsTransitionListMap = new HashMap<>();
        for (String transition : ltsState.transitionList) {
            LtsTransition ltsTransition = ltsTransitionMap.get(transition);
            if (!bpmnNodeLtsTransitionListMap.containsKey(ltsTransition.bpmnNodeId)) {
                bpmnNodeLtsTransitionListMap.put(ltsTransition.bpmnNodeId, new ArrayList<>());
            }
            bpmnNodeLtsTransitionListMap.get(ltsTransition.bpmnNodeId).add(ltsTransition.id);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("        state = ").append(ltsState.id).append(" :\n");
        stringBuilder.append("          {\n");

        List<String> bpmnNodeList = new ArrayList<>(bpmnNodeLtsTransitionListMap.keySet());
        for (int i = 0; i < bpmnNodeList.size(); i++) {
            String bpmnNodeId = bpmnNodeList.get(i);
            List<String> ltsTransitionList = bpmnNodeLtsTransitionListMap.get(bpmnNodeId);
            if (ltsTransitionList.size() == 1) {
                LtsTransition ltsTransition = ltsTransitionMap.get(ltsTransitionList.get(0));
                stringBuilder.append("            ").append(ltsTransition.target);
            } else {
                stringBuilder.append("            case\n");
                String defaultTarget = "";
                for (String ltsTransitionId : ltsTransitionList) {
                    LtsTransition ltsTransition = ltsTransitionMap.get(ltsTransitionId);
                    String condition = translateCondition(ltsTransition.condition);
                    stringBuilder.append("              ").append(condition).append(" : ").append(ltsTransition.target).append(";\n");
                    defaultTarget = ltsTransition.target;
                }
                stringBuilder.append("              ").append("TRUE").append(" : ").append(defaultTarget).append(";\n");
                stringBuilder.append("            esac");
            }
            if (i < bpmnNodeList.size() - 1) {
                stringBuilder.append(",\n");
            } else {
                stringBuilder.append("\n");
            }
        }

        stringBuilder.append("          };\n");
        return stringBuilder.toString();
    }

    private String processVariableAssignment(ProcessVariable processVariable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("    init(").append(processVariable.getName()).append(") :=\n");
        if (!processVariable.getDefaultValue().equals("random")) {
            stringBuilder.append("      ").append(removeQuotation(processVariable.getDefaultValue())).append(";\n");
        } else {
            List<String> valueRange = processVariable.getValueRange();
            stringBuilder.append("      {\n");
            for (int i = 0; i < valueRange.size(); i++) {
                String value = removeQuotation(valueRange.get(i));
                stringBuilder.append("        ").append(value);
                if (i < valueRange.size() - 1) {
                    stringBuilder.append(",\n");
                } else {
                    stringBuilder.append("\n");
                }
            }
            stringBuilder.append("      };\n");
        }

        stringBuilder.append("    next(").append(processVariable.getName()).append(") :=\n");
        if (processVariableLtsTransitionConditionValueListMap.containsKey(processVariable.getName())) {
            stringBuilder.append("      case\n");
            Map<String, List<ConditionValue>> ltsTransitionConditionValueListMap = processVariableLtsTransitionConditionValueListMap.get(processVariable.getName());
            for (Map.Entry<String, List<ConditionValue>> ltsTransitionConditionValueListEntry : ltsTransitionConditionValueListMap.entrySet()) {
                String ltsTransitionId = ltsTransitionConditionValueListEntry.getKey();
                List<ConditionValue> conditionValueList = ltsTransitionConditionValueListEntry.getValue();
                LtsTransition ltsTransition = ltsTransitionMap.get(ltsTransitionId);
                stringBuilder.append("        state = ").append(ltsTransition.source).append(" & next(state) = ").append(ltsTransition.target).append(" :\n");
                stringBuilder.append("          case\n");
                for (ConditionValue conditionValue : conditionValueList) {
                    stringBuilder.append("            ").append(conditionValue.condition).append(" : ").append(conditionValue.value).append(";\n");
                }
                stringBuilder.append("            TRUE : ").append(processVariable.getName()).append(";\n");
                stringBuilder.append("          esac;\n");
            }
            stringBuilder.append("        TRUE : ").append(processVariable.getName()).append(";\n");
            stringBuilder.append("      esac;\n");
        } else {
            stringBuilder.append("      ").append(processVariable.getName()).append(";\n");
        }
        return stringBuilder.toString();
    }

    private String bpmnNodeToLtsStateList(String bpmnNodeId) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> ltsStateList = bpmnNodeLtsStateListMap.get(bpmnNodeId);
        int i = 0;
        for (String ltsStateId : ltsStateList) {
            if (i++ > 0) {
                stringBuilder.append(" | ");
            }
            stringBuilder.append("state = ").append(ltsStateId);
        }
        return stringBuilder.toString();
    }

    private String removeQuotation(String s) {
        return s.replace("\"", "_");
    }

    private String translateCondition(String condition) {
        String inputs = "WHEN " + condition + " SET a = a";
        List<String> result = SimpleVerifier.verify(inputs);
        return result.get(2);
    }

    private List<String> translateAnnotation(String annotation) {
        return SimpleVerifier.verify(annotation);
    }

    private boolean modelCheck(String inputFile) throws Exception {
        String directoryName = "nusmv";
        File directoryFile = new File(directoryName);
        if (!directoryFile.exists()) {
            directoryFile.mkdirs();
        }

        String toolName = "NuSMV.exe";
        File toolFile = new File(directoryFile, toolName);
        if (!toolFile.exists()) {
            InputStream inputStream = new ClassPathResource(toolName).getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(toolFile);
            int len;
            byte[] b = new byte[1024];
            while ((len = inputStream.read(b)) != -1) {
                fileOutputStream.write(b, 0, len);
            }
            inputStream.close();
            fileOutputStream.close();
        }

        String inputFileName = UUID.randomUUID() + ".smv";
        File inputFileFile = new File(directoryFile, inputFileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(inputFileFile));
        bufferedWriter.write(inputFile);
        bufferedWriter.close();

        String[] runCommand = new String[]{"NuSMV", inputFileName};
        Process process = Runtime.getRuntime().exec(runCommand, null, directoryFile);
        if (!process.waitFor(10, TimeUnit.SECONDS)) {
            process.destroy();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        int numOfTrue = 0;
        int numOfFalse = 0;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
            if (line.contains("is true")) {
                numOfTrue++;
            } else if (line.contains("is false")) {
                numOfFalse++;
            }
        }
        bufferedReader.close();
        verificationOutput = stringBuilder.toString();
        return numOfTrue == bpmnConstraintList.size();
    }

    private List<Map<String, String>> explainVerificationResult() {
        List<String> verificationOutputList = NusmvResultReader.read(verificationOutput);
        List<Map<String, String>> resultList = new ArrayList<>();
        Map<String, String> variableMap = new HashMap<>();
        String variable = "";
        String value = "";
        int state = 0; // 1 STATE, 2 variable, 3 value
        for (int i = 0; i < verificationOutputList.size(); i++) {
            if (verificationOutputList.get(i).equals("STATE")) {
                variableMap = new HashMap<>();
                state = 1;
            } else if (state == 1) {
                variable = verificationOutputList.get(i);
                state = 2;
            } else if (state == 2) {
                value = verificationOutputList.get(i);
                variableMap.put(variable, value);
                if (variable.equals("state")) {
                    variableMap.put("stateToBpmnNodeId", stateToBpmnNodeId(value));
                }
                if (i == verificationOutputList.size() - 1 || verificationOutputList.get(i + 1).equals("STATE")) {
                    resultList.add(variableMap);
                }
                state = 1;
            }
        }
        return resultList;
    }

    private String stateToBpmnNodeId(String state) {
        String[] bpmnFlowIds = state.split("__");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bpmnFlowIds.length; i++) {
            BpmnFlow bpmnFlow = bpmnFlowMap.get(bpmnFlowIds[i]);
            if (i > 0) {
                stringBuilder.append("__");
            }
            stringBuilder.append(bpmnFlow.source);
        }
        return stringBuilder.toString();
    }
}
