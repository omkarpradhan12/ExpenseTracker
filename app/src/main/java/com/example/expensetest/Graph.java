package com.example.expensetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class Graph extends AppCompatActivity {


    List<Expense> expenses;
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        TextView frdt = findViewById(R.id.frdt);
        TextView todt = findViewById(R.id.todt);

        Bundle extras = getIntent().getExtras();
        String fromdate,todate;

        if (extras != null) {
            fromdate = extras.getString("fromdate");
            todate = extras.getString("todate");
            // and get whatever type user account id is
        }

        else
        {
            fromdate="";
            todate="";
        }

        expenseDB_Helper db = new expenseDB_Helper(this);

        frdt.setText(fromdate);
        todt.setText(todate);

        Hashtable<String, Double> cat_sums = category_sums(db.time_period(fromdate, todate));



        TextView food = (TextView) findViewById(R.id.food);
        food.setText(cat_sums.get("Food").toString());

        TextView flat = (TextView) findViewById(R.id.flat);
        flat.setText(cat_sums.get("Flat").toString());

        TextView drink = (TextView) findViewById(R.id.drink);
        drink.setText(cat_sums.get("Drink").toString());

        TextView other = (TextView) findViewById(R.id.other);
        other.setText(cat_sums.get("Other").toString());

        TextView travel = (TextView) findViewById(R.id.travel);
        travel.setText(cat_sums.get("Travel").toString());

        TextView gt = (TextView) findViewById(R.id.grandtotal);
        Double Grand_Total = cat_sums.get("Food") + cat_sums.get("Flat") + cat_sums.get("Drink") + cat_sums.get("Other") +cat_sums.get("Travel");
        gt.setText(Grand_Total.toString());


        barChart = findViewById(R.id.category_graph);

        barChart.getAxisLeft().setTextColor(Color.parseColor("#ffffff")); // left y-axis
        barChart.getXAxis().setTextColor(Color.parseColor("#ffffff"));
        barChart.getLegend().setTextColor(Color.parseColor("#ffffff"));
        barChart.getDescription().setEnabled(false);



        XAxis xaxis = barChart.getXAxis();
        YAxis yaxis = barChart.getAxisRight();
        xaxis.setTextColor(Color.WHITE);
        yaxis.setTextColor(Color.WHITE);



        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<BarEntry> entries = new ArrayList<>();


        //input data
        valueList.add(cat_sums.get("Food"));
        valueList.add(cat_sums.get("Drink"));
        valueList.add(cat_sums.get("Flat"));
        valueList.add(cat_sums.get("Travel"));
        valueList.add(cat_sums.get("Other"));

        //fit the data into a bar
        String[] labels = {"Food","Drink","Flat","Travel","Other"};

        for (int i = 0; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
            entries.add(barEntry);
        }

        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xaxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(new int []{
                            Color.parseColor("#70D53B"),//food
                            Color.parseColor("#7DBBDD"),//drink
                            Color.parseColor("#989393"),//flat
                            Color.parseColor("#F1905C"),//travel
                            Color.parseColor("#EC6161") //other
        });


        BarData data = new BarData(barDataSet);

        barChart.setData(data);

        data.setValueTextColor(Color.parseColor("#ffffff"));
        data.setValueTextSize(15);

        xaxis.setGranularity(1);
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(Graph.this,labels[Math.round(e.getX())],Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });


        TextView save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fname = fromdate+"_to_"+todate;

                try {
                    // image naming and path  to include sd card  appending name you choose for file

                    String mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fname + ".jpg";


                    // create bitmap screen capture
                    View v1 = getWindow().getDecorView().getRootView();
                    v1.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                    v1.setDrawingCacheEnabled(false);

                    File imageFile = new File(mPath);

                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();



                } catch (Throwable e) {
                    // Several error may come out with file handling or DOM
                    e.printStackTrace();
                }
                Toast.makeText(Graph.this,"Saving Image : "+fname+" in Downloads",Toast.LENGTH_SHORT).show();

            }
        });

    }




    public Hashtable<String, Double> category_sums(List<Expense> expenses)
    {
        Hashtable<String, Double> cat_sum= new Hashtable<String, Double>();
        
        cat_sum.put("Food", 0.0);
        cat_sum.put("Flat", 0.0);
        cat_sum.put("Drink", 0.0);
        cat_sum.put("Other", 0.0);
        cat_sum.put("Travel", 0.0);

        Double food=0.0,flat=0.0,drink=0.0,other=0.0,travel=0.0;


        for(Expense exp:expenses)
        {
            switch(exp.getCategory())
            {
                case "Food": food+=Double.parseDouble(exp.getAmount());
                break;
                case "Flat": flat+=Double.parseDouble(exp.getAmount());
                break;
                case "Drink": drink+=Double.parseDouble(exp.getAmount());
                break;
                case "Other": other+=Double.parseDouble(exp.getAmount());
                break;
                case "Travel": travel+=Double.parseDouble(exp.getAmount());
                break;
            }
        }

        cat_sum.replace("Food",food);
        cat_sum.replace("Flat",flat);
        cat_sum.replace("Drink",drink);
        cat_sum.replace("Other",other);
        cat_sum.replace("Travel",travel);

        return cat_sum;

    }



}