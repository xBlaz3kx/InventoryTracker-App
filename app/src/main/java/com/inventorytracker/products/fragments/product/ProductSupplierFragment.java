package com.inventorytracker.products.fragments.product;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inventorytracker.R;
import com.inventorytracker.products.data.Product;
import com.inventorytracker.suppliers.Supplier;
import com.inventorytracker.suppliers.SupplierAdapter;

import static com.inventorytracker.utils.UIGenerics.createOptions;
import static com.inventorytracker.utils.UIGenerics.setFirestoreAdapter;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;


public class ProductSupplierFragment extends Fragment implements View.OnClickListener {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference supplierRef = db.collection("suppliers");
    private CollectionReference productRef = db.collection("products");
    //ui
    private NavController controller;
    private SupplierAdapter adapter;
    private ProductViewModel viewModel;
    private Resources resources;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.productsupplierfragment, container, false);
        resources = getResources();
        viewModel = new ViewModelProvider(getActivity()).get(ProductViewModel.class);
        adapter = new SupplierAdapter(createOptions(supplierRef.orderBy("supplierName"), Supplier.class));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
        RecyclerView supplierList = view.findViewById(R.id.supplierList);
        Button next = view.findViewById(R.id.productSupplier_addSupplier);
        next.setOnClickListener(this);
        setFirestoreAdapter(supplierList, adapter, getContext());
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.productSupplier_addSupplier) {
            String productBarcode = viewModel.getProductBarcodeLive().getValue();
            String productName = viewModel.getProductNameLive().getValue();
            DocumentReference supplierReference = viewModel.getSupplierReferenceLive().getValue();
            if (isNoneBlank(productBarcode, productName) && supplierReference != null) {
                uploadProduct();
            } else {
                Toast.makeText(getContext(), R.string.InvalidData, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadProduct() {
        try {
            Product product = new Product(viewModel.getProductBarcodeLive().getValue(),
                    viewModel.getProductNameLive().getValue(),
                    viewModel.getProductStockLive().getValue(),
                    viewModel.getProductPriceLive().getValue(),
                    viewModel.getProductVATLive().getValue().doubleValue(),
                    viewModel.getSupplierReferenceLive().getValue());
            productRef.document(viewModel.getProductBarcodeLive().getValue()).set(product)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), resources.getString(R.string.ProductAdded), Toast.LENGTH_SHORT).show();
                        controller.navigate(R.id.mainMenuFragment);
                    }).addOnFailureListener(e -> Toast.makeText(getContext(), resources.getString(R.string.Error), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
