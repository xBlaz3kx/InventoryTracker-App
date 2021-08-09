package com.inventorytracker.utils;

import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;

public class EditTextUtils {

    public static String getTextToDecimal(EditText editText) {
        try {
            return StringUtils.normalizeSpace(editText.getText().toString().replace(",", "."));
        } catch (Exception e) {
        }
        return "";
    }

    public static String getTextFromEditText(EditText editText) {
        String text = "";
        try {
            text = StringUtils.normalizeSpace(editText.getText().toString());
        } catch (Exception e) {
        }
        return text;
    }

    public static BigDecimal getNumberFromEditText(EditText editText) {
        try {
            String str = getTextFromEditText(editText);
            if (NumberUtils.isCreatable(str)) {
                return NumberUtils.createBigDecimal(str);
            } else {
                return BigDecimal.ZERO;
            }
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @SafeVarargs
    public static <E extends TextView> void clearEditTexts(E... texts) {
        for (E text : texts) {
            text.setText("");
        }
    }
}
