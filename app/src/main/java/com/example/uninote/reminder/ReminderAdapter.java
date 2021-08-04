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
import com.example.uninote.models.ProjectFirebase;
import com.example.uninote.models.Reminder;
import com.example.uninote.models.ReminderFirebase;
import com.parse.ParseGeoPoint;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private final Context context;
    private final List<ReminderFirebase> Reminders;
    private final boolean click;
    private ProjectFirebase project;

    public ReminderAdapter(Context context, List<ReminderFirebase> Reminders, boolean click) {
        this.context = context;
        this.Reminders = Reminders;
        this.click = click;
    }

    public ReminderAdapter(Context context, List<ReminderFirebase> Reminders, boolean click, ProjectFirebase project) {
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

        public void bind(ReminderFirebase reminder) {
            tvTitle.setText(reminder.getTitle());
            final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
            Date date = new Date();

            try {
                date = ISO_8601_FORMAT.parse(reminder.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(date));
            tvTime.setText(new SimpleDateFormat("HH:mm").format(date));

            try {
                final List<Address> addresses = new Geocoder(context).getFromLocation(reminder.getLatitude(), reminder.getLongitude(), 1);
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
            final ReminderFirebase reminder = Reminders.get(position);
            if (project == null) {
                final Intent intentProject = new Intent(context, EditReminder.class);
                intentProject.putExtra(ReminderFirebase.class.getSimpleName(), reminder);
                context.startActivity(intentProject);
                return;
            }
            final Intent intent = new Intent(context, EditReminderProject.class);
            intent.putExtra(ReminderFirebase.class.getSimpleName(), reminder);
            intent.putExtra(ProjectFirebase.class.getSimpleName(), project);
            context.startActivity(intent);
        }
    }
}
