package com.inventorytracker.utils;

import android.graphics.Color;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {

    public static int selectedColor = Color.parseColor("#b8bab9");
    public static int inactiveColor = Color.parseColor("#990000");
    public static int activeColor = Color.parseColor("#383838");
    public static int whiteColor = Color.WHITE;

    public static final int PRODUCT_SCAN = 9001;
    public static final int PACKAGE_SCAN = 9002;
    public static final int SIGNATURE_CAPTURE = 9003;
    public static final int UPDATE_BARCODE = 9004;

    public static DateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    public static SimpleDateFormat dotDateFormatWithTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    public static SimpleDateFormat dotDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public static String UID = "UID";

    public static String CUSTOMER = "customer";
    public static String CUSTOMER_ID = "customerID";
    public static String ORDER_ID = "orderID";
    public static String CONSIGNMENT_ID = "consignmentID";
    public static String TIMESTAMP = "timestamp";

    public static String PICKUP_METHOD = "pickupMethod";
    public static String PICKUP_PERSONAL = "Prevzem";
    public static String PICKUP_POST = "Pošta";

    public static String IS_REPORT = "report";

    public static String PRODUCT_ID = "productID";
    public static String PACKAGE_BARCODE = "PackageBarcode";
    public static String BARCODE = "barcode";

    public static String REQUEST = "request";

    public static String DISCOUNT_TYPE = "discountType";

    public static String DOCUMENT_PATH = "documentPath";

    public static String CONSIGNMENT_STATUS_OPEN = "open";
    public static String CONSIGNMENT_STATUS_CLOSED = "closed";

    public static String TASK_STATUS_UNFINISHED = "Nedokončano";
    public static String TASK_STATUS_FINISHED = "Dokončano";

    public static String STATS_BESTELLERS = "bestSellers";
    public static String STATS_CUSTOMERS = "bestCustomers";

    public static String URL = "URL";

}
