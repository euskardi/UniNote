package com.example.uninote.models;

public class UserHasToDo {
    private String user;
    private String toDo;

    public UserHasToDo(String user, String reminder) {
        this.user = user;
        this.toDo = reminder;
    }

    public UserHasToDo() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getToDo() {
        return toDo;
    }

    public void setToDo(String reminder) {
        this.toDo = reminder;
    }
}
