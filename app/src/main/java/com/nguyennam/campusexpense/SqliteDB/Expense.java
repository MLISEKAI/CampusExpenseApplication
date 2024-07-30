package com.nguyennam.campusexpense.SqliteDB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.nguyennam.campusexpense.Model.ExpenseModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Expense extends SQLiteOpenHelper {

    private static final String NAME = "expense.db";
    private static final int VERSION = 1;
    public static final String TABLE_NAME = "expense";

    public static final String ID = "id";
    public static final String AMOUNT = "amount";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String CATEGORY = "category";
    public static final String DATE = "date";
    public static final String USER_CREATED_ID = "user_create_id";




    public Expense(@Nullable Context context, @Nullable String name) {
        super(context, NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AMOUNT + " INTEGER, " +
                TITLE + " TEXT, " +
                DESCRIPTION + " TEXT, " +
                CATEGORY + " TEXT, " +
                DATE + " TEXT, " +
                USER_CREATED_ID + " INTEGER)";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);

    }

    public long insert(long amount, String title, String description, String category, String date, int user_create_id) {
        SQLiteDatabase db = getWritableDatabase();
        // Insert if not exists
        ContentValues values = new ContentValues();
        values.put(AMOUNT, amount);
        values.put(TITLE, title);
        values.put(DESCRIPTION, description);
        values.put(CATEGORY, category);
        values.put(DATE, date);
        values.put(USER_CREATED_ID, user_create_id);
        return db.insert(TABLE_NAME, null, values);
    }

    public long getUserExpenseTotal(int user_create_id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT SUM(" + AMOUNT + ") FROM " + TABLE_NAME + " WHERE " + USER_CREATED_ID + " = ?";
        String[] selectionArgs = { String.valueOf(user_create_id) };
        Cursor cursor = db.rawQuery(query, selectionArgs);
        long total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }



    public boolean delete(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = ID + " = ?";
        String[] whereArgs = { String.valueOf(id) };
        return db.delete(TABLE_NAME, whereClause, whereArgs) > 0;
    }

    public boolean update(int id, long amount, String title, String description, String category, String date, int user_create_id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AMOUNT, amount);
        values.put(TITLE, title);
        values.put(DESCRIPTION, description);
        values.put(CATEGORY, category);
        values.put(DATE, date);
        values.put(USER_CREATED_ID, user_create_id);
        String whereClause = ID + " = ?";
        String[] whereArgs = { String.valueOf(id) };
        return db.update(TABLE_NAME, values, whereClause, whereArgs) > 0;
    }


    @SuppressLint("Range")
    public List<ExpenseModel> getAllExpenses(int userId) {
        List<ExpenseModel> expenses = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USER_CREATED_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                ExpenseModel expense = new ExpenseModel();
                expense.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                expense.setAmount(cursor.getLong(cursor.getColumnIndex(AMOUNT)));
                expense.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                expense.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                expense.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
                expense.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
                expense.setUserCreatedId(cursor.getInt(cursor.getColumnIndex(USER_CREATED_ID)));
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return expenses;
    }

        public long getTotalExpenseByCategory(int userId, String category) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT SUM(" + AMOUNT + ") FROM " + TABLE_NAME + " WHERE " + USER_CREATED_ID + " = ? AND " + CATEGORY + " = ?";
        String[] selectionArgs = { String.valueOf(userId), category };
        Cursor cursor = db.rawQuery(query, selectionArgs);
        long total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
        }


        @SuppressLint("Range")
        public List<ExpenseModel> getExpenseCategoryList(int userId) {
        List<ExpenseModel> expenses = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT DISTINCT " + CATEGORY + " FROM " + TABLE_NAME + " WHERE " + USER_CREATED_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                ExpenseModel expense = new ExpenseModel();
                expense.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
                expenses.add(expense);
            } while (cursor.moveToNext());

        }
        cursor.close();
        return expenses;

        }



}
