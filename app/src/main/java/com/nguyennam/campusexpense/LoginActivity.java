package com.nguyennam.campusexpense;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nguyennam.campusexpense.Model.User;
import com.nguyennam.campusexpense.SqliteDB.Account;

public class LoginActivity extends AppCompatActivity {
    public EditText edtUsername, edtPassword;
    public Button btnLogin, BtnRegister;
    public Account accountDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtUsername = findViewById(R.id.usernameEditText);
        edtPassword = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.loginButton);
        BtnRegister = findViewById(R.id.registerButton);

        // Khởi tạo database
        accountDatabase = new Account(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginAccount();
            }
        });

        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void LoginAccount() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username and password are correct
        User data = accountDatabase.getUserInfo(username, password);
        if (data == null) {
            Toast.makeText(this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
        } else {

            saveUserSession(data);


            Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();

            // Start another activity or perform other actions
             Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
             startActivity(intent);
             finish(); // Optional: close current activity

        }
    }


    private void saveUserSession(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", user.getId());
        editor.putString("username", user.getUsername());
        editor.putString("email", user.getEmail());
        editor.putString("phone", user.getPhone());
        editor.apply(); // Commit changes
    }
}
