package com.inventorytracker.customer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inventorytracker.R;
import com.inventorytracker.customer.data.Customer;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilterCustomers extends RecyclerView.Adapter<FilterCustomers.customerHolder> implements Filterable {

    private List<Customer> exampleList = new ArrayList<>();
    private List<Customer> exampleListFull = new ArrayList<>();
    private HashMap<Customer, DocumentSnapshot> snapshotHashMap = new HashMap<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClickParseSnapshot(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    class customerHolder extends RecyclerView.ViewHolder {
        private TextView CustomerName, CustomerSurname, CustomerAddress, FirmName, CustomerPost;
        private CardView customerCard;

        public customerHolder(@NonNull View itemView) {
            super(itemView);
            this.CustomerName = itemView.findViewById(R.id.customerName);
            this.CustomerSurname = itemView.findViewById(R.id.customerSurname);
            this.CustomerAddress = itemView.findViewById(R.id.customerAddress);
            this.FirmName = itemView.findViewById(R.id.firmName);
            this.CustomerPost = itemView.findViewById(R.id.customerPost);
            this.customerCard = itemView.findViewById(R.id.customerCard);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onClickParseSnapshot(snapshotHashMap.get(exampleList.get(position)), position);
                }
            });
        }
    }

    public FilterCustomers(List<Customer> exampleList, HashMap<Customer, DocumentSnapshot> snapshotHashMap) {
        this.exampleList = exampleList;
        this.snapshotHashMap = snapshotHashMap;
        exampleListFull = new ArrayList<>(exampleList);
    }

    public void addCustomer(DocumentSnapshot customer) {
        if (!snapshotHashMap.containsValue(customer)) {
            Customer obj = customer.toObject(Customer.class);
            exampleList.add(obj);
            exampleListFull.add(obj);
            snapshotHashMap.put(obj, customer);
            notifyDataSetChanged();
        }
    }

    public void removeCustomer(DocumentSnapshot documentSnapshot) {
        if (snapshotHashMap.containsValue(documentSnapshot)) {
            Customer obj = documentSnapshot.toObject(Customer.class);
            exampleList.remove(obj);
            exampleListFull.remove(obj);
            snapshotHashMap.remove(obj);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public FilterCustomers.customerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilterCustomers.customerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.customer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull customerHolder holder, int position) {
        try {
            Customer customer = exampleList.get(position);
            holder.CustomerName.setText(customer.getCustomerName());
            holder.CustomerSurname.setText(customer.getCustomerSurname());
            holder.CustomerAddress.setText(customer.getCustomerAddress());
            holder.FirmName.setText(customer.getFirmName());
            holder.CustomerPost.setText(customer.getCustomerPost());
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
            List<Customer> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = StringUtils.normalizeSpace(constraint.toString()).toLowerCase();
                for (Customer item : exampleListFull) {
                    if (item.getCustomerPost().toLowerCase().contains(filterPattern) || item.getFirmName().toLowerCase().contains(filterPattern)) {
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
