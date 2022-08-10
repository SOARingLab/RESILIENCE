package top.soaringlab.longtailed.compilerbackend.domain;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PublicApi {

    @Id
    @GeneratedValue
    private Long id;

    private String processId = "";

    private String name = "";

    private String method = "";

    private String url = "";

    @ElementCollection
    private List<String> inputFroms = new ArrayList<>();

    @ElementCollection
    private List<String> inputTos = new ArrayList<>();

    @ElementCollection
    private List<String> outputFroms = new ArrayList<>();

    @ElementCollection
    private List<String> outputTos = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getInputFroms() {
        return inputFroms;
    }

    public void setInputFroms(List<String> inputFroms) {
        this.inputFroms = inputFroms;
    }

    public List<String> getInputTos() {
        return inputTos;
    }

    public void setInputTos(List<String> inputTos) {
        this.inputTos = inputTos;
    }

    public List<String> getOutputFroms() {
        return outputFroms;
    }

    public void setOutputFroms(List<String> outputFroms) {
        this.outputFroms = outputFroms;
    }

    public List<String> getOutputTos() {
        return outputTos;
    }

    public void setOutputTos(List<String> outputTos) {
        this.outputTos = outputTos;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
