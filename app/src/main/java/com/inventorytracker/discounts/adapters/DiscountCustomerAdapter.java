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
import com.inventorytracker.customer.data.Customer;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.inventorytracker.utils.Constants.activeColor;
import static com.inventorytracker.utils.Constants.inactiveColor;

public class DiscountCustomerAdapter extends RecyclerView.Adapter<DiscountCustomerAdapter.customerHolder> implements Filterable {

    private List<Customer> exampleList;
    private List<Customer> exampleListFull;
    private HashMap<Customer, DocumentSnapshot> snapshotHashMap;
    private HashMap<DocumentReference, Boolean> selectedMap = new HashMap<>();
    public onClickListener listener;

    public interface onClickListener {
        void onClick(DocumentReference doc);
    }

    public DiscountCustomerAdapter() {
        this.exampleList = new ArrayList<>();
        this.exampleListFull = new ArrayList<>();
        this.snapshotHashMap = new HashMap<>();
    }

    @NonNull
    @Override
    public DiscountCustomerAdapter.customerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiscountCustomerAdapter.customerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.customer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull customerHolder holder, int position) {
        holder.CustomerName.setText(exampleList.get(position).getCustomerName());
        holder.CustomerSurname.setText(exampleList.get(position).getCustomerAddress());
        holder.CustomerAddress.setText(exampleList.get(position).getCustomerSurname());
        holder.FirmName.setText(exampleList.get(position).getFirmName());
        holder.CustomerPost.setText(exampleList.get(position).getCustomerPost());
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

    public void addCustomer(DocumentSnapshot customer) {
        Customer obj = customer.toObject(Customer.class);
        exampleList.add(obj);
        exampleListFull.add(obj);
        snapshotHashMap.put(obj, customer);
        selectedMap.put(customer.getReference(), false);
        notifyDataSetChanged();
    }

    public void removeCustomer(DocumentSnapshot documentSnapshot) {
        Customer obj = documentSnapshot.toObject(Customer.class);
        exampleList.remove(obj);
        exampleListFull.remove(obj);
        snapshotHashMap.remove(obj);
        selectedMap.remove(documentSnapshot.getReference());
        notifyDataSetChanged();
    }

    public void setSelected(ArrayList<DocumentReference> selected) {
        for (DocumentReference reference : selected) {
            this.selectedMap.put(reference, true);
        }
    }

    public void setListener(onClickListener listener) {
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
                    if (selectedMap.get(snapshotHashMap.get(exampleList.get(position)).getReference())) {
                        customerCard.setCardBackgroundColor(inactiveColor);
                    } else {
                        customerCard.setCardBackgroundColor(activeColor);
                    }
                    listener.onClick(snapshotHashMap.get(exampleList.get(position)).getReference());
                }
            });
        }
    }
}
