package com.company.record;

import com.company.types.Location;

public class TeacherRecord extends Record{
    private final String address;
    private final String phone;
    private final String specialization;
    private final Location location;

    public TeacherRecord(String recordID,
                         String firstName,
                         String lastName,
                         String address,
                         String phone,
                         String specialization,
                         Location location) {
        super(recordID, firstName, lastName);
        this.address = address;
        this.phone = phone;
        this.specialization = specialization;
        this.location = location;
    }
}
