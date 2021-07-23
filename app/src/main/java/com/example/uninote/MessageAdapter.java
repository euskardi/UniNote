package com.example.uninote;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uninote.models.Message;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final String TAG = "RecyclerView";
    private final Context context;
    private final List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapter.ViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvMessage;
        private final CardView cdMessage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageInput);
            cdMessage = itemView.findViewById(R.id.cvMessage);
        }

        public void bind(Message message) {
            tvMessage.setText(message.getContent());
            try {
                if (!message.getSender().fetchIfNeeded().getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                    Log.i(TAG, "My: ");
                    cdMessage.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
