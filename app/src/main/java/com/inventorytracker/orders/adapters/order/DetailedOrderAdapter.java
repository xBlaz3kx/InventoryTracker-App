package com.inventorytracker.orders.adapters.order;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inventorytracker.R;
import com.inventorytracker.customer.data.Customer;
import com.inventorytracker.orders.data.order.Order;
import com.inventorytracker.products.data.Product;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.inventorytracker.utils.BigDecimalUtil.fromDecimalToPercent;
import static com.inventorytracker.utils.Constants.activeColor;
import static com.inventorytracker.utils.Constants.dotDateFormatWithTime;
import static com.inventorytracker.utils.Constants.selectedColor;
import static com.inventorytracker.utils.Constants.whiteColor;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

public class DetailedOrderAdapter extends FirestoreRecyclerAdapter<Order, DetailedOrderAdapter.orderHolder> {
    longKeyPressedEventListener listener;
    private Context context;
    private Resources resources;
    private String sellerName = "";

    public interface longKeyPressedEventListener {
        void longKeyPressed(DocumentSnapshot doc, int position);
    }

    public DetailedOrderAdapter(@NonNull FirestoreRecyclerOptions<Order> options) {
        super(options);
    }

    @NonNull
    @Override
    public orderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details, parent, false);
        context = parent.getContext();
        resources = Resources.getSystem();
        return new orderHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull final orderHolder orderHolder, final int i, @NonNull final Order order) {
        //Add item info to order
        final LinearLayout layout = orderHolder.itemLayout;
        if (layout.getChildCount() == 0) {
            try {
                if (orderHolder.sellerID.getText().toString().contains("Prodajalec: %s")) {
                    List<DocumentReference> productList = order.getProductList();
                    final Map<String, Double> discounts = order.getProductDiscount();
                    final Map<String, Integer> productNumbers = order.getProductNumbers();
                    //For each order insert first line : column names
                    TextView textView = new TextView(context);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(param);
                    textView.setTextSize(18);
                    textView.setTextColor(whiteColor);
                    textView.setMaxLines(1);
                    textView.setText(String.format(Locale.getDefault(), "%5s %30s %20s",
                            resources.getString(R.string.Product),
                            resources.getString(R.string.Quantity),
                            resources.getString(R.string.Discount)));
                    layout.addView(textView);
                    for (DocumentReference product : productList) {
                        final TextView titleView = new TextView(context);
                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        titleView.setLayoutParams(lparams);
                        titleView.setTextSize(18);
                        titleView.setTextColor(whiteColor);
                        product.get().addOnSuccessListener(documentSnapshot -> {
                            try {
                                Product productObj = documentSnapshot.toObject(Product.class);
                                String productBarcode = productObj.getProductBarcode();
                                String sFormatted = "";
                                Double productDiscount = discounts.get(productBarcode);
                                Integer numProducts = productNumbers.get(productBarcode);
                                if (productDiscount != null && productDiscount.equals(0.0)) {
                                    sFormatted = String.format(Locale.getDefault(), "%5s %35s",
                                            productObj.getProductName(),
                                            numProducts);
                                } else {
                                    BigDecimal discount = NumberUtils.createBigDecimal(productDiscount.toString());
                                    sFormatted = String.format(Locale.getDefault(), "%5s %35s %23s",
                                            productObj.getProductName(),
                                            numProducts,
                                            fromDecimalToPercent(discount.doubleValue()));
                                }
                                titleView.setText(sFormatted.trim());
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                            }
                        });
                        layout.addView(titleView);
                    }
                    order.getCustomerReference().get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Customer customer = documentSnapshot.toObject(Customer.class);
                            String customerText;
                            if (customer.getFirmName().isEmpty()) {
                                customerText = String.format(Locale.getDefault(), "%s %s", customer.getCustomerName(), customer.getCustomerSurname());
                            } else {
                                customerText = customer.getFirmName();
                            }
                            orderHolder.customerName.setText(String.format(Locale.getDefault(), resources.getString(R.string.Customerr), normalizeSpace(customerText)));
                        }
                    });
                    //get seller Info from database
                    order.getSellerReference().get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            sellerName = String.format(Locale.getDefault(), "%s %s", documentSnapshot.getString("sellerName"), documentSnapshot.getString("sellerSurname"));
                            orderHolder.sellerID.setText(String.format(Locale.getDefault(), resources.getString(R.string.Seller), normalizeSpace(sellerName)));
                        }
                    });
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
        orderHolder.orderTotal.setText(String.format(Locale.getDefault(), resources.getString(R.string.OrderTotal), order.getOrderTotal()));
        orderHolder.orderStatus.setText(order.getPickupMethod());
        orderHolder.orderTimestamp.setText(String.format(Locale.getDefault(), "%s", dotDateFormatWithTime.format(order.getOrderTimestamp())));
    }

    class orderHolder extends RecyclerView.ViewHolder {
        private TextView customerName, orderStatus, orderTimestamp, sellerID, orderTotal;
        private LinearLayout itemLayout;
        private CardView cardView;
        private int isSelected = 0;

        public orderHolder(@NonNull View itemView) {
            super(itemView);
            this.customerName = itemView.findViewById(R.id.detailedOrderCustomerName);
            this.orderTimestamp = itemView.findViewById(R.id.orderDate);
            this.sellerID = itemView.findViewById(R.id.orderSeller);
            this.orderStatus = itemView.findViewById(R.id.orderStatus);
            this.orderTotal = itemView.findViewById(R.id.orderTotal);
            this.itemLayout = itemView.findViewById(R.id.itemList);
            this.cardView = itemView.findViewById(R.id.detailedOrderCard);
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    if (isSelected == 0) {
                        cardView.setCardBackgroundColor(selectedColor);
                        isSelected = 1;
                    } else {
                        cardView.setCardBackgroundColor(activeColor);
                        isSelected = 0;
                    }
                    listener.longKeyPressed(getSnapshots().getSnapshot(position), getAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }

    public void setListener(longKeyPressedEventListener listener) {
        this.listener = listener;
    }
}
