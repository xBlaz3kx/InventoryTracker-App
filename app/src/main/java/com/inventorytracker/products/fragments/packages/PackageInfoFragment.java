package com.inventorytracker.products.fragments.packages;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.textfield.TextInputLayout;
import com.inventorytracker.R;
import com.inventorytracker.utils.Constants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Locale;

import static com.inventorytracker.utils.EditTextUtils.getTextFromEditText;
import static com.inventorytracker.utils.StringHelper.isStringNumeric;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.math.NumberUtils.createInteger;

public class PackageInfoFragment extends Fragment implements View.OnClickListener {
    //ui
    private NavController controller;
    private TextView productNum, packageNum;
    private TextInputLayout input;
    private PackageSessionViewModel viewModel;
    //vars
    private Resources resources;
    private String barcode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        resources = getResources();
        viewModel = new ViewModelProvider(getActivity()).get(PackageSessionViewModel.class);
        try {
            barcode = getArguments().getString(Constants.BARCODE);
            viewModel.getTotalProductsLive().observe(getViewLifecycleOwner(), stringIntegerHashMap -> productNum.setText(String.format(Locale.getDefault(),
                    resources.getString(R.string.ReportProductNum), stringIntegerHashMap.get(barcode))));
            viewModel.getPackageCountLive().observe(getViewLifecycleOwner(), stringIntegerHashMap -> packageNum.setText(String.format(Locale.getDefault(),
                    resources.getString(R.string.ReportPackageNum), stringIntegerHashMap.get(barcode))));
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return inflater.inflate(R.layout.package_order_itemoptions_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);
        productNum = view.findViewById(R.id.reportProductNum);
        packageNum = view.findViewById(R.id.reportPackageNum);
        Button confirm = view.findViewById(R.id.confirmReportPackageModification);
        confirm.setOnClickListener(this);
        input = view.findViewById(R.id.reportAdditionalProductNum);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirmReportPackageModification) {
            String inputText = getTextFromEditText(input.getEditText());
            if (isNotBlank(barcode) && isStringNumeric(inputText)) {
                Integer numProducts = createInteger(inputText);
                if (numProducts >= 0) {
                    viewModel.addProducts(barcode, numProducts);
                    controller.navigate(R.id.action_packagePacketInfoFragment_to_packageSessionReport);
                } else {
                    Toast.makeText(getContext(), resources.getText(R.string.ReportInvalidProductNum), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
