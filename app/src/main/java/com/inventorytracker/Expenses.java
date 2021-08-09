package com.inventorytracker;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.inventorytracker.utils.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.inventorytracker.utils.EditTextUtils.getNumberFromEditText;

public class Expenses extends FragmentActivity implements View.OnClickListener {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference sellerRef = db.collection("sellers");
    private FirebaseFunctions functions = FirebaseFunctions.getInstance();
    private Resources resources;
    private TextInputLayout day1, day2, day3, day4, day5;
    //var
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travelling);
        day1 = findViewById(R.id.Day1Input);
        day2 = findViewById(R.id.Day2Input);
        day3 = findViewById(R.id.Day3Input);
        day4 = findViewById(R.id.Day4Input);
        day5 = findViewById(R.id.Day5Input);
        Button submit = findViewById(R.id.submit_expenses);
        submit.setOnClickListener(this);
        UID = getIntent().getStringExtra(Constants.UID);
        resources = getResources();
        getDistances();
    }

    private Task<String> submitExpenses(ArrayList<Number> distances) {
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.UID, UID);
        DayOfWeek[] days = DayOfWeek.values();
        for (int i = 0; i <= 4; i++) {
            data.put(days[i].getDisplayName(TextStyle.FULL, Locale.US).toLowerCase(), distances.get(i));
        }
        return functions.getHttpsCallable("travellingExpenses").call(data).continueWith(task -> task.getResult().getData().toString());
    }

    private void getDistances() {
        if (UID == null) {
            Toast.makeText(this, resources.getText(R.string.SomethingWentWrong), Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(cal.getTime());
        String currentWeek = String.format(Locale.getDefault(), "%s %d", "week", cal.get(Calendar.WEEK_OF_YEAR));
        DocumentReference week = sellerRef.document(UID).collection("expenses").document("travelling")
                .collection(String.valueOf(cal.get(Calendar.YEAR))).document(currentWeek);
        week.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                try {
                    Map<String, Number> distances = (Map<String, Number>) documentSnapshot.get("distances");
                    DayOfWeek[] days = DayOfWeek.values();
                    for (int i = 0; i <= 4; i++) {
                        Number distance = distances.get(days[i].getDisplayName(TextStyle.FULL, Locale.US).toLowerCase());
                        switch (i) {
                            case 0:
                                day1.getEditText().setText(String.format(Locale.getDefault(), "%.2f", distance));
                                break;
                            case 1:
                                day2.getEditText().setText(String.format(Locale.getDefault(), "%.2f", distance));
                                break;
                            case 2:
                                day3.getEditText().setText(String.format(Locale.getDefault(), "%.2f", distance));
                                break;
                            case 3:
                                day4.getEditText().setText(String.format(Locale.getDefault(), "%.2f", distance));
                                break;
                            case 4:
                                day5.getEditText().setText(String.format(Locale.getDefault(), "%.2f", distance));
                                break;
                        }
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }
        });
    }

    private ArrayList<Number> isInputOk() {
        //if nothing is input in the field => distance = 0;
        ArrayList<Number> distances = new ArrayList<>();
        try {
            BigDecimal distance1 = getNumberFromEditText(day1.getEditText()).setScale(2, RoundingMode.CEILING),
                    distance2 = getNumberFromEditText(day2.getEditText()).setScale(2, RoundingMode.CEILING),
                    distance3 = getNumberFromEditText(day3.getEditText()).setScale(2, RoundingMode.CEILING),
                    distance4 = getNumberFromEditText(day4.getEditText()).setScale(2, RoundingMode.CEILING),
                    distance5 = getNumberFromEditText(day5.getEditText()).setScale(2, RoundingMode.CEILING);
            if (distance1.doubleValue() <= 0.0) {
                distance1 = BigDecimal.ZERO;
            }
            if (distance2.doubleValue() <= 0.0) {
                distance2 = BigDecimal.ZERO;
            }
            if (distance3.doubleValue() <= 0.0) {
                distance3 = BigDecimal.ZERO;
            }
            if (distance4.doubleValue() <= 0.0) {
                distance4 = BigDecimal.ZERO;
            }
            if (distance5.doubleValue() <= 0.0) {
                distance5 = BigDecimal.ZERO;
            }
            distances.add(distance1.doubleValue());
            distances.add(distance2.doubleValue());
            distances.add(distance3.doubleValue());
            distances.add(distance4.doubleValue());
            distances.add(distance5.doubleValue());
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return distances;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit_expenses) {
            ArrayList<Number> expenses = isInputOk();
            if (expenses.size() != 0) {
                submitExpenses(expenses)
                        .addOnSuccessListener(s -> Toast.makeText(this, resources.getString(R.string.TravellingDistanceSubmitted), Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, resources.getText(R.string.SomethingWentWrong), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, resources.getText(R.string.SomethingWentWrong), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
