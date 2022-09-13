package top.soaringlab.longtailed.compilerbackend.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionalVerificationResult {

    private Boolean result = false;

    private List<Map<String, String>> detail = new ArrayList<>();

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public List<Map<String, String>> getDetail() {
        return detail;
    }

    public void setDetail(List<Map<String, String>> detail) {
        this.detail = detail;
    }
}
