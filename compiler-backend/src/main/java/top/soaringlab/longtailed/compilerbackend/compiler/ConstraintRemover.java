package top.soaringlab.longtailed.compilerbackend.compiler;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import java.util.List;

public class ConstraintRemover {

    private Document document;

    public String constraintRemoveAll(String file) throws Exception {
        readBpmnXml(file);
        removeAttribute("constraint:declarative");
        removeAttribute("constraint:temporal");
        return writeBpmnXml();
    }

    public String constraintRemoveFunctional(String file) throws Exception {
        readBpmnXml(file);
        removeAttribute("constraint:declarative");
        return writeBpmnXml();
    }

    public String constraintRemoveNonFunctional(String file) throws Exception {
        readBpmnXml(file);
        removeAttribute("constraint:temporal");
        return writeBpmnXml();
    }

    private void readBpmnXml(String bpmnXml) throws Exception {
        document = DocumentHelper.parseText(bpmnXml);
        document.getRootElement().addNamespace("constraint", "http://some-company/schema/bpmn/constraint");
    }

    private String writeBpmnXml() {
        return document.asXML();
    }

    private void removeAttribute(String attribute) {
        List<Node> nodeList = document.selectNodes("//*[@" + attribute + "]");
        for (Node node : nodeList) {
            String localName = node.valueOf("local-name()");
            String id = node.valueOf("@id");
            String value = node.valueOf("@" + attribute);
            if (localName.equals("sequenceFlow") || localName.equals("messageFlow")) {
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
