package com.inventorytracker.orders.adapters.consignment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inventorytracker.R;
import com.inventorytracker.customer.data.Customer;
import com.inventorytracker.orders.data.consignment.ConsignmentOrder;
import com.inventorytracker.products.data.Product;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.inventorytracker.utils.BigDecimalUtil.fromDecimalToPercent;
import static com.inventorytracker.utils.Constants.CONSIGNMENT_STATUS_CLOSED;
import static com.inventorytracker.utils.Constants.dotDateFormatWithTime;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

public class ConsignmentOrderAdapter extends FirestoreRecyclerAdapter<ConsignmentOrder, ConsignmentOrderAdapter.orderHolder> {

    private Context context;
    private String sellerName = "";
    Resources resources;

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onConsignmentOrderClick(DocumentSnapshot documentSnapshot, int position);
    }

    public ConsignmentOrderAdapter(@NonNull FirestoreRecyclerOptions<ConsignmentOrder> options) {
        super(options);
    }

    @NonNull
    @Override
    public orderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.consignment_order_item, parent, false);
        context = v.getContext();
        resources = Resources.getSystem();
        return new orderHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull final orderHolder orderHolder, final int i, @NonNull final ConsignmentOrder order) {
        //Add item info to order
        final LinearLayout layout = orderHolder.itemLayout;
        if (layout.getChildCount() == 0) {
            try {
                List<DocumentReference> productList = order.getConsignmentProductList();
                Map<String, Integer> productNumber = order.getConsignmentProductNumbers();
                Map<String, Double> discounts = order.getConsignmentProductDiscounts();
                Map<String, Integer> sold = order.getConsignmentProductsSold();
                TextView textView = new TextView(context);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(param);
                textView.setTextSize(18);
                textView.setTextColor(Color.WHITE);
                textView.setMaxLines(1);
                textView.setText(String.format("%5s %15s %10s %10s",
                        resources.getString(R.string.Product),
                        resources.getString(R.string.Ordered),
                        resources.getString(R.string.Sold),
                        resources.getString(R.string.Discount)));
                layout.addView(textView);
                for (DocumentReference product : productList) {
                    //insert new line for each product
                    final TextView titleView = new TextView(context);
                    titleView.setLayoutParams(param);
                    titleView.setTextSize(18);
                    titleView.setTextColor(Color.WHITE);
                    product.get().addOnSuccessListener(documentSnapshot -> {
                        try {
                            Product product1 = documentSnapshot.toObject(Product.class);
                            Integer prodNum = productNumber.get(product1.getProductBarcode());
                            Double discount = discounts.get(product1.getProductBarcode());
                            String sFormatted, consignmentStatus = order.getConsignmentStatus();
                            if (discount != null && discount.equals(0.0)) {
                                if (consignmentStatus.equals(CONSIGNMENT_STATUS_CLOSED)) {
                                    Integer numSold = sold.get(product1.getProductBarcode());
                                    sFormatted = String.format(Locale.getDefault(), "%5s %20s %15s",
                                            product1.getProductName(),
                                            prodNum,
                                            numSold);
                                } else {
                                    sFormatted = String.format(Locale.getDefault(), "%5s %20s %15s",
                                            product1.getProductName(),
                                            prodNum, "");
                                }
                            } else {
                                if (consignmentStatus.equals(CONSIGNMENT_STATUS_CLOSED)) {
                                    Integer numSold = sold.get(product1.getProductBarcode());
                                    sFormatted = String.format(Locale.getDefault(), "%5s %20s %15s %15s",
                                            product1.getProductName(), prodNum, numSold, fromDecimalToPercent(discount));
                                } else {
                                    sFormatted = String.format(Locale.getDefault(), "%5s %20s %15s",
                                            product1.getProductName(), prodNum, fromDecimalToPercent(discount));
                                }
                            }
                            titleView.setText(sFormatted.trim());
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                    });
                    layout.addView(titleView);
                }
                //get customerInfo from database
                order.getCustomerReference().get().addOnSuccessListener(documentSnapshot -> {
                    try {
                        if (documentSnapshot.exists()) {
                            Customer customer = documentSnapshot.toObject(Customer.class);
                            String text = "";
                            if (customer.getFirmName().isEmpty()) {
                                text = String.format(Locale.getDefault(), "%s %s", customer.getCustomerName(), customer.getCustomerSurname());
                            } else {
                                text = customer.getFirmName();
                            }
                            orderHolder.consignmentCustomer.setText(normalizeSpace(text));
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                });
                //get Seller Info from database
                order.getSellerReference().get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        sellerName = String.format(Locale.getDefault(), "%s %s", documentSnapshot.getString("sellerName"), documentSnapshot.getString("sellerSurname"));
                        orderHolder.consignmentSeller.setText(normalizeSpace(sellerName));
                    }
                });
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
        orderHolder.consignmentTotal.setText(String.format(Locale.getDefault(), resources.getString(R.string.OrderTotal), order.getConsignmentTotal()));
        orderHolder.consignmentStatus.setText(order.getConsignmentStatus());
        orderHolder.timestamp.setText(dotDateFormatWithTime.format(order.getConsignmentCreated()));
        orderHolder.consignmentSeller.setText(sellerName);
    }

    public class orderHolder extends RecyclerView.ViewHolder {
        private TextView consignmentCustomer, consignmentStatus, timestamp, consignmentSeller, consignmentTotal;
        private LinearLayout itemLayout;

        public orderHolder(@NonNull View itemView) {
            super(itemView);
            this.consignmentCustomer = itemView.findViewById(R.id.consignmentCustomer);
            this.timestamp = itemView.findViewById(R.id.consignmentDate);
            this.consignmentSeller = itemView.findViewById(R.id.consignmentSeller);
            this.consignmentStatus = itemView.findViewById(R.id.consignationStatus);
            this.consignmentTotal = itemView.findViewById(R.id.consignmentTotal);
            this.itemLayout = itemView.findViewById(R.id.consigned_items);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onConsignmentOrderClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }
}
