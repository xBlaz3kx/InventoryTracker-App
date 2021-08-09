package com.inventorytracker.products.data;


import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import static com.inventorytracker.utils.BigDecimalUtil.VAT22;

@IgnoreExtraProperties
public class Product {
    private String productBarcode, productName, productReference;
    private Integer productStock, productSKU, productsSold;
    private Double productPrice, productVATValue;
    private Date lastUpdated;
    private DocumentReference supplierReference;

    public Product() {
    }

    public Product(String productBarcode, String productName, BigDecimal productPrice, Integer productStock) {
        this.productPrice = productPrice.setScale(2, RoundingMode.CEILING).doubleValue();
        this.productName = productName;
        this.productStock = productStock;
        this.productBarcode = productBarcode;
        this.productSKU = 1;
        this.productsSold = 0;
        if (productVATValue == null) {
            this.productVATValue = VAT22.doubleValue();
        }
    }

    public Product(String productBarcode, String productName, Integer productStock, BigDecimal productPrice, Double productVATValue, DocumentReference supplierReference) {
        this.productBarcode = productBarcode;
        this.productName = productName;
        this.productStock = productStock;
        this.productPrice = productPrice.doubleValue();
        if (productVATValue == null) {
            this.productVATValue = VAT22.doubleValue();
        }
        this.productSKU = 1;
        this.productsSold = 0;
        this.productVATValue = productVATValue;
        this.supplierReference = supplierReference;
    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public Integer getProductSKU() {
        return productSKU;
    }

    public Integer getProductsSold() {
        return productsSold;
    }

    public String getProductReference() {
        return productReference;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public Double getProductVATValue() {
        return productVATValue;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public DocumentReference getSupplierReference() {
        return supplierReference;
    }


    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public void setProductSKU(Integer productSKU) {
        this.productSKU = productSKU;
    }

    public void setProductsSold(Integer productsSold) {
        this.productsSold = productsSold;
    }

    public void setProductReference(String productReference) {
        this.productReference = productReference;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }


    public void setProductVATValue(Double productVATValue) {
        this.productVATValue = productVATValue;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setSupplierReference(DocumentReference supplierReference) {
        this.supplierReference = supplierReference;
    }
}
