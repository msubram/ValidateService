package com.csharp.solutions.validations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import util.GlobalClass;
import util.TypefaceUtil;

/** RegistrationStep1 - This class will get the users mobile number and their country code. */
public class RegistrationStep1 extends Activity {

    /**Widgets*/
    Button registration;
    EditText user_mobile_number;
    Spinner country_list;
    TextView id_reg_step1_label1,id_reg_step1_label2,id_reg_country_label,id_reg_mobile_label;

    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    SharedPreferences sharedPreferences;
    String country_code;

    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    GlobalClass globalClass;
    Context context  = this;

    /** Progress dialog*/
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_step_one);

        sharedPreferences = new SecurePreferences(this);
        globalClass = (GlobalClass) getApplicationContext();


        if(sharedPreferences.getBoolean("login",false))
        {
            Intent intent = new Intent(RegistrationStep1.this, ValidateScreen.class);
            startActivity(intent);
        }
        else
        {
            /** Storing the counrty code values for the country.
             * Key - country name, value - country code*/
            SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
            editor.putString("United Kingdom","44");
            editor.putString("India","91");
            editor.commit();


            /** Initialising the UI Widgets*/
            add_views();



            /** Setting the listener for Register button click event.*/
            registration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    /**Validate mobile number and throw alert when mobile number is empty*/

                    if(user_mobile_number.getText().length()!=0)
                    {
                        /** Asynctask to register the mobile number along with country code*/
                        new RegistrationStep1_Task().execute(globalClass.getBase_url());

                    }
                    else
                    {
                        Toast.makeText(RegistrationStep1.this,"Please fill your mobile number",
                                Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }


    }


    /** Method to refer the views that have been created in xml. Using te id of the view the widgets can be refered*/
    public void add_views(){
        registration=(Button)findViewById(R.id.button_register);
        registration.setTransformationMethod(null);
        user_mobile_number=(EditText)findViewById(R.id.user_mobile_number);
        country_list=(Spinner)findViewById(R.id.country_list);
        country_list.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        id_reg_step1_label1=(TextView)findViewById(R.id.id_reg_step1_label1);
        id_reg_step1_label2=(TextView)findViewById(R.id.id_reg_step1_label2);
        id_reg_country_label=(TextView)findViewById(R.id.id_reg_country_label);
        id_reg_mobile_label=(TextView)findViewById(R.id.id_reg_mobile_label);
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
                 * */
                String body_in_post = new JSONObject().put("CountryCode",country_code).put("MobileNumber",user_mobile_number.getText().toString()).toString();
                System.out.println(globalClass.TAG+body_in_post);

                /** Calling RegistrationRequest(http://www.csharpsolutions.co.uk/ValidateApp/api/v1/RegistrationRequest/) API  and the response will be a statuscode and actual response from server in JSON format.*/
                response_from_server = globalClass.sendPost(urls[0]+"RegistrationRequest/",body_in_post);

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
                SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                editor.putString("reg_id",result);
                editor.putString("mobile_number",user_mobile_number.getText().toString());
                editor.putString("country_code",country_code);
                editor.commit();


                System.out.println(globalClass.TAG+"reg_id"+sharedPreferences.getString("reg_id",""));
                Intent intent = new Intent(RegistrationStep1.this, RegistrationStep2.class);
                startActivity(intent);
            }

            else
            {

                Toast.makeText(RegistrationStep1.this,
                        "Server error : ",
                        Toast.LENGTH_SHORT).show();
            }



        }
    }


    /** Custom Listener for Spinner selection*/
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {


            /*Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + sharedPreferences.getString(parent.getItemAtPosition(pos).toString(),""),
                    Toast.LENGTH_SHORT).show();*/
            /** Here selecting the counrty is the key and we can retrieve the counrty code by using this key and get the value from sharedpreferences*/
            country_code = sharedPreferences.getString(parent.getItemAtPosition(pos).toString(),"");
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

    }




}
