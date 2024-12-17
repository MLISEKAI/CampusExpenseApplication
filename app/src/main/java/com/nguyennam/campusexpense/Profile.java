package com.nguyennam.campusexpense;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nguyennam.campusexpense.Model.User;
import com.nguyennam.campusexpense.SqliteDB.Account;

public class Profile extends Fragment {
    private EditText edtUsername, edtEmail, edtPhone, edtOldPassword, edtNewPassword;
    private Button btnSave, btnLogout;
    private Account accountDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtUsername = view.findViewById(R.id.edtUsername);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtOldPassword = view.findViewById(R.id.edtOldPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        btnSave = view.findViewById(R.id.btnSave);
        accountDatabase = new Account(getContext());
        btnLogout = view.findViewById(R.id.btnLogout);


        // Lấy thông tin người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phone", "");

        // Hiển thị thông tin người dùng
        edtUsername.setText(username);
        edtEmail.setText(email);
        edtPhone.setText(phone);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void updateUserInfo() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();

        if (username.isEmpty()) {
            edtUsername.setError("Username is required");
            edtUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Email is required");
            edtEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            edtPhone.setError("Phone number is required");
            edtPhone.requestFocus();
            return;
        }

        // Kiểm tra mật khẩu cũ
        User user = accountDatabase.getUserInfo(username, oldPassword);
        if (user == null) {
            Toast.makeText(getContext(), "Old password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra các trường thông tin
//        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)) {
//            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Cập nhật thông tin người dùng
//        boolean isUpdated = accountDatabase.update(email, username, phone, TextUtils.isEmpty(newPassword) ? oldPassword : newPassword);

        boolean isUpdated = accountDatabase.update(email, username, phone, TextUtils.isEmpty(newPassword) ? oldPassword : newPassword);

        if (!isUpdated) {
            Toast.makeText(getContext(), "Email already exists. Please try again!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isUpdated) {
            // Lưu lại thông tin mới vào SharedPreferences
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                    "UserSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.putString("phone", phone);
            editor.apply();

            Toast.makeText(getContext(), "Information updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to update information", Toast.LENGTH_SHORT).show();
        }
    }


    private void logout() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // chuyen huong user
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
