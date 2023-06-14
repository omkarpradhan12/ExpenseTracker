package com.example.expensetest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class Prediction_Helper {


    private String data_month;
    private int days_in_month;

    private int data_days;
    private int prediction_days;
    private String prediction="";
    public Prediction_Helper(List<Expense> expense_set)
    {
        if(expense_set.size()!=0)
        {
            data_month = expense_set.get(0).getDate().split("-")[1];

            List data_days_list = new ArrayList<>();

            for(Expense exp:expense_set)
            {
                if(!data_days_list.contains(exp.getDate()))
                {
                    data_days_list.add(exp.getDate());
                }
            }

            data_days = data_days_list.size();

            List days_31 = Arrays.asList(new String[]{"01", "03", "05", "07", "08", "10", "12"});

            days_in_month = days_31.contains(data_month) ? 31: data_month=="02" ? 29:30;

            prediction_days = days_in_month-data_days;



            Hashtable<String, Double> cat_sums = category_sums(expense_set);

            Double current_mean = 0.0;
            Double cur_total=0.0;
            Double n=0.0;

            for(String key:cat_sums.keySet())
            {
                cur_total+=cat_sums.get(key);
                n+=1;
            }

            current_mean=cur_total/n;

            Hashtable<String,Double> day_values = new Hashtable<String,Double>();

            for(String key:cat_sums.keySet())
            {
                Double val = cat_sums.get(key);
                if(val<current_mean || val<(current_mean + current_mean/data_days))
                {
                    day_values.put(key,val/data_days);
                }
            }

            Double predicted_sum=0.0;

            for(String key:day_values.keySet())
            {
                predicted_sum += day_values.get(key);
            }

            predicted_sum *= prediction_days;

            double adjustment_factor = cur_total / prediction_days + predicted_sum / data_days;

            double predict_total = ((cur_total+predicted_sum)+(cur_total+predicted_sum+adjustment_factor))/2;

            double final_prediction = prediction_days <= 5 ? (cur_total + predicted_sum) : predict_total;


            prediction += "Final Prediction : "+final_prediction;

        }
        else
        {
            prediction = "Insufficient Data";
        }
    }

    public String get_prediction()
    {
        return prediction;
    }

    public String get_pred_month()
    {
        return data_month+"";
    }

    private Hashtable<String, Double> category_sums(List<Expense> expenses) {
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

}
