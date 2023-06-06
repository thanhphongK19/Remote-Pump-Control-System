package com.example.iukhinmybm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> mlistTask;
    private Context mContext;
    private IClickItemTask iClickItemTask;

    public interface IClickItemTask{
        void deleteTask(Task task);
    }

    public TaskAdapter(IClickItemTask iClickItemTask){
        this.iClickItemTask = iClickItemTask;

    }
    public void setData(List<Task> list){
        this.mlistTask = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm,parent,false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = mlistTask.get(position);
        if(task == null){
            return;
        }
        holder.textTime.setText(task.getTime());
        holder.textDate.setText(task.getDate());
        holder.timePump.setText(task.getTimePump());


        holder.deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iClickItemTask.deleteTask(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mlistTask != null){
            return mlistTask.size();
        }
        return 0;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder{
        private TextView textTime;
        private TextView textDate;
        private TextView timePump;
        private ImageView deleteTask;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            textTime = itemView.findViewById(R.id.textTime);
            textDate = itemView.findViewById(R.id.textDate);
            timePump = itemView.findViewById(R.id.timePump);
            deleteTask = itemView.findViewById(R.id.deleteTask);
        }
    }
}
