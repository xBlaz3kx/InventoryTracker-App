package com.inventorytracker.orders.fragments.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.crashlytics.android.Crashlytics;
import com.inventorytracker.R;
import com.inventorytracker.orders.NewOrder;
import com.inventorytracker.utils.Constants;

import static com.inventorytracker.utils.Constants.CUSTOMER;
import static com.inventorytracker.utils.Constants.CUSTOMER_ID;
import static com.inventorytracker.utils.Constants.PICKUP_METHOD;
import static com.inventorytracker.utils.Constants.PICKUP_PERSONAL;
import static com.inventorytracker.utils.Constants.PICKUP_POST;

public class AdditionalOrderInfoFragment extends Fragment implements View.OnClickListener {
    private String UID, customerName, customerID, pickupMethod;
    private RadioButton personalPickup, postDelivery;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            UID = getArguments().getString(Constants.UID);
            customerID = getArguments().getString(CUSTOMER_ID);
            customerName = getArguments().getString(CUSTOMER);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return inflater.inflate(R.layout.order_additionalinfo_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postDelivery = view.findViewById(R.id.post);
        personalPickup = view.findViewById(R.id.personalPickup);
        Button next = view.findViewById(R.id.next);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next) {
            if ((personalPickup.isChecked() || postDelivery.isChecked())) {
                if (personalPickup.isChecked()) {
                    pickupMethod = PICKUP_PERSONAL;
                } else if (postDelivery.isChecked()) {
                    pickupMethod = PICKUP_POST;
                }
                if (!pickupMethod.isEmpty()) {
                    //parse info
                    Intent intent = new Intent(getContext(), NewOrder.class);
                    intent.putExtra(Constants.UID, UID);
                    intent.putExtra(CUSTOMER_ID, customerID);
                    intent.putExtra(CUSTOMER, customerName);
                    intent.putExtra(PICKUP_METHOD, pickupMethod);
                    startActivity(intent);
                }
            }
        }
    }
}
