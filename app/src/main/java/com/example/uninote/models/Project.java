package com.example.uninote.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Project")
public class Project extends ParseObject {

    public static final String KEY_TITLE = "Title";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_EDITOR = "Editor";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseUser getEditor() {
        return getParseUser(KEY_EDITOR);
    }

    public void setEditor(ParseUser parseUser) {
        put(KEY_EDITOR, parseUser);
    }

}
