package com.inventorytracker.discounts;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.inventorytracker.R;
import com.inventorytracker.utils.Constants;

public class DiscountManagement extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_management);
        String sellerID = getIntent().getStringExtra(Constants.UID);
        Bundle args = new Bundle();
        args.putString(Constants.UID, sellerID);
        NavController navController = Navigation.findNavController(this, R.id.discount_container);
        navController.navigate(R.id.discountChooserFragment, args);
    }
}
