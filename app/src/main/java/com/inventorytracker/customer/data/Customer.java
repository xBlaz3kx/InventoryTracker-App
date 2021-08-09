package com.inventorytracker.customer.data;

import com.google.firebase.database.IgnoreExtraProperties;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@IgnoreExtraProperties
public class Customer {
    private String firmName, customerName, customerSurname, customerAddress, customerPost;
    private String customerVAT;
    private Integer customerReference, customerNumberOfOrders;
    private Double customerOrderedTotal, customerPaidTotal;

    public Customer() {
    }

    public Customer(String firmName, String customerName, String customerSurname, String customerAddress, String customerPost, String customerVAT) {
        this.customerName = customerName;
        this.customerSurname = customerSurname;
        this.customerAddress = customerAddress;
        this.customerPost = customerPost;
        if (isEmpty(customerVAT)) {
            this.customerVAT = "";
        } else {
            this.customerVAT = "SI" + customerVAT;
        }
        this.firmName = firmName;
        this.customerOrderedTotal = 0.0;
        this.customerPaidTotal = 0.0;
        this.customerNumberOfOrders = 0;
    }

    public Integer getCustomerReference() {
        return customerReference;
    }

    public String getCustomerVAT() {
        return customerVAT;
    }

    public String getCustomerAddress() {
        return this.customerAddress;
    }

    public String getCustomerSurname() {
        return this.customerSurname;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public String getFirmName() {
        return firmName;
    }

    public String getCustomerPost() {
        return this.customerPost;
    }

    public Double getCustomerOrderedTotal() {
        return this.customerOrderedTotal;
    }

    public Integer getCustomerNumberOfOrders() {
        return customerNumberOfOrders;
    }

    public Double getCustomerPaidTotal() {
        return customerPaidTotal;
    }

    public void setCustomerPaidTotal(Double customerPaidTotal) {
        this.customerPaidTotal = customerPaidTotal;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerSurname(String customerSurname) {
        this.customerSurname = customerSurname;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setCustomerPost(String customerPost) {
        this.customerPost = customerPost;
    }

    public void setCustomerVAT(String customerVAT) {
        this.customerVAT = customerVAT;
    }

    public void setCustomerReference(Integer customerReference) {
        this.customerReference = customerReference;
    }

    public void setCustomerNumberOfOrders(Integer customerNumberOfOrders) {
        this.customerNumberOfOrders = customerNumberOfOrders;
    }

    public void setCustomerOrderedTotal(Double customerOrderedTotal) {
        this.customerOrderedTotal = customerOrderedTotal;
    }
}
