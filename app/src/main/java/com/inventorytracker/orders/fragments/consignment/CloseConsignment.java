package com.inventorytracker.orders.fragments.consignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inventorytracker.R;
import com.inventorytracker.orders.adapters.consignment.ConsignmentOrderAdapter;
import com.inventorytracker.orders.data.consignment.ConsignmentOrder;
import com.inventorytracker.utils.Constants;

import static com.inventorytracker.utils.Constants.CONSIGNMENT_STATUS_OPEN;
import static com.inventorytracker.utils.Constants.ORDER_ID;
import static com.inventorytracker.utils.UIGenerics.createOptions;
import static com.inventorytracker.utils.UIGenerics.setFirestoreAdapter;

public class CloseConsignment extends Fragment {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference consignmentRef = db.collection("consignments");
    //ui
    private ConsignmentOrderAdapter adapter;
    private NavController controller;
    //vars
    private String UID;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.closeconsignmentfragment, container, false);
        try {
            UID = getArguments().getString(Constants.UID);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        Query query = consignmentRef.whereEqualTo("consignmentStatus", CONSIGNMENT_STATUS_OPEN).orderBy("consignmentCreated", Query.Direction.DESCENDING);
        adapter = new ConsignmentOrderAdapter(createOptions(query, ConsignmentOrder.class));
        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            String consignmentOrder = documentSnapshot.getId();
            Bundle args = new Bundle();
            args.putString(Constants.UID, UID);
            args.putString(ORDER_ID, consignmentOrder);
            controller.navigate(R.id.action_closeConsignment_to_finishConsignment, args);
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView consignmentOrders = view.findViewById(R.id.closeconsigmentrecycler);
        setFirestoreAdapter(consignmentOrders, adapter, getContext());
        controller = Navigation.findNavController(view);
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
