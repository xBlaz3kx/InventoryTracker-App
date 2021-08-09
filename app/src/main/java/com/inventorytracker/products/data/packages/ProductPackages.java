package com.inventorytracker.products.data.packages;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class ProductPackages {
    private Integer packageProductNumber, receivedPackages;
    private Double packageWeight;
    private Double packageHeight, packageDepth, packageWidth;
    private String packageBarcode, packageContent;
    private DocumentReference productReference;
    private Date lastReceived;

    public ProductPackages() {
    }

    public ProductPackages(Integer packageProductNumber, String packageBarcode, DocumentReference productReference) {
        this.packageProductNumber = packageProductNumber;
        this.packageBarcode = packageBarcode;
        this.productReference = productReference;
        this.receivedPackages = 0;
        this.packageDepth = 0.0;
        this.packageHeight = 0.0;
        this.packageWeight = 0.0;
        this.packageWidth = 0.0;
    }

    public Integer getPackageProductNumber() {
        return packageProductNumber;
    }

    public void setPackageProductNumber(Integer packageProductNumber) {
        this.packageProductNumber = packageProductNumber;
    }

    public Integer getReceivedPackages() {
        return receivedPackages;
    }

    public void setReceivedPackages(Integer receivedPackages) {
        this.receivedPackages = receivedPackages;
    }

    public Double getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(Double packageWeight) {
        this.packageWeight = packageWeight;
    }

    public Double getPackageHeight() {
        return packageHeight;
    }

    public void setPackageHeight(Double packageHeight) {
        this.packageHeight = packageHeight;
    }

    public Double getPackageDepth() {
        return packageDepth;
    }

    public void setPackageDepth(Double packageDepth) {
        this.packageDepth = packageDepth;
    }

    public Double getPackageWidth() {
        return packageWidth;
    }

    public void setPackageWidth(Double packageWidth) {
        this.packageWidth = packageWidth;
    }

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public String getPackageContent() {
        return packageContent;
    }

    public void setPackageContent(String packageContent) {
        this.packageContent = packageContent;
    }

    public DocumentReference getProductReference() {
        return productReference;
    }

    public void setProductReference(DocumentReference productReference) {
        this.productReference = productReference;
    }

    public Date getLastReceived() {
        return lastReceived;
    }

    public void setLastReceived(Date lastReceived) {
        this.lastReceived = lastReceived;
    }
}
