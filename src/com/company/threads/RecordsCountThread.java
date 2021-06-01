package com.company.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class RecordsCountThread implements Runnable{
    private int recordsCountPort;
    private int totalRecords;

    public RecordsCountThread(int recordsCountPort, int totalRecords) {
        this.recordsCountPort = recordsCountPort;
        this.totalRecords = totalRecords;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(recordsCountPort);
            byte[] buf = new byte[1000];
            while (true) {
                DatagramPacket req = new DatagramPacket(buf, buf.length);
                socket.receive(req);

                byte[] msg = ByteBuffer.allocate(4).putInt(totalRecords).array();
                DatagramPacket reply = new DatagramPacket(msg, msg.length, req.getAddress(), req.getPort());
                socket.send(reply);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
