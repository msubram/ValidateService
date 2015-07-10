package com.csharp.solutions.validations;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    Context context = this;
    String TAG = "Validation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


                int port = 9000;
            String msg = "Hi from server";

               /* try {
                    String servername = getBroadcastAddress().toString();
                    System.out.println(TAG+"servername " + servername);

                    System.out.println(TAG+"Connecting to " + servername
                            + " on port " + port);
                    List<String> items = Arrays.asList(servername.split("/"));
                    String serverName = items.get(1);

                    System.out.println(TAG+"Connecting to " + serverName
                            + " on port " + port);
                    InetAddress local = InetAddress.getByName("192.168.0.6");

                    Socket client = new Socket(local, port);
                    System.out.println(TAG+"Just connected to "
                            + client.getRemoteSocketAddress());

                    OutputStream outToServer = client.getOutputStream();
                    DataOutputStream out =
                            new DataOutputStream(outToServer);

                    out.writeUTF("Hello from "
                            + client.getLocalSocketAddress());
                    InputStream inFromServer = client.getInputStream();
                    DataInputStream in =
                            new DataInputStream(inFromServer);
                    System.out.println(TAG+"Server says " + in.readUTF());
                    client.close();
                }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        System.out.println(TAG+e.toString());
                    }*/
            try{
                int server_port = 9000;

                WifiManager wim = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                int ip = wim.getConnectionInfo().getIpAddress();
                String servername = ipToString(ip, true);
                   // String servername = getBroadcastAddress().toString();
                    System.out.println(TAG+"servername " + servername);

                    System.out.println(TAG+"Connecting to " + servername
                            + " on port " + port);

                String messageStr="Hello Android!";
                DatagramSocket s = new DatagramSocket();
                InetAddress local = InetAddress.getByName(servername);
                int msg_length=messageStr.length();
                byte[] message = messageStr.getBytes();
                DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
                s.send(p);
                System.out.println(TAG+"Message Sent");
                    /*List<String> items = Arrays.asList(servername.split("/"));
                    String serverName = items.get(1);*/

                   /* String messageStr="Hello Android!";
                    DatagramSocket s = new DatagramSocket();
                    InetAddress local = InetAddress.getByName(servername);
                    int msg_length=messageStr.length();
                    byte[] message = messageStr.getBytes();
                    byte[] receiveData = new byte[1024];
                    DatagramPacket p = new DatagramPacket(message, msg_length,local,port);
                    s.send(p);
                  System.out.println(TAG+"waiting");
                    DatagramSocket rs = new DatagramSocket(port);
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    rs.receive(receivePacket);

                    String modifiedSentence = new String(receivePacket.getData());
                    System.out.println(TAG+"modifiedSentence " + modifiedSentence);*/
                }catch(Exception e)
                {
                    e.printStackTrace();
                    System.out.println(TAG+e.toString());
                }
            /*try {
                String servername = getBroadcastAddress().toString();
                System.out.println("servername " + servername);

                System.out.println("Connecting to " + servername
                        + " on port " + port);
                List<String> items = Arrays.asList(servername.split("/"));
                String serverName = items.get(1);

                DatagramSocket s = new DatagramSocket();
                InetAddress local  = InetAddress.getByName(serverName);
                int msg_lenght = msg.length();
                byte []message = msg.getBytes();
                DatagramPacket p = new DatagramPacket(message,msg_lenght,local,port);
                s.send(p);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }*/


            return "No address found";

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
