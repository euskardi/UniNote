package com.example.uninote;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uninote.models.ProjectFirebase;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private final Context context;
    private final List<ProjectFirebase> projects;

    public ProjectAdapter(Context context, List<ProjectFirebase> projects) {
        this.context = context;
        this.projects = projects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_project, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProjectAdapter.ViewHolder holder, int position) {
        holder.bind(projects.get(position));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public void clear() {
        projects.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvReminders;
        private final TextView tvToDos;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvProject);
            tvDescription = itemView.findViewById(R.id.tvDescriptionProject);
            tvReminders = itemView.findViewById(R.id.tvCountReminders);
            tvToDos = itemView.findViewById(R.id.tvCountToDos);
            itemView.setOnClickListener(this);
        }

        public void bind(ProjectFirebase project) {
            tvTitle.setText(project.getName());
            tvDescription.setText(project.getDescription());
            tvReminders.setText("Reminders: " + project.getCountReminders());//project.getCountReminders());
            tvToDos.setText("ToDos: " + project.getCountTodos());//project.getCountTodos());
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }

            final ProjectFirebase project = projects.get(position);
            final Intent intent = new Intent(context, ProjectActivity.class);
            intent.putExtra(ProjectFirebase.class.getSimpleName(), project);
            context.startActivity(intent);
        }
    }
}
