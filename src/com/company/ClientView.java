package com.company;

import com.company.types.Location;
import com.company.types.Status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClientView {
    private ManagerClient client;
    private Scanner sc;

    public ClientView() {
        sc = new Scanner(System.in);
    }

    public void promptGetRecordCounts(HashMap<Location, Integer> counts) {
        try {
            for (Map.Entry<Location, Integer> s : counts.entrySet()) {
                System.out.println(s.getKey().name() + ": " + s.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("We couldn't get the count. Please try again!\n\n");
        }
    }

    public HashMap<String, String> promptTRecord() {
        HashMap<String, String> args = new HashMap<>();

        System.out.print("Enter FirstName: ");
        args.put("firstName", sc.nextLine());

        System.out.print("Enter LastName: ");
        args.put("lastName", sc.nextLine());

        System.out.print("Enter Address: ");
        args.put("address", sc.nextLine());

        System.out.print("Enter Phone Number: ");
        args.put("phone", sc.nextLine());

        System.out.print("Enter Specialization: ");
        args.put("specialization", sc.nextLine());

        return args;
    }


    public HashMap<String, Object> promptSRecord() {
        HashMap<String, Object> args = new HashMap<>();

        boolean isValid;

        System.out.print("Enter FirstName: ");
        args.put("firstName", sc.nextLine());

        System.out.print("Enter LastName: ");
        args.put("lastName", sc.nextLine());

        System.out.print("Enter the number of courses registered: ");
        int num = Integer.parseInt(sc.nextLine());
        ArrayList<String> courses = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            System.out.print("Enter the course number " + String.valueOf(i + 1) + ": ");
            courses.add(sc.nextLine());
        }
        args.put("courses", courses);

        Status status = null;
        isValid = false;
        while (!isValid) {
            System.out.println("Enter the number of the corresponding Status: ");
            System.out.println("1 = Active");
            System.out.println("2 = Inactive");
            int stat = Integer.parseInt(sc.nextLine());
            if (stat == 1 || stat == 2) {
                isValid = true;
                status = Status.values()[stat - 1];
            } else {
                System.out.println("Please enter a valid number corresponding to the status!");
            }
        }
        args.put("status", status);

        isValid = false;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        while (!isValid) {
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
        args.put("date", date);

        return args;
    }

    public int getOperation() {
        System.out.println("\nEnter the number corresponding to one of the following operations:");
        System.out.println("1. createTRecord");
        System.out.println("2. createSRecord");
        System.out.println("3. getRecordCounts");
        System.out.println("4. editRecord");
        System.out.println("5. quit");

        return Integer.parseInt(sc.nextLine());
    }

    public void operationStatus(boolean isCreated) {
        if (isCreated)
            System.out.println("Operation done successfully\n\n");
        else
            System.out.println("Operation failed\n\n");
    }

    public String getManagerID() {
        System.out.println("Please enter your manager ID:");
        return sc.nextLine();
    }
}
