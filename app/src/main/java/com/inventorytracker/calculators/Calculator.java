package com.inventorytracker.calculators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static com.inventorytracker.utils.BigDecimalUtil.VAT22;
import static com.inventorytracker.utils.BigDecimalUtil.VAT95;
import static com.inventorytracker.utils.BigDecimalUtil.toDecimals;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

public class Calculator {

    public static BigDecimal calculateDiscount(ArrayList<BigDecimal> discounts) throws IllegalArgumentException {
        BigDecimal discountTotal = ONE;
        for (BigDecimal discountValue : discounts) {
            if (discountValue != null) {
                discountTotal = discountTotal.multiply(ONE.subtract(discountValue));
            }
        }
        return discountTotal;
    }

    public static BigDecimal calculateDiscountedPrice(BigDecimal price, BigDecimal VAT, ArrayList<BigDecimal> discounts) throws IllegalArgumentException {
        BigDecimal finalPrice = ZERO;
        BigDecimal discount = calculateDiscount(discounts);
        if (price.doubleValue() > 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (VAT.equals(VAT22) || VAT.equals(VAT95)) {
            VAT = toDecimals(VAT);
        } else {
            throw new IllegalArgumentException("VAT is not defined");
        }
        finalPrice = price.multiply(discount).multiply(ONE.add(VAT)).setScale(2, RoundingMode.CEILING);
        return finalPrice;
    }

    public static BigDecimal calculateMargin(BigDecimal targetPrice, BigDecimal VAT, BigDecimal currentPrice) throws IllegalArgumentException {
        if (targetPrice.doubleValue() < 0) {
            throw new IllegalArgumentException("Target price cannot be negative");
        }
        if (targetPrice.doubleValue() < currentPrice.doubleValue()) {
            throw new IllegalArgumentException("Target price cannot be less than current price!");
        }
        if (!(VAT.equals(VAT22) || VAT.equals(VAT95))) {
            throw new IllegalArgumentException("VAT is not 22 or 9.5");
        }
        return targetPrice.divide(ONE.add(VAT).add(currentPrice), 2, RoundingMode.CEILING);
    }

    public static BigDecimal calculateTargetPrice(BigDecimal marginPercentage, BigDecimal VAT, BigDecimal currentPrice) throws IllegalArgumentException {
        marginPercentage = toDecimals(marginPercentage);
        if (marginPercentage.equals(ZERO)) {
            throw new IllegalArgumentException("Invalid margin");
        }
        if (!(VAT.equals(VAT22) || VAT.equals(VAT95))) {
            throw new IllegalArgumentException("VAT is not 22 or 9.5");
        }
        return currentPrice.multiply(marginPercentage).multiply(BigDecimal.ONE.add(VAT));
    }

}
