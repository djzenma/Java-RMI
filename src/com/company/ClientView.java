package com.company;

import com.company.record.Record;
import com.company.record.StudentRecord;
import com.company.record.TeacherRecord;
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

    private String promptRecordID() {
        while (true) {
            String recordID = sc.nextLine();

            if ((recordID.startsWith("TR") || recordID.startsWith("SR"))
                    && recordID.length() == 7 && recordID.substring(2).matches("^[0-9]{5}$")) {
                return recordID;
            } else {
                System.out.println("Please enter a valid recordID! The format is SR or TR followed by 5 digits.");
            }
        }
    }

    public ArrayList<String> promptCourses() {
        System.out.print("Enter the number of courses registered: ");
        int num = Integer.parseInt(sc.nextLine());
        ArrayList<String> courses = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            System.out.print("Enter the course number " + String.valueOf(i + 1) + ": ");
            courses.add(sc.nextLine());
        }
        return courses;
    }

    public Status promptStatus() {
        Status status = null;
        boolean isValid = false;
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
        return status;
    }

    public Date promptDate() {
        boolean isValid = false;
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
        return date;
    }

    public HashMap<String, Object> promptSRecord() {
        HashMap<String, Object> args = new HashMap<>();

        System.out.print("Enter FirstName: ");
        args.put("firstName", sc.nextLine());

        System.out.print("Enter LastName: ");
        args.put("lastName", sc.nextLine());

        args.put("courses", promptCourses());

        args.put("status", promptStatus());

        args.put("date", promptDate());

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

    public void operationStatus(boolean isSuccess) {
        if (isSuccess)
            System.out.println("Operation done successfully\n");
        else
            System.out.println("Operation failed\n");
    }

    public String getManagerID() {
        System.out.println("Please enter your manager ID:");
        return sc.nextLine();
    }

    public HashMap<String, Object> promptEditRecord(String managerID) {
        HashMap<String, Object> args = new HashMap<>();

        System.out.print("Enter the recordID of the record you want to edit: ");
        args.put("recordID", sc.nextLine());

        TeacherRecord templateTR = new TeacherRecord(null, null, null, null, null, null, null);
        StudentRecord templateSR = new StudentRecord(null, null, null, null, null, null);
        Record templateR;
        if(((String) args.get("recordID")).startsWith("TR"))
            templateR = templateTR;
        else
            templateR = templateSR;

        System.out.println("You can edit one of the following fields:");
        int i = 0;
        for (String field: templateR.getFieldNames()) {
            System.out.println(i + ". " + field);
            i++;
        }

        System.out.print("Enter the field name to be edited: ");
        args.put("fieldName", sc.nextLine());

        // Handle Teacher fields
        if(((String) args.get("recordID")).startsWith("TR")) {
            if(!templateTR.getFieldNames().contains((String) args.get("fieldName"))) {
                System.out.println("The field name you entered is not a valid Teacher Record field!");
                return null;
            }
            else {
                String fieldName = (String) args.get("fieldName");

                System.out.print("Enter the new value: ");
                if(fieldName.equals("recordID")) {
                    args.put("newValue", promptRecordID());
                }
                else
                    args.put("newValue", sc.nextLine());
            }
        }

        // Handle Student fields
        else if(((String) args.get("recordID")).startsWith("SR")) {
            if(!templateSR.getFieldNames().contains((String) args.get("fieldName"))) {
                System.out.println("The field name you entered is not a valid Student Record field!");
                return null;
            }
            else {
                System.out.print("Enter the new value: ");

                String fieldName = (String) args.get("fieldName");
                switch (fieldName) {
                    case "recordID":
                        args.put("newValue", promptRecordID());
                        break;
                    case "courses":
                        args.put("newValue", promptCourses());
                        break;
                    case "status":
                        args.put("newValue", promptStatus());
                        break;
                    case "statusDate":
                        args.put("newValue", promptDate());
                        break;
                    default:
                        args.put("newValue", sc.nextLine());
                        break;
                }
            }
        }

        return args;
    }


    public void invalidID() {
        System.out.println("Your recordID is invalid! Make sure it is composed of 'MTL' or 'LVL' or 'DDO' followed by 4 digits.");
    }
}
