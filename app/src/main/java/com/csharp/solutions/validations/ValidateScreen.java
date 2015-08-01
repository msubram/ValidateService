package com.csharp.solutions.validations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

import util.GlobalClass;
import util.TypefaceUtil;

/**
 * Created by Arputha on 04/07/2015.
 */
public class ValidateScreen extends ActionBarActivity {

    /**Widgets*/
    Button validate;
    ImageView logo;
    TextView textView;


    Context context = this;
    String tag = "UDP";

    /**UDP Port and Packet Size*/
    private static final int UDP_SERVER_PORT = 38798;
    private final static int PACKETSIZE = 1000 ;

    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    GlobalClass globalClass;
    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    SharedPreferences sharedPreferences;


    DatagramSocket rSocket = null ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.validatescreen);
        System.out.println( tag+"onCreate" ) ;
        add_views();
        globalClass = (GlobalClass) getApplicationContext();
       // UDP_SERVER_PORT = Integer.parseInt(globalClass.get_Udp_port());

        sharedPreferences = new SecurePreferences(this);


        /** Validate button click event
         * Send the broadcast message and start the server to receive the data.
         * */
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(globalClass.checkWifiConnectivity())
                {
                    Toast.makeText(context, "Validation Success", Toast.LENGTH_LONG).show();

                    /**Asynctasks to send and receive broadcast messages*/

                   // startService(new Intent(ValidateScreen.this, UDPListenerService.class));

                        System.out.println("myDatagramReceiver Start");
                    WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                    if (wifi != null){
                        WifiManager.MulticastLock lock = wifi.createMulticastLock("mylock");
                        lock.acquire();
                    }

                        myDatagramReceiver = new MyDatagramReceiver();
                        myDatagramReceiver.start();


                        new SendBroadcast(context).execute("");
                   //new ListenerTask(context).execute("");


                }
                else
                {
                    Toast.makeText(context, getResources().getString(R.string.check_wifi_string), Toast.LENGTH_LONG).show();
                }



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
    public void add_views(){
        context=this;
        validate=(Button)findViewById(R.id.button_validate);
        logo=(ImageView)findViewById(R.id.imageview_logo);
        validate.setTransformationMethod(null);

        textView = (TextView) findViewById(R.id.client_text);

        /** Setting typeface for views*/
        validate.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        textView.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
    }




    /** Asynctask to SendBroadcast message*/
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

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                sendbroadcastmessage();
            }

            return "";

        }
        protected void onPostExecute(String result) {
        }
    }

    /** Asynctask to Fill the message in the Endpoint URL*/
    private class FillEndPointUrl extends
            AsyncTask<String, Void, String> {
        Context mContext;
        double type;
        public FillEndPointUrl(Context context) {
            super();
            mContext = context;
        }


        @Override
        protected String doInBackground(String... params) {
            String text=null;


            return  fillEndPointurl(params[0]);

        }
        protected void onPostExecute(String result) {
            /** Sometimes response can be in negatve value so it indicates error.*/
            if(!result.equals("error")&&!result.equals("-1"))
            {
                Toast.makeText(ValidateScreen.this,
                        "Success : ",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(ValidateScreen.this,
                        "Server error : ",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
    /** Method to send the broadcast message with the following items
     * CountryCode, MobileNumber, IP, Name, EndPoint and UDPListenPort
     * Construct the JSON format of the above items and send the message in the DatagramPacket.
     * */
    private void sendbroadcastmessage()  {
        //String msg = "{CountryCode:'44', MobileNumber:'9566510535', IP: '192.168.0.12', Name: 'Srinath', EndPoint: '', UDPListenPort: '38798'}";
        String coutry_code = sharedPreferences.getString("country_code","");
        String mobile_number = sharedPreferences.getString("mobile_number","");
        String IP = getLocalIpAddress();
        String port = Integer.toString(UDP_SERVER_PORT);

        DatagramSocket socket = null ;

        try {

            /** Broadcast data JSON formation*/
            JSONObject broadcaste_message_json = new JSONObject().put("CountryCode",coutry_code).put("MobileNumber",mobile_number).put("IP",IP).put("Name","motoe").put("EndPoint","").put("UDPListenPort",port);
            System.out.println( tag+"message"+broadcaste_message_json.toString() ) ;

            /** To get the broadcast address of a WIFI on which device is connected*/
            InetAddress host = getBroadcastAddress() ;
            System.out.println( tag+host.toString() ) ;

            // Construct the socket
            socket = new DatagramSocket();

            /** Setting the broadcast*/
            socket.setBroadcast(true);
            System.out.println( tag+"setBroadcast" );

            byte [] data = broadcaste_message_json.toString().getBytes() ;

            /** DatagramPacket with the
             * data - JSON data
             * host - Broadcast address
             * port - Port on which the data is going to broadcast*/
            DatagramPacket packet = new DatagramPacket( data, data.length,host,32233 );

            // Send the packet in the socket
            socket.send( packet ) ;
            System.out.println( tag+"message sent" );

            Thread.sleep(500);


        }
        catch( Exception e )
        {
            System.out.println(tag+e ) ;
        }

    }





    /** Method to fill the endpoint url with the Mobile, Home and Work Telephone numbers */
    private String fillEndPointurl(String url)  {
        String response_from_server=null;
    /** Format of the POST body content*/

        /*     {
                "Email": "email@domain.com",
                "HomeNumber": "02098999899",
                "WorkNumber": "02011123344",
                "MobileNumber": "07988676543",
                "MobileCountryCode": "44"
                }
        */

        /** Forming JSON to send the body of the POST*/
        try {



            JSONObject endpointbodyJSON = new JSONObject().put("Email","")
                                                          .put("HomeNumber",sharedPreferences.getString("home_number",""))
                                                          .put("WorkNumber",sharedPreferences.getString("work_number",""))
                                                          .put("MobileNumber",sharedPreferences.getString("mobile_number",""))
                                                          .put("MobileCountryCode",sharedPreferences.getString("country_code",""));

            System.out.println("Fillendpoint body"+endpointbodyJSON.toString());

            response_from_server = globalClass.sendPost(url,endpointbodyJSON.toString());
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
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return response_from_server;
    }

    /** Method to get the Broadcast address of WIFI on which the device is connecte*/
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

    /** Method to get the local IP address of a device connected to the WIFI*/
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("UDP"+ex.toString());
        }
        return null;
    }

    public void onBackPressed() {
        Intent intent = new Intent(ValidateScreen.this,UpdateScreen.class);
        startActivity(intent);
    }

    private MyDatagramReceiver myDatagramReceiver = null;

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println( tag+"onResume" ) ;
     //   myDatagramReceiver = new MyDatagramReceiver();
       // myDatagramReceiver.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println( tag+"onPause" ) ;
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (wifi != null){
            WifiManager.MulticastLock lock = wifi.createMulticastLock("mylock");
            if(lock.isHeld())
            {
                lock.release();
            }

        }
        if(myDatagramReceiver!=null)
        {
            myDatagramReceiver.kill();
        }


    }

    private class MyDatagramReceiver extends Thread {
        private boolean bKeepRunning = true;
        private String lastMessage = "";

        public void run() {
            String message;

            System.out.println(tag + "MyDatagramReceiver run");
            try {
                rSocket= new DatagramSocket(UDP_SERVER_PORT);
                rSocket.setReuseAddress(true);
               // SocketAddress socketAddr=new InetSocketAddress(UDP_SERVER_PORT);

                rSocket.setBroadcast(true);
               // rSocket.bind(socketAddr);
                rSocket.setSoTimeout(10000);

                while(bKeepRunning) {
                    byte[] lmessage = new byte[PACKETSIZE];
                    System.out.println(tag + "MyDatagramReceiver waiting for message");
                    DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

                    rSocket.receive(packet);
                    System.out.println(tag + "Received"+new String(packet.getData()));
                    message = new String(lmessage, 0, packet.getLength());
                    lastMessage = message;

                    runOnUiThread(updateTextMessage);
                }
            } catch (Throwable e) {
                e.printStackTrace();
                System.out.println(tag + e);
                if (rSocket != null) {
                    System.out.println(tag + "Throwable rSocket close");
                    rSocket.close();
                   // rSocket.disconnect();
                }
            }


        }

        public void kill() {
            bKeepRunning = false;
        }

        public String getLastMessage() {
            return lastMessage;
        }
    }

    private Runnable updateTextMessage = new Runnable() {
        public void run() {
            if (myDatagramReceiver == null) return;
            System.out.println(tag + "Received"+myDatagramReceiver.getLastMessage());
            textView.setText(myDatagramReceiver.getLastMessage());
            new FillEndPointUrl(context).execute(myDatagramReceiver.getLastMessage());
           /* if (rSocket != null) {
                System.out.println(tag + "rSocket close");
                rSocket.close();
                rSocket.disconnect();
            }*/
        }
    };

}
