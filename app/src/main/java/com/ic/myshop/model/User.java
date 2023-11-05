package com.ic.myshop.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String id;
    private String email;
    private String password;
    private String phone;
    private List<Address> addresses;
    private String avatar;

    public User() {

    }

    public User(String id, String email, String password, String phone) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.addresses = new ArrayList<>();
        this.avatar = defaultAvatar;
    }

    public User(String id, String email, String password, String phone, List<Address> addresses) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.addresses = addresses;
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private final static String defaultAvatar = "https://firebasestorage.googleapis.com/v0/b/myshop-91fed.appspot.com/o/images%2Favatar%20null.jpg?alt=media&token=e3118488-166f-4681-8dcd-7fbc948402ab";
}
