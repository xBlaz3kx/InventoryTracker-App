package com.inventorytracker.orders.fragments.consignment;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inventorytracker.R;
import com.inventorytracker.products.data.Product;

import java.math.BigDecimal;

import static com.inventorytracker.utils.BigDecimalUtil.toDecimals;
import static com.inventorytracker.utils.Constants.PRODUCT_ID;
import static com.inventorytracker.utils.EditTextUtils.getTextToDecimal;
import static org.apache.commons.lang3.math.NumberUtils.createBigDecimal;


public class ConsignmentProductInfoFragment extends Fragment implements View.OnClickListener {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection("products");
    //ui
    private NavController controller;
    private TextView productName, productStock, productPrice, productNum;
    private TextInputLayout productDiscountIn;
    private CheckBox orderDiscount, productDiscount;
    private Button submit;
    private ConsignmentViewModel viewModel;
    //vars
    private DocumentReference productReference;
    private String barcode;
    private Resources resources;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        resources = getResources();
        String productID = "";
        try {
            viewModel = new ViewModelProvider(getActivity()).get(ConsignmentViewModel.class);
            productID = getArguments().getString(PRODUCT_ID);
            productReference = productRef.document(productID);
            getProductInfo();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return inflater.inflate(R.layout.order_productinfo_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productName = view.findViewById(R.id.productInfoName);
        productStock = view.findViewById(R.id.productInfoStock);
        productPrice = view.findViewById(R.id.productInfoPrice);
        productNum = view.findViewById(R.id.productInfoNumProd);
        productDiscountIn = view.findViewById(R.id.productDiscountInput);
        orderDiscount = view.findViewById(R.id.productApplyOrderDiscount);
        productDiscount = view.findViewById(R.id.productApplyDiscount);
        submit = view.findViewById(R.id.applyToProduct);
        controller = Navigation.findNavController(view);

    }

    private void getProductInfo() {
        if (productReference != null) {
            productReference.get().addOnSuccessListener(documentSnapshot -> {
                Product product = documentSnapshot.toObject(Product.class);
                if (product != null) {
                    try {
                        barcode = product.getProductBarcode();
                        productName.setText(String.format(resources.getString(R.string.ProductInfoName), product.getProductName()));
                        productStock.setText(String.format(resources.getString(R.string.ProductStock), product.getProductStock()));
                        productPrice.setText(String.format(resources.getString(R.string.ProductPrice), product.getProductPrice() * product.getProductVATValue()));
                        productNum.setText(viewModel.getNumProducts().get(barcode).toString());
                        orderDiscount.setChecked(viewModel.getApplyOrderDiscount().get(barcode));
                        productDiscount.setChecked(viewModel.getApplyProductDiscount().get(barcode));
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                }
            });
        }
    }

    private void setDiscount(BigDecimal discount) {
        viewModel.addProductDiscount(barcode, discount);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == submit.getId()) {
            viewModel.isOrderDiscountApplicable(barcode, orderDiscount.isChecked());
            viewModel.isProductDiscountApplicable(barcode, productDiscount.isChecked());
            if (productDiscount.isChecked()) {
                BigDecimal discount = toDecimals(createBigDecimal(getTextToDecimal(productDiscountIn.getEditText())));
                if (discount != null) {
                    setDiscount(discount);
                    Toast.makeText(getContext(), resources.getString(R.string.DiscountAdded), Toast.LENGTH_SHORT).show();
                }
            }
        }
        controller.navigate(R.id.action_productInfoFragment2_to_makeConsignment);
    }
}
