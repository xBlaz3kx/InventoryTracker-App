package com.inventorytracker.reminder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.inventorytracker.R;
import com.inventorytracker.products.data.Product;
import com.inventorytracker.reminder.data.Reminder;

import static com.inventorytracker.utils.Constants.dotDateFormat;
import static org.apache.commons.lang3.StringUtils.isBlank;


public class ReminderAdapter extends FirestoreRecyclerAdapter<Reminder, ReminderAdapter.reminderHolder> {

    public ReminderAdapter(@NonNull FirestoreRecyclerOptions<Reminder> options) {
        super(options);
    }

    @NonNull
    @Override
    public reminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new reminderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull final reminderHolder reminderHolder, int i, @NonNull Reminder reminder) {
        reminderHolder.date.setText(dotDateFormat.format(reminder.getReminderDate()));
        reminderHolder.status.setText(reminder.getReminderStatus());
        if (isBlank(reminderHolder.productName.getText())) {
            try {
                reminder.getProductReference().get().addOnSuccessListener(documentSnapshot -> {
                    Product product = documentSnapshot.toObject(Product.class);
                    reminderHolder.productName.setText(product.getProductName());
                });
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }

    static class reminderHolder extends RecyclerView.ViewHolder {
        private TextView status, productName, date;

        public reminderHolder(@NonNull View itemView) {
            super(itemView);
            this.date = itemView.findViewById(R.id.reminderDate);
            this.status = itemView.findViewById(R.id.reminderStatus);
            this.productName = itemView.findViewById(R.id.reminderProduct);
        }
    }
}
