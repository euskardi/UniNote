package com.example.uninote.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ToDo")
public class ToDo extends ParseObject {

    public static final String KEY_TITLE = "Title";
    public static final String KEY_CONTENT = "Content";
    public static final String KEY_USER = "Username";
    public static final String KEY_IMAGE = "Photo";
    public static final String KEY_PROJECT = "Project";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getContent() {
        return getString(KEY_CONTENT);
    }

    public void setContent(String content) {
        put(KEY_CONTENT, content);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseObject getProject() {
        return getParseObject(KEY_PROJECT);
    }

    public void setProject(Project project) {
        put(KEY_PROJECT, project);
    }
}

