package com.company;

import com.company.config.Config;
import com.company.record.Record;
import com.company.record.StudentRecord;
import com.company.record.TeacherRecord;
import com.company.rmi.RecordOps;
import com.company.threads.RecordsCountThread;
import com.company.types.Location;
import com.company.types.Status;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CenterServer extends UnicastRemoteObject implements RecordOps {
    private HashMap<Character, ArrayList<Record>> records;
    private int totalTRecords;
    private int totalSRecords;
    private Location location;


    public CenterServer(Location location, int registryPort, int recordsCountPort) throws RemoteException {
        super();
        totalTRecords = 0;
        totalSRecords = 0;
        this.location = location;

        try {
            System.out.println(location + " server running registry on port " + registryPort +
                    " and records count on port " + recordsCountPort);

            // receive records count requests thread
            Thread recordsCountThread = new Thread(new RecordsCountThread(recordsCountPort, totalTRecords + totalSRecords));
            recordsCountThread.start();

            // RMI operations
            Registry registry = LocateRegistry.createRegistry(registryPort);
            registry.bind("ops", this);
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean createTRecord(String firstName,
                                 String lastName,
                                 String address,
                                 String phone,
                                 String specialization,
                                 Location location) throws RemoteException {
        TeacherRecord teacherRecord = new TeacherRecord(
                "TR" + String.format("%05d", totalTRecords++),
                firstName,
                lastName,
                address,
                phone,
                specialization,
                location);

        this.records.get(lastName.charAt(0)).add(teacherRecord);

        System.out.println(firstName + " " + lastName + " TR Created");
        // TODO:: store this op in log
        return true;
    }

    @Override
    public boolean createSRecord(String firstName,
                                 String lastName,
                                 ArrayList<String> courseRegistered,
                                 Status status,
                                 Date statusDate) throws RemoteException {
        StudentRecord studentRecord = new StudentRecord(
                "SR" + String.format("%05d", totalSRecords++),
                firstName,
                lastName,
                courseRegistered,
                status,
                statusDate);

        this.records.get(lastName.charAt(0)).add(studentRecord);

        System.out.println(firstName + " " + lastName + " SR Created");
        // TODO:: store this op in log
        return true;
    }

    @Override
    public HashMap<Location, Integer> getRecordCounts() throws RemoteException {
        HashMap<Location, Integer> recordCounts = new HashMap<>();
        try {
            int count;
            count = sendAndReceiveUDP(Config.MTLServerRecordsCountPort);
            recordCounts.put(Location.MTL, count);
            System.out.println("MTL Count = " + count);

            count = sendAndReceiveUDP(Config.LVLServerRecordsCountPort);
            recordCounts.put(Location.LVL, count);
            System.out.println("LVL Count = " + count);

            count = sendAndReceiveUDP(Config.DDOServerRecordsCountPort);
            recordCounts.put(Location.DDO, count);
            System.out.println("DDO Count = " + count);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordCounts;
    }

    @Override
    public boolean editRecord(String recordID, String fieldName, String newValue) throws RemoteException {
        System.out.println("Edit");
        return false;
    }


    private int sendAndReceiveUDP(int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        // send request
        InetAddress host = InetAddress.getByName("localhost");
        String msg = "count";
        DatagramPacket req = new DatagramPacket(msg.getBytes(), msg.length(), host, port);
        socket.send(req);

        // get reply
        byte[] buf = new byte[1000];
        DatagramPacket reply = new DatagramPacket(buf, buf.length);
        socket.receive(reply);
        return ByteBuffer.wrap(reply.getData()).getInt();   // convert byte array to int
    }
}
