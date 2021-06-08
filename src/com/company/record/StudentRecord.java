package com.company.record;

import com.company.types.Status;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class StudentRecord extends Record  implements Serializable {
    public StudentRecord(String recordID,
                         String firstName,
                         String lastName,
                         ArrayList<String> courses,
                         Status status,
                         Date statusDate) {
        super(recordID, firstName, lastName);
        map.put("courses", courses);
        map.put("status", status);
        map.put("statusDate", statusDate);
    }

    @Override
    public String print() {
        return super.print() + ", " +
                "courses: " + ((ArrayList<String>) map.get("courses")).toString() + ", " +
                "status: " + ((Status) map.get("status")).name() + ", " +
                "statusDate: " + ((Date) map.get("statusDate")).toString();
    }

    public ArrayList<String> getCourses() {
        return (ArrayList<String>) map.get("courses");
    }

    public Status getStatus() {
        return (Status) map.get("status");
    }

    public Date getStatusDate() {
        return (Date) map.get("statusDate");
    }
}
