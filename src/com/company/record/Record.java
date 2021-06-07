package com.company.record;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class Record implements Serializable {

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

    public String getLastName() {
        return (String) map.get("lastName");
    }

    public String print() {
        return "recordID: " + map.get("recordID") + ", " +
                "firstName: " + map.get("firstName") + ", " +
                "lastName: " + map.get("lastName");
    }

    public Set<String> getFieldNames() {
        return map.keySet();
    }
}
