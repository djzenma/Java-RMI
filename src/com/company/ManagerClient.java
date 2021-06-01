package com.company;

import com.company.rmi.RecordOps;
import com.company.rmi.RecordOpsClass;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ManagerClient {
    private String managerID;

    public ManagerClient(int serverRegistryPort) {
        try {
            Registry registry = LocateRegistry.getRegistry(serverRegistryPort);

            RecordOps ops = (RecordOps) registry.lookup("ops");
            ops.getRecordCounts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
