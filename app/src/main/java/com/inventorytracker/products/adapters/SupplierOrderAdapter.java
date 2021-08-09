package com.inventorytracker.products.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.inventorytracker.R;
import com.inventorytracker.products.data.packages.SupplierOrder;

import java.util.ArrayList;
import java.util.List;

import static com.inventorytracker.utils.Constants.dotDateFormatWithTime;

public class SupplierOrderAdapter extends RecyclerView.Adapter<SupplierOrderAdapter.supplierOrderHolder> {

    private List<SupplierOrder> orderList = new ArrayList<>();
    onClickListener listener;

    public interface onClickListener {
        void onClick(String reference);
    }

    public SupplierOrderAdapter() {
    }

    public void addOrder(SupplierOrder order) {
        orderList.add(order);
        notifyDataSetChanged();
    }

    public void removeOrder(SupplierOrder order) {
        orderList.remove(order);
        notifyDataSetChanged();
    }

    public void setOnClickListener(onClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SupplierOrderAdapter.supplierOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new supplierOrderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.supplierorder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierOrderAdapter.supplierOrderHolder holder, int position) {
        SupplierOrder order = orderList.get(position);
        holder.ID.setText(order.getID());
        try {
            holder.Date.setText(dotDateFormatWithTime.format(order.getOrderDate()));
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class supplierOrderHolder extends RecyclerView.ViewHolder {
        private TextView ID, Date;

        public supplierOrderHolder(@NonNull View itemView) {
            super(itemView);
            this.ID = itemView.findViewById(R.id.supplierOrderID);
            this.Date = itemView.findViewById(R.id.supplierOrderDate);
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onClick(orderList.get(pos).getReference());
                }
            });
        }
    }
}
