package top.soaringlab.longtailed.compilerbackend.verifier;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;

public class FunctionalVerifier {

    static class GraphNode {

        public String id = "";

        public String name = "";

        public String type = ""; // node, exclusiveGateway, parallelGateway, sequenceFlow, messageFlow

        public List<String> sequenceInList = new ArrayList<>();

        public List<String> sequenceOutList = new ArrayList<>();

        public List<String> messageInList = new ArrayList<>();

        public List<String> messageOutList = new ArrayList<>();
    }

    static class GraphPath {

        public String id = "";

        public List<String> graphNodeIdList = new ArrayList<>();
    }

    static class GraphConstraint {

        public String type = ""; // response

        public String source = "";

        public String target = "";
    }

    private String xmlnsBpmn;

    private String exclusiveGatewayName;

    private String parallelGatewayName;

    private String sequenceFlowName;

    private String messageFlowName;

    private List<String> nodeNames;

    private String declarativeName = "constraint:declarative";

    private Map<String, String> elementIdTextContentMap = new HashMap<>();

    private Map<String, GraphNode> graphNodeMap = new HashMap<>();

    private Map<String, GraphPath> graphPathMap = new HashMap<>();

    private int currentGraphPathId = 0;

    private String startGraphNodeId = "";

    private String startGraphNodeName = "";

    private List<GraphConstraint> graphConstraintList = new ArrayList<>();

    private Map<String, List<String>> pathConstraintsMap = new HashMap<>();

