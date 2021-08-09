package com.inventorytracker.orders;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.inventorytracker.R;
import com.inventorytracker.utils.Constants;

public class ConsignmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consignment);
        String UID = getIntent().getStringExtra(Constants.UID);
        Bundle args = new Bundle();
        args.putString(Constants.UID, UID);
        NavController controller = Navigation.findNavController(this, R.id.consignment_container);
        controller.setGraph(R.navigation.navigation_consignment, args);
    }
}
