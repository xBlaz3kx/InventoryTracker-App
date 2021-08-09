package com.inventorytracker.orders.fragments.order;

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
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

public class OrderViewModel extends AndroidViewModel {
    //live data
    private MutableLiveData<ArrayList<DocumentReference>> productsLive = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ArrayList<String>> barcodesLive = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<BigDecimal> totalLive = new MutableLiveData<>(ZERO);
    private MutableLiveData<Map<String, Integer>> numProductsLive = new MutableLiveData<>(new HashMap<>());
    private MutableLiveData<Map<String, Integer>> productStockLive = new MutableLiveData<>(new HashMap<>());

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
    private Map<String, BigDecimal> discountValues = new HashMap<>();
    private Map<String, Boolean> applyProductDiscount = new HashMap<>();
    private Map<String, Boolean> applyOrderDiscount = new HashMap<>();
    //variables
    private BigDecimal total = ZERO.setScale(2, BigDecimal.ROUND_CEILING), prevTotal = ZERO;
    private BigDecimal orderDiscount = ZERO;
    private String pickupMethod; //IDs
    private DocumentReference customerReference, sellerReference;

    public OrderViewModel(@NonNull Application application) {
        super(application);
    }

    public void addProductDiscount(String barcode, BigDecimal discount) {
        insertOrReplace(discountValues, barcode, discount);
        updateTotal();
    }

    public void addOrderDiscount(BigDecimal discount) {
        if (discount.doubleValue() >= 0.0 && discount.doubleValue() <= 1.0) {
            orderDiscount = discount;
        }
        updateTotal();
    }

    public void isProductDiscountApplicable(String barcode, boolean isApplicable) {
        insertOrReplace(applyProductDiscount, barcode, isApplicable);
        if (!isApplicable) {
            discountValues.replace(barcode, ZERO);
        }
    }

    public void isOrderDiscountApplicable(String barcode, boolean isApplicable) {
        insertOrReplace(applyOrderDiscount, barcode, isApplicable);
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
        discountValues.put(barcodeValue, ZERO);
        applyProductDiscount.put(barcodeValue, true);
        applyOrderDiscount.put(barcodeValue, true);
        barcodesLive.setValue(barcodes);
        updateTotal();
    }

    public void updateItemStock(final int position) {
        try {
            products.get(position).get().addOnSuccessListener(documentSnapshot -> {
                Product product = documentSnapshot.toObject(Product.class);
                if (product != null) {
                    productStock.replace(barcodes.get(position), product.getProductStock());
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void updateItemOnPress(String barcode) {
        try {
            updateItemStock(barcodes.indexOf(barcode));
            numProducts.replace(barcode, numProducts.get(barcode) + 1);
            updateTotal();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void updateTotal() throws NullPointerException {
        prevTotal = total;
        total = ZERO;
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
            orderDiscount = ZERO;
        } else {
            totalLive.setValue(total);
        }
    }

    private void calculateTotal(String barcode, BigDecimal price) {
        BigDecimal numberProducts = new BigDecimal(numProducts.get(barcode));
        if (productPrice != null && !productPrice.isEmpty()) {
            BigDecimal discount = ONE;
            if (applyProductDiscount.get(barcode)) {
                discount = discount.multiply(ONE.subtract(discountValues.get(barcode)));
            }
            if (applyOrderDiscount.get(barcode)) {
                discount = discount.multiply(ONE.subtract(orderDiscount));
            }
            BigDecimal discountedProductPrice = price.multiply(discount).multiply(productVAT.get(barcode)).setScale(2, RoundingMode.CEILING);
            total = total.add(discountedProductPrice.multiply(numberProducts));
        }
    }

    private BigDecimal estimateTotal(String barcode, BigDecimal price, BigDecimal discountOrderTotal) {
        BigDecimal estimate = ZERO;
        BigDecimal numberProducts = new BigDecimal(numProducts.get(barcode));
        if (productPrice != null && !productPrice.isEmpty()) {
            BigDecimal discount = ONE;
            if (applyProductDiscount.get(barcode)) {
                discount = discount.multiply(ONE.subtract(discountValues.get(barcode)));
            }
            if (applyOrderDiscount.get(barcode)) {
                discount = discount.multiply(ONE.subtract(discountOrderTotal));
            }
            BigDecimal discountedProductPrice = price.multiply(discount).multiply(productVAT.get(barcode)).setScale(2, RoundingMode.CEILING);
            estimate = estimate.add(discountedProductPrice.multiply(numberProducts));
        }
        return estimate.setScale(2, RoundingMode.CEILING);
    }

    private BigDecimal estimatedTotal = ZERO;

    public String calculateEstimatedOrderTotal(final BigDecimal discountOrderTotal) {
        estimatedTotal = ZERO;
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
                        totalLive.setValue(estimatedTotal);
                    }
                });
            }
        }
        return String.format(Locale.getDefault(), "%.2f €", estimatedTotal.setScale(2, RoundingMode.CEILING).doubleValue());
    }

    public void removeItem(int position) {
        String removeProduct = barcodes.get(position);
        discountValues.remove(removeProduct);
        numProducts.remove(removeProduct);
        productPrice.remove(removeProduct);
        barcodes.remove(position);
        products.remove(position);
        if (barcodes.size() == 0) {
            removeAllFromOrder();
        }
        barcodesLive.setValue(barcodes);
        updateTotal();
    }

    private void removeAllFromOrder() {
        numProducts.clear();
        products.clear();
        productPrice.clear();
        discountValues.clear();
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
        String additionalInfo = "";
        return additionalInfo;
    }

    public String getPickupMethod() {
        return pickupMethod;
    }

    public void setCustomerID(String customerID) {
        this.customerReference = customerRef.document(customerID);
    }

    public void setSellerID(String sellerID) {
        sellerRef.whereEqualTo("ID", sellerID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                sellerReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
            }
        });
    }

    public void setPickupMethod(String pickupMethod) {
        this.pickupMethod = pickupMethod;
    }

    public void setOrderDiscount(BigDecimal orderDiscount) {
        this.orderDiscount = orderDiscount;
    }

    public BigDecimal getOrderDiscount() {
        return orderDiscount;
    }

    public Map<String, Double> getDiscountValues() {
        Map<String, Double> ret = new HashMap<>();
        for (String key : discountValues.keySet()) {
            try {
                ret.put(key, discountValues.get(key).doubleValue());
            } catch (Exception e) {
                return null;
            }
        }
        return ret;
    }

    public ArrayList<String> getBarcodes() {
        return barcodes;
    }

    public Map<String, Integer> getProductStock() {
        return productStock;
    }
}
