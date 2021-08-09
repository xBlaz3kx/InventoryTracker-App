package com.inventorytracker.products.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inventorytracker.R;
import com.inventorytracker.products.data.Product;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

public class FilterProducts extends RecyclerView.Adapter<FilterProducts.productHolder> implements Filterable {

    private List<Product> exampleList;
    private List<Product> exampleListFull;
    private List<DocumentSnapshot> snapshots = new ArrayList<>();
    private Context context;
    Resources resources;
    ContextMenuInterface menuInterface;

    public void setMenuInterface(ContextMenuInterface menuInterface) {
        this.menuInterface = menuInterface;
    }

    public interface ContextMenuInterface {
        void updateBarcode(String docPath);
    }

    static class productHolder extends RecyclerView.ViewHolder {
        private TextView productName, productPrice, productStock, productBarcode;
        private CardView productCard;
        private ImageView contextMenu;

        public productHolder(@NonNull View itemView) {
            super(itemView);
            this.productName = itemView.findViewById(R.id.productName);
            this.productPrice = itemView.findViewById(R.id.productPrice);
            this.productStock = itemView.findViewById(R.id.addProductNumber);
            this.productBarcode = itemView.findViewById(R.id.productBarcode);
            this.productCard = itemView.findViewById(R.id.productCard);
            this.contextMenu = itemView.findViewById(R.id.product_context);
        }
    }

    public FilterProducts(List<Product> exampleList) {
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }

    public void addProduct(DocumentSnapshot documentSnapshot) {
        Product obj = documentSnapshot.toObject(Product.class);
        snapshots.add(documentSnapshot);
        exampleList.add(obj);
        exampleListFull.add(obj);
        notifyDataSetChanged();
    }

    public void removeProduct(DocumentSnapshot documentSnapshot) {
        Product obj = documentSnapshot.toObject(Product.class);
        exampleList.remove(obj);
        exampleListFull.remove(obj);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilterProducts.productHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        resources = Resources.getSystem();
        return new productHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull productHolder holder, int position) {
        try {
            Product product = exampleList.get(position);
            holder.productName.setText(normalizeSpace(product.getProductName().toUpperCase()));
            holder.productPrice.setText(String.format(Locale.getDefault(), resources.getString(R.string.ProductPrice), product.getProductPrice() * product.getProductVATValue()));
            holder.productStock.setText(String.format(Locale.getDefault(), resources.getString(R.string.ProductStock), product.getProductStock().toString()));
            holder.productBarcode.setText(exampleList.get(position).getProductBarcode());
            holder.contextMenu.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, holder.itemView);
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.updateProductBarcode) {
                        if (menuInterface != null && position != RecyclerView.NO_POSITION) {
                            menuInterface.updateBarcode(snapshots.get(position).getReference().getPath());
                        }
                    }
                    return false;
                });
                popup.getMenuInflater().inflate(R.menu.product_context_menu, popup.getMenu());
                popup.show();
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }


    @Override
    public int getItemCount() {
        return exampleList.size();
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
                String filterPattern = normalizeSpace(constraint.toString()).toLowerCase();
                for (Product item : exampleListFull) {
                    if (item.getProductName().toLowerCase().contains(filterPattern) || item.getProductBarcode().toLowerCase().contains(filterPattern)) {
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
}
