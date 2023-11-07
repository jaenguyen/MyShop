package com.ic.myshop.output;

import com.ic.myshop.model.Address;
import com.ic.myshop.model.Order;

import java.io.Serializable;

public class OrderOutput implements Serializable {

    private String id;
    private String productId;
    private int quantity;
    private long price;
    private long totalPrice;
    private String parentId;
    private String sellerId;
    private long createdTime;
    private long updatedTime;
    private Address address;
    private int status;
    private String imageUrl;

    private String nameProduct;

    public OrderOutput() {

    }

    public OrderOutput(Order order, String imageUrl, String nameProduct) {
        this.id = order.getId();
        this.productId = order.getProductId();
        this.quantity = order.getQuantity();
        this.price = order.getPrice();
        this.totalPrice = order.getTotalPrice();
        this.parentId = order.getParentId();
        this.sellerId = order.getSellerId();
        this.createdTime = order.getCreatedTime();
        this.updatedTime = order.getUpdatedTime();
        this.address = order.getAddress();
        this.status = order.getStatus();
        this.imageUrl = imageUrl;
        this.nameProduct = nameProduct;
    }

    public OrderOutput(String productId, int quantity, long price, long totalPrice, String parentId, String sellerId, Address address) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
        this.parentId = parentId;
        this.sellerId = sellerId;
        status = 0;
        createdTime = System.currentTimeMillis();
        updatedTime = System.currentTimeMillis();
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }
}
