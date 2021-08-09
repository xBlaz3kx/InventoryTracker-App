package com.inventorytracker.discounts.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.inventorytracker.R;
import com.inventorytracker.discounts.adapters.DiscountProductAdapter;
import com.inventorytracker.utils.Constants;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static com.inventorytracker.utils.Constants.DISCOUNT_TYPE;

public class DiscountProductFragment extends Fragment implements View.OnClickListener {
    //variables
    private String UID = "";
    private NavController controller;
    private DiscountCreationViewModel viewModel;
    //database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection("products");
    private DiscountProductAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            viewModel = new ViewModelProvider(getActivity()).get(DiscountCreationViewModel.class);
            UID = getArguments().getString(Constants.UID);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        adapter = new DiscountProductAdapter(new ArrayList<>(), new ArrayList<>(), new HashMap<>());
        productRef.orderBy("productName").addSnapshotListener((queryDocumentSnapshots, e) -> queryDocumentSnapshots.getDocumentChanges().forEach(documentChange -> {
            switch (documentChange.getType()) {
                case MODIFIED:
                    adapter.removeProduct(documentChange.getDocument());
                    adapter.addProduct(documentChange.getDocument());
                    break;
                case REMOVED:
                    adapter.removeProduct(documentChange.getDocument());
                    break;
                case ADDED:
                    adapter.addProduct(documentChange.getDocument());
                    break;
            }
        }));
        if (viewModel.getProductReferences().getValue() != null && viewModel.getProductReferences().getValue().isEmpty()) {
            adapter.setSelected(new ArrayList<>(viewModel.getProductReferences().getValue()));
        }
        adapter.setListener(doc -> viewModel.addProduct(doc));
        return inflater.inflate(R.layout.discountproductfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ui
        RecyclerView productList = view.findViewById(R.id.discountProductList);
        Button next = view.findViewById(R.id.discountProductNext);
        TextView search = view.findViewById(R.id.discountProductSearch);
        next.setOnClickListener(this);
        controller = Navigation.findNavController(view);
        productList.setLayoutManager(new LinearLayoutManager(getContext()));
        productList.setAdapter(adapter);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(StringUtils.normalizeSpace(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) throws NullPointerException {
        if (v.getId() == R.id.discountProductNext) {
            try {
                if (!viewModel.getProductReferences().getValue().isEmpty()) {
                    Bundle args = new Bundle();
                    args.putInt(DISCOUNT_TYPE, 2);
                    args.putString(Constants.UID, UID);
                    controller.navigate(R.id.action_discountProductFragment_to_discountMakeFragment, args);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }
}
