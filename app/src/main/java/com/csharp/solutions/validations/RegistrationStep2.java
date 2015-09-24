package com.csharp.solutions.validations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.securepreferences.SecurePreferences;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import gcm.WakeLocker;
import util.GlobalClass;
import util.TypefaceUtil;


import static gcm.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static gcm.CommonUtilities.SENDER_ID;

/**
 * Created by Arputha on 04/07/2015.
 */

/** RegistrationStep2 - This class reads the RegCode. and completes the Registration process*/

public class RegistrationStep2 extends Activity {


    /**Widgets*/
    private Button complete;
    private Button retry;
    private EditText firstdigit_regcode;
    private EditText seconddigit_regcode;
    private EditText thirddigit_regcode;
    private EditText fourthdigit_regcode;
    private EditText fifthdigit_regcode;

    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    private SharedPreferences sharedPreferences;

    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    private GlobalClass globalClass;

    /** Progress dialog*/
    private ProgressDialog progressDialog;

    private Context context;

    /** Intent filter to filter the incoming messages*/
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private final String mRegCode = GlobalClass.REG_CODE;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_step_two);
        context=this;

        /** Method to refer the views from xml*/
        addViews();

        sharedPreferences = new SecurePreferences(this);
        globalClass = (GlobalClass) getApplicationContext();

        /** Complete button action listener. Listener to work after entering the RegCode*/
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(firstdigit_regcode.getText().length()==0 || seconddigit_regcode.getText().length()==0 || thirddigit_regcode.getText().length()==0 || fourthdigit_regcode.getText().length()==0 || fifthdigit_regcode.getText().length()==0)
                {
                    Toast.makeText(RegistrationStep2.this, getResources().getString(R.string.empty_fields),
                            Toast.LENGTH_SHORT).show();
                }
                else
                {

                    if(globalClass.checkWifiConnectivity(context)) {

                        /** Asynctask to call when complete button is pressed*/
                        new Complete_Registration_Task().execute(globalClass.getBase_url());
                    }
                    else
                    {
                        Toast.makeText(context, getResources().getString(R.string.check_wifi_string), Toast.LENGTH_LONG).show();

                    }

                }
            }
        });

        /** Retry button action listener.
         * If for some reason the API does not succeed, a value of -1 will be returned.
         * Because of wrong mobile number or delay of SMS. So retry with the mobile number.
         * */
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent retryintent = new Intent(RegistrationStep2.this, RegistrationStep1.class);
                retryintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(retryintent);
            }
        });

            /**  5 digit code placeholders*/

            /** First digit RegCode placeholder - 1st digit falls here and the onTextChanged will get called and move to the next digit placeholder*/
            firstdigit_regcode.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (firstdigit_regcode.getText().length() == 1) {
                    firstdigit_regcode.clearFocus();
                    seconddigit_regcode.requestFocus();
                }
            }
            });

            /** Seecond digit RegCode placeholder - 2nd digit falls here and the onTextChanged will get called and move to the next digit placeholder*/
            seconddigit_regcode.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (seconddigit_regcode.getText().length() == 1) {
                    seconddigit_regcode.clearFocus();
                    thirddigit_regcode.requestFocus();
                }
            }
            });

            /** Third digit RegCode placeholder - 3rd digit falls here and the onTextChanged will get called and move to the next digit placeholder*/
            thirddigit_regcode.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (thirddigit_regcode.getText().length() == 1) {
                        thirddigit_regcode.clearFocus();
                        fourthdigit_regcode.requestFocus();
                    }
                }
            });

            /** Fourth digit RegCode placeholder - 4th digit falls here and the onTextChanged will get called and move to the next digit placeholder*/
            fourthdigit_regcode.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (fourthdigit_regcode.getText().length() == 1) {
                    fourthdigit_regcode.clearFocus();
                    fifthdigit_regcode.requestFocus();
                }
            }

            });

            /** Code to listen the incoming SMS to get the RegCode*/
            IntentFilter filter = new IntentFilter(SMS_RECEIVED);
            registerReceiver(receiver_SMS, filter);



    }

        /** Method to refer the views that have been created in xml. Using te id of the view the widgets can be refered*/
        void addViews(){
        complete=(Button)findViewById(R.id.button_complete);
        retry=(Button)findViewById(R.id.button_retry);
        firstdigit_regcode =(EditText)findViewById(R.id.edittext_ota_first_digit);
        seconddigit_regcode =(EditText)findViewById(R.id.edittext_ota_second_digit);
        thirddigit_regcode =(EditText)findViewById(R.id.edittext_ota_third_digit);
        fourthdigit_regcode =(EditText)findViewById(R.id.edittext_ota_fourth_digit);
        fifthdigit_regcode =(EditText)findViewById(R.id.edittext_ota_fifth_digit);


            TextView id_reg_step2_label1 = (TextView) findViewById(R.id.id_reg_step2_label1);
            TextView id_reg_step2_label2 = (TextView) findViewById(R.id.id_reg_step2_label2);
            TextView id_reg_step2_label3 = (TextView) findViewById(R.id.id_reg_step2_label3);
            TextView id_reg_note = (TextView) findViewById(R.id.id_reg_step2_label1);

        /** Setting typeface for views*/
        id_reg_step2_label1.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        id_reg_step2_label2.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        id_reg_step2_label3.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        id_reg_note.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        firstdigit_regcode.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        seconddigit_regcode.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        thirddigit_regcode.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        fourthdigit_regcode.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        fifthdigit_regcode.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        complete.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        retry.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));

        firstdigit_regcode.setInputType(InputType.TYPE_CLASS_NUMBER);
        complete.setTransformationMethod(null);
        retry.setTransformationMethod(null);
        }

    /** Background network operation can be performed in a separate thread. We must use AsyncTask for that.*/
    private class Complete_Registration_Task extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** Custom progressdialog to show loading symbol*/
            progressDialog = CustomProgressDialog.ctor(context,getResources().getString(R.string.completing_register));
            progressDialog.show();
        }

        protected String doInBackground(String... urls)
        {


            /** Sending the post data in JSON format in the body of the post*/
            String mResponseFromServer = null;

            try {
                /** body_in_post  POST data in JSON format.
                 * RegCode, Mobile number and CountryCode - Obtained in the first step
                 * */

                String mRegCode = firstdigit_regcode.getText().toString()+ seconddigit_regcode.getText().toString()+ thirddigit_regcode.getText().toString()+ fourthdigit_regcode.getText().toString()+ fifthdigit_regcode.getText().toString();
                SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                editor.putString(RegistrationStep2.this.mRegCode,mRegCode);
                editor.commit();

                /* Tags declaration*/
                String mTagCountryCode = GlobalClass.COUNTRY_CODE;
                String mTagMobileNumber = GlobalClass.MOBILE_NUMBER;
                String mTagMobileInfo = GlobalClass.MOBILE_INFO;
                String mPostBodyData = new JSONObject().put(RegistrationStep2.this.mRegCode,Integer.parseInt(sharedPreferences.getString(RegistrationStep2.this.mRegCode,""))).put(mTagMobileInfo,new JSONObject().put(mTagCountryCode,sharedPreferences.getString(mTagCountryCode,"")).put(mTagMobileNumber,sharedPreferences.getString(mTagMobileNumber,""))).toString();



                /** Calling Registrations(http://www.csharpsolutions.co.uk/ValidateApp/api/v1/Registrations/) API  and the response will be a statuscode and actual response from server in JSON format.*/
               /**
                * sendPost(String url,String urlParameters)
                * */
                mResponseFromServer = globalClass.sendPost(urls[0]+globalClass.getComplete_Registration_Request_Routes(),mPostBodyData);

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


            /** Sometimes response can be in negative value so it indicates error.*/
            if(!mResult.equals("error")&&!mResult.equals("-1"))
            {

                SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                editor.putBoolean(globalClass.getCheck_Login(),true);
                editor.commit();

                new GCMCredentials(context).execute();

            }

            else
            {
                if(progressDialog!=null)
                {
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                }
                Toast.makeText(RegistrationStep2.this,
                        getResources().getString(R.string.try_again),
                        Toast.LENGTH_SHORT).show();
            }



        }
    }


    class GCMCredentials extends AsyncTask<String, String, String> {
        private final ProgressDialog progressDialog;
        public GCMCredentials(Context ctx) {
            progressDialog = CustomProgressDialog.ctor(context,getResources().getString(R.string.completing_register));
            progressDialog.show();
        }


        // Download Music File from Internet
        @Override
        protected String doInBackground(String... params) {
            /**GCM*/
            GCMRegistrar.checkDevice(RegistrationStep2.this);
            // Make sure the manifest was properly set - comment out this line
            // while developing the app, then uncomment it when it's ready.
            GCMRegistrar.checkManifest(RegistrationStep2.this);

            registerReceiver(mHandleMessageReceiver, new IntentFilter(
                    DISPLAY_MESSAGE_ACTION));

            // Get GCM registration id
            // Check if regid already presents
            /*GCM*/
            String GCMregId = "";
            if (GCMregId.equals("")) {
                // Registration is not present, register now with GCM
                GCMRegistrar.register(RegistrationStep2.this, SENDER_ID);
            } else {
                // Device is already registered on GCM
                if (GCMRegistrar.isRegisteredOnServer(RegistrationStep2.this)) {
                    // Skips registration.
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.already_registered), Toast.LENGTH_LONG).show();
                }
            }
            return "success";
        }



        @Override
        protected void onPostExecute(String file_url) {

            Intent intent = new Intent(RegistrationStep2.this, UpdateScreen.class);
            startActivity(intent);
            finish();

        }
    }


    /** Broadcast Receiver to get the 5-digit code from SMS*/
    private final BroadcastReceiver receiver_SMS = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(SMS_RECEIVED))
            {
                Bundle bundle = intent.getExtras();
                if (bundle != null)
                {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] messages = new SmsMessage[pdus.length];

                    for (int i = 0; i < pdus.length; i++)
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    for (SmsMessage message : messages)
                    {
                        if(message.getDisplayMessageBody().contains(getResources().getString(R.string.reg_code_message)))
                        {

                            String mMessageBody = message.getDisplayMessageBody();
                            List<String> mMessageBodyItems = Arrays.asList(mMessageBody.split(":"));


                            String mRegCode = mMessageBodyItems.get(1).trim();


                            SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                            editor.putString(RegistrationStep2.this.mRegCode,mRegCode);
                            editor.commit();



                            /** Code to get the individual digit in the 5-digit code*/
                            if(mRegCode.length()==5)
                            {
                                firstdigit_regcode.setText(Character.toString(mRegCode.charAt(0)));
                                seconddigit_regcode.setText(Character.toString(mRegCode.charAt(1)));
                                thirddigit_regcode.setText(Character.toString(mRegCode.charAt(2)));
                                fourthdigit_regcode.setText(Character.toString(mRegCode.charAt(3)));
                                fifthdigit_regcode.setText(Character.toString(mRegCode.charAt(4)));
                            }

                        }
                    }
                }
            }
        }
    };



    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            Toast.makeText(context, getResources().getString(R.string.gcm_error), Toast.LENGTH_LONG).show();

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
            // Releasing wake lock
            WakeLocker.release();
        }
    };

    /** Method to be called when back button is pressed in this activity*/
    public void onBackPressed() {

        Intent intent = new Intent(RegistrationStep2.this,RegistrationStep1.class);
        startActivity(intent);
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
