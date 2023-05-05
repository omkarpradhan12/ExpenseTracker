package com.example.expensetest;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Launcher extends AppCompatActivity {

    private static final int REQUEST_WRITE_PERMISSION = 786;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {

        }
    }


    public void setpass(View view)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!sharedPreferences.contains("password"))
        {
            Toast.makeText(Launcher.this,"No Password found\nDefault: Password",Toast.LENGTH_LONG).show();
            editor.putString("password","Password");
            editor.commit();
        }

        else
        {
            String pword = sharedPreferences.getString("password","");


            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater linf = this.getLayoutInflater();
            View dialogview = linf.inflate(R.layout.password_dialog,null);

            dialogview.setBackgroundColor(getResources().getColor(R.color.bg));

            dialogBuilder.setView(dialogview);
            dialogBuilder.setCancelable(true);
            String x = pword;
            dialogBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                        EditText oldpword = (EditText) dialogview.findViewById(R.id.oldpword);
                        EditText newpword = (EditText) dialogview.findViewById(R.id.newpword);

                        if(x.equals(oldpword.getText().toString()))
                        {
                            if(newpword.getText().toString().isEmpty())
                            {
                                Toast.makeText(Launcher.this,"Password cannot be empty",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                editor.putString("password",newpword.getText().toString()).apply();
                                Toast.makeText(Launcher.this,"Password changed",Toast.LENGTH_LONG).show();
                            }
                        }

                        else
                        {
                            Toast.makeText(Launcher.this,"Incorrect Old Password",Toast.LENGTH_LONG).show();
                        }



                }
            });

            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(),"Later Then",Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog password_changer = dialogBuilder.create();
            password_changer.show();



        }


    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        requestPermission();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }



        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        EditText pass = (EditText) findViewById(R.id.editTextTextPassword2);

        if(!sharedPreferences.contains("password"))
        {
            Toast.makeText(Launcher.this,"No Password found\nDefault: Password",Toast.LENGTH_LONG).show();
            editor.putString("password","Password");
            editor.commit();

            pass.setHint("Default Password : 'Password'");
        }

        pass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    sendme(view);
                    return true;
                }
                return false;
            }
        });

        TextView category_text = findViewById(R.id.cat_text);
        category_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category_utility();
            }
        });

        if(!sharedPreferences.contains("category_list"))
        {


            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater linf = this.getLayoutInflater();
            View dialogview = linf.inflate(R.layout.category_dialog,null);

            dialogview.setBackgroundColor(getResources().getColor(R.color.bg));

            dialogBuilder.setView(dialogview);
            dialogBuilder.setCancelable(true);


            dialogBuilder.setPositiveButton("Create Categories", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText Category_List = dialogview.findViewById(R.id.category_list);
                    editor.putString("category_list",Category_List.getText().toString());
                    editor.commit();
                    Toast.makeText(Launcher.this,"Categories successfully Created",Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog category_lister = dialogBuilder.create();
            category_lister.show();
        }

    }


    public void category_utility()
    {

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        if(sharedPreferences.contains("category_list"))
        {
            String cat_list = sharedPreferences.getString("category_list","");

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater linf = this.getLayoutInflater();
            View dialogview = linf.inflate(R.layout.category_dialog,null);

            dialogview.setBackgroundColor(getResources().getColor(R.color.bg));

            dialogBuilder.setView(dialogview);
            dialogBuilder.setCancelable(true);

            EditText Category_List = dialogview.findViewById(R.id.category_list);
            Category_List.setText(cat_list);

            dialogBuilder.setPositiveButton("Modify Categories", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText Category_List = dialogview.findViewById(R.id.category_list);

                    String new_catlist = Category_List.getText().toString();
                    editor.putString("category_list",Category_List.getText().toString());
                    editor.commit();
                    Toast.makeText(Launcher.this,"Categories successfully Modified",Toast.LENGTH_LONG).show();

                }
            });

            AlertDialog category_lister = dialogBuilder.create();
            category_lister.show();
        }
    }

    public void bypass(View view)
    {
        Intent myIntent = new Intent(Launcher.this, MainActivity.class);
        Launcher.this.startActivity(myIntent);
    }

    public void sendme(View view)
    {
        Intent myIntent = new Intent(Launcher.this, MainActivity.class);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);

        EditText pass = (EditText)findViewById(R.id.editTextTextPassword2);

        String Password = sharedPreferences.getString("password","");

        if(pass.getText().toString().equals(Password))
        {
            Launcher.this.startActivity(myIntent);
        }
        else
        {
            Toast.makeText(Launcher.this,"Not Authorized ",Toast.LENGTH_LONG).show();
        }



    }
}