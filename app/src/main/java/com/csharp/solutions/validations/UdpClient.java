package com.csharp.solutions.validations;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class UdpClient extends Activity {
    /** Called when the activity is first created. */
    Context context = this;
    String tag = "UDP";
    private static final int UDP_SERVER_PORT = 38798;
    private final static int PACKETSIZE = 100 ;
    TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.udpclient);
        textView = (TextView) findViewById(R.id.client_text);

        new SendBroadcast(context).execute("");
        new RunServer(context).execute("");
        // finish();
    }

    private class RunServer extends
            AsyncTask<String, Void, String> {
        Context mContext;
        double type;
        public RunServer(Context context) {
            super();
            mContext = context;
        }


        @Override
        protected String doInBackground(String... params) {
            String text=null;

            runserver();
            return text;

        }
        protected void onPostExecute(String result) {
        }
    }

    private class SendBroadcast extends
            AsyncTask<String, Void, String> {
        Context mContext;
        double type;
        public SendBroadcast(Context context) {
            super();
            mContext = context;
        }


        @Override
        protected String doInBackground(String... params) {
            String text=null;

            sendbroadcastmessage();
            return text;

        }
        protected void onPostExecute(String result) {
        }
    }
    private void runserver()  {


        DatagramSocket socket = null ;
        try
        {
            // Convert the argument to ensure that is it valid

            // Construct the socket
            socket = new DatagramSocket( UDP_SERVER_PORT ) ;

            System.out.println( tag+"The server is ready..." ) ;


            for( ;; )
            {
                // Create a packet
                DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;

                // Receive a packet (blocking)
                socket.receive( packet ) ;

                // Print the packet
              //  System.out.println(tag+ packet.getAddress() + " " + packet.getPort() + ": " + new String(packet.getData()) ) ;

                final String data = new String(packet.getData());
                System.out.println(tag+data ) ;


                            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("received data "+data);
                }
            });

            }
        }
        catch( Exception e )
        {
            System.out.println( tag+e ) ;
        }
    }


    private void sendbroadcastmessage()  {
        String msg = "{CountryCode:'44', MobileNumber:'9566510535', IP: '192.168.0.3', Name: 'Srinath', EndPoint: '', UDPListenPort: '38798'}";

        DatagramSocket socket = null ;

        try {
            // Convert the arguments first, to ensure that they are valid
         //  InetAddress host = InetAddress.getByName("192.168.0.39") ;
            InetAddress host = getBroadcastAddress() ;
           // InetAddress host = InetAddress.getByName("255.255.255.255") ;
            System.out.println( tag+host.toString() ) ;
            // Construct the socket
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            System.out.println( tag+"setBroadcast" ) ;
            byte [] data = msg.getBytes() ;
            DatagramPacket packet = new DatagramPacket( data, data.length,host,UDP_SERVER_PORT );

            // Send it
            socket.send( packet ) ;
            System.out.println( tag+"message sent" ) ;

          /*  byte[] buf = new byte[1024];
            DatagramPacket receivedpacket = new DatagramPacket(buf, buf.length);
            socket.receive(receivedpacket);
            System.out.println( tag+"message received" ) ;
            // Print the response
            System.out.println( tag+"Client side received"+new String(receivedpacket.getData()) ) ;*/
        }
        catch( Exception e )
        {
            System.out.println(tag+e ) ;
        }
        finally
        {
            if( socket != null )
                socket.close() ;
        }
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);

        return InetAddress.getByAddress(quads);
    }
}