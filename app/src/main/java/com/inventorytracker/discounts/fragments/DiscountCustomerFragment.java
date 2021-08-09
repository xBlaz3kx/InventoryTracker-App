package com.inventorytracker.discounts.fragments;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inventorytracker.R;
import com.inventorytracker.discounts.adapters.DiscountCustomerAdapter;

import java.util.ArrayList;

public class DiscountCustomerFragment extends Fragment implements View.OnClickListener {
    //ui
    private RecyclerView customerList;
    private Button next;
    private DiscountCustomerAdapter adapter;
    private NavController controller;
    private DiscountCreationViewModel viewModel;
    //database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference customerRef = db.collection("customers");
    //vars
    private String UID;

    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.discountcustomerfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        customerList = view.findViewById(R.id.discountCustomerList);
        next = view.findViewById(R.id.discountCustomerNext);
        next.setOnClickListener(this);
        controller = Navigation.findNavController(view);
        try {
            viewModel = new ViewModelProvider(getActivity()).get(DiscountCreationViewModel.class);
            UID = getArguments().getString("UID");
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        adapter = new DiscountCustomerAdapter();
        adapter.setListener(doc -> viewModel.addCustomer(doc));
        customerList.setLayoutManager(new LinearLayoutManager(getContext()));
        customerList.setAdapter(adapter);
        customerRef.orderBy("firmName", Query.Direction.DESCENDING).addSnapshotListener((queryDocumentSnapshots, e) -> queryDocumentSnapshots.getDocumentChanges().forEach(documentChange -> {
            switch (documentChange.getType()) {
                case ADDED:
                    adapter.addCustomer(documentChange.getDocument());
                    break;
                case REMOVED:
                    adapter.removeCustomer(documentChange.getDocument());
                    break;
                case MODIFIED:
                    adapter.addCustomer(documentChange.getDocument());
                    break;
            }
        }));
        if (!viewModel.getCustomerReferences().getValue().isEmpty()) {
            adapter.setSelected(new ArrayList<>(viewModel.getCustomerReferences().getValue()));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.discountCustomerNext) {
            try {
                if (viewModel.getCustomerReferences().getValue() != null && !viewModel.getCustomerReferences().getValue().isEmpty()) {
                    Bundle args = new Bundle();
                    args.putString("UID", UID);
                    args.putInt("discountType", 1);
                    controller.navigate(R.id.action_discountCustomerFragment_to_discountMakeFragment, args);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }
}
