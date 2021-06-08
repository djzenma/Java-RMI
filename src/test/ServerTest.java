package test;

import com.company.ManagerClient;
import com.company.RecordIDsServer;
import com.company.Server;
import com.company.Servers;
import com.company.config.Config;
import com.company.record.Record;
import com.company.record.StudentRecord;
import com.company.record.TeacherRecord;
import com.company.types.Location;
import com.company.types.Status;
import org.junit.After;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

public class ServerTest {
    ManagerClient mtlManager;
    ManagerClient lvlManager;
    ManagerClient ddoManager;

    Server mtlServer;
    Server lvlServer;
    Server ddoServer;
    RecordIDsServer recordIDsServer;


    @org.junit.Before
    public void setUp() throws RemoteException {
        recordIDsServer = new RecordIDsServer(Config.RecordIDsServerPort);

        mtlServer = new Server(Location.MTL,
                Config.MTLServerRegistryPort,
                Config.MTLServerRecordsCountPort);


        lvlServer = new Server(Location.LVL,
                Config.LVLServerRegistryPort,
                Config.LVLServerRecordsCountPort);


        ddoServer = new Server(Location.DDO,
                Config.DDOServerRegistryPort,
                Config.DDOServerRecordsCountPort);

        mtlManager = new ManagerClient(Config.MTLServerRegistryPort);
        lvlManager = new ManagerClient(Config.LVLServerRegistryPort);
        ddoManager = new ManagerClient(Config.DDOServerRegistryPort);
    }

    @After
    public void tearDown(){
        recordIDsServer.destroy();
        mtlServer.destroy();
        lvlServer.destroy();
        ddoServer.destroy();
    }

    @org.junit.Test
    public void createTRecord() {
        boolean result = false;
        try {
            result = mtlManager.createTRecord("Mazen", "Eid", "maisonneuve", "12354", "CS", Location.MTL);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        assertTrue(result);
    }

    @org.junit.Test
    public void createSRecord() {
        ArrayList<String> courses = new ArrayList<>();
        courses.add("math");
        courses.add("comp");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        boolean result = false;
        try {
            date = dateFormat.parse("10-5-2021");
            result = mtlManager.createSRecord("Mazen","Eid", courses, Status.ACTIVE, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(result);
    }

    @org.junit.Test
    public void getRecordCounts() {
        try {
            mtlManager.createTRecord("reza1", "Nikzad1", "pi X1", "123548961", "Math", Location.MTL);
            ddoManager.createTRecord("reza2", "Nikzad2", "pi X2", "123548962", "Math", Location.DDO);
            ddoManager.createTRecord("reza3", "Nikzad3", "pi X3", "123548963", "Math", Location.DDO);
            assertEquals(1, (int) lvlManager.getRecordCounts().get(Location.MTL));
            assertEquals(2, (int) lvlManager.getRecordCounts().get(Location.DDO));
            assertEquals(0, (int) lvlManager.getRecordCounts().get(Location.LVL));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void editRecord() {
        boolean isEdited = false;
        try {
            assertTrue(mtlManager.createTRecord("Mazen", "Eid", "maisonneuve", "12354", "CS", Location.MTL));
            assertTrue(mtlManager.editRecord("TR00001", "phone", "0"));
            HashMap<Character, ArrayList<Record>> records = mtlManager.getRecords();
            if(records.containsKey('E')) {
                for (Record record : records.get('E')){
                    if (record.getRecordID().equals("TR00001")) {
                        TeacherRecord tr = (TeacherRecord) record;
                        isEdited = tr.getPhone().equals("0");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isEdited);
    }
}