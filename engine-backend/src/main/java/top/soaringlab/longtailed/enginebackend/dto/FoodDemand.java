package top.soaringlab.longtailed.enginebackend.dto;

public class FoodDemand {

    private Long id;

    private String demander;

    private String community;

    private String item;

    private Integer amount;

    public FoodDemand() {
    }

    public FoodDemand(String demander, String community, String item, Integer amount) {
        this.demander = demander;
        this.community = community;
        this.item = item;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDemander() {
        return demander;
    }

    public void setDemander(String demander) {
        this.demander = demander;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
