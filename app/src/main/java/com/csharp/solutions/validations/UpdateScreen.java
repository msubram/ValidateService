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
public class UpdateScreen extends ActionBarActivity {
    /** Widgets declaration*/
    Button update;
    ImageView logo;
    EditText work_number,home_number;
    TextView update_screen_label1,update_work_telephone_label,update_home_telephone_label;


    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    SharedPreferences sharedPreferences;


    /**GCM*/
    String mGcmregId ="";
    String mGcmID;

    Context context = this;
    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    GlobalClass globalClass;

    /** Progress dialog*/
    ProgressDialog progressDialog;

    /** Tags declaration*/
    String mTagCountryCode = GlobalClass.COUNTRY_CODE;
    String mTagMobileNumber = GlobalClass.MOBILE_NUMBER;
    String mTagWorkNumber = GlobalClass.WORK_NUMBER;
    String mTagHomeNumber = GlobalClass.HOME_NUMBER;
    String mTagInstanceId = GlobalClass.INSTANCE_ID;
    String mTagGcmToken = GlobalClass.GCM_TOKEN;
    String mTagIsGcmRegistered = GlobalClass.CHECK_GCMISREGISTERED;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatescreen);

        addViews();

        sharedPreferences = new SecurePreferences(this);
        globalClass = (GlobalClass) getApplicationContext();

        /** Show the updated values in the respective fields by getting the data from sharedpreference*/

        work_number.setText(sharedPreferences.getString(mTagWorkNumber,""));
        home_number.setText(sharedPreferences.getString(mTagHomeNumber,""));



        mGcmID =  GCMRegistrar.getRegistrationId(UpdateScreen.this);

        if(mGcmID.length()==0)
        {
            mGcmID = sharedPreferences.getString(globalClass.GCM_TOKEN,"");
        }
        System.out.println("GCMID"+ mGcmID);

        /** Code to Send the GCM RegId to the server.
         * Check whether we sent the RegId already or not*/
        if(!sharedPreferences.getBoolean(mTagIsGcmRegistered,false))
        {
            if(mGcmID.length()!=0)
            {
                /** Commit the changes in the sharedpreference*/
                SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                editor.putString(mTagGcmToken, mGcmID);
                editor.putBoolean(mTagIsGcmRegistered,true);
                editor.commit();

                new GCMRegistrationRequest().execute(globalClass.getBase_url());
            }
        }


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



    private class GCMRegistrationRequest extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /** Custom progressdialog to show loading symbol*/
            progressDialog = CustomProgressDialog.ctor(context);
            progressDialog.show();
        }

        protected String doInBackground(String... urls) {


            /** Sending the post data in JSON format in the body*/
            String response_from_server = null;

            try {
                /** body_in_post  POST data in JSON format.
                 * Country code - Selected by user
                 * MobileNumber - Users mobile number
                 * InstanceId - not mandatory
                 * Token - GCM Registration ID
                 * */
                String body_in_post = new JSONObject().put(mTagCountryCode,sharedPreferences.getString(mTagCountryCode,"")).put(mTagMobileNumber, sharedPreferences.getString(mTagMobileNumber, "")).put(mTagInstanceId, "").put(mTagGcmToken, sharedPreferences.getString(mTagGcmToken, "")).toString();
                System.out.println(globalClass.TAG+body_in_post);

                /** Calling GCMRegistrationRequest(http://www.csharpsolutions.co.uk/ValidateApp/api/v1/GCMRegistrationRequest/) API  and the response will be a statuscode and actual response from server in JSON format.*/
                response_from_server = globalClass.sendPost(urls[0]+globalClass.getGcm_Registration_Request(),body_in_post);

                /** Parsing response to get Status code and response from server*/
                globalClass.parseServerResponseJSON(response_from_server);

                if(globalClass.getStatusCode() == 200)
                {
                    response_from_server = globalClass.getServerResponse();
                }
                else
                {
                    response_from_server = "error";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response_from_server;
        }



        protected void onPostExecute(String result) {

            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }

            /** Sometimes response can be in negatve value so it indicates error.*/
            if(!result.equals("error")&&!result.equals("-1"))
            {
                Toast.makeText(UpdateScreen.this,
                        getResources().getString(R.string.success),
                        Toast.LENGTH_SHORT).show();
            }

            else
            {

                Toast.makeText(UpdateScreen.this,
                        getResources().getString(R.string.try_again),
                        Toast.LENGTH_SHORT).show();
            }



        }
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
