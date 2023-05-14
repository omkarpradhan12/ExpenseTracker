package com.example.expensetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Graph extends AppCompatActivity {


    List<Expense> expenses;
    BarChart barChart;
    PieChart pieChart;

    boolean flag=true;

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



        tab_maker(cat_sums);
        bar_maker(cat_sums);




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


        pieChart = findViewById(R.id.pieChart_view);
        setupPieChart();
        loadPieChartData(cat_sums);

        LinearLayout graphcontainer = (LinearLayout) findViewById(R.id.graph_container);
        graphcontainer.removeAllViews();
        graphcontainer.addView(barChart);


    }

    public void tab_maker(Hashtable<String, Double> cat_sums)
    {
        TableLayout dynamic_table = (TableLayout) findViewById(R.id.dynamictable);
        dynamic_table.removeAllViews();

        for(String category:cat_sums.keySet())
        {
            View tabrow = LayoutInflater.from(this).inflate(R.layout.visual_table_item,null,false);

            TextView cat_key = (TextView) tabrow.findViewById(R.id.cat_key);
            cat_key.setText(category);

            TextView cat_value = (TextView) tabrow.findViewById(R.id.cat_value);
            cat_value.setText(cat_sums.get(category).toString());

            tabrow.setClickable(true);
            tabrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Graph.this,category+" : "+cat_sums.get(category),Toast.LENGTH_LONG).show();
                }
            });

            dynamic_table.addView(tabrow);


        }

        Double gt = 0.0;
        for(Double tot:cat_sums.values())
        {
            gt+=tot;
        }

        TextView grand_total = (TextView) findViewById(R.id.grand_total);
        grand_total.setText(gt.toString());


    }


    public void bar_maker(Hashtable<String, Double> cat_sums)
    {
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
        ArrayList<String> label_List = new ArrayList<String>();

        for(String cat_val:cat_sums.keySet())
        {
            valueList.add(cat_sums.get(cat_val));
            label_List.add(cat_val);
        }



        for (int i = 0; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
            entries.add(barEntry);
        }

        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xaxis.setValueFormatter(new IndexAxisValueFormatter(label_List));

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);


        BarData data = new BarData(barDataSet);

        barChart.setData(data);

        data.setValueTextColor(Color.parseColor("#ffffff"));
        data.setValueTextSize(15);

        xaxis.setGranularity(1);
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();



        barChart.animateY(1400, Easing.EaseInOutQuad);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(Graph.this,label_List.get(Math.round(e.getX()))+" : "+e.getY(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    public void switcher(View view)
    {
        flag = !flag;

        LinearLayout graphcontainer = (LinearLayout) findViewById(R.id.graph_container);
        TextView bartab = (TextView) findViewById(R.id.bartab);
        TextView pietab = (TextView) findViewById(R.id.pietab);

        graphcontainer.removeAllViews();

        if(flag)
        {
            pietab.setBackgroundColor(Color.parseColor("#6E6666"));
            bartab.setBackgroundColor(Color.parseColor("#918888"));
            graphcontainer.addView(barChart);
            barChart.animateY(1400, Easing.EaseInOutQuad);
        }

        else
        {
            bartab.setBackgroundColor(Color.parseColor("#6E6666"));
            pietab.setBackgroundColor(Color.parseColor("#918888"));
            graphcontainer.addView(pieChart);
            pieChart.animateY(1400, Easing.EaseInOutQuad);
        }
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setCenterText("Spending by Category");
        pieChart.setCenterTextSize(18);
        pieChart.getDescription().setEnabled(false);


    }

    private void loadPieChartData(Hashtable<String, Double> cat_sums) {


        Double gt = 0.0;

        for(String k:cat_sums.keySet())
        {
            gt+=cat_sums.get(k);
        }



        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<String> label_List = new ArrayList<String>();

        for(String category:cat_sums.keySet())
        {
            entries.add(new PieEntry((float) (cat_sums.get(category)/gt), category));
            label_List.add(category);
        }
//
//        entries.add(new PieEntry((float) (cat_sums.get("Food")/gt), "Food"));
//        entries.add(new PieEntry((float) (cat_sums.get("Drink")/gt), "Drink"));
//        entries.add(new PieEntry((float) (cat_sums.get("Flat")/gt), "Flat"));
//        entries.add(new PieEntry((float) (cat_sums.get("Travel")/gt), "Travel"));
//        entries.add(new PieEntry((float) (cat_sums.get("Other")/gt), "Other"));





        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
//        dataSet.setColors(new int []{
//                Color.parseColor("#70D53B"),//food
//                Color.parseColor("#7DBBDD"),//drink
//                Color.parseColor("#989393"),//flat
//                Color.parseColor("#F1905C"),//travel
//                Color.parseColor("#EC6161") //other
//        });
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();


        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(1400, Easing.EaseInOutQuad);

        Double finalGt = gt;
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(Graph.this,label_List.get(Math.round(h.getX()))+" : "+Math.round(e.getY()* finalGt),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }


    public Hashtable<String, Double> category_sums(List<Expense> expenses)
    {
        Hashtable<String, Double> cat_sum= new Hashtable<String, Double>();

        ArrayList<String> nkeys = new ArrayList<String>();


        for(Expense exp:expenses)
        {
            if(!nkeys.contains(exp.getCategory()))
            {
                nkeys.add(exp.getCategory());

            }
        }



//        cat_sum.put("Food", 0.0);
//        cat_sum.put("Flat", 0.0);
//        cat_sum.put("Drink", 0.0);
//        cat_sum.put("Other", 0.0);
//        cat_sum.put("Travel", 0.0);

        for (String key:nkeys)
        {
            cat_sum.put(key,0.0);
        }

        Double food=0.0,flat=0.0,drink=0.0,other=0.0,travel=0.0;





        for(Expense exp:expenses)
        {
            Double temp = cat_sum.get(exp.getCategory())+ Double.parseDouble(exp.getAmount());
            cat_sum.replace(exp.getCategory(),temp);
        }


        return cat_sum;

    }


    public void go_home(View view)
    {
        Intent myIntent = new Intent(Graph.this, MainActivity.class);
        Graph.this.startActivity(myIntent);
    }


}