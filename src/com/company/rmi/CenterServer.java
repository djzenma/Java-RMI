package com.company.rmi;

import com.company.record.Record;
import com.company.types.Location;
import com.company.types.Status;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public interface CenterServer extends Remote {

    public boolean createTRecord( String firstName,
                                  String lastName,
                                  String address,
                                  String phone,
                                  String specialization,
                                  Location location) throws RemoteException;

    public boolean createSRecord(String firstName,
                                 String lastName,
                                 ArrayList<String> courseRegistered,
                                 Status status,
                                 Date statusDate) throws RemoteException;

    public HashMap<Location, Integer> getRecordCounts() throws RemoteException;

    public boolean editRecord(String recordID, String fieldName, Object newValue) throws RemoteException;

    public HashMap<Character, ArrayList<Record>> getRecords() throws RemoteException;
}
