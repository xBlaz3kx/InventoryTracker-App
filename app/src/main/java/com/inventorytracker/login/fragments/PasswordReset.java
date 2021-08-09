package com.inventorytracker.login.fragments;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.inventorytracker.R;

import org.apache.commons.lang3.StringUtils;

import static com.inventorytracker.utils.EditTextUtils.getTextFromEditText;


public class PasswordReset extends Fragment implements View.OnClickListener {
    //db
    private FirebaseAuth mAuth;
    private Resources resources;
    //ui
    private TextInputLayout emailInput;
    private NavController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_passwordreset_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();
        resources = getResources();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button resetPassword = view.findViewById(R.id.pasword_reset);
        resetPassword.setOnClickListener(this);
        controller = Navigation.findNavController(view);
        emailInput = view.findViewById(R.id.login_passwordreset_email);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pasword_reset) {
            String email = getTextFromEditText(emailInput.getEditText());
            if (StringUtils.isNotBlank(email)) {
                mAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), resources.getString(R.string.ResetPasswordEmail), Toast.LENGTH_SHORT).show();
                    controller.navigate(R.id.action_passwordResetFragment_to_loginFragment);
                }).addOnFailureListener(e -> Toast.makeText(getContext(), resources.getString(R.string.Error), Toast.LENGTH_SHORT).show());
            }
        }
    }
}
