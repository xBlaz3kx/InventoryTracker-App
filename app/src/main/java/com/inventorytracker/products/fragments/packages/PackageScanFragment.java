package com.inventorytracker.products.fragments.packages;

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
import com.inventorytracker.products.data.packages.ProductPackages;

import java.util.Locale;

import static com.inventorytracker.utils.Constants.IS_REPORT;
import static com.inventorytracker.utils.Constants.PACKAGE_SCAN;
import static com.inventorytracker.utils.EditTextUtils.getTextToDecimal;
import static com.inventorytracker.utils.StringHelper.isStringNumeric;
import static org.apache.commons.lang3.math.NumberUtils.createInteger;

public class PackageScanFragment extends Fragment implements View.OnClickListener {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference packageRef = db.collection("packages");
    //var
    private boolean exists = false, report = false;
    private String BarcodeValue;
    private Integer numberOfPackages = 0;
    private DocumentReference packRef;
    private PackageSessionViewModel viewModel;
    //ui
    private TextInputLayout numberInput;
    private TextView packagesReceived, packageContent, packageBarcode;
    private Button updateStock;
    private NavController controller;
    private ProductPackages packaging;
    private Resources resources;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resources = getResources();
        viewModel = new ViewModelProvider(getActivity()).get(PackageSessionViewModel.class);
        try {
            report = getArguments().getBoolean(IS_REPORT);
            if (report) {
                updateStock.setText(resources.getText(R.string.next));
            }
        } catch (Exception e) {
            report = false;
        }
        return inflater.inflate(R.layout.package_reception_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateStock = view.findViewById(R.id.updateStock);
        updateStock.setOnClickListener(this);
        Button rescan = view.findViewById(R.id.rescan);
        rescan.setOnClickListener(this);
        packageBarcode = view.findViewById(R.id.packageBarocde);
        packageContent = view.findViewById(R.id.packageContent);
        packagesReceived = view.findViewById(R.id.numberOfProducts);
        numberInput = view.findViewById(R.id.packageCount);
        controller = Navigation.findNavController(view);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PACKAGE_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    BarcodeValue = barcode.displayValue;
                    packageRef.whereEqualTo("packageBarcode", BarcodeValue).get().addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                            DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                            exists = snapshot.exists();
                            if (exists) {
                                updateStock.setEnabled(true);
                                packRef = snapshot.getReference();
                                packaging = snapshot.toObject(ProductPackages.class);
                                if (packaging != null) {
                                    if (packaging.getReceivedPackages() != null) {
                                        packagesReceived.setText(String.format(resources.getString(R.string.PackagesReceived), packaging.getReceivedPackages()));
                                    }
                                    packageContent.setText(packaging.getPackageContent());
                                }
                            } else {
                                Toast.makeText(getContext(), resources.getString(R.string.PacketDoesntExist), Toast.LENGTH_SHORT).show();
                                updateStock.setEnabled(false);
                            }
                            packageBarcode.setText(String.format(Locale.getDefault(), resources.getString(R.string.Barcode), BarcodeValue));
                        }
                    });
                }
            }
        }
    }

    private void updateProductStock() {
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            try {
                if (packRef != null && packaging != null) {
                    DocumentSnapshot packSnapshot = transaction.get(packRef);
                    ProductPackages productPackaging = packSnapshot.toObject(ProductPackages.class);
                    DocumentSnapshot prodSnapshot = transaction.get(packaging.getProductReference());
                    Product product = prodSnapshot.toObject(Product.class);
                    if (product != null && productPackaging != null) {
                        transaction.update(packaging.getProductReference(), "productStock", product.getProductStock() + (productPackaging.getPackageProductNumber() * numberOfPackages));
                        transaction.update(packRef, "receivedPackages", productPackaging.getReceivedPackages() + numberOfPackages);
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
            return null;
        }).addOnSuccessListener(aVoid -> Toast.makeText(getContext(), resources.getString(R.string.StockUpdated), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), resources.getString(R.string.Error), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rescan:
                startActivityForResult(new Intent(getContext(), BarcodeCaptureActivity.class), PACKAGE_SCAN);
                break;
            case R.id.updateStock:
                String num = getTextToDecimal(numberInput.getEditText());
                if (exists) {
                    if (isStringNumeric(num)) {
                        numberOfPackages = createInteger(num);
                    } else {
                        numberInput.getEditText().setText(String.format(resources.getString(R.string.PackagesReceived), numberOfPackages));
                    }
                    if (report) {
                        if (numberOfPackages >= 0) {
                            viewModel.insertPackage(BarcodeValue, numberOfPackages);
                            controller.navigate(R.id.action_packageScanFragment_to_packageSessionReport2);
                        }
                    } else {
                        if (numberOfPackages > 0) {
                            updateProductStock();
                        } else {
                            Toast.makeText(getContext(), resources.getText(R.string.ReportInvalidProductNum).toString().replace("Št. izdelkov", "Št. paketov"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }
}
