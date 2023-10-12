package top.soaringlab.longtailed.compilerbackend.verifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;

import top.soaringlab.longtailed.compilerbackend.domain.ControllabilityModel;

public class PrismVerifier {

    class BpmnNode {
        String id = "";
        String name = "";
        String type = ""; // startEvent, intermediateEvent, endEvent, task,
                          // exclusiveGateway, parallelGateway
        List<String> sequenceInList = new ArrayList<>();
        List<String> sequenceOutList = new ArrayList<>();
        List<String> messageInList = new ArrayList<>();
        List<String> messageOutList = new ArrayList<>();
        String annotation = ""; // DSL language
        String functional = ""; // front-end language
        String nonFunctional = ""; // front-end language
    }

    class BpmnFlow {
        String id = "";
        String name = "";
        String type = ""; // sequenceFlow, messageFlow
        String source = "";
        String target = "";
        String condition = ""; // BPMN language
        String functional = ""; // front-end language
        String nonFunctional = ""; // front-end language
    }

    @JSONType(orders = { "name", "type", "defaultValue", "minValue", "maxValue" })
    class BpmnVariableDefinition {
        String name = ""; // unique
        String type = ""; // integer, bool
        String defaultValue = ""; // 0, false, true
        String minValue = "";
        String maxValue = "";

        public BpmnVariableDefinition(String name, String type, String defaultValue, String minValue,
                String maxValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getMinValue() {
            return minValue;
        }

        public void setMinValue(String minValue) {
            this.minValue = minValue;
        }

        public String getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(String maxValue) {
            this.maxValue = maxValue;
        }
    }

    @JSONType(orders = { "flowId", "condition" })
    class BpmnVariableCondition {
        String flowId = ""; // one flow has at most one condition
        String condition = ""; // PRISM language

        public BpmnVariableCondition(String flowId, String condition) {
            this.flowId = flowId;
            this.condition = condition;
        }

        public String getFlowId() {
            return flowId;
        }

        public void setFlowId(String flowId) {
            this.flowId = flowId;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }
    }

    @JSONType(orders = { "nodeId", "variableName", "type", "value", "minValue", "maxValue" })
    class BpmnVariableModification {
        String nodeId = ""; // one activity may modify multiple variables
        String variableName = "";
        String type = ""; // determinate, controllable, uncontrollable
        String value = ""; // for determinate, PRISM language
        String minValue = ""; // for controllable, uncontrollable
        String maxValue = ""; // for controllable, uncontrollable

