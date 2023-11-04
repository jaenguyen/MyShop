package com.ic.myshop.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Product implements Serializable {

    private String id;
    private String name;
    private String description;
    private long price;
    private int sellNumber;
    private int soldNumber;
    private String type;
    private String imageUrl;
    private long createdTime;
    private long updatedTime;
    private String parentId;

    public Product() {

    }

    public Product(String name, String description, long price, int sellNumber, String type, String imageUrl, String parentId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sellNumber = sellNumber;
        this.type = type;
        this.imageUrl = imageUrl;
        this.soldNumber = 0;
        this.createdTime = System.currentTimeMillis();
        this.updatedTime = System.currentTimeMillis();
        this.parentId = parentId;
    }

    public Product(String name, String description, long price, int sellNumber, int soldNumber, String type, String imageUrl, long createdTime, long updatedTime, String parentId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sellNumber = sellNumber;
        this.soldNumber = soldNumber;
        this.type = type;
        this.imageUrl = imageUrl;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getSellNumber() {
        return sellNumber;
    }

    public void setSellNumber(int sellNumber) {
        this.sellNumber = sellNumber;
    }

    public int getSoldNumber() {
        return soldNumber;
    }

    public void setSoldNumber(int soldNumber) {
        this.soldNumber = soldNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
