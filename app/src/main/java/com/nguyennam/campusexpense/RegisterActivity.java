package com.nguyennam.campusexpense;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nguyennam.campusexpense.SqliteDB.Account;

public class RegisterActivity extends AppCompatActivity {
    public EditText edtEmail, edtUsername, edtPhone, edtPassword;
    public Button btnRegister, btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edtEmail = findViewById(R.id.emailEditText);
        edtUsername = findViewById(R.id.usernameEditText);
        edtPhone = findViewById(R.id.phoneEditText);
        edtPassword = findViewById(R.id.passwordEditText);
        btnRegister = findViewById(R.id.registerButton);
        btnLogin = findViewById(R.id.loginButton);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegisterAccount();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void RegisterAccount(){
        String email = edtEmail.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || username.isEmpty() || phone.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 8 characters, include a number, an uppercase letter, and a special character.", Toast.LENGTH_LONG).show();
            return;
        }

        long result = new Account(this).insert(email, username, phone, password);
        if (result == -2) {
            Toast.makeText(RegisterActivity.this, "Email already exists!", Toast.LENGTH_SHORT).show();
        } else if (result == -3) {
            Toast.makeText(RegisterActivity.this, "Username already exists!", Toast.LENGTH_SHORT).show();
        } else if (result == -1) {
            Toast.makeText(RegisterActivity.this, "Failed to register. Try again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại màn hình trước
        }


    }

    private boolean isValidPassword(String password) {
        // Regex kiểm tra mật khẩu
        String passwordPattern = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }


}