    public Map<String, List<String>> functionalVerify(String file, String start) throws Exception {
        // parse xml
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(file)));
        startGraphNodeName = start;

        // name
        String tagName = document.getDocumentElement().getTagName();
        if (tagName.equals("bpmn:definitions")) {
            xmlnsBpmn = "bpmn:";
        } else if (tagName.equals("bpmn2:definitions")) {
            xmlnsBpmn = "bpmn2:";
        } else {
            xmlnsBpmn = tagName.split(":")[0] + ":";
        }
        exclusiveGatewayName = xmlnsBpmn + "exclusiveGateway";
        parallelGatewayName = xmlnsBpmn + "parallelGateway";
        sequenceFlowName = xmlnsBpmn + "sequenceFlow";
        messageFlowName = xmlnsBpmn + "messageFlow";
        nodeNames = List.of(
                xmlnsBpmn + "startEvent",
                xmlnsBpmn + "endEvent",
                xmlnsBpmn + "intermediateCatchEvent",
                xmlnsBpmn + "intermediateThrowEvent",
                xmlnsBpmn + "exclusiveGateway",
                xmlnsBpmn + "parallelGateway",
                xmlnsBpmn + "task",
                xmlnsBpmn + "scriptTask",
                xmlnsBpmn + "sendTask",
                xmlnsBpmn + "receiveTask",
                xmlnsBpmn + "userTask",
                xmlnsBpmn + "manualTask",
                xmlnsBpmn + "businessRuleTask",
                xmlnsBpmn + "serviceTask",
                xmlnsBpmn + "sequenceFlow",
                xmlnsBpmn + "messageFlow"
        );

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

        // convert node and flow
        for (String nodeName : nodeNames) {
            convertNode(document, nodeName);
        }

        // generate path
        GraphPath graphPath = new GraphPath();
        graphPath.id = String.valueOf(currentGraphPathId++);
        graphPathMap.put(graphPath.id, graphPath);
        visitGraphNode(Collections.singletonList(graphPath.id), startGraphNodeId);

        // verify
        verifyPaths();
        return pathConstraintsMap;
    }

    private void convertNode(Document document, String tagName) {
        NodeList nodeList = document.getElementsByTagName(tagName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            GraphNode graphNode = new GraphNode();
            graphNode.id = element.getAttribute("id");
            graphNode.name = element.getAttribute("name");
            String sourceRef = element.getAttribute("sourceRef");
            String targetRef = element.getAttribute("targetRef");
            String declarative = element.getAttribute(declarativeName);
            if (tagName.equals(exclusiveGatewayName)) {
                graphNode.type = "exclusiveGateway";
            } else if (tagName.equals(parallelGatewayName)) {
                graphNode.type = "parallelGateway";
            } else if (tagName.equals(sequenceFlowName)) {
                graphNode.type = "sequenceFlow";
                if (!declarative.isEmpty()) {
                    GraphConstraint graphConstraint = new GraphConstraint();
                    graphConstraint.type = declarative;
                    graphConstraint.source = sourceRef;
                    graphConstraint.target = targetRef;
                    graphConstraintList.add(graphConstraint);
                } else {
                    graphNodeMap.get(sourceRef).sequenceOutList.add(graphNode.id);
                    graphNode.sequenceInList.add(sourceRef);
                    graphNode.sequenceOutList.add(targetRef);
                    graphNodeMap.get(targetRef).sequenceInList.add(graphNode.id);
                }
            } else if (tagName.equals(messageFlowName)) {
                graphNode.type = "messageFlow";
                if (!declarative.isEmpty()) {
                    GraphConstraint graphConstraint = new GraphConstraint();
                    graphConstraint.type = declarative;
                    graphConstraint.source = sourceRef;
                    graphConstraint.target = targetRef;
                    graphConstraintList.add(graphConstraint);
                } else {
                    graphNodeMap.get(sourceRef).messageOutList.add(graphNode.id);
                    graphNode.messageInList.add(sourceRef);
                    graphNode.messageOutList.add(targetRef);
                    graphNodeMap.get(targetRef).messageInList.add(graphNode.id);
                }
            } else {
                graphNode.type = "node";
                if (elementIdTextContentMap.containsKey(graphNode.id)) {
                    String textContent = elementIdTextContentMap.get(graphNode.id);
                    if (textContent.contains("SET order_status = \"canceled\"")) {
                        graphNode.type = "exclusiveGateway";

                        GraphNode exitGraphNode = new GraphNode();
                        exitGraphNode.id = graphNode.id + "_exit";
                        exitGraphNode.name = "Exit";
                        exitGraphNode.type = "node";
                        graphNodeMap.put(exitGraphNode.id, exitGraphNode);

                        graphNode.sequenceOutList.add(exitGraphNode.id);
                    }
                }
            }
            if (graphNode.name.equals(startGraphNodeName)) {
                startGraphNodeId = graphNode.id;
            }
            graphNodeMap.put(graphNode.id, graphNode); // problem: constraints are also put in this map
        }
    }

    private List<String> visitGraphNode(List<String> graphPathIdList, String graphNodeId) {
        List<String> returnGraphPathIdList = new ArrayList<>();

        for (String graphPathId : graphPathIdList) {
            GraphPath graphPath = graphPathMap.get(graphPathId);
            GraphNode graphNode = graphNodeMap.get(graphNodeId);

            boolean canVisit = true;
            if (!graphNode.sequenceInList.isEmpty()) {
                if (graphNode.type.equals("parallelGateway")) {
                    for (String sequenceIn : graphNode.sequenceInList) {
                        if (!graphPath.graphNodeIdList.contains(sequenceIn)) {
                            canVisit = false;
                            break;
                        }
                    }
                } else {
                    boolean sequenceReceived = false;
                    for (String sequenceIn : graphNode.sequenceInList) {
                        if (graphPath.graphNodeIdList.contains(sequenceIn)) {
                            sequenceReceived = true;
                            break;
                        }
                    }
                    if (!sequenceReceived) {
                        canVisit = false;
                    }
                }
            }
            if (!graphNode.messageInList.isEmpty()) {
                boolean messageReceived = false;
                for (String messageIn : graphNode.messageInList) {
                    if (graphPath.graphNodeIdList.contains(messageIn)) {
                        messageReceived = true;
                        break;
                    }
                }
                if (!messageReceived) {
                    canVisit = false;
                }
            }

            if (canVisit) {
                graphPath.graphNodeIdList.add(graphNodeId);

                List<String> outList = new ArrayList<>(graphNode.sequenceOutList);
                outList.addAll(graphNode.messageOutList);
                if (!outList.isEmpty()) {
                    if (outList.size() == 1) {
                        returnGraphPathIdList.addAll(visitGraphNode(Collections.singletonList(graphPathId), outList.get(0)));
                    } else {
                        graphPathMap.remove(graphPathId);
                        if (graphNode.type.equals("exclusiveGateway")) {
                            for (String sequenceOut : graphNode.sequenceOutList) {
                                GraphPath newGraphPath = new GraphPath();
                                newGraphPath.id = String.valueOf(currentGraphPathId++);
                                newGraphPath.graphNodeIdList = new ArrayList<>(graphPath.graphNodeIdList);
                                graphPathMap.put(newGraphPath.id, newGraphPath);
                                returnGraphPathIdList.addAll(visitGraphNode(Collections.singletonList(newGraphPath.id), sequenceOut));
                            }
                        } else {
                            for (int i = 0; i < outList.size(); i++) {
                                GraphPath newGraphPath = new GraphPath();
                                newGraphPath.id = String.valueOf(currentGraphPathId++);
                                newGraphPath.graphNodeIdList = new ArrayList<>(graphPath.graphNodeIdList);
                                graphPathMap.put(newGraphPath.id, newGraphPath);
                                List<String> newGraphPathIdList = Collections.singletonList(newGraphPath.id);
                                for (int j = 0; j < outList.size(); j++) {
                                    String out = outList.get((i + j) % outList.size());
                                    newGraphPathIdList = visitGraphNode(newGraphPathIdList, out);
                                }
                                returnGraphPathIdList.addAll(newGraphPathIdList);
                            }
                        }
                    }
                } else {
                    returnGraphPathIdList.add(graphPathId);
                }
            } else {
                returnGraphPathIdList.add(graphPathId);
            }
        }
        return returnGraphPathIdList;
    }

    private boolean verifyPaths() {
        Set<String> graphPathStringSet = new HashSet<>();

        for (GraphPath graphPath : graphPathMap.values()) {
            StringBuilder pathStringBuilder = new StringBuilder();
            for (String graphNodeId : graphPath.graphNodeIdList) {
                GraphNode graphNode = graphNodeMap.get(graphNodeId);
                if (!graphNode.type.equals("sequenceFlow") && !graphNode.type.equals("messageFlow")) {
                    if (pathStringBuilder.length() > 0) {
                        pathStringBuilder.append(", ");
                    }
                    pathStringBuilder.append(graphNode.name);
                }
            }
            String graphPathString = pathStringBuilder.toString();
            if (!graphPathStringSet.contains(graphPathString)) {
                graphPathStringSet.add(graphPathString);

                for (GraphConstraint graphConstraint : graphConstraintList) {
                    if (graphConstraint.type.equals("response")) {
                        GraphNode sourceGraphNode = graphNodeMap.get(graphConstraint.source);
                        GraphNode targetGraphNode = graphNodeMap.get(graphConstraint.target);
                        String graphConstraintString = "response(" + sourceGraphNode.name + ", " + targetGraphNode.name + ")";

                        boolean sourceExecutes = false;
                        boolean targetExecutes = false;
                        for (String graphNodeId : graphPath.graphNodeIdList) {
                            if (graphNodeId.equals(graphConstraint.source)) {
                                sourceExecutes = true;
                            }
                            if (sourceExecutes && graphNodeId.equals(graphConstraint.target)) {
                                targetExecutes = true;
                            }
                        }
                        if (sourceExecutes && !targetExecutes) {
                            if (!pathConstraintsMap.containsKey(graphPathString)) {
                                pathConstraintsMap.put(graphPathString, new ArrayList<>());
                            }
                            pathConstraintsMap.get(graphPathString).add(graphConstraintString);
                        }
                    }
                }
            }
        }
        return pathConstraintsMap.isEmpty();
    }
}
