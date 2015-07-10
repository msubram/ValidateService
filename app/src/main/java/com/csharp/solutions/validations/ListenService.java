package com.csharp.solutions.validations;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;

public class ListenService extends Service {

    private static final String TAG = "UDP";
    Context context = this;
    private boolean isRunning  = false;
    private static final int UDP_SERVER_PORT = 11111;
    private static final int MAX_UDP_DATAGRAM_LEN = 1500;
    private final static int PACKETSIZE = 100 ;
    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
       // new ValidateThread(context).execute("");
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                runUdpServer();
            }
        };
        splashThread.start();
        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
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

            runUdpServer();
            return text;

        }
        protected void onPostExecute(String result) {
        }
    }

    private void runUdpServer() {
       /* String lText;
        byte[] lMsg = new byte[MAX_UDP_DATAGRAM_LEN];
        DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
        DatagramSocket ds = null;
        try {
            DatagramChannel channel = DatagramChannel.open();
            ds = channel.socket();
            ds.setReuseAddress(true);
            ds.bind(new InetSocketAddress(UDP_SERVER_PORT));
            System.out.println(TAG+"UDP_SERVER_PORT"+ UDP_SERVER_PORT);
            //disable timeout for testing
            //ds.setSoTimeout(100000);
            ds.receive(dp);
            lText = new String(dp.getData(), 0, dp.getLength());
            Log.i(TAG+"UDP packet received", lText);
           // textView.setText(lText);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println(TAG+"Exception"+e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(TAG+"Exception"+e.toString());
        } finally {
            if (ds != null) {
                ds.close();
            }
        }*/
        try
        {
            // Convert the argument to ensure that is it valid
            int port = UDP_SERVER_PORT ;

            // Construct the socket
            System.out.println( TAG+"The server starting" ) ;
            DatagramSocket socket = new DatagramSocket( port ) ;

            System.out.println( TAG+"The server is ready..." ) ;


            for( ;; )
            {
                // Create a packet
                DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;

                // Receive a packet (blocking)
                socket.receive( packet ) ;

                // Print the packet
                System.out.println( TAG+packet.getAddress() + " " + packet.getPort() + ": " + new String(packet.getData()) ) ;

                // Return the packet to the sender
                socket.send( packet ) ;
            }
        }
        catch( Exception e )
        {
            System.out.println( TAG+e ) ;
        }

    }
}