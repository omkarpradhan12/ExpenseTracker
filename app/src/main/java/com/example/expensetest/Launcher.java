package com.example.expensetest;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
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

            AlertDialog add_new_expense = dialogBuilder.create();



            add_new_expense.show();



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

        if(!sharedPreferences.contains("password"))
        {
            Toast.makeText(Launcher.this,"No Password found\nDefault: Password",Toast.LENGTH_LONG).show();
            editor.putString("password","Password");
            editor.commit();

            EditText pass = (EditText) findViewById(R.id.editTextTextPassword2);
            pass.setHint("Default Password : 'Password'");
        }

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