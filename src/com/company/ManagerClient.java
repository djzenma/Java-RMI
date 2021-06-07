package com.company;

import com.company.record.StudentRecord;
import com.company.record.TeacherRecord;
import com.company.rmi.CenterServer;
import com.company.types.Location;
import com.company.types.Status;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ManagerClient {
    private String managerID;
    private CenterServer ops;
    private Location location;
    private FileWriter writer;

    public ManagerClient(int serverRegistryPort) {
        try {
            Registry registry = LocateRegistry.getRegistry(serverRegistryPort);

            ops = (CenterServer) registry.lookup("ops");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean createTRecord(String firstName,
                                 String lastName,
                                 String address,
                                 String phone,
                                 String specialization,
                                 Location location) throws RemoteException {
        TeacherRecord templateTR = new TeacherRecord(
                "",
                firstName,
                lastName,
                address,
                phone,
                specialization,
                location);
        try {
            writeEvent("ISSUED CREATE TEACHER RECORD: " + templateTR.print());
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean isCreated = ops.createTRecord(firstName, lastName, address, phone, specialization, location);

        try {
            if (isCreated)
                writeEvent("SUCCESS: TEACHER RECORD CREATED: " + templateTR.print());
            else
                writeEvent("FAILURE: TEACHER RECORD NOT CREATED: " + templateTR.print());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCreated;
    }

    public boolean createSRecord(String firstName,
                                 String lastName,
                                 ArrayList<String> courseRegistered,
                                 Status status,
                                 Date statusDate) throws RemoteException {
        StudentRecord templateSR = new StudentRecord(
                "",
                firstName,
                lastName,
                courseRegistered,
                status,
                statusDate);

        try {
            writeEvent("ISSUED CREATE STUDENT RECORD: " + templateSR.print());
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean isCreated = ops.createSRecord(firstName, lastName, courseRegistered, status, statusDate);

        try {
            if (isCreated)
                writeEvent("SUCCESS: STUDENT RECORD CREATED: " + templateSR.print());
            else
                writeEvent("FAILURE: STUDENT RECORD NOT CREATED: " + templateSR.print());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCreated;
    }

    ;

    public HashMap<Location, Integer> getRecordCounts() throws RemoteException {
        try {
            writeEvent("ISSUED RECORDS COUNT");
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<Location, Integer> count = ops.getRecordCounts();

        try {
            StringBuilder event = new StringBuilder();
            for (Location loc : Location.values()) {
                event.append(loc.name()).append(": ").append(count.get(loc)).append(", ");
            }
            writeEvent("SUCCESS: RECORDS COUNT: " + event);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    ;

    public boolean editRecord(String recordID, String fieldName, Object newValue) throws RemoteException {
        try {
            writeEvent("ISSUED EDIT RECORD: update the field \"" + fieldName + "\" to " + newValue.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean isEdited = ops.editRecord(recordID, fieldName, newValue);

        try {
            if (isEdited)
                writeEvent("SUCCESS: RECORD EDITED: updated the field \"" + fieldName + "\" of the record with recordID \"" + recordID + "\" to " + newValue.toString());
            else
                writeEvent("FAILURE: RECORD NOT EDITED: did not update the field \"" + fieldName + "\" of the record with recordID \"" + recordID + "\" to " + newValue.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isEdited;
    }

    ;

    public Location getLocation() {
        if (managerID.startsWith(Location.MTL.name()))
            return Location.MTL;
        if (managerID.startsWith(Location.LVL.name()))
            return Location.LVL;
        if (managerID.startsWith(Location.DDO.name()))
            return Location.DDO;
        return null;
    }

    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }

    private synchronized void writeEvent(String event) throws IOException {
        writer = new FileWriter("log/client/" + managerID + ".txt", true);
        writer.write(new Timestamp(new Date().getTime()).toString() + " " + event + "\n");
        writer.close();
    }
}
