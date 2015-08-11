package com.csharp.solutions.validations;

import com.csharp.solutions.validations.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_handle);

        Bundle extras = getIntent().getExtras();
        id_show_recv_notification = (TextView) findViewById(R.id.id_show_recv_notification);

        if(extras!=null)
        {
            id_show_recv_notification.setText(extras.getString(GlobalClass.NOTIFICATION_MESSAGE));
            id_show_recv_notification.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        }

    }
}
