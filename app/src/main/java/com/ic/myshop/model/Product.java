package com.ic.myshop.model;

import java.io.Serializable;

public class Product {

    private String imageUrl;
    private String name;
    private String parentId;
    private long createdTime;

    public Product() {

    }

    public Product(String imageUrl, String name, String parentId, long createdTime) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.parentId = parentId;
        this.createdTime = createdTime;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
