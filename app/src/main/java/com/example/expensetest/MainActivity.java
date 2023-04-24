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

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TableLayout tableLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        table_update(getall());

        Spinner category = (Spinner) findViewById(R.id.filter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int postion, long arg3) {
                // TODO Auto-generated method stub
                String  category = parent.getItemAtPosition(postion).toString();
                if (postion==0)
                {
                    table_update(getall());
                }
                else {
                    Toast.makeText(getBaseContext(),category,Toast.LENGTH_SHORT).show();
                    table_update(getfiltered(category));
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    private Double total_calculator(List<Expense> expenses)
    {
        Double total = 0.0;

        for(Expense exp:expenses){

            total+=Double.parseDouble(exp.getAmount());

        }

        return total;
    }

    public List<Expense> getall()
    {
        expenseDB_Helper db = new expenseDB_Helper(this);
        List<Expense> expenses = db.getExpenses();

        Double total = total_calculator(expenses);
        TextView totalamt = (TextView) findViewById(R.id.totalamt);

        totalamt.setText(total.toString());

        return expenses;
    }
    public List<Expense> getfiltered(String category)
    {
        expenseDB_Helper db = new expenseDB_Helper(this);
        List<Expense> expenses = db.filter_expense(category);

        Double total = total_calculator(expenses);
        TextView totalamt = (TextView) findViewById(R.id.totalamt);

        totalamt.setText(total.toString());

        return expenses;
    }

    public void table_update(List<Expense> expenses)
    {

        tableLayout=(TableLayout)findViewById(R.id.expensetable);

        tableLayout.removeAllViews();


        expenseDB_Helper db = new expenseDB_Helper(this);

        for(Expense exp:expenses){

            View tabrow = LayoutInflater.from(this).inflate(R.layout.table_item,null,false);

            int key = exp.getExpkey();

            TextView date = (TextView)tabrow.findViewById(R.id.date);
            date.setText(exp.getDate());

            TextView reason = (TextView)tabrow.findViewById(R.id.reason);
            reason.setText(exp.getReason());

            TextView category = (TextView)tabrow.findViewById(R.id.category);
            category.setText(exp.getCategory());

            TextView amount = (TextView)tabrow.findViewById(R.id.amount);
            amount.setText(exp.getAmount());

            Button delete = (Button)tabrow.findViewById(R.id.deleter);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getBaseContext(),String.valueOf(db.row_deleter(exp.getExpkey())),Toast.LENGTH_SHORT).show();
                }
            });

            tableLayout.addView(tabrow);


        }

    }

    public void new_expense(View view)
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater linf = this.getLayoutInflater();
        View dialogview = linf.inflate(R.layout.new_expense,null);

        dialogview.setBackgroundColor(getResources().getColor(R.color.bg));

        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DatePicker date = (DatePicker) dialogview.findViewById(R.id.datePicker);
                EditText reason = (EditText) dialogview.findViewById(R.id.reason_get);
                Spinner category = (Spinner) dialogview.findViewById(R.id.category_get);
                EditText amount = (EditText) dialogview.findViewById(R.id.amount_get);


                String dt = String.valueOf(date.getYear()) + "-" + String.valueOf(date.getMonth()+1) + "-" + String.valueOf(date.getDayOfMonth());
                String reas = reason.getText().toString();
                String cate = category.getSelectedItem().toString();
                String amt = amount.getText().toString();

                db.addExpense(new Expense(dt,reas,cate,amt));
                table_update(getall());
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