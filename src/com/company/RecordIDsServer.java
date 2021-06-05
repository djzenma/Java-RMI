package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class RecordIDsServer {
    private HashMap<String, Integer> recordIDs;

    public RecordIDsServer(int port) {
        recordIDs = new HashMap<>();
        recordIDs.put("TR", 0);
        recordIDs.put("SR", 0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Record IDs server running on port " + port);
                while(true) {
                    try {
                        DatagramSocket socket = new DatagramSocket(port);
                        byte[] buf = new byte[1000];
                        while (true) {
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
