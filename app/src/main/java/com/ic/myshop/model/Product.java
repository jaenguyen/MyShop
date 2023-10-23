package com.ic.myshop.model;

import java.io.Serializable;

public class Product {

    private String imageUrl;
    private String name;
    private String parentId;

    public Product() {

    }

    public Product(String imageUrl, String name, String parentId) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.parentId = parentId;
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
}
