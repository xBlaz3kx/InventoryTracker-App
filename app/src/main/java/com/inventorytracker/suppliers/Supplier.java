package com.inventorytracker.suppliers;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;

@IgnoreExtraProperties
public class Supplier {

    private String supplierName, supplierFirmName, supplierAddress, supplierPost, supplierCountry;
    private ArrayList<DocumentReference> supplierProducts;
    private Date lastOrdered;
    private String supplierOrderTemplate;
    private String numberVAT;

    public Supplier() {
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setSupplierFirmName(String supplierFirmName) {
        this.supplierFirmName = supplierFirmName;
    }

    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
    }

    public void setSupplierPost(String supplierPost) {
        this.supplierPost = supplierPost;
    }

    public void setSupplierCountry(String supplierCountry) {
        this.supplierCountry = supplierCountry;
    }

    public void setSupplierProducts(ArrayList<DocumentReference> supplierProducts) {
        this.supplierProducts = supplierProducts;
    }

    public void setLastOrdered(Date lastOrdered) {
        this.lastOrdered = lastOrdered;
    }

    public void setSupplierOrderTemplate(String supplierOrderTemplate) {
        this.supplierOrderTemplate = supplierOrderTemplate;
    }

    public void setNumberVAT(String numberVAT) {
        this.numberVAT = numberVAT;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierFirmName() {
        return supplierFirmName;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public String getSupplierPost() {
        return supplierPost;
    }

    public String getSupplierCountry() {
        return supplierCountry;
    }

    public ArrayList<DocumentReference> getSupplierProducts() {
        return supplierProducts;
    }

    public Date getLastOrdered() {
        return lastOrdered;
    }

    public String getSupplierOrderTemplate() {
        return supplierOrderTemplate;
    }

    public String getNumberVAT() {
        return numberVAT;
    }
}
