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

import com.example.cookbook.MainActivity;
import com.example.cookbook.R;
import com.example.cookbook.databinding.ActivityRegisterBinding;
import com.example.cookbook.entity.User;
import com.example.cookbook.repository.UserRepository;
import com.example.cookbook.utils.StateManager;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding registerBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(registerBinding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    public void onRegisterClick(View view) {
        if (!isValidInput()) return;

        AsyncTask.execute(() -> AsyncTask.execute(() -> {
            try {
                User user = new UserRepository(getApplicationContext()).register(
                        registerBinding.signupUsername.getText().toString(),
                        registerBinding.signupEmail.getText().toString(),
                        registerBinding.signupPassword.getText().toString()
                );
                StateManager.setLoggedInUser(user);
                redirectToMainActivity();
            } catch (Exception e) {
                this.runOnUiThread(() ->
                        Toast.makeText(this, R.string.registration_failed, Toast.LENGTH_SHORT).show()
                );
            }
        }));
    }

    public void onContinueAsGuestInRegister(View view) {
        redirectToMainActivity();
    }

    public boolean isValidInput() {
        boolean valid = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(registerBinding.signupEmail.getText()).matches()) {
            registerBinding.signupEmail.setError("Invalid email address");
            valid = false;
        }

        if (registerBinding.signupPassword.getText().toString().isEmpty()) {
            registerBinding.signupPassword.setError("Password cannot be empty");
            valid = false;
        }

        if (registerBinding.signupUsername.getText().toString().isEmpty()) {
            registerBinding.signupUsername.setError("Username cannot be empty");
            valid = false;
        }

        return valid;
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
