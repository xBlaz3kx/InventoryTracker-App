package com.inventorytracker.orders.adapters.consignment;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.inventorytracker.R;
import com.inventorytracker.products.data.Product;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.inventorytracker.utils.StringHelper.isStringNumeric;
import static org.apache.commons.lang3.StringUtils.containsNone;
import static org.apache.commons.lang3.math.NumberUtils.createInteger;

public class ConsignmentClosingAdapter extends RecyclerView.Adapter<ConsignmentClosingAdapter.productAdapterViewHolder> {

    private ArrayList<DocumentReference> products;
    public Map<String, Integer> productsSold = new HashMap<>();
    private Map<String, Integer> productsInOrder = new HashMap<>();
    static transferData data;
    Resources resources;

    public void setData(transferData data) {
        ConsignmentClosingAdapter.data = data;
    }

    public interface transferData {
        void getOrderInfo(Integer position, Integer numSold, Integer productsSold);
    }

    public ConsignmentClosingAdapter(ArrayList<DocumentReference> products, Map<String, Integer> productsSold, Map<String, Integer> productsInOrder) {
        this.products = products;
        this.productsSold = productsSold;
        this.productsInOrder = productsInOrder;
    }

    public void setProductsInOrder(Map<String, Integer> productsInOrder) {
        this.productsInOrder = productsInOrder;
    }

    public ArrayList<DocumentReference> getProducts() {
        return products;
    }


    @NonNull
    @Override
    public ConsignmentClosingAdapter.productAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        resources = Resources.getSystem();
        return new ConsignmentClosingAdapter.productAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.consignment_order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ConsignmentClosingAdapter.productAdapterViewHolder holder, final int position) {
        DocumentReference currentProduct = products.get(position);
        currentProduct.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null) {
                try {
                    Product product = documentSnapshot.toObject(Product.class);
                    holder.productBarcode.setText(String.format(Locale.getDefault(), "%s", product.getProductBarcode()));
                    holder.productsInOrder = productsInOrder.get(product.getProductBarcode());
                    holder.productName.setText(String.format(Locale.getDefault(), "%s", product.getProductName()));
                    holder.numProducts.setText(String.format(Locale.getDefault(), resources.getString(R.string.NumProductsInOrder), productsInOrder.get(product.getProductBarcode())));
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class productAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView productName, productBarcode, numProducts;
        int productsInOrder;


        public productAdapterViewHolder(@NonNull final View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.consignment_productName);
            productBarcode = itemView.findViewById(R.id.consignment_productBarcode);
            numProducts = itemView.findViewById(R.id.consignment_productsnumber);
            TextInputLayout numProductsInput = itemView.findViewById(R.id.consignment_numinput);
            numProductsInput.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String input = StringUtils.normalizeSpace(s.toString());
                    if (isStringNumeric(input) && containsNone(input, ",.df")) {
                        try {
                            data.getOrderInfo(getAdapterPosition(), createInteger(input), productsInOrder);
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                    } else {
                        data.getOrderInfo(getAdapterPosition(), 0, productsInOrder);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }
}
