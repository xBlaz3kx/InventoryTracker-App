package com.inventorytracker.orders;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inventorytracker.R;
import com.inventorytracker.orders.adapters.order.DetailedOrderAdapter;
import com.inventorytracker.orders.data.order.Order;

import static com.inventorytracker.utils.UIGenerics.createOptions;
import static com.inventorytracker.utils.UIGenerics.setFirestoreAdapter;

public class OrdersDetailed extends Activity {
    //db
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference orderRef = db.collection("orders");
    //ui
    private DetailedOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_detailed);
        RecyclerView orders = findViewById(R.id.ordersDetailedRecycler);
        adapter = new DetailedOrderAdapter(createOptions(orderRef.orderBy("orderTimestamp", Query.Direction.DESCENDING), Order.class));
        setFirestoreAdapter(orders, adapter, getApplicationContext());
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
