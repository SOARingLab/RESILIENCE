package top.soaringlab.longtailed.compilerbackend.compiler;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import java.util.List;

public class ConstraintRemover {

    private Document document;

    public String constraintRemoveAll(String file) throws Exception {
        readBpmnXml(file);
        removeFunctional();
        removeNonFunctional();
        return writeBpmnXml();
    }

    public String constraintRemoveFunctional(String file) throws Exception {
        readBpmnXml(file);
        removeFunctional();
        return writeBpmnXml();
    }

    public String constraintRemoveNonFunctional(String file) throws Exception {
        readBpmnXml(file);
        removeNonFunctional();
        return writeBpmnXml();
    }

    private void readBpmnXml(String bpmnXml) throws Exception {
        document = DocumentHelper.parseText(bpmnXml);
        document.getRootElement().addNamespace("constraint", "http://some-company/schema/bpmn/constraint");
    }

    private String writeBpmnXml() {
        return document.asXML();
    }

    private void removeFunctional() {
        List<Node> nodeList = document.selectNodes("//*[@constraint:declarative]");
        for (Node node : nodeList) {
            String localName = node.valueOf("local-name()");
            String id = node.valueOf("@id");
            String value = node.valueOf("@constraint:declarative");
            if (localName.equals("sequenceFlow") || localName.equals("messageFlow")) {
                node.getParent().remove(node);
                removeGoing(id);
                removeDi(id);
            }
        }
    }

    private void removeNonFunctional() {
        List<Node> nodeList = document.selectNodes("//*[@constraint:temporal]");
        for (Node node : nodeList) {
            String localName = node.valueOf("local-name()");
            String id = node.valueOf("@id");
            String value = node.valueOf("@constraint:temporal");
            if (
                    (localName.equals("sequenceFlow") || localName.equals("messageFlow"))
                            && (value.contains(":S") || value.contains(":E"))
            ) {
                node.getParent().remove(node);
                removeGoing(id);
                removeDi(id);
            }
        }
    }

    private void removeGoing(String bpmnElement) {
        List<Node> nodeList = document.selectNodes("//*[text()='" + bpmnElement + "']");
        for (Node node : nodeList) {
            node.getParent().remove(node);
        }
    }

    private void removeDi(String bpmnElement) {
        List<Node> nodeList = document.selectNodes("//*[@bpmnElement='" + bpmnElement + "']");
        for (Node node : nodeList) {
            node.getParent().remove(node);
        }
    }
}
