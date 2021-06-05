package com.company;

import com.company.config.Config;
import com.company.types.Location;

import java.rmi.RemoteException;

public class Servers {

    public static void main(String[] args) {
        try {
            RecordIDsServer recordIDsServer = new RecordIDsServer(Config.RecordIDsServerPort);

            Server mtlServer = new Server(Location.MTL,
                    Config.MTLServerRegistryPort,
                    Config.MTLServerRecordsCountPort);

            Server lvlServer = new Server(Location.LVL,
                    Config.LVLServerRegistryPort,
                    Config.LVLServerRecordsCountPort);

            Server ddoServer = new Server(Location.DDO,
                    Config.DDOServerRegistryPort,
                    Config.DDOServerRecordsCountPort);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
