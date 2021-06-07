package com.company.threads;

import com.company.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class RecordsCountThread implements Runnable{
    private final int recordsCountPort;
    private final Server server;
    private static volatile boolean exit;
    private DatagramSocket socket;

    public RecordsCountThread(int recordsCountPort, Server server) {
        this.recordsCountPort = recordsCountPort;
        this.server = server;
        exit = false;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(recordsCountPort);
            byte[] buf = new byte[1000];
            while (!exit) {
                DatagramPacket req = new DatagramPacket(buf, buf.length);
                socket.receive(req);

                byte[] msg = ByteBuffer.allocate(4).putInt(server.getCenterTotalRecords()).array();
                DatagramPacket reply = new DatagramPacket(msg, msg.length, req.getAddress(), req.getPort());
                socket.send(reply);
            }
            if(!socket.isClosed())
                socket.close();
        } catch (IOException e) {
            if(exit)
                return;
            e.printStackTrace();
        }
    }

    public void stop() {
        exit = true;
        if(!socket.isClosed())
            socket.close();
    }
}
