package com.inventorytracker.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

public class StringHelper {

    public static String finalPriceWithTax(BigDecimal price, BigDecimal multiplyWith, BigDecimal VAT) {
        return price.multiply(multiplyWith).multiply(BigDecimal.ONE.add(VAT)).setScale(2, RoundingMode.CEILING).toString();
    }

    public static boolean isStringNumeric(String string) {
        return isNotBlank(string) && isCreatable(string);
    }

    public static boolean areStringsNumeric(String... strings) {
        for (String s : strings) {
            if (!isStringNumeric(s)) {
                return false;
            }
        }
        return true;
    }

}
