package com.company;

import com.company.config.Config;
import com.company.types.Location;

import java.rmi.RemoteException;

public class Servers {

    public static void main(String[] args) {
        try {
            CenterServer mtlServer = new CenterServer(Location.MTL,
                    Config.MTLServerRegistryPort,
                    Config.MTLServerRecordsCountPort);
            CenterServer lvlServer = new CenterServer(Location.LVL,
                    Config.LVLServerRegistryPort,
                    Config.LVLServerRecordsCountPort);
            CenterServer ddoServer = new CenterServer(Location.DDO,
                    Config.DDOServerRegistryPort,
                    Config.DDOServerRecordsCountPort);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
