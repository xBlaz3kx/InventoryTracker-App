package com.inventorytracker.orders.fragments.order;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inventorytracker.R;
import com.inventorytracker.camera.BarcodeCaptureActivity;
import com.inventorytracker.orders.adapters.order.ProductAdapterInOrder;
import com.inventorytracker.products.data.Product;
import com.inventorytracker.utils.Constants;

import java.util.ArrayList;
import java.util.Locale;

import static com.inventorytracker.utils.Constants.CUSTOMER;
import static com.inventorytracker.utils.Constants.CUSTOMER_ID;
import static com.inventorytracker.utils.Constants.PICKUP_METHOD;
import static com.inventorytracker.utils.Constants.PRODUCT_ID;
import static com.inventorytracker.utils.Constants.PRODUCT_SCAN;
import static com.inventorytracker.utils.UIGenerics.setRecAdapter;


public class NewOrderFragment extends Fragment implements ProductAdapterInOrder.OnItemClickListener, View.OnClickListener {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection("products");
    private ProductAdapterInOrder adapter;
    //ui
    private RecyclerView productList;
    private Button next;
    private TextView orderPrice;
    private OrderViewModel viewModel;
    private NavController controller;

    private String customerID, customer, sellerID, pickupMethod;
    private Resources resources;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_main_fragment, container, false);
        Bundle bundle = getArguments();
        resources = getResources();
        try {
            viewModel = new ViewModelProvider(getActivity()).get(OrderViewModel.class);
            customerID = bundle.getString(CUSTOMER_ID);
            sellerID = bundle.getString(Constants.UID);
            customer = bundle.getString(CUSTOMER);
            pickupMethod = bundle.getString(PICKUP_METHOD);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        adapter = new ProductAdapterInOrder(viewModel.getProducts(), viewModel.getNumProducts());
        viewModel.setCustomerID(customerID);
        viewModel.setPickupMethod(pickupMethod);
        viewModel.setSellerID(sellerID);
        viewModel.getLiveProducts().observe(getViewLifecycleOwner(), documentReferences -> adapter.setProducts(documentReferences));
        viewModel.getLiveNumProducts().observe(getViewLifecycleOwner(), stringIntegerMap -> adapter.setNumList(stringIntegerMap));
        viewModel.getLiveTotal().observe(getViewLifecycleOwner(), bigDecimal ->
                orderPrice.setText(String.format(Locale.getDefault(), resources.getString(R.string.OrderTotal), bigDecimal.doubleValue())));
        viewModel.getLiveBarcodes().observe(getViewLifecycleOwner(), strings -> {
            if (strings.isEmpty()) next.setEnabled(false);
            else next.setEnabled(true);
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
        TextView customerFullName = view.findViewById(R.id.custFullName);
        TextView customerText = view.findViewById(R.id.customerID);
        Button add = view.findViewById(R.id.btn_add);
        productList = view.findViewById(R.id.productList);
        next = view.findViewById(R.id.btn_submit);
        orderPrice = view.findViewById(R.id.price);
        customerText.setText(customerID);
        customerFullName.setText(customer);
        add.setOnClickListener(this);
        next.setOnClickListener(this);
        next.setEnabled(false);
        recView();
    }

    //process activity results (signature or adding items to the order)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PRODUCT_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    final Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    final String barcodeValue = barcode.displayValue;
                    ArrayList<String> barcodes = viewModel.getLiveBarcodes().getValue();
                    if (barcodes.contains(barcodeValue)) {
                        viewModel.updateItemOnPress(barcodeValue);
                    } else {
                        addProduct(barcodeValue);
                    }
                    adapter.notifyItemChanged(barcodes.indexOf(barcodeValue));
                }
            }
        }
    }

    private void addProduct(final String barcodeValue) throws NullPointerException {
        productRef.whereEqualTo("productBarcode", barcodeValue).limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
            try {
                boolean isNull = true;
                if (!queryDocumentSnapshots.isEmpty()) {
                    next.setEnabled(true);
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    DocumentReference doc = documentSnapshot.getReference();
                    Product product = documentSnapshot.toObject(Product.class);
                    if (product != null) {
                        if (product.getProductStock() <= 0) {
                            Toast.makeText(getContext(), resources.getString(R.string.NotInStock), Toast.LENGTH_SHORT).show();
                            isNull = true;
                        } else {
                            isNull = false;
                            viewModel.addProduct(barcodeValue, doc, product);
                        }
                    }
                    if (!isNull) {
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), resources.getString(R.string.NotInStock), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.ProductUnavailable), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        });
    }

    //setup recyclerView
    private void recView() {
        setRecAdapter(productList, adapter, getContext());
        adapter.setOnItemClickListener(position -> {
            try {
                String barcode = viewModel.getLiveBarcodes().getValue().get(position);
                if (viewModel.getProductStock().get(barcode) >= viewModel.getNumProducts().get(barcode)) {
                    viewModel.updateItemOnPress(barcode);
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        });
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    viewModel.removeItem(viewHolder.getAdapterPosition());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), resources.getString(R.string.ProductRemoved), Toast.LENGTH_SHORT).show();
                } else if (direction == ItemTouchHelper.LEFT) {
                    Bundle args = new Bundle();
                    args.putString(PRODUCT_ID, adapter.getItemID(viewHolder.getAdapterPosition()));
                    controller.navigate(R.id.action_makeOrderFragment_to_productInfoFragment, args);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(productList);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                Bundle args = new Bundle();
                args.putString(Constants.UID, sellerID);
                controller.navigate(R.id.action_makeOrderFragment_to_orderDiscountFragment, args);
                break;
            case R.id.btn_add:
                startActivityForResult(new Intent(getContext(), BarcodeCaptureActivity.class), PRODUCT_SCAN);
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        viewModel.updateItemOnPress(viewModel.getBarcodes().get(position));
    }
}