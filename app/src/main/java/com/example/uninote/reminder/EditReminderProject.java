package com.example.uninote.reminder;

import androidx.annotation.NonNull;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.uninote.MainActivity;
import com.example.uninote.R;
import com.example.uninote.ShareContent;
import com.example.uninote.models.ButtonsReminder;
import com.example.uninote.models.Reminder;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditReminderProject extends ButtonsReminder {

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
    private int hour, minutes;
    private Reminder reminder;

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

        reminder = Parcels.unwrap(getIntent().getParcelableExtra(Reminder.class.getSimpleName()));

        btnCreateReminder.setText("EDIT");
        etTitle.setText(reminder.getTitle());
        etInputDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(reminder.getDate()));
        etInputHour.setText(new SimpleDateFormat("HH:mm").format(reminder.getDate()));
        final ParseGeoPoint location = reminder.getLocation();
        try {
            final List<Address> addresses = new Geocoder(this).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty())
                etInputUbication.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());

        } catch (IOException e) {
            e.printStackTrace();
        }

        settingButtons(EditReminderProject.this);

        btnCreateReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                final Geocoder geocoder = new Geocoder(EditReminderProject.this);
                Date date = new Date();
                List<Address> addresses = new ArrayList<>();
                ParseGeoPoint location = new ParseGeoPoint();

                try {
                    addresses = geocoder.getFromLocationName(etInputUbication.getText().toString(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    date = new SimpleDateFormat("dd/MM/yyyy").parse(etInputDate.getText().toString());
                    String[] parts = etInputHour.getText().toString().split(":");
                    date.setHours(Integer.parseInt(parts[0]));
                    date.setMinutes(Integer.parseInt(parts[1]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (!addresses.isEmpty()) {
                    location = new ParseGeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                }

                if (title.isEmpty()) {
                    Toast.makeText(EditReminderProject.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (date == null) {
                    Toast.makeText(EditReminderProject.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ParseUser currentUser = ParseUser.getCurrentUser();
                updateReminder(title, date, addresses, currentUser, reminder);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteReminder(reminder);
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;

            case R.id.cancel:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteReminder(Reminder reminder) {
        final ParseQuery<ParseObject> queryReminder = ParseQuery.getQuery("Reminder");
        queryReminder.getInBackground(reminder.getObjectId(), (object, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            object.deleteInBackground(e2 -> {
                if (e2 == null) {
                    Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "Error: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void updateReminder(String title, Date date, List<Address> addresses, ParseUser currentUser, Reminder reminder) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Reminder");
        query.getInBackground(reminder.getObjectId());

        query.getInBackground(reminder.getObjectId(), (object, e) -> {
            object.put("Title", title);
            object.put("Day", date);
            if (!addresses.isEmpty()) {
                object.put("Location", new ParseGeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()));
            } else {
                object.put("Location", new ParseGeoPoint(0, 0));
            }
            object.put("Username", currentUser);
            object.saveInBackground();
        });
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
    }
}