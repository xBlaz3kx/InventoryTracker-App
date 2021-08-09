package com.inventorytracker.products.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inventorytracker.R;
import com.inventorytracker.camera.BarcodeCaptureActivity;
import com.inventorytracker.products.adapters.FilterProducts;

import java.util.ArrayList;

import static com.inventorytracker.utils.Constants.DOCUMENT_PATH;
import static com.inventorytracker.utils.Constants.PRODUCT_SCAN;
import static com.inventorytracker.utils.Constants.UPDATE_BARCODE;
import static com.inventorytracker.utils.UIGenerics.setRecAdapter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

public class InventoryFragment extends Fragment implements View.OnClickListener {
    //database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection("products");
    //ui
    private FilterProducts adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new FilterProducts(new ArrayList<>());
        adapter.setMenuInterface(docPath -> {
            Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
            intent.putExtra(DOCUMENT_PATH, docPath);
            startActivityForResult(intent, UPDATE_BARCODE);
        });
        productRef.addSnapshotListener((queryDocumentSnapshots, e) -> queryDocumentSnapshots.getDocumentChanges().forEach(documentChange -> {
            switch (documentChange.getType()) {
                case MODIFIED:
                    adapter.removeProduct(documentChange.getDocument());
                    adapter.addProduct(documentChange.getDocument());
                    break;
                case REMOVED:
                    adapter.removeProduct(documentChange.getDocument());
                    break;
                case ADDED:
                    adapter.addProduct(documentChange.getDocument());
                    break;
            }
        }));
        return inflater.inflate(R.layout.product_inventory_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView productList = view.findViewById(R.id.productList);
        FloatingActionButton searchByScan = view.findViewById(R.id.productSearchScan);
        TextInputLayout search = view.findViewById(R.id.productSearch);
        searchByScan.setOnClickListener(this);
        search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = normalizeSpace(s.toString());
                if (isNotBlank(str)) {
                    adapter.getFilter().filter(str);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setRecAdapter(productList, adapter, getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PRODUCT_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String Barcode = barcode.displayValue;
                    adapter.getFilter().filter(Barcode);
                }
            }
        } else if (requestCode == UPDATE_BARCODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String Barcode = barcode.displayValue;
                    db.collection("products").whereEqualTo("productBarcode", Barcode).addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() == 0) {
                            db.document(data.getStringExtra(DOCUMENT_PATH))
                                    .update("productBarcode", Barcode)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), getResources().getText(R.string.Updated), Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(x -> Toast.makeText(getContext(), getResources().getText(R.string.DatabaseError), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(getContext(), getResources().getText(R.string.BarcodeExists), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.productSearchScan) {
            startActivityForResult(new Intent(getContext(), BarcodeCaptureActivity.class), PRODUCT_SCAN);
        }
    }
}
