package com.example.uninote.models;

public class UserHasProject {
    private String user;
    private String project;
    private Boolean view;

    public UserHasProject(String user, String project, Boolean view) {
        this.user = user;
        this.project = project;
        this.view = view;
    }

    public UserHasProject() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Boolean getView() {
        return view;
    }

    public void setView(Boolean view) {
        this.view = view;
    }
}
