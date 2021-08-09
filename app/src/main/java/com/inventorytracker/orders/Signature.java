package com.inventorytracker.orders;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.inventorytracker.R;
import com.inventorytracker.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import static com.inventorytracker.utils.Constants.CUSTOMER_ID;
import static com.inventorytracker.utils.Constants.TIMESTAMP;

public class Signature extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SignaturePad mSignaturePad;
    private Button sendToDB, clearPad;
    private String customerID, timeStamp, URL;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resources = getResources();
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_signature);
        Intent prevIntent = getIntent();
        customerID = prevIntent.getStringExtra(CUSTOMER_ID);
        timeStamp = prevIntent.getStringExtra(TIMESTAMP);
        sendToDB = findViewById(R.id.send);
        clearPad = findViewById(R.id.clear);
        sendToDB.setEnabled(false);
        clearPad.setEnabled(false);
        mSignaturePad = findViewById(R.id.signature);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                sendToDB.setEnabled(true);
                clearPad.setEnabled(true);
            }

            @Override
            public void onClear() {
                sendToDB.setEnabled(false);
                clearPad.setEnabled(false);
            }
        });

        clearPad.setOnClickListener(view -> mSignaturePad.clear());
        sendToDB.setOnClickListener(view -> {
            writeDB(mSignaturePad.getSignatureBitmap());
        });
    }

    private void writeDB(Bitmap bitmap) {
        //make a unique ID
        String filename = String.format(Locale.getDefault(), "customerSignatures/customer_%s_%s.jpeg", customerID, timeStamp);
        final StorageReference pictureRef = mStorageRef.child(filename);
        //make image from capture pad
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        //create metadata
        final StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("customerID", customerID)
                .setCustomMetadata("date", timeStamp)
                .build();
        //upload image
        UploadTask uploadTask = pictureRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            Toast.makeText(Signature.this, resources.getString(R.string.DatabaseError), Toast.LENGTH_SHORT).show();
            finish();
        }).addOnSuccessListener(taskSnapshot -> {
            mSignaturePad.clear();
            pictureRef.updateMetadata(metadata).addOnSuccessListener(storageMetadata -> {
            });
        }).addOnFailureListener(e -> {
            setResult(CommonStatusCodes.NETWORK_ERROR);
            finish();
        }).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return pictureRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                URL = downloadUri.toString();
                //parse URL to prev activity
                Intent data1 = new Intent();
                data1.putExtra(Constants.URL, URL);
                setResult(CommonStatusCodes.SUCCESS, data1);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Signature.this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
