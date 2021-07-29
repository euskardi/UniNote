package com.example.uninote.fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.example.uninote.models.Message;
import com.example.uninote.models.Project;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    public static final String TAG = "Chat";
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvMessages;
    private LinearLayout mLayoutManager;
    private MessageAdapter adapter;
    private List<Message> allMessages;
    private ParseUser message;
    private EditText etMessage;
    private Button btnSend;


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
        adapter = new MessageAdapter(getContext(), allMessages);
        rvMessages.setAdapter(adapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        queryMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        final Project project = getArguments().getParcelable("code");
        final Message newMessage = new Message();
        newMessage.setContent(etMessage.getText().toString());
        newMessage.setSender(ParseUser.getCurrentUser());
        newMessage.setProject(project);

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
                Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void queryMessages() {
        final Project project = getArguments().getParcelable("code");
        final ParseQuery<Message> conditionOne = ParseQuery.getQuery("Message");
        conditionOne.whereEqualTo("project", project);

        conditionOne.findInBackground(new FindCallback<Message>() {
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
