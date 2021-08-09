package com.inventorytracker.orders.data.order;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Order {
    private String pickupMethod, signatureURL, additionalInfo;
    private Double orderTotal, orderDiscount;
    private boolean isPaid;
    private List<DocumentReference> productList;
    private Map<String, Integer> productNumbers;
    private Map<String, Double> productDiscount;
    private Map<String, Boolean> hasOrderDiscount;
    @ServerTimestamp
    private Date orderTimestamp;
    private Integer orderReference;
    private DocumentReference customerReference, sellerReference;

    public Order() {
    }

    public Order(DocumentReference customerReference, DocumentReference sellerReference, List<DocumentReference> productList, Map<String, Integer> productNumbers,
                 Map<String, Double> productDiscount, Map<String, Boolean> hasOrderDiscount, Double orderDiscount, Double total, String signatureURL, String pickupMethod, String additionalInfo, Date orderTimestamp) {
        this.sellerReference = sellerReference;
        this.customerReference = customerReference;
        this.productDiscount = productDiscount;
        this.orderDiscount = orderDiscount;
        this.hasOrderDiscount = hasOrderDiscount;
        this.productNumbers = productNumbers;
        this.signatureURL = signatureURL;
        this.productList = productList;
        this.orderTimestamp = orderTimestamp;
        this.pickupMethod = pickupMethod;
        this.additionalInfo = additionalInfo;
        this.orderTotal = total;
    }

    public String getPickupMethod() {
        return pickupMethod;
    }

    public void setPickupMethod(String pickupMethod) {
        this.pickupMethod = pickupMethod;
    }

    public String getSignatureURL() {
        return signatureURL;
    }

    public void setSignatureURL(String signatureURL) {
        this.signatureURL = signatureURL;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public List<DocumentReference> getProductList() {
        return productList;
    }

    public void setProductList(List<DocumentReference> productList) {
        this.productList = productList;
    }

    public Map<String, Integer> getProductNumbers() {
        return productNumbers;
    }

    public void setProductNumbers(Map<String, Integer> productNumbers) {
        this.productNumbers = productNumbers;
    }

    public Map<String, Double> getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(Map<String, Double> productDiscount) {
        this.productDiscount = productDiscount;
    }

    public Map<String, Boolean> getHasOrderDiscount() {
        return hasOrderDiscount;
    }

    public void setHasOrderDiscount(Map<String, Boolean> hasOrderDiscount) {
        this.hasOrderDiscount = hasOrderDiscount;
    }

    public Date getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(Date orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public Integer getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(Integer orderReference) {
        this.orderReference = orderReference;
    }

    public DocumentReference getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(DocumentReference customerReference) {
        this.customerReference = customerReference;
    }

    public DocumentReference getSellerReference() {
        return sellerReference;
    }

    public void setSellerReference(DocumentReference sellerReference) {
        this.sellerReference = sellerReference;
    }

    public Double getOrderDiscount() {
        return orderDiscount;
    }

    public void setOrderDiscount(Double orderDiscount) {
        this.orderDiscount = orderDiscount;
    }
}
