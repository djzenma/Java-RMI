package com.company.rmi;

import com.company.Server;
import com.company.types.Location;
import com.company.types.Status;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class RecordOpsClass extends UnicastRemoteObject implements CenterServer {
    Server server;

    public RecordOpsClass(Server server) throws RemoteException {
        this.server = server;
    }

    @Override
    public boolean createTRecord(String firstName,
                                 String lastName,
                                 String address,
                                 String phone,
                                 String specialization,
                                 Location location) throws RemoteException {
        System.out.println("Create");
        return false;
    }

    @Override
    public boolean createSRecord(String firstName,
                                 String lastName,
                                 ArrayList<String> courseRegistered,
                                 Status status,
                                 Date statusDate) throws RemoteException {
        System.out.println("Create");
        return false;
    }

    @Override
    public HashMap<Location, Integer> getRecordCounts() throws RemoteException {
        System.out.println("Count");
        return null;
    }

    @Override
    public boolean editRecord(String recordID, String fieldName, Object newValue) throws RemoteException {
        System.out.println("Edit");
        return false;
    }
}
