package com.inventorytracker.discounts.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.inventorytracker.R;
import com.inventorytracker.customer.data.Customer;
import com.inventorytracker.discounts.data.Discount;
import com.inventorytracker.products.data.Product;

import java.util.Locale;

import static com.inventorytracker.utils.BigDecimalUtil.fromDecimalToPercent;
import static com.inventorytracker.utils.Constants.activeColor;
import static com.inventorytracker.utils.Constants.inactiveColor;
import static com.inventorytracker.utils.Constants.selectedColor;
import static com.inventorytracker.utils.Constants.whiteColor;
import static com.inventorytracker.utils.EditTextUtils.clearEditTexts;

public class DiscountAdapter extends FirestoreRecyclerAdapter<Discount, DiscountAdapter.Holder> {
    private onClickListener listener;
    private int isSelected = 0;
    private Context context;
    Resources resources;
    private boolean bIsActive = true;

    public interface onClickListener {
        void onClick(DocumentSnapshot doc, int position);
    }

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    public DiscountAdapter(@NonNull FirestoreRecyclerOptions<Discount> options) {
        super(options);
    }

    @NonNull
    @Override
    public DiscountAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount, parent, false);
        context = parent.getContext();
        resources = Resources.getSystem();
        return new DiscountAdapter.Holder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Holder holder, int i, @NonNull Discount discount) {
        try {
            if (discount.getDiscountReference() != null) {
                if (discount.getDiscountReference().get(0).getPath().toLowerCase().contains("product")) {
                    holder.discountType.setText(resources.getString(R.string.ProductDiscount));
                } else {
                    holder.discountType.setText(resources.getString(R.string.CustomerDiscount));
                }
                if (holder.layout.getChildCount() == 0) {
                    for (DocumentReference doc : discount.getDiscountReference()) {
                        final TextView textView = new TextView(context);
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        textView.setLayoutParams(param);
                        textView.setTextSize(18);
                        textView.setTextColor(whiteColor);
                        doc.get().addOnSuccessListener(documentSnapshot -> {
                            String path = documentSnapshot.getReference().getPath();
                            if (path.contains("products")) {
                                Product product = documentSnapshot.toObject(Product.class);
                                textView.setText(product.getProductName());
                            } else {
                                Customer customer = documentSnapshot.toObject(Customer.class);
                                textView.setText(customer.getFirmName());
                            }
                        });
                        holder.layout.addView(textView);
                    }
                }
            } else {
                holder.discountType.setText(resources.getString(R.string.StandardDiscount));
            }
            String isActive;
            bIsActive = discount.getDiscountActive();
            if (bIsActive) {
                isActive = resources.getString(R.string.ActiveDiscount);
            } else {
                isActive = resources.getString(R.string.InactiveDiscount);
                holder.cardView.setCardBackgroundColor(inactiveColor);
            }
            holder.isDiscountActive.setText(isActive);
            holder.discountPercentage.setText(fromDecimalToPercent(discount.getDiscountPercentage()));
            if (discount.getDiscountEndDate() != null) {
                holder.discountStartDate.setText(String.format(Locale.getDefault(), "%s: %s", holder.discountStartDate.getText(), discount.getDiscountStartDate().toString()));
                holder.discountEndDate.setText(String.format(Locale.getDefault(), "%s: %s", holder.discountEndDate.getText(), discount.getDiscountEndDate().toString()));
            } else {
                clearEditTexts(holder.discountEndDate, holder.discountStartDate);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView discountPercentage, discountType, discountStartDate, discountEndDate, isDiscountActive;
        private CardView cardView;
        private LinearLayout layout;

        public Holder(@NonNull final View itemView) {
            super(itemView);
            discountPercentage = itemView.findViewById(R.id.discountPercentage);
            discountType = itemView.findViewById(R.id.discountType);
            isDiscountActive = itemView.findViewById(R.id.discountIsActive);
            discountEndDate = itemView.findViewById(R.id.discountEndDate);
            discountStartDate = itemView.findViewById(R.id.discountStartDate);
            layout = itemView.findViewById(R.id.discountReferences);
            cardView = itemView.findViewById(R.id.discountCard);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    if (bIsActive) {
                        if (isSelected == 0) {
                            cardView.setCardBackgroundColor(selectedColor);
                            isSelected = 1;
                        } else {
                            cardView.setCardBackgroundColor(activeColor);
                            isSelected = 0;
                        }
                    } else {
                        if (isSelected == 0) {
                            cardView.setCardBackgroundColor(selectedColor);
                            isSelected = 1;
                        } else {
                            cardView.setCardBackgroundColor(inactiveColor);
                            isSelected = 0;
                        }
                    }
                    listener.onClick(getSnapshots().getSnapshot(position), getAdapterPosition());
                }
            });
        }
    }
}
