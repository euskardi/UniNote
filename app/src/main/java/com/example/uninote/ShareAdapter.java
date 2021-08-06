package com.example.uninote;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uninote.models.ToDo;
import com.example.uninote.models.UserFirebase;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {

    private final Context context;
    private final List<UserFirebase> users;

    public ShareAdapter(Context context, List<UserFirebase> users) {
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

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final ImageView ivImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivImage = itemView.findViewById(R.id.ivProfileFriend);
        }

        public void bind(UserFirebase userFirebase) {
            tvName.setText(userFirebase.getUsername());
            if (userFirebase.getImage() != null) {
                Glide.with(context).load(userFirebase.getImage()).into(ivImage);
            }
        }
    }
}
