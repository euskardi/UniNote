package com.example.uninote.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ReminderFirebase implements Parcelable {

    private String id;
    private String title;
    private String date;
    private String project;
    private double latitude;
    private double longitude;

    public ReminderFirebase() {

    }

    public ReminderFirebase(String id, String title, String date, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected ReminderFirebase(Parcel in) {
        id = in.readString();
        title = in.readString();
        date = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<ReminderFirebase> CREATOR = new Creator<ReminderFirebase>() {
        @Override
        public ReminderFirebase createFromParcel(Parcel in) {
            return new ReminderFirebase(in);
        }

        @Override
        public ReminderFirebase[] newArray(int size) {
            return new ReminderFirebase[size];
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
        dest.writeString(date);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
