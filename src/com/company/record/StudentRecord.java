package com.company.record;

import com.company.types.Status;

import java.util.ArrayList;
import java.util.Date;

public class StudentRecord extends Record {
    private final ArrayList<String> courses;
    private final Status status;
    private final Date statusDate;

    public StudentRecord(String recordID,
                         String firstName,
                         String lastName,
                         ArrayList<String> courses,
                         Status status,
                         Date statusDate) {
        super(recordID, firstName, lastName);
        this.courses = courses;
        this.status = status;
        this.statusDate = statusDate;
    }
}
