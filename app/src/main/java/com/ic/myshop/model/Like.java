package com.ic.myshop.model;

import java.io.Serializable;

public class Like implements Serializable {

    private String userId;
    private String productId;
    private long timestamp;

    public Like() {

    }

    public Like(String userId, String productId, long timestamp) {
        this.userId = userId;
        this.productId = productId;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
