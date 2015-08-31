package com.csharp.solutions.validations;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;


import gcm.WakeLocker;
import util.GlobalClass;
import util.TypefaceUtil;

import static gcm.CommonUtilities.DISPLAY_NOTIFICATION_ACTION;
import static gcm.CommonUtilities.NOTIFICATION_MESSAGE;
import static gcm.CommonUtilities.TRIGGER_NFC_ACTION;
import static gcm.CommonUtilities.CANCEL_PROGRESS_DIALOG;
import static gcm.CommonUtilities.SHOW_PROGRESS_DIALOG;


/**
 * Created by Arputha on 04/07/2015.
 */
/** ValidateScreen - This class will send udp broadcast message and listen for the endpoint. After receiving the endpoint it transfers the stored data in the UpdateScreen to the endpoint.
 * It also transfers the data to the NFC reader when a device is in contatct with the NFC reader.
 * */

@TargetApi(21)
public class ValidateScreen extends ActionBarActivity {

    /**Widgets*/
    Button validate;
    ImageView logo;
    ImageView note_icon;
    TextView textView;

    Context context = this;

    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    GlobalClass globalClass;
    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    SharedPreferences sharedPreferences;

    /** Progress dialog*/
    ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.validatescreen);

        /** Initialising the UI Widgets*/
        addViews();

        globalClass = (GlobalClass) getApplicationContext();
        sharedPreferences = new SecurePreferences(this);

        /** receiver to handle the GCM Messages*/
        registerReceiver(mHandleGCMNotificationReceiver, new IntentFilter(
                DISPLAY_NOTIFICATION_ACTION));

        /** receiver to handle the Cancelling of Progressdialog*/
        registerReceiver(mHandleprogressdialogReceiver, new IntentFilter(
                CANCEL_PROGRESS_DIALOG));


        /** Check is any GCM messages is present if yes then show note icon*/
        if(sharedPreferences.getString(GlobalClass.NOTIFICATION_MESSAGE,"").length()!=0)
        {
            note_icon.setVisibility(View.VISIBLE);
        }


        /** Validate button click event
         * Send the broadcast message and start the Datagramreceiver  to receive the UDP data.
         * */
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /** show progress dialog*/
                progressDialog = CustomProgressDialog.ctor(context,getResources().getString(R.string.validateloading));
                progressDialog.show();

                /** Send a broadcast message to broadcastreceiver to validate*/
                Intent intent = new Intent(TRIGGER_NFC_ACTION);
                intent.putExtra(SHOW_PROGRESS_DIALOG, true);
                sendBroadcast(intent);

            }
        });


        note_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                /** Show the GCM notification received.*/
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle(sharedPreferences.getString(GlobalClass.NOTIFICATION_TITLE_TAG,""));
                alertDialogBuilder.setMessage(sharedPreferences.getString(GlobalClass.NOTIFICATION_MESSAGE,"")).setCancelable(false);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(true);


            }
        });

    }

    /** Menu Options - Update and Validate*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /** Callback to receive when menu options are selected*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_update:
                Intent intent = new Intent(ValidateScreen.this, UpdateScreen.class);
                startActivity(intent);
                return true;
            case R.id.menu_validate:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /** Method to refer the views that have been created in xml. Using te id of the view the widgets can be refered*/
    public void addViews(){
        context=this;
        validate=(Button)findViewById(R.id.button_validate);
        logo=(ImageView)findViewById(R.id.imageview_logo);
        note_icon =(ImageView)findViewById(R.id.note_icon);
        validate.setTransformationMethod(null);
        textView = (TextView) findViewById(R.id.client_text);


        /** Setting typeface for views*/
        validate.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        textView.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
    }

    public final BroadcastReceiver mHandleGCMNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(NOTIFICATION_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            if(newMessage.length()!=0)
            {
                note_icon.setVisibility(View.VISIBLE);
            }

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


    public final BroadcastReceiver mHandleprogressdialogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            if(progressDialog!=null)
            {
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
            }

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


    @Override
    protected void onPause() {
        super.onPause();

        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (wifi != null){
            WifiManager.MulticastLock lock = wifi.createMulticastLock("mylock");
            if(lock.isHeld())
            {
                lock.release();
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

    }

}
