package com.company.threads;

import com.company.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class RecordsCountThread implements Runnable{
    private final int recordsCountPort;
    private final Server server;

    public RecordsCountThread(int recordsCountPort, Server server) {
        this.recordsCountPort = recordsCountPort;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(recordsCountPort);
            byte[] buf = new byte[1000];
            while (true) {
                DatagramPacket req = new DatagramPacket(buf, buf.length);
                socket.receive(req);

                byte[] msg = ByteBuffer.allocate(4).putInt(server.getCenterTotalRecords()).array();
                DatagramPacket reply = new DatagramPacket(msg, msg.length, req.getAddress(), req.getPort());
                socket.send(reply);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
