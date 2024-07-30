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
        long result = new Account(this).insert(email, username, phone, password);
        if (result == -1){
            Toast.makeText(this, "Failed to register account", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Register successfully", Toast.LENGTH_SHORT).show();
        }


    }


}
