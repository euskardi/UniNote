package com.example.uninote.models;

public class UserHasReminder {
    private String user;
    private String reminder;

    public UserHasReminder(String user, String reminder) {
        this.user = user;
        this.reminder = reminder;
    }

    public UserHasReminder() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }
}