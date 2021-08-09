package com.inventorytracker.calculators.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.inventorytracker.R;
import com.inventorytracker.calculators.Calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import static com.inventorytracker.calculators.Calculator.calculateDiscountedPrice;
import static com.inventorytracker.utils.BigDecimalUtil.toDecimals;
import static com.inventorytracker.utils.EditTextUtils.clearEditTexts;
import static com.inventorytracker.utils.EditTextUtils.getTextToDecimal;
import static com.inventorytracker.utils.StringHelper.areStringsNumeric;
import static com.inventorytracker.utils.StringHelper.isStringNumeric;
import static org.apache.commons.lang3.math.NumberUtils.createBigDecimal;


public class DiscountCalculator extends Fragment implements View.OnClickListener {
    //ui
    private Button addDiscount;
    private TextInputLayout priceInput, discountValue, VATIn;
    private TextView discountCalc_output;
    private RecyclerView discountDisplay;
    //vars
    private ArrayList<BigDecimal> discountList = new ArrayList<>();
    private Resources resources;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resources = getResources();
        return inflater.inflate(R.layout.calculator_discount_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addDiscount = view.findViewById(R.id.discountCalc_addDiscount);
        Button clear = view.findViewById(R.id.discountCalc_clear);
        Button calculate = view.findViewById(R.id.discountCalc_calculate);
        priceInput = view.findViewById(R.id.discountCalc_priceInput);
        discountValue = view.findViewById(R.id.discountCalc_discountInput);
        VATIn = view.findViewById(R.id.discountCalc_VAT);
        discountCalc_output = view.findViewById(R.id.discountCalc_output);
        addDiscount.setOnClickListener(this);
        clear.setOnClickListener(this);
        calculate.setOnClickListener(this);
    }

    private void addDiscount(String discount) {
        if (isStringNumeric(discount)) {
            BigDecimal BDDiscount = createBigDecimal(discount);
            if (BDDiscount.doubleValue() > 0.0 && BDDiscount.doubleValue() <= 100) {
                discountList.add(toDecimals(BDDiscount));
            } else {
                Toast.makeText(getContext(), resources.getString(R.string.InvalidDiscount), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void calculateDiscount() {
        String price = getTextToDecimal(priceInput.getEditText());
        String VAT = getTextToDecimal(VATIn.getEditText());
        if (areStringsNumeric(price, VAT)) {
            BigDecimal BDPrice = createBigDecimal(price);
            BigDecimal BDVAT = createBigDecimal(VAT);
            try {
                discountCalc_output.setText(String.format(Locale.getDefault(), "%.2f", calculateDiscountedPrice(BDPrice, BDVAT, discountList).doubleValue()));
            } catch (Exception e) {
                discountCalc_output.setText(resources.getString(R.string.InvalidData));
            }
        } else {
            discountCalc_output.setText(resources.getString(R.string.InvalidData));
        }
    }

    private void clear() {
        clearEditTexts(discountValue.getEditText(), priceInput.getEditText());
        VATIn.getEditText().setText(getResources().getString(R.string.TwentyTwo));
        discountList.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discountCalc_calculate:
                calculateDiscount();
                break;
            case R.id.discountCalc_clear:
                clear();
                break;
            case R.id.discountCalc_addDiscount:
                addDiscount(addDiscount.getText().toString());
                break;
        }
    }
}
