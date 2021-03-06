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
import java.util.*;

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

    @org.junit.Test
    public void editInvalidRecordFields() {
        boolean isTested = false;
        try {
            isTested = true;
            assertTrue(mtlManager.createSRecord(
                    "Mazen",
                    "Eid",
                    new ArrayList<String>(Arrays.asList("DSD", "CS")),
                    Status.ACTIVE,
                    new Date(new Date().getTime())));
            assertTrue(mtlManager.createTRecord("Mazen", "Eid", "maisonneuve", "12354", "CS", Location.MTL));

            // invalid fields
            assertFalse(mtlManager.editRecord("TR00001", "status", Status.INACTIVE));
            assertFalse(mtlManager.editRecord("TR00001", "statusDate", new Date(new Date().getTime())));
            assertFalse(mtlManager.editRecord("TR00001", "courses", new ArrayList<>(Arrays.asList("CE"))));

            assertFalse(mtlManager.editRecord("SR00001", "address", "concordia"));
            assertFalse(mtlManager.editRecord("SR00001", "phone", "1"));
            assertFalse(mtlManager.editRecord("SR00001", "specialization", ""));
            assertFalse(mtlManager.editRecord("SR00001", "location", Location.LVL));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isTested);
    }

    /**
     * test whether the HashMap gets rearranged if the lastName changes
     */
    @org.junit.Test
    public void editLastName() {
        boolean isUpdated = false;
        boolean notMoved = false;
        try {
            assertTrue(mtlManager.createTRecord("Mazen", "Eid", "maisonneuve", "12354", "CS", Location.MTL));
            assertTrue(mtlManager.editRecord("TR00001", "lastName", "Amr"));
            HashMap<Character, ArrayList<Record>> records = mtlManager.getRecords();

            // check if it moved from the old key
            if(records.containsKey('E')) {
                for (Record record : records.get('E')){
                    if (record.getRecordID().equals("TR00001")) {
                        notMoved = true;
                    }
                }
            }
            // check if it moved to the new key
            if(records.containsKey('A')) {
                for (Record record : records.get('A')) {
                    if (record.getRecordID().equals("TR00001")) {
                        TeacherRecord tr = (TeacherRecord) record;
                        isUpdated = tr.getLastName().equals("Amr");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertFalse(notMoved);  // check if still in the old key
        assertTrue(isUpdated);  // check if moved to the new key
    }


    /**
     * test whether the statusDate gets updated if the status changes
     */
    @org.junit.Test
    public void isStatusDateUpdated() {
        boolean isTested = false;
        try {
            Date initialDate = new Date(new Date().getTime());
            assertTrue(mtlManager.createSRecord(
                    "Mazen",
                    "Eid",
                    new ArrayList<String>(Arrays.asList("DSD", "CS")),
                    Status.ACTIVE,
                    initialDate));
            assertTrue(mtlManager.editRecord("SR00001", "status", Status.INACTIVE));
            HashMap<Character, ArrayList<Record>> records = mtlManager.getRecords();

            // check if the date got upadted
            if(records.containsKey('E')) {
                for (Record record : records.get('E')){
                    if (record.getRecordID().equals("SR00001")) {
                        StudentRecord recordSR = (StudentRecord) record;
                        assertEquals(recordSR.getStatus(), Status.INACTIVE);
                        assertTrue(recordSR.getStatusDate().after(initialDate));
                        isTested = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(isTested);
    }
}