package com.example.expensetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;

    ArrayList<String> categories;
    LayoutInflater inflter;
    
    View view;

    public CustomAdapter(Context applicationContext, ArrayList<String> categories) {
        this.context = applicationContext;

        this.categories = categories;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_filter_item, null);
        TextView names = (TextView) view.findViewById(R.id.title);
        names.setText(categories.get(i));
        return view;
    }
}