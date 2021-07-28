package com.example.uninote.reminder;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uninote.R;
import com.example.uninote.models.Project;
import com.example.uninote.models.Reminder;
import com.parse.ParseGeoPoint;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private final Context context;
    private final List<Reminder> Reminders;
    private final boolean click;
    private Project project;

    public ReminderAdapter(Context context, List<Reminder> Reminders, boolean click) {
        this.context = context;
        this.Reminders = Reminders;
        this.click = click;
    }

    public ReminderAdapter(Context context, List<Reminder> Reminders, boolean click, Project project) {
        this.context = context;
        this.Reminders = Reminders;
        this.click = click;
        this.project = project;
    }

    @NonNull
    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
            itemView.setOnClickListener(this);
        }

        public void bind(Reminder reminder) {
            tvTitle.setText(reminder.getTitle());
            tvDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(reminder.getDate()));
            tvTime.setText(new SimpleDateFormat("HH:mm").format(reminder.getDate()));

            final ParseGeoPoint location = reminder.getLocation();
            try {
                final List<Address> addresses = new Geocoder(context).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    tvLocation.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || !click) {
                return;
            }
            final Reminder reminder = Reminders.get(position);
            if (project == null) {
                final Intent intentProject = new Intent(context, EditReminder.class);
                intentProject.putExtra(Reminder.class.getSimpleName(), Parcels.wrap(reminder));
                context.startActivity(intentProject);
                return;
            }
            final Intent intent = new Intent(context, EditReminderProject.class);
            intent.putExtra(Reminder.class.getSimpleName(), Parcels.wrap(reminder));
            intent.putExtra(Project.class.getSimpleName(), Parcels.wrap(project));
            context.startActivity(intent);
        }
    }
}