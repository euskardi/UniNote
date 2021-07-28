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
import com.example.uninote.ProjectActivity;
import com.example.uninote.R;
import com.example.uninote.models.ButtonsReminder;
import com.example.uninote.models.Project;
import com.example.uninote.models.Reminder;
import com.parse.ParseACL;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReminderDetailProject extends ButtonsReminder {

    public static final String TAG = "ReminderActivity";
    private final Calendar calendar = Calendar.getInstance();
    private final int year = calendar.get(Calendar.YEAR);
    private final int month = calendar.get(Calendar.MONTH);
    private final int day = calendar.get(Calendar.DAY_OF_MONTH);
    private EditText etTitle;
    private EditText etInputDate;
    private EditText etInputHour;
    private EditText etInputUbication;
    private ImageButton btnDate;
    private ImageButton btnHour;
    private ImageButton btnUbication;
    private Button btnCreateReminder;
    private DatePickerDialog.OnDateSetListener setListener;
    private int hour, minutes;
    private Reminder reminder;
    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        etTitle = findViewById(R.id.etInputTitle);
        etInputDate = findViewById(R.id.etInputDate);
        etInputHour = findViewById(R.id.etInputHour);
        etInputUbication = findViewById(R.id.etInputUbication);
        btnDate = findViewById(R.id.btnDate);
        btnHour = findViewById(R.id.btnHour);
        btnUbication = findViewById(R.id.btnUbication);
        btnCreateReminder = findViewById(R.id.btnCreateReminder);

        project = Parcels.unwrap(getIntent().getParcelableExtra(Project.class.getSimpleName()));

        settingButtons(ReminderDetailProject.this);

        btnCreateReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                Date date = new Date();
                Geocoder geocoder = new Geocoder(ReminderDetailProject.this);
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
                    Toast.makeText(ReminderDetailProject.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (date == null) {
                    Toast.makeText(ReminderDetailProject.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ParseUser currentUser = ParseUser.getCurrentUser();
                saveReminder(title, currentUser, date, location);
            }
        });
    }

    private void saveReminder(String title, ParseUser currentUser, Date date, ParseGeoPoint location) {
        final Reminder reminder = new Reminder();
        reminder.setTitle(title);
        reminder.setDate(date);
        reminder.setLocation(location);
        reminder.setUser(currentUser);
        reminder.setProject(project);

        reminder.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    return;
                }
                Log.e(TAG, "Error while saving", e);
                Toast.makeText(ReminderDetailProject.this, "Error while saving", Toast.LENGTH_SHORT).show();
            }
        });

        final Intent intentProject = new Intent(this, ProjectActivity.class);
        intentProject.putExtra(Project.class.getSimpleName(), Parcels.wrap(project));
        this.startActivity(intentProject);
        finish();
    }
}