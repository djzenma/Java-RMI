package com.company;

import com.company.config.Config;
import com.company.record.Record;
import com.company.record.StudentRecord;
import com.company.record.TeacherRecord;
import com.company.rmi.CenterServer;
import com.company.threads.RecordsCountThread;
import com.company.types.Location;
import com.company.types.Status;

import java.io.FileWriter;
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
import java.sql.Timestamp;
import java.util.*;

public class Server extends UnicastRemoteObject implements CenterServer {
    private HashMap<Character, ArrayList<Record>> records;
    private int totalTRecords;
    private int totalSRecords;
    private Location location;
    private FileWriter writer;


    public Server(Location location, int registryPort, int recordsCountPort) throws RemoteException {
        super();
        records = new HashMap<>();
        totalTRecords = 0;
        totalSRecords = 0;
        this.location = location;

        try {
            // receive records count requests thread
            Thread recordsCountThread = new Thread(new RecordsCountThread(recordsCountPort, this));
            recordsCountThread.start();

            // RMI operations
            Registry registry = LocateRegistry.createRegistry(registryPort);
            registry.bind("ops", this);

            System.out.println(location + " server running registry on port " + registryPort +
                    " and records count on port " + recordsCountPort);
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
                "",
                firstName,
                lastName,
                address,
                phone,
                specialization,
                location);

        return createRecord("TR", teacherRecord);
    }

    @Override
    public boolean createSRecord(String firstName,
                                 String lastName,
                                 ArrayList<String> courseRegistered,
                                 Status status,
                                 Date statusDate) throws RemoteException {
        StudentRecord studentRecord = new StudentRecord(
                "",
                firstName,
                lastName,
                courseRegistered,
                status,
                statusDate);

        return createRecord("SR", studentRecord);
    }


    @Override
    public HashMap<Location, Integer> getRecordCounts() throws RemoteException {
        HashMap<Location, Integer> recordCounts = new HashMap<>();
        try {
            recordCounts.put(Location.MTL, getCountFromServer(Config.MTLServerRecordsCountPort));
            recordCounts.put(Location.LVL, getCountFromServer(Config.LVLServerRecordsCountPort));
            recordCounts.put(Location.DDO, getCountFromServer(Config.DDOServerRecordsCountPort));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            writeEvent("RECORDS COUNT REQUESTED: " +
                    Location.MTL.name() + " has " + recordCounts.get(Location.MTL) + ", " +
                    Location.LVL.name() + " has " + recordCounts.get(Location.LVL) + ", " +
                    Location.DDO.name() + " has " + recordCounts.get(Location.DDO));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to log the records count!");
        }
        return recordCounts;
    }

    @Override
    public synchronized boolean editRecord(String recordID, String fieldName, Object newValue) throws RemoteException {
        // TODO:: rearrange the hashmap if the lastName changed
        for(ArrayList<Record> recordList : records.values()) {
            for (Record record : recordList) {
                if (record.getRecordID().equals(recordID)) {
                    record.set(fieldName, newValue);
                    try {
                        writeEvent("RECORD EDITED: Changed record " + recordID + "'s \"" + fieldName + "\" to be " + newValue.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Failed to log the record edit!");
                    }
                    return true;
                }
            }
        }
        try {
            writeEvent("RECORD NOT EDITED: Could not edit the record " + recordID + "'s field \"" + fieldName + "\" to be \"" + newValue.toString() +
                    "\" of the record with ID " + recordID);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to log the record edit!");
        }
        return false;
    }

    /**
     * Gets an available recordID and adds the record to the records HashMap
     * @param prefix TR or SR
     * @param record the record to create
     * @return True if success, false otherwise
     */
    private boolean createRecord(String prefix, Record record) {
        try {
            String reservedID = reserveAvailableRecordID(prefix.equals("TR"));
            if(reservedID.equals("-1"))
                return false;
            record.set("recordID", prefix + reservedID);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        synchronized (this) {
            String lastName = record.getLastName();
            if (!this.records.containsKey(lastName.charAt(0)))
                this.records.put(lastName.charAt(0), new ArrayList<>(List.of(record)));
            else
                this.records.get(lastName.charAt(0)).add(record);
            if(prefix.equals("TR"))
                totalTRecords++;
            else
                totalSRecords++;
        }

        try {
            writeEvent("RECORD CREATED: " + record.print());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write the record creation event in the log!");
        }
        return true;
    }

    /**
     * Writes event to the server's corresponding log file depending on its location
     * @param event the event to be logged
     * @throws IOException
     */
    private synchronized void writeEvent(String event) throws IOException {
        writer = new FileWriter("log/server/" + location.name() + ".txt", true);
        writer.write(new Timestamp(new Date().getTime()).toString() + " " + event + "\n");
        System.out.println(event);
        writer.close();
    }

    /**
     * gets the number of records in the passed server
     * @param serverPort the server port from which we want to get its records number
     * @return the number of records that the passed server has
     * @throws IOException
     */
    private int getCountFromServer(int serverPort) throws IOException {
        int count;
        DatagramPacket reply = sendAndReceiveUDP(serverPort, "count");
        count = ByteBuffer.wrap(reply.getData()).getInt();
        return count;
    }


    /**
     * reserves and gets an available RecordID from the Records IDs server
     * @return the reserved RecordID string without the TR or SR
     * @throws IOException
     */
    private String reserveAvailableRecordID(boolean isTeacher) throws IOException {
        String msg = "SR";
        if(isTeacher)
            msg = "TR";

        DatagramPacket reply = sendAndReceiveUDP(Config.RecordIDsServerPort, msg);
        return new String(reply.getData(), 0, reply.getLength());
    }

    private DatagramPacket sendAndReceiveUDP(int port, String msg) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        // send request
        InetAddress host = InetAddress.getByName("localhost");
        DatagramPacket req = new DatagramPacket(msg.getBytes(), msg.length(), host, port);
        socket.send(req);

        // get reply
        byte[] buf = new byte[1000];
        DatagramPacket reply = new DatagramPacket(buf, buf.length);
        socket.receive(reply);

        return reply;
    }

    public int getCenterTotalRecords() {
        return totalTRecords + totalSRecords;
    }


}
