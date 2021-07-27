package com.example.uninote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uninote.models.Project;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private final Context context;
    private final List<Project> projects;

    public ProjectAdapter(Context context, List<Project> projects) {
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

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvDescription;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvProject);
            tvDescription = itemView.findViewById(R.id.tvDescriptionProject);
        }

        public void bind(Project project) {
            tvTitle.setText(project.getTitle());
            tvDescription.setText(project.getDescription());
        }
    }
}
