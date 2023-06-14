package com.example.expensetest;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

public class RGBColorPicker {
    private Context context;
    private int color;
    private OnColorSelectedListener listener;

    public RGBColorPicker(Context context, int color) {
        this.context = context;
        this.color = color;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    public void showColorPickerDialog() {
        // Extract the RGB color components from the parsed color
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        // Inflate the layout for the color picker dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View colorPickerView = inflater.inflate(R.layout.color_picker_slider, null);


        // Get references to the sliders and the color preview TextView
        Slider redSlider = colorPickerView.findViewById(R.id.red_slider);
        Slider greenSlider = colorPickerView.findViewById(R.id.green_slider);
        Slider blueSlider = colorPickerView.findViewById(R.id.blue_slider);
        final TextView colorPreviewTextView = colorPickerView.findViewById(R.id.color_preview_text);

        // Set the initial color values
        redSlider.setValue(red);
        greenSlider.setValue(green);
        blueSlider.setValue(blue);

        // Set the initial color preview
        updateColorPreview(colorPreviewTextView);

        // Set up the Slider listeners to update the color preview
        redSlider.addOnChangeListener(createSliderChangeListener(colorPreviewTextView, 0));
        greenSlider.addOnChangeListener(createSliderChangeListener(colorPreviewTextView, 1));
        blueSlider.addOnChangeListener(createSliderChangeListener(colorPreviewTextView, 2));

        // Create and show the color picker dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DarkAlertDialogTheme));

        builder.setView(colorPickerView);
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (listener != null) {
                // Return the selected color to the listener
                listener.onColorSelected(color);
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private Slider.OnChangeListener createSliderChangeListener(final TextView colorPreviewTextView, final int colorComponent) {
        return (slider, value, fromUser) -> {
            // Update the respective color component based on the Slider value
            int intValue = (int) value;
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            switch (colorComponent) {
                case 0: // Red
                    red = intValue;
                    break;
                case 1: // Green
                    green = intValue;
                    break;
                case 2: // Blue
                    blue = intValue;
                    break;
            }

            // Update the color preview
            color = Color.rgb(red, green, blue);
            updateColorPreview(colorPreviewTextView);
        };
    }

    private void updateColorPreview(TextView colorPreviewTextView) {
        colorPreviewTextView.setBackgroundColor(color);
        colorPreviewTextView.setText(String.format("#%06X", (0xFFFFFF & color)));
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }
}
