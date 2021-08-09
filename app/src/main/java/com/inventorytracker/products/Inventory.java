package com.inventorytracker.products;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.inventorytracker.R;

public class Inventory extends FragmentActivity {
    NavController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        controller = Navigation.findNavController(this, R.id.inventory_container);
    }
}
