package com.example.expensetest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class expenseDB_Helper extends SQLiteOpenHelper {

    private static final int DB_Version = 1;
    private static final String DB_Name = "Expense.db";
    private final Context c;


    public expenseDB_Helper(Context context) {
        super(context, DB_Name, null, DB_Version);
        c = context;
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {

        MyDB.execSQL("CREATE TABLE if not exists Expenses_Table(ExpenseKey INTEGER PRIMARY KEY AUTOINCREMENT,Date Date, Reason TEXT,Category TEXT,Amount TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    void addExpense(Expense exp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put("Date", exp.getDate());
        values.put("Reason", exp.getReason());
        values.put("Category", exp.getCategory());
        values.put("Amount", exp.getAmount());

        long result = db.insert("Expenses_Table", null, values);
        if (result == -1) Log.d("Meow : ", "Nah Bro");
        else
            Log.d("Insert : ", "Yah Bro");
        db.close();
    }

    void editExpense(Expense exp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put("Date", exp.getDate());
        values.put("Reason", exp.getReason());
        values.put("Category", exp.getCategory());
        values.put("Amount", exp.getAmount());

        Log.d("key : ", String.valueOf(exp.getExpkey()));

        db.update("Expenses_Table", values, "ExpenseKey=?", new String[]{String.valueOf(exp.getExpkey())});


        db.close();
    }

    public List<String> getCategories() {
        String selectQuery = "SELECT DISTINCT(Category) FROM Expenses_Table";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<String> cat_list = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                cat_list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        db.close();
        return cat_list;

    }

    public List<Expense> getExpenses() {
        List<Expense> expenseList = new ArrayList<Expense>();

        String selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Date DESC,Amount DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setExpkey(cursor.getInt(0));
                expense.setDate(cursor.getString(1));
                expense.setReason(cursor.getString(2));
                expense.setCategory(cursor.getString(3));
                expense.setAmount(cursor.getString(4));

                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        db.close();
        return expenseList;
    }

    public List<Expense> filter_expense(String category) {
        List<Expense> expenseList = new ArrayList<Expense>();

        String selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table WHERE Category LIKE '" + category + "' ORDER by Date,Amount DESC";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setExpkey(cursor.getInt(0));
                expense.setDate(cursor.getString(1));
                expense.setReason(cursor.getString(2));
                expense.setCategory(cursor.getString(3));
                expense.setAmount(cursor.getString(4));

                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        db.close();
        return expenseList;
    }

    public List<Expense> time_period(String fromdate, String todate) {
        List<Expense> expenseList = new ArrayList<Expense>();
        String selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table WHERE Date BETWEEN '" + fromdate + "' and '" + todate + "' ORDER by Date,Amount DESC";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setExpkey(cursor.getInt(0));
                expense.setDate(cursor.getString(1));
                expense.setReason(cursor.getString(2));
                expense.setCategory(cursor.getString(3));
                expense.setAmount(cursor.getString(4));

                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        db.close();
        return expenseList;
    }

    public boolean row_deleter(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("Expenses_Table", "ExpenseKey=?", new String[]{Integer.toString(id)}) > 0;
    }

    public List<Expense> sortdate(Boolean flag) {
        String selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Date, Amount DESC";

        if (flag) {
            selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Date, Amount DESC";
        } else {
            selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Date DESC, Amount DESC";
        }

        List<Expense> expenseList = new ArrayList<Expense>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setExpkey(cursor.getInt(0));
                expense.setDate(cursor.getString(1));
                expense.setReason(cursor.getString(2));
                expense.setCategory(cursor.getString(3));
                expense.setAmount(cursor.getString(4));

                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        db.close();
        return expenseList;
    }

    public List<Expense> sortreason(Boolean flag) {
        String selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Reason, Amount DESC";

        if (flag) {
            selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Reason, Amount DESC";
        } else {
            selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Reason DESC, Amount DESC";
        }

        List<Expense> expenseList = new ArrayList<Expense>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setExpkey(cursor.getInt(0));
                expense.setDate(cursor.getString(1));
                expense.setReason(cursor.getString(2));
                expense.setCategory(cursor.getString(3));
                expense.setAmount(cursor.getString(4));

                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        db.close();
        return expenseList;
    }

    public List<Expense> sortcategory(Boolean flag) {
        String selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Category, Amount DESC";

        if (flag) {
            selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Category, Amount DESC";
        } else {
            selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Category DESC, Amount DESC";
        }

        List<Expense> expenseList = new ArrayList<Expense>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setExpkey(cursor.getInt(0));
                expense.setDate(cursor.getString(1));
                expense.setReason(cursor.getString(2));
                expense.setCategory(cursor.getString(3));
                expense.setAmount(cursor.getString(4));

                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        db.close();
        return expenseList;
    }


    public List<Expense> amountsort(Boolean flag) {
        String selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Amount";

        if (flag) {
            selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Amount";
        } else {
            selectQuery = "SELECT ExpenseKey,Date,Reason,Category,CAST(Amount as Double)[Amount] FROM Expenses_Table ORDER by Amount DESC";
        }

        List<Expense> expenseList = new ArrayList<Expense>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setExpkey(cursor.getInt(0));
                expense.setDate(cursor.getString(1));
                expense.setReason(cursor.getString(2));
                expense.setCategory(cursor.getString(3));
                expense.setAmount(cursor.getString(4));

                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        db.close();
        return expenseList;
    }

    public List<Expense> clearall() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM Expenses_Table");

        List<Expense> expenseList = new ArrayList<Expense>();
        return expenseList;
    }

}