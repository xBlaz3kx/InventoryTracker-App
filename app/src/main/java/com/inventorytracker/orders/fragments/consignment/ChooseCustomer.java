package com.inventorytracker.orders.fragments.consignment;

import android.content.res.Resources;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inventorytracker.R;
import com.inventorytracker.customer.adapters.FilterCustomers;
import com.inventorytracker.customer.data.Customer;
import com.inventorytracker.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.inventorytracker.utils.Constants.CUSTOMER;
import static com.inventorytracker.utils.Constants.CUSTOMER_ID;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

public class ChooseCustomer extends Fragment {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference customerRef = db.collection("customers");
    //ui
    private RecyclerView CustomerList;
    private NavController controller;
    private FilterCustomers adapter;
    //var
    private String UID, sellerID;
    private Resources resources;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        resources = getResources();
        adapter = new FilterCustomers(new ArrayList<>(), new HashMap<>());
        customerRef.orderBy("firmName", Query.Direction.ASCENDING).addSnapshotListener((queryDocumentSnapshots, e) -> queryDocumentSnapshots.getDocumentChanges().forEach(documentChange -> {
            switch (documentChange.getType()) {
                case ADDED:
                    adapter.addCustomer(documentChange.getDocument());
                    break;
                case REMOVED:
                    adapter.removeCustomer(documentChange.getDocument());
                    break;
                case MODIFIED:
                    adapter.removeCustomer(documentChange.getDocument());
                    adapter.addCustomer(documentChange.getDocument());
                    break;
            }
        }));
        try {
            UID = getArguments().getString(Constants.UID);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        if (isNotBlank(UID)) {
            db.collection("sellers").whereEqualTo("ID", UID).get().addOnSuccessListener(queryDocumentSnapshots -> sellerID = queryDocumentSnapshots.getDocuments().get(0).getId());
        }
        return inflater.inflate(R.layout.customer_choose_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
        CustomerList = view.findViewById(R.id.custList);
        TextInputLayout search = view.findViewById(R.id.customerSearch);
        adapterSetup();
        search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = normalizeSpace(s.toString());
                if (isNoneBlank(str)) {
                    adapter.getFilter().filter(str);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void adapterSetup() {
        CustomerList.setAdapter(adapter);
        CustomerList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            Customer customer = documentSnapshot.toObject(Customer.class);
            String ID = documentSnapshot.getId();
            String sCustomer = customer.getFirmName();
            if (isNoneBlank(sCustomer, UID)) {
                sCustomer = String.format(Locale.getDefault(), "%s %s", customer.getCustomerName(), customer.getCustomerSurname());
            }
            if (isNoneBlank(ID, UID)) {
                //parse info
                Bundle bundle = new Bundle();
                bundle.putString(Constants.UID, UID);
                bundle.putString(CUSTOMER_ID, ID);
                bundle.putString(CUSTOMER, sCustomer);
                controller.navigate(R.id.action_chooseCustomer_to_consignmentAdditionalInfo, bundle);
            } else {
                Toast.makeText(getContext(), resources.getString(R.string.Error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
