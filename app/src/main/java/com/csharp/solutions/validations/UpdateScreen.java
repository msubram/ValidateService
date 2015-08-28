package com.csharp.solutions.validations;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.securepreferences.SecurePreferences;

import org.json.JSONObject;

import util.GlobalClass;
import util.TypefaceUtil;

/**
 * Created by Arputha on 04/07/2015.
 */
/** UpdateScreen - This class will get the user Work and Home telephone number and saves it.*/

public class UpdateScreen extends ActionBarActivity {
    /** Widgets declaration*/
    Button update;
    ImageView logo;
    EditText work_number,home_number;
    TextView update_screen_label1,update_work_telephone_label,update_home_telephone_label;


    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    SharedPreferences sharedPreferences;


    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    GlobalClass globalClass;


    /** Tags declaration*/
    String mTagWorkNumber = GlobalClass.WORK_NUMBER;
    String mTagHomeNumber = GlobalClass.HOME_NUMBER;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatescreen);

        /** Method to refer the views from xml*/
        addViews();



        sharedPreferences = new SecurePreferences(this);
        globalClass = (GlobalClass) getApplicationContext();

        /** Show the updated values in the respective fields by getting the data from sharedpreference*/

        work_number.setText(sharedPreferences.getString(mTagWorkNumber,""));
        home_number.setText(sharedPreferences.getString(mTagHomeNumber,""));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(work_number.getText().toString().length()!=0&&work_number.getText().toString().length()!=0)
                {
                    /** Save the Work Telephone and Home Telephone number in sharepreference*/
                    SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                    editor.putString(mTagWorkNumber,work_number.getText().toString());
                    editor.putString(mTagHomeNumber,home_number.getText().toString());
                    editor.commit();

                    /** After successfully updated move from Update Screen to Validate Screen*/
                    Intent intent = new Intent(UpdateScreen.this, ValidateScreen.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(UpdateScreen.this, getResources().getString(R.string.empty_fields),
                            Toast.LENGTH_SHORT).show();
                }




            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.menu_validate:
                Intent intent = new Intent(UpdateScreen.this, ValidateScreen.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Method to refer the views that have been created in xml. Using te id of the view the widgets can be refered*/
    public void addViews(){
        update=(Button)findViewById(R.id.button_update);
        logo=(ImageView)findViewById(R.id.imageview_logo);
        work_number = (EditText)findViewById(R.id.work_number);
        home_number = (EditText)findViewById(R.id.home_number);
        update.setTransformationMethod(null);
        update_screen_label1 = (TextView)findViewById(R.id.update_screen_label1);
        update_work_telephone_label = (TextView)findViewById(R.id.update_work_telephone_label);
        update_home_telephone_label = (TextView)findViewById(R.id.update_home_telephone_label);

        /** Setting typeface for views*/
        update_screen_label1.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        update_work_telephone_label.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        update_home_telephone_label.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        update.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        work_number.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        home_number.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
    }
}
