package com.nguyennam.campusexpense;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.nguyennam.campusexpense.SqliteDB.Expense;
import com.nguyennam.campusexpense.Model.ExpenseModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Home extends Fragment {

    private Expense dbHelper;
    private int userId;
    private TextView tvTotalByCategory;
    private Spinner spinnerCategories;

   private TextView tvTotalExpenses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new Expense(getContext(), null);
        tvTotalByCategory = view.findViewById(R.id.tvTotalByCategory);
        spinnerCategories = view.findViewById(R.id.spinnerCategories);

        tvTotalExpenses = view.findViewById(R.id.tvTotalExpenses);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        updateCategories();
        updateTotalExpenses();

        return view;
    }


    private void updateTotalExpenses() {
        if (userId != -1) {
            long total = dbHelper.getUserExpenseTotal(userId);
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
            String formattedAmount = numberFormat.format(total);
            tvTotalExpenses.setText("Total expenses: " + formattedAmount + " VND");
        }
    }

    private void updateCategories() {
        if (userId != -1) {
            List<ExpenseModel> categories = dbHelper.getExpenseCategoryList(userId);
            List<String> categoryNames = new ArrayList<>();
            for (ExpenseModel category : categories) {
                categoryNames.add(category.getCategory());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategories.setAdapter(adapter);

            spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCategory = categoryNames.get(position);
                    long total = dbHelper.getTotalExpenseByCategory(userId, selectedCategory);


                    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
                    String formattedAmount = numberFormat.format(total);

                    tvTotalByCategory.setText(selectedCategory + ": " + formattedAmount + " VND");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    tvTotalByCategory.setText("Total by category: 0 VND");
                }
            });
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        updateCategories();
        updateTotalExpenses();
    }
}
