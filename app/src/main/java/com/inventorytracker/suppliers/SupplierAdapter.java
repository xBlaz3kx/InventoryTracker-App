package com.inventorytracker.suppliers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.inventorytracker.R;

import static com.inventorytracker.utils.Constants.activeColor;
import static com.inventorytracker.utils.Constants.selectedColor;

public class SupplierAdapter extends FirestoreRecyclerAdapter<Supplier, SupplierAdapter.supplierHolder> {
    public interface onClickListener {
        void onClick(DocumentReference supplierReference);
    }

    public onClickListener listener;

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    public SupplierAdapter(@NonNull FirestoreRecyclerOptions<Supplier> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull supplierHolder supplierHolder, int i, @NonNull Supplier supplier) {
        supplierHolder.supplierName.setText(supplier.getSupplierName());
        supplierHolder.supplierAddress.setText(supplier.getSupplierAddress());
        supplierHolder.supplierCountry.setText(supplier.getSupplierCountry());
    }

    @NonNull
    @Override
    public supplierHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new supplierHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier, parent, false));
    }

    class supplierHolder extends RecyclerView.ViewHolder {
        private TextView supplierName, supplierAddress, supplierCountry;
        private CardView cardView;
        int isSelected = 0;

        public supplierHolder(@NonNull View itemView) {
            super(itemView);
            this.supplierCountry = itemView.findViewById(R.id.supplierCountry);
            this.supplierName = itemView.findViewById(R.id.supplierName);
            this.supplierAddress = itemView.findViewById(R.id.supplierAddress);
            this.cardView = itemView.findViewById(R.id.supplierCard);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    if (isSelected == 0) {
                        cardView.setCardBackgroundColor(selectedColor);
                        isSelected = 1;
                    } else {
                        cardView.setCardBackgroundColor(activeColor);
                        isSelected = 0;
                    }
                    listener.onClick(getSnapshots().getSnapshot(position).getReference());
                }
            });
        }
    }
}
