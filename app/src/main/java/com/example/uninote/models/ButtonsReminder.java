package com.example.uninote.models;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uninote.ProjectActivity;
import com.example.uninote.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ButtonsReminder extends AppCompatActivity {
    public static final String TAG = "ReminderActivity";
    private final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();
    private EditText etTitle;
    private EditText etInputDate;
    private EditText etInputHour;
    private EditText etInputUbication;
    private ImageButton btnDate;
    private ImageButton btnHour;
    private ImageButton btnUbication;
    private Button btnCreateReminder;
    private DatePickerDialog.OnDateSetListener setListener;
    private final Calendar calendar = Calendar.getInstance();
    private final int year = calendar.get(Calendar.YEAR);
    private final int month = calendar.get(Calendar.MONTH);
    private final int day = calendar.get(Calendar.DAY_OF_MONTH);
    private final int hour = calendar.get(Calendar.HOUR);
    private final int minutes = calendar.get(Calendar.MINUTE);

    public void settingButtons(Context context) {
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
                final DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        etInputDate.setText(day + "/" + month + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        btnHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        etInputHour.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, year, minutes, true);
                timePickerDialog.show();
            }
        });

        btnUbication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Geocoder geocoder = new Geocoder(context);
                final List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocationName(etInputUbication.getText().toString(), 1);
                    etInputUbication.setText("");
                    etInputUbication.setHint("Not Found");
                    if (!addresses.isEmpty())
                        etInputUbication.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateReminder(ReminderFirebase reminder, String title, Date date, List<Address> addresses, Context context) {
        final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

        final HashMap hashMap = new HashMap();
        hashMap.put("title", title);
        hashMap.put("date", ISO_8601_FORMAT.format(date));

        if (!addresses.isEmpty()) {
            hashMap.put("latitude", addresses.get(0).getLatitude());
            hashMap.put("longitude", addresses.get(0).getLongitude());
        } else {
            hashMap.put("latitude", 0);
            hashMap.put("longitude", 0);
        }

        final Query innerQuery = FirebaseDatabase.getInstance().getReference("Reminders")
                .orderByChild("title")
                .equalTo(reminder.getTitle());

        innerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final String key = dataSnapshot.getKey();
                    rootDatabase.child("Reminders").child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(context, "Your data is Successfully Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error In Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateScore(Boolean type, ProjectFirebase project, Context context) {
        final Intent intentProject = new Intent(context, ProjectActivity.class);
        intentProject.putExtra(ProjectFirebase.class.getSimpleName(), project);

        final HashMap hashMap = new HashMap();
        final int count = type ? 1 : -1;
        project.setCountReminders(project.getCountReminders() + count);
        hashMap.put("countReminders", project.getCountReminders());

        final Query innerQuery = FirebaseDatabase.getInstance().getReference("Project")
                .orderByChild("name")
                .equalTo(project.getName());

        innerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final String key = dataSnapshot.getKey();
                    rootDatabase.child("Project").child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            startActivity(intentProject);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error In Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