        public BpmnVariableModification(String nodeId, String variableName, String type, String value,
                String minValue, String maxValue) {
            this.nodeId = nodeId;
            this.variableName = variableName;
            this.type = type;
            this.value = value;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getVariableName() {
            return variableName;
        }

        public void setVariableName(String variableName) {
            this.variableName = variableName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getMinValue() {
            return minValue;
        }

        public void setMinValue(String minValue) {
            this.minValue = minValue;
        }

        public String getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(String maxValue) {
            this.maxValue = maxValue;
        }
    }

    @JSONType(orders = { "name", "value" })
    class BpmnKpi {
        String name;
        String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    class MdpModule { // parallel layer
        int id = -1;
        int parentId = -1;
    }

    class MdpState {
        int moduleId = -1;
        int localId = -1; // in module
        String type = ""; // start, end, action, determinate, controllable, uncontrollable,
                          // exclusiveSplit, exclusiveJoin, parallelSplit, parallelJoin
    }

    class MdpTransition { // one gateway's one flow, or one node modify one variable
        int source = -1;
        int target = -1;
        String bpmnNodeId = "";
        String bpmnFlowId = ""; // if gateway, bpmnFlowVariableConditionMap
        int variableModificationIndex = -1; // if node, bpmnNodeVariableModificationListMap
    }

    public String prismDirectoryName;
    public String prismProgramName;

    private int valueStep = 10;

    private String startBpmnNodeId = "";
    private String startBpmnNodeName = "";
    private Map<String, BpmnNode> bpmnNodeMap = new HashMap<>();
    private Map<String, BpmnFlow> bpmnFlowMap = new HashMap<>();
    private List<BpmnFlow> bpmnFunctionalList = new ArrayList<>();
    private List<BpmnFlow> bpmnNonFunctionalList = new ArrayList<>();

    private Map<String, BpmnVariableDefinition> bpmnVariableDefinitionMap = new HashMap<>();
    private Map<String, BpmnVariableCondition> bpmnFlowVariableConditionMap = new HashMap<>();
    private Map<String, List<BpmnVariableModification>> bpmnNodeVariableModificationListMap = new HashMap<>();
    private List<BpmnKpi> bpmnKpiList = new ArrayList<>();

    private List<MdpModule> mdpModuleList = new ArrayList<>();
    private List<List<MdpState>> mdpModuleStateList = new ArrayList<>();
    private List<List<List<MdpTransition>>> mdpModuleStateTransitionList = new ArrayList<>();

    private Map<String, Integer> bpmnNodeVisitMap = new HashMap<>(); // for gateway
    private Map<String, List<MdpState>> bpmnNodeMdpStateListMap = new HashMap<>(); // for gateway

    private String verificationModel = "";
    private String verificationProperty = "";
    private String verificationOutput = "";

    public ControllabilityModel build(ControllabilityModel controllabilityModel) throws Exception {
        startBpmnNodeName = controllabilityModel.getBpmnStart();

        List<BpmnVariableDefinition> bpmnVariableDefinitionList = JSON
                .parseArray(controllabilityModel.getBpmnVariableDefinitionList(), BpmnVariableDefinition.class);
        for (BpmnVariableDefinition bpmnVariableDefinition : bpmnVariableDefinitionList) {
            bpmnVariableDefinitionMap.put(bpmnVariableDefinition.name, bpmnVariableDefinition);
        }

        List<BpmnVariableCondition> bpmnVariableConditionList = JSON
                .parseArray(controllabilityModel.getBpmnVariableConditionList(), BpmnVariableCondition.class);
        for (BpmnVariableCondition bpmnVariableCondition : bpmnVariableConditionList) {
            bpmnFlowVariableConditionMap.put(bpmnVariableCondition.flowId, bpmnVariableCondition);
        }

        List<BpmnVariableModification> bpmnVariableModificationList = JSON.parseArray(
                controllabilityModel.getBpmnVariableModificationList(),
                BpmnVariableModification.class);
        for (BpmnVariableModification bpmnVariableModification : bpmnVariableModificationList) {
            if (!bpmnNodeVariableModificationListMap.containsKey(bpmnVariableModification.nodeId)) {
                bpmnNodeVariableModificationListMap.put(bpmnVariableModification.nodeId, new ArrayList<>());
            }
            bpmnNodeVariableModificationListMap.get(bpmnVariableModification.nodeId).add(bpmnVariableModification);
        }

        bpmnKpiList = JSON.parseArray(controllabilityModel.getBpmnKpiList(), BpmnKpi.class);

        readBpmnXml(controllabilityModel.getBpmnModel());
        // readConstraint(fileConstraint);
        bpmnToMdp();
        writeVerificationModel();
        // writeVerificationProperty();

        controllabilityModel.setVerificationModel(verificationModel);
        return controllabilityModel;
    }

    public ControllabilityModel verify(ControllabilityModel controllabilityModel) throws Exception {
        verificationProperty = controllabilityModel.getVerificationProperty();

        modelCheck();

        controllabilityModel.setVerificationOutput(verificationOutput);
        return controllabilityModel;
    }

    public void verifyTest(String name) throws Exception {
        String directoryName = "test-case\\prism";
        File directoryFile = new File(directoryName);

        String bpmnName = name + ".bpmn";
        File bpmnFile = new File(directoryFile, bpmnName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(bpmnFile));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        bufferedReader.close();
        String bpmn = stringBuilder.toString();

        bpmnVariableDefinitionMap.put("v1", new BpmnVariableDefinition("v1", "integer", "2", "1", "10"));
        bpmnVariableDefinitionMap.put("v2", new BpmnVariableDefinition("v2", "integer", "4", "2", "20"));
        bpmnVariableDefinitionMap.put("v3", new BpmnVariableDefinition("v3", "bool", "true", "xxxx", "xxxx"));

        bpmnFlowVariableConditionMap.put("Flow_00hmmyn", new BpmnVariableCondition("Flow_00hmmyn", "v1>6"));
        bpmnFlowVariableConditionMap.put("Flow_12nrnhl", new BpmnVariableCondition("Flow_12nrnhl", "v2>12"));
        bpmnFlowVariableConditionMap.put("Flow_1nbkpuo", new BpmnVariableCondition("Flow_1nbkpuo", "v3=true"));

        // bpmnNodeVariableModificationListMap.put("Activity_02jvj9o", List.of(
        // new BpmnVariableModification("Activity_02jvj9o", "v1", "determinate",
        // "v2+10", "xxxx", "xxxx")));
        // bpmnNodeVariableModificationListMap.put("Activity_1s2hzze", List.of(
        // new BpmnVariableModification("Activity_1s2hzze", "v2", "controllable",
        // "xxxx", "5", "8")));
        // bpmnNodeVariableModificationListMap.put("Activity_0ajglz1", List.of(
        // new BpmnVariableModification("Activity_0ajglz1", "v2", "uncontrollable",
        // "xxxx", "15", "18")));

        bpmnNodeVariableModificationListMap.put("Activity_02jvj9o", List.of(
                new BpmnVariableModification("Activity_02jvj9o", "v1", "determinate",
                        "v2+10", "xxxx", "xxxx"),
                new BpmnVariableModification("Activity_02jvj9o", "v2", "controllable",
                        "xxxx", "5", "8"),
                new BpmnVariableModification("Activity_02jvj9o", "v2", "uncontrollable",
                        "xxxx", "15", "18")));

        startBpmnNodeName = "Start";
        readBpmnXml(bpmn);
        // readConstraint(bpmn);
        bpmnToMdp();
        writeVerificationModel();
        // writeVerificationProperty();
        modelCheck();
    }

    public void jsonTest() throws Exception {
        String directoryName = "test-case\\prism";
        File directoryFile = new File(directoryName);

        String json;
        String jsonName;
        File jsonFile;
        BufferedWriter bufferedWriter;

        List<BpmnVariableDefinition> bpmnVariableDefinitionList = List.of(
                new BpmnVariableDefinition("item_type", "integer", "0", "0", "1"),
                new BpmnVariableDefinition("item_quality", "integer", "0", "0", "5"),
                new BpmnVariableDefinition("issue_priority", "integer", "0", "0", "5"),
                new BpmnVariableDefinition("delivery_temperature", "integer", "0", "0", "1"),
                new BpmnVariableDefinition("delivery_time", "integer", "0", "0", "1000"),
                new BpmnVariableDefinition("discount_amount", "integer", "0", "0", "1000"));
        json = JSON.toJSONString(bpmnVariableDefinitionList);
        jsonName = "bpmnVariableDefinitionList.json";
        jsonFile = new File(directoryFile, jsonName);
        bufferedWriter = new BufferedWriter(new FileWriter(jsonFile));
        bufferedWriter.write(json);
        bufferedWriter.close();

        List<BpmnVariableCondition> bpmnVariableConditionList = List.of(
                new BpmnVariableCondition("Flow_078lh8w", "delivery_temperature=0"),
                new BpmnVariableCondition("Flow_19dmqro", "delivery_temperature=1"));
        json = JSON.toJSONString(bpmnVariableConditionList);
        jsonName = "bpmnVariableConditionList.json";
        jsonFile = new File(directoryFile, jsonName);
        bufferedWriter = new BufferedWriter(new FileWriter(jsonFile));
        bufferedWriter.write(json);
        bufferedWriter.close();

        List<BpmnVariableModification> bpmnVariableModificationList = List.of(
                new BpmnVariableModification("Activity_06zlm8c", "item_type", "uncontrollable",
                        "", "0", "1"),
                new BpmnVariableModification("Activity_06zlm8c", "item_quality", "uncontrollable",
                        "", "3", "5"),
                new BpmnVariableModification("Activity_1x91s1w", "issue_priority", "controllable",
                        "", "3", "5"),
                new BpmnVariableModification("Activity_1x91s1w", "delivery_temperature", "controllable",
                        "", "0", "1"),
                new BpmnVariableModification("Activity_1fnbzn3", "delivery_time", "uncontrollable",
                        "", "0", "120"),
                new BpmnVariableModification("Activity_1r0drlg", "discount_amount", "controllable",
                        "", "0", "50"));
        json = JSON.toJSONString(bpmnVariableModificationList);
        jsonName = "bpmnVariableModificationList.json";
        jsonFile = new File(directoryFile, jsonName);
        bufferedWriter = new BufferedWriter(new FileWriter(jsonFile));
        bufferedWriter.write(json);
        bufferedWriter.close();
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
                xmlnsBpmn + "intermediateThrowEvent");
        List<String> taskNames = List.of(
                xmlnsBpmn + "task",
                xmlnsBpmn + "scriptTask",
                xmlnsBpmn + "sendTask",
                xmlnsBpmn + "receiveTask",
                xmlnsBpmn + "userTask",
                xmlnsBpmn + "manualTask",
                xmlnsBpmn + "businessRuleTask",
                xmlnsBpmn + "serviceTask");
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
        String conditionExpressionName = xmlnsBpmn + "conditionExpression";
        String scriptName = xmlnsBpmn + "script";
        String functionalName = "constraint:functional";
        String nonFunctionalName = "constraint:nonFunctional";

        // node
        for (String nodeName : nodeNames) {
            NodeList nodeList = document.getElementsByTagName(nodeName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                BpmnNode bpmnNode = new BpmnNode();
                bpmnNode.id = element.getAttribute("id");
                bpmnNode.name = element.getAttribute("name");
                bpmnNode.functional = element.getAttribute(functionalName);
                bpmnNode.nonFunctional = element.getAttribute(nonFunctionalName);
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
                bpmnFlow.functional = element.getAttribute(functionalName);
                bpmnFlow.nonFunctional = element.getAttribute(nonFunctionalName);
                if (!bpmnFlow.functional.isEmpty()) {
                    // bpmnFunctionalList.add(bpmnFlow);
                } else if (!bpmnFlow.nonFunctional.isEmpty()) {
                    // bpmnNonFunctionalList.add(bpmnFlow);
                } else if (flowName.equals(sequenceFlowName)) {
                    bpmnFlow.type = "sequenceFlow";
                    bpmnNodeMap.get(bpmnFlow.source).sequenceOutList.add(bpmnFlow.id);
                    bpmnNodeMap.get(bpmnFlow.target).sequenceInList.add(bpmnFlow.id);
                    NodeList conditionExpressionNodeList = element.getElementsByTagName(conditionExpressionName);
                    if (conditionExpressionNodeList.getLength() > 0) {
                        Element conditionExpressionElement = (Element) conditionExpressionNodeList.item(0);
                        bpmnFlow.condition = conditionExpressionElement.getTextContent();
                    }
                    bpmnFlowMap.put(bpmnFlow.id, bpmnFlow);
                } else if (flowName.equals(messageFlowName)) {
                    bpmnFlow.type = "messageFlow";
                    // bpmnNodeMap.get(bpmnFlow.source).messageOutList.add(bpmnFlow.id);
                    // bpmnNodeMap.get(bpmnFlow.target).messageInList.add(bpmnFlow.id);
                    // bpmnFlowMap.put(bpmnFlow.id, bpmnFlow);
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
        String functionalName = "constraint:functional";
        String nonFunctionalName = "constraint:nonFunctional";

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
                bpmnFlow.functional = element.getAttribute(functionalName);
                bpmnFlow.nonFunctional = element.getAttribute(nonFunctionalName);
                if (!bpmnFlow.functional.isEmpty()) {
                    bpmnFunctionalList.add(bpmnFlow);
                } else if (!bpmnFlow.nonFunctional.isEmpty()) {
                    bpmnNonFunctionalList.add(bpmnFlow);
                }
            }
        }
    }

    private void bpmnToMdp() {
        searchBpmn(-1, -1, startBpmnNodeId);
    }

    private void searchBpmn(int moduleId, int localId, String bpmnNodeId) {
        MdpState mdpState = new MdpState();
        if (moduleId >= 0 && localId >= 0) {
            mdpState = mdpModuleStateList.get(moduleId).get(localId);
        }
        BpmnNode bpmnNode = bpmnNodeMap.get(bpmnNodeId);
        if (bpmnNode.type.equals("startEvent")) {
            MdpModule nextMdpModule = newMdpModule(moduleId);
            MdpState nextMdpState = newMdpState(nextMdpModule.id);
            BpmnFlow bpmnFlow = bpmnFlowMap.get(bpmnNode.sequenceOutList.get(0));
            searchBpmn(nextMdpState.moduleId, nextMdpState.localId, bpmnFlow.target);
        } else if (bpmnNode.type.equals("endEvent")) {
            mdpState.type = "end";
            // MdpTransition mdpTransition = newMdpTransition(moduleId, localId);
            // mdpTransition.source = localId;
            // mdpTransition.target = localId;
            // mdpTransition.bpmnNodeId = bpmnNodeId;
        } else if (bpmnNode.type.equals("intermediateEvent") || bpmnNode.type.equals("task")) {
            MdpTransition mdpTransition = new MdpTransition();
            MdpState nextMdpState = new MdpState();
            if (!bpmnNodeVariableModificationListMap.containsKey(bpmnNodeId)) {
                mdpState.type = "action";
                mdpTransition = newMdpTransition(moduleId, localId);
                nextMdpState = newMdpState(moduleId);
                mdpTransition.source = localId;
                mdpTransition.target = nextMdpState.localId;
                mdpTransition.bpmnNodeId = bpmnNodeId;
            } else {
                List<BpmnVariableModification> bpmnVariableModificationList = bpmnNodeVariableModificationListMap
                        .get(bpmnNodeId);
                for (int i = 0; i < bpmnVariableModificationList.size(); i++) {
                    BpmnVariableModification bpmnVariableModification = bpmnVariableModificationList.get(i);
                    mdpState.type = bpmnVariableModification.type;
                    mdpTransition = newMdpTransition(moduleId, localId);
                    nextMdpState = newMdpState(moduleId);
                    mdpTransition.source = localId;
                    mdpTransition.target = nextMdpState.localId;
                    mdpTransition.bpmnNodeId = bpmnNodeId;
                    mdpTransition.variableModificationIndex = i;
                    mdpState = nextMdpState;
                    localId = nextMdpState.localId;
                }
            }
            BpmnFlow bpmnFlow = bpmnFlowMap.get(bpmnNode.sequenceOutList.get(0));
            searchBpmn(nextMdpState.moduleId, nextMdpState.localId, bpmnFlow.target);
        } else if (bpmnNode.type.equals("exclusiveGateway")) {
            if (bpmnNode.sequenceInList.size() == 1 && bpmnNode.sequenceOutList.size() == 1) {
                mdpState.type = "action";
                MdpTransition mdpTransition = newMdpTransition(moduleId, localId);
                MdpState nextMdpState = newMdpState(moduleId);
                mdpTransition.source = localId;
                mdpTransition.target = nextMdpState.localId;
                mdpTransition.bpmnNodeId = bpmnNodeId;
                BpmnFlow bpmnFlow = bpmnFlowMap.get(bpmnNode.sequenceOutList.get(0));
                searchBpmn(nextMdpState.moduleId, nextMdpState.localId, bpmnFlow.target);
            } else if (bpmnNode.sequenceOutList.size() > 1) {
                mdpState.type = "exclusiveSplit";
                for (int i = 0; i < bpmnNode.sequenceOutList.size(); i++) {
                    String sequenceOut = bpmnNode.sequenceOutList.get(i);
                    MdpTransition mdpTransition = newMdpTransition(moduleId, localId);
                    MdpState nextMdpState = newMdpState(moduleId);
                    mdpTransition.source = localId;
                    mdpTransition.target = nextMdpState.localId;
                    mdpTransition.bpmnNodeId = bpmnNodeId;
                    mdpTransition.bpmnFlowId = sequenceOut;
                    BpmnFlow bpmnFlow = bpmnFlowMap.get(sequenceOut);
                    searchBpmn(nextMdpState.moduleId, nextMdpState.localId, bpmnFlow.target);
                }
            } else if (bpmnNode.sequenceInList.size() > 1) {
                mdpState.type = "exclusiveJoin";
                int visit = bpmnNodeVisitMap.getOrDefault(bpmnNodeId, 0) + 1;
                bpmnNodeVisitMap.put(bpmnNodeId, visit);
                List<MdpState> mdpStateList = bpmnNodeMdpStateListMap.getOrDefault(bpmnNodeId, new ArrayList<>());
                mdpStateList.add(mdpModuleStateList.get(moduleId).get(localId));
                bpmnNodeMdpStateListMap.put(bpmnNodeId, mdpStateList);
                if (bpmnNode.sequenceInList.size() == visit) {
                    MdpState nextMdpState = newMdpState(moduleId);
                    for (MdpState lastMdpState : mdpStateList) {
                        MdpTransition mdpTransition = newMdpTransition(moduleId, lastMdpState.localId);
                        mdpTransition.source = lastMdpState.localId;
                        mdpTransition.target = nextMdpState.localId;
                        mdpTransition.bpmnNodeId = bpmnNodeId;
                    }
                    BpmnFlow bpmnFlow = bpmnFlowMap.get(bpmnNode.sequenceOutList.get(0));
                    searchBpmn(nextMdpState.moduleId, nextMdpState.localId, bpmnFlow.target);
                }
            }
        } else if (bpmnNode.type.equals("parallelGateway")) {
            if (bpmnNode.sequenceInList.size() == 1 && bpmnNode.sequenceOutList.size() == 1) {
                mdpState.type = "action";
                MdpTransition mdpTransition = newMdpTransition(moduleId, localId);
                MdpState nextMdpState = newMdpState(moduleId);
                mdpTransition.source = localId;
                mdpTransition.target = nextMdpState.localId;
                mdpTransition.bpmnNodeId = bpmnNodeId;
                BpmnFlow bpmnFlow = bpmnFlowMap.get(bpmnNode.sequenceOutList.get(0));
                searchBpmn(nextMdpState.moduleId, nextMdpState.localId, bpmnFlow.target);
            } else if (bpmnNode.sequenceOutList.size() > 1) {
                mdpState.type = "parallelSplit";
                MdpTransition mdpTransition = newMdpTransition(moduleId, localId);
                MdpState nextMdpState = newMdpState(moduleId);
                mdpTransition.source = localId;
                mdpTransition.target = nextMdpState.localId;
                mdpTransition.bpmnNodeId = bpmnNodeId;
                List<MdpState> mdpStateList = bpmnNodeMdpStateListMap.getOrDefault(bpmnNodeId, new ArrayList<>());
                for (int i = 0; i < bpmnNode.sequenceOutList.size(); i++) {
                    String sequenceOut = bpmnNode.sequenceOutList.get(i);
                    MdpModule childMdpModule = newMdpModule(moduleId);
                    MdpState childMdpState = newMdpState(childMdpModule.id);
                    childMdpState.type = "start";
                    MdpTransition childMdpTransition = newMdpTransition(childMdpModule.id, childMdpState.localId);
                    MdpState startMdpState = newMdpState(childMdpModule.id);
                    childMdpTransition.source = childMdpState.localId;
                    childMdpTransition.target = startMdpState.localId;
                    mdpStateList.add(childMdpState);
                    bpmnNodeMdpStateListMap.put(bpmnNodeId, mdpStateList);
                    BpmnFlow bpmnFlow = bpmnFlowMap.get(sequenceOut);
                    searchBpmn(startMdpState.moduleId, startMdpState.localId, bpmnFlow.target);
                }
            } else if (bpmnNode.sequenceInList.size() > 1) {
                mdpState.type = "end";
                int visit = bpmnNodeVisitMap.getOrDefault(bpmnNodeId, 0) + 1;
                bpmnNodeVisitMap.put(bpmnNodeId, visit);
                List<MdpState> mdpStateList = bpmnNodeMdpStateListMap.getOrDefault(bpmnNodeId, new ArrayList<>());
                mdpStateList.add(mdpModuleStateList.get(moduleId).get(localId));
                bpmnNodeMdpStateListMap.put(bpmnNodeId, mdpStateList);
                if (bpmnNode.sequenceInList.size() == visit) {
                    MdpModule mdpModule = mdpModuleList.get(moduleId);
                    MdpModule parentMdpModule = mdpModuleList.get(mdpModule.parentId);
                    int parentMdpStateId = mdpModuleStateList.get(parentMdpModule.id).size() - 1;
                    MdpState parentMdpState = mdpModuleStateList.get(parentMdpModule.id).get(parentMdpStateId);
                    parentMdpState.type = "parallelJoin";
                    MdpTransition parentMdpTransition = newMdpTransition(parentMdpModule.id, parentMdpState.localId);
                    MdpState nextMdpState = newMdpState(parentMdpModule.id);
                    parentMdpTransition.source = parentMdpState.localId;
                    parentMdpTransition.target = nextMdpState.localId;
                    parentMdpTransition.bpmnNodeId = bpmnNodeId;
                    BpmnFlow bpmnFlow = bpmnFlowMap.get(bpmnNode.sequenceOutList.get(0));
                    searchBpmn(nextMdpState.moduleId, nextMdpState.localId, bpmnFlow.target);
                }
            }
        }
    }

    private MdpModule newMdpModule(int parentId) {
        MdpModule mdpModule = new MdpModule();
        mdpModule.id = mdpModuleList.size();
        mdpModule.parentId = parentId;
        mdpModuleList.add(mdpModule);
        mdpModuleStateList.add(new ArrayList<>());
        mdpModuleStateTransitionList.add(new ArrayList<>());
        return mdpModule;
    }

    private MdpState newMdpState(int moduleId) {
        List<MdpState> mdpStateList = mdpModuleStateList.get(moduleId);
        MdpState mdpState = new MdpState();
        mdpState.moduleId = moduleId;
        mdpState.localId = mdpStateList.size();
        mdpStateList.add(mdpState);
        mdpModuleStateTransitionList.get(moduleId).add(new ArrayList<>());
        return mdpState;
    }

    private MdpTransition newMdpTransition(int moduleId, int localId) {
        List<MdpTransition> mdpTransitionList = mdpModuleStateTransitionList.get(moduleId).get(localId);
        MdpTransition mdpTransition = new MdpTransition();
        mdpTransition.source = localId;
        mdpTransitionList.add(mdpTransition);
        return mdpTransition;
    }

    private void writeVerificationModel() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("mdp\n\n");

        for (int moduleId = 0; moduleId < mdpModuleList.size(); moduleId++) {
            List<MdpState> mdpStateList = mdpModuleStateList.get(moduleId);
            stringBuilder.append("global s").append(moduleId).append(" : [0..").append(mdpStateList.size() - 1)
                    .append("] init 0;\n");
        }
        stringBuilder.append("\n");

        for (Map.Entry<String, BpmnVariableDefinition> entry : bpmnVariableDefinitionMap.entrySet()) {
            BpmnVariableDefinition bpmnVariableDefinition = entry.getValue();
            if (bpmnVariableDefinition.type.equals("integer")) {
                stringBuilder.append("global ").append(bpmnVariableDefinition.name).append(" : [")
                        .append(bpmnVariableDefinition.minValue).append("..").append(bpmnVariableDefinition.maxValue)
                        .append("] init ").append(bpmnVariableDefinition.defaultValue).append(";\n");
            } else if (bpmnVariableDefinition.type.equals("bool")) {
                stringBuilder.append("global ").append(bpmnVariableDefinition.name).append(" : bool init ")
                        .append(bpmnVariableDefinition.defaultValue).append(";\n");
            }
        }
        stringBuilder.append("\n");

        for (BpmnKpi bpmnKpi : bpmnKpiList) {
            stringBuilder.append("formula ").append(bpmnKpi.name).append("=").append(bpmnKpi.value).append(";\n");
        }
        stringBuilder.append("\n");

        for (int moduleId = 0; moduleId < mdpModuleList.size(); moduleId++) {
            stringBuilder.append("module M").append(moduleId).append("\n");
            List<MdpState> mdpStateList = mdpModuleStateList.get(moduleId);
            for (int localId = 0; localId < mdpStateList.size(); localId++) {
                MdpState mdpState = mdpStateList.get(localId);
                List<MdpTransition> mdpTransitionList = mdpModuleStateTransitionList.get(moduleId).get(localId);
                if (mdpState.type.equals("start")) {
                } else if (mdpState.type.equals("end")) {
                    stringBuilder.append("    [] s").append(moduleId).append("=").append(localId).append(" -> ")
                            .append("true;\n");
                } else if (mdpState.type.equals("action")) {
                    MdpTransition mdpTransition = mdpTransitionList.get(0);
                    stringBuilder.append("    [] s").append(moduleId).append("=").append(localId).append(" -> ")
                            .append("(s").append(moduleId).append("'=").append(mdpTransition.target).append(");\n");
                } else if (mdpState.type.equals("determinate")) {
                    MdpTransition mdpTransition = mdpTransitionList.get(0);
                    BpmnVariableModification bpmnVariableModification = bpmnNodeVariableModificationListMap
                            .get(mdpTransition.bpmnNodeId).get(mdpTransition.variableModificationIndex);
                    stringBuilder.append("    [] s").append(moduleId).append("=").append(localId).append(" -> ")
                            .append("(s").append(moduleId).append("'=").append(mdpTransition.target).append(") & (")
                            .append(bpmnVariableModification.variableName).append("'=")
                            .append(bpmnVariableModification.value).append(");\n");
                } else if (mdpState.type.equals("controllable")) {
                    MdpTransition mdpTransition = mdpTransitionList.get(0);
                    BpmnVariableModification bpmnVariableModification = bpmnNodeVariableModificationListMap
                            .get(mdpTransition.bpmnNodeId).get(mdpTransition.variableModificationIndex);
                    int minValue = Integer.valueOf(bpmnVariableModification.minValue);
                    int maxValue = Integer.valueOf(bpmnVariableModification.maxValue);
                    for (int value = minValue; value < maxValue; value += valueStep) {
                        stringBuilder.append("    [] s").append(moduleId).append("=").append(localId).append(" -> ")
                                .append("(s").append(moduleId).append("'=").append(mdpTransition.target).append(") & (")
                                .append(bpmnVariableModification.variableName).append("'=").append(value)
                                .append(");\n");
                    }
                    stringBuilder.append("    [] s").append(moduleId).append("=").append(localId).append(" -> ")
                            .append("(s").append(moduleId).append("'=").append(mdpTransition.target).append(") & (")
                            .append(bpmnVariableModification.variableName).append("'=").append(maxValue)
                            .append(");\n");
                } else if (mdpState.type.equals("uncontrollable")) {
                    MdpTransition mdpTransition = mdpTransitionList.get(0);
                    BpmnVariableModification bpmnVariableModification = bpmnNodeVariableModificationListMap
                            .get(mdpTransition.bpmnNodeId).get(mdpTransition.variableModificationIndex);
                    int minValue = Integer.valueOf(bpmnVariableModification.minValue);
                    int maxValue = Integer.valueOf(bpmnVariableModification.maxValue);
                    int count = (maxValue - minValue + valueStep - 1) / valueStep + 1;
                    stringBuilder.append("    [] s").append(moduleId).append("=").append(localId).append(" -> ");
                    for (int value = minValue; value < maxValue; value += valueStep) {
                        if (value > minValue) {
                            stringBuilder.append("        + ");
                        }
                        stringBuilder.append("1/").append(count).append(" : ")
                                .append("(s").append(moduleId).append("'=").append(mdpTransition.target).append(") & (")
                                .append(bpmnVariableModification.variableName).append("'=").append(value).append(")\n");
                    }
                    if (maxValue > minValue) {
                        stringBuilder.append("        + ");
                    }
                    stringBuilder.append("1/").append(count).append(" : ")
                            .append("(s").append(moduleId).append("'=").append(mdpTransition.target).append(") & (")
                            .append(bpmnVariableModification.variableName).append("'=").append(maxValue).append(")\n");
                    stringBuilder.append("        ;\n");
                } else if (mdpState.type.equals("exclusiveSplit")) {
                    for (int transitionId = 0; transitionId < mdpTransitionList.size(); transitionId++) {
                        MdpTransition mdpTransition = mdpTransitionList.get(transitionId);
                        BpmnVariableCondition bpmnVariableCondition = bpmnFlowVariableConditionMap
                                .get(mdpTransition.bpmnFlowId);
                        String condition = "true";
                        if (bpmnVariableCondition != null) {
                            condition = bpmnVariableCondition.condition;
                        }
                        stringBuilder.append("    [] s").append(moduleId).append("=").append(localId).append(" & ")
                                .append(condition).append(" -> ")
                                .append("(s").append(moduleId).append("'=").append(mdpTransition.target).append(");\n");
                    }
                } else if (mdpState.type.equals("exclusiveJoin")) {
                    MdpTransition mdpTransition = mdpTransitionList.get(0);
                    stringBuilder.append("    [] s").append(moduleId).append("=").append(localId).append(" -> ")
                            .append("(s").append(moduleId).append("'=").append(mdpTransition.target).append(");\n");
                } else if (mdpState.type.equals("parallelSplit")) {
                    MdpTransition mdpTransition = mdpTransitionList.get(0);
                    stringBuilder.append("    [] s").append(moduleId).append("=").append(localId).append(" -> ")
                            .append("(s").append(moduleId).append("'=").append(mdpTransition.target).append(")\n");
                    List<MdpState> bpmnNodeMdpStateList = bpmnNodeMdpStateListMap.get(mdpTransition.bpmnNodeId);
                    for (MdpState bpmnNodeMdpState : bpmnNodeMdpStateList) {
                        stringBuilder.append("        & (s").append(bpmnNodeMdpState.moduleId).append("'=")
                                .append(1).append(")\n");
                    }
                    stringBuilder.append("        ;\n");
                } else if (mdpState.type.equals("parallelJoin")) {
                    MdpTransition mdpTransition = mdpTransitionList.get(0);
                    stringBuilder.append("    [] s").append(moduleId).append("=").append(localId).append("\n");
                    List<MdpState> bpmnNodeMdpStateList = bpmnNodeMdpStateListMap.get(mdpTransition.bpmnNodeId);
                    for (MdpState bpmnNodeMdpState : bpmnNodeMdpStateList) {
                        stringBuilder.append("        & (s").append(bpmnNodeMdpState.moduleId).append("=")
                                .append(bpmnNodeMdpState.localId).append(")\n");
                    }
                    stringBuilder.append("        -> ").append("(s").append(moduleId).append("'=")
                            .append(mdpTransition.target).append(");\n");
                }
            }
            stringBuilder.append("endmodule").append("\n\n");
        }

        stringBuilder.append("system\n    ");
        for (int moduleId = 0; moduleId < mdpModuleList.size(); moduleId++) {
            if (moduleId > 0) {
                stringBuilder.append(" || ");
            }
            stringBuilder.append("M").append(moduleId);
        }
        stringBuilder.append("\nendsystem\n\n");

        verificationModel = stringBuilder.toString();
    }

    private void writeVerificationProperty() {
    }

    private void modelCheck() throws Exception {
        String directoryName = "prism";
        File directoryFile = new File(directoryName);
        if (!directoryFile.exists()) {
            directoryFile.mkdirs();
        }

        String name = UUID.randomUUID().toString();

        String modelName = name + ".prism";
        File modelFile = new File(directoryFile, modelName);
        BufferedWriter modelBufferedWriter = new BufferedWriter(new FileWriter(modelFile));
        modelBufferedWriter.write(verificationModel);
        modelBufferedWriter.close();

        String propertyName = name + ".props";
        File propertyFile = new File(directoryFile, propertyName);
        BufferedWriter propertyBufferedWriter = new BufferedWriter(new FileWriter(propertyFile));
        propertyBufferedWriter.write(verificationProperty);
        propertyBufferedWriter.close();

        // String prismDirectoryName = "C:\\Program Files\\prism-4.7\\bin";
        File prismDirectoryFile = new File(prismDirectoryName);
        // String prismProgramName = "prism.bat";
        // File prismProgramFile = new File(prismDirectoryFile, prismProgramName);

        String[] runCommand = new String[] { prismProgramName,
                modelFile.getAbsolutePath(), propertyFile.getAbsolutePath() };
        Process process = Runtime.getRuntime().exec(runCommand, null, prismDirectoryFile);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        bufferedReader.close();
        if (!process.waitFor(10, TimeUnit.SECONDS)) {
            process.destroy();
        }

        verificationOutput = stringBuilder.toString();
    }
}
