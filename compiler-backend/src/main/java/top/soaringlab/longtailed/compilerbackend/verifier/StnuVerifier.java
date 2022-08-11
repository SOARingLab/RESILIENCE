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

public class StnuVerifier {

    static class StnuNode {
        String id = "";
        String name = "";
        String temporal = "";
    }

    static class StnuEdge {
        String id = "";
        String name = "";
        String source = "";
        String target = "";
        String temporal = "";
    }

    private Map<String, StnuNode> stnuNodeMap = new HashMap<>();

    private Map<String, StnuEdge> stnuEdgeMap = new HashMap<>();

    private int nVertices = 0;

    private int nEdges = 0;

    private int nContingent = 0;

    public boolean nonFunctionalVerify(String file) throws Exception {
        readBpmnXml(file);
        String stnuXml = writeStnuXml();
        return checkDc(stnuXml);
    }

    private void readBpmnXml(String bpmnXml) throws Exception {
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
                StnuEdge stnuEdge = new StnuEdge();
                stnuEdge.id = id;
                stnuEdge.name = name;
                stnuEdge.source = source;
                stnuEdge.target = target;
                stnuEdge.temporal = temporal;
                stnuEdgeMap.put(id, stnuEdge);
            } else {
                StnuNode stnuNode = new StnuNode();
                stnuNode.id = id;
                stnuNode.name = name;
                stnuNode.temporal = temporal;
                stnuNodeMap.put(id, stnuNode);
            }
        }
    }

    private String writeStnuXml() throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new ClassPathResource("stnu.xml").getFile());

        Element graphElement = (Element) document.selectSingleNode("graphml/graph");

        for (StnuNode stnuNode : stnuNodeMap.values()) {
            graphElement.addElement("node").addAttribute("id", stnuNode.id + "_start");
            graphElement.addElement("node").addAttribute("id", stnuNode.id + "_end");
            nVertices = nVertices + 2;

            addEdgeElement(graphElement, stnuNode.id, stnuNode.id + "_start", stnuNode.id + "_end", stnuNode.temporal);
        }

        for (StnuEdge stnuEdge : stnuEdgeMap.values()) {
            if (stnuEdge.temporal.equals("sequence") || stnuEdge.temporal.startsWith("[[")) {
                addEdgeElement(graphElement, stnuEdge.id, stnuEdge.source + "_end", stnuEdge.target + "_start", stnuEdge.temporal);
            } else {
                addEdgeElement(graphElement, stnuEdge.id, stnuEdge.source + "_start", stnuEdge.target + "_end", stnuEdge.temporal);
            }
        }

        Element nVerticesElement = (Element) graphElement.selectSingleNode("data[@key='nVertices']");
        nVerticesElement.setText(String.valueOf(nVertices));
        Element nEdgesElement = (Element) graphElement.selectSingleNode("data[@key='nEdges']");
        nEdgesElement.setText(String.valueOf(nEdges));
        Element nContingentElement = (Element) graphElement.selectSingleNode("data[@key='nContingent']");
        nContingentElement.setText(String.valueOf(nContingent));

        return document.asXML();
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

    private boolean checkDc(String stnuXml) throws Exception {
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
        if (!process.waitFor(10, TimeUnit.SECONDS)) {
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
        return output.contains("The given STNU is dynamic controllable!");
    }
}
