package com.example.expensetest;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        table_update();


    }

    public void table_update()
    {
        expenseDB_Helper db = new expenseDB_Helper(this);
        List<Expense> expenses = db.getExpenses();
        tableLayout=(TableLayout)findViewById(R.id.expensetable);

        tableLayout.removeAllViews();



        for(Expense exp:expenses){

            View tabrow = LayoutInflater.from(this).inflate(R.layout.table_item,null,false);

            TextView date = (TextView)tabrow.findViewById(R.id.date);
            date.setText(exp.getDate());

            TextView reason = (TextView)tabrow.findViewById(R.id.reason);
            reason.setText(exp.getReason());

            TextView category = (TextView)tabrow.findViewById(R.id.category);
            category.setText(exp.getCategory());

            TextView amount = (TextView)tabrow.findViewById(R.id.amount);
            amount.setText(exp.getAmount());

            tableLayout.addView(tabrow);


        }

    }

    public void new_expense(View view)
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater linf = this.getLayoutInflater();
        View dialogview = linf.inflate(R.layout.new_expense,null);

        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DatePicker date = (DatePicker) dialogview.findViewById(R.id.datePicker);
                EditText reason = (EditText) dialogview.findViewById(R.id.reason_get);
                EditText category = (EditText) dialogview.findViewById(R.id.category_get);
                EditText amount = (EditText) dialogview.findViewById(R.id.amount_get);


                String dt = String.valueOf(date.getDayOfMonth()) + "-" + String.valueOf(date.getMonth()) + "-" + String.valueOf(date.getYear());
                String reas = reason.getText().toString();
                String cate = category.getText().toString();
                String amt = amount.getText().toString();


                db.addExpense(new Expense(dt,reas,cate,amt));
                table_update();

            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(),"Okay",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog add_new_expense = dialogBuilder.create();



        add_new_expense.show();

    }
}