package com.inventorytracker.products.fragments.packages;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inventorytracker.R;
import com.inventorytracker.products.adapters.PackageAdapterInReport;
import com.inventorytracker.products.data.ProductReport;
import com.inventorytracker.utils.Constants;

import java.util.ArrayList;

import static com.inventorytracker.utils.Constants.IS_REPORT;
import static com.inventorytracker.utils.UIGenerics.setRecAdapter;

public class PackageSessionReport extends Fragment implements View.OnClickListener {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reportRef = db.collection("reports");
    //navigation
    private NavController controller;
    private PackageSessionViewModel viewModel;
    private RecyclerView packageList;
    private PackageAdapterInReport adapter;
    private Resources resources;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resources = getResources();
        viewModel = new ViewModelProvider(getActivity()).get(PackageSessionViewModel.class);
        adapter = new PackageAdapterInReport();
        viewModel.getProductPackagesLive().observe(getViewLifecycleOwner(), packages -> {
            adapter.setPackageList(new ArrayList<>(packages));
            adapter.setPackageCount(viewModel.getPackageCount());
            adapter.setProductCount(viewModel.getTotalProducts());
        });
        viewModel.getPackageCountLive().observe(getViewLifecycleOwner(), stringIntegerHashMap -> adapter.setPackageCount(stringIntegerHashMap));
        viewModel.getTotalProductsLive().observe(getViewLifecycleOwner(), stringIntegerHashMap -> adapter.setProductCount(stringIntegerHashMap));
        return inflater.inflate(R.layout.package_order_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button scanPackage = view.findViewById(R.id.scanPackage);
        Button submitReport = view.findViewById(R.id.submitReport);
        packageList = view.findViewById(R.id.packageList);
        scanPackage.setOnClickListener(this);
        submitReport.setOnClickListener(this);
        controller = Navigation.findNavController(view);
        setupRecycler();
    }

    private void setupRecycler() {
        setRecAdapter(packageList, adapter, getContext());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    viewModel.removeItem(viewHolder.getAdapterPosition());
                } else if (direction == ItemTouchHelper.LEFT) {
                    Bundle args = new Bundle();
                    args.putString(Constants.BARCODE, viewModel.getPackageBarcodes().get(viewHolder.getAdapterPosition()));
                    controller.navigate(R.id.action_packageSessionReport_to_packagePacketInfoFragment, args);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(packageList);
    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();
        switch (v.getId()) {
            case R.id.scanPackage:
                args.putBoolean(IS_REPORT, true);
                controller.navigate(R.id.action_packageSessionReport_to_packageScanFragment2, args);
                break;
            case R.id.submitReport:
                submitReport(new ProductReport(viewModel.getPackageReferences(), viewModel.getPackageCount(), viewModel.getTotalProducts(), viewModel.getSupplierOrderReference()));
                break;
        }
    }

    private void submitReport(final ProductReport report) {
        if (report != null && !report.getPackageReferences().isEmpty() && !report.getProductsTotal().isEmpty()) {
            reportRef.document().set(report).addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), resources.getString(R.string.ReportMade), Toast.LENGTH_SHORT).show();
                viewModel.clearViewModel();
                controller.navigate(R.id.packageActionFragment2);
            }).addOnFailureListener(e -> Toast.makeText(getContext(), resources.getString(R.string.DatabaseError), Toast.LENGTH_SHORT).show());
        }
    }
}
