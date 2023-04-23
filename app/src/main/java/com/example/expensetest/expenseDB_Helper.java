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
    private Context c;


    public expenseDB_Helper(Context context) {
        super(context,DB_Name,null,DB_Version);
        c = context;
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {

        MyDB.execSQL("CREATE TABLE if not exists Expenses_Table(Date TEXT, Reason TEXT,Category TEXT,Amount TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    void addExpense(Expense exp)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put("Date",exp.getDate().toString());
        values.put("Reason",exp.getReason());
        values.put("Category",exp.getCategory());
        values.put("Amount",exp.getAmount());

        long result = db.insert("Expenses_Table",null,values);
        if (result==-1) Log.d("Meow : ","Nah Bro");
        else
            Log.d("Insert : ","Yah Bro");
        db.close();
    }

    public List<Expense> getExpenses()
    {
        List<Expense> expenseList = new ArrayList<Expense>();

        String selectQuery = "SELECT * FROM Expenses_Table" ;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                Expense expense = new Expense();
                expense.setDate(cursor.getString(0));
                expense.setReason(cursor.getString(1));
                expense.setCategory(cursor.getString(2));
                expense.setAmount(cursor.getString(3));

                expenseList.add(expense);
            }while(cursor.moveToNext());
        }
        db.close();
        return  expenseList;
    }


}