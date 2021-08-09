package com.inventorytracker.tasks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.inventorytracker.R;
import com.inventorytracker.tasks.data.UserTask;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

public class TaskAdapter extends FirestoreRecyclerAdapter<UserTask, TaskAdapter.menuTaskHolder> {

    public TaskAdapter(@NonNull FirestoreRecyclerOptions<UserTask> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull menuTaskHolder taskHolder, int i, @NonNull UserTask userTask) {
        taskHolder.taskContent.setText(normalizeSpace(userTask.getTaskContent()));
        taskHolder.taskTitle.setText(normalizeSpace(userTask.getTaskTitle()));
    }

    @NonNull
    public menuTaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new menuTaskHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task, parent, false));
    }

    static class menuTaskHolder extends RecyclerView.ViewHolder {
        private TextView taskTitle, taskContent;

        public menuTaskHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.menuTaskTitle);
            taskContent = itemView.findViewById(R.id.menuTaskContent);
        }
    }
}
