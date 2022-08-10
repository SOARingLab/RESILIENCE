package top.soaringlab.longtailed.enginebackend.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class MessageEvent {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String businessKey;

    public MessageEvent() {
    }

    public MessageEvent(String name, String businessKey) {
        this.name = name;
        this.businessKey = businessKey;
    }

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

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
}
