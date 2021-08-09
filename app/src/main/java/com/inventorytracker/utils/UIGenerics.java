package com.inventorytracker.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class UIGenerics {

    public static <adapterClass extends FirestoreRecyclerAdapter<firestoreObject, ?>, firestoreObject> void setFirestoreAdapter(@NonNull RecyclerView recyclerView, @NonNull adapterClass adapter, @NonNull Context context) {
        try {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setHasFixedSize(true);
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public static <firestoreObject> FirestoreRecyclerOptions<firestoreObject> createOptions(Query query, Class<firestoreObject> objectClass) {
        return new FirestoreRecyclerOptions.Builder<firestoreObject>()
                .setQuery(query, objectClass)
                .build();
    }

    public static <adapterClass extends RecyclerView.Adapter<?>> void setRecAdapter(@NonNull RecyclerView recyclerView, @NonNull adapterClass adapter, @NonNull Context context) {
        try {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setHasFixedSize(true);
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }
}
