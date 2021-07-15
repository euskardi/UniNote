package com.example.uninote;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.parse.ParseGeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReminderDetailActivity extends AppCompatActivity {

    public static final String TAG = "ReminderActivity";
    private EditText etInputDate;
    private EditText etInputHour;
    private EditText etInputUbication;
    private ImageButton btnDate;
    private ImageButton btnHour;
    private ImageButton btnUbication;
    DatePickerDialog.OnDateSetListener setListener;
    Calendar calendar = Calendar.getInstance();
    final int year = calendar.get(Calendar.YEAR);
    final int month = calendar.get(Calendar.MONTH);
    final int day = calendar.get(Calendar.DAY_OF_MONTH);
    private int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        etInputDate = findViewById(R.id.etInputDate);
        etInputHour = findViewById(R.id.etInputHour);
        etInputUbication = findViewById(R.id.etInputUbication);
        btnDate = findViewById(R.id.btnDate);
        btnHour = findViewById(R.id.btnHour);
        btnUbication = findViewById(R.id.btnUbication);

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

        btnUbication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Geocoder geocoder = new Geocoder(ReminderDetailActivity.this);
                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocationName(etInputUbication.getText().toString(),1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.isEmpty()){
                    etInputUbication.setText("");
                    etInputUbication.setHint("Not Found");
                }
                else etInputUbication.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
            }
        });
    }

    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
                hour = hourOfDay;
                minute = minuteOfDay;
                etInputHour.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.show();
    }
}