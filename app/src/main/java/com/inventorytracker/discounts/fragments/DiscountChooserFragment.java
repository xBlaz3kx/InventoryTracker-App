package com.inventorytracker.discounts.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.crashlytics.android.Crashlytics;
import com.inventorytracker.R;
import com.inventorytracker.utils.Constants;


public class DiscountChooserFragment extends Fragment implements View.OnClickListener {
    //ui
    private RadioButton customer, product, updateDiscount, makeDiscount;
    private NavController navController;
    //vars
    private String UID = "";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            UID = getArguments().getString(Constants.UID);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return inflater.inflate(R.layout.discountchooser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        customer = view.findViewById(R.id.discountCustomerLink);
        product = view.findViewById(R.id.discountProductLink);
        updateDiscount = view.findViewById(R.id.discountUpdateStatus);
        makeDiscount = view.findViewById(R.id.makeDiscount);
        Button next = view.findViewById(R.id.discount_next);
        next.setOnClickListener(this);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.discount_next) {
            if (UID != null) {
                Bundle args = new Bundle();
                args.putString(Constants.UID, UID);
                if (customer.isChecked()) {
                    navController.navigate(R.id.action_discountChooserFragment_to_discountCustomerFragment, args);
                } else if (product.isChecked()) {
                    navController.navigate(R.id.action_discountChooserFragment_to_discountProductFragment, args);
                } else if (updateDiscount.isChecked()) {
                    navController.navigate(R.id.action_discountChooserFragment_to_discountManagerFragment, args);
                } else if (makeDiscount.isChecked()) {
                    navController.navigate(R.id.action_discountChooserFragment_to_discountMakeFragment, args);
                }
            }
        }
    }
}
