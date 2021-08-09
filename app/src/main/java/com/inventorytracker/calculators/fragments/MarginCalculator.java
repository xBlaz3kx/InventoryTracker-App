package com.inventorytracker.calculators.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.inventorytracker.R;
import com.inventorytracker.calculators.Calculator;

import java.math.BigDecimal;
import java.util.Locale;

import static com.inventorytracker.calculators.Calculator.calculateTargetPrice;
import static com.inventorytracker.utils.BigDecimalUtil.VAT22;
import static com.inventorytracker.utils.BigDecimalUtil.VAT95;
import static com.inventorytracker.utils.BigDecimalUtil.toDecimals;
import static com.inventorytracker.utils.EditTextUtils.clearEditTexts;
import static com.inventorytracker.utils.EditTextUtils.getTextToDecimal;
import static org.apache.commons.lang3.math.NumberUtils.createBigDecimal;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;


public class MarginCalculator extends Fragment implements View.OnClickListener {

    private TextInputLayout marginInput, targetPrice, priceIn, VATInput;
    private TextView output;
    private Resources resources;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resources = getResources();
        return inflater.inflate(R.layout.calculator_margin_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        targetPrice = view.findViewById(R.id.marginCalc_targetPrice);
        Button clear = view.findViewById(R.id.marginCalc_clear);
        Button calculate = view.findViewById(R.id.marginCalc_calculate);
        priceIn = view.findViewById(R.id.marginCalc_priceInput);
        marginInput = view.findViewById(R.id.marginCalc_marginInput);
        VATInput = view.findViewById(R.id.marginCalc_VAT);
        output = view.findViewById(R.id.marginCalc_output);
        clear.setOnClickListener(this);
        calculate.setOnClickListener(this);
    }

    private void calculateMargin() {
        String marginPercentage = getTextToDecimal(marginInput.getEditText()); //percentage
        String target = getTextToDecimal(targetPrice.getEditText());
        String VAT = getTextToDecimal(VATInput.getEditText());
        String priceInput = getTextToDecimal(priceIn.getEditText());
        BigDecimal BDVAT = null;
        BigDecimal BDPrice = null;
        String outputText = "";
        if (isCreatable(VAT)) {
            BDVAT = createBigDecimal(VAT);
            if (BDVAT.equals(VAT22) || BDVAT.equals(VAT95)) {
                BDVAT = toDecimals(BDVAT);
            }
        } else {
            outputText = resources.getString(R.string.InvalidPriceOrVAT);
        }
        if (isCreatable(priceInput)) {
            BDPrice = createBigDecimal(priceInput);
        } else {
            outputText = resources.getString(R.string.InvalidPriceOrVAT);
        }
        // if margin percentage is creatable and margin is not creatable
        if (isCreatable(marginPercentage) && (!isCreatable(target))) {
            BigDecimal margin = toDecimals(createBigDecimal(marginPercentage));
            if (BDPrice != null && BDVAT != null) {
                outputText = String.format(Locale.getDefault(), "%s :%.2f", resources.getString(R.string.Margin), calculateTargetPrice(margin, BDVAT, BDPrice).doubleValue());
            } else {
                outputText = resources.getString(R.string.InvalidData);
            } //if target price is creatable and margin price is not
        } else if ((!isCreatable(marginPercentage)) && isCreatable(target)) {
            BigDecimal priceTarget = createBigDecimal(target);
            if (BDPrice != null && BDVAT != null) {
                outputText = String.format(Locale.getDefault(), "%s :%.2f ", resources.getString(R.string.TargetPrice), Calculator.calculateMargin(priceTarget, BDVAT, BDPrice).doubleValue());
            } else {
                outputText = resources.getString(R.string.InvalidData);
            }
        } else {
            clear();
        }
        output.setText(outputText);
    }

    private void clear() {
        clearEditTexts(priceIn.getEditText(), targetPrice.getEditText());
        VATInput.getEditText().setText(resources.getString(R.string.TwentyTwo));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.marginCalc_calculate:
                calculateMargin();
                break;
            case R.id.marginCalc_clear:
                clear();
                break;
        }
    }
}
