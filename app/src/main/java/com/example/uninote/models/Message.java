package com.example.uninote.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {

    public static final String KEY_CONTENT = "content";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_PROJECT = "project";

    public String getContent() {
        return getString(KEY_CONTENT);
    }

    public void setContent(String content) {
        put(KEY_CONTENT, content);
    }

    public ParseUser getSender() {
        return getParseUser(KEY_SENDER);
    }

    public void setSender(ParseUser parseUser) {
        put(KEY_SENDER, parseUser);
    }

    public ParseObject getProject() {
        return getParseObject(KEY_PROJECT);
    }

    public void setProject(Project project) {
        put(KEY_PROJECT, project);
    }
}
