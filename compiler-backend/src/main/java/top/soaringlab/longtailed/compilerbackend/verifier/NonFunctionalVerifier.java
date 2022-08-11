package top.soaringlab.longtailed.compilerbackend.verifier;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NonFunctionalVerifier {

    static class GraphNode {

        public String id = "";

        public String name = "";

        public String temporal = "";
    }

    static class GraphEdge {

        public String id = "";

        public String name = "";

        public String source = "";

        public String target = "";

        public String temporal = "";
    }

    private String bpmnXml = "";

    private Map<String, GraphNode> graphNodeMap = new HashMap<>();

    private Map<String, GraphEdge> graphEdgeMap = new HashMap<>();

    private String stnuXml = "";

    private int nVertices = 0;
    private int nEdges = 0;
    private int nContingent = 0;

    private boolean dc = false;

    public boolean nonFunctionalVerify(String file) throws Exception {
        this.bpmnXml = file;
        readBpmnXml();

        writeStnuXml();

        checkDc();
        return dc;
    }

    private void readBpmnXml() throws Exception {
        Document document = DocumentHelper.parseText(bpmnXml);
        document.getRootElement().addNamespace("constraint", "http://some-company/schema/bpmn/constraint");
        List<Node> list = document.selectNodes("//*[@constraint:temporal]");
        for (Node node : list) {
            String localName = node.valueOf("local-name()");
            String id = node.valueOf("@id");
            String name = node.valueOf("@name");
            String temporal = node.valueOf("@constraint:temporal");
            if (localName.equals("sequenceFlow") || localName.equals("messageFlow")) {
                String source = node.valueOf("@sourceRef");
                String target = node.valueOf("@targetRef");
                GraphEdge graphEdge = new GraphEdge();
                graphEdge.id = id;
                graphEdge.name = name;
                graphEdge.source = source;
                graphEdge.target = target;
                graphEdge.temporal = temporal;
                graphEdgeMap.put(id, graphEdge);
            } else {
                GraphNode graphNode = new GraphNode();
                graphNode.id = id;
                graphNode.name = name;
                graphNode.temporal = temporal;
                graphNodeMap.put(id, graphNode);
            }
        }
    }

    private void writeStnuXml() throws Exception {
        InputStream inputStream = new ClassPathResource("stnu.xml").getInputStream();
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        inputStream.close();

        Element graphElement = (Element) document.selectSingleNode("graphml/graph");


        for (GraphNode graphNode : graphNodeMap.values()) {
            graphElement.addElement("node").addAttribute("id", graphNode.id + "_start");
            graphElement.addElement("node").addAttribute("id", graphNode.id + "_end");
            nVertices = nVertices + 2;

            addEdgeElement(graphElement, graphNode.id, graphNode.id + "_start", graphNode.id + "_end", graphNode.temporal);
        }

        for (GraphEdge graphEdge : graphEdgeMap.values()) {

            if (graphEdge.temporal.equals("sequence") || graphEdge.temporal.startsWith("[[")) {
                addEdgeElement(graphElement, graphEdge.id, graphEdge.source + "_end", graphEdge.target + "_start", graphEdge.temporal);
            } else {
                addEdgeElement(graphElement, graphEdge.id, graphEdge.source + "_start", graphEdge.target + "_end", graphEdge.temporal);
            }
        }

        Element nVerticesElement = (Element) graphElement.selectSingleNode("data[@key='nVertices']");
        nVerticesElement.setText(String.valueOf(nVertices));
        Element nEdgesElement = (Element) graphElement.selectSingleNode("data[@key='nEdges']");
        nEdgesElement.setText(String.valueOf(nEdges));
        Element nContingentElement = (Element) graphElement.selectSingleNode("data[@key='nContingent']");
        nContingentElement.setText(String.valueOf(nContingent));

        stnuXml = document.asXML();
    }

    private void addEdgeElement(Element graphElement, String id, String source, String target, String temporal) {
        Element lowerEdgeElement = graphElement.addElement("edge")
                .addAttribute("id", id + "_lower")
                .addAttribute("source", target)
                .addAttribute("target", source);
        Element upperEdgeElement = graphElement.addElement("edge")
                .addAttribute("id", id + "_upper")
                .addAttribute("source", source)
                .addAttribute("target", target);
        nEdges = nEdges + 2;

        if (temporal.equals("sequence")) {
            addDataElement(lowerEdgeElement, "requirement", "-0");
            addDataElement(upperEdgeElement, "requirement", "0");
        } else if (temporal.startsWith("[[")) {
            String[] values = temporal.split("\\[\\[|\\]\\]|,");
            addDataElement(lowerEdgeElement, "contingent", "-" + values[1]);
            addDataElement(upperEdgeElement, "contingent", values[2]);
            nContingent = nContingent + 1;
        } else {
            String[] values = temporal.split("\\[|\\]|,");
            addDataElement(lowerEdgeElement, "requirement", "-" + values[1]);
            addDataElement(upperEdgeElement, "requirement", values[2]);
        }
    }

    private void addDataElement(Element edgeElement, String type, String value) {
        edgeElement.addElement("data")
                .addAttribute("key", "Type")
                .addText(type);
        edgeElement.addElement("data")
                .addAttribute("key", "Value")
                .addText(value);
    }

    private void checkDc() throws Exception {
        String directoryName = "stnu";
        File directoryFile = new File(directoryName);
        if (!directoryFile.exists()) {
            directoryFile.mkdirs();
        }

        String toolName = "CSTNU-Tool-4.5.jar";
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

        String stnuName = UUID.randomUUID() + ".stnu";
        File stnuFile = new File(directoryFile, stnuName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(stnuFile));
        bufferedWriter.write(stnuXml);
        bufferedWriter.close();

        String[] runCommand = new String[]{"java", "-cp", toolName, "it.univr.di.cstnu.algorithms.STNU", stnuName};
        Process process = Runtime.getRuntime().exec(runCommand, null, directoryFile);
        if (!process.waitFor(10000, TimeUnit.MILLISECONDS)) {
            process.destroy();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        bufferedReader.close();
        String output = stringBuilder.toString();
        dc = output.contains("The given STNU is dynamic controllable!");
    }
}
