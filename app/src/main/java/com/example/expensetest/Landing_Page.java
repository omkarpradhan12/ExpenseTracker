package com.example.expensetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class Landing_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // this code will be executed after 2 seconds
                Intent myIntent = new Intent(Landing_Page.this, Launcher.class);
                Landing_Page.this.startActivity(myIntent);
            }
        }, 2000);
    }
}