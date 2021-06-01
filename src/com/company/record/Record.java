package com.company.record;

import java.util.HashMap;

public class Record {

    protected final HashMap<String, Object> map;


    public Record(String recordID, String firstName, String lastName) {
        map = new HashMap<>();
        map.put("recordID", recordID);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
    }

    public String getRecordID() {
        return (String) map.get("recordID");
    }

    public void set(String field, Object value) {
        map.put(field, value);
    }
}
