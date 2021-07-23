package com.example.uninote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.uninote.models.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "Chat";
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvMessages;
    private LinearLayout mLayoutManager;
    private MessageAdapter adapter;
    private List<Message> allMessages;
    private ParseUser message;
    private EditText etMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etInputMessage);
        btnSend = findViewById(R.id.btnSend);
        allMessages = new ArrayList<>();
        adapter = new MessageAdapter(this, allMessages);
        rvMessages.setAdapter(adapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

        message = Parcels.unwrap(getIntent().getParcelableExtra(ParseUser.class.getSimpleName()));

        queryMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        final Message newMessage = new Message();
        newMessage.setContent(etMessage.getText().toString());
        newMessage.setSender(ParseUser.getCurrentUser());
        newMessage.setRecipient(message);

        allMessages.add(newMessage);
        adapter.notifyItemChanged(allMessages.size() - 1);
        etMessage.setText("");

        newMessage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    adapter.notifyDataSetChanged();
                    return;
                }
                Log.e(TAG, "Error while saving", e);
                Toast.makeText(ChatActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void queryMessages() {

        final ParseQuery<Message> conditionOne = ParseQuery.getQuery("Message");
        conditionOne.whereEqualTo("sender", ParseUser.getCurrentUser());
        conditionOne.whereEqualTo("recipient", message);

        final ParseQuery<Message> conditionTwo = ParseQuery.getQuery("Message");
        conditionTwo.whereEqualTo("sender", message);
        conditionTwo.whereEqualTo("recipient", ParseUser.getCurrentUser());

        final List<ParseQuery<Message>> queries = new ArrayList<ParseQuery<Message>>();
        queries.add(conditionOne);
        queries.add(conditionTwo);

        final ParseQuery<Message> mainQuery = ParseQuery.or(queries);

        mainQuery.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                for (Message message : objects) {
                    Log.i(TAG, "Message:" + message.getContent());
                }
                allMessages.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }
}