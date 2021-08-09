package com.inventorytracker.discounts.fragments;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.inventorytracker.R;
import com.inventorytracker.discounts.adapters.DiscountAdapter;
import com.inventorytracker.discounts.data.Discount;

import java.util.ArrayList;
import java.util.HashMap;

import static com.inventorytracker.utils.DataStructureUtil.insertIntoArray;
import static com.inventorytracker.utils.UIGenerics.createOptions;
import static com.inventorytracker.utils.UIGenerics.setFirestoreAdapter;


public class DiscountManageFragment extends Fragment implements DiscountAdapter.onClickListener, View.OnClickListener {
    //variables
    private String UID;
    private ArrayList<DocumentReference> discounts = new ArrayList<>();
    private DiscountAdapter adapter;
    private NavController controller;
    //database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference discountRef = db.collection("discounts");
    private Resources resources;

    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        resources = getResources();
        try {
            UID = getArguments().getString("UID");
            Query query = discountRef.whereEqualTo("sellerID", UID).orderBy("discountCreationDate", Query.Direction.DESCENDING);
            adapter = new DiscountAdapter(createOptions(query, Discount.class));
            adapter.setListener((doc, position) -> insertIntoArray(discounts, doc.getReference()));
        } catch (Exception e) {
            UID = "";
        }
        return inflater.inflate(R.layout.discountmanager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ui
        RecyclerView discountList = view.findViewById(R.id.discountList);
        controller = Navigation.findNavController(view);
        Button clear = view.findViewById(R.id.clearList);
        Button discountCommit = view.findViewById(R.id.discountCommit);
        Button discountMake = view.findViewById(R.id.addDiscount);
        clear.setOnClickListener(this);
        discountCommit.setOnClickListener(this);
        discountMake.setOnClickListener(this);
        setFirestoreAdapter(discountList, adapter, getContext());
    }

    private void updateStatuses() {
        try {
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                ArrayList<DocumentSnapshot> discountSnapshots = new ArrayList<>();
                HashMap<DocumentReference, Discount> map = new HashMap<>();
                for (DocumentReference discount : discounts) {
                    DocumentSnapshot discountSnap = transaction.get(discount);
                    discountSnapshots.add(discountSnap);
                    map.put(discount, discountSnap.toObject(Discount.class));
                }
                for (DocumentReference discountRef : discounts) {
                    try {
                        transaction.update(discountRef, "getDiscountActive", !map.get(discountRef).getDiscountActive());
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                }
                return null;
            }).addOnFailureListener(e -> Toast.makeText(getContext(), resources.getString(R.string.UpdateError), Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(aVoid -> {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), resources.getString(R.string.Updated), Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Crashlytics.logException(e);
            Toast.makeText(getContext(), resources.getString(R.string.Error), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(DocumentSnapshot doc, int position) {
        insertIntoArray(discounts, doc.getReference());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearList:
                if (!discounts.isEmpty()) {
                    discounts.clear();
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.addDiscount:
                Bundle args = new Bundle();
                args.putString("UID", UID);
                controller.navigate(R.id.action_discountManagerFragment_to_discountMakeFragment, args);
                break;
            case R.id.discountCommit:
                updateStatuses();
                break;
        }
    }
}
