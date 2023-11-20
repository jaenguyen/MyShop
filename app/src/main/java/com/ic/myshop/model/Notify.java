package com.ic.myshop.model;

public class Notify {


    private String id;
    private String title;
    private String body;
    private long timestamp;
    private boolean unread;
    private String parentId;

    public Notify() {
    }

    public Notify(String id, String title, String body, long timestamp, boolean unread, String parentId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.unread = unread;
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
