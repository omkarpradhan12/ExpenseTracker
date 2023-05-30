package com.example.expensetest;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Executor;

import es.dmoral.toasty.Toasty;


public class Launcher extends AppCompatActivity {

    private static final int REQUEST_WRITE_PERMISSION = 786;

    private ProgressBar progressscam;
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


    public void setpass()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!sharedPreferences.contains("password"))
        {
            Toasty.info(Launcher.this,"No Password found\nDefault: Password",Toast.LENGTH_LONG).show();
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
                                Toasty.error(Launcher.this,"Password cannot be empty",Toast.LENGTH_LONG,true).show();
                            }
                            else
                            {
                                editor.putString("password",newpword.getText().toString()).apply();
                                Toasty.success(Launcher.this,"Password changed",Toast.LENGTH_LONG,true).show();
                            }
                        }

                        else
                        {
                            Toasty.error(Launcher.this,"Incorrect Old Password",Toast.LENGTH_LONG,true).show();
                        }



                }
            });

            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toasty.info(getApplicationContext(),"Later Then",Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog password_changer = dialogBuilder.create();
            password_changer.show();



        }


    }



    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);


        progressscam = findViewById(R.id.progress_scam);
        progressscam.setVisibility(View.GONE);

        fp_driver();
        requestPermission();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }



        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        TextInputLayout pass = (TextInputLayout) findViewById(R.id.editTextTextPassword2);

        if(!sharedPreferences.contains("password"))
        {
            Toasty.info(Launcher.this,"No Password found\nDefault: Password",Toast.LENGTH_LONG).show();
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

//        TextView category_text = findViewById(R.id.cat_text);
//        category_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                category_utility();
//            }
//        });

        if(!sharedPreferences.contains("category_list"))
        {
            editor.putString("category_list","Food,Drink,Other,Misc");
            editor.commit();
            Toasty.success(Launcher.this,"Default Categories successfully Created",Toast.LENGTH_LONG,true).show();
            color_changer();
        }

        color_check();

    }

    @Override
    protected void onResume() {
        super.onResume();
        color_check();
        progressscam.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        color_check();
        progressscam.setVisibility(View.INVISIBLE);
    }

    private void color_check()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String cat_list = sharedPreferences.getString("category_list","").strip();
        String cat_colors = sharedPreferences.getString("Cat_Colors","").strip();

        ArrayList<String> cat_list_arr = new ArrayList<>(Arrays.asList(cat_list.replace(" ","").split(",")));

        expenseDB_Helper edbh = new expenseDB_Helper(Launcher.this);

        List<String> cat_listdb = edbh.getCategories();

        String cols="";
        for(String x:cat_colors.split("\n"))
        {
            if(cat_list_arr.contains(x.split(":")[0]))
            {
                cols+=x+"\n";
            }
        }

        for(String x:cat_listdb)
        {
            if(!cat_list_arr.contains(x))
            {
                cols+=x+":"+"-5462104"+"\n";
            }
        }

        editor.putString("Cat_Colors",cols);
        editor.commit();
    }

    /*Use Pass*/

    private Boolean use_pass_flag = false;
    public void use_pass(View view)
    {
        use_pass_flag = !use_pass_flag;
        TextInputLayout edpass = findViewById(R.id.editTextTextPassword2);
        Button go = findViewById(R.id.button5);


        if(use_pass_flag)
        {
            edpass.setVisibility(View.VISIBLE);
            go.setVisibility(View.VISIBLE);
        }
        else
        {
            edpass.setVisibility(View.INVISIBLE);
            go.setVisibility(View.INVISIBLE);
        }
    }

    /*Menu*/
    public void optionsme(View view)
    {
        PopupMenu settingspopup = new PopupMenu(Launcher.this,view);


        settingspopup.getMenuInflater().inflate(R.menu.settings_options_menu,settingspopup.getMenu());



        settingspopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.menu_change_password)
                {
                    setpass();

                }else if(menuItem.getItemId() == R.id.menu_cat_util)
                {
                    category_utility();
                }else if(menuItem.getItemId() == R.id.menu_col_util)
                {
                    color_changer();
                }



                return false;
            }
        });

        settingspopup.show();
    }


    public void fp_driver()
    {
        /*
        Test 1
         */
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(Launcher.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                progressscam.setVisibility(View.VISIBLE);
                Toasty.error(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT,true)
                        .show();
                progressscam.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                progressscam.setVisibility(View.VISIBLE);
                Toasty.success(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT,true).show();
                Intent myIntent = new Intent(Launcher.this, MainActivity.class);

                Launcher.this.startActivity(myIntent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toasty.error(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT,true)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Login")
                .setSubtitle("Log in using your fingerprint")
                .setNegativeButtonText("Use password")
                .build();


        Button use_fp = findViewById(R.id.use_fp);
        use_fp.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });
        /*
        Test 1
         */
    }

    public void color_changer()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String cat_list = sharedPreferences.getString("category_list","");


        ArrayList<String> cat_list_arr = new ArrayList<>(Arrays.asList(cat_list.replace(" ","").split(",")));


        if(!sharedPreferences.contains("Cat_Colors"))
        {
            String temp_s="";
            for(String x:cat_list.split(","))
            {
                temp_s+=x+":"+"-5462104"+"\n";
            }
            editor.putString("Cat_Colors",temp_s);
            editor.commit();
        }

        String cat_colors = "";

        if(sharedPreferences.contains("Cat_Colors"))
        {



            cat_colors = sharedPreferences.getString("Cat_Colors","").strip();
            ArrayList<String> existing_colors = new ArrayList<String>();

            for(String X:cat_colors.split("\n"))
            {
                existing_colors.add(X.split(":")[0]);
            }

            for (String ex:cat_list_arr)
            {
                if(!existing_colors.contains(ex))
                {
                    cat_colors+="\n";
                    cat_colors+=ex+":"+"-5462104"+"\n";
                }
            }
            editor.putString("Cat_Colors",cat_colors);
            editor.commit();

        }


        cat_colors = sharedPreferences.getString("Cat_Colors","").strip();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater linf = this.getLayoutInflater();
        View dialogview = linf.inflate(R.layout.category_colors,null);

        dialogBuilder.setTitle("Select Color For Categories");

        TableLayout tab_lay = dialogview.findViewById(R.id.cat_color_table);
        tab_lay.removeAllViews();

        for(String x:cat_colors.split("\n"))
        {

            String cname=x.split(":")[0];
            String ccol=x.split(":")[1];

            View temp_view = linf.inflate(R.layout.category_color_item,null);
            TextView cat_name = temp_view.findViewById(R.id.cat_name);
            cat_name.setText(cname);


            TextView cat_color = temp_view.findViewById(R.id.cat_color);
            cat_color.setText(ccol);

            if(!ccol.equals("TextView"))
            {
                temp_view.setBackgroundColor(Integer.parseInt(ccol));
            }

            temp_view.setClickable(true);
            temp_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Color_Picker_Handler cph = new Color_Picker_Handler(Launcher.this, Integer.parseInt(ccol), new Color_Picker_Handler.OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int color) {
                            view.setBackgroundColor(color);
                            cat_color.setText(color+"");
                        }
                    });
                    cph.show();
                }
            });


            tab_lay.addView(temp_view);

        }

        dialogview.setBackgroundColor(getResources().getColor(R.color.bg));



        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(true);

        dialogBuilder.setPositiveButton("Modify Colors", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //ClearAll();
                        String s="";
                        for(int i=0;i<tab_lay.getChildCount();i++)
                        {
                            TableRow temp_row =(TableRow) tab_lay.getChildAt(i);
                            TextView cat_te = (TextView) temp_row.findViewById(R.id.cat_name);
                            TextView cat_cl = (TextView) temp_row.findViewById(R.id.cat_color);

                            if(cat_list_arr.contains(cat_te.getText()))
                                s+= cat_te.getText()+":"+cat_cl.getText()+"\n";

                        }



                        editor.putString("Cat_Colors",s);
                        editor.commit();
                        Toasty.info(Launcher.this,"Please Restart Application for effects to apply",Toast.LENGTH_SHORT).show();

                    }})
                .setNegativeButton("Cancel", null);


        AlertDialog category_lister = dialogBuilder.create();
        category_lister.show();

        color_check();
    }

    public void category_utility()
    {

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();






        if(sharedPreferences.contains("category_list"))
        {
            String cat_list = sharedPreferences.getString("category_list","").replace(" ","");

            Toasty.normal(Launcher.this,sharedPreferences.getString("Cat_Colors",""),Toasty.LENGTH_SHORT).show();

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater linf = this.getLayoutInflater();
            View dialogview = linf.inflate(R.layout.category_dialog,null);

            EditText new_cat = dialogview.findViewById(R.id.new_cat);
            ImageButton cat_add = (ImageButton) dialogview.findViewById(R.id.cat_add);




            dialogview.setBackgroundColor(getResources().getColor(R.color.bg));


            
            dialogBuilder.setView(dialogview);
            dialogBuilder.setCancelable(true);


            LinearLayout category_list = dialogview.findViewById(R.id.categories_ll);
            category_list.removeAllViews();

            cat_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!new_cat.getText().toString().isEmpty())
                    {
                        View cat_new = getLayoutInflater().inflate(R.layout.category_item,null);
                        EditText category_name = (EditText) cat_new.findViewById(R.id.dial_cat_name);
                        category_name.setText(new_cat.getText().toString());
                        category_name.setHint("Category");



                        ImageButton deleter = (ImageButton) cat_new.findViewById(R.id.cat_delete);
                        deleter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                category_list.removeView(cat_new);
                            }
                        });

                        Toasty.success(getApplicationContext(),"Added New Category",Toasty.LENGTH_SHORT).show();
                        category_list.addView(cat_new);
                        new_cat.setText("");
                    }
                }
            });


            for(String category:cat_list.split(","))
            {
                View cat_view = getLayoutInflater().inflate(R.layout.category_item,null);

                EditText category_name = (EditText) cat_view.findViewById(R.id.dial_cat_name);

                ImageButton deleter = (ImageButton) cat_view.findViewById(R.id.cat_delete);
                deleter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        category_list.removeView(cat_view);
                    }
                });

                category_name.setText(category);
                category_list.addView(cat_view);
            }

            dialogBuilder.setPositiveButton("Modify Categories", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    expenseDB_Helper edbh = new expenseDB_Helper(Launcher.this);
                    List<String> cat_listdb = edbh.getCategories();

                    final int childcount = category_list.getChildCount();

                    String n_cat_list ="";

                    for(int x=0;x<childcount;x++)
                    {
                        EditText et = category_list.getChildAt(x).findViewById(R.id.dial_cat_name);
                        n_cat_list+=et.getText().toString()+",";
                    }



                    n_cat_list = n_cat_list.substring(0,n_cat_list.length()-1);
                    editor.putString("category_list",n_cat_list);
                    editor.commit();

                    color_check();

                    Toasty.success(Launcher.this,"Categories successfully Modified",Toast.LENGTH_SHORT,true).show();
                    Toasty.info(Launcher.this,"Please Restart Application for effects to apply",Toast.LENGTH_SHORT).show();
                    color_changer();
                }
            });

            AlertDialog category_lister = dialogBuilder.create();
            category_lister.show();



        }
    }

    public void bypass(View view)
    {
        Intent myIntent = new Intent(Launcher.this, MainActivity.class);
        progressscam.setVisibility(View.VISIBLE);
        Launcher.this.startActivity(myIntent);
    }

    public void sendme(View view)
    {
        Intent myIntent = new Intent(Launcher.this, MainActivity.class);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);

        TextInputLayout pass = (TextInputLayout) findViewById(R.id.editTextTextPassword2);

        String Password = sharedPreferences.getString("password","");

        if(pass.getEditText().getText().toString().equals(Password))
        {
            progressscam.setVisibility(View.VISIBLE);
            Launcher.this.startActivity(myIntent);
        }
        else
        {
            Toasty.error(Launcher.this,"Not Authorized ",Toast.LENGTH_LONG,true).show();
            pass.setError("Wrong Password");
        }



    }



    public void Help_Me(View view)
    {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.help_dialog, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}