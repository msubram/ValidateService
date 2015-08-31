package com.csharp.solutions.validations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
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

import static gcm.CommonUtilities.CANCEL_PROGRESS_DIALOG;
import static gcm.CommonUtilities.SHOW_PROGRESS_DIALOG;

/**
 * Created by srinath on 31-08-2015.
 */
public class NFCBroadcastReceiver extends BroadcastReceiver {

    Context mContext;

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



    private final Handler mtoasthandler =new Handler();

    boolean mcancelProgressdialog;


    @Override
    public void onReceive(Context mreceiverContext, Intent intent) {
        mContext = mreceiverContext;

        globalClass = new GlobalClass();
        sharedPreferences = new SecurePreferences(mContext);

        mcancelProgressdialog = intent.getExtras().getBoolean(SHOW_PROGRESS_DIALOG);

        if(!mcancelProgressdialog)
        {
            Toast.makeText(mContext,
                    mContext.getResources().getString(R.string.validateloading),
                    Toast.LENGTH_SHORT).show();
        }

        validateMethod();
    }



    /** Method to send broadcast and receive endpoint url, and fills the stored data to the validate API and gets validated.*/
    private void validateMethod()
    {
        if(globalClass.checkWifiConnectivity(mContext))
        {
            WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
            if (wifi != null){
                WifiManager.MulticastLock lock = wifi.createMulticastLock("mylock");
                lock.acquire();
            }

            SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
            editor.putBoolean(GlobalClass.CHECK_MESSAGE_RECEIVED,false);
            editor.commit();


            /** Start a thread to listen for incoming udp messages*/
            myDatagramReceiver = new MyDatagramReceiver();
            myDatagramReceiver.start();

            /** Asynctask to Broadcast the message*/
            new SendBroadcast(mContext).execute("");

        }
        else
        {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.check_wifi_string), Toast.LENGTH_LONG).show();
        }
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

            /** To get the broadcast address of a WIFI on which device is connected*/
            InetAddress host = getBroadcastAddress() ;

            // Construct the socket
            socket = new DatagramSocket();

            /** Setting the broadcast*/
            socket.setBroadcast(true);

            byte [] data = broadcaste_message_json.toString().getBytes() ;

            /** DatagramPacket with the
             * data - JSON data
             * host - Broadcast address
             * port - Port on which the data is going to broadcast*/
            DatagramPacket packet = new DatagramPacket( data, data.length,host, UDP_BROADCAST_PORT);

            // Send the packet in the socket
            socket.send( packet ) ;

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
        WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
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

        }


        @Override
        protected String doInBackground(String... params) {

            return  fillEndPointurl(params[0]);

        }
        protected void onPostExecute(String result) {
            /** Sometimes response can be in negatve value so it indicates error.*/

            if(mcancelProgressdialog)
            {
                Intent intent = new Intent(CANCEL_PROGRESS_DIALOG);
                mContext.sendBroadcast(intent);
            }



            if(!result.equals("error"))
            {
                Toast.makeText(mContext,
                        mContext.getResources().getString(R.string.success),
                        Toast.LENGTH_SHORT).show();

                /** code to not show the toast once the validate is successful. Show it when the validation is not successfull or socket is timed out*/
                SecurePreferences.Editor editor = (SecurePreferences.Editor) sharedPreferences.edit();
                editor.putBoolean(GlobalClass.CHECK_MESSAGE_RECEIVED, true);
                editor.commit();
            }
            else
            {
                Toast.makeText(mContext,
                        mContext.getString(R.string.try_again),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    private MyDatagramReceiver myDatagramReceiver = null;


    /** Thread to listen for the UDP Message on the specified port*/
    private class MyDatagramReceiver extends Thread {
        private boolean bKeepRunning = true;
        private String lastMessage = "";

        public void run() {
            String message;


            try {
                rSocket= new DatagramSocket(UDP_LISTENER_PORT);
                rSocket.setReuseAddress(true);
                rSocket.setBroadcast(true);
                rSocket.setSoTimeout(10000);

                while(bKeepRunning) {
                    byte[] lmessage = new byte[PACKETSIZE];

                    DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);
                    rSocket.receive(packet);
                    message = new String(lmessage, 0, packet.getLength());
                    lastMessage = message;

                    if (myDatagramReceiver == null) return;

                    new FillEndPointUrl(mContext).execute(myDatagramReceiver.getLastMessage());
                }
            } catch (Throwable e) {
                e.printStackTrace();
                System.out.println(mTag + e);
                if (rSocket != null) {
                    rSocket.close();

                    if(!sharedPreferences.getBoolean(GlobalClass.CHECK_MESSAGE_RECEIVED,false))
                    {
                        mtoasthandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext,
                                        mContext.getResources().getString(R.string.try_again),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    if(mcancelProgressdialog)
                    {
                        Intent intent = new Intent(CANCEL_PROGRESS_DIALOG);
                        mContext.sendBroadcast(intent);
                    }


                }
            }


        }


        public String getLastMessage() {
            return lastMessage;
        }
    }


}