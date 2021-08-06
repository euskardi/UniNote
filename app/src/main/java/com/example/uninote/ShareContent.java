package com.example.uninote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uninote.models.ReminderFirebase;
import com.example.uninote.models.UserFirebase;
import com.example.uninote.models.UserHasReminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShareContent extends AppCompatActivity {

    public static final String TAG = "ShareContent";
    private final ArrayList<String> allUsers = new ArrayList<>();
    private final Map<String, String> allParseUsers = new HashMap<String, String>();
    private AutoCompleteTextView autoCompleteTextView;
    private TextView tvShareCode;
    private TextView tvUserName;
    private ImageButton btnAddUser;
    private ArrayAdapter<String> adapter;
    private String userInfo;
    private ReminderFirebase reminder;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    final private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_content);
        autoCompleteTextView = findViewById(R.id.acProfileSearch);
        tvShareCode = findViewById(R.id.tvShareCode);
        tvUserName = findViewById(R.id.userId);
        btnAddUser = findViewById(R.id.btnAddUser);

        reminder = getIntent().getParcelableExtra(ReminderFirebase.class.getSimpleName());
        tvShareCode.setText(reminder.getId());

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
            Toast.makeText(this, "User can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        rootNode = FirebaseDatabase.getInstance();

        final UserHasReminder userHasReminder = new UserHasReminder(userInfo, reminder.getId());
        reference = rootNode.getReference("UserHasReminder");
        reference.child(UUID.randomUUID().toString()).setValue(userHasReminder);

        Toast.makeText(this, "User added", Toast.LENGTH_SHORT).show();


    }

    private void gettingUsers() {

        final Query query = FirebaseDatabase.getInstance().getReference("Users");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                for (DataSnapshot dataSnapshot : snapshot2.getChildren()) {
                    allUsers.add(dataSnapshot.getValue(UserFirebase.class).getUsername());
                    allParseUsers.put(dataSnapshot.getValue(UserFirebase.class).getUsername(), dataSnapshot.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShareContent.this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}