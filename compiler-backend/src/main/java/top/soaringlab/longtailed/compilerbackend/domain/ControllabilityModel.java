package top.soaringlab.longtailed.compilerbackend.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ControllabilityModel {

    @Id
    @GeneratedValue
    private Long id;

    private String processId = "";

    private String bpmnStart = "";

    @Column(columnDefinition = "text")
    private String bpmnModel = "";

    @Column(columnDefinition = "text")
    private String bpmnVariableDefinitionList = "";

    @Column(columnDefinition = "text")
    private String bpmnVariableConditionList = "";

    @Column(columnDefinition = "text")
    private String bpmnVariableModificationList = "";

    @Column(columnDefinition = "text")
    private String bpmnKpiList = "";

    @Column(columnDefinition = "text")
    private String verificationModel = "";

    @Column(columnDefinition = "text")
    private String verificationProperty = "";

    @Column(columnDefinition = "text")
    private String verificationOutput = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getBpmnStart() {
        return bpmnStart;
    }

    public void setBpmnStart(String bpmnStart) {
        this.bpmnStart = bpmnStart;
    }

    public String getBpmnModel() {
        return bpmnModel;
    }

    public void setBpmnModel(String bpmnModel) {
        this.bpmnModel = bpmnModel;
    }

    public String getBpmnVariableDefinitionList() {
        return bpmnVariableDefinitionList;
    }

    public void setBpmnVariableDefinitionList(String bpmnVariableDefinitionList) {
        this.bpmnVariableDefinitionList = bpmnVariableDefinitionList;
    }

    public String getBpmnVariableConditionList() {
        return bpmnVariableConditionList;
    }

    public void setBpmnVariableConditionList(String bpmnVariableConditionList) {
        this.bpmnVariableConditionList = bpmnVariableConditionList;
    }

    public String getBpmnVariableModificationList() {
        return bpmnVariableModificationList;
    }

    public void setBpmnVariableModificationList(String bpmnVariableModificationList) {
        this.bpmnVariableModificationList = bpmnVariableModificationList;
    }

    public String getBpmnKpiList() {
        return bpmnKpiList;
    }

    public void setBpmnKpiList(String bpmnKpiList) {
        this.bpmnKpiList = bpmnKpiList;
    }

    public String getVerificationModel() {
        return verificationModel;
    }

    public void setVerificationModel(String verificationModel) {
        this.verificationModel = verificationModel;
    }

    public String getVerificationProperty() {
        return verificationProperty;
    }

    public void setVerificationProperty(String verificationProperty) {
        this.verificationProperty = verificationProperty;
    }

    public String getVerificationOutput() {
        return verificationOutput;
    }

    public void setVerificationOutput(String verificationOutput) {
        this.verificationOutput = verificationOutput;
    }
}
