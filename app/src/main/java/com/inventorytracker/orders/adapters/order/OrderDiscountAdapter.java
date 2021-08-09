package com.inventorytracker.orders.adapters.order;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.inventorytracker.R;
import com.inventorytracker.discounts.data.Discount;
import com.inventorytracker.utils.BigDecimalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.inventorytracker.utils.BigDecimalUtil.fromDecimalToPercent;
import static com.inventorytracker.utils.Constants.activeColor;
import static com.inventorytracker.utils.Constants.selectedColor;
import static com.inventorytracker.utils.EditTextUtils.clearEditTexts;

public class OrderDiscountAdapter extends RecyclerView.Adapter<OrderDiscountAdapter.discountHolder> {
    private ArrayList<Discount> discounts = new ArrayList<>();
    private HashMap<Discount, DocumentReference> referenceHashMap = new HashMap<>();
    private ArrayList<Discount> selectedDiscounts = new ArrayList<>();
    private Integer selectedIndex = -1;
    longKeyPressedEventListener listener;
    Resources resources;

    public OrderDiscountAdapter(@NonNull ArrayList<Discount> discounts) {
        this.discounts = discounts;
    }

    public interface longKeyPressedEventListener {
        void longKeyPressed(DocumentReference doc);
    }

    @NonNull
    @Override
    public OrderDiscountAdapter.discountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        resources = parent.getResources();
        return new OrderDiscountAdapter.discountHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_shortened, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull discountHolder holder, int position) {
        Discount discount = discounts.get(position);
        holder.discountPercentage.setText(fromDecimalToPercent(discount.getDiscountPercentage()));
        if (discount.getDiscountType() == 1) {
            holder.discountType.setText(resources.getString(R.string.CustomerDiscount));
            clearEditTexts(holder.discountProductName);
        } else if (discount.getDiscountType() == 2) {
            holder.discountType.setText(String.format(Locale.getDefault(), "%s : %s",
                    resources.getString(R.string.ProductDiscount), ""));
            clearEditTexts(holder.discountProductName);
        } else {
            holder.discountType.setText(resources.getString(R.string.Discount));
        }
    }

    @Override
    public int getItemCount() {
        return discounts.size();
    }

    public class discountHolder extends RecyclerView.ViewHolder {
        private TextView discountType, discountPercentage, discountProductName;
        private CardView cardView;
        int isSelected = 0;

        public discountHolder(@NonNull View itemView) {
            super(itemView);
            this.discountType = itemView.findViewById(R.id.discountType_short);
            this.discountPercentage = itemView.findViewById(R.id.discountPercentage_short);
            this.discountProductName = itemView.findViewById(R.id.discountProductName_short);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    if (selectedIndex == -1) {
                        selectedIndex = position;
                        if (isSelected == 0) {
                            cardView.setCardBackgroundColor(selectedColor);
                            isSelected = 1;
                        } else {
                            cardView.setCardBackgroundColor(activeColor);
                            isSelected = 0;
                        }
                        listener.longKeyPressed(referenceHashMap.get(discounts.get(position)));
                    } else {
                        if (position == selectedIndex) {
                            selectedIndex = -1;
                            if (isSelected == 0) {
                                cardView.setCardBackgroundColor(selectedColor);
                                isSelected = 1;
                            } else {
                                cardView.setCardBackgroundColor(activeColor);
                                isSelected = 0;
                            }
                        }
                    }
                }
            });
        }
    }

    public void addDiscounts(Discount discount, DocumentReference documentReference) {
        if (!this.discounts.contains(discount)) {
            this.discounts.add(discount);
            referenceHashMap.put(discount, documentReference);
        }
        notifyDataSetChanged();
    }

    public void setListener(longKeyPressedEventListener listener) {
        this.listener = listener;
    }
}
