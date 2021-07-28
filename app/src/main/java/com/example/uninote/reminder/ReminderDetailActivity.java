package com.example.uninote.reminder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.uninote.MainActivity;
import com.example.uninote.R;
import com.example.uninote.models.ButtonsReminder;
import com.example.uninote.models.Reminder;
import com.parse.ParseACL;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReminderDetailActivity extends ButtonsReminder {

    public static final String TAG = "ReminderActivity";
    private final Calendar calendar = Calendar.getInstance();
    private final int year = calendar.get(Calendar.YEAR);
    private final int month = calendar.get(Calendar.MONTH);
    private final int day = calendar.get(Calendar.DAY_OF_MONTH);
    private EditText etTitle;
    private EditText etInputDate;
    private EditText etInputHour;
    private EditText etInputUbication;
    private EditText etShareCode;
    private ImageButton btnDate;
    private ImageButton btnHour;
    private ImageButton btnUbication;
    private Button btnCreateReminder;
    private Button btnShare;
    private DatePickerDialog.OnDateSetListener setListener;
    private int hour, minutes;
    private Reminder reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        findViewById(R.id.shareReminder).setVisibility(View.VISIBLE);
        etTitle = findViewById(R.id.etInputTitle);
        etInputDate = findViewById(R.id.etInputDate);
        etInputHour = findViewById(R.id.etInputHour);
        etInputUbication = findViewById(R.id.etInputUbication);
        etShareCode = findViewById(R.id.etShareCodeReminder);
        btnDate = findViewById(R.id.btnDate);
        btnHour = findViewById(R.id.btnHour);
        btnUbication = findViewById(R.id.btnUbication);
        btnCreateReminder = findViewById(R.id.btnCreateReminder);
        btnShare = findViewById(R.id.btnLinkReminder);

        settingButtons(ReminderDetailActivity.this);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etShareCode.getText().toString().isEmpty()) {
                    Toast.makeText(ReminderDetailActivity.this, "Empty Code", Toast.LENGTH_SHORT).show();
                    return;
                }
                linkReminder(etShareCode.getText().toString());
            }
        });

        btnCreateReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                Date date = new Date();
                Geocoder geocoder = new Geocoder(ReminderDetailActivity.this);
                List<Address> addresses = new ArrayList<>();
                ParseGeoPoint location = new ParseGeoPoint();

                try {
                    addresses = geocoder.getFromLocationName(etInputUbication.getText().toString(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    date = new SimpleDateFormat("dd/MM/yyyy").parse(etInputDate.getText().toString());
                    final String[] parts = etInputHour.getText().toString().split(":");
                    date.setHours(Integer.parseInt(parts[0]));
                    date.setMinutes(Integer.parseInt(parts[1]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (!addresses.isEmpty()) {
                    location = new ParseGeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                }

                if (title.isEmpty()) {
                    Toast.makeText(ReminderDetailActivity.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (date == null) {
                    Toast.makeText(ReminderDetailActivity.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ParseUser currentUser = ParseUser.getCurrentUser();
                saveReminder(title, currentUser, date, location);
            }
        });
    }

    private void linkReminder(String code) {
        final ParseObject entity = new ParseObject("User_Reminder");
        entity.put("username", ParseUser.getCurrentUser());

        ParseQuery<Reminder> query = ParseQuery.getQuery("Reminder");
        query.getInBackground(code, (object, e) -> {
            if (e != null) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            entity.put("reminder", object);
            entity.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        Log.i(TAG, "Link was successfully!!");
                        return;
                    }
                    Log.e(TAG, "Error while saving 2", e);
                    Toast.makeText(ReminderDetailActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
            });
        });
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void saveReminder(String title, ParseUser currentUser, Date date, ParseGeoPoint location) {
        final Reminder reminder = new Reminder();
        reminder.setTitle(title);
        reminder.setDate(date);
        reminder.setLocation(location);
        reminder.setUser(currentUser);

        reminder.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    return;
                }
                Log.e(TAG, "Error while saving", e);
                Toast.makeText(ReminderDetailActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
            }
        });

        final ParseObject entity = new ParseObject("User_Reminder");
        final ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
        parseACL.setPublicReadAccess(true);
        ParseUser.getCurrentUser().setACL(parseACL);

        entity.put("username", currentUser);
        entity.put("reminder", reminder);

        entity.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Log.i(TAG, "Post save was succesful!!");
                    return;
                }
                Log.e(TAG, "Error while saving 2", e);
                Toast.makeText(ReminderDetailActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
            }
        });

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}