package com.inventorytracker.mainmenu.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.inventorytracker.R;
import com.inventorytracker.tasks.adapter.TaskAdapter;
import com.inventorytracker.tasks.data.UserTask;
import com.inventorytracker.utils.Constants;

import java.util.Locale;

import static com.inventorytracker.utils.Constants.TASK_STATUS_UNFINISHED;
import static com.inventorytracker.utils.UIGenerics.createOptions;
import static com.inventorytracker.utils.UIGenerics.setFirestoreAdapter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class MainMenuFragment extends Fragment {
    //db
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference sellersRef = db.collection("sellers");
    private CollectionReference taskRef = db.collection("tasks");
    //UI
    private TaskAdapter adapter;
    private TextView welcomeText;
    //var
    private String sWelcome, UID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Resources resources = getResources();
        try {
            UID = getArguments().getString(Constants.UID);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        sWelcome = resources.getString(R.string.Welcome);
        Query query = taskRef.whereEqualTo("uploaderID", UID)
                .whereEqualTo("taskStatus", TASK_STATUS_UNFINISHED)
                .orderBy("priority", Query.Direction.ASCENDING)
                .orderBy("taskTitle", Query.Direction.DESCENDING).limit(5);
        adapter = new TaskAdapter(createOptions(query, UserTask.class));
        if (isNotBlank(UID)) {
            sellersRef.whereEqualTo("ID", UID).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty()) {
                    String sellerName = "";
                    try {
                        sellerName = queryDocumentSnapshots.getDocuments().get(0).getString("sellerName");
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                    if (isNotBlank(sellerName) && !sWelcome.contains(sellerName)) {
                        sWelcome = String.format(Locale.getDefault(), "%s, %s!",
                                resources.getString(R.string.Welcome),
                                sellerName);
                        welcomeText.setText(sWelcome);
                    }
                }
            });
        }
        return inflater.inflate(R.layout.mainmenu_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        welcomeText = view.findViewById(R.id.mainMenuWelcomeText);
        RecyclerView taskDisplay = view.findViewById(R.id.mainMenuTaskDisplay);
        setFirestoreAdapter(taskDisplay, adapter, getActivity().getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

