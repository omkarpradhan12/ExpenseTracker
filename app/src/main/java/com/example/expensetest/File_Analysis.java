package com.example.expensetest;



import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class File_Analysis extends AppCompatActivity {

    private static final int PICKFILE_RESULT_CODE = 2000;

    List<Expense> expenses;
    BarChart barChart;
    PieChart pieChart;

    boolean flag = true;

    String cat_color;


    private EditText filename_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_analysis);

        filename_et = (EditText) findViewById(R.id.filename_analysis);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        cat_color = sharedPreferences.getString("Cat_Colors", "");


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        pieChart = findViewById(R.id.pieChart_view);

    }




    public void file_analysis_picker(View view) {
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
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File file = new File(uri.getPath());//create path from uri
            final String[] split = file.getPath().split(":");//split the path.



            if(split[0].endsWith("msf")) {
                final File file_t = new File(getCacheDir(), uri.getLastPathSegment());
                try (final InputStream inputStream = getContentResolver().openInputStream(uri);
                     OutputStream output = new FileOutputStream(file_t)) {
                    // You may need to change buffer size. I use large buffer size to help loading large file , but be ware of
                    //  OutOfMemory Exception
                    final byte[] buffer = new byte[8 * 1024];
                    int read;

                    while ((read = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }

                    output.flush();
                    reader(file_t.getPath());

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
            else {
                reader(split[1]);
            }


        }
    }

    public void reader(String filename) {


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
            } catch (IOException e) {
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

        filename_et.setText(filename);
        Toasty.info(getBaseContext(),filename,Toasty.LENGTH_LONG).show();



    }


    private String month;
    public void verify(List<String> fileContent) {

        List<Expense> todb = new ArrayList<Expense>();

        if (fileContent.get(0).equals("Date,Reason,Category,Amount") && fileContent.size()>1) {

            month = fileContent.get(1).split(",")[0];

            for (int i = 1; i < fileContent.size(); i++) {
                ;
                Expense expense = new Expense(fileContent.get(i).split(",")[0],fileContent.get(i).split(",")[1],fileContent.get(i).split(",")[2],fileContent.get(i).split(",")[3]);
                todb.add(expense);
            }
            //send_to_db(todb);
            setup_graphs(todb);

        } else {
            Toasty.error(File_Analysis.this, "Unsupported CSV found", Toast.LENGTH_LONG, true).show();
        }


    }

    public void setup_graphs(List<Expense> fileContent){

        Hashtable<String, Double> cat_sums = category_sums(fileContent);
        tab_maker(cat_sums);
        bar_maker(cat_sums);


        setupPieChart();
        loadPieChartData(cat_sums);

        LinearLayout graphcontainer = (LinearLayout) findViewById(R.id.graph_container);
        graphcontainer.removeAllViews();
        graphcontainer.addView(barChart);


        flag =true;
    }

    public Hashtable<String, Double> category_sums(List<Expense> expenses) {
        Hashtable<String, Double> cat_sum = new Hashtable<String, Double>();

        ArrayList<String> nkeys = new ArrayList<String>();


        for (Expense exp : expenses) {
            if (!nkeys.contains(exp.getCategory())) {
                nkeys.add(exp.getCategory());

            }
        }


//        cat_sum.put("Food", 0.0);
//        cat_sum.put("Flat", 0.0);
//        cat_sum.put("Drink", 0.0);
//        cat_sum.put("Other", 0.0);
//        cat_sum.put("Travel", 0.0);

        for (String key : nkeys) {
            cat_sum.put(key, 0.0);
        }

        for (Expense exp : expenses) {
            Double temp = cat_sum.get(exp.getCategory()) + Double.parseDouble(exp.getAmount());
            cat_sum.replace(exp.getCategory(), temp);
        }


        return cat_sum;

    }



    public void tab_maker(Hashtable<String, Double> cat_sums) {
        TableLayout dynamic_table = (TableLayout) findViewById(R.id.dynamictable);
        dynamic_table.removeAllViews();

        Hashtable<String, Integer> cat_color_table = new Hashtable<>();


        String cat_colors = cat_color + "";
        cat_colors = cat_colors.trim();

        Double gt = 0.0;
        for (Double tot : cat_sums.values()) {
            gt += tot;
        }


        for (String x : cat_colors.split("\n")) {
            if (x.split(":").length == 2) {
                cat_color_table.put(x.split(":")[0], Integer.parseInt(x.split(":")[1]));
            }
        }


        for (String category : cat_sums.keySet()) {
            View tabrow = LayoutInflater.from(dynamic_table.getContext()).inflate(R.layout.visual_table_item, dynamic_table, false);


            TextView cat_key = (TextView) tabrow.findViewById(R.id.cat_key);
            cat_key.setText(category);

            TextView cat_value = (TextView) tabrow.findViewById(R.id.cat_value);
            cat_value.setText("₹ " + cat_sums.get(category).toString());

            ExtendedFloatingActionButton fab_test = tabrow.findViewById(R.id.fab_test);

            ProgressBar cat_prg = (ProgressBar) tabrow.findViewById(R.id.cat_progress);


            if (cat_color_table.containsKey(category)) {
//                GradientDrawable shape = new GradientDrawable();
//
//                shape.setShape(GradientDrawable.OVAL);
//                shape.setSize(200,200);
//                shape.setStroke(1, Color.parseColor("#ffffff"));
//                shape.setColor(cat_color_table.get(category));


                cat_prg.setProgressTintList(ColorStateList.valueOf(cat_color_table.get(category)));
                Double perc = (cat_sums.get(category) / gt) * 100;
                Log.d("Perc : ", "" + perc);
                cat_prg.setProgress(perc.intValue());
//                cat_key.setBackgroundDrawable(shape);
//                cat_value.setBackgroundDrawable(shape);


                fab_test.setBackgroundTintList(ColorStateList.valueOf(cat_color_table.get(category)));
                fab_test.setText(perc.toString().substring(0, 4) + "%");

                fab_test.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Snackbar.make(view,category+" : "+perc.toString().substring(0,4)+"%",Snackbar.LENGTH_SHORT).show();
                        Toasty.info(File_Analysis.this, category + " : " + perc.toString().substring(0, 4) + "%", Toasty.LENGTH_SHORT, false).show();
                    }
                });


            } else {
                tabrow.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            tabrow.setClickable(true);
            tabrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toasty.info(File_Analysis.this, category + " : " + cat_sums.get(category), Toast.LENGTH_LONG, false).show();
                }
            });

            dynamic_table.addView(tabrow);


        }

        Hashtable<String,String> month_names = new Hashtable<>();
        month_names.put("01","January");
        month_names.put("02","February");
        month_names.put("03","March");
        month_names.put("04","April");
        month_names.put("05","May");
        month_names.put("06","June");
        month_names.put("07","July");
        month_names.put("08","August");
        month_names.put("09","September");
        month_names.put("10","October");
        month_names.put("11","November");
        month_names.put("12","December");



        TextView month_name = (TextView) findViewById(R.id.Month_Name);
        month_name.setText(month_names.get(month.split("-")[1]));


        TextView grand_total = (TextView) findViewById(R.id.grand_total);
        grand_total.setText("Total Amount Spent : ₹" + gt);


    }


    public void bar_maker(Hashtable<String, Double> cat_sums) {
        barChart = findViewById(R.id.category_graph);

        barChart.getAxisLeft().setTextColor(Color.parseColor("#ffffff")); // left y-axis
        barChart.getXAxis().setTextColor(Color.parseColor("#ffffff"));
        barChart.getLegend().setTextColor(Color.parseColor("#ffffff"));
        barChart.getDescription().setEnabled(false);


        Hashtable<String, Integer> cat_color_table = new Hashtable<>();

        ArrayList<Integer> ncolors = new ArrayList<>();

        String cat_colors = cat_color + "";
        cat_colors = cat_colors.trim();


        for (String x : cat_colors.split("\n")) {
            if (x.split(":").length == 2) {
                cat_color_table.put(x.split(":")[0], Integer.parseInt(x.split(":")[1]));
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

        for (String cat_val : cat_sums.keySet()) {
            valueList.add(cat_sums.get(cat_val));
            label_List.add(cat_val);
        }


        for (int i = 0; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
            entries.add(barEntry);

            if (cat_color_table.containsKey(label_List.get(i))) {
                ncolors.add(cat_color_table.get(label_List.get(i)));
            } else {
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
                Toasty.info(File_Analysis.this, label_List.get(Math.round(e.getX())) + " : " + e.getY(), Toast.LENGTH_LONG, false).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    public void switcher(View view) {
        flag = !flag;

        LinearLayout graphcontainer = (LinearLayout) findViewById(R.id.graph_container);
        TextView bartab = (TextView) findViewById(R.id.bartab);
        TextView pietab = (TextView) findViewById(R.id.pietab);

        graphcontainer.removeAllViews();

        if (flag) {
            pietab.setBackgroundColor(Color.parseColor("#6E6666"));
            bartab.setBackgroundColor(Color.parseColor("#918888"));
            graphcontainer.addView(barChart);
            barChart.animateY(1400, Easing.EaseInOutQuad);
        } else {
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

        for (String k : cat_sums.keySet()) {
            gt += cat_sums.get(k);
        }


        Hashtable<String, Integer> cat_color_table = new Hashtable<>();

        ArrayList<Integer> ncolors = new ArrayList<>();

        String cat_colors = cat_color + "";
        cat_colors = cat_colors.trim();


        for (String x : cat_colors.split("\n")) {
            if (x.split(":").length == 2) {
                cat_color_table.put(x.split(":")[0], Integer.parseInt(x.split(":")[1]));
            }
        }


        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<String> label_List = new ArrayList<String>();

        for (String category : cat_sums.keySet()) {
            entries.add(new PieEntry((float) (cat_sums.get(category) / gt), category));
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
                Toasty.info(File_Analysis.this, label_List.get(Math.round(h.getX())) + " : " + Math.round(e.getY() * finalGt), Toast.LENGTH_LONG, false).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
}