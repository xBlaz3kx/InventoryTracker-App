package com.inventorytracker.discounts.data;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

@IgnoreExtraProperties
public class Discount {

    private Integer discountType;
    private ArrayList<DocumentReference> discountReference; //to product or customer
    private String sellerID;
    private Double discountPercentage;
    private boolean discountActive;
    @ServerTimestamp
    private Date discountCreationDate;
    private Date discountStartDate, discountEndDate;

    public Discount() {
    }

    public Discount(Integer discountType, String sellerID, Double discountPercentage, boolean isDiscountActive) {
        this.discountType = discountType;
        this.sellerID = sellerID;
        this.discountPercentage = discountPercentage;
        this.discountActive = isDiscountActive;
    }

    public Date getDiscountCreationDate() {
        return discountCreationDate;
    }

    public Date getDiscountStartDate() {
        return discountStartDate;
    }

    public Date getDiscountEndDate() {
        return discountEndDate;
    }

    public String getSellerID() {
        return sellerID;
    }

    public Integer getDiscountType() {
        return discountType;
    }

    public ArrayList<DocumentReference> getDiscountReference() {
        return discountReference;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public boolean getDiscountActive() {
        return discountActive;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }


    public void setDiscountCreationDate(Date discountCreationDate) {
        this.discountCreationDate = discountCreationDate;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public void setDiscountActive(boolean discountActive) {
        this.discountActive = discountActive;
    }

    public void setDiscountStartDate(Date discountStartDate) {
        this.discountStartDate = discountStartDate;
    }

    public void setDiscountReference(ArrayList<DocumentReference> discountReference) {
        this.discountReference = discountReference;
    }

    public void addDiscountReference(DocumentReference documentReference) {
        if (this.discountReference != null && !discountReference.contains(documentReference)) {
            discountReference.add(documentReference);
        }
    }

    public void setDiscountType(Integer discountType) {
        this.discountType = discountType;
    }

    public void setDiscountEndDate(Date discountEndDate) {
        this.discountEndDate = discountEndDate;
    }
}
