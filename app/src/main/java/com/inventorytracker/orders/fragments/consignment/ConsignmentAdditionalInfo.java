package com.inventorytracker.orders.fragments.consignment;

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

import static com.inventorytracker.utils.Constants.CUSTOMER;
import static com.inventorytracker.utils.Constants.CUSTOMER_ID;
import static com.inventorytracker.utils.Constants.PICKUP_METHOD;
import static com.inventorytracker.utils.Constants.PICKUP_PERSONAL;
import static com.inventorytracker.utils.Constants.PICKUP_POST;


public class ConsignmentAdditionalInfo extends Fragment implements View.OnClickListener {
    //ui
    private RadioButton personalPickup, pickupPostOffice;
    private NavController controller;
    //var
    private String UID, customerName, customerID, pickupMethod;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_additionalinfo_fragment, container, false);
        try {
            UID = getArguments().getString(Constants.UID);
            customerID = getArguments().getString(CUSTOMER_ID);
            customerName = getArguments().getString(CUSTOMER);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pickupPostOffice = view.findViewById(R.id.post);
        personalPickup = view.findViewById(R.id.personalPickup);
        Button next = view.findViewById(R.id.next);
        next.setOnClickListener(this);
        controller = Navigation.findNavController(view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next) {
            if ((personalPickup.isChecked() || pickupPostOffice.isChecked())) {
                if (personalPickup.isChecked()) {
                    pickupMethod = PICKUP_PERSONAL;
                } else if (pickupPostOffice.isChecked()) {
                    pickupMethod = PICKUP_POST;
                }
                if (!pickupMethod.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.UID, UID);
                    bundle.putString(CUSTOMER_ID, customerID);
                    bundle.putString(CUSTOMER, customerName);
                    bundle.putString(PICKUP_METHOD, pickupMethod);
                    controller.navigate(R.id.action_consignmentAdditionalInfo_to_makeConsignment, bundle);
                }
            }
        }
    }
}
