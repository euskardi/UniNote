package com.example.uninote.models;

public class MessageFirebase {

    private String image;
    private String sender;
    private String content;
    private String project;
    private String username;

    public MessageFirebase() {
    }

    public MessageFirebase(String image, String sender, String content, String project) {
        this.image = image;
        this.sender = sender;
        this.content = content;
        this.project = project;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
