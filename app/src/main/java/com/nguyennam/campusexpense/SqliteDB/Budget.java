package com.nguyennam.campusexpense.SqliteDB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.nguyennam.campusexpense.Model.BudgetModel;

import java.util.ArrayList;
import java.util.List;

public class Budget extends SQLiteOpenHelper {

    private static final String NAME = "budget.db";
    private static final int VERSION = 1;
    public static final String TABLE_NAME = "expense";

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String USER_CREATED_ID = "user_create_id";




    public Budget(@Nullable Context context, @Nullable String name) {
        super(context, NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " +
                DESCRIPTION + " TEXT, " +
                USER_CREATED_ID + " INTEGER)";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);

    }

    public long insert(String title, String description, int user_create_id) {
        SQLiteDatabase db = getWritableDatabase();
        // Insert if not exists
        ContentValues values = new ContentValues();
        values.put(TITLE, title);
        values.put(DESCRIPTION, description);
        values.put(USER_CREATED_ID, user_create_id);
        return db.insert(TABLE_NAME, null, values);
    }



    public boolean delete(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = ID + " = ?";
        String[] whereArgs = { String.valueOf(id) };
        return db.delete(TABLE_NAME, whereClause, whereArgs) > 0;
    }

    public boolean update(int id, String title, String description, int user_create_id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, title);
        values.put(DESCRIPTION, description);
        values.put(USER_CREATED_ID, user_create_id);
        String whereClause = ID + " = ?";
        String[] whereArgs = { String.valueOf(id) };
        return db.update(TABLE_NAME, values, whereClause, whereArgs) > 0;
    }


    @SuppressLint("Range")
    public List<BudgetModel> getAllBudget(int userId) {
        List<BudgetModel> budgets = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USER_CREATED_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                BudgetModel budget = new BudgetModel();
                budget.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                budget.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                budget.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                budget.setUserCreatedId(cursor.getInt(cursor.getColumnIndex(USER_CREATED_ID)));
                budgets.add(budget);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return budgets;
    }
}
