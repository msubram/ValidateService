package com.csharp.solutions.validations;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.securepreferences.SecurePreferences;

import java.util.Arrays;
import java.util.List;

import util.GlobalClass;

import static gcm.CommonUtilities.SENDER_ID;
import static gcm.CommonUtilities.displayMessage;

public class GCMIntentService extends GCMBaseIntentService {
Context context=this;
    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
   static SharedPreferences sharedPreferences;
	private static final String TAG = "GCMIntentService";
    public GCMIntentService() {
        super(SENDER_ID);

    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);

        sharedPreferences = new SecurePreferences(this);

        GlobalClass globalClass = (GlobalClass) getApplicationContext();

        SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
        editor.putString(globalClass.gcm_token,registrationId);
        editor.commit();

        displayMessage(context, "Your device registred with GCM");

    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        GlobalClass globalClass = (GlobalClass) getApplicationContext();
        Log.i("onMessage", extras.getString("gcm.notification.body"));


        sharedPreferences = new SecurePreferences(this);

        String title = extras.getString("gcm.notification.title");
        String message = extras.getString("gcm.notification.body");



        generateNotification(context,title,message);

    }



    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context,"", message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
	private static void generateNotification(Context context,String title, String message) {


        int notif_id = sharedPreferences.getInt("notif_id",1);



        SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
        editor.putInt("notif_id", notif_id + 1);
        editor.commit();


        int icon = R.drawable.note_icon;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);


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





    }


}
