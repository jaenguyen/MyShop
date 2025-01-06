package com.ic.myshop.model;

import java.io.Serializable;

public class Address implements Serializable {

    private String name;
    private String phone;
    private String street;

    public Address() {

    }

    public Address(String name, String phone, String street) {
        this.name = name;
        this.phone = phone;
        this.street = street;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
