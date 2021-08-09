package com.inventorytracker.discounts.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inventorytracker.R;
import com.inventorytracker.discounts.data.Discount;
import com.inventorytracker.utils.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.inventorytracker.utils.BigDecimalUtil.toDecimals;
import static com.inventorytracker.utils.Constants.DISCOUNT_TYPE;
import static com.inventorytracker.utils.Constants.dotDateFormat;
import static com.inventorytracker.utils.EditTextUtils.clearEditTexts;
import static com.inventorytracker.utils.EditTextUtils.getTextFromEditText;
import static com.inventorytracker.utils.EditTextUtils.getTextToDecimal;
import static com.inventorytracker.utils.StringHelper.isStringNumeric;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.apache.commons.lang3.math.NumberUtils.createBigDecimal;
import static org.apache.commons.lang3.math.NumberUtils.createInteger;

public class DiscountMakeDiscountFragment extends Fragment implements View.OnClickListener {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference discountRef = db.collection("discounts");
    private TextView typeText;
    private EditText discountPercentage, startDate, endDate;
    private NavController controller;
    private DiscountCreationViewModel viewModel;
    //variables
    private String UID = "";
    private Integer discountType = -1;
    private ArrayList<DocumentReference> discountReference = new ArrayList<>();
    private Resources resources;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        resources = getResources();
        context = getContext();
        viewModel = new ViewModelProvider(getActivity()).get(DiscountCreationViewModel.class);
        try {
            UID = getArguments().getString(Constants.UID);
            discountType = getArguments().getInt(DISCOUNT_TYPE);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return inflater.inflate(R.layout.discountmake, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        discountPercentage = view.findViewById(R.id.discountInput);
        startDate = view.findViewById(R.id.discountStartDate);
        endDate = view.findViewById(R.id.discountEndDate);
        EditText typeIn = view.findViewById(R.id.discountTypeInput);
        typeText = view.findViewById(R.id.discountTypeText);
        //ui
        Button submitDiscount = view.findViewById(R.id.discountSubmit);
        Button addNew = view.findViewById(R.id.addNewDiscount);
        addNew.setOnClickListener(this);
        submitDiscount.setOnClickListener(this);
        controller = Navigation.findNavController(view);

        if (discountType == 2) {
            typeText.setText(resources.getString(R.string.ProductDiscount));
        } else if (discountType == 1) {
            typeText.setText(resources.getString(R.string.CustomerDiscount));
        }
        typeIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputString = normalizeSpace(s.toString());
                if (isStringNumeric(inputString) && discountType == -1) {
                    discountType = createInteger(inputString);
                    switch (discountType) {
                        case 0:
                            typeText.setText(resources.getString(R.string.StandardDiscount));
                            break;
                        case 1:
                            typeText.setText(resources.getString(R.string.ProductDiscount));
                            break;
                        case 2:
                            typeText.setText(resources.getString(R.string.CustomerDiscount));
                            break;
                        default:
                            clearEditTexts(typeText);
                            break;
                    }
                }
            }
        });
    }

    private void submitData() {
        BigDecimal bdDiscount = createBigDecimal(getTextToDecimal(discountPercentage)).setScale(2, RoundingMode.HALF_EVEN);
        bdDiscount = toDecimals(bdDiscount);
        if (bdDiscount != null && !(bdDiscount).equals(BigDecimal.ZERO)) {
            Discount discount = new Discount(discountType, UID, bdDiscount.doubleValue(), true);
            String discEndDate = getTextFromEditText(endDate);
            String discStartDate = getTextFromEditText(startDate);
            if (isNotBlank(discEndDate)) {
                if (isNotBlank(discStartDate)) { //if it has start date it must have end date
                    try {
                        Date startDate = dotDateFormat.parse(discEndDate);
                        Date endDate = dotDateFormat.parse(discStartDate);
                        if (startDate.after(endDate)) {
                            Toast.makeText(context, resources.getString(R.string.InvalidStartDate), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (startDate.before(Calendar.getInstance().getTime())) {
                            Toast.makeText(context, resources.getString(R.string.InvalidStartDate), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (endDate.before(startDate)) {
                            Toast.makeText(context, resources.getString(R.string.InvalidEndDate), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        discount.setDiscountEndDate(startDate);
                        discount.setDiscountStartDate(startDate);
                        if (Calendar.getInstance().getTime().before(startDate)) {
                            discount.setDiscountActive(false);
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                }
            }
            if (discountType == 1 || discountType == 2) {
                if (!discountReference.isEmpty()) {
                    discount.setDiscountReference(discountReference);
                } else {
                    Toast.makeText(context, resources.getString(R.string.Error), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            discountRef.document().set(discount)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, resources.getString(R.string.DiscountAdded), Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, resources.getString(R.string.DatabaseError), Toast.LENGTH_SHORT).show());

        } else {
            Toast.makeText(getContext(), resources.getString(R.string.InvalidDiscount), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discountSubmit:
                if (isNotBlank(UID)) {
                    Bundle args = new Bundle();
                    args.putString(Constants.UID, UID);
                    String percentage = getTextToDecimal(discountPercentage);
                    if (isStringNumeric(percentage)) {
                        if (discountType != -1) {
                            boolean isOk;
                            if (discountType == 1 && !discountReference.isEmpty()) {
                                discountReference = new ArrayList<>(viewModel.getCustomerReferences().getValue());
                                isOk = true;
                            } else if (discountType == 2) {
                                discountReference = new ArrayList<>(viewModel.getProductReferences().getValue());
                                isOk = true;
                            } else isOk = discountType == 0;
                            if (isOk) {
                                submitData();
                                controller.navigate(R.id.action_discountMakeFragment_to_discountManagerFragment, args);
                            } else {
                                Toast.makeText(context, resources.getString(R.string.NoDiscountReferences), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                break;
            case R.id.addNewDiscount:
                if (discountType != -1) {
                    Bundle args = new Bundle();
                    args.putString(Constants.UID, UID);
                    switch (discountType) {
                        case 1: //discount on customers
                            args.putInt(DISCOUNT_TYPE, 1);
                            controller.navigate(R.id.action_discountMakeFragment_to_discountCustomerFragment, args);
                            break;
                        case 2:
                            args.putInt(DISCOUNT_TYPE, 2);
                            controller.navigate(R.id.action_discountMakeFragment_to_discountProductFragment, args);
                            break;
                        default:
                            break;
                    }
                }
                break;
        }
    }
}
