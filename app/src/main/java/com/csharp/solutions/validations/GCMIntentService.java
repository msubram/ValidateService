package com.csharp.solutions.validations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.securepreferences.SecurePreferences;

import org.json.JSONObject;

import util.GlobalClass;

import static gcm.CommonUtilities.SENDER_ID;
import static gcm.CommonUtilities.displayMessage;
import static gcm.CommonUtilities.displayNotification;


public class GCMIntentService extends GCMBaseIntentService {

    private final Context context = this;

    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    private static SharedPreferences sharedPreferences;

    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    private GlobalClass globalClass;
    public GCMIntentService() {
        super(SENDER_ID);

    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {


        sharedPreferences = new SecurePreferences(this);
        globalClass = (GlobalClass) getApplicationContext();

        SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
        editor.putString(globalClass.GCM_TOKEN,registrationId);
        editor.commit();

        if(registrationId.length()!=0)
        {
            new GCMRegistrationRequest().execute(globalClass.getBase_url());
        }



    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {

    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        String title = extras.getString(GlobalClass.NOTIFICATION_TITLE_TAG);
        String message = extras.getString(GlobalClass.NOTIFICATION_BODY_TAG);

        sharedPreferences = new SecurePreferences(this);
        SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
        editor.putString(GlobalClass.NOTIFICATION_TITLE_TAG,title);
        editor.putString(GlobalClass.NOTIFICATION_MESSAGE,message);
        editor.commit();

        displayNotification(context, message);


    }



    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {



    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {

        displayMessage(context,  getString(R.string.gcm_error));

    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message

        displayMessage(context, getString(R.string.gcm_error));
        return super.onRecoverableError(context, errorId);
    }



    private class GCMRegistrationRequest extends AsyncTask<String, Integer, String> {


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
                String mTagInstanceId = GlobalClass.INSTANCE_ID;
                String mTagGcmToken = GlobalClass.GCM_TOKEN;
                /* Tags declaration*/
                String mTagCountryCode = GlobalClass.COUNTRY_CODE;
                String mTagMobileNumber = GlobalClass.MOBILE_NUMBER;
                String body_in_post = new JSONObject().put(mTagCountryCode,sharedPreferences.getString(mTagCountryCode,"")).put(mTagMobileNumber, sharedPreferences.getString(mTagMobileNumber, "")).put(mTagInstanceId, "").put(mTagGcmToken, sharedPreferences.getString(mTagGcmToken, "")).toString();
                System.out.println(GlobalClass.TAG+body_in_post);

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

                if(result.equals("error"))
                {
                    Toast.makeText(context,
                            getResources().getString(R.string.try_again),
                            Toast.LENGTH_SHORT).show();
                }


        }
    }
}
