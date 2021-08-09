package com.inventorytracker.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.inventorytracker.R;
import com.inventorytracker.mainmenu.MainMenu;
import com.inventorytracker.utils.Constants;


public class Login extends FragmentActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        NavController controller = Navigation.findNavController(this, R.id.login_hostfragment);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                user = firebaseAuth.getCurrentUser();
                Intent intent = new Intent(Login.this, MainMenu.class);
                intent.putExtra(Constants.UID, user.getUid());
                startActivity(intent);
                finish();
            }
        });
    }
}
