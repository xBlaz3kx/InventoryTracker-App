package com.inventorytracker.products.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inventorytracker.R;
import com.inventorytracker.products.data.packages.ProductPackages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PackageAdapterInReport extends RecyclerView.Adapter<PackageAdapterInReport.ViewHolder> {

    private OnItemClickListener listener;
    private ArrayList<ProductPackages> packageList = new ArrayList<>();
    private Map<String, Integer> packageCount = new HashMap<>();
    private Map<String, Integer> productCount = new HashMap<>();
    Resources resources;

    public PackageAdapterInReport() {
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public PackageAdapterInReport.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        resources = Resources.getSystem();
        return new PackageAdapterInReport.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.package_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PackageAdapterInReport.ViewHolder holder, int position) {
        ProductPackages packaging = packageList.get(position);
        holder.packageName.setText(packaging.getPackageContent());
        holder.packageCount.setText(String.format(Locale.getDefault(), resources.getString(R.string.ReportPackageNum), packageCount.get(packaging.getPackageBarcode())));
        holder.productCount.setText(String.format(Locale.getDefault(), resources.getString(R.string.ReportProductNum), productCount.get(packaging.getPackageBarcode())));
    }

    @Override
    public int getItemCount() {
        if (packageList != null) {
            return packageList.size();
        } else {
            return 0;
        }
    }

    public void setPackageList(ArrayList<ProductPackages> packageList) {
        this.packageList = packageList;
        notifyDataSetChanged();
    }

    public void setPackageCount(Map<String, Integer> packageCount) {
        this.packageCount = packageCount;
        notifyDataSetChanged();
    }

    public void setProductCount(Map<String, Integer> productCount) {
        this.productCount = productCount;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (packageList.size() != 0 && position >= 0) {
            packageList.remove(position);
            packageCount.remove(position);
            productCount.remove(position);
            notifyDataSetChanged();
        }
    }

    public ProductPackages getPackage(int position) {
        return packageList.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView packageName, packageCount, productCount;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            packageCount = itemView.findViewById(R.id.packagesText);
            packageName = itemView.findViewById(R.id.packageName);
            productCount = itemView.findViewById(R.id.productsText);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(position);
                }
            });
        }
    }
}
