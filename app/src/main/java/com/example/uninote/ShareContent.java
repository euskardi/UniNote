package com.example.uninote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uninote.models.Reminder;
import com.example.uninote.models.ToDo;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareContent extends AppCompatActivity {

    public static final String TAG = "ShareContent";
    private final ArrayList<String> allUsers = new ArrayList<>();
    private final Map<String, ParseUser> allParseUsers = new HashMap<String, ParseUser>();
    private AutoCompleteTextView autoCompleteTextView;
    private TextView tvShareCode;
    private TextView tvUserName;
    private ImageButton btnAddUser;
    private ArrayAdapter<String> adapter;
    private ParseUser userInfo;
    private Reminder reminder;
    private ToDo toDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_content);
        autoCompleteTextView = findViewById(R.id.acProfileSearch);
        tvShareCode = findViewById(R.id.tvShareCode);
        tvUserName = findViewById(R.id.userId);
        btnAddUser = findViewById(R.id.btnAddUser);

        toDo = Parcels.unwrap(getIntent().getParcelableExtra(ToDo.class.getSimpleName()));
        reminder = Parcels.unwrap(getIntent().getParcelableExtra(Reminder.class.getSimpleName()));
        if (toDo == null) {
            tvShareCode.setText(reminder.getObjectId());
        } else {
            tvShareCode.setText(toDo.getObjectId());
        }

        gettingUsers();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, allUsers);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvUserName.setText(adapter.getItem(position));
                userInfo = allParseUsers.get(adapter.getItem(position));
            }
        });

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
    }

    private void addUser() {
        if (userInfo == null) {
            Toast.makeText(this, "User can'' be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        final ParseObject entity = entityType();
        entity.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(ShareContent.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(ShareContent.this, "File Added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ParseObject entityType() {
        if (toDo == null) {
            final ParseObject entity = new ParseObject("User_Reminder");
            entity.put("reminder", reminder);
            entity.put("username", userInfo);
            return entity;
        }
        final ParseObject entity = new ParseObject("User_ToDo");
        entity.put("toDo", toDo);
        entity.put("username", userInfo);
        return entity;
    }

    private void gettingUsers() {
        final ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereExists("objectId");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                for (ParseUser user : users) {
                    Log.i(TAG, "ToDo is good " + user.getUsername());
                    allUsers.add(user.getUsername());
                    allParseUsers.put(user.getUsername(), user);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}