package com.nguyennam.campusexpense;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nguyennam.campusexpense.Model.ExpenseModel;
import com.nguyennam.campusexpense.Model.User;
import com.nguyennam.campusexpense.SqliteDB.Account;
import com.nguyennam.campusexpense.SqliteDB.Expense;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class Expenses extends Fragment implements ExpenseAdapter.OnExpenseUpdatedListener{

    private Expense db;
    private ListView lvExpenses;
    private TextView tvTotalExpenses;
    private Button btnAddExpense;
    private List<ExpenseModel> expenseList;
    public int userId;
    private ExpenseAdapter expenseAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        db = new Expense(getContext(), null);
        lvExpenses = view.findViewById(R.id.lvExpenses);
        tvTotalExpenses = view.findViewById(R.id.tvTotalExpenses);
        btnAddExpense = view.findViewById(R.id.btnAddExpense);


        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        loadTotalExpenses();
        loadExpenses();

        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExpenseDialog();
            }
        });

        return view;
    }
    private void loadTotalExpenses() {
        long totalExpenses = db.getUserExpenseTotal(userId);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        String formattedAmount = numberFormat.format(totalExpenses);

//        tvTotalExpenses.setText("Total expenses: " + formattedAmount+" VND");
    }


    private void loadExpenses() {
        expenseList = db.getAllExpenses(userId);
        expenseAdapter = new ExpenseAdapter(getContext(), expenseList, this); // Pass `this` to the adapter
        lvExpenses.setAdapter(expenseAdapter);
    }


    private void showAddExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Budget");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_expense, null);

        builder.setView(dialogView);

        final EditText etAmount = dialogView.findViewById(R.id.etAmount);
        final EditText etTitle = dialogView.findViewById(R.id.etTitle);
        final EditText etDescription = dialogView.findViewById(R.id.etDescription);
        final EditText etCategory = dialogView.findViewById(R.id.etCategory);
        final EditText etDate = dialogView.findViewById(R.id.etDate);

        Calendar calendar = Calendar.getInstance();

        // Định dạng chỉ hiển thị ngày
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Hiển thị lịch khi người dùng bấm vào EditText
        etDate.setOnClickListener(v -> {
            // Lấy ngày, tháng, năm hiện tại từ Calendar
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Tạo DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Cập nhật Calendar với ngày được chọn
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                        // Định dạng lại ngày và hiển thị trong EditText
                        etDate.setText(formatter.format(calendar.getTime()));
                    },
                    year, month, day
            );

            // Hiển thị DatePickerDialog
            datePickerDialog.show();
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amountText = etAmount.getText().toString().trim();
                String title = etTitle.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String category = etCategory.getText().toString().trim();
                String date = etDate.getText().toString().trim();

                if (amountText.isEmpty() || title.isEmpty() || description.isEmpty() || category.isEmpty() || date.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                long amount;
                try {
                    amount = Long.parseLong(amountText);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                long data = db.insert(amount, title, description, category, date, userId);
                if (data == -1) {
                    Toast.makeText(getContext(), "Create failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Create success", Toast.LENGTH_SHORT).show();
                    loadTotalExpenses();
                    loadExpenses();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onExpenseUpdated() {
        loadTotalExpenses();
        loadExpenses();
    }
}
