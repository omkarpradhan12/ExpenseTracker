package com.example.expensetest;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
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
                    db.row_deleter(exp.getExpkey());

                    tableLayout.removeView(tabrow);
                    TextView totalamt = (TextView) findViewById(R.id.totalamt);
                    Double newamt = Double.parseDouble(totalamt.getText().toString()) - Double.parseDouble(exp.getAmount());
                    totalamt.setText(newamt.toString());

                }

            });

            switch(exp.getCategory())
            {
                case "Food":tabrow.setBackgroundColor(getColor(R.color.food));
                break;
                case "Flat":tabrow.setBackgroundColor(getColor(R.color.flat));
                break;
                case "Other":tabrow.setBackgroundColor(getColor(R.color.other));
                break;
                case "Drink":tabrow.setBackgroundColor(getColor(R.color.drink));
                break;
                case "Travel":tabrow.setBackgroundColor(getColor(R.color.travel));
                break;

            }

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

                int month = date.getMonth()+1;
                int day = date.getDayOfMonth();


                String mth = (month<=9) ? "0"+String.valueOf(month):String.valueOf(month);
                String dy = (day<=9) ? "0"+String.valueOf(day):String.valueOf(day);

                String dt = String.valueOf(date.getYear()) + "-" + mth + "-" + dy;
                String reas = reason.getText().toString();
                String cate = category.getSelectedItem().toString();
                String amt = amount.getText().toString();

                if (reas.isEmpty() || cate.equals("Select Categories") || amt.isEmpty()){
                    Toast.makeText(getBaseContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                }
                else {
                    db.addExpense(new Expense(dt,reas,cate,amt));
                }

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

    public void csv_maker(View view)
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater linf = this.getLayoutInflater();
        View dialogview = linf.inflate(R.layout.csv_dialog,null);

        dialogview.setBackgroundColor(getResources().getColor(R.color.bg));

        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(true);


        dialogBuilder.setPositiveButton("Create CSV", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatePicker fromdate = (DatePicker) dialogview.findViewById(R.id.datePickerfrom);
                DatePicker todate = (DatePicker) dialogview.findViewById(R.id.datePickerto);

                int month =0;
                int day =0;

                String mth,dy;

                String frdt,todt;


                month = fromdate.getMonth()+1;
                day = fromdate.getDayOfMonth();
                mth = (month<=9) ? "0"+String.valueOf(month):String.valueOf(month);
                dy = (day<=9) ? "0"+String.valueOf(day):String.valueOf(day);

                frdt = String.valueOf(fromdate.getYear()) + "-" + mth + "-" + dy;

                month = todate.getMonth()+1;
                day = todate.getDayOfMonth();
                mth = (month<=9) ? "0"+String.valueOf(month):String.valueOf(month);
                dy = (day<=9) ? "0"+String.valueOf(day):String.valueOf(day);

                todt = String.valueOf(todate.getYear()) + "-" + mth + "-" + dy;



                filewriter(db.time_period_csv(frdt,todt));

                //Toast.makeText(getBaseContext(),frdt + " - " + todt,Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog csv_maker = dialogBuilder.create();
        csv_maker.show();

    }

    public void filewriter(List<Expense> expenses)
    {
        String data="";
        data+="Date,Reason,Category,Amount\n";

        for(Expense exp:expenses)
        {
            data+= exp.getDate()+","+exp.getReason()+","+exp.getCategory()+","+exp.getAmount()+"\n";
        }

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        root = new File(root , "data.csv");

        try {
            FileOutputStream fout = new FileOutputStream(root);
            fout.write(data.getBytes());
            fout.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

            boolean bool = false;
            try {
                // try to create the file
                bool = root.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getBaseContext(),"Create and saved file in Downloads",Toast.LENGTH_LONG).show();
    }

}