package com.example.uninote;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.uninote.models.Reminder;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderDetailActivity extends AppCompatActivity {

    public static final String TAG = "ReminderActivity";
    private EditText etTitle;
    private EditText etInputDate;
    private EditText etInputHour;
    private EditText etInputUbication;
    private ImageButton btnDate;
    private ImageButton btnHour;
    private ImageButton btnUbication;
    private Button btnCreateReminder;
    private DatePickerDialog.OnDateSetListener setListener;
    private Calendar calendar = Calendar.getInstance();
    private final int year = calendar.get(Calendar.YEAR);
    private final int month = calendar.get(Calendar.MONTH);
    private final int day = calendar.get(Calendar.DAY_OF_MONTH);
    private int hour, minute;

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

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReminderDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        etInputDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        btnHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
                        hour = hourOfDay;
                        minute = minuteOfDay;
                        etInputHour.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(ReminderDetailActivity.this, onTimeSetListener, hour, minute, true);
                timePickerDialog.show();
            }
        });

        btnUbication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Geocoder geocoder = new Geocoder(ReminderDetailActivity.this);
                final List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocationName(etInputUbication.getText().toString(), 1);
                    if (addresses.isEmpty()) {
                        etInputUbication.setText("");
                        etInputUbication.setHint("Not Found");
                    } else
                        etInputUbication.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnCreateReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
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
                    date.setHours(hour);
                    date.setMinutes(minute);
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
                ParseUser currentUser = ParseUser.getCurrentUser();
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
        reminder.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(ReminderDetailActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was succesful!!");
                etTitle.setText("");
                etInputDate.setText("");
                etInputHour.setText("");
                etInputUbication.setText("");
            }
        });
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}