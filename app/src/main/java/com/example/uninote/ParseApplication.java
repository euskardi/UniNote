package com.example.uninote;

import android.app.Application;

import com.example.uninote.models.Reminder;
import com.example.uninote.models.ToDo;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(ToDo.class);
        ParseObject.registerSubclass(Reminder.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("PTklUxQ065JbONoFbXYEkQPa6BsWjyYUuqxIWfC3")
                .clientKey("p2jAsHWOD5Nt9II8vZQW02C11EFNdLqbijZRanel")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
