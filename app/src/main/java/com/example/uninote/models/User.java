package com.example.uninote.models;

public class User {
     private String username;
     private String password;
     private String profileUrl;


    public User(String username, String password, String profilePicture) {
        this.username = username;
        this.password = password;
        this.profileUrl = profilePicture;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

}
