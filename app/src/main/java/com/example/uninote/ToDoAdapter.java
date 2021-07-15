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
import com.example.uninote.models.ToDo;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private Context context;
    private List<ToDo> toDos;

    public ToDoAdapter(Context context, List<ToDo> toDos) {
        this.context = context;
        this.toDos = toDos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ToDoAdapter.ViewHolder holder, int position) {
        ToDo toDo = toDos.get(position);
        holder.bind(toDo);
    }

    @Override
    public int getItemCount() {
        return toDos.size();
    }

    public void clear() {
        toDos.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTitle;
        private TextView tvDescription;
        private ImageView ivImage;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvLocation);
            ivImage = itemView.findViewById(R.id.ivImage);
            //itemView.setOnClickListener(this);

        }

        public void bind(ToDo toDo) {
            tvTitle.setText(toDo.getTitle());
            //tvTime.setText(new SimpleDateFormat("MM/dd/yyyy").format(post.getUser().getCreatedAt()));
            tvDescription.setText(toDo.getContent());
            final ParseFile image = toDo.getImage();
            if (image != null){
                Glide.with(context).load(toDo.getImage().getUrl()).into(ivImage);
            }
            else ivImage.setVisibility(View.GONE);
        }
    }
}
