package com.inventorytracker.tasks;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inventorytracker.R;
import com.inventorytracker.tasks.adapter.TaskManagerAdapter;
import com.inventorytracker.tasks.data.UserTask;
import com.inventorytracker.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.inventorytracker.utils.Constants.TASK_STATUS_FINISHED;
import static com.inventorytracker.utils.Constants.TASK_STATUS_UNFINISHED;
import static com.inventorytracker.utils.DataStructureUtil.insertOrDeleteArrayList;
import static com.inventorytracker.utils.EditTextUtils.getTextFromEditText;
import static com.inventorytracker.utils.EditTextUtils.getTextToDecimal;
import static com.inventorytracker.utils.StringHelper.isStringNumeric;
import static com.inventorytracker.utils.UIGenerics.createOptions;
import static com.inventorytracker.utils.UIGenerics.setFirestoreAdapter;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.math.NumberUtils.toInt;


public class TaskManager extends AppCompatActivity implements View.OnClickListener {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tasksRef = db.collection("tasks");
    private Button finishTask;
    private RecyclerView taskList;
    private TaskManagerAdapter adapter;
    private TextInputLayout taskTitle, taskContent, taskPriority;
    //vars
    private String uploaderID;
    private ArrayList<String> taskID = new ArrayList<>();
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        resources = getResources();
        uploaderID = getIntent().getStringExtra(Constants.UID);
        //ui
        Button commitTask = findViewById(R.id.commitTask);
        commitTask.setOnClickListener(this);
        finishTask = findViewById(R.id.finishTask);
        finishTask.setOnClickListener(this);
        taskTitle = findViewById(R.id.taskTitle);
        taskContent = findViewById(R.id.taskContent);
        taskPriority = findViewById(R.id.priority);
        taskList = findViewById(R.id.taskList);
        setupAdapter();
        commitTask.setEnabled(false);
    }


    //upload task to database
    private void uploadTask(String title, String content, Integer priority) {
        Date timestamp = Calendar.getInstance().getTime();
        UserTask newTask = new UserTask(uploaderID, title, content, TASK_STATUS_UNFINISHED, priority, timestamp);
        tasksRef.add(newTask).addOnSuccessListener(documentReference -> Toast.makeText(TaskManager.this, resources.getString(R.string.TaskAdded), Toast.LENGTH_SHORT).show());
    }

    //updates task(s) as finished
    private void finishTask() {
        if (!taskID.isEmpty()) {
            for (String ID : taskID) {
                db.runTransaction(transaction -> {
                    DocumentReference taskReference = tasksRef.document(ID);
                    transaction.update(taskReference, "taskStatus", TASK_STATUS_FINISHED);
                    transaction.update(taskReference, "taskFinished", Calendar.getInstance().getTime());
                    return null;
                });
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupAdapter() {
        Query query = tasksRef.whereEqualTo("uploaderID", uploaderID).orderBy("priority", Query.Direction.ASCENDING).orderBy("taskTitle", Query.Direction.DESCENDING);
        adapter = new TaskManagerAdapter(createOptions(query, UserTask.class));
        adapter.setListener((doc, position) -> {
            insertOrDeleteArrayList(taskID, doc.getId());
            if (taskID.isEmpty()) {
                finishTask.setEnabled(false);
            } else {
                finishTask.setEnabled(true);
            }
        });
        setFirestoreAdapter(taskList, adapter, getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commitTask:
                String sTaskTitle, sTaskContent, sTaskPriority;
                sTaskTitle = getTextFromEditText(taskTitle.getEditText());
                sTaskContent = getTextFromEditText(taskContent.getEditText());
                sTaskPriority = getTextToDecimal(taskPriority.getEditText());
                if (uploaderID != null) {
                    if (isNotBlank(sTaskTitle)) {
                        if (isNotBlank(sTaskContent) && sTaskContent.length() >= 20 && isStringNumeric(sTaskPriority)) {
                            int taskPriority = toInt(sTaskPriority);
                            if (!(taskPriority <= 0) && !(taskPriority > 5)) {
                                uploadTask(sTaskTitle, sTaskContent, taskPriority);
                            } else {
                                Toast.makeText(TaskManager.this, resources.getString(R.string.InvalidPriority), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(TaskManager.this, resources.getString(R.string.TooLittleChars), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TaskManager.this, resources.getString(R.string.TitleNeeded), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.finishTask:
                finishTask();
                break;
        }
    }
}
