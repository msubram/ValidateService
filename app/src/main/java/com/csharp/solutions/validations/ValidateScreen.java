package com.csharp.solutions.validations;

import android.app.ProgressDialog;
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
import java.net.NetworkInterface;
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
    String mTag = GlobalClass.TAG;

    /**UDP Port and Packet Size*/
    private static final int UDP_LISTENER_PORT = 38798;
    private static final int UDP_BROADCAST_PORT = 32233;
    private final static int PACKETSIZE = 1000 ;

    /** GlobalClass - Extends Application class in which the values can be set and accessed from a single place*/
    GlobalClass globalClass;
    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    SharedPreferences sharedPreferences;

    /** Receiving Socket*/
    DatagramSocket rSocket = null ;

    /** Tags declaration*/
    String mTagCountryCode = GlobalClass.COUNTRY_CODE;
    String mTagMobileNumber = GlobalClass.MOBILE_NUMBER;
    String mTagWorkNumber = GlobalClass.WORK_NUMBER;
    String mTagHomeNumber = GlobalClass.HOME_NUMBER;
    String mTagIpAddress = GlobalClass.IP_ADDRESS;
    String mTagName = GlobalClass.SENDER_NAME;
    String mTagEndPoint = GlobalClass.END_POINT;
    String mTagUdpListenPort = GlobalClass.UDP_LISTEN_PORT;
    String mTagEmail = GlobalClass.EMAIL;
    String mTagMobileCountryCode = GlobalClass.MOBILE_COUNTRY_CODE;

    /** Progress dialog*/
    ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.validatescreen);
        System.out.println( mTag +"onCreate" ) ;

        addViews();

        globalClass = (GlobalClass) getApplicationContext();
       // UDP_SERVER_PORT = Integer.parseInt(globalClass.get_Udp_port());
        sharedPreferences = new SecurePreferences(this);


        /** Validate button click event
         * Send the broadcast message and start the Datagramreceiver  to receive the UDP data.
         * */
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(globalClass.checkWifiConnectivity())
                {

                    System.out.println("myDatagramReceiver Start");

                    WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                    if (wifi != null){
                        WifiManager.MulticastLock lock = wifi.createMulticastLock("mylock");
                        lock.acquire();
                    }

                    /** show progress dialog*/
                    progressDialog = CustomProgressDialog.ctor(context);
                    progressDialog.show();

                    /** Start a thread to listen for incoming udp messages*/
                    myDatagramReceiver = new MyDatagramReceiver();
                    myDatagramReceiver.start();

                    /** Asynctask to Broadcast the message*/
                    new SendBroadcast(context).execute("");

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
    public void addViews(){
        context=this;
        validate=(Button)findViewById(R.id.button_validate);
        logo=(ImageView)findViewById(R.id.imageview_logo);
        validate.setTransformationMethod(null);
        textView = (TextView) findViewById(R.id.client_text);

        /** Setting typeface for views*/
        validate.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
        textView.setTypeface(TypefaceUtil.getMyFont(getApplicationContext()));
    }


    /** Method to send the broadcast message with the following items
     * CountryCode, MobileNumber, IP, Name, EndPoint and UDPListenPort
     * Construct the JSON format of the above items and send the message in the DatagramPacket.
     * */
    private void sendBroadcastMessage()  {
        String mcountryCode = sharedPreferences.getString(mTagCountryCode,"");
        String mMobileNumber = sharedPreferences.getString(mTagMobileNumber,"");
        String mLocalIpAddress = getLocalIpAddress();
        String mListenPort = Integer.toString(UDP_LISTENER_PORT);

        DatagramSocket socket = null ;

        try {

            /** Broadcast data JSON formation*/
            JSONObject broadcaste_message_json = new JSONObject().put(mTagCountryCode,mcountryCode).put(mTagMobileNumber,mMobileNumber).put(mTagIpAddress,mLocalIpAddress).put(mTagName,"motoe").put(mTagEndPoint,"").put(mTagUdpListenPort,mListenPort);
            System.out.println( mTag +"message"+broadcaste_message_json.toString() ) ;

            /** To get the broadcast address of a WIFI on which device is connected*/
            InetAddress host = getBroadcastAddress() ;
            System.out.println( mTag +host.toString() ) ;

            // Construct the socket
            socket = new DatagramSocket();

            /** Setting the broadcast*/
            socket.setBroadcast(true);
            System.out.println( mTag +"setBroadcast" );

            byte [] data = broadcaste_message_json.toString().getBytes() ;

            /** DatagramPacket with the
             * data - JSON data
             * host - Broadcast address
             * port - Port on which the data is going to broadcast*/
            DatagramPacket packet = new DatagramPacket( data, data.length,host, UDP_BROADCAST_PORT);

            // Send the packet in the socket
            socket.send( packet ) ;
            System.out.println( mTag +"message sent" );

            Thread.sleep(500);


        }
        catch( Exception e )
        {
            System.out.println(mTag +e ) ;
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



            JSONObject endpointbodyJSON = new JSONObject().put(mTagEmail,"")
                                                          .put(mTagHomeNumber,sharedPreferences.getString(mTagHomeNumber,""))
                                                          .put(mTagWorkNumber,sharedPreferences.getString(mTagWorkNumber,""))
                                                          .put(mTagMobileNumber,sharedPreferences.getString(mTagMobileNumber,""))
                                                          .put(mTagMobileCountryCode,sharedPreferences.getString(mTagCountryCode,""));

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



    /** Asynctask to SendBroadcast message*/
    private class SendBroadcast extends
            AsyncTask<String, Void, String> {
        Context mContext;

        public SendBroadcast(Context context) {
            super();
            mContext = context;
        }


        @Override
        protected String doInBackground(String... params) {


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                sendBroadcastMessage();
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
          /*  progressDialog = CustomProgressDialog.ctor(context);
            progressDialog.show();*/
        }


        @Override
        protected String doInBackground(String... params) {
            String text=null;


            return  fillEndPointurl(params[0]);

        }
        protected void onPostExecute(String result) {
            /** Sometimes response can be in negatve value so it indicates error.*/
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }

            if(!result.equals("error")&&!result.equals("-1"))
            {
                Toast.makeText(ValidateScreen.this,
                        getResources().getString(R.string.success),
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(ValidateScreen.this,
                        getResources().getString(R.string.try_again),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }



    private MyDatagramReceiver myDatagramReceiver = null;

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println( mTag +"onResume" ) ;
     //   myDatagramReceiver = new MyDatagramReceiver();
       // myDatagramReceiver.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println( mTag +"onPause" ) ;
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

    /** Thread to listen for the UDP Message on the specified port*/
    private class MyDatagramReceiver extends Thread {
        private boolean bKeepRunning = true;
        private String lastMessage = "";

        public void run() {
            String message;

            System.out.println(mTag + "MyDatagramReceiver run");
            try {
                rSocket= new DatagramSocket(UDP_LISTENER_PORT);
                rSocket.setReuseAddress(true);
                // SocketAddress socketAddr=new InetSocketAddress(UDP_SERVER_PORT);

                rSocket.setBroadcast(true);
                // rSocket.bind(socketAddr);
                rSocket.setSoTimeout(10000);

                while(bKeepRunning) {
                    byte[] lmessage = new byte[PACKETSIZE];
                    System.out.println(mTag + "MyDatagramReceiver waiting for message");
                    DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

                    rSocket.receive(packet);
                    System.out.println(mTag + "Received"+new String(packet.getData()));
                    message = new String(lmessage, 0, packet.getLength());
                    lastMessage = message;

                    /** Once the message is received call the endpoint url and send the data stored by the user.*/
                    runOnUiThread(updateMessage);
                }
            } catch (Throwable e) {
                e.printStackTrace();
                System.out.println(mTag + e);
                if (rSocket != null) {
                    System.out.println(mTag + "Throwable rSocket close");
                    rSocket.close();
                    if(progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
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

    private Runnable updateMessage = new Runnable() {
        public void run() {
            if (myDatagramReceiver == null) return;
            System.out.println(mTag + "updateTextMessage Received"+myDatagramReceiver.getLastMessage());
            new FillEndPointUrl(context).execute(myDatagramReceiver.getLastMessage());

        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
    }



}
