package com.csharp.solutions.validations;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.util.ExceptionUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


/*
 * Linux command to send UDP:
 * #socat - UDP-DATAGRAM:192.168.1.255:11111,broadcast,sp=11111
 */
public class UDPListenerService extends Service {
    static String UDP_BROADCAST = "UDPBroadcast";

    //Boolean shouldListenForUDPBroadcast = false;
    DatagramSocket socket=null;
    private final static int PACKETSIZE = 1000 ;
    private static final int UDP_SERVER_PORT = 38798;
    String tag = "UDP";
    Context context = this;

    private void listenAndWaitAndThrowIntent( Integer port) throws Exception {
        byte[] recvBuf = new byte[PACKETSIZE];
        if (socket == null || socket.isClosed()) {
            System.out.println(tag + "Socket initialisation");
            socket = new DatagramSocket(port);
            socket.setBroadcast(true);
            //socket.setReuseAddress(true);
        }
        //socket.setSoTimeout(1000);
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        System.out.println(tag + "Waiting for UDP broadcast");
        socket.receive(packet);

        String senderIP = packet.getAddress().getHostAddress();
        String message = new String(packet.getData()).trim();

        System.out.println(tag + "Got UDP broadcast from " + senderIP + ", message: " + message);

        broadcastIntent(senderIP, message);
        socket.close();
    }

    private void broadcastIntent(String senderIP, String message) {
        Intent intent = new Intent(UDPListenerService.UDP_BROADCAST);
        intent.putExtra("sender", senderIP);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    Thread UDPBroadcastThread;

    void startListenForUDPBroadcast() {
        UDPBroadcastThread = new Thread(new Runnable() {
            public void run() {
                try {
                  //  InetAddress broadcastIP = InetAddress.getByName("172.16.238.255"); //172.16.238.42 //192.168.1.255
                   // Integer port = 11111;
                    while (shouldRestartSocketListen) {
                        listenAndWaitAndThrowIntent(UDP_SERVER_PORT);
                    }
                    //if (!shouldListenForUDPBroadcast) throw new ThreadDeath();
                } catch (Exception e) {
                    System.out.println(tag + "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                }
            }
        });
        UDPBroadcastThread.start();
    }

    private Boolean shouldRestartSocketListen=true;

    void stopListen() {
        shouldRestartSocketListen = false;
        socket.close();
       // stopService(new Intent(context, UDPListenerService.class));
    }

    @Override
    public void onCreate() {

    };

    @Override
    public void onDestroy() {
        System.out.println(tag +"Service"+ "onDestroy");
        stopListen();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        shouldRestartSocketListen = true;
        startListenForUDPBroadcast();
        System.out.println(tag + "Service started");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}