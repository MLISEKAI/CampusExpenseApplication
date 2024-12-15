package com.nguyennam.campusexpense.Model;

public class BudgetModel {
    public int id;
    public String title;
    public String description;
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

    public int getUserCreatedId() {
        return user_create_id;
    }





}
