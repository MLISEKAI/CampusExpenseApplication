package com.nguyennam.campusexpense.SqliteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Budget extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CampusExpense.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_BUDGET = "Budget";
    private static final String COLUMN_USER_ID = "UserId";
    private static final String COLUMN_TOTAL_BUDGET = "TotalBudget";

    public Budget(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_BUDGET + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TOTAL_BUDGET + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        onCreate(db);
    }

    public boolean setBudget(int userId, long budget) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_TOTAL_BUDGET, budget);

        long result = db.replace(TABLE_BUDGET, null, values);
        return result != -1;
    }

    public long getTotalBudget(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_BUDGET,
                new String[]{COLUMN_TOTAL_BUDGET},
                COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        );

        long budget = 0;
        if (cursor.moveToFirst()) {
            budget = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_BUDGET));
        }
        cursor.close();
        return budget;
    }
}
