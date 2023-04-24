package com.example.expensetest;

import java.util.Date;

public class Expense {



    int expkey;
    String date;
    String Reason;
    String Category;
    String Amount;

    public Expense()
    {

    }
    public Expense(String date, String Reason, String Category, String Amount)
    {
        this.date = date;
        this.Reason = Reason;
        this.Category = Category;
        this.Amount = Amount;
    }

    public int getExpkey() {
        return expkey;
    }

    public void setExpkey(int expkey) {
        this.expkey = expkey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
}
