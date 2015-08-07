package com.csharp.solutions.validations;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
    Button update;
    ImageView logo;
    EditText work_number,home_number;
    TextView update_screen_label1,update_work_telephone_label,update_home_telephone_label;


    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    SharedPreferences sharedPreferences;

    String gcmID;
    /**GCM*/
    String GCMregId="";

    Context context = this;
    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    GlobalClass globalClass;

    /** Progress dialog*/
    ProgressDialog progressDialog;

    /** Tags declaration*/
    String tag_country_code = GlobalClass.country_code;
    String tag_mobile_number = GlobalClass.mobile_number;
    String tag_work_number = GlobalClass.work_number;
    String tag_home_number  = GlobalClass.home_number;
    String tag_instance_id  = GlobalClass.instance_id;
    String tag_gcm_token = GlobalClass.gcm_token;
    String tag_isgcmregistered  = GlobalClass.check_gcmisregistered;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatescreen);
        add_views();
        sharedPreferences = new SecurePreferences(this);
        globalClass = (GlobalClass) getApplicationContext();

        /** Show the updated values in the respective fields*/

        work_number.setText(sharedPreferences.getString(tag_work_number,""));
        home_number.setText(sharedPreferences.getString(tag_home_number,""));



        gcmID =  GCMRegistrar.getRegistrationId(UpdateScreen.this);
        System.out.println("GCMID"+gcmID);

        if(!sharedPreferences.getBoolean(tag_isgcmregistered,false))
        {
            if(gcmID.length()!=0)
            {
                SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                editor.putString(tag_gcm_token,gcmID);
                editor.putBoolean(tag_isgcmregistered,true);
                editor.commit();

                new GCMRegistrationRequest().execute(globalClass.getBase_url());
            }
        }




        /** temp code to be remove*/

        int icon = R.drawable.note_icon;
        long when = System.currentTimeMillis();
        String message="Message from server";
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, NotificationHandleActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 1, notificationIntent, 0);

        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        //notification.defaults |= Notification.DEFAULT_SOUND;

        // notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.siren);

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(1, notification);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(work_number.getText().toString().length()!=0&&work_number.getText().toString().length()!=0)
                {
                    SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                    editor.putString(tag_work_number,work_number.getText().toString());
                    editor.putString(tag_home_number,home_number.getText().toString());
                    editor.commit();

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
    public void add_views(){
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
                String body_in_post = new JSONObject().put(tag_country_code,sharedPreferences.getString(tag_country_code,"")).put(tag_mobile_number, sharedPreferences.getString(tag_mobile_number, "")).put(tag_instance_id, "").put(tag_gcm_token, sharedPreferences.getString(tag_gcm_token, "")).toString();
                System.out.println(globalClass.TAG+body_in_post);

                /** Calling GCMRegistrationRequest(http://www.csharpsolutions.co.uk/ValidateApp/api/v1/GCMRegistrationRequest/) API  and the response will be a statuscode and actual response from server in JSON format.*/
                response_from_server = globalClass.sendPost(urls[0]+globalClass.get_gcm_registration_request(),body_in_post);

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
                        "Success",
                        Toast.LENGTH_SHORT).show();
            }

            else
            {

                Toast.makeText(UpdateScreen.this,
                        "Server error : ",
                        Toast.LENGTH_SHORT).show();
            }



        }
    }


    public void onBackPressed() {
        Intent intent = new Intent(UpdateScreen.this,RegistrationStep2.class);
        startActivity(intent);
    }
}
