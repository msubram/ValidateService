package com.csharp.solutions.validations;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class UdpServer extends Activity {
    /** Called when the activity is first created. */
	private TextView textView;
    private static final String TAG = "UDP";
    Context context = this;
    private boolean isRunning  = false;
    private static final int UDP_SERVER_PORT = 11111;
    private static final int MAX_UDP_DATAGRAM_LEN = 1500;
    private final static int PACKETSIZE = 100 ;
    Handler handler = new Handler();
    DatagramSocket socket;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.udpserver);
        textView = (TextView) findViewById(R.id.text1);
        new ValidateThread(context).execute("");
        /*WifiManager wim = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ip = wim.getConnectionInfo().getIpAddress();
        String servername = ipToString(ip, true);
        Log.i(TAG+"servername", servername);*/
        /*AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), ListenService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, 0, 60000, pendingIntent);*/
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
            socket = new DatagramSocket( port ) ;

            System.out.println( TAG+"The server is ready..." ) ;

            handler.removeCallbacks(runable);
            handler.postDelayed(runable, 10000);
           /* for( ;; )
            {
                // Create a packet
                DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;

                // Receive a packet (blocking)
                socket.receive( packet ) ;

                // Print the packet
                final String data = new String(packet.getData());
                System.out.println( TAG+packet.getAddress() + " " + packet.getPort() + ": " + data ) ;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(data);
                    }
                });
                // Return the packet to the sender
                socket.send( packet ) ;
            }*/
        }
        catch( Exception e )
        {
            System.out.println( TAG+e ) ;
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


    Runnable runable = new Runnable() {

        @Override
        public void run() {
            try{
            for (; ; ) {
                // Create a packet
                DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);

                // Receive a packet (blocking)
                socket.receive(packet);

                // Print the packet
                final String data = new String(packet.getData());
                System.out.println(TAG + packet.getAddress() + " " + packet.getPort() + ": " + data);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("From Client "+data);
                    }
                });
                // Return the packet to the sender
                socket.send(packet);
            }
        }
            catch( Exception e )
            {
                System.out.println( TAG+e ) ;
            }
}
};
}