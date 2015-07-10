package com.csharp.solutions.validations;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Validate extends ActionBarActivity {
    protected static String DEBUG_TAG = "UDPMessenger"; // to log out things
    protected static final Integer BUFFER_SIZE = 4096; // size of the reading buffer

    protected String TAG; // chat TAG
    protected int MULTICAST_PORT; // chat port

    private boolean receiveMessages = false; // variable to know if we have to listen for incoming packets

    protected Context context=this; // the application's context, used to get network state info etc.
    private DatagramSocket socket; // the socket used to send the messages

  // the thread used to receive the multicast
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);

        UDPMessenger(context,"UDP Server",6066);
        new ValidateThread(context).execute("");
    }


    /**
     * Class constructor
     * @param context the application's context
     * @param tag a valid string, used to filter the UDP broadcast messages (in and out). It can't be null or 0-characters long.
     * @param multicastPort the port to multicast to. Must be between 1025 and 49151 (inclusive)
     */
    public void UDPMessenger(Context context, String tag, int multicastPort) throws IllegalArgumentException {
        if(context == null || tag == null || tag.length() == 0 ||
                multicastPort <= 1024 || multicastPort > 49151)
            throw new IllegalArgumentException();

        this.context = context.getApplicationContext();
        TAG = tag;
        MULTICAST_PORT = multicastPort;

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


    /**
     * Sends a broadcast message (TAG EPOCH_TIME message). Opens a new socket in case it's closed.
     * @param message the message to send (multicast). It can't be null or 0-characters long.
     * @return
     * @throws IllegalArgumentException
     */
    public boolean sendMessage(String message) throws IllegalArgumentException {
        System.out.println(DEBUG_TAG+"sendMessage");
        if(message == null || message.length() == 0)
            throw new IllegalArgumentException();

        // Check for WiFi connectivity
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(mWifi == null || !mWifi.isConnected())
        {
            Log.d(DEBUG_TAG, "Sorry! You need to be in a WiFi network in order to send UDP multicast packets. Aborting.");
            return false;
        }

        // Check for IP address
        WifiManager wim = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ip = wim.getConnectionInfo().getIpAddress();

        // Create the send socket
        if(socket == null) {
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                Log.d(DEBUG_TAG, "There was a problem creating the sending socket. Aborting.");
                e.printStackTrace();
                return false;
            }
        }

        // Build the packet
        DatagramPacket packet;

        byte data[] = new byte[BUFFER_SIZE];
        data = message.getBytes();
        try {
            System.out.println(DEBUG_TAG+ipToString(ip, true));
            packet = new DatagramPacket(data, data.length, InetAddress.getByName(ipToString(ip, true)), MULTICAST_PORT);
        } catch (UnknownHostException e) {
            Log.d(DEBUG_TAG, "It seems that " + ipToString(ip, true) + " is not a valid ip! Aborting.");
            e.printStackTrace();
            return false;
        }

        try {
            socket.send(packet);
            byte[] receiveData = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            System.out.println(DEBUG_TAG+ modifiedSentence);
            receiveMessages = true;
        } catch (IOException e) {
            Log.d(DEBUG_TAG, "There was an error sending the UDP packet. Aborted.");
            e.printStackTrace();
            return false;
        }



        return true;
    }


    public void startMessageReceiver() {

        WifiManager wim = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wim != null) {
            WifiManager.MulticastLock mcLock = wim.createMulticastLock(TAG);
            mcLock.acquire();
        }

                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket rPacket = new DatagramPacket(buffer, buffer.length);
                /*Socket rSocket;

                try {
                    rSocket = new Socket(MULTICAST_PORT);
                } catch (IOException e) {
                    Log.d(DEBUG_TAG, "Impossible to create a new MulticastSocket on port " + MULTICAST_PORT);
                    e.printStackTrace();
                    return;
                }*/

                //while(receiveMessages) {
                    try {
                        socket.receive(rPacket);
                    } catch (IOException e1) {
                        Log.d(DEBUG_TAG, "There was a problem receiving the incoming message.");
                        e1.printStackTrace();
                    }



                    byte data[] = rPacket.getData();
                    int i;
                    for(i = 0; i < data.length; i++)
                    {
                        if(data[i] == '\0')
                            break;
                    }

                    String messageText;

                    try {
                        messageText = new String(data, 0, i, "UTF-8");
                        System.out.println(DEBUG_TAG+messageText);
                    } catch (UnsupportedEncodingException e) {
                        Log.d(DEBUG_TAG, "UTF-8 encoding is not supported. Can't receive the incoming message.");
                        e.printStackTrace();
                    }


                //}

    }

    public class ValidateThread extends
            AsyncTask<String, Void, String> {
        Context mContext;
        double type;
        public ValidateThread(Context context) {
            super();
            mContext = context;

        }


        @Override
        protected String doInBackground(String... params) {
            sendMessage("Mobile number");
            startMessageReceiver();
            return "Success";

        }
        protected void onPostExecute(String result) {

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
