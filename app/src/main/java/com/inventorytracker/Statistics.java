package com.inventorytracker;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.inventorytracker.utils.Constants.REQUEST;
import static com.inventorytracker.utils.Constants.STATS_BESTELLERS;
import static com.inventorytracker.utils.Constants.STATS_CUSTOMERS;

public class Statistics extends AppCompatActivity {

    private FirebaseFunctions functions = FirebaseFunctions.getInstance();
    private ArrayList<BarEntry> statData = new ArrayList<>();
    private BarData chartData = new BarData();
    private BarChart stats;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resources = getResources();
        setContentView(R.layout.activity_stats);
        stats = findViewById(R.id.chart);
        String dataRequest = getIntent().getStringExtra(REQUEST);
        getDataSet(dataRequest);
    }

    private void getDataSet(String request) {
        if (request.equals(STATS_BESTELLERS)) {
            try {
                getStats("bestsellers").addOnSuccessListener(this::drawGraph)
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), resources.getString(R.string.SomethingWentWrong), Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.SomethingWentWrong), Toast.LENGTH_SHORT).show();
            }
        } else if (request.equals(STATS_CUSTOMERS)) {
            try {
                getStats("customers").addOnSuccessListener(this::drawGraph)
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), resources.getString(R.string.SomethingWentWrong), Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.SomethingWentWrong), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void drawGraph(HashMap<String, Integer> map) {
        int x = 0;
        BarData barData = new BarData();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            BarEntry barEntry = new BarEntry(x, entry.getValue());
            statData.add(barEntry);
            BarDataSet barDataSet = new BarDataSet(statData, entry.getKey());
            barData.addDataSet(barDataSet);
            x += 1.2f;
        }
        chartData.setBarWidth(0.9f);
        stats.setData(barData);
        stats.setFitBars(true);
        stats.invalidate();
    }

    private Task<HashMap<String, Integer>> getStats(String type) {
        Map<String, Object> data = new HashMap<>();
        data.put(REQUEST, type);
        return functions.getHttpsCallable("getStats").call(data).continueWith(task -> {
            HashMap<String, Integer> returnValues = new HashMap<>();
            JSONObject functionReturn = new JSONObject(task.getResult().getData().toString());
            JSONArray array = new JSONArray();
            if (functionReturn.has(STATS_BESTELLERS)) {
                array = functionReturn.getJSONArray(STATS_BESTELLERS);
            } else if (functionReturn.has(STATS_CUSTOMERS)) {
                array = functionReturn.getJSONArray(STATS_CUSTOMERS);
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (functionReturn.has(STATS_BESTELLERS)) {
                    returnValues.put(object.getString("productName"), object.getInt("sold"));
                } else if (functionReturn.has(STATS_CUSTOMERS)) {
                    returnValues.put(object.getString("customerName"), object.getInt("orderedTotal"));
                }
            }
            return returnValues;
        });
    }
}
