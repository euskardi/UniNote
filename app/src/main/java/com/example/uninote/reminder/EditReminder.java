package com.example.uninote.reminder;

import androidx.annotation.NonNull;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
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
import com.example.uninote.models.ReminderFirebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
import java.util.HashMap;
import java.util.List;

public class EditReminder extends ButtonsReminder {

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
    private ReminderFirebase reminder;
    private final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();

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

        reminder = getIntent().getParcelableExtra(ReminderFirebase.class.getSimpleName());

        btnCreateReminder.setText("EDIT");
        etTitle.setText(reminder.getTitle());

        final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        Date date = new Date();

        try {
            date = ISO_8601_FORMAT.parse(reminder.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        etInputDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(date));
        etInputHour.setText(new SimpleDateFormat("HH:mm").format(date));
        try {
            final List<Address> addresses = new Geocoder(this).getFromLocation(reminder.getLatitude(), reminder.getLongitude(), 1);
            if (!addresses.isEmpty())
                etInputUbication.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());

        } catch (IOException e) {
            e.printStackTrace();
        }

        settingButtons(EditReminder.this);

        btnCreateReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                final Geocoder geocoder = new Geocoder(EditReminder.this);
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
                    Toast.makeText(EditReminder.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (date == null) {
                    Toast.makeText(EditReminder.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateReminder(reminder, title, date, addresses, EditReminder.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                final Intent intent = new Intent(this, ShareContent.class);
                intent.putExtra(Reminder.class.getSimpleName(), Parcels.wrap(reminder));
                startActivity(intent);
                finish();
                return true;

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

    private void deleteReminder(ReminderFirebase reminder) {
        rootDatabase.child("Reminders").child(reminder.getId()).removeValue();

        final Query innerQuery = FirebaseDatabase.getInstance().getReference("UserHasReminder")
                .orderByChild("reminder")
                .equalTo(reminder.getId());

        innerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final String key = dataSnapshot.getKey();
                    rootDatabase.child("UserHasReminder").child(key).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditReminder.this, "Error In Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}