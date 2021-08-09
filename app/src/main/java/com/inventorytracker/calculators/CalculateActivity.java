package com.inventorytracker.calculators;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.inventorytracker.R;

public class CalculateActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        NavController controller = Navigation.findNavController(this, R.id.tools_fragment);
        controller.navigate(R.id.toolChooser);
    }
}
