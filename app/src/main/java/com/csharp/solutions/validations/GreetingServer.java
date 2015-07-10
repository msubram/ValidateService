package com.csharp.solutions.validations;// File Name GreetingServer.java

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import java.net.*;
import java.io.*;

public class GreetingServer extends ActionBarActivity
{
   private ServerSocket serverSocket;
            Context context =this;
    protected static String DEBUG_TAG = "Validation";
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);

        new ValidateThread(context).execute("");

    }
    private class ValidateThread extends
            AsyncTask<String, Void, String> {
        Context mContext;
        double type;
        public ValidateThread(Context context) {
            super();
            mContext = context;
        }


        @Override
        protected String doInBackground(String... params) {
            String text=null;


          try{

              int server_port = 9000;
              byte[] message = new byte[1500];
              DatagramPacket p = new DatagramPacket(message, message.length);
              DatagramSocket s = new DatagramSocket(server_port);
              s.receive(p);
              text = new String(message, 0, p.getLength());
              Log.d("Udp tutorial","message:" + text);
              System.out.println(DEBUG_TAG + "Message" + text);
              s.close();
          }
          catch(Exception e)
          {
              e.printStackTrace();
          }

            return text;

        }
        protected void onPostExecute(String result) {
            text.setText(result);
        }
    }


    public static String ipToString(int ip, boolean broadcast) {
        String result = new String();

        Integer[] address = new Integer[4];
        for(int i = 0; i < 4; i++)
            address[i] = (ip >> 8*i) & 0xFF;
        for(int i = 0; i < 4; i++) {
            if(i != 3)
                result = result.concat(address[i]+".");
            else result = result.concat("255.");
        }
        return result.substring(0, result.length() - 2);
    }
}