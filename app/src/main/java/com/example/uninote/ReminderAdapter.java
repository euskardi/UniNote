package com.example.uninote;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uninote.models.Reminder;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private final Context context;
    private List<Reminder> Reminders;

    public ReminderAdapter(Context context, List<Reminder> reminders) {
        this.context = context;
        this.Reminders = reminders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_reminder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReminderAdapter.ViewHolder holder, int position) {
        holder.bind(Reminders.get(position));
    }

    @Override
    public int getItemCount() {
        return Reminders.size();
    }

    public void clear() {
        Reminders.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvDate;
        private final TextView tvLocation;
        private final TextView tvTime;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }

        public void bind(Reminder reminder) {
            tvTitle.setText(reminder.getTitle());
            tvDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(reminder.getDate()));
            tvTime.setText(new SimpleDateFormat("HH:mm").format(reminder.getDate()));

            final ParseGeoPoint location = reminder.getLocation();
            if (location.getLatitude() == 0 && location.getLongitude() == 0) {
                tvLocation.setText(" ");
            } else {
                try {
                    final List<Address> addresses = new Geocoder(context).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    tvLocation.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
