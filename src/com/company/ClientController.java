package com.company;

import com.company.config.Config;
import com.company.types.Location;
import com.company.types.Status;

import java.rmi.RemoteException;
import java.util.*;

public class ClientController {
    private static ManagerClient mtlManager;
    private static ManagerClient lvlManager;
    private static ManagerClient ddoManager;
    private static ManagerClient client;
    private static ClientView view;

    public static void main(String[] args) {
        mtlManager = new ManagerClient(Config.MTLServerRegistryPort);
        lvlManager = new ManagerClient(Config.LVLServerRegistryPort);
        ddoManager = new ManagerClient(Config.DDOServerRegistryPort);

        view = new ClientView();
        run();
    }

    private static void run() {
        String managerID = "";

        boolean isValidID = false;
        while (!isValidID) {
            managerID = view.getManagerID();
            for (Location val : Location.values()) {
                if (managerID.startsWith(val.name())) {
                    isValidID = true;
                    break;
                }
            }
        }

        switch (managerID.substring(0, 3)) {
            case "MTL":
                client = mtlManager;
                break;
            case "LVL":
                client = lvlManager;
                break;
            case "DDO":
                client = ddoManager;
                break;
            default:
                System.out.println("Unknown region! Please enter one of the supported regions!");
        }
        client.setManagerID(managerID);

        while (true) {
            int op = view.getOperation();
            HashMap<String, String> args;
            switch (op) {
                case 1:
                    args = view.promptTRecord();
                    try {
                        boolean isCreated = client.createTRecord(
                                args.get("firstName"),
                                args.get("lastName"),
                                args.get("address"),
                                args.get("phone"),
                                args.get("specialization"),
                                client.getLocation());
                        view.operationStatus(isCreated);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Creation Failed!\n\n");
                    }
                    break;
                case 2:
                    HashMap<String, Object> SRargs = view.promptSRecord();
                    try {
                        boolean isCreated = client.createSRecord(
                                (String) SRargs.get("firstName"),
                                (String) SRargs.get("lastName"),
                                (ArrayList<String>) SRargs.get("courses"),
                                (Status) SRargs.get("status"),
                                (Date) SRargs.get("date"));
                        view.operationStatus(isCreated);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Creation Failed!\n\n");
                    }
                    break;
                case 3:
                    try {
                        view.promptGetRecordCounts(client.getRecordCounts());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        System.out.println("Records count operation failed");
                    }
                    break;
                case 4:
                    break;
                case 5:
                    System.out.println("Bye Bye!");
                    return;
                default:
                    System.out.println("Unknown operation! Please enter one of the supported operations!");
            }
        }
    }
}
