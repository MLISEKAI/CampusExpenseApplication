package com.nguyennam.campusexpense;

import android.app.Activity;
import android.app.AlertDialog;
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

        etTitle.setText(expense.getTitle());
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        String formattedAmount = numberFormat.format(expense.getAmount());
        etAmount.setText(formattedAmount);
        etCategory.setText(expense.getCategory());
        etDescription.setText(expense.getDescription());
        etDate.setText(expense.getDate());

        // Set dialog buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                expense.setTitle(etTitle.getText().toString());
                try {
                    String amountText = etAmount.getText().toString().replace(",", "");
                    expense.setAmount(Long.parseLong(amountText));
                } catch (NumberFormatException e) {
                    expense.setAmount(0);
                }
                expense.setCategory(etCategory.getText().toString());
                expense.setDescription(etDescription.getText().toString());
                expense.setDate(etDate.getText().toString());

                Expense expenseDb = new Expense(context, null);
                boolean isUpdated = expenseDb.update(expense.getId(), expense.getAmount(), expense.getTitle(),
                        expense.getDescription(), expense.getCategory(), expense.getDate(), expense.getUserCreatedId());

                if (isUpdated) {
                    if (listener != null) {
                        listener.onExpenseUpdated();
                    }
                    Toast.makeText(context, "Expense updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to update expense.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

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
