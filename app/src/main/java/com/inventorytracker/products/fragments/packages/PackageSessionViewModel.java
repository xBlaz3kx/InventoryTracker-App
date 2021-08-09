package com.inventorytracker.products.fragments.packages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inventorytracker.products.data.packages.ProductPackages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.inventorytracker.utils.DataStructureUtil.insertIntoArray;
import static com.inventorytracker.utils.DataStructureUtil.insertOrAdd;

public class PackageSessionViewModel extends AndroidViewModel {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference packageRef = db.collection("packages");
    //data
    private MutableLiveData<List<ProductPackages>> productPackagesLive = new MutableLiveData<>();
    private MutableLiveData<List<DocumentReference>> packageReferencesLive = new MutableLiveData<>();
    private MutableLiveData<HashMap<String, Integer>> packageCountLive = new MutableLiveData<>();
    private MutableLiveData<HashMap<String, Integer>> totalProductsLive = new MutableLiveData<>();
    private MutableLiveData<List<String>> packageBarcodesLive = new MutableLiveData<>();
    private ArrayList<ProductPackages> productPackages = new ArrayList<>();
    private ArrayList<String> packageBarcodes = new ArrayList<>();
    private ArrayList<DocumentReference> packageReferences = new ArrayList<>();
    private DocumentReference supplierOrderReference = null;
    private HashMap<String, Integer> packageCount = new HashMap<>();
    private HashMap<String, Integer> totalProducts = new HashMap<>();

    public PackageSessionViewModel(@NonNull Application application) {
        super(application);
    }

    public void insertPackage(final String barcode, final Integer packageCount) {
        if (!packageBarcodes.contains(barcode)) {
            insertIntoArray(packageBarcodes, barcode);
            packageRef.whereEqualTo("packageBarcode", barcode).get().addOnSuccessListener(queryDocumentSnapshots -> {
                try {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                        if (snapshot != null) {
                            DocumentReference docRef = snapshot.getReference();
                            ProductPackages pack = snapshot.toObject(ProductPackages.class);
                            if (pack != null) {
                                insertIntoArray(productPackages, pack);
                                insertIntoArray(packageReferences, docRef);
                                addPackages(barcode, packageCount);
                                productPackagesLive.setValue(productPackages);
                            }
                        }
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            });
        } else {
            addPackages(barcode, packageCount);
        }
    }

    public void removeItem(int position) {
        String item = packageBarcodes.get(position);
        packageBarcodes.remove(position);
        productPackages.remove(position);
        totalProducts.remove(item);
        packageCount.remove(item);
        packageBarcodesLive.setValue(packageBarcodes);
        productPackagesLive.setValue(productPackages);
        totalProductsLive.setValue(totalProducts);
        packageCountLive.setValue(packageCount);
    }

    public void addPackages(String barcode, Integer numPackages) {
        insertOrAdd(packageCount, barcode, numPackages);
        insertOrAdd(totalProducts, barcode, numPackages * productPackages.get(packageBarcodes.indexOf(barcode)).getPackageProductNumber());
    }

    public void addProducts(String barcode, Integer productNumber) {
        insertOrAdd(totalProducts, barcode, productNumber);
    }

    public void setProducts(ArrayList<ProductPackages> packReferences) {
        this.productPackages = packReferences;
    }

    public LiveData<List<ProductPackages>> getProductPackagesLive() {
        productPackagesLive.setValue(productPackages);
        return productPackagesLive;
    }

    public ArrayList<ProductPackages> getProductPackages() {
        return productPackages;
    }

    public ArrayList<String> getPackageBarcodes() {
        return packageBarcodes;
    }

    public MutableLiveData<List<DocumentReference>> getPackageReferencesLive() {
        packageReferencesLive.setValue(packageReferences);
        return packageReferencesLive;
    }

    public MutableLiveData<HashMap<String, Integer>> getPackageCountLive() {
        packageCountLive.setValue(packageCount);
        return packageCountLive;
    }

    public MutableLiveData<HashMap<String, Integer>> getTotalProductsLive() {
        totalProductsLive.setValue(totalProducts);
        return totalProductsLive;
    }

    public MutableLiveData<List<String>> getPackageBarcodesLive() {
        packageBarcodesLive.setValue(packageBarcodes);
        return packageBarcodesLive;
    }

    public DocumentReference getSupplierOrderReference() {
        return supplierOrderReference;
    }

    public void setSupplierOrderReference(DocumentReference supplierOrderReference) {
        this.supplierOrderReference = supplierOrderReference;
    }

    public ArrayList<DocumentReference> getPackageReferences() {
        return packageReferences;
    }

    public HashMap<String, Integer> getPackageCount() {
        return packageCount;
    }

    public HashMap<String, Integer> getTotalProducts() {
        return totalProducts;
    }

    public void clearViewModel() {
        productPackages.clear();
        packageBarcodes.clear();
        packageCount.clear();
        totalProducts.clear();
        packageBarcodesLive.setValue(packageBarcodes);
        totalProductsLive.setValue(totalProducts);
        packageCountLive.setValue(packageCount);
        packageReferencesLive.setValue(packageReferences);
        productPackagesLive.setValue(productPackages);
    }
}
