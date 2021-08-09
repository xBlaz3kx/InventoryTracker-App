package com.inventorytracker.discounts.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inventorytracker.R;
import com.inventorytracker.products.data.Product;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.inventorytracker.utils.Constants.selectedColor;
import static com.inventorytracker.utils.Constants.whiteColor;

public class DiscountProductAdapter extends RecyclerView.Adapter<DiscountProductAdapter.productViewHolder> implements Filterable {
    private List<Product> exampleList;
    private List<Product> exampleListFull;
    private HashMap<Product, DocumentReference> snapshotHashMap = new HashMap<>();
    private ArrayList<Integer> selected = new ArrayList<>();

    public onClickListener listener;

    public DiscountProductAdapter(List<Product> exampleList, List<Product> exampleListFull, HashMap<Product, DocumentReference> snapshotHashMap) {
        this.exampleList = exampleList;
        this.exampleListFull = exampleListFull;
        this.snapshotHashMap = snapshotHashMap;
    }

    @NonNull
    @Override
    public productViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiscountProductAdapter.productViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull productViewHolder holder, int position) {
        try {
            Product product = exampleList.get(position);
            holder.productName.setText(product.getProductName());
            holder.productStock.setText(String.format(Locale.getDefault(), "%d", product.getProductStock()));
            holder.productBarcode.setText(product.getProductBarcode());
            holder.productPrice.setText(String.format(Locale.getDefault(), "%.2f €", product.getProductPrice() * product.getProductVATValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    public void setSelected(ArrayList<DocumentReference> selected) {
        for (DocumentReference s : selected) {
            if (snapshotHashMap.containsValue(s)) {
            }
        }
    }

    public void addProduct(DocumentSnapshot snapshot) {
        Product obj = snapshot.toObject(Product.class);
        exampleList.add(obj);
        exampleListFull.add(obj);
        snapshotHashMap.put(obj, snapshot.getReference());
        notifyDataSetChanged();
    }

    public void removeProduct(DocumentSnapshot documentSnapshot) {
        Product obj = documentSnapshot.toObject(Product.class);
        exampleList.remove(obj);
        exampleListFull.remove(obj);
        snapshotHashMap.remove(obj);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = StringUtils.normalizeSpace(constraint.toString()).toLowerCase();
                for (Product item : exampleListFull) {
                    if (item.getProductBarcode().toLowerCase().contains(filterPattern) || item.getProductName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exampleList.clear();
            exampleList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public interface onClickListener {
        void onClick(DocumentReference doc);
    }

    public class productViewHolder extends RecyclerView.ViewHolder {
        private TextView productName, productBarcode, productPrice, productStock;
        private CardView cardView;

        public productViewHolder(@NonNull final View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productBarcode = itemView.findViewById(R.id.productBarcode);
            productPrice = itemView.findViewById(R.id.productPrice);
            cardView = itemView.findViewById(R.id.productCard);
            productStock = itemView.findViewById(R.id.addProductNumber);
            if (selected.contains(getAdapterPosition())) {
                cardView.setCardBackgroundColor(selectedColor);
            }
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    if (selected.contains(position)) {
                        cardView.setCardBackgroundColor(selectedColor);
                        selected.remove(position);
                    } else {
                        selected.add(position);
                        cardView.setCardBackgroundColor(whiteColor);
                    }
                    listener.onClick(snapshotHashMap.get(exampleList.get(position)));
                }
            });
        }
    }
}
