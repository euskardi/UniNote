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

import androidx.annotation.NonNull;

import com.example.uninote.MainActivity;
import com.example.uninote.ProjectDetailActivity;
import com.example.uninote.R;
import com.example.uninote.models.ButtonsReminder;
import com.example.uninote.models.GeneratorId;
import com.example.uninote.models.ProjectFirebase;
import com.example.uninote.models.Reminder;
import com.example.uninote.models.ReminderFirebase;
import com.example.uninote.models.UserHasProject;
import com.example.uninote.models.UserHasReminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;


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

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    final private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


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
                saveReminder(title, date, location);
            }
        });
    }

    private void linkReminder(String code) {

        rootNode = FirebaseDatabase.getInstance();
        final UserHasReminder userHasReminder = new UserHasReminder();

        userHasReminder.setUser(firebaseAuth.getUid());

        final Query query = FirebaseDatabase.getInstance().getReference("Reminders")
                .orderByKey()
                .equalTo(code);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userHasReminder.setReminder(dataSnapshot.getValue(ReminderFirebase.class).getId());
                    reference = rootNode.getReference("UserHasReminder");
                    reference.child(UUID.randomUUID().toString()).setValue(userHasReminder);
                }
                startActivity(new Intent(ReminderDetailActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReminderDetailActivity.this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveReminder(String title, Date date, ParseGeoPoint location) {

        final String id = GeneratorId.get();
        final ReminderFirebase reminderFirebase = new ReminderFirebase();
        final UserHasReminder userHasReminder = new UserHasReminder(firebaseAuth.getUid(), id);
        final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

        reminderFirebase.setTitle(title);
        reminderFirebase.setDate(ISO_8601_FORMAT.format(date));
        reminderFirebase.setLatitude(location.getLatitude());
        reminderFirebase.setLongitude(location.getLongitude());
        reminderFirebase.setId(id);

        rootNode = FirebaseDatabase.getInstance();

        reference = rootNode.getReference("Reminders");
        reference.child(id).setValue(reminderFirebase);
        reference = rootNode.getReference("UserHasReminder");
        reference.child(UUID.randomUUID().toString()).setValue(userHasReminder);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
