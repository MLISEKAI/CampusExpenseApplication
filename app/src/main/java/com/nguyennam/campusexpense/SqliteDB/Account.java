package com.nguyennam.campusexpense.SqliteDB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.nguyennam.campusexpense.Model.User;

public class Account extends SQLiteOpenHelper {
    private static final String NAME = "account.db";
    private static final int VERSION = 1;

    public static final String TABLE_NAME = "account";
    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String PHONE = "phone";
    public static final String PASSWORD = "password";

    public Account(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EMAIL + " TEXT, " +
                USERNAME + " TEXT, " +
                PHONE + " TEXT, " +
                PASSWORD + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    public long insert(String email, String username, String phone, String password) {
        SQLiteDatabase db = getWritableDatabase();

        // Kiểm tra trùng email
        if (isEmailExists(email)) {
            return -2; // Mã lỗi trùng email
        }

        // Kiểm tra trùng username
        if (isUsernameExists(username)) {
            return -3; // Mã lỗi trùng username
        }

        // Thêm dữ liệu nếu không trùng
        ContentValues values = new ContentValues();
        values.put(EMAIL, email);
        values.put(USERNAME, username);
        values.put(PHONE, phone);
        values.put(PASSWORD, password);

        return db.insert(TABLE_NAME, null, values);
    }


    private boolean isEmailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { EMAIL };
        String selection = EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    private boolean isUsernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { USERNAME };
        String selection = USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    @SuppressLint("Range")
    public User getUserInfo(String input, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = "(" + USERNAME + " = ? OR " + EMAIL + " = ?) AND " + PASSWORD + " = ?";
        String[] selectionArgs = { input, input, password };

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
            user.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));
            user.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD)));
            cursor.close();
        }
        return user;
    }





//    public boolean update(String email, String username, String phone, String password) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(EMAIL, email);
//        values.put(USERNAME, username);
//        values.put(PHONE, phone);
//        values.put(PASSWORD, password);
//
//        String selection = USERNAME + " = ?";
//        String[] selectionArgs = { username };
//
//        return db.update(TABLE_NAME, values, selection, selectionArgs) > 0;
//    }

    public boolean update(String email, String username, String phone, String password) {
        SQLiteDatabase db = getWritableDatabase();

        // Kiểm tra xem email có tồn tại và không thuộc về user đang cập nhật
        if (isEmailExistsForOtherUser(email, username)) {
            return false; // Email đã tồn tại
        }

        ContentValues values = new ContentValues();
        values.put(EMAIL, email);
        values.put(USERNAME, username);
        values.put(PHONE, phone);
        values.put(PASSWORD, password);

        String selection = USERNAME + " = ?";
        String[] selectionArgs = { username };

        return db.update(TABLE_NAME, values, selection, selectionArgs) > 0;
    }

    private boolean isEmailExistsForOtherUser(String email, String currentUsername) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + EMAIL + " = ? AND " + USERNAME + " != ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, currentUsername});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


}
