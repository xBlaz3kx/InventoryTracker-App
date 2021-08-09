package com.inventorytracker.products.fragments.packages;

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

import com.inventorytracker.R;


public class PackageActionFragment extends Fragment implements View.OnClickListener {
    //ui
    private Button next;
    private RadioButton updateProduct, orderReception;
    private NavController controller;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.package_chooseaction_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        next = view.findViewById(R.id.packageNext);
        updateProduct = view.findViewById(R.id.packageCreateUpdate);
        orderReception = view.findViewById(R.id.packageOrderReception);
        next.setOnClickListener(this);
        controller = Navigation.findNavController(view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == next.getId()) {
            if (updateProduct.isChecked()) {
                controller.navigate(R.id.action_packageActionFragment2_to_packageCreationFragment2);
            } else if (orderReception.isChecked()) {
                controller.navigate(R.id.action_packageActionFragment2_to_packageSupplierOrderFragment);
            }
        }
    }
}
