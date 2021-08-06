package com.example.uninote.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ProjectFirebase implements Parcelable {

    private String id;
    private String name;
    private String description;
    private String editor;
    private int countReminders;
    private int countTodos;

    public ProjectFirebase(String id, String name, String description, String editor, int countReminders, int countTodos) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.editor = editor;
        this.countReminders = countReminders;
        this.countTodos = countTodos;
    }

    public ProjectFirebase() {
    }

    protected ProjectFirebase(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        editor = in.readString();
        countReminders = in.readInt();
        countTodos = in.readInt();
    }

    public static final Creator<ProjectFirebase> CREATOR = new Creator<ProjectFirebase>() {
        @Override
        public ProjectFirebase createFromParcel(Parcel in) {
            return new ProjectFirebase(in);
        }

        @Override
        public ProjectFirebase[] newArray(int size) {
            return new ProjectFirebase[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public int getCountReminders() {
        return countReminders;
    }

    public void setCountReminders(int countReminders) {
        this.countReminders = countReminders;
    }

    public int getCountTodos() {
        return countTodos;
    }

    public void setCountTodos(int countTodos) {
        this.countTodos = countTodos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(editor);
        dest.writeInt(countReminders);
        dest.writeInt(countTodos);
    }
}
