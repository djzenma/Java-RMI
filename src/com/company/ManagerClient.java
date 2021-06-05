package com.company;

import com.company.rmi.CenterServer;
import com.company.types.Location;
import com.company.types.Status;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManagerClient {
    private String managerID;

    public ManagerClient(int serverRegistryPort) {
        try {
            Registry registry = LocateRegistry.getRegistry(serverRegistryPort);

            CenterServer ops = (CenterServer) registry.lookup("ops");
            ops.createTRecord("Mohamed", "Shaalan", "AUC", "1234", "CE", Location.MTL);
            ops.createTRecord("Nouri", "Sakr", "AUC", "1234", "CE", Location.MTL);
            ops.createSRecord("Mazen", "Eid", new ArrayList<>(List.of("DSD", "Comp")), Status.ACTIVE, new Date(new Date().getTime()));
            ops.getRecordCounts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
