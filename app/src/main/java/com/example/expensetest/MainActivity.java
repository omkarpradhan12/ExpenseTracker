package com.example.expensetest;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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


    //flags for sorting
    private Boolean dateflag = true;
    private Boolean reasonflag = true;
    private Boolean categoryflag = true;
    private Boolean amountflag = true;

    private TableLayout tableLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        table_update(getall());

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#070A52"));

        actionBar.setBackgroundDrawable(colorDrawable);

        TextView datesorter = (TextView) findViewById(R.id.datesorter);
        datesorter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateSorter();
            }
        });

        TextView reasonsorter = (TextView) findViewById(R.id.reasonsorter);
        reasonsorter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReasonSorter();
            }
        });

        TextView categorysorter = (TextView) findViewById(R.id.categorysorter);
        categorysorter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategorySorter();
            }
        });

        TextView amountsorter = (TextView) findViewById(R.id.amountsorter);
        amountsorter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmountSorter();
            }
        });

        TextView totalamt = (TextView) findViewById(R.id.totalamt);

        Button deleter = (Button) findViewById(R.id.clearall);
        deleter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete All Records ?")
                        .setMessage("This will clear all existing records from the database")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                ClearAll();
                                Toast.makeText(MainActivity.this, "Clearing all", Toast.LENGTH_SHORT).show();
                                totalamt.setText("0.0");
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

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

    public void ClearAll()
    {
        expenseDB_Helper db = new expenseDB_Helper(this);
        List<Expense> expenses = db.clearall();

        table_update(expenses);

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



            switch(exp.getCategory())
            {
                case "Food":tabrow.setBackgroundDrawable(getDrawable(R.drawable.food));
                break;
                case "Flat":tabrow.setBackgroundDrawable(getDrawable(R.drawable.flat));
                break;
                case "Other":tabrow.setBackgroundDrawable(getDrawable(R.drawable.other));
                break;
                case "Drink":tabrow.setBackgroundDrawable(getDrawable(R.drawable.drink));
                break;
                case "Travel":tabrow.setBackgroundDrawable(getDrawable(R.drawable.travel));
                break;

            };

            tabrow.setLongClickable(true);
            tabrow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete Record ?")
                            .setMessage("Date : "+exp.getDate()+
                                    "\nReason : "+exp.getReason()+
                                    "\nCategory : "+exp.getCategory()+
                                    "\nAmount : "+exp.getAmount())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Delete Record", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //ClearAll();
                                    db.row_deleter(exp.getExpkey());

                                    tableLayout.removeView(tabrow);
                                    TextView totalamt = (TextView) findViewById(R.id.totalamt);
                                    Double newamt = Double.parseDouble(totalamt.getText().toString()) - Double.parseDouble(exp.getAmount());
                                    totalamt.setText(newamt.toString());
                                    Toast.makeText(MainActivity.this, "Removed Record", Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                    return false;
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
                Toast.makeText(getApplicationContext(),"Baadme Bhulega to rona mat 🤦‍",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog add_new_expense = dialogBuilder.create();



        add_new_expense.show();

    }

    public void DateSorter()
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        dateflag = !dateflag;

        table_update(db.sortdate(dateflag));

    }
    public void ReasonSorter()
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        reasonflag = !reasonflag;

        table_update(db.sortreason(reasonflag));

    }
    public void CategorySorter()
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        categoryflag = !categoryflag;

        table_update(db.sortcategory(categoryflag));

    }

    public void AmountSorter()
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        amountflag = !amountflag;

        table_update(db.amountsort(amountflag));

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

                EditText fname = (EditText) dialogview.findViewById(R.id.fname);

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



                filewriter(db.time_period_csv(frdt,todt),fname.getText().toString());

                //Toast.makeText(getBaseContext(),frdt + " - " + todt,Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog csv_maker = dialogBuilder.create();
        csv_maker.show();

    }

    public void filewriter(List<Expense> expenses,String fname)
    {
        String data="";
        data+="Date,Reason,Category,Amount\n";

        for(Expense exp:expenses)
        {
            data+= exp.getDate()+","+exp.getReason()+","+exp.getCategory()+","+exp.getAmount()+"\n";
        }

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        String file_name = "expenses.csv";

        if (fname.isEmpty()==false)
        {
            file_name = fname+".csv";
        }


        root = new File(root , file_name);

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

        Toast.makeText(getBaseContext(),"Create and saved file "+file_name+" in Downloads",Toast.LENGTH_LONG).show();
    }

}