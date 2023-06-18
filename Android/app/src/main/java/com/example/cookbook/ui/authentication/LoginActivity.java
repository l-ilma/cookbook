package com.example.cookbook.ui.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cookbook.R;
import com.example.cookbook.databinding.ActivityLoginBinding;
import com.example.cookbook.entity.User;
import com.example.cookbook.repository.UserRepository;
import com.example.cookbook.utils.StateManager;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding loginBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
    }


    public void onLoginClick(View view) {
        if (!isValidInput()) return;

        AsyncTask.execute(() -> {
            try {
                User user = new UserRepository(getApplicationContext()).login(
                        loginBinding.loginEmail.getText().toString(),
                        loginBinding.loginPassword.getText().toString()
                );
                StateManager.setLoggedInUser(user);
                finish();
            } catch (Exception e) {
                this.runOnUiThread(() ->
                        Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    public void onSignupClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onContinueAsGuestInLoginClick(View view) {
        finish();
    }

    private boolean isValidInput() {
        boolean valid = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(loginBinding.loginEmail.getText()).matches()) {
            loginBinding.loginEmail.setError("Invalid email address");
            valid = false;
        }

        if (loginBinding.loginPassword.getText().toString().isEmpty()) {
            loginBinding.loginPassword.setError("Password cannot be empty");
            valid = false;
        }

        return valid;
    }
}
