package com.inventorytracker.orders.fragments.consignment;

import android.content.Context;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.inventorytracker.R;
import com.inventorytracker.orders.adapters.consignment.ConsignmentClosingAdapter;
import com.inventorytracker.orders.data.consignment.ConsignmentOrder;
import com.inventorytracker.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.inventorytracker.utils.Constants.CONSIGNMENT_ID;
import static com.inventorytracker.utils.Constants.ORDER_ID;
import static com.inventorytracker.utils.UIGenerics.setRecAdapter;

public class FinishConsignment extends Fragment implements View.OnClickListener {
    //db
    private FirebaseFunctions functions = FirebaseFunctions.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference consignmentRef = db.collection("consignments");
    //ui
    private ConsignmentClosingAdapter adapter;
    private NavController controller;
    private Context context;
    //vars
    private String sellerID, orderID = "";
    private ArrayList<DocumentReference> products = new ArrayList<>();
    private Map<String, Integer> consignmentProductsSold = new HashMap<>();
    private Map<DocumentReference, String> productRef = new HashMap<>();
    private Resources resources;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.consignment_finish_fragment, container, false);
        context = getContext();
        resources = getResources();
        try {
            sellerID = getArguments().getString(Constants.UID);
            orderID = getArguments().getString(ORDER_ID);
            adapter = new ConsignmentClosingAdapter(products, consignmentProductsSold, new HashMap<>());
            adapter.setData((position, numSold, productsConsigned) -> {
                String bc = productRef.get(adapter.getProducts().get(position));
                if (productsConsigned >= numSold) {
                    consignmentProductsSold.put(bc, numSold);
                }
            });
            consignmentRef.document(orderID).get().addOnSuccessListener(documentSnapshot -> {
                ConsignmentOrder order = documentSnapshot.toObject(ConsignmentOrder.class);
                adapter.setProductsInOrder(order.getConsignmentProductNumbers());
                products = order.getConsignmentProductList();
                for (DocumentReference product : products) {
                    product.get().addOnSuccessListener(documentSnapshot1 -> {
                        String barcode = documentSnapshot1.getString("productBarcode");
                        consignmentProductsSold.put(barcode, 0);
                        productRef.put(product, barcode);
                    });
                }
            });

        } catch (Exception e) {
            Crashlytics.logException(e);
            Toast.makeText(getContext(), resources.getString(R.string.SomethingWentWrong), Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView productList = view.findViewById(R.id.consignment_productList);
        Button closeConsignment = view.findViewById(R.id.close_consignment);
        closeConsignment.setOnClickListener(this);
        setRecAdapter(productList, adapter, context);
        controller = Navigation.findNavController(view);
    }

    //update consignment order info with input from user (number of products sold)
    private void closeConsignment() {
        if (!consignmentProductsSold.isEmpty()) {
            consignmentRef.document(orderID).update("consignmentProductsSold", consignmentProductsSold)
                    .addOnFailureListener(e -> {
                        Crashlytics.logException(e);
                        Toast.makeText(getContext(), resources.getString(R.string.ConsignmentClosingError), Toast.LENGTH_SHORT).show();
                    }).addOnSuccessListener(aVoid -> closeConsignment(orderID).addOnSuccessListener(s ->
                    Toast.makeText(getContext(), resources.getString(R.string.ConsignmentClosed), Toast.LENGTH_SHORT).show()));
        }
    }

    private Task<String> closeConsignment(String consignmentID) {
        Map<String, Object> data = new HashMap<>();
        data.put(CONSIGNMENT_ID, consignmentID);
        return functions.getHttpsCallable("closeConsignment").call(data).continueWith(task -> {
            if (task.getResult() != null) {
                return task.getResult().getData().toString();
            } else {
                throw new Exception(resources.getString(R.string.SomethingWentWrong));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_consignment) {
            closeConsignment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.UID, sellerID);
            controller.popBackStack(R.id.consignmentChooseAction, true);
        }
    }
}
