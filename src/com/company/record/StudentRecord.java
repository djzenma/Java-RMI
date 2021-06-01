package com.company.record;

import com.company.types.Status;

import java.util.ArrayList;
import java.util.Date;

public class StudentRecord extends Record {
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
}
