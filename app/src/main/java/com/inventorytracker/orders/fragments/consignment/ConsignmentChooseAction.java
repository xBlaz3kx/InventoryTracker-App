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


public class ConsignmentChooseAction extends Fragment implements View.OnClickListener {
    //ui
    private RadioButton consignment_make, consignment_close;
    private NavController controller;
    //vars
    private String UID;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.consignment_chooseaction_fragment, container, false);
        try {
            UID = getArguments().getString(Constants.UID);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        consignment_make = view.findViewById(R.id.makeConsignment);
        consignment_close = view.findViewById(R.id.closeConsigment);
        Button next = view.findViewById(R.id.consignment_next);
        next.setOnClickListener(this);
        controller = Navigation.findNavController(view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.consignment_next) {
            Bundle args = new Bundle();
            args.putString(Constants.UID, UID);
            if (consignment_close.isChecked()) {
                controller.navigate(R.id.action_consignmentChooseAction_to_closeConsignment, args);
            } else if (consignment_make.isChecked()) {
                controller.navigate(R.id.action_consignmentChooseAction_to_chooseCustomer, args);
            }
        }
    }
}
