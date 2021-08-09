package com.inventorytracker.products.fragments.packages;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.inventorytracker.R;
import com.inventorytracker.camera.BarcodeCaptureActivity;
import com.inventorytracker.products.data.packages.ProductPackages;

import java.math.BigDecimal;
import java.util.Locale;

import static com.inventorytracker.utils.Constants.PACKAGE_BARCODE;
import static com.inventorytracker.utils.Constants.PACKAGE_SCAN;
import static com.inventorytracker.utils.Constants.PRODUCT_SCAN;
import static com.inventorytracker.utils.EditTextUtils.getTextToDecimal;
import static com.inventorytracker.utils.StringHelper.areStringsNumeric;
import static com.inventorytracker.utils.StringHelper.isStringNumeric;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.math.NumberUtils.createBigDecimal;
import static org.apache.commons.lang3.math.NumberUtils.createInteger;

public class PackageCreationFragment extends Fragment implements View.OnClickListener {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productsRef = db.collection("products");
    private CollectionReference packageRef = db.collection("packages");
    //UI
    private TextInputLayout packagingProductNumber, packagingWeight, packagingHeight, packagingDepth, packagingWidth;
    private Button submit;
    private TextView packBarcode, prodInPackBarcode;
    private Context context = getContext();
    PackageCreateViewModel viewModel;

