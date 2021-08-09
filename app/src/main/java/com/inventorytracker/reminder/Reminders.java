package com.inventorytracker.reminder;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inventorytracker.R;
import com.inventorytracker.reminder.adapters.ReminderAdapter;
import com.inventorytracker.reminder.data.Reminder;
import com.inventorytracker.utils.BigDecimalUtil;

import static com.inventorytracker.utils.UIGenerics.createOptions;
import static com.inventorytracker.utils.UIGenerics.setFirestoreAdapter;

public class Reminders extends AppCompatActivity {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference remindersRef = db.collection("reminders");
    //ui
    private ReminderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        RecyclerView reminders = findViewById(R.id.reminderView);
        Query query = remindersRef.orderBy("reminderDate", Query.Direction.DESCENDING).limit(10);
        adapter = new ReminderAdapter(createOptions(query, Reminder.class));
        setFirestoreAdapter(reminders, adapter, this);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                TextView textView = new TextView(getApplicationContext());
                textView.setText(getResources().getText(R.string.NoItemsToDisplay));
                if (itemCount == 0) {
                    reminders.addView(textView);
                } else {
                    reminders.removeView(textView);
                }
            }
        });
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
}
