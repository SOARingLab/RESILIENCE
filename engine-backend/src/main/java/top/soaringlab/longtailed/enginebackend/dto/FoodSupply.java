package top.soaringlab.longtailed.enginebackend.dto;

public class FoodSupply {

    private Long id;

    private String supplier;

    private String community;

    private String item;

    private Integer amount;

    public FoodSupply() {
    }

    public FoodSupply(String supplier, String community, String item, Integer amount) {
        this.supplier = supplier;
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

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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
