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
import com.example.uninote.models.Message;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MESSAGE_ME = 1;
    private static final int MESSAGE_OTHER = 0;
    public static final String TAG = "RecyclerView";
    private final Context context;
    private final List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        try {
            final String messageUsername = messages.get(position).getSender().fetchIfNeeded().getUsername();
            if (messageUsername.equals(ParseUser.getCurrentUser().getUsername())) {
                return MESSAGE_ME;
            }
        } catch (ParseException e) {
            e.printStackTrace();
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

        public void bind(Message message) {
            tvMessage.setText(message.getContent());
            try {
                final String messageUsername = message.getSender().fetchIfNeeded().getUsername();
                tvName.setText(messageUsername);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            final ParseFile image = message.getSender().getParseFile("picture");
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
        }
    }
}
