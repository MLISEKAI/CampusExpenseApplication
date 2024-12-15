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

import com.nguyennam.campusexpense.Model.BudgetModel;
import com.nguyennam.campusexpense.Model.ExpenseModel;
import com.nguyennam.campusexpense.SqliteDB.Budget;
import com.nguyennam.campusexpense.SqliteDB.Expense;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends BaseAdapter {

    private List<BudgetModel> budgetList;
    private LayoutInflater inflater;
    private Context context;
    private OnBudgetUpdatedListener listener;

    public interface OnBudgetUpdatedListener {
        void onBudgetUpdated();
    }

    public BudgetAdapter(Context context, List<BudgetModel> budgetList, OnBudgetUpdatedListener listener) {
        this.budgetList = budgetList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return budgetList.size();
    }

    @Override
    public Object getItem(int position) {
        return budgetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_budget, parent, false);
        }

        TextView tvTitle = convertView.findViewById(R.id.tvTitleBudget);

        BudgetModel budget = budgetList.get(position);

        tvTitle.setText(budget.getTitle());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExpenseDetails(budget);
            }
        });

        return convertView;
    }

    private void showExpenseDetails(final BudgetModel budget) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Budget Details");

        // Inflate the custom layout for dialog
        View dialogView = inflater.inflate(R.layout.dialog_budget_details, null);
        builder.setView(dialogView);

        // Find and set the views
        final EditText etTitle = dialogView.findViewById(R.id.etTitle);
        final EditText etDescription = dialogView.findViewById(R.id.etDescription);

        // Populate data into fields
        etTitle.setText(budget.getTitle());
        etDescription.setText(budget.getDescription());

        // Set dialog buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(context, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                budget.setTitle(title);
                budget.setDescription(description);

                Budget budgetDb = new Budget(context, null);
                boolean isUpdated = budgetDb.update(
                        budget.getId(),
                        budget.getTitle(),
                        budget.getDescription(),
                        budget.getUserCreatedId()
                );

                if (isUpdated) {
                    if (listener != null) {
                        listener.onBudgetUpdated();
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
            Budget budgetDb = new Budget(context, null);
            boolean isDeleted = budgetDb.delete(budget.getId());
            if (isDeleted) {
                if (listener != null) {
                    listener.onBudgetUpdated();
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
