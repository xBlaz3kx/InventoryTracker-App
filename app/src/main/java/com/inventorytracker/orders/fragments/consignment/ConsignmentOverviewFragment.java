package com.inventorytracker.orders.fragments.consignment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inventorytracker.R;
import com.inventorytracker.discounts.data.Discount;
import com.inventorytracker.orders.Signature;
import com.inventorytracker.orders.adapters.order.OrderDiscountAdapter;
import com.inventorytracker.orders.data.consignment.ConsignmentOrder;
import com.inventorytracker.utils.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.inventorytracker.utils.BigDecimalUtil.toDecimals;
import static com.inventorytracker.utils.Constants.CONSIGNMENT_STATUS_OPEN;
import static com.inventorytracker.utils.Constants.CUSTOMER_ID;
import static com.inventorytracker.utils.Constants.PICKUP_PERSONAL;
import static com.inventorytracker.utils.Constants.PICKUP_POST;
import static com.inventorytracker.utils.Constants.SIGNATURE_CAPTURE;
import static com.inventorytracker.utils.Constants.TIMESTAMP;
import static com.inventorytracker.utils.Constants.dotDateFormatWithTime;
import static com.inventorytracker.utils.EditTextUtils.getTextToDecimal;
import static com.inventorytracker.utils.StringHelper.isStringNumeric;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.apache.commons.lang3.math.NumberUtils.createBigDecimal;

public class ConsignmentOverviewFragment extends Fragment implements View.OnClickListener {
    private TextInputLayout orderDiscountInput;
    private TextView orderTotal;
    private OrderDiscountAdapter adapter;
    private ConsignmentViewModel viewModel;
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference consignmentRef = db.collection("consignments");
    private CollectionReference discountRef = db.collection("discounts");
    //vars
    private DocumentReference chosenDiscount = null;
    private String sellerID;
    private Resources resources;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_overview_fragment, container, false);
        resources = getResources();
        try {
            viewModel = new ViewModelProvider(getActivity()).get(ConsignmentViewModel.class);
            sellerID = getArguments().getString(Constants.UID);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        viewModel.getLiveTotal().observe(getViewLifecycleOwner(), bigDecimal ->
                orderTotal.setText(String.format(Locale.getDefault(), resources.getString(R.string.OrderTotal), bigDecimal.doubleValue())));
        adapter = new OrderDiscountAdapter(new ArrayList<>());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ui
        Button submit = view.findViewById(R.id.orderSubmit);
        submit.setOnClickListener(this);
        orderDiscountInput = view.findViewById(R.id.orderDiscountInput);
        RecyclerView availableDiscountList = view.findViewById(R.id.orderDiscountList);
        orderTotal = view.findViewById(R.id.finalOrderTotal);
        availableDiscountList.setAdapter(adapter);
        availableDiscountList.setLayoutManager(new LinearLayoutManager(getContext()));
        orderDiscountInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = normalizeSpace(s.toString());
                if (isStringNumeric(input)) {
                    BigDecimal discount = toDecimals(createBigDecimal(input));
                    try {
                        orderTotal.setText(viewModel.calculateEstimatedOrderTotal(discount));
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // getActiveDiscounts();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.orderSubmit) {
            String input = getTextToDecimal(orderDiscountInput.getEditText());
            if (chosenDiscount != null) {
                chosenDiscount.get().addOnSuccessListener(documentSnapshot -> {
                    try {
                        if (documentSnapshot != null) {
                            Discount discount = documentSnapshot.toObject(Discount.class);
                            viewModel.addOrderDiscount(BigDecimal.valueOf(discount.getDiscountPercentage()));
                            makeOrder();
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                });
            } else if (isStringNumeric(input)) {
                BigDecimal discount = toDecimals(createBigDecimal(input));
                viewModel.addOrderDiscount(discount);
                makeOrder();
            } else {
                makeOrder();
            }
        }
    }

    private void getActiveDiscounts() {
        discountRef.whereEqualTo("discountActive", true).whereArrayContains("discountReference", viewModel.getCustomerReference()).whereEqualTo("sellerID", sellerID).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            adapter.addDiscounts(doc.toObject(Discount.class), doc.getReference());
                        }
                    }
                });
        for (final DocumentReference documentReference : viewModel.getProducts()) {
            discountRef.whereEqualTo("discountActive", true).whereArrayContains("discountReference", documentReference).whereEqualTo("sellerID", sellerID).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                adapter.addDiscounts(snapshot.toObject(Discount.class), snapshot.getReference());
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGNATURE_CAPTURE) {
            String URL;
            if (resultCode == CommonStatusCodes.SUCCESS && data != null) {
                URL = data.getStringExtra(Constants.URL);
                if (isNotBlank(URL)) {
                    ConsignmentOrder newOrder = new ConsignmentOrder(viewModel.getCustomerReference(),
                            viewModel.getSellerReference(),
                            viewModel.getProducts(),
                            viewModel.getNumProducts(),
                            viewModel.getDiscountValue(),
                            viewModel.getApplyOrderDiscount(),
                            viewModel.getOrderDiscount().doubleValue(),
                            viewModel.getTotal().doubleValue(),
                            URL, viewModel.getPickupMethod(),
                            CONSIGNMENT_STATUS_OPEN,
                            Calendar.getInstance(Locale.getDefault()).getTime());
                    newOrder.setAdditionalInfo(viewModel.getAdditionalInfo());
                    submitOrder(newOrder);
                }
            }
        }
    }

    //create new order from class attributes
    private void makeOrder() {
        if (!viewModel.getBarcodes().isEmpty() && viewModel.getTotal().doubleValue() > 0.0 && viewModel.getSellerReference() != null) {
            String pickupMethod = viewModel.getPickupMethod();
            if (pickupMethod.equals(PICKUP_POST)) {
                ConsignmentOrder newOrder = new ConsignmentOrder(viewModel.getCustomerReference(),
                        viewModel.getSellerReference(),
                        viewModel.getProducts(),
                        viewModel.getNumProducts(),
                        viewModel.getDiscountValue(),
                        viewModel.getApplyOrderDiscount(),
                        viewModel.getOrderDiscount().doubleValue(),
                        viewModel.getTotal().doubleValue(),
                        "", viewModel.getPickupMethod(),
                        CONSIGNMENT_STATUS_OPEN,
                        Calendar.getInstance(Locale.getDefault()).getTime());
                newOrder.setAdditionalInfo(viewModel.getAdditionalInfo());
                submitOrder(newOrder);
            } else if (pickupMethod.equals(PICKUP_PERSONAL)) {
                Intent intent = new Intent(getContext(), Signature.class);
                intent.putExtra(CUSTOMER_ID, viewModel.getCustomerReference().getId())
                        .putExtra(TIMESTAMP, dotDateFormatWithTime.format(Calendar.getInstance().getTime()));
                startActivityForResult(intent, SIGNATURE_CAPTURE);
            }
        }
    }

    //make and submit order
    private void submitOrder(final ConsignmentOrder order) {
        if (!viewModel.getBarcodes().isEmpty() && !viewModel.getProducts().isEmpty()) {
            consignmentRef.document().set(order).addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), resources.getString(R.string.OrderSubmitted), Toast.LENGTH_SHORT).show();
                try {
                    getActivity().finish();
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }).addOnFailureListener(e -> Toast.makeText(getContext(), resources.getString(R.string.SomethingWentWrong), Toast.LENGTH_SHORT).show());
        }
    }
}
