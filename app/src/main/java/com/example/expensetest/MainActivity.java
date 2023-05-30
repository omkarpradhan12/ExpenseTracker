package com.example.expensetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {


    private static final int PICKFILE_RESULT_CODE = 2000;
    //flags for sorting
    private Boolean dateflag = false;
    private Boolean reasonflag = true;
    private Boolean categoryflag = true;
    private Boolean amountflag = true;

    private TableLayout tableLayout;

    private String cat_list;
    private String cat_color;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        cat_list = sharedPreferences.getString("category_list", "").replace(" ","");
        cat_color = sharedPreferences.getString("Cat_Colors","");


        chip_driver();


        if(cat_list.endsWith(" "))
        {
            cat_list = cat_list.substring(0,cat_list.length() - 1);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        TextView titlebar = findViewById(R.id.titlebar);
        titlebar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Dev_Show(view);
                return false;
            }
        });


        TextView totalamt = (TextView) findViewById(R.id.totalamt);


        ArrayList<String> filter_category = new ArrayList<String>();
        filter_category.add("Click to Apply Filter");
        filter_category.add("All");

        for(String cat:cat_list.split(","))
        {
            filter_category.add(cat);
        }






        TextView helpme = findViewById(R.id.helme);
        helpme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Help_Show(view);
            }
        });


        Context wrapper = new ContextThemeWrapper(MainActivity.this,R.style.PopupMenuStyle2);



        BottomAppBar bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphme(view);
            }
        });





        TextView sort_title = (TextView) findViewById(R.id.sort_title);
        sort_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort_title.setText("Expenses");
                table_update(getall());
            }
        });

        menu_driver();
        task_menu();
        table_update(getall());

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ChipGroup cg = (ChipGroup) findViewById(R.id.filter_chips);
        cg.clearCheck();
    }

    public void task_menu()
    {
        TextView totalamt = (TextView) findViewById(R.id.totalamt);
        ImageButton task_menu_button = findViewById(R.id.task_menu_button);

        task_menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context wrapper = new ContextThemeWrapper(MainActivity.this,R.style.PopupMenuStyle1);

                PopupMenu task_popup = new PopupMenu(wrapper,view);

                task_popup.getMenuInflater().inflate(R.menu.bottom_app_bar_menu,task_popup.getMenu());

                task_popup.setForceShowIcon(true);

                task_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId() == R.id.menu_clear_text)
                        {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Delete All Records ?")
                                    .setMessage("This will clear all existing records from the database")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            ClearAll();
                                            Toasty.error(MainActivity.this, "Clearing all", Toast.LENGTH_SHORT,true).show();
                                            totalamt.setText("₹ 0.0");
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();
                            return true;
                        }

                        if(item.getItemId() == R.id.menu_read_csv)
                        {
                            file_picker();
                            return true;
                        }
                        if(item.getItemId() == R.id.menu_save_csv)
                        {
                            csv_maker();
                            return true;

                        }
                        if(item.getItemId() == R.id.date_filter)
                        {
                            date_options();
                            return true;
                        }

                        return true;
                    }
                });

                task_popup.show();
            }
        });
    }

    public void menu_driver()
    {
        ImageButton sort_button = (ImageButton) findViewById(R.id.sort_button);

        sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context wrapper = new ContextThemeWrapper(MainActivity.this,R.style.PopupMenuStyle2);

                PopupMenu sort_popup = new PopupMenu(wrapper,view);

                sort_popup.getMenuInflater().inflate(R.menu.sort_menu,sort_popup.getMenu());

                sort_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId()==R.id.menu_Amount_sort)
                        {
                            AmountSorter();
                            return true;
                        }

                        if(item.getItemId()==R.id.menu_date_sort)
                        {
                            DateSorter();
                            return true;
                        }

                        if(item.getItemId()==R.id.menu_Category_sort)
                        {
                            CategorySorter();
                            return true;
                        }

                        if(item.getItemId()==R.id.menu_reason_sort)
                        {
                            ReasonSorter();
                            return true;
                        }
                        return true;
                    }
                });

                sort_popup.show();
            }
        });

    }
    private int chipid;
    public void chip_driver()
    {
        ChipGroup filter_group = (ChipGroup) findViewById(R.id.filter_chips);

        chipid=1;
        filter_group.removeAllViews();

        HashMap<Integer,String> filter_map = new HashMap<>();

        TextView sort_title = (TextView) findViewById(R.id.sort_title);


        TextView totalamt = (TextView) findViewById(R.id.totalamt);

        for(String category:cat_list.split(","))
        {
            Chip chip = new Chip(this);
            chip.setId(chipid);
            chip.setText(category);
            chip.setChipBackgroundColor(getColorStateList(R.color.chip_color));
            chip.setTextColor(getColorStateList(R.color.text_color));
            chip.setChipStrokeWidth(2);
            chip.setChipStrokeColor(getColorStateList(R.color.borde_colors));
            chip.setCheckable(true);
            chip.setFocusable(true);
            //chip.setChecked(true);
            filter_map.put(chipid,category);
            filter_group.addView(chip);

            chipid+=1;

        }

        filter_group.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {




                if(cat_list.split(",").length==checkedIds.size() || checkedIds.size()==0)
                {
                    sort_title.setText("Expenses");
                    table_update(getall());
                }
                else
                {
                    List<Expense> filtered_expense = new ArrayList<>();
                    String temps = "";
                    for(int id:checkedIds)
                    {
                        for(Expense exp:getfiltered(filter_map.get(id)))
                        {
                            filtered_expense.add((exp));
                        }

                    }



                    totalamt.setText("₹ "+total_calculator(filtered_expense));
                    sort_title.setText("Expenses");
                    table_update(filtered_expense);

                }

            }
        });

        filter_group.setClickable(true);
        filter_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_group.clearCheck();
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

        totalamt.setText("₹ "+total.toString());

        return expenses;
    }
    public List<Expense> getfiltered(String category)
    {
        expenseDB_Helper db = new expenseDB_Helper(this);
        List<Expense> expenses = db.filter_expense(category);

        Double total = total_calculator(expenses);
        TextView totalamt = (TextView) findViewById(R.id.totalamt);

        totalamt.setText("₹ "+total.toString());

        return expenses;
    }

    public void date_options()
    {


        expenseDB_Helper db = new expenseDB_Helper(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater linf = this.getLayoutInflater();
        View dialogview = linf.inflate(R.layout.graphdialog,null);

        TextView reason_dialog = dialogview.findViewById(R.id.reason_dialog);
        reason_dialog.setText("Select Dates for Filter");


        dialogview.setBackgroundColor(getResources().getColor(R.color.bg));

        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(true);


        dialogBuilder.setPositiveButton("Show For Dates", new DialogInterface.OnClickListener() {
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


                List<Expense> db_tp = db.time_period(frdt, todt);

                Double total = total_calculator(db_tp);
                TextView totalamt = (TextView) findViewById(R.id.totalamt);

                totalamt.setText("₹ "+total.toString());

                table_update(db_tp);

            }
        });

        AlertDialog date_filter = dialogBuilder.create();
        date_filter.show();


    }

    public void table_update(List<Expense> expenses)
    {

        Hashtable<String,Integer> cat_color_table = new Hashtable<>();


        String cat_colors=cat_color+"";
        cat_colors = cat_colors.trim();


        for(String x:cat_colors.split("\n"))
        {
            if(x.split(":").length==2)
            {
                cat_color_table.put(x.split(":")[0],Integer.parseInt(x.split(":")[1]));
            }
        }





        tableLayout=(TableLayout)findViewById(R.id.expensetable);

        tableLayout.removeAllViews();

        String s="";
        expenseDB_Helper db = new expenseDB_Helper(this);

        for(Expense exp:expenses){

//            View tabrow = LayoutInflater.from(this).inflate(R.layout.table_item,null,false);
//
//            int key = exp.getExpkey();
//
//
//            TextView date = (TextView)tabrow.findViewById(R.id.date);
//            date.setText(exp.getDate());
//
//            TextView reason = (TextView)tabrow.findViewById(R.id.reason);
//            reason.setText(exp.getReason());
//
//            TextView category = (TextView)tabrow.findViewById(R.id.category);
//            category.setText(exp.getCategory());
//
//            TextView amount = (TextView)tabrow.findViewById(R.id.amount);
//            amount.setText(exp.getAmount());


            View tabrow = LayoutInflater.from(this).inflate(R.layout.table_card,tableLayout,false);

            MaterialCardView mcard = (MaterialCardView)tabrow.findViewById(R.id.tab_card);

            TextView date = (TextView) tabrow.findViewById(R.id.date_card);
            date.setText(exp.getDate());

            TextView reason = (TextView)tabrow.findViewById(R.id.reason_card);
            reason.setText(exp.getReason());

            TextView category = (TextView)tabrow.findViewById(R.id.cat_card);
            category.setText(exp.getCategory());

            TextView amount = (TextView)tabrow.findViewById(R.id.amount_card);
            amount.setText("₹ "+exp.getAmount());




            if(cat_color_table.keySet().contains(exp.getCategory()))
            {
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setStroke(1, Color.parseColor("#ffffff"));
                shape.setColor(cat_color_table.get(exp.getCategory()));

                tabrow.setBackgroundDrawable(shape);
            }

            else
            {
                tabrow.setBackgroundColor(Color.parseColor("#788780"));
            }


//            switch(exp.getCategory())
//            {
//                case "Food":tabrow.setBackgroundDrawable(getDrawable(R.drawable.food));
//                break;
//                case "Flat":tabrow.setBackgroundDrawable(getDrawable(R.drawable.flat));
//                break;
//                case "Other":tabrow.setBackgroundDrawable(getDrawable(R.drawable.other));
//                break;
//                case "Drink":tabrow.setBackgroundDrawable(getDrawable(R.drawable.drink));
//                break;
//                case "Travel":tabrow.setBackgroundDrawable(getDrawable(R.drawable.travel));
//                break;
//                default:tabrow.setBackgroundDrawable(getDrawable(R.drawable.default_color));
//
//            };



            mcard.setLongClickable(true);
            mcard.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    totalamt.setText("₹ "+newamt.toString());
                                    Toasty.warning(MainActivity.this, "Removed Record", Toast.LENGTH_SHORT,true).show();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                    return false;
                }
            });

            mcard.setClickable(true);
            mcard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edit_expenese(exp);
                }
            });

            tableLayout.addView(tabrow);


        }



    }

    public void graphme(View view)
    {
        Intent myIntent = new Intent(MainActivity.this, Graph.class);

        expenseDB_Helper db = new expenseDB_Helper(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater linf = this.getLayoutInflater();
        View dialogview = linf.inflate(R.layout.graphdialog,null);

        dialogview.setBackgroundColor(getResources().getColor(R.color.bg));

        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(true);

        TextView reason_dialog = dialogview.findViewById(R.id.reason_dialog);
        reason_dialog.setText("Select Dates for Visualization");
        dialogBuilder.setPositiveButton("Visualize For Dates", new DialogInterface.OnClickListener() {
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

                myIntent.putExtra("fromdate",frdt);
                myIntent.putExtra("todate",todt);
                MainActivity.this.startActivity(myIntent);


                //Toast.makeText(getBaseContext(),frdt + " - " + todt,Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog grapher_dialog = dialogBuilder.create();
        grapher_dialog.show();




    }

    public void edit_expenese(Expense exp)
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater linf = this.getLayoutInflater();
        View dialogview = linf.inflate(R.layout.new_expense,null);

        dialogview.setBackgroundColor(getResources().getColor(R.color.bg));



        dialogBuilder.setView(dialogview);

        DatePicker datep = (DatePicker) dialogview.findViewById(R.id.datePicker);
        EditText reasonp = (EditText) dialogview.findViewById(R.id.reason_get);

        Spinner categoryset = (Spinner) dialogview.findViewById(R.id.category_get);

        ArrayList<String> filter_category = new ArrayList<String>();
        filter_category.add("Click to Select Category");

        for(String cat:cat_list.split(","))
        {
            filter_category.add(cat);
        }

        CustomAdapter2 cad2 = new CustomAdapter2(MainActivity.this,filter_category);
        categoryset.setAdapter(cad2);

        EditText amountp = (EditText) dialogview.findViewById(R.id.amount_get);

        String[] dt_set = exp.getDate().split("-");



        datep.updateDate(Integer.parseInt(dt_set[0]),Integer.parseInt(dt_set[1])-1,Integer.parseInt(dt_set[2]));
        reasonp.setText(exp.getReason());
        amountp.setText(exp.getAmount());
        categoryset.setSelection(filter_category.indexOf(exp.getCategory()));

        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
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
                String cate =filter_category.get(category.getSelectedItemPosition());
                String amt = amount.getText().toString();

                if (reas.isEmpty() || cate.equals("Click to Select Category") || amt.isEmpty()){
                    Toasty.error(getBaseContext(),"Something went wrong",Toast.LENGTH_LONG,true).show();
                }
                else {
                    db.editExpense(new Expense(exp.getExpkey(),dt,reas,cate,amt));
                    Toasty.success(getApplicationContext(),"Edited Entry successfully",Toasty.LENGTH_LONG,true).show();
                }

                ChipGroup cg = findViewById(R.id.filter_chips);
                cg.clearCheck();

                table_update(getall());
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getApplicationContext(),"Baadme Bhulega to rona mat 🤦‍",Toast.LENGTH_SHORT).show();
                Toasty.warning(getApplicationContext(),"No Changes made to entry",Toasty.LENGTH_LONG,true).show();
            }
        });

        AlertDialog edit_expense = dialogBuilder.create();

        edit_expense.show();
//        edit_expense.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void new_expense(View view)
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater linf = this.getLayoutInflater();
        View dialogview = linf.inflate(R.layout.new_expense,null);
        Spinner categoryset = (Spinner) dialogview.findViewById(R.id.category_get);

        ArrayList<String> filter_category = new ArrayList<String>();
        filter_category.add("Click to Select Category");

        for(String cat:cat_list.split(","))
        {
            filter_category.add(cat);
        }



        CustomAdapter2 cad2 = new CustomAdapter2(MainActivity.this,filter_category);
        categoryset.setAdapter(cad2);

        dialogview.setBackgroundColor(getResources().getColor(R.color.bg));

        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DatePicker date = (DatePicker) dialogview.findViewById(R.id.datePicker);
                EditText reason = (EditText) dialogview.findViewById(R.id.reason_get);
                Spinner categoryget = (Spinner) dialogview.findViewById(R.id.category_get);
                EditText amount = (EditText) dialogview.findViewById(R.id.amount_get);



                int month = date.getMonth()+1;
                int day = date.getDayOfMonth();


                String mth = (month<=9) ? "0"+String.valueOf(month):String.valueOf(month);
                String dy = (day<=9) ? "0"+String.valueOf(day):String.valueOf(day);

                String dt = String.valueOf(date.getYear()) + "-" + mth + "-" + dy;
                String reas = reason.getText().toString();
                String cate =filter_category.get(categoryget.getSelectedItemPosition());






                String amt = amount.getText().toString();

                if (reas.isEmpty() || cate.equals("Click to Select Category") || amt.isEmpty()){
                    Toasty.error(getBaseContext(),"Something went wrong",Toast.LENGTH_LONG,true).show();
                }
                else {
                    db.addExpense(new Expense(dt,reas, cate,amt));
                }

                ChipGroup cg = findViewById(R.id.filter_chips);
                cg.clearCheck();
                table_update(getall());
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toasty.warning(getApplicationContext(),"No new entry added",Toast.LENGTH_SHORT,true).show();
            }
        });

        AlertDialog add_new_expense = dialogBuilder.create();



        add_new_expense.show();
//        add_new_expense.getWindow().setBackgroundDrawable(new ColorDrawable(R.drawable.bg));
    }



    public void DateSorter()
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        ChipGroup cg = (ChipGroup) findViewById(R.id.filter_chips);
        cg.clearCheck();

        dateflag = !dateflag;
        TextView sorter = findViewById(R.id.sort_title);
        if(dateflag)
            sorter.setText("Expenses (Date ↑)");
        else
            sorter.setText("Expenses (Date ↓)");

        table_update(db.sortdate(dateflag));

    }
    public void ReasonSorter()
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        ChipGroup cg = (ChipGroup) findViewById(R.id.filter_chips);
        cg.clearCheck();

        reasonflag = !reasonflag;
        TextView sorter = findViewById(R.id.sort_title);
        if(reasonflag)
            sorter.setText("Expenses (Reason ↑)");
        else
            sorter.setText("Expenses (Reason ↓)");

        table_update(db.sortreason(reasonflag));

    }
    public void CategorySorter()
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        ChipGroup cg = (ChipGroup) findViewById(R.id.filter_chips);
        cg.clearCheck();

        categoryflag = !categoryflag;
        TextView sorter = findViewById(R.id.sort_title);
        if(categoryflag)
            sorter.setText("Expenses (Category ↑)");
        else
            sorter.setText("Expenses (Category ↓)");

        table_update(db.sortcategory(categoryflag));

    }

    public void AmountSorter()
    {
        expenseDB_Helper db = new expenseDB_Helper(this);

        ChipGroup cg = (ChipGroup) findViewById(R.id.filter_chips);
        cg.clearCheck();

        amountflag = !amountflag;
        TextView sorter = findViewById(R.id.sort_title);
        if(amountflag)
            sorter.setText("Expenses (Amount ↑)");
        else
            sorter.setText("Expenses (Amount ↓)");

        table_update(db.amountsort(amountflag));

    }
    public void csv_maker()
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
                EditText fname = (EditText) dialogview.findViewById(R.id.filename);

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



                filewriter(db.time_period(frdt,todt),frdt,todt,fname.getText().toString());

                //Toast.makeText(getBaseContext(),frdt + " - " + todt,Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog csv_maker = dialogBuilder.create();
        csv_maker.show();

    }

    public void filewriter(List<Expense> expenses,String frdt,String todt, String fname)
    {
        String data="";
        data+="Date,Reason,Category,Amount\n";

        for(Expense exp:expenses)
        {
            data+= exp.getDate()+","+exp.getReason()+","+exp.getCategory()+","+exp.getAmount()+"\n";
        }

        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        String file_name = frdt+"_"+todt+".csv";

        if(fname.isEmpty())
        {
            file_name = frdt+"_"+todt+".csv";
        }
        else
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
                Log.d("Exception : ",ex.toString());
            }

        } catch (IOException e) {
            Log.d("Exception : ",e.toString());
        }

        Toasty.success(getBaseContext(),"Created and saved file "+file_name+" in Downloads",Toast.LENGTH_LONG,true).show();
    }


    public void Dev_Show(View view)
    {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dev_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void Help_Show(View view)
    {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.help_dialog, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }


    public void file_picker()
    {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("text/*");
        startActivityForResult(
                Intent.createChooser(chooseFile, "Select a CSV"),
                PICKFILE_RESULT_CODE
        );


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            File file = new File(uri.getPath());//create path from uri
            final String[] split = file.getPath().split(":");//split the path.
            reader(split[1]);
        }
    }


    public void reader(String filename){


        File file = new File(filename);

        String string = "";
        StringBuilder stringBuilder = new StringBuilder();

        List<String> fileContent = new ArrayList<String>();

        InputStream is = null;
        try {
            is = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                if ((string = reader.readLine()) == null) break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            fileContent.add(string);

        }
        try {
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        verify(fileContent);

    }


    public void verify(List<String> fileContent)
    {

        List<String> todb = new ArrayList<String>();

        if(fileContent.get(0).equals("Date,Reason,Category,Amount"))
        {


            for(int i=1;i<fileContent.size();i++)
            {
                todb.add(fileContent.get(i));
            }
            send_to_db(todb);
        }


        else{
            Toasty.error(MainActivity.this,"Unsupported CSV found",Toast.LENGTH_LONG,true).show();
        }


    }


    public  void send_to_db(List<String> todb)
    {
        expenseDB_Helper db = new expenseDB_Helper(MainActivity.this);


        List<String> new_to_db = new ArrayList<String>();

        List<String> db_content = new ArrayList<String>();

        for(Expense exp:db.getExpenses())
        {
            db_content.add(exp.getDate()+","+exp.getReason()+","+exp.getCategory()+","+exp.getAmount());
        }

        int row_add_count = 0;

        for(String exp:todb)
        {
            String[] data = exp.split(",");

            String[] dt_chk = data[0].split("-");

            if (dt_chk[0].length()==4 && dt_chk[1].length()==2 && dt_chk[2].length()==2)
            {
                if(!data[1].isEmpty() && !data[2].isEmpty() && !data[3].isEmpty())
                {
                    Log.d("Format","Correct ");

                    if(!db_content.contains(exp))
                    {
                        row_add_count+=1;
                        db.addExpense(new Expense(data[0],data[1],data[2],data[3]));
                    }



                }
                else
                {
                    Log.d("Format","Incorrect");
                }
            }
            else
            {
                Log.d("Format","Incorrect : "+data[0]);
            }
        }

        Toasty.success(MainActivity.this,row_add_count+" Rows Added",Toast.LENGTH_LONG,true).show();
        table_update(getall());

    }
}