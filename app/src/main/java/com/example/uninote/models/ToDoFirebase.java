package com.example.uninote.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ToDoFirebase implements Parcelable {

    private String id;
    private String title;
    private String description;
    private String url;
    private String project;

    public ToDoFirebase() {
    }

    public ToDoFirebase(String id, String title, String description, String url) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
    }

    protected ToDoFirebase(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        url = in.readString();
    }

    public static final Creator<ToDoFirebase> CREATOR = new Creator<ToDoFirebase>() {
        @Override
        public ToDoFirebase createFromParcel(Parcel in) {
            return new ToDoFirebase(in);
        }

        @Override
        public ToDoFirebase[] newArray(int size) {
            return new ToDoFirebase[size];
        }
    };

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
    }
}
