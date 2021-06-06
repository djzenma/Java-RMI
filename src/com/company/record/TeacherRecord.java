package com.company.record;

import com.company.types.Location;

public class TeacherRecord extends Record{
    public TeacherRecord(String recordID,
                         String firstName,
                         String lastName,
                         String address,
                         String phone,
                         String specialization,
                         Location location) {
        super(recordID, firstName, lastName);
        map.put("address", address);
        map.put("phone", phone);
        map.put("specialization", specialization);
        map.put("location", location);
    }

    // TODO
    @Override
    public String print() {
        return super.print();
    }
}
