package com.inventorytracker.orders.data.consignment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@IgnoreExtraProperties
public class ConsignmentOrder {
    private String pickupMethod, signatureURL, consignmentStatus, additionalInfo;
    private Double consignmentTotal, consignmentDiscount;
    private ArrayList<DocumentReference> consignmentProductList;
    private Map<String, Integer> consignmentProductNumbers;
    private Map<String, Integer> consignmentProductsSold;
    private Map<String, Double> consignmentProductDiscounts;
    private Map<String, Boolean> hasOrderDiscount;
    @ServerTimestamp
    private Date consignmentCreated, consignmentClosed;
    private Integer orderReference;
    private DocumentReference customerReference, sellerReference;

    public ConsignmentOrder() {
    }

    public ConsignmentOrder(DocumentReference customerReference, DocumentReference sellerReference, ArrayList<DocumentReference> productList, Map<String, Integer> consignmentProductNumbers,
                            Map<String, Double> consignmentProductDiscounts,
                            Map<String, Boolean> hasOrderDiscount, Double consignmentDiscount,
                            Double total, String signatureURL, String pickupMethod, String status, Date orderTimestamp) {
        this.sellerReference = sellerReference;
        this.customerReference = customerReference;
        this.signatureURL = signatureURL;
        this.consignmentProductDiscounts = consignmentProductDiscounts;
        this.consignmentProductNumbers = consignmentProductNumbers;
        this.hasOrderDiscount = hasOrderDiscount;
        this.consignmentDiscount = consignmentDiscount;
        this.consignmentProductList = productList;
        this.consignmentCreated = orderTimestamp;
        this.pickupMethod = pickupMethod;
        this.consignmentStatus = status;
        this.consignmentTotal = total;
        this.consignmentClosed = null;
    }

    public Double getConsignmentDiscount() {
        return consignmentDiscount;
    }

    public void setConsignmentDiscount(Double consignmentDiscount) {
        this.consignmentDiscount = consignmentDiscount;
    }

    public Map<String, Integer> getConsignmentProductsSold() {
        return consignmentProductsSold;
    }

    public void setConsignmentProductsSold(Map<String, Integer> consignmentProductsSold) {
        this.consignmentProductsSold = consignmentProductsSold;
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

    public String getConsignmentStatus() {
        return consignmentStatus;
    }

    public void setConsignmentStatus(String consignmentStatus) {
        this.consignmentStatus = consignmentStatus;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Double getConsignmentTotal() {
        return consignmentTotal;
    }

    public void setConsignmentTotal(Double consignmentTotal) {
        this.consignmentTotal = consignmentTotal;
    }

    public ArrayList<DocumentReference> getConsignmentProductList() {
        return consignmentProductList;
    }

    public void setConsignmentProductList(ArrayList<DocumentReference> consignmentProductList) {
        this.consignmentProductList = consignmentProductList;
    }

    public Map<String, Integer> getConsignmentProductNumbers() {
        return consignmentProductNumbers;
    }

    public void setConsignmentProductNumbers(Map<String, Integer> consignmentProductNumbers) {
        this.consignmentProductNumbers = consignmentProductNumbers;
    }

    public Map<String, Double> getConsignmentProductDiscounts() {
        return consignmentProductDiscounts;
    }

    public void setConsignmentProductDiscounts(Map<String, Double> consignmentProductDiscounts) {
        this.consignmentProductDiscounts = consignmentProductDiscounts;
    }

    public Map<String, Boolean> getHasOrderDiscount() {
        return hasOrderDiscount;
    }

    public void setHasOrderDiscount(Map<String, Boolean> hasOrderDiscount) {
        this.hasOrderDiscount = hasOrderDiscount;
    }

    public Date getConsignmentCreated() {
        return consignmentCreated;
    }

    public void setConsignmentCreated(Date consignmentCreated) {
        this.consignmentCreated = consignmentCreated;
    }

    public Date getConsignmentClosed() {
        return consignmentClosed;
    }

    public void setConsignmentClosed(Date consignmentClosed) {
        this.consignmentClosed = consignmentClosed;
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
}
