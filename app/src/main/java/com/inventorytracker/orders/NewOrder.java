package com.inventorytracker.orders;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.inventorytracker.R;
import com.inventorytracker.orders.adapters.order.ProductAdapterInOrder;
import com.inventorytracker.utils.Constants;

import static com.inventorytracker.utils.Constants.CUSTOMER;
import static com.inventorytracker.utils.Constants.CUSTOMER_ID;
import static com.inventorytracker.utils.Constants.PICKUP_METHOD;

public class NewOrder extends FragmentActivity implements ProductAdapterInOrder.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_orderv3);
        //vars
        String customerID = getIntent().getStringExtra(CUSTOMER_ID);
        String customer = getIntent().getStringExtra(CUSTOMER);
        String sellerID = getIntent().getStringExtra(Constants.UID);
        String pickupMethod = getIntent().getStringExtra(PICKUP_METHOD);
        //ui
        NavController controller = Navigation.findNavController(this, R.id.order_hostfragment);
        Bundle args = new Bundle();
        args.putString(CUSTOMER_ID, customerID);
        args.putString(CUSTOMER, customer);
        args.putString(Constants.UID, sellerID);
        args.putString(PICKUP_METHOD, pickupMethod);
        controller.setGraph(R.navigation.navigation_orders, args);
    }

    @Override
    public void onItemClick(int position) {
    }
}
