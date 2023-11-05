package com.ic.myshop.output;

public class BuyItem {

    private String id;
    private String imageUrl;
    private String name;
    private long price;
    private int quantity;
    private String parentId;

    public BuyItem() {

    }

    public BuyItem(String id, String imageUrl, String name, long price, String parentId) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
