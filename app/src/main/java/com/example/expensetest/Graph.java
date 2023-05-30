package com.example.expensetest;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class Graph extends AppCompatActivity {


    List<Expense> expenses;
    BarChart barChart;
    PieChart pieChart;

    boolean flag=true;

    String cat_color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        cat_color = sharedPreferences.getString("Cat_Colors","");


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


        String cat_colors = cat_color+"";


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
                Toasty.success(Graph.this,"Saving Image : "+fname+" in Downloads",Toast.LENGTH_SHORT,true).show();

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

        Hashtable<String,Integer> cat_color_table = new Hashtable<>();


        String cat_colors=cat_color+"";
        cat_colors = cat_colors.trim();

        Double gt = 0.0;
        for(Double tot:cat_sums.values())
        {
            gt+=tot;
        }


        for(String x:cat_colors.split("\n"))
        {
            if(x.split(":").length==2)
            {
                cat_color_table.put(x.split(":")[0],Integer.parseInt(x.split(":")[1]));
            }
        }


        for(String category:cat_sums.keySet())
        {
            View tabrow = LayoutInflater.from(dynamic_table.getContext()).inflate(R.layout.visual_table_item,dynamic_table,false);





            TextView cat_key = (TextView) tabrow.findViewById(R.id.cat_key);
            cat_key.setText(category);

            TextView cat_value = (TextView) tabrow.findViewById(R.id.cat_value);
            cat_value.setText("₹ "+cat_sums.get(category).toString());

            ExtendedFloatingActionButton fab_test = tabrow.findViewById(R.id.fab_test);

            ProgressBar cat_prg = (ProgressBar) tabrow.findViewById(R.id.cat_progress);



            if(cat_color_table.keySet().contains(category))
            {
//                GradientDrawable shape = new GradientDrawable();
//
//                shape.setShape(GradientDrawable.OVAL);
//                shape.setSize(200,200);
//                shape.setStroke(1, Color.parseColor("#ffffff"));
//                shape.setColor(cat_color_table.get(category));



                cat_prg.setProgressTintList(ColorStateList.valueOf(cat_color_table.get(category)));
                Double perc =  (cat_sums.get(category)/gt)*100;
                Log.d("Perc : ",""+perc);
                cat_prg.setProgress(perc.intValue());
//                cat_key.setBackgroundDrawable(shape);
//                cat_value.setBackgroundDrawable(shape);



                fab_test.setBackgroundTintList(ColorStateList.valueOf(cat_color_table.get(category)));
                fab_test.setText(perc.toString().substring(0,4)+"%");

                fab_test.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Snackbar.make(view,category+" : "+perc.toString().substring(0,4)+"%",Snackbar.LENGTH_SHORT).show();
                        Toasty.info(Graph.this,category+" : "+perc.toString().substring(0,4)+"%",Toasty.LENGTH_SHORT,false).show();
                    }
                });


            }

            else
            {
                tabrow.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            tabrow.setClickable(true);
            tabrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toasty.info(Graph.this,category+" : "+cat_sums.get(category),Toast.LENGTH_LONG,false).show();
                }
            });

            dynamic_table.addView(tabrow);


        }



        TextView grand_total = (TextView) findViewById(R.id.grand_total);
        grand_total.setText("Total Amount Spent ₹"+gt.toString());


    }


    public void bar_maker(Hashtable<String, Double> cat_sums)
    {
        barChart = findViewById(R.id.category_graph);

        barChart.getAxisLeft().setTextColor(Color.parseColor("#ffffff")); // left y-axis
        barChart.getXAxis().setTextColor(Color.parseColor("#ffffff"));
        barChart.getLegend().setTextColor(Color.parseColor("#ffffff"));
        barChart.getDescription().setEnabled(false);


        Hashtable<String,Integer> cat_color_table = new Hashtable<>();

        ArrayList<Integer> ncolors = new ArrayList<>();

        String cat_colors=cat_color+"";
        cat_colors = cat_colors.trim();


        for(String x:cat_colors.split("\n"))
        {
            if(x.split(":").length==2)
            {
                cat_color_table.put(x.split(":")[0],Integer.parseInt(x.split(":")[1]));
            }
        }

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

            if(cat_color_table.keySet().contains(label_List.get(i)))
            {
                ncolors.add(cat_color_table.get(label_List.get(i)));
            }

            else
            {
                ncolors.add(0);
            }

        }

        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xaxis.setValueFormatter(new IndexAxisValueFormatter(label_List));

        BarDataSet barDataSet = new BarDataSet(entries, "");


        barDataSet.setColors(ncolors);


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
                Toasty.info(Graph.this,label_List.get(Math.round(e.getX()))+" : "+e.getY(),Toast.LENGTH_LONG,false).show();
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


        Hashtable<String,Integer> cat_color_table = new Hashtable<>();

        ArrayList<Integer> ncolors = new ArrayList<>();

        String cat_colors=cat_color+"";
        cat_colors = cat_colors.trim();


        for(String x:cat_colors.split("\n"))
        {
            if(x.split(":").length==2)
            {
                cat_color_table.put(x.split(":")[0],Integer.parseInt(x.split(":")[1]));
            }
        }


        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<String> label_List = new ArrayList<String>();

        for(String category:cat_sums.keySet())
        {
            entries.add(new PieEntry((float) (cat_sums.get(category)/gt), category));
            label_List.add(category);
            ncolors.add(cat_color_table.get(category));
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

        int[] colors = new int[]{
                Color.parseColor("#2979FF"),  // Blue
                Color.parseColor("#9FA8DA"),
                Color.parseColor("#C5CAE9"),
                Color.parseColor("#E8EAF6"),
                Color.parseColor("#F8BBD0"),
                Color.parseColor("#F48FB1"),
                Color.parseColor("#F06292"),
                Color.parseColor("#EC407A"),
                Color.parseColor("#E91E63"),
                Color.parseColor("#D81B60"),
                Color.parseColor("#C2185B"),
                Color.parseColor("#AD1457"),
                Color.parseColor("#880E4F"),
                Color.parseColor("#FF1744")   // Red
        };


        dataSet.setColors(ncolors);
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
                Toasty.info(Graph.this,label_List.get(Math.round(h.getX()))+" : "+Math.round(e.getY()* finalGt),Toast.LENGTH_LONG,false).show();
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