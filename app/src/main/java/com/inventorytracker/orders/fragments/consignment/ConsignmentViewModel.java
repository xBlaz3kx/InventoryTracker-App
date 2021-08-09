package com.inventorytracker.orders.fragments.consignment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inventorytracker.products.data.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.inventorytracker.utils.DataStructureUtil.insertOrReplace;

public class ConsignmentViewModel extends AndroidViewModel {
    //live data
    private MutableLiveData<ArrayList<DocumentReference>> productsLive = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> barcodesLive = new MutableLiveData<>();
    private MutableLiveData<BigDecimal> totalLive = new MutableLiveData<>();
    private MutableLiveData<Map<String, Integer>> numProductsLive = new MutableLiveData<>();
    private MutableLiveData<Map<String, Integer>> productStockLive = new MutableLiveData<>();

    //firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference customerRef = db.collection("customers");
    private CollectionReference sellerRef = db.collection("sellers");
    //list of things needed
    private ArrayList<DocumentReference> products = new ArrayList<>();
    private ArrayList<String> barcodes = new ArrayList<>();
    private Map<String, BigDecimal> productPrice = new HashMap<>();
    private Map<String, BigDecimal> productVAT = new HashMap<>();
    private Map<String, Integer> numProducts = new HashMap<>();
    private Map<String, Integer> productStock = new HashMap<>();
    private Map<String, BigDecimal> discountValue = new HashMap<>();
    private Map<String, Boolean> applyProductDiscount = new HashMap<>();
    private Map<String, Boolean> applyOrderDiscount = new HashMap<>();
    //variables
    private BigDecimal total = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_CEILING), prevTotal = BigDecimal.ZERO.setScale(2, RoundingMode.CEILING);
    private BigDecimal orderDiscount = BigDecimal.ZERO;
    private String pickupMethod; //IDs
    private DocumentReference customerReference, sellerReference;

    public ConsignmentViewModel(@NonNull Application application) {
        super(application);
    }

    public void addProductDiscount(String barcode, BigDecimal discount) {
        insertOrReplace(discountValue, barcode, discount);
        updateTotal();
    }

    public void addOrderDiscount(BigDecimal discount) {
        if (discount.doubleValue() > 0.0) {
            orderDiscount = discount;
        }
        updateTotal();
    }

    public void isProductDiscountApplicable(String barcode, boolean isProductDiscountApplicable) {
        insertOrReplace(applyProductDiscount, barcode, isProductDiscountApplicable);
        if (!isProductDiscountApplicable) {
            discountValue.replace(barcode, BigDecimal.ZERO);
        }
    }

    public void isOrderDiscountApplicable(String barcode, boolean isDiscountApplicable) {
        insertOrReplace(applyOrderDiscount, barcode, isDiscountApplicable);
    }

    public Map<String, Boolean> getApplyProductDiscount() {
        return applyProductDiscount;
    }

    public Map<String, Boolean> getApplyOrderDiscount() {
        return applyOrderDiscount;
    }

    public void addProduct(String barcodeValue, DocumentReference doc, Product product) {
        barcodes.add(barcodeValue);
        products.add(doc);
        productStock.put(barcodeValue, product.getProductStock());
        numProducts.put(barcodeValue, product.getProductSKU());
        discountValue.put(barcodeValue, BigDecimal.ZERO);
        applyProductDiscount.put(barcodeValue, true);
        applyOrderDiscount.put(barcodeValue, true);
        barcodesLive.setValue(barcodes);
        updateTotal();
    }

    public void updateItemOnPress(String barcode) { //updates number of items, order total and order's product info
        try {
            numProducts.replace(barcode, numProducts.get(barcode) + 1);
            updateTotal();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void updateTotal() throws NullPointerException {
        prevTotal = total;
        total = BigDecimal.ZERO;
        for (final String barcode : barcodes) {
            if (productPrice.containsKey(barcode)) {
                calculateTotal(barcode, productPrice.get(barcode));
            } else {
                products.get(barcodes.indexOf(barcode)).get().addOnSuccessListener(documentSnapshot -> {
                    Product product = documentSnapshot.toObject(Product.class);
                    if (product != null) {
                        BigDecimal price = BigDecimal.valueOf(product.getProductPrice());
                        insertOrReplace(productPrice, barcode, price);
                        insertOrReplace(productVAT, barcode, BigDecimal.valueOf(product.getProductVATValue()));
                        calculateTotal(barcode, price);
                        totalLive.setValue(total);
                    }
                });
            }
        }
        if (total.doubleValue() < 0.0) {
            total = prevTotal;
            orderDiscount = BigDecimal.ZERO;
        } else {
            totalLive.setValue(total);
        }
    }

    private void calculateTotal(String barcode, BigDecimal price) throws NullPointerException {
        BigDecimal numberProducts = new BigDecimal(numProducts.get(barcode));
        if (productPrice != null && !productPrice.isEmpty()) {
            BigDecimal discount = BigDecimal.ONE;
            if (applyProductDiscount.get(barcode)) {
                discount = discount.multiply(BigDecimal.ONE.subtract(discountValue.get(barcode)));
            }
            if (applyOrderDiscount.get(barcode)) {
                discount = discount.multiply(BigDecimal.ONE.subtract(orderDiscount));
            }
            BigDecimal discountedProductPrice = price.multiply(discount).multiply(productVAT.get(barcode)).setScale(2, RoundingMode.CEILING);
            total = total.add(discountedProductPrice.multiply(numberProducts));
        }
    }

    private BigDecimal estimateTotal(String barcode, BigDecimal price, BigDecimal discountOrderTotal) {
        BigDecimal estimate = BigDecimal.ZERO;
        BigDecimal numberProducts = new BigDecimal(numProducts.get(barcode));
        if (productPrice != null && !productPrice.isEmpty()) {
            BigDecimal discount = BigDecimal.ONE;
            if (applyProductDiscount.get(barcode)) {
                discount = discount.multiply(BigDecimal.ONE.subtract(discountValue.get(barcode)));
            }
            if (applyOrderDiscount.get(barcode)) {
                discount = discount.multiply(BigDecimal.ONE.subtract(discountOrderTotal));
            }
            BigDecimal discountedProductPrice = price.multiply(discount).multiply(productVAT.get(barcode)).setScale(2, RoundingMode.CEILING);
            estimate = estimate.add(discountedProductPrice.multiply(numberProducts));
        }
        return estimate.setScale(2, RoundingMode.CEILING);
    }

    private BigDecimal estimatedTotal = BigDecimal.ZERO;

    public String calculateEstimatedOrderTotal(final BigDecimal discountOrderTotal) {
        estimatedTotal = BigDecimal.ZERO;
        for (final String barcode : barcodes) {
            if (productPrice.containsKey(barcode)) {
                estimatedTotal = estimatedTotal.add(estimateTotal(barcode, productPrice.get(barcode), discountOrderTotal));
            } else {
                products.get(barcodes.indexOf(barcode)).get().addOnSuccessListener(documentSnapshot -> {
                    Product product = documentSnapshot.toObject(Product.class);
                    if (product != null) {
                        BigDecimal price = BigDecimal.valueOf(product.getProductPrice());
                        insertOrReplace(productPrice, barcode, price);
                        insertOrReplace(productVAT, barcode, BigDecimal.valueOf(product.getProductVATValue()));
                        estimatedTotal = estimatedTotal.add(estimateTotal(barcode, price, discountOrderTotal));
                    }
                });
            }
        }
        return String.format(Locale.getDefault(), "%.2f €", estimatedTotal.setScale(2, RoundingMode.CEILING).doubleValue());
    }

    public void removeItem(int position) {
        String removeProduct = barcodes.get(position);
        discountValue.remove(removeProduct);
        numProducts.remove(removeProduct);
        barcodes.remove(position);
        products.remove(position);
        productPrice.remove(removeProduct);
        if (barcodes.size() == 0) {
            removeAllFromOrder();
        }
        barcodesLive.setValue(barcodes);
        updateTotal();
    }

    public Map<String, Double> getDiscountValue() {
        Map<String, Double> ret = new HashMap<>();
        for (String key : discountValue.keySet()) {
            ret.put(key, discountValue.get(key).doubleValue());
        }
        return ret;
    }

    public BigDecimal getOrderDiscount() {
        return orderDiscount;
    }

    private void removeAllFromOrder() {
        numProducts.clear();
        products.clear();
        productPrice.clear();
        discountValue.clear();
    }

    public LiveData<ArrayList<DocumentReference>> getLiveProducts() {
        productsLive.setValue(products);
        return productsLive;
    }

    public LiveData<BigDecimal> getLiveTotal() {
        totalLive.setValue(total);
        return totalLive;
    }

    public LiveData<Map<String, Integer>> getLiveNumProducts() {
        numProductsLive.setValue(numProducts);
        return numProductsLive;
    }

    public LiveData<ArrayList<String>> getLiveBarcodes() {
        barcodesLive.setValue(barcodes);
        return barcodesLive;
    }

    public MutableLiveData<Map<String, Integer>> getProductStockLive() {
        productStockLive.setValue(productStock);
        return productStockLive;
    }

    public DocumentReference getCustomerReference() {
        return customerReference;
    }

    public DocumentReference getSellerReference() {
        return sellerReference;
    }

    public ArrayList<DocumentReference> getProducts() {
        return products;
    }


    public Map<String, Integer> getNumProducts() {
        return numProducts;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getAdditionalInfo() {
        return "";
    }

    public String getPickupMethod() {
        return pickupMethod;
    }

    public void setCustomerReference(String customerID) {
        this.customerReference = customerRef.document(customerID);
    }

    public void setSellerReference(String sellerID) {
        this.sellerReference = sellerRef.document(sellerID);
    }

    public void setPickupMethod(String pickupMethod) {
        this.pickupMethod = pickupMethod;
    }

    public void setOrderDiscount(BigDecimal orderDiscount) {
        this.orderDiscount = orderDiscount;
    }

    public ArrayList<String> getBarcodes() {
        return barcodes;
    }

    public Map<String, Integer> getProductStock() {
        return productStock;
    }
}
