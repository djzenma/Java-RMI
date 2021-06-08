package com.company;

import com.company.config.Config;
import com.company.types.Location;
import com.company.types.Status;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Servers {

    public static void main(String[] args) {
        try {
            RecordIDsServer recordIDsServer = new RecordIDsServer(Config.RecordIDsServerPort);

            Server mtlServer = new Server(Location.MTL,
                    Config.MTLServerRegistryPort,
                    Config.MTLServerRecordsCountPort);
            mtlServer.createSRecord(
                    "Mazen1",
                    "Eid1",
                    new ArrayList<String>(Arrays.asList("DSD", "CE")),
                    Status.ACTIVE,
                    new Date(new Date().getTime()));
            mtlServer.createTRecord(
                    "Mohamed",
                    "Tolba",
                    "Concordia",
                    "12354",
                    "DSD",
                    Location.MTL);

            Server lvlServer = new Server(Location.LVL,
                    Config.LVLServerRegistryPort,
                    Config.LVLServerRecordsCountPort);
            lvlServer.createSRecord(
                    "Reza",
                    "Mohamed",
                    new ArrayList<String>(Arrays.asList("DSD")),
                    Status.INACTIVE,
                    new Date(new Date().getTime()));
            lvlServer.createTRecord(
                    "Mohamed2",
                    "Tolba2",
                    "Concordia2",
                    "12354",
                    "DSD2",
                    Location.LVL);

            Server ddoServer = new Server(Location.DDO,
                    Config.DDOServerRegistryPort,
                    Config.DDOServerRecordsCountPort);
            ddoServer.createSRecord(
                    "Mazen2",
                    "Eid2",
                    new ArrayList<String>(Arrays.asList("DSD2", "CE2")),
                    Status.ACTIVE,
                    new Date(new Date().getTime()));
            ddoServer.createTRecord(
                    "Mohamed3",
                    "Tolba3",
                    "Concordia3",
                    "12354",
                    "DSD3",
                    Location.DDO);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
