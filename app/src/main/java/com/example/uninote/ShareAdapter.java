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
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {

    private final Context context;
    private final List<ParseUser> users;

    public ShareAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ShareAdapter.ViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvName;
        private final ImageView ivImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivImage = itemView.findViewById(R.id.ivProfileFriend);
            itemView.setOnClickListener(this);
        }

        public void bind(ParseUser parseUser) {
            tvName.setText(parseUser.getUsername());
            final ParseFile image = parseUser.getParseFile("picture");
            if (image != null) {
                Glide.with(context).load(parseUser.getParseFile("picture").getUrl()).into(ivImage);
            }
        }

        @Override
        public void onClick(View v) {

        }
    }
}
