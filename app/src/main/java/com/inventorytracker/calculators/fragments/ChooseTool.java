package com.inventorytracker.calculators.fragments;

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

public class ChooseTool extends Fragment implements View.OnClickListener {
    //nav
    private NavController controller;
    private RadioButton marginCalc, discountCalc;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calculator_choose_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button next = view.findViewById(R.id.toolsNext);
        next.setOnClickListener(this);
        marginCalc = view.findViewById(R.id.toolsMarginCalculator);
        discountCalc = view.findViewById(R.id.toolsDiscountCalculator);
        controller = Navigation.findNavController(view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolsNext) {
            if (marginCalc.isChecked()) {
                controller.navigate(R.id.action_toolChooser_to_marginCalculator);
            } else if (discountCalc.isChecked()) {
                controller.navigate(R.id.action_toolChooser_to_discountCalculator);
            }
        }
    }
}
