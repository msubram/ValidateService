package com.csharp.solutions.validations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import util.GlobalClass;

/**
 * Created by Arputha on 04/07/2015.
 */
public class ValidateScreen extends ActionBarActivity {
    Button validate;
    ImageView logo;
    TextView textView;


    Context context = this;
    String tag = "UDP";
    private static final int UDP_SERVER_PORT = 38798;
    private final static int PACKETSIZE = 1000 ;

    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    GlobalClass globalClass;
    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    SharedPreferences sharedPreferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.validatescreen);
        add_views();
        globalClass = (GlobalClass) getApplicationContext();
       // UDP_SERVER_PORT = Integer.parseInt(globalClass.get_Udp_port());

        sharedPreferences = new SecurePreferences(this);


        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "Validation Sucess", Toast.LENGTH_LONG).show();

                new SendBroadcast(context).execute("");
                new RunServer(context).execute("");
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
    public void add_views(){
        context=this;
        validate=(Button)findViewById(R.id.button_validate);
        logo=(ImageView)findViewById(R.id.imageview_logo);
        validate.setTransformationMethod(null);

        textView = (TextView) findViewById(R.id.client_text);


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
        //String msg = "{CountryCode:'44', MobileNumber:'9566510535', IP: '192.168.0.12', Name: 'Srinath', EndPoint: '', UDPListenPort: '38798'}";
        String coutry_code = sharedPreferences.getString("country_code","");
        String mobile_number = sharedPreferences.getString("mobile_number","");
        String IP = "192.168.0.12";
        String port = Integer.toString(UDP_SERVER_PORT);

        DatagramSocket socket = null ;

        try {


            JSONObject broadcaste_message_json = new JSONObject().put("CountryCode",coutry_code).put("MobileNumber",mobile_number).put("IP",IP).put("Name","motoe").put("EndPoint","").put("UDPListenPort",port);
            System.out.println( tag+"message"+broadcaste_message_json.toString() ) ;

            // Convert the arguments first, to ensure that they are valid
            //  InetAddress host = InetAddress.getByName("192.168.0.39") ;
            InetAddress host = getBroadcastAddress() ;
            // InetAddress host = InetAddress.getByName("255.255.255.255") ;
            System.out.println( tag+host.toString() ) ;
            // Construct the socket
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            System.out.println( tag+"setBroadcast" ) ;
            byte [] data = broadcaste_message_json.toString().getBytes() ;
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



    public void onBackPressed() {
        Intent intent = new Intent(ValidateScreen.this,UpdateScreen.class);
        startActivity(intent);
    }

}
