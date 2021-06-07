package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class RecordIDsServer {
    private HashMap<String, Integer> recordIDs;
    private Thread recordIDsThread;
    private static volatile boolean exit;
    private DatagramSocket socket;

    public RecordIDsServer(int port) {
        recordIDs = new HashMap<>();
        recordIDs.put("TR", 0);
        recordIDs.put("SR", 0);

        exit = false;

        recordIDsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Record IDs server running on port " + port);
                try {
                    socket = new DatagramSocket(port);
                    byte[] buf = new byte[1000];
                    while (!exit) {
                        DatagramPacket req = new DatagramPacket(buf, buf.length);
                        socket.receive(req);



                        String recordType = new String(req.getData(), 0, req.getLength());
                        String msg = "-1";
                        if (recordType.equals("TR") || recordType.equals("SR")) {
                            synchronized (this) {
                                int lastUsed = recordIDs.get(recordType);
                                int available = lastUsed + 1;
                                recordIDs.put(recordType, available);
                                msg = String.format("%05d", available);
                            }
                        }

                        byte[] sendData = msg.getBytes();
                        DatagramPacket reply = new DatagramPacket(sendData, sendData.length, req.getAddress(), req.getPort());
                        socket.send(reply);
                    }
                    System.out.println("exiting");
                    if (!socket.isClosed())
                        socket.close();
                } catch (IOException e) {
                    if(exit)
                        return;
                    e.printStackTrace();
                }
            }
        });

        recordIDsThread.start();
    }


    public void destroy() {
        exit = true;
        recordIDsThread.interrupt();
        if (!socket.isClosed())
            socket.close();
    }


}
