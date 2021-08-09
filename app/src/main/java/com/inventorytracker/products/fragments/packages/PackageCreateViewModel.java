package com.inventorytracker.products.fragments.packages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

class PackageCreateViewModel extends AndroidViewModel {

    private MutableLiveData<BigDecimal> weightLive = new MutableLiveData<>(ZERO);
    private MutableLiveData<BigDecimal> heightLive = new MutableLiveData<>(ZERO);
    private MutableLiveData<BigDecimal> depthLive = new MutableLiveData<>(ZERO);
    private MutableLiveData<BigDecimal> widthLive = new MutableLiveData<>(ZERO);
    private MutableLiveData<String> productBarcode = new MutableLiveData<>("");
    private MutableLiveData<String> packageBarcode = new MutableLiveData<>("");
    private MutableLiveData<DocumentReference> productReference = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> productExists = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> packageExists = new MutableLiveData<>(true);
    private MutableLiveData<Integer> numProducts = new MutableLiveData<>(1);

    public PackageCreateViewModel(@NonNull Application application) {
        super(application);
    }

    public void setProductExists(Boolean productExists) {
        this.productExists.setValue(productExists);
    }

    public void setPackageExists(Boolean packageExists) {
        this.packageExists.setValue(packageExists);
    }

    public MutableLiveData<Boolean> getProductExists() {
        return productExists;
    }

    public MutableLiveData<Boolean> getPackageExists() {
        return packageExists;
    }

    public MutableLiveData<BigDecimal> getWeightLive() {
        return weightLive;
    }

    public void setWeightLive(BigDecimal weightLive) {
        this.weightLive.setValue(weightLive);
    }

    public MutableLiveData<BigDecimal> getHeightLive() {
        return heightLive;
    }

    public void setHeightLive(BigDecimal heightLive) {
        this.heightLive.setValue(heightLive);
    }

    public MutableLiveData<BigDecimal> getDepthLive() {
        return depthLive;
    }

    public void setDepthLive(BigDecimal depthLive) {
        this.depthLive.setValue(depthLive);
    }

    public MutableLiveData<BigDecimal> getWidthLive() {
        return widthLive;
    }

    public void setWidthLive(BigDecimal widthLive) {
        this.widthLive.setValue(widthLive);
    }

    public MutableLiveData<String> getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode.setValue(productBarcode);
    }

    public MutableLiveData<String> getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode.setValue(packageBarcode);
    }

    public MutableLiveData<DocumentReference> getProductReference() {
        return productReference;
    }

    public void setProductReference(DocumentReference productReference) {
        this.productReference.setValue(productReference);
    }

    public MutableLiveData<Integer> getNumProducts() {
        return numProducts;
    }

    public void setNumProducts(Integer numProducts) {
        this.numProducts.setValue(numProducts);
    }
}
