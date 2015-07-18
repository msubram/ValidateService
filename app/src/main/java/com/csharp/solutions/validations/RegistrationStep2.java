package com.csharp.solutions.validations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.json.JSONObject;

import util.GlobalClass;
import util.TypefaceUtil;

/**
 * Created by Arputha on 04/07/2015.
 */
public class RegistrationStep2 extends Activity {


    /**Widgets*/
    Button complete,retry;
    ImageView logo;
    EditText firstdigit,seconddigit,thirddigit,fourthdigit,fifthdigit;
    TextView id_reg_step2_label1,id_reg_step2_label2,id_reg_step2_label3,id_reg_note;

    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    SharedPreferences sharedPreferences;

    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    GlobalClass globalClass;

    /** Progress dialog*/
    ProgressDialog progressDialog;

    Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_step_two);
        context=this;

        add_views();


        sharedPreferences = new SecurePreferences(this);
        globalClass = (GlobalClass) getApplicationContext();


        /** Complete button action listener. */
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                if(firstdigit.getText().length()==0 || seconddigit.getText().length()==0 || thirddigit.getText().length()==0 || fourthdigit.getText().length()==0 || fifthdigit.getText().length()==0)
                {
                    Toast.makeText(RegistrationStep2.this, getResources().getString(R.string.empty_fields),
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    /** Asynctask to call when complete button is pressed*/
                    new Complete_registration_Task().execute(globalClass.getBase_url());
                }



            }
        });

        /**  5 digit code placeholders*/

        /** First digit placeholder - 1st digit falls here and the onTextChanged will get called and move to the next digit placeholder*/
        firstdigit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(firstdigit.getText().length()==1) {
                    firstdigit.clearFocus();
                    seconddigit.requestFocus();
                }
            }
        });

        /** Seecond digit placeholder - 2nd digit falls here and the onTextChanged will get called and move to the next digit placeholder*/
        seconddigit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(seconddigit.getText().length()==1) {
                    seconddigit.clearFocus();
                    thirddigit.requestFocus();
                }
            }
        });

        /** Third digit placeholder - 3rd digit falls here and the onTextChanged will get called and move to the next digit placeholder*/
        thirddigit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(thirddigit.getText().length()==1) {
                    thirddigit.clearFocus();
                    fourthdigit.requestFocus();
                }
            }
        });

        /** Fourth digit placeholder - 4th digit falls here and the onTextChanged will get called and move to the next digit placeholder*/
        fourthdigit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(fourthdigit.getText().length()==1) {
                    fourthdigit.clearFocus();
                    fifthdigit.requestFocus();
                }
            }

        });

        /** Asynctask to get the RegCode - Testing purpose. In production the app will automatically read the 5-digit code from the Inbox. In some mobiles automatic sms reading cant be done. In that case user has to manually enter the 5-digit code*/
        new RegCode_Task().execute(globalClass.getBase_url());




    }

    /** Method to refer the views that have been created in xml. Using te id of the view the widgets can be refered*/
    public void add_views(){
        complete=(Button)findViewById(R.id.button_complete);
        retry=(Button)findViewById(R.id.button_retry);
        logo=(ImageView)findViewById(R.id.imageview_logo);
        firstdigit=(EditText)findViewById(R.id.edittext_ota_first_digit);
        seconddigit=(EditText)findViewById(R.id.edittext_ota_second_digit);
        thirddigit=(EditText)findViewById(R.id.edittext_ota_third_digit);
        fourthdigit=(EditText)findViewById(R.id.edittext_ota_fourth_digit);
        fifthdigit=(EditText)findViewById(R.id.edittext_ota_fifth_digit);


        id_reg_step2_label1=(TextView)findViewById(R.id.id_reg_step2_label1);
        id_reg_step2_label2=(TextView)findViewById(R.id.id_reg_step2_label1);
        id_reg_step2_label3=(TextView)findViewById(R.id.id_reg_step2_label1);
        id_reg_note=(TextView)findViewById(R.id.id_reg_step2_label1);

        /** Setting typeface for views*/
        id_reg_step2_label1.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        id_reg_step2_label2.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        id_reg_step2_label3.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        id_reg_note.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));

        firstdigit.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        seconddigit.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        thirddigit.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        fourthdigit.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        fifthdigit.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        complete.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        retry.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));

        firstdigit.setInputType(InputType.TYPE_CLASS_NUMBER);
        complete.setTransformationMethod(null);
        retry.setTransformationMethod(null);
    }


    /** Background task to get the RegCode but it is for testing purpose actual pulling of RegCode will done through Sms read operation automatically*/
    private class RegCode_Task extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // publishProgress("");

            /** Custom progressdialog to show loading symbol*/
            progressDialog = CustomProgressDialog.ctor(context);
            progressDialog.show();
        }

        protected String doInBackground(String... urls) {


            /** Sending the get data in JSON format in the body*/
            String response_from_server = null;

            try {

                /** Calling Registrations(http://www.csharpsolutions.co.uk/ValidateApp/api/v1/Registrations/{id}) API  and the response will be a statuscode and actual response from server in JSON format.*/
                System.out.println(globalClass.TAG+"reg_id"+sharedPreferences.getString("reg_id",""));
                response_from_server = globalClass.sendGet(urls[0]+"Registrations/"+sharedPreferences.getString("reg_id",""),5000);

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
                System.out.println(globalClass.TAG+e.toString());
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
                String reg_code = null;
                try{
                    JSONObject response_from_server = new JSONObject(result);

                    reg_code = Integer.toString(response_from_server.getInt("RegCode"));

                    SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                    editor.putString("RegCode",reg_code);
                    editor.commit();

                    System.out.println(globalClass.TAG+"reg_code"+reg_code);

                    /** Code to get the individual digit in the 5-digit code*/
                    if(reg_code.length()==5)
                    {
                        firstdigit.setText(Character.toString(reg_code.charAt(0)));
                        seconddigit.setText(Character.toString(reg_code.charAt(1)));
                        thirddigit.setText(Character.toString(reg_code.charAt(2)));
                        fourthdigit.setText(Character.toString(reg_code.charAt(3)));
                        fifthdigit.setText(Character.toString(reg_code.charAt(4)));
                    }


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            else
            {

                Toast.makeText(RegistrationStep2.this,
                        "Server error : ",
                        Toast.LENGTH_SHORT).show();
            }



        }
    }


    /** Background network operation can be performed in a separate thread. We must use AsyncTask for that.*/
    private class Complete_registration_Task extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // publishProgress("");
            /** Custom progressdialog to show loading symbol*/
            progressDialog = CustomProgressDialog.ctor(context);
            progressDialog.show();
        }

        protected String doInBackground(String... urls) {


            /** Sending the post data in JSON format in the body of the post*/
            String response_from_server = null;

            try {
                /** body_in_post  POST data in JSON format.
                 * RegCode, Mobile number and CountryCode - Obtained in the first step
                 * */
                String body_in_post = new JSONObject().put("RegCode",Integer.parseInt(sharedPreferences.getString("RegCode",""))).put("MobileInfo",new JSONObject().put("CountryCode",sharedPreferences.getString("country_code","")).put("MobileNumber",sharedPreferences.getString("mobile_number",""))).toString();

                System.out.println(globalClass.TAG+"Complete_registration_Task"+body_in_post);

                /** Calling Registrations(http://www.csharpsolutions.co.uk/ValidateApp/api/v1/Registrations/) API  and the response will be a statuscode and actual response from server in JSON format.*/
                response_from_server = globalClass.sendPost(urls[0]+"Registrations/",body_in_post);

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

                System.out.println(globalClass.TAG+"complete registration result"+result);

                SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                editor.putBoolean("login",true);
                editor.commit();

                Intent intent = new Intent(RegistrationStep2.this, UpdateScreen.class);
                startActivity(intent);
            }

            else
            {

                Toast.makeText(RegistrationStep2.this,
                        "Server error : ",
                        Toast.LENGTH_SHORT).show();
            }



        }
    }


    /** Method to be called when back button is pressed in this activity*/
    public void onBackPressed() {
        Intent intent = new Intent(RegistrationStep2.this,RegistrationStep1.class);
        startActivity(intent);
    }

}
