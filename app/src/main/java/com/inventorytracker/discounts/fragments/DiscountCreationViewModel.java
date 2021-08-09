package com.inventorytracker.discounts.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

import static com.inventorytracker.utils.DataStructureUtil.insertIntoArray;

public class DiscountCreationViewModel extends ViewModel {
    //vars
    private MutableLiveData<List<DocumentReference>> customerReferences = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<DocumentReference>> productReferences = new MutableLiveData<>(new ArrayList<>());
    private ArrayList<DocumentReference> productRef = new ArrayList<>();
    private ArrayList<DocumentReference> customerRef = new ArrayList<>();

    public LiveData<List<DocumentReference>> getDocuments(Integer discountType) {
        LiveData<List<DocumentReference>> ret;
        if (discountType == 1) {
            customerReferences.setValue(customerRef);
            ret = customerReferences;
        } else {
            productReferences.setValue(productRef);
            ret = productReferences;
        }
        return ret;
    }

    public void addCustomer(DocumentReference documentReference) {
        insertIntoArray(customerRef, documentReference);
        customerReferences.setValue(customerRef);
    }

    public void addProduct(DocumentReference documentReference) {
        insertIntoArray(productRef, documentReference);
        productReferences.setValue(productRef);
    }

    public MutableLiveData<List<DocumentReference>> getCustomerReferences() {
        customerReferences.setValue(customerRef);
        return customerReferences;
    }

    public MutableLiveData<List<DocumentReference>> getProductReferences() {
        customerReferences.setValue(productRef);
        return productReferences;
    }
}
