package com.company;

import com.company.rmi.CenterServer;
import com.company.types.Location;
import com.company.types.Status;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ManagerClient {
    private String managerID;
    private CenterServer ops;
    private Location location;

    public ManagerClient(int serverRegistryPort) {
        try {
            Registry registry = LocateRegistry.getRegistry(serverRegistryPort);

            ops = (CenterServer) registry.lookup("ops");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean createTRecord(String firstName,
                                 String lastName,
                                 String address,
                                 String phone,
                                 String specialization,
                                 Location location) throws RemoteException {
        return ops.createTRecord(firstName, lastName, address, phone, specialization, location);
    }

    public boolean createSRecord(String firstName,
                                 String lastName,
                                 ArrayList<String> courseRegistered,
                                 Status status,
                                 Date statusDate) throws RemoteException {
        return ops.createSRecord(firstName, lastName, courseRegistered, status, statusDate);
    };

    public HashMap<Location, Integer> getRecordCounts() throws RemoteException {
        return ops.getRecordCounts();
    };

    public boolean editRecord(String recordID, String fieldName, Object newValue) throws RemoteException {
        return ops.editRecord(recordID, fieldName, newValue);
    };

    public Location getLocation() {
        if(managerID.startsWith(Location.MTL.name()))
            return Location.MTL;
        if(managerID.startsWith(Location.LVL.name()))
            return Location.LVL;
        if(managerID.startsWith(Location.DDO.name()))
            return Location.DDO;
        return null;
    }

    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }
}
