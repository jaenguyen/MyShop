package com.ic.myshop.model;

public class Statistics {

    private String orderId;
    private String parentId;
    private long price;
    // 0: -, 1: +
    private int type;
    private Long timestamp;

    public Statistics() {

    }

    public Statistics(String orderId, String parentId, long price, int type, long timestamp) {
        this.orderId = orderId;
        this.parentId = parentId;
        this.price = price;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
