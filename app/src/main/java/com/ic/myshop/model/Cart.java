package com.ic.myshop.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Cart implements Serializable {

    private String id;
    private String parentId;
    private Map<String, Integer> quantityProducts;

    public Cart() {

    }

    public Cart(String id, String parentId) {
        this.id = id;
        this.parentId = parentId;
        this.quantityProducts = new HashMap<>();
    }

    public Cart(String id, String parentId, Map<String, Integer> quantityProducts) {
        this.id = id;
        this.parentId = parentId;
        this.quantityProducts = quantityProducts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Map<String, Integer> getQuantityProducts() {
        return quantityProducts;
    }

    public void setQuantityProducts(Map<String, Integer> quantityProducts) {
        this.quantityProducts = quantityProducts;
    }
}
