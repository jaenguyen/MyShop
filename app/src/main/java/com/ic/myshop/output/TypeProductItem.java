package com.ic.myshop.output;

public class TypeProductItem {

    private String name;
    private int resourceId;

    public TypeProductItem() {
    }

    public TypeProductItem(String name, int resourceId) {
        this.name = name;
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