    private Resources resources;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resources = getResources();
        try {
            String packageBarcode = getArguments().getString(PACKAGE_BARCODE);
            viewModel = new ViewModelProvider(getActivity()).get(PackageCreateViewModel.class);
            packBarcode.setText(packageBarcode);
            viewModel.setPackageBarcode(packageBarcode);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return inflater.inflate(R.layout.package_createpackage_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        packagingProductNumber = view.findViewById(R.id.productInPKG);
        packagingWeight = view.findViewById(R.id.packageWeight);
        packagingHeight = view.findViewById(R.id.packageHeight);
        packagingDepth = view.findViewById(R.id.packageDepth);
        packagingWidth = view.findViewById(R.id.packageWidth);
        packBarcode = view.findViewById(R.id.packBarcode);
        prodInPackBarcode = view.findViewById(R.id.packProdBarcode);
        Button rescan_product = view.findViewById(R.id.scanProduct);
        Button rescan_package = view.findViewById(R.id.packageRescan);
        submit = view.findViewById(R.id.uploadPackage);
        rescan_package.setOnClickListener(this);
        rescan_product.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PRODUCT_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    com.google.android.gms.vision.barcode.Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String productBarcode = barcode.displayValue;
                    viewModel.setProductBarcode(productBarcode);
                    prodInPackBarcode.setText(String.format(Locale.getDefault(), resources.getString(R.string.Barcode), prodInPackBarcode));
                    submit.setEnabled(true);
                    productsRef.whereEqualTo("productBarcode", productBarcode).get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty()) {
                            boolean productExists = queryDocumentSnapshots.getDocuments().get(0).exists();
                            viewModel.setProductExists(productExists);
                            try {
                                if (productExists) {
                                    viewModel.setProductReference(queryDocumentSnapshots.getDocuments().get(0).getReference());
                                }
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                            }
                        }
                    });
                }
            } else {
                prodInPackBarcode.setText(resources.getString(R.string.BarcodeReadFailed));
            }
        } else if (requestCode == PACKAGE_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    com.google.android.gms.vision.barcode.Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String packageBarcode = barcode.displayValue;
                    viewModel.setPackageBarcode(packageBarcode);
                    packBarcode.setText(packageBarcode);
                    packageRef.whereEqualTo("packageBarcode", packageBarcode).get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots != null) {
                            boolean packageExists = !queryDocumentSnapshots.getDocuments().isEmpty();
                            viewModel.setPackageExists(packageExists);
                            if (packageExists) {
                                setTexts(queryDocumentSnapshots.getDocuments().get(0).toObject(ProductPackages.class));
                            }
                        }
                    });
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void uploadPackage(@NonNull ProductPackages packaging) {
        packageRef.document(packaging.getPackageBarcode()).set(packaging)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), resources.getString(R.string.PackageMade), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), resources.getString(R.string.DatabaseError), Toast.LENGTH_SHORT).show());
    }

    private void updateProductLU(final String packageBarcode, final String field, final BigDecimal value) {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            Query snapshots = packageRef.whereEqualTo("packageBarcode", packageBarcode);
            try {
                if (snapshots.get().getResult() != null) {
                    DocumentSnapshot documentReference = snapshots.get().getResult().getDocuments().get(0);
                    ProductPackages packaging = documentReference.toObject(ProductPackages.class);
                    if (packaging != null) {
                        transaction.update(documentReference.getReference(), field, value);
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
            return null;
        }).addOnSuccessListener(aVoid -> Toast.makeText(context, resources.getString(R.string.Updated), Toast.LENGTH_SHORT).show());
    }

    private void setTexts(ProductPackages packaging) {
        try {
            packagingProductNumber.getEditText().setText(String.format(Locale.getDefault(), "%d", packaging.getPackageProductNumber()));
            packagingDepth.getEditText().setText(String.format(Locale.getDefault(), "%.3f", packaging.getPackageDepth()));
            packagingHeight.getEditText().setText(String.format(Locale.getDefault(), "%.3f", packaging.getPackageHeight()));
            packagingWeight.getEditText().setText(String.format(Locale.getDefault(), "%.3f", packaging.getPackageWeight()));
            packagingWidth.getEditText().setText(String.format(Locale.getDefault(), "%.3f", packaging.getPackageWidth()));
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void getInputData() {
        String height = getTextToDecimal(packagingHeight.getEditText());
        String depth = getTextToDecimal(packagingDepth.getEditText());
        String width = getTextToDecimal(packagingWidth.getEditText());
        String weight = getTextToDecimal(packagingWeight.getEditText());
        if (isStringNumeric(weight)) {
            viewModel.setWeightLive(createBigDecimal(weight).setScale(3, BigDecimal.ROUND_HALF_EVEN));
        }
        if (areStringsNumeric(height, depth, width)) {
            viewModel.setHeightLive(createBigDecimal(height).setScale(3, BigDecimal.ROUND_CEILING));
            viewModel.setWidthLive(createBigDecimal(width).setScale(3, BigDecimal.ROUND_CEILING));
            viewModel.setDepthLive(createBigDecimal(depth).setScale(3, BigDecimal.ROUND_CEILING));
        }
        String sNumProducts = getTextToDecimal(packagingProductNumber.getEditText());
        if (isStringNumeric(sNumProducts)) {
            viewModel.setNumProducts(createInteger(sNumProducts));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadPackage:
                getInputData();
                String packageBarcode = viewModel.getPackageBarcode().getValue();
                String productBarcode = viewModel.getProductBarcode().getValue();
                DocumentReference productRef = viewModel.getProductReference().getValue();
                BigDecimal Weight = viewModel.getWeightLive().getValue();
                BigDecimal Height = viewModel.getHeightLive().getValue();
                BigDecimal Depth = viewModel.getDepthLive().getValue();
                BigDecimal Width = viewModel.getWidthLive().getValue();
                Integer numProducts = viewModel.getNumProducts().getValue();
                boolean productExists = viewModel.getProductExists().getValue();
                boolean packageExists = viewModel.getPackageExists().getValue();

                if (isNoneBlank(packageBarcode, productBarcode) && !packageBarcode.equals(productBarcode)) {
                    if (productExists) {
                        createPackage(packageBarcode, numProducts, Height, Width, Depth, Weight, productRef);
                    } else {
                        Toast.makeText(getContext(), resources.getString(R.string.ProductUnavailable), Toast.LENGTH_SHORT).show();
                    }
                } else if (isNotBlank(packageBarcode) && packageExists) {
                    updateProductAttributes(packageBarcode, numProducts, Height, Width, Depth, Weight);
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.PleaseRescan), Toast.LENGTH_SHORT).show();
                    submit.setEnabled(false);
                }
                break;
            case R.id.scanProduct:
                startActivityForResult(new Intent(getContext(), BarcodeCaptureActivity.class), PRODUCT_SCAN);
                break;
            case R.id.packageRescan:
                startActivityForResult(new Intent(getContext(), BarcodeCaptureActivity.class), PACKAGE_SCAN);
                break;
        }
    }

    private void updateProductAttributes(String packageBarcode, Integer numProducts, BigDecimal Height, BigDecimal Width, BigDecimal Depth, BigDecimal Weight) {
        if (numProducts != null && numProducts != 1) {
            updateProductLU(packageBarcode, "packageProductNumber", BigDecimal.valueOf(numProducts));
        }
        if (Height != null && Width != null && Depth != null) {
            updateProductLU(packageBarcode, "packageHeight", Height);
            updateProductLU(packageBarcode, "packageWidth", Width);
            updateProductLU(packageBarcode, "packageDepth", Depth);
        }
        if (Weight != null) {
            updateProductLU(packageBarcode, "packageWeight", Weight);
        }
    }

    private void createPackage(String packageBarcode, Integer numProducts, BigDecimal Height, BigDecimal Width, BigDecimal Depth, BigDecimal Weight, DocumentReference productRef) {
        ProductPackages packaging = new ProductPackages(numProducts, packageBarcode, productRef);
        packaging.setPackageContent("");
        if (Weight != null) {
            packaging.setPackageWeight(Weight.doubleValue());
        }
        if (Height != null && Depth != null && Width != null) {
            packaging.setPackageHeight(Height.doubleValue());
            packaging.setPackageDepth(Depth.doubleValue());
            packaging.setPackageWidth(Width.doubleValue());
        }
        uploadPackage(packaging);
    }
}
