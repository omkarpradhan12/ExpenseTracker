package com.example.expensetest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.slider.Slider;

public class Color_Picker_Handler {
    private Context context;
    private int selectedColor;
    private OnColorSelectedListener colorSelectedListener;

    private int red=0,green=0,blue=0;



    public Color_Picker_Handler(Context context, int selectedColor, OnColorSelectedListener listener) {
        this.context = context;
        this.selectedColor = selectedColor;
        this.colorSelectedListener = listener;

    }



    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DarkAlertDialogTheme));

        // Inflate the color picker layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View colorPickerView = inflater.inflate(R.layout.color_picker, null);

        builder.setView(colorPickerView);


        // Get references to the color picker components
         SeekBar redSeekBar = colorPickerView.findViewById(R.id.seekBarRed);
         SeekBar greenSeekBar = colorPickerView.findViewById(R.id.seekBarGreen);
         SeekBar blueSeekBar = colorPickerView.findViewById(R.id.seekBarBlue);
         View colorPreview = colorPickerView.findViewById(R.id.color_preview);


         redSeekBar.setProgress(Color.red(selectedColor));
         greenSeekBar.setProgress(Color.green(selectedColor));
         blueSeekBar.setProgress(Color.blue(selectedColor));

         red = Color.red(selectedColor);
         green = Color.green(selectedColor);
         blue =  Color.blue(selectedColor);

         colorPreview.setBackgroundColor(selectedColor);



         redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                 change_red(i);
                 mod_color(colorPreview,red,green,blue);
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {

             }
         });

         greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                 change_green(i);
                 mod_color(colorPreview,red,green,blue);
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {

             }
         });

         blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                 change_blue(i);
                 mod_color(colorPreview,red,green,blue);
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {

             }
         });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (colorSelectedListener != null) {
                    int selectedColor = Color.rgb(redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress());
                    colorSelectedListener.onColorSelected(selectedColor);
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }


    private void change_red(int r)
    {
        this.red = r;
    }

    private void change_green(int g)
    {
        this.green = g;
    }

    private void change_blue(int b)
    {
        this.blue = b;
    }

    private void mod_color(View colorPreview,int r,int g,int b)
    {
        colorPreview.setBackgroundColor(Color.rgb(r,g,b));
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }


}
