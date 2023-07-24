package com.example.expensetest;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class Add_Expense extends AppCompatActivity {

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private String cat_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        cat_list = sharedPreferences.getString("category_list", "").replace(" ", "");
        if (cat_list.endsWith(" ")) {
            cat_list = cat_list.substring(0, cat_list.length() - 1);
        }


        ArrayList<String> filter_category = new ArrayList<String>();
        filter_category.add("Click to Select Category");

        Collections.addAll(filter_category, cat_list.split(","));


        AutoCompleteTextView act = findViewById(R.id.autoCompleteTextView_ss);

        act.setDropDownBackgroundResource(R.drawable.pop_bg);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, filter_category);


        act.setThreshold(1);
        act.setAdapter(adapter);



        checkPermission();



    }



    public void cancel(View view)
    {
        finishAffinity();
    }


    public void add_expense(View view)
    {

        expenseDB_Helper db = new expenseDB_Helper(this);

        ArrayList<String> filter_category = new ArrayList<String>();
        filter_category.add("Click to Select Category");

        Collections.addAll(filter_category, cat_list.split(","));


        AutoCompleteTextView act = findViewById(R.id.autoCompleteTextView_ss);

        act.setDropDownBackgroundResource(R.drawable.pop_bg);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, filter_category);


        act.setThreshold(1);
        act.setAdapter(adapter);


                DatePicker date = (DatePicker) findViewById(R.id.datePicker_ss);
                EditText reason = (EditText) findViewById(R.id.reason_get_ss);
//                Spinner categoryget = (Spinner) dialogview.findViewById(R.id.category_get);
                EditText amount = (EditText) findViewById(R.id.amount_get_ss);


                int month = date.getMonth() + 1;
                int day = date.getDayOfMonth();


                String mth = (month <= 9) ? "0" + month : String.valueOf(month);
                String dy = (day <= 9) ? "0" + day : String.valueOf(day);

                String dt = date.getYear() + "-" + mth + "-" + dy;
                String reas = reason.getText().toString();
                //String cate =filter_category.get(categoryget.getSelectedItemPosition());
                String cate = act.getEditableText().toString();


                String amt = amount.getText().toString();

                if (reas.isEmpty() || cate.equals("Click to Select Category") || cate.equals("") || amt.isEmpty()) {
                    Toasty.error(getBaseContext(), "Something went wrong", Toast.LENGTH_LONG, true).show();
                } else {
                    //Toasty.info(MainActivity.this,cate,Toasty.LENGTH_LONG).show();
                    db.addExpense(new Expense(dt, reas, cate, amt));
                    Toasty.success(getApplicationContext(),"Successfully added",Toasty.LENGTH_LONG).show();

                    Intent intent =new Intent(this, Total_Widget.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

                    int[] ids = AppWidgetManager.getInstance(getApplication())
                            .getAppWidgetIds(new ComponentName(getApplication(),
                                    Total_Widget.class));
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    sendBroadcast(intent);

                    this.finishAffinity();
                }
            }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission();
            } else {
                // Do as per your logic 
            }

        }

    }
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }


}