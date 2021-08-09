package com.inventorytracker.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.inventorytracker.Expenses;
import com.inventorytracker.R;
import com.inventorytracker.Statistics;
import com.inventorytracker.calculators.CalculateActivity;
import com.inventorytracker.discounts.DiscountManagement;
import com.inventorytracker.orders.ConsignmentActivity;
import com.inventorytracker.orders.OrdersDetailed;
import com.inventorytracker.products.Inventory;
import com.inventorytracker.products.Storage;
import com.inventorytracker.reminder.Reminders;
import com.inventorytracker.tasks.TaskManager;
import com.inventorytracker.utils.Constants;

import java.util.HashMap;
import java.util.Map;

import static com.inventorytracker.utils.Constants.REQUEST;
import static com.inventorytracker.utils.Constants.STATS_BESTELLERS;
import static com.inventorytracker.utils.Constants.STATS_CUSTOMERS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MainMenu extends FragmentActivity {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference deviceRef = db.collection("devices");
    //vars
    private String UID;

    private NavigationView.OnNavigationItemSelectedListener drawerListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Intent intent = null;
            switch (menuItem.getItemId()) {
                case R.id.productList:
                    intent = new Intent(getApplicationContext(), Inventory.class);
                    break;
                case R.id.warehouseReception:
                    intent = new Intent(getApplicationContext(), Storage.class);
                    break;
                case R.id.pastOrders:
                    intent = new Intent(getApplicationContext(), OrdersDetailed.class);
                    break;
                case R.id.reminders:
                    intent = new Intent(getApplicationContext(), Reminders.class);
                    break;
                case R.id.tasks:
                    intent = new Intent(getApplicationContext(), TaskManager.class);
                    intent.putExtra(Constants.UID, UID);
                    break;
                case R.id.discountManagement:
                    intent = new Intent(getApplicationContext(), DiscountManagement.class);
                    intent.putExtra(Constants.UID, UID);
                    break;
                case R.id.consignation:
                    intent = new Intent(getApplicationContext(), ConsignmentActivity.class);
                    intent.putExtra(Constants.UID, UID);
                    break;
                case R.id.tools:
                    intent = new Intent(getApplicationContext(), CalculateActivity.class);
                    break;
                case R.id.bestsellers:
                    intent = new Intent(getApplicationContext(), Statistics.class);
                    intent.putExtra(REQUEST, STATS_BESTELLERS);
                    break;
                case R.id.best_customers:
                    intent = new Intent(getApplicationContext(), Statistics.class);
                    intent.putExtra(REQUEST, STATS_CUSTOMERS);
                    break;
                case R.id.travellingExpenses:
                    intent = new Intent(getApplicationContext(), Expenses.class);
                    intent.putExtra(Constants.UID, UID);
                    break;
            }
            if (intent != null) {
                startActivity(intent);
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationView drawer = findViewById(R.id.nav_view);
        UID = getIntent().getStringExtra(Constants.UID);
        Bundle args = new Bundle();
        args.putString(Constants.UID, UID);
        NavController controller = Navigation.findNavController(this, R.id.fragment_container);
        controller.setGraph(R.navigation.navigation_mainmenu, args);
        NavigationUI.setupWithNavController(bottomNav, controller);
        drawer.setNavigationItemSelectedListener(drawerListener);
        getToken();
    }

    //retrieve token
    private void getToken() {
        FirebaseInstanceId instanceId = FirebaseInstanceId.getInstance();
        instanceId.getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful() && isNotBlank(UID)) {
                return;
            }
            try {
                final String token = task.getResult().getToken();
                deviceRef.whereEqualTo("UID", UID).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("ID", UID);
                        map.put("token", token);
                        deviceRef.document(instanceId.getId()).set(map);
                    } else {
                        if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                            try {
                                if (!queryDocumentSnapshots.getDocuments().get(0).getString("token").equals(token)) {
                                    deviceRef.document(FirebaseInstanceId.getInstance().getId()).update("token", token);
                                }
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
