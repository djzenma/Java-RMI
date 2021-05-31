package com.company;

import com.company.record.Record;
import com.company.rmi.RecordOpsClass;
import com.company.types.Location;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

public class CenterServer {
    private HashMap<Character, ArrayList<Record>> records;

    public CenterServer(int port, Location location) {
        try {
            RecordOpsClass recordOps = new RecordOpsClass();

            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("ops", recordOps);

            System.out.println(location + " Server running on port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
