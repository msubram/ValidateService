package com.csharp.solutions.validations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.json.JSONObject;

import util.GlobalClass;
import util.TypefaceUtil;

/** RegistrationStep1 - This class will get the users mobile number and their country code and send to the server get the RegCode. */
public class RegistrationStep1 extends Activity {

    /**UI Widgets*/
    Button registration;
    EditText user_mobile_number;
    Spinner country_list;
    TextView id_reg_step1_label1,id_reg_step1_label2,id_reg_country_label,id_reg_mobile_label;

    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    SharedPreferences sharedPreferences;

    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    GlobalClass globalClass;
    Context context  = this;

    /** Progress dialog*/
    ProgressDialog progressDialog;

    /** Tags used to access the Global Variables*/
    String mTagCountryCode = GlobalClass.COUNTRY_CODE;
    String mTagMobileNumber = GlobalClass.MOBILE_NUMBER;
    String mTagRegId = GlobalClass.REG_ID;

    String mcountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = new SecurePreferences(this);

        /** Code to check the login status of the user*/
        if(sharedPreferences.getBoolean(GlobalClass.CHECK_LOGIN,false))
        {
            Intent intent = new Intent(RegistrationStep1.this, ValidateScreen.class);
            startActivity(intent);
        }
        else
        {
            setContentView(R.layout.registration_step_one);
            globalClass = (GlobalClass) getApplicationContext();

            /** Storing the counrty code values for the country.
             * Key - country name, value - country code*/
            SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
            editor.putString("United Kingdom","44");
            editor.putString("India","91");
            editor.commit();

            /** Initialising the UI Widgets*/
            addViews();

            /** Code to listen when Register button is clicked*/
            registration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    /**Validate mobile number and throw alert when mobile number is empty*/

                    if (user_mobile_number.getText().length() != 0) {

                        if(globalClass.checkWifiConnectivity())
                        {
                            /** Asynctask to register the mobile number along with country code*/
                            new RegistrationStep1_Task().execute(globalClass.getBase_url());
                        }
                        else
                        {
                            Toast.makeText(context, getResources().getString(R.string.check_wifi_string), Toast.LENGTH_LONG).show();

                        }

                    } else {
                        Toast.makeText(RegistrationStep1.this, getResources().getString(R.string.empty_phonenumber),
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    /** Method to refer the views that have been created in xml. Using te id of the view the widgets can be refered*/
    public void addViews(){
        registration = (Button)findViewById(R.id.button_register);
        registration.setTransformationMethod(null);
        user_mobile_number = (EditText)findViewById(R.id.user_mobile_number);
        country_list = (Spinner)findViewById(R.id.country_list);
        country_list.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        id_reg_step1_label1 = (TextView)findViewById(R.id.id_reg_step1_label1);
        id_reg_step1_label2 = (TextView)findViewById(R.id.id_reg_step1_label2);
        id_reg_country_label = (TextView)findViewById(R.id.id_reg_country_label);
        id_reg_mobile_label = (TextView)findViewById(R.id.id_reg_mobile_label);

        /** Setting typeface for views*/
        registration.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        user_mobile_number.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        id_reg_step1_label1.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        id_reg_step1_label2.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        id_reg_country_label.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        id_reg_mobile_label.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
    }

    /** Background network operation can be performed in a separate thread. We must use AsyncTask for that.*/
    private class RegistrationStep1_Task extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /** Custom progressdialog to show loading symbol*/
            progressDialog = CustomProgressDialog.ctor(context,getResources().getString(R.string.registering));
            progressDialog.show();
        }

        protected String doInBackground(String... urls) {
            /** Sending the post data in JSON format in the body*/
                String mResponseFromServer = null;

                try {
                /** body_in_post  POST data in JSON format.
                 * Country code - Selected by user
                 * MobileNumber - Users mobile number
                 * */
                String mPostBodyData = new JSONObject().put(mTagCountryCode, mcountryCode).put(mTagMobileNumber,user_mobile_number.getText().toString()).toString();

                /** Calling RegistrationRequest(http://www.csharpsolutions.co.uk/ValidateApp/api/v1/RegistrationRequest/) API  and the response will be a statuscode and actual response from server in JSON format.*/
                mResponseFromServer = globalClass.sendPost(urls[0]+globalClass.getRegistration_Request_Routes(),mPostBodyData);

               /** Parsing response to get Status code and response from server*/
                globalClass.parseServerResponseJSON(mResponseFromServer);

                if(globalClass.getStatusCode() == 200)
                {
                    mResponseFromServer = globalClass.getServerResponse();
                }
                else
                {
                    mResponseFromServer = "error";
                }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return mResponseFromServer;
        }


        protected void onPostExecute(String mResult) {
            /** Sometimes response can be in negatve value so it indicates error.*/
           if(!mResult.equals("error"))
            {

                /** Save RegId, MobileNumber and CountryCode in Sharedpreference*/
                SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                editor.putString(mTagRegId,mResult);
                editor.putString(mTagMobileNumber,user_mobile_number.getText().toString());
                editor.putString(mTagCountryCode, mcountryCode);
                editor.commit();

                /** When we successfully get RegId move from RegistrationStep1 to RegistrationStep1 Activity*/
                Intent intent = new Intent(RegistrationStep1.this, RegistrationStep2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
            else
            {

                Toast.makeText(RegistrationStep1.this,
                        getResources().getString(R.string.try_again),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    /** Custom Listener for Country Spinner selection*/
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

            /** Here selecting the counrty is the key and we can retrieve the counrty code by using this key and get the value from sharedpreferences*/
            mcountryCode = sharedPreferences.getString(parent.getItemAtPosition(pos).toString(),"");
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    /** Method to be called when back button is pressed in this activity*/
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO Auto-generated method stub
        if(progressDialog!=null)
        {
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }
    }


}
