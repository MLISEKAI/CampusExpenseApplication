package com.nguyennam.campusexpense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nguyennam.campusexpense.SqliteDB.Budget;

import java.text.NumberFormat;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    private Budget db;
    private TextView tvTotalBudget, tvRemainingBudget;
    private Button btnSetBudget;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        db = new Budget(getContext());
        tvTotalBudget = view.findViewById(R.id.tvTotalBudget);
        tvRemainingBudget = view.findViewById(R.id.tvRemainingBudget);
        btnSetBudget = view.findViewById(R.id.btnSetBudget);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        updateBudgetInfo();

        btnSetBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetBudgetDialog();
            }
        });

        return view;
    }

    // Cập nhật thông tin ngân sách
    private void updateBudgetInfo() {
        if (userId != -1) {
            long totalBudget = db.getTotalBudget(userId);
            long totalExpenses = 0; // Placeholder, nếu có thêm bảng chi phí, hãy thay thế bằng phương thức tính tổng chi phí.
            long remainingBudget = totalBudget - totalExpenses;

            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
            tvTotalBudget.setText("Total Budget: " + numberFormat.format(totalBudget) + " VND");
            tvRemainingBudget.setText("Remaining Budget: " + numberFormat.format(remainingBudget) + " VND");
        }
    }

    // Hiển thị hộp thoại thiết lập ngân sách
    private void showSetBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Set Budget");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_set_budget, null);
        builder.setView(dialogView);

        final EditText etBudget = dialogView.findViewById(R.id.etBudget);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String budgetText = etBudget.getText().toString().trim();

                if (budgetText.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a valid budget", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    long budget = Long.parseLong(budgetText);
                    db.setBudget(userId, budget);
                    Toast.makeText(getContext(), "Budget updated successfully", Toast.LENGTH_SHORT).show();
                    updateBudgetInfo();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid budget amount", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
