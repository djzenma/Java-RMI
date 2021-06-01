package com.company;

import com.company.config.Config;

public class Clients {

    public static void main(String[] args) {
        ManagerClient mtlManager = new ManagerClient(Config.MTLServerRegistryPort);
        //ManagerClient lvlManager = new ManagerClient(8888);
        //ManagerClient ddoManager = new ManagerClient(9999);
    }
}
