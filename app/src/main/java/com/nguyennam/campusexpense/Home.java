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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.nguyennam.campusexpense.Model.BudgetModel;
import com.nguyennam.campusexpense.SqliteDB.Budget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Home extends Fragment implements BudgetAdapter.OnBudgetUpdatedListener {

    public Budget db;
    private int userId;
    public Button btnAddBudget;
    private List<BudgetModel> budgetList;
    private ListView lvBudgets;
    private BudgetAdapter budgetAdapter;

   private TextView tvTotalExpenses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = new Budget(getContext(), null);


        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        btnAddBudget = view.findViewById(R.id.btnAddBudget);
        lvBudgets = view.findViewById(R.id.lvBudgets);

        btnAddBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBudgetDialog();
            }
        });

        loadBudgets();
        return view;
    }

    private void showAddBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Budget");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_budget, null);

        builder.setView(dialogView);

        final EditText etTitle = dialogView.findViewById(R.id.etTitleBudget);
        final EditText etDescription = dialogView.findViewById(R.id.etDescriptionBudget);

        Calendar calendar = Calendar.getInstance();

        // Định dạng chỉ hiển thị ngày
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = etTitle.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                if ( title.isEmpty() || description.isEmpty() ) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                long data = db.insert(title, description, userId);
                if (data == -1) {
                    Toast.makeText(getContext(), "Create failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Create success", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadBudgets() {
        budgetList = db.getAllBudget(userId);
        budgetAdapter = new BudgetAdapter(getContext(), budgetList,this); // Pass `this` to the adapter
        lvBudgets.setAdapter(budgetAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBudgetUpdated() {
        loadBudgets();
    }
}
