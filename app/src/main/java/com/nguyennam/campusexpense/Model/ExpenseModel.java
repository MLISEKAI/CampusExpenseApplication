package com.nguyennam.campusexpense.Model;

public class ExpenseModel {
    public int id;
    public String title;
    public String category;
    public String description;
    public long amount;
    public String date;
    public int user_create_id;



    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUserCreatedId(int UserCreatedId) {
        this.user_create_id = UserCreatedId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public long getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public int getUserCreatedId() {
        return user_create_id;
    }





}
