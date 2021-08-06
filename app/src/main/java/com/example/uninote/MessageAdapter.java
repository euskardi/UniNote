package com.example.uninote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uninote.models.MessageFirebase;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final String TAG = "RecyclerView";
    private static final int MESSAGE_ME = 1;
    private static final int MESSAGE_OTHER = 0;
    private final Context context;
    private final List<MessageFirebase> messages;
    private final String firebaseAuth;

    public MessageAdapter(String firebaseAuth, Context context, List<MessageFirebase> messages) {
        this.firebaseAuth = firebaseAuth;
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        final String messageUsername = messages.get(position).getSender();
        if (messageUsername.equals(firebaseAuth)) {
            return MESSAGE_ME;
        }
        return MESSAGE_OTHER;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return viewType == MESSAGE_OTHER
                ? new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_other, parent, false))
                : new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_me, parent, false));
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
        private final TextView tvName;
        private final ImageView ivImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageInput);
            tvName = itemView.findViewById(R.id.tvMessageName);
            ivImage = itemView.findViewById(R.id.ivImageMessage);
        }

        public void bind(MessageFirebase message) {
            tvMessage.setText(message.getContent());
            tvName.setText(message.getUsername());

            if (message.getImage() != null) {
                Glide.with(context).load(message.getImage()).into(ivImage);
            }
        }
    }
}
