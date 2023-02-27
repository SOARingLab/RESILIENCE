package top.soaringlab.longtailed.compilerbackend.dto;

import top.soaringlab.longtailed.compilerbackend.verifier.StnuHDCVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NonFunctionalVerificationResult {

    private Boolean result = false;

    private Map<String, StnuHDCVerifier.nonDcXmlId> detail = new HashMap<>();

    private ArrayList<Boolean> logicResult = new ArrayList<Boolean>();

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Map<String, StnuHDCVerifier.nonDcXmlId> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, StnuHDCVerifier.nonDcXmlId> detail) {
        this.detail = detail;
    }

    public ArrayList<Boolean> getLogicResult() {
        return logicResult;
    }

    public void setLogicResult(Boolean value) {
        this.logicResult.add(value);
    }

}
