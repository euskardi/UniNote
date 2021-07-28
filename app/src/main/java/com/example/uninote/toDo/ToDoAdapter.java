package com.example.uninote.toDo;

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
import com.example.uninote.R;
import com.example.uninote.models.Project;
import com.example.uninote.models.ToDo;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private final Context context;
    private final List<ToDo> toDos;
    private final boolean click;
    private Project project;

    public ToDoAdapter(Context context, List<ToDo> toDos, boolean click) {
        this.context = context;
        this.toDos = toDos;
        this.click = click;
    }

    public ToDoAdapter(Context context, List<ToDo> toDos, boolean click, Project project) {
        this.context = context;
        this.toDos = toDos;
        this.click = click;
        this.project = project;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ToDoAdapter.ViewHolder holder, int position) {
        holder.bind(toDos.get(position));
    }

    @Override
    public int getItemCount() {
        return toDos.size();
    }

    public void clear() {
        toDos.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvTitle;
        private final TextView tvDescription;
        private final ImageView ivImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvLocation);
            ivImage = itemView.findViewById(R.id.ivImage);
            itemView.setOnClickListener(this);
        }

        public void bind(ToDo toDo) {
            tvTitle.setText(toDo.getTitle());
            tvDescription.setText(toDo.getContent());
            final ParseFile image = toDo.getImage();
            if (image != null) {
                Glide.with(context).load(toDo.getImage().getUrl()).into(ivImage);
            } else ivImage.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION || !click) {
                return;
            }
            final ToDo toDo = toDos.get(position);
            if (project != null) {
                final Intent intentProject = new Intent(context, EditToDoProject.class);
                intentProject.putExtra(ToDo.class.getSimpleName(), Parcels.wrap(toDo));
                context.startActivity(intentProject);
                return;
            }
            final Intent intent = new Intent(context, EditToDo.class);
            intent.putExtra(ToDo.class.getSimpleName(), Parcels.wrap(toDo));
            context.startActivity(intent);
        }
    }
}
