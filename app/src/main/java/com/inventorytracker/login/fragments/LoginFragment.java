package com.inventorytracker.login.fragments;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.inventorytracker.R;
import com.inventorytracker.mainmenu.MainMenu;
import com.inventorytracker.utils.Constants;

import static com.inventorytracker.utils.EditTextUtils.clearEditTexts;
import static com.inventorytracker.utils.EditTextUtils.getTextFromEditText;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;


public class LoginFragment extends Fragment implements View.OnClickListener {
    //db
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextInputLayout Email, Password;
    private NavController controller;
    private Resources resources;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ui
        Button login = view.findViewById(R.id.login);
        Button resetPassword = view.findViewById(R.id.login_resetpassword);
        Email = view.findViewById(R.id.loginMail);
        Password = view.findViewById(R.id.loginPWD);
        controller = Navigation.findNavController(view);
        login.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        resources = getResources();
        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                String email = getTextFromEditText(Email.getEditText());
                String password = getTextFromEditText(Password.getEditText());
                if (isNoneBlank(email, password)) {
                    LogIn(email, password);
                } else {
                    Toast.makeText(getContext(), resources.getString(R.string.InvalidData), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.login_resetpassword:
                controller.navigate(R.id.action_loginFragment_to_passwordResetFragment);
                break;
        }
    }

    private void LogIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        Intent intent = new Intent(getContext(), MainMenu.class);
                        intent.putExtra(Constants.UID, user.getUid());
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), resources.getString(R.string.LoginError), Toast.LENGTH_SHORT).show();
                        clearEditTexts(Password.getEditText());
                    }
                });
    }
}
