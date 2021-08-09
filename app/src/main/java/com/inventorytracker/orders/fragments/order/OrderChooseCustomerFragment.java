package com.inventorytracker.orders.fragments.order;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inventorytracker.R;
import com.inventorytracker.customer.adapters.FilterCustomers;
import com.inventorytracker.customer.data.Customer;
import com.inventorytracker.utils.Constants;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.inventorytracker.utils.Constants.CUSTOMER;
import static com.inventorytracker.utils.Constants.CUSTOMER_ID;
import static com.inventorytracker.utils.UIGenerics.setRecAdapter;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;


public class OrderChooseCustomerFragment extends Fragment {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference CustomerRef = db.collection("customers");
    private NavController controller;
    private Context context;
    private FilterCustomers adapter;
    private HashMap<Customer, DocumentSnapshot> snapshotHashMap = new HashMap<>();
    //vars
    private String UID = "";
    private Resources resources;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_choose_fragment, container, false);
        context = getContext();
        resources = getResources();
        try {
            UID = requireActivity().getIntent().getStringExtra(Constants.UID);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        adapter = new FilterCustomers(new ArrayList<>(), snapshotHashMap);
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
                controller.navigate(R.id.action_addOrderFragment_to_additionalInfoFragment, bundle);
            } else {
                Toast.makeText(context, resources.getString(R.string.Error), Toast.LENGTH_SHORT).show();
            }
        });
        CustomerRef.orderBy("firmName", Query.Direction.ASCENDING).addSnapshotListener((queryDocumentSnapshots, e) -> queryDocumentSnapshots.getDocumentChanges().forEach(documentChange -> {
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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
        //ui
        RecyclerView customerList = view.findViewById(R.id.custList);
        TextInputLayout search = view.findViewById(R.id.customerSearch);
        setRecAdapter(customerList, adapter, getContext());
        search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = StringUtils.normalizeSpace(s.toString());
                if (StringUtils.isNotBlank(str)) {
                    adapter.getFilter().filter(str);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}
