package com.inventorytracker.orders.adapters.order;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.inventorytracker.R;
import com.inventorytracker.products.data.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProductAdapterInOrder extends RecyclerView.Adapter<ProductAdapterInOrder.productAdapterViewHolder> {
    private static OnItemClickListener listener;
    private static ArrayList<DocumentReference> products = new ArrayList<>();
    private static Map<String, Integer> numList = new HashMap<>();
    Resources resources;

    public void setOnItemClickListener(OnItemClickListener listener) {
        ProductAdapterInOrder.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ProductAdapterInOrder(ArrayList<DocumentReference> products, Map<String, Integer> numList) {
        ProductAdapterInOrder.products = products;
        ProductAdapterInOrder.numList = numList;
    }

    public static class productAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView productName, productBarcode, numProducts, productPrice;

        public productAdapterViewHolder(@NonNull final View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productBarcode = itemView.findViewById(R.id.productBarcode);
            numProducts = itemView.findViewById(R.id.addProductNumber);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    @NonNull
    @Override
    public productAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        resources = parent.getResources();
        return new productAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final productAdapterViewHolder holder, final int position) {
        DocumentReference currentProduct = products.get(position);
        currentProduct.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null) {
                try {
                    Product product = documentSnapshot.toObject(Product.class);
                    holder.productBarcode.setText(product.getProductBarcode());
                    holder.productName.setText(product.getProductName());
                    holder.productPrice.setText(String.format(Locale.getDefault(), resources.getString(R.string.ProductPrice), product.getProductPrice()));
                    holder.numProducts.setText(String.format(Locale.getDefault(), resources.getString(R.string.NumProductsInOrder), numList.get(product.getProductBarcode())));
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

    public String getItemID(int position) {
        return products.get(position).getId();
    }

    public void setProducts(ArrayList<DocumentReference> products) {
        ProductAdapterInOrder.products = products;
        notifyDataSetChanged();
    }

    public void setNumList(Map<String, Integer> numList) {
        ProductAdapterInOrder.numList = numList;
        notifyDataSetChanged();
    }
}
