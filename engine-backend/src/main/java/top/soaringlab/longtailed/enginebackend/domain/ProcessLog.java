package top.soaringlab.longtailed.enginebackend.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ProcessLog {

    @Id
    @GeneratedValue
    private Long id;

    private String businessKey;

    private String message;

    public ProcessLog() {
    }

    public ProcessLog(String businessKey, String message) {
        this.businessKey = businessKey;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
