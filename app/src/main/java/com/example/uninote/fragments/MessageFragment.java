package com.example.uninote.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.uninote.MessageAdapter;
import com.example.uninote.R;
import com.example.uninote.models.MessageFirebase;
import com.example.uninote.models.ProjectFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageFragment extends Fragment {

    public static final String TAG = "Chat";
    private final DatabaseReference rootDatabase = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvMessages;
    private LinearLayout mLayoutManager;
    private MessageAdapter adapter;
    private List<MessageFirebase> allMessages;
    private ParseUser message;
    private EditText etMessage;
    private Button btnSend;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;


    public MessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etInputMessage);
        btnSend = view.findViewById(R.id.btnSend);
        allMessages = new ArrayList<>();
        adapter = new MessageAdapter(firebaseAuth.getUid(), getContext(), allMessages);
        rvMessages.setAdapter(adapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        rootDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMessages.clear();
                queryMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Internet Connection Error", Toast.LENGTH_SHORT).show();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        final ProjectFirebase project = getArguments().getParcelable("code");
        final MessageFirebase newMessage = new MessageFirebase();
        newMessage.setContent(etMessage.getText().toString());
        newMessage.setSender(firebaseAuth.getUid());
        newMessage.setProject(project.getId());

        rootDatabase.child("Users").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newMessage.setUsername(snapshot.child("username").getValue().toString());
                newMessage.setImage(snapshot.child("image").getValue().toString());
                allMessages.add(newMessage);
                adapter.notifyItemChanged(allMessages.size() - 1);
                etMessage.setText("");
                uploadMessage(newMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadMessage(MessageFirebase newMessage) {
        final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Messages");
        reference.child(ISO_8601_FORMAT.format(new Date())).setValue(newMessage);
    }

    private void queryMessages() {
        final ProjectFirebase project = getArguments().getParcelable("code");
        final Query query = FirebaseDatabase.getInstance().getReference("Messages")
                .orderByChild("project")
                .equalTo(project.getId());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageFirebase messageFirebase = dataSnapshot.getValue(MessageFirebase.class);
                    allMessages.add(messageFirebase);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Internet Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
