package com.nguyennam.campusexpense;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.nguyennam.campusexpense.Model.ExpenseModel;
import com.nguyennam.campusexpense.SqliteDB.Expense;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends BaseAdapter {

    private List<ExpenseModel> expenseList;
    private LayoutInflater inflater;
    private Context context;
    private OnExpenseUpdatedListener listener;

    public interface OnExpenseUpdatedListener {
        void onExpenseUpdated();
    }

    public ExpenseAdapter(Context context, List<ExpenseModel> expenseList, OnExpenseUpdatedListener listener) {
        this.expenseList = expenseList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return expenseList.size();
    }

    @Override
    public Object getItem(int position) {
        return expenseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_expense, parent, false);
        }

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvAmount = convertView.findViewById(R.id.tvAmount);

        ExpenseModel expense = expenseList.get(position);

        tvTitle.setText(expense.getTitle());
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        String formattedAmount = numberFormat.format(expense.getAmount());
        tvAmount.setText(formattedAmount + " VND");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExpenseDetails(expense);
            }
        });

        return convertView;
    }

    private void showExpenseDetails(final ExpenseModel expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Expense Details");

        // Inflate the custom layout for dialog
        View dialogView = inflater.inflate(R.layout.dialog_expense_details, null);
        builder.setView(dialogView);

        // Find and set the views
        final EditText etTitle = dialogView.findViewById(R.id.etTitle);
        final EditText etAmount = dialogView.findViewById(R.id.etAmount);
        final EditText etCategory = dialogView.findViewById(R.id.etCategory);
        final EditText etDescription = dialogView.findViewById(R.id.etDescription);
        final EditText etDate = dialogView.findViewById(R.id.etDate);

        // Populate data into fields
        etTitle.setText(expense.getTitle());
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        String formattedAmount = numberFormat.format(expense.getAmount());
        etAmount.setText(formattedAmount);
        etCategory.setText(expense.getCategory());
        etDescription.setText(expense.getDescription());
        etDate.setText(expense.getDate());

        // Set up calendar and formatter for date selection
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Set up click listener for etDate to show DatePickerDialog
        etDate.setOnClickListener(v -> {
            String[] dateParts = expense.getDate().split("/");
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // If a valid date is already present, parse and set it as default
            if (dateParts.length == 3) {
                year = Integer.parseInt(dateParts[2]);
                month = Integer.parseInt(dateParts[1]) - 1; // Month is 0-based
                day = Integer.parseInt(dateParts[0]);
            }

            // Show DatePickerDialog
            new DatePickerDialog(context, (view, selectedYear, selectedMonth, selectedDay) -> {
                calendar.set(selectedYear, selectedMonth, selectedDay);
                etDate.setText(dateFormatter.format(calendar.getTime()));
            }, year, month, day).show();
        });

        // Set dialog buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String amountText = etAmount.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            if (title.isEmpty() || amountText.isEmpty() || category.isEmpty() || date.isEmpty()) {
                Toast.makeText(context, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                expense.setTitle(title);
                expense.setAmount(Long.parseLong(amountText.replace(",", "")));
                expense.setCategory(category);
                expense.setDescription(etDescription.getText().toString().trim());
                expense.setDate(date);

                Expense expenseDb = new Expense(context, null);
                boolean isUpdated = expenseDb.update(
                        expense.getId(),
                        expense.getAmount(),
                        expense.getTitle(),
                        expense.getDescription(),
                        expense.getCategory(),
                        expense.getDate(),
                        expense.getUserCreatedId()
                );

                if (isUpdated) {
                    if (listener != null) {
                        listener.onExpenseUpdated();
                    }
                    Toast.makeText(context, "Expense updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to update expense.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid amount entered.", Toast.LENGTH_SHORT).show();
            }
        });


        builder.setNegativeButton("Delete", (dialog, which) -> {
            Expense expenseDb = new Expense(context, null);
            boolean isDeleted = expenseDb.delete(expense.getId());
            if (isDeleted) {
                if (listener != null) {
                    listener.onExpenseUpdated();
                }
                Toast.makeText(context, "Expense deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete expense.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Customize button colors after showing the dialog
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        if (positiveButton != null) {
            positiveButton.setTextColor(ContextCompat.getColor(context, R.color.colorAccent)); // Customize color
        }

        if (negativeButton != null) {
            negativeButton.setTextColor(Color.RED); // Customize color to red
        }

        if (neutralButton != null) {
            neutralButton.setTextColor(Color.GRAY); // Customize color for Cancel button
        }
    }



}
