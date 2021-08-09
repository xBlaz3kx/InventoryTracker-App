package com.inventorytracker.products.fragments.product;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.inventorytracker.R;
import com.inventorytracker.camera.BarcodeCaptureActivity;
import com.inventorytracker.products.data.Product;
import com.inventorytracker.utils.BigDecimalUtil;

import java.math.BigDecimal;

import static com.inventorytracker.utils.BigDecimalUtil.VAT22;
import static com.inventorytracker.utils.Constants.PRODUCT_SCAN;
import static com.inventorytracker.utils.EditTextUtils.clearEditTexts;
import static com.inventorytracker.utils.EditTextUtils.getTextFromEditText;
import static com.inventorytracker.utils.EditTextUtils.getTextToDecimal;
import static com.inventorytracker.utils.StringHelper.isStringNumeric;
import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.math.NumberUtils.createBigDecimal;
import static org.apache.commons.lang3.math.NumberUtils.createInteger;


public class NewProductFragment extends Fragment implements View.OnClickListener {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection("products");
    private CollectionReference packageRef = db.collection("packages");
    //ui
    private TextInputLayout productPrice, productStock, productName;
    private RadioButton VAT95;
    private TextView barcodeValue, currentStock;
    private Context context;
    private NavController controller;
    //vars
    private Product product;
    private DocumentReference productReference;
    private ProductViewModel viewModel;
    private Resources resources;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_new_fragment, container, false);
        resources = getResources();
        viewModel = new ViewModelProvider(getActivity()).get(ProductViewModel.class);
        context = getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
        barcodeValue = view.findViewById(R.id.barcodeValue);
        productPrice = view.findViewById(R.id.addProductPrice);
        productName = view.findViewById(R.id.addProductTitle);
        productStock = view.findViewById(R.id.addProductNumber);
        currentStock = view.findViewById(R.id.addProductStock);
        VAT95 = view.findViewById(R.id.VAT95);
        Button addItem = view.findViewById(R.id.addProductAddItem);
        addItem.setOnClickListener(this);
        Button productRescan = view.findViewById(R.id.addProductRescan);
        productRescan.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PRODUCT_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String Barcode = barcode.displayValue;
                    viewModel.setProductBarcode(Barcode);
                    barcodeValue.setText(Barcode);
                    productRef.whereEqualTo("productBarcode", Barcode).get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            viewModel.setExists(true);
                            try {
                                product = queryDocumentSnapshots.getDocuments().get(0).toObject(Product.class);
                                productReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
                                productName.getEditText().setText(String.format(resources.getString(R.string.ProductInfoName), product.getProductName()));
                                barcodeValue.setText(product.getProductBarcode());
                                currentStock.setText(String.format(resources.getString(R.string.ProductStock), product.getProductStock()));
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                            }
                        } else {
                            viewModel.setExists(true);
                            clearEditTexts(productName.getEditText(), productName.getEditText());
                        }
                    });
                } else {
                    barcodeValue.setText(resources.getString(R.string.BarcodeReadFailed));
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addProductRescan:
                startActivityForResult(new Intent(getContext(), BarcodeCaptureActivity.class), PRODUCT_SCAN);
                break;
            case R.id.addProductAddItem:
                String productName = getTextFromEditText(this.productName.getEditText());
                String productPrice = getTextToDecimal(this.productPrice.getEditText());
                String productStock = getTextToDecimal(this.productStock.getEditText());
                BigDecimal price = ZERO;
                Integer stock = 0;
                if (isStringNumeric(productPrice)) {
                    price = createBigDecimal(productPrice).setScale(2, BigDecimal.ROUND_CEILING);
                }
                if (isStringNumeric(productStock)) {
                    stock = createInteger(productStock);
                }
                if (!viewModel.getExists().getValue()) {
                    if (isNotBlank(productName) && stock >= 0 && price.doubleValue() > 0.0) {
                        BigDecimal VATValue = VAT22;
                        if (VAT95.isChecked()) {
                            VATValue = BigDecimalUtil.VAT95;
                        }
                        viewModel.addProductInfo(viewModel.getProductBarcodeLive().getValue(), productName, stock, price, VATValue);
                        controller.navigate(R.id.action_addProductFragment_to_productSupplierFragment);
                    } else {
                        Toast.makeText(context, resources.getString(R.string.InvalidData), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    updateStock(stock);
                }
                break;
        }
    }

    private void updateStock(final int newItems) {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot productSnapshot = transaction.get(productReference);
            Product product = productSnapshot.toObject(Product.class);
            Integer newStock = newItems;
            if (productReference != null && product != null) {
                if (newStock == 0) {
                    newStock = product.getProductSKU();
                }
                transaction.update(productReference, "productStock", product.getProductStock() + newStock);
            }
            return null;
        }).addOnSuccessListener(aVoid -> Toast.makeText(context, resources.getString(R.string.StockUpdated), Toast.LENGTH_SHORT).show());
    }
}
