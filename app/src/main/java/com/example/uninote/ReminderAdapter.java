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

    private Context context;
    private List<Reminder> Reminders;

    public ReminderAdapter(Context context, List<Reminder> reminders) {
        this.context = context;
        this.Reminders = reminders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReminderAdapter.ViewHolder holder, int position) {
        Reminder reminder = Reminders.get(position);
        holder.bind(reminder);
    }

    @Override
    public int getItemCount() {
        return Reminders.size();
    }

    public void clear() {
        Reminders.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTitle;
        private TextView tvDate;
        private TextView tvLocation;
        private TextView tvTime;
        //private ImageView ivImage;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            //ivImage = itemView.findViewById(R.id.ivImage);
            //itemView.setOnClickListener(this);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }

        public void bind(Reminder reminder) {
            tvTitle.setText(reminder.getTitle());
            tvDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(reminder.getDate()));
            tvTime.setText(new SimpleDateFormat("HH:mm").format(reminder.getDate()));

            final ParseGeoPoint location = reminder.getLocation();
            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses = new ArrayList<>();
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                //addresses = geocoder.getFromLocationName("Monterrey",1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.isEmpty()){
                tvLocation.setText(" ");
            }
            else tvLocation.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());

        }
    }
}
