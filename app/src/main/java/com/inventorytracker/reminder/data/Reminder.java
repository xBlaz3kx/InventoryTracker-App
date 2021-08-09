package com.inventorytracker.reminder.data;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.sql.Date;

@IgnoreExtraProperties
public class Reminder {
    private String reminderStatus;
    private Date reminderDate;
    private DocumentReference productReference;

    public Reminder() {
    }

    public Reminder(String reminderStatus, Date reminderDate, DocumentReference productReference) {
        this.reminderStatus = reminderStatus;
        this.reminderDate = reminderDate;
        this.productReference = productReference;
    }

    public String getReminderStatus() {
        return reminderStatus;
    }

    public void setReminderStatus(String reminderStatus) {
        this.reminderStatus = reminderStatus;
    }

    public Date getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(Date reminderDate) {
        this.reminderDate = reminderDate;
    }

    public DocumentReference getProductReference() {
        return productReference;
    }

    public void setProductReference(DocumentReference productReference) {
        this.productReference = productReference;
    }
}