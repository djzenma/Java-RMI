package com.company;

import com.company.config.Config;
import com.company.types.Location;
import com.company.types.Status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

public class Clients {
    private static Scanner sc;
    private static ManagerClient mtlManager;
    private static ManagerClient lvlManager;
    private static ManagerClient ddoManager;
    private static ManagerClient client;

    public static void main(String[] args) {
        sc = new Scanner(System.in);
        mtlManager = new ManagerClient(Config.MTLServerRegistryPort);
        lvlManager = new ManagerClient(Config.LVLServerRegistryPort);
        ddoManager = new ManagerClient(Config.DDOServerRegistryPort);
        run();
    }

    private static void run() {
        String managerID = "";

        boolean isValidID = false;
        while(!isValidID) {
            System.out.println("Please enter your manager ID:");
            managerID = sc.nextLine();
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
                System.out.println("Unknow region! Please enter one of the supported regions!");
        }

        client.setManagerID(managerID);

        while(true) {
            System.out.println("\nEnter the number corresponding to one of the following operations:");
            System.out.println("1. createTRecord");
            System.out.println("2. createSRecord");
            System.out.println("3. getRecordCounts");
            System.out.println("4. editRecord");
            System.out.println("5. quit");

            int op = Integer.parseInt(sc.nextLine());
            switch (op) {
                case 1:
                    promptTRecord();
                    break;
                case 2:
                    promptSRecord();
                    break;
                case 3:
                    promptGetRecordCounts();
                    break;
                case 4:
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Unknown operation! Please enter one of the supported operations!");
            }
        }
    }

    private static void promptGetRecordCounts() {
        try {
            for (Map.Entry<Location, Integer> s : client.getRecordCounts().entrySet()) {
                System.out.println(s.getKey().name() + ": " + s.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("We couldn't get the count. Please try again!\n\n");
        }
    }

    private static void promptTRecord() {
        System.out.print("Enter FirstName: ");
        String firstName = sc.nextLine();

        System.out.print("Enter LastName: ");
        String lastName = sc.nextLine();

        System.out.print("Enter Address: ");
        String address = sc.nextLine();

        System.out.print("Enter Phone Number: ");
        String phone = sc.nextLine();

        System.out.print("Enter Specialization: ");
        String spec = sc.nextLine();

        try {
            boolean isCreated = client.createTRecord(firstName, lastName, address, phone, spec, client.getLocation());
            if(isCreated)
                System.out.println("Operation done successfully\n\n");
            else
                System.out.println("Operation failed\n\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Creation Failed!\n\n");
        }
    }


    private static void promptSRecord() {
        boolean isValid;

        System.out.print("Enter FirstName: ");
        String firstName = sc.nextLine();

        System.out.print("Enter LastName: ");
        String lastName = sc.nextLine();

        System.out.print("Enter the number of courses registered: ");
        int num = Integer.parseInt(sc.nextLine());
        ArrayList<String> courses = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            System.out.print("Enter the course number " + String.valueOf(i+1) + ": ");
            courses.add(sc.nextLine());
        }

        Status status = null;
        isValid = false;
        while (!isValid) {
            System.out.println("Enter the number of the corresponding Status: ");
            System.out.println("1 = Active");
            System.out.println("2 = Inactive");
            int stat = Integer.parseInt(sc.nextLine());
            if (stat == 1 || stat == 2) {
                isValid = true;
                status = Status.values()[stat-1];
            }
            else {
                System.out.println("Please enter a valid number corresponding to the status!");
            }
        }

        isValid = false;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        while(!isValid) {
            System.out.print("Enter status date in the format dd-MM-yyyy: ");
            String dateStr = sc.nextLine();
            try {
                date = dateFormat.parse(dateStr);
                isValid = true;
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("Failed to parse the date");
            }
        }

        try {
            boolean isCreated = client.createSRecord(firstName, lastName, courses, status, date);
            if(isCreated)
                System.out.println("Operation done successfully\n\n");
            else
                System.out.println("Operation failed\n\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Creation Failed!\n\n");
        }
    }
}
