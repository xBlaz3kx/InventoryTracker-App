package com.inventorytracker.products.data;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@IgnoreExtraProperties
public class ProductReport {
    @ServerTimestamp
    private Date reportTimestamp;
    private DocumentReference supplierOrderReference;
    private ArrayList<DocumentReference> packageReferences;
    private Map<String, Integer> packagesTotal;
    private Map<String, Integer> productsTotal;

    public ProductReport() {
    }

    public ProductReport(ArrayList<DocumentReference> packageReferences, Map<String, Integer> packagesTotal, Map<String, Integer> productsTotal, DocumentReference supplierOrderReference) {
        this.packageReferences = packageReferences;
        this.packagesTotal = packagesTotal;
        this.productsTotal = productsTotal;
        this.supplierOrderReference = supplierOrderReference;
    }

    public Date getReportTimestamp() {
        return reportTimestamp;
    }

    public void setReportTimestamp(Date reportTimestamp) {
        this.reportTimestamp = reportTimestamp;
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

    public void setPackageReferences(ArrayList<DocumentReference> packageReferences) {
        this.packageReferences = packageReferences;
    }

    public Map<String, Integer> getPackagesTotal() {
        return packagesTotal;
    }

    public void setPackagesTotal(Map<String, Integer> packagesTotal) {
        this.packagesTotal = packagesTotal;
    }

    public Map<String, Integer> getProductsTotal() {
        return productsTotal;
    }

    public void setProductsTotal(Map<String, Integer> productsTotal) {
        this.productsTotal = productsTotal;
    }
}
