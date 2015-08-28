package com.csharp.solutions.validations;

import com.csharp.solutions.validations.util.SystemUiHider;
import com.securepreferences.SecurePreferences;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import util.GlobalClass;
import util.TypefaceUtil;


/**
 An Activity to handle the message received via GCM
 */
public class NotificationHandleActivity extends Activity {

    TextView id_show_recv_notification;
    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_handle);
        sharedPreferences = new SecurePreferences(this);
        id_show_recv_notification = (TextView) findViewById(R.id.id_show_recv_notification);

        setTitle(sharedPreferences.getString(GlobalClass.NOTIFICATION_TITLE_TAG,""));

        id_show_recv_notification.setText(sharedPreferences.getString(GlobalClass.NOTIFICATION_MESSAGE,""));
        id_show_recv_notification.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));

    }


}
