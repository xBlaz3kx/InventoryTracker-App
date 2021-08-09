package com.inventorytracker.products.fragments.product;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;

import java.math.BigDecimal;

import static com.inventorytracker.utils.BigDecimalUtil.VAT22;
import static java.math.BigDecimal.ZERO;

public class ProductViewModel extends AndroidViewModel {

    private MutableLiveData<String> productBarcodeLive = new MutableLiveData<>("");
    private MutableLiveData<String> productNameLive = new MutableLiveData<>("");
    private MutableLiveData<Integer> productStockLive = new MutableLiveData<>(0);
    private MutableLiveData<BigDecimal> productVATLive = new MutableLiveData<>(VAT22);
    private MutableLiveData<DocumentReference> supplierReferenceLive = new MutableLiveData<>();
    private MutableLiveData<BigDecimal> productPriceLive = new MutableLiveData<>(ZERO);
    private MutableLiveData<Boolean> exists = new MutableLiveData<>(true);

    public ProductViewModel(@NonNull Application application) {
        super(application);
    }

    public void addProductInfo(String productBarcode, String productName, Integer productStock, BigDecimal productPrice, BigDecimal productVat) {
        setProductBarcode(productBarcode);
        setProductNameLive(productName);
        setProductStockLive(productStock);
        setProductPriceLive(productPrice);
        setProductVATLive(productVat);
    }

    public void setSupplierReference(DocumentReference supplierReference) {
        supplierReferenceLive.setValue(supplierReference);
    }

    public void setProductStock(Integer stock) {
        productStockLive.setValue(stock);
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcodeLive.setValue(productBarcode);
    }

    public void setProductBarcodeLive(String productBarcodeLive) {
        this.productBarcodeLive.setValue(productBarcodeLive);
    }

    public void setProductNameLive(String productNameLive) {
        this.productNameLive.setValue(productNameLive);
    }

    public void setProductStockLive(Integer productStockLive) {
        this.productStockLive.setValue(productStockLive);
    }

    public void setProductVATLive(BigDecimal productVATLive) {
        this.productVATLive.setValue(productVATLive);
    }

    public void setSupplierReferenceLive(DocumentReference supplierReferenceLive) {
        this.supplierReferenceLive.setValue(supplierReferenceLive);
    }

    public void setProductPriceLive(BigDecimal productPriceLive) {
        this.productPriceLive.setValue(productPriceLive);
    }

    public void setExists(Boolean exists) {
        this.exists.setValue(exists);
    }

    public MutableLiveData<Boolean> getExists() {
        return exists;
    }

    public MutableLiveData<String> getProductBarcodeLive() {
        return productBarcodeLive;
    }


    public MutableLiveData<String> getProductNameLive() {
        return productNameLive;
    }

    public MutableLiveData<Integer> getProductStockLive() {
        return productStockLive;
    }

    public MutableLiveData<BigDecimal> getProductVATLive() {
        return productVATLive;
    }

    public MutableLiveData<DocumentReference> getSupplierReferenceLive() {
        return supplierReferenceLive;
    }

    public MutableLiveData<BigDecimal> getProductPriceLive() {
        return productPriceLive;
    }
}
