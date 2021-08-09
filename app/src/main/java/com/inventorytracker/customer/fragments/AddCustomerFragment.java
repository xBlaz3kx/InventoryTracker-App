package com.inventorytracker.customer.fragments;

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

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inventorytracker.R;
import com.inventorytracker.customer.data.Customer;

import static com.inventorytracker.utils.EditTextUtils.clearEditTexts;
import static com.inventorytracker.utils.EditTextUtils.getTextFromEditText;
import static com.inventorytracker.utils.StringHelper.isStringNumeric;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class AddCustomerFragment extends Fragment implements View.OnClickListener {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference customerRef = db.collection("customers");
    //ui
    private TextInputLayout CustomerName, CustomerSurname, CustomerAddress, CustomerPost, ID_DDV, CustomerFirmName;
    //vars
    private String customerName, customerSurname, customerAddress, customerPost, customerFirm, customerDDV;
    private boolean userExists = false;
    private Resources resources;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        resources = getResources();
        return inflater.inflate(R.layout.customer_add_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CustomerName = view.findViewById(R.id.customerName);
        CustomerSurname = view.findViewById(R.id.customerSurname);
        CustomerAddress = view.findViewById(R.id.customerAddress);
        CustomerPost = view.findViewById(R.id.Posta);
        ID_DDV = view.findViewById(R.id.ID_DDV);
        CustomerFirmName = view.findViewById(R.id.firmName);
        Button addCustomer = view.findViewById(R.id.addCustomer);
        addCustomer.setOnClickListener(this);
    }

    //clear fields
    private void clear() {
        clearEditTexts(CustomerName.getEditText(), CustomerSurname.getEditText(), CustomerAddress.getEditText(), CustomerFirmName.getEditText(), CustomerPost.getEditText(), ID_DDV.getEditText());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addCustomer) {
            customerName = getTextFromEditText(CustomerName.getEditText());
            customerSurname = getTextFromEditText(CustomerSurname.getEditText());
            customerAddress = getTextFromEditText(CustomerAddress.getEditText());
            customerPost = getTextFromEditText(CustomerPost.getEditText());
            customerFirm = getTextFromEditText(CustomerFirmName.getEditText());
            customerDDV = getTextFromEditText(ID_DDV.getEditText());
            if (isAnyBlank(customerName, customerSurname, customerAddress, customerSurname, customerAddress, customerPost)
                    || (customerDDV.length() != 8 && !isStringNumeric(customerDDV))) {
                Toast.makeText(getContext(), resources.getString(R.string.InvalidData), Toast.LENGTH_SHORT).show();
            } else {
                if (isNotBlank(customerDDV)) {
                    customerRef.whereEqualTo("id_DDV", customerDDV).limit(1).addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty()) {
                            userExists = queryDocumentSnapshots.getDocuments().get(0).exists();
                        } else userExists = false;
                        if (!userExists) {
                            customerRef.document().set(new Customer(customerFirm, customerName, customerSurname, customerAddress, customerPost, customerDDV))
                                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), resources.getString(R.string.CustomerAdded), Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e1 -> Toast.makeText(getContext(), resources.getString(R.string.DatabaseError), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(getContext(), resources.getString(R.string.CustomerAlreadyExists), Toast.LENGTH_SHORT).show();
                        }
                        clear();
                    });
                }
            }
        }
    }
}
