package com.inventorytracker.products.fragments.packages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.inventorytracker.R;
import com.inventorytracker.products.adapters.SupplierOrderAdapter;
import com.inventorytracker.products.data.packages.SupplierOrder;
import com.inventorytracker.utils.JSONUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PackageSupplierOrderFragment extends Fragment implements View.OnClickListener {
    //db
    private FirebaseFunctions functions = FirebaseFunctions.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //ui
    private NavController controller;
    private SupplierOrderAdapter adapter;
    private PackageSessionViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.supplierorder_chooseorder_fragment, container, false);
        controller = Navigation.findNavController(view);
        adapter = new SupplierOrderAdapter();
        viewModel = new ViewModelProvider(getActivity()).get(PackageSessionViewModel.class);
        adapter.setOnClickListener(reference -> {
            viewModel.setSupplierOrderReference(db.document(reference));
            controller.navigate(R.id.action_packageSupplierOrderFragment_to_packageSessionReport);
        });
        getSupplierOrders().addOnSuccessListener(orderList -> orderList.forEach(order -> {
            adapter.addOrder(order);
        }));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button next = view.findViewById(R.id.reportSupplierOrderNext);
        next.setOnClickListener(this);
        RecyclerView supplierOrderList = view.findViewById(R.id.supplierOrderRec);
        supplierOrderList.setLayoutManager(new LinearLayoutManager(getContext()));
        supplierOrderList.setHasFixedSize(true);
        supplierOrderList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reportSupplierOrderNext) {
            controller.navigate(R.id.action_packageSupplierOrderFragment_to_packageSessionReport);
        }
    }

    private Task<List<SupplierOrder>> getSupplierOrders() {
        return functions.getHttpsCallable("getSupplierOrders").call(null).continueWith(task -> {
            try {
                Object obj = task.getResult().getData();
                if (obj instanceof String) {
                    String s = obj.toString();
                    return Arrays.asList(JSONUtils.readJson(s, SupplierOrder[].class));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
            return new ArrayList<>();
        });
    }
}
