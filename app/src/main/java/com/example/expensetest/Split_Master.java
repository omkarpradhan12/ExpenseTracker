package com.example.expensetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class Split_Master extends AppCompatActivity {

    private EditText editTextTextMultiLine;

    private SharedPreferences sp;

    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_master);
        editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine);

        sp = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sp.edit();

        if (!sp.contains("split_expense")) {
            editor.putString("split_expense", " ");
            editor.commit();
        }else {
            editTextTextMultiLine.setText(sp.getString("split_expense",""));
        }

    }

    public void save_text(View view)
    {
        editor.putString("split_expense", editTextTextMultiLine.getText().toString());
        editor.commit();
    }

    public  void clear_text(View view)
    {
        editTextTextMultiLine.setText(" ");
        editor.putString("split_expense", " ");
        editor.commit();
    }
}