package com.inventorytracker.utils;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class BigDecimalUtil {

    public static BigDecimal HUNDRED = BigDecimal.valueOf(100), VAT22 = BigDecimal.valueOf(1.22), VAT95 = BigDecimal.valueOf(1.095);

    public static BigDecimal toDecimals(@NonNull BigDecimal number) {
        if (number.doubleValue() > 0 && number.doubleValue() <= 1) {
            return number;
        } else if (number.doubleValue() > 0.0 && number.doubleValue() <= 100) {
            return number.divide(HUNDRED, 2, RoundingMode.HALF_EVEN);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static String fromDecimalToPercent(Double discount) {
        if (discount > 0.0 && discount <= 1.0) {
            discount = discount * 100;
            return String.format(Locale.getDefault(), "%4.2f", discount) + "%";
        } else if (discount > 0.0 && discount <= 100.0) {
            return String.format(Locale.getDefault(), "%4.2f", discount) + "%";
        }
        return "";
    }

}
