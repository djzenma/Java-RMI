package com.company;

import com.company.types.Location;

public class Servers {

    public static void main(String[] args) {
        CenterServer mtlServer = new CenterServer(7777, Location.MTL);
        //CenterServer lvlServer = new CenterServer(8888, Location.LVL);
        //CenterServer ddoServer = new CenterServer(9999, Location.DDO);
    }
}
