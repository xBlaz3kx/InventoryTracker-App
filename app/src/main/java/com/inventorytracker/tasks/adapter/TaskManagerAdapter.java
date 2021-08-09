package com.inventorytracker.tasks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inventorytracker.R;
import com.inventorytracker.tasks.data.UserTask;

import java.util.ArrayList;

import static com.inventorytracker.utils.Constants.TASK_STATUS_FINISHED;
import static com.inventorytracker.utils.Constants.activeColor;
import static com.inventorytracker.utils.Constants.inactiveColor;
import static com.inventorytracker.utils.Constants.selectedColor;

public class TaskManagerAdapter extends FirestoreRecyclerAdapter<UserTask, TaskManagerAdapter.menuTaskHolder> {
    LongKeyPressedEventListener listener;

    public MutableLiveData<ArrayList<Integer>> selectedStatus = new MutableLiveData<>();

    public interface LongKeyPressedEventListener {
        void longKeyPressed(DocumentSnapshot doc, int position);
    }

    public void setListener(LongKeyPressedEventListener listener) {
        this.listener = listener;
    }

    public TaskManagerAdapter(@NonNull FirestoreRecyclerOptions<UserTask> options) {
        super(options);
        this.selectedStatus.setValue(new ArrayList<>());
    }

    @Override
    protected void onBindViewHolder(@NonNull TaskManagerAdapter.menuTaskHolder taskHolder, int i, @NonNull UserTask userTask) {
        taskHolder.taskContent.setText(userTask.getTaskContent());
        taskHolder.taskTitle.setText(userTask.getTaskTitle());
        if (userTask.getTaskStatus().equals(TASK_STATUS_FINISHED)) {
            taskHolder.taskCardView.setCardBackgroundColor(inactiveColor);
            taskHolder.itemView.setOnLongClickListener(null);//disable changing color
        }
    }

    @NonNull
    @Override
    public TaskManagerAdapter.menuTaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskManagerAdapter.menuTaskHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task, parent, false));
    }

    class menuTaskHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskContent;
        CardView taskCardView;
        private int isSelected = 0;

        public menuTaskHolder(@NonNull final View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.menuTaskTitle);
            taskContent = itemView.findViewById(R.id.menuTaskContent);
            taskCardView = itemView.findViewById(R.id.taskCardView);
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    if (isSelected == 0) {
                        taskCardView.setCardBackgroundColor(selectedColor);
                        isSelected = 1;
                        selectedStatus.getValue().add(1);
                    } else {
                        taskCardView.setCardBackgroundColor(activeColor);
                        isSelected = 0;
                        selectedStatus.getValue().remove(1);
                    }
                    listener.longKeyPressed(getSnapshots().getSnapshot(position), getAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }
}

