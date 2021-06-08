package test;

import com.company.ManagerClient;
import com.company.RecordIDsServer;
import com.company.Server;
import com.company.config.Config;
import com.company.record.Record;
import com.company.record.StudentRecord;
import com.company.record.TeacherRecord;
import com.company.types.Location;
import com.company.types.Status;
import org.junit.After;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Synchronization Testing
 */
public class SyncTest {
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
    }

    @After
    public void tearDown() {
        recordIDsServer.destroy();
        mtlServer.destroy();
        lvlServer.destroy();
        ddoServer.destroy();
    }

    /**
     * Tests when a lot of clients access the createSRecord operation on a same server
     */
    @Test
    public void createSR() {
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<StudentRecord> records = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            StudentRecord sr = new StudentRecord(
                    "",
                    "firstName" + i,
                    "A" + i,
                    new ArrayList<>(Collections.singletonList("" + i)),
                    Status.ACTIVE,
                    new Date(new Date().getTime()));
            records.add(sr);

            threads.add(new Thread(() -> {
                try {
                    new ManagerClient(Config.MTLServerRegistryPort).createSRecord(
                            sr.getFirstName(),
                            sr.getLastName(),
                            sr.getCourses(),
                            sr.getStatus(),
                            sr.getStatusDate());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }));
        }

        startAndWaitUntilThreadFinish(threads);

        boolean correct = false;
        try {
            ArrayList<Record> serverRecords = mtlServer.getRecords().get('A');
            if (serverRecords != null) {
                for (int i = 0; i < records.size(); i++) {
                    for (Record serverSR : serverRecords) {
                        correct = isEqualSR((StudentRecord) serverSR, records.get(i));
                        if (correct)
                            break;
                    }
                    if (!correct)
                        break;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        assertTrue(correct);
    }

    /**
     * Tests when a lot of clients access the createTRecord operation on a same server
     */
    @Test
    public void createTR() {
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<TeacherRecord> records = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            TeacherRecord tr = new TeacherRecord(
                    "",
                    "Mazen" + i,
                    "A" + i,
                    "Concordia",
                    "12345678",
                    "CS",
                    Location.MTL);
            records.add(tr);

            threads.add(new Thread(() -> {
                try {
                    new ManagerClient(Config.MTLServerRegistryPort).createTRecord(
                            tr.getFirstName(),
                            tr.getLastName(),
                            tr.getAddress(),
                            tr.getPhone(),
                            tr.getSpecialization(),
                            tr.getLocation());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }));
        }

        startAndWaitUntilThreadFinish(threads);

        boolean correct = false;
        try {
            ArrayList<Record> serverRecords = mtlServer.getRecords().get('A');
            if (serverRecords != null) {
                for (int i = 0; i < records.size(); i++) {
                    for (Record serverSR : serverRecords) {
                        correct = isEqualTR((TeacherRecord) serverSR, records.get(i));
                        if (correct)
                            break;
                    }
                    if (!correct)
                        break;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        assertTrue(correct);
    }


    /**
     * Tests when a lot of clients access the getRecordsCount operation on a same server
     */
    @Test
    public void getRecordsCount() {
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<HashMap<Location, Integer>> actualCounts = new ArrayList<>();
        ManagerClient client = new ManagerClient(Config.MTLServerRegistryPort);

        // add some TR records
        for (int i = 0; i < 10; i++) {
            TeacherRecord tr = new TeacherRecord(
                    "",
                    "Mazen" + i,
                    "A" + i,
                    "Concordia",
                    "12345678",
                    "CS",
                    Location.MTL);
            try {
                client.createTRecord(
                        tr.getFirstName(),
                        tr.getLastName(),
                        tr.getAddress(),
                        tr.getPhone(),
                        tr.getSpecialization(),
                        tr.getLocation());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // define threads getting the records count
            threads.add(new Thread(() -> {
                try {
                    actualCounts.add(new ManagerClient(Config.MTLServerRegistryPort).getRecordCounts());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }));
        }

        startAndWaitUntilThreadFinish(threads);

        // check if the counts are correct
        boolean correct = true;
        for (HashMap<Location, Integer> countLoc : actualCounts) {
            if ((countLoc.get(Location.MTL) != 10) || (countLoc.get(Location.LVL) != 0) || (countLoc.get(Location.DDO) != 0))
                correct = false;
        }
        assertTrue(correct);
    }

    /**
     * Tests when a lot of clients access the editRecord operation on a same server
     */
    @Test
    public void editMultipleRecords() {
        ArrayList<Thread> threads = new ArrayList<>();
        ManagerClient client = new ManagerClient(Config.MTLServerRegistryPort);

        // add some TR records
        for (int i = 0; i < 10; i++) {
            TeacherRecord tr = new TeacherRecord(
                    "",
                    "Mazen" + i,
                    "A" + i,
                    "Concordia",
                    "12345678",
                    "CS",
                    Location.MTL);
            try {
                client.createTRecord(
                        tr.getFirstName(),
                        tr.getLastName(),
                        tr.getAddress(),
                        tr.getPhone(),
                        tr.getSpecialization(),
                        tr.getLocation());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // define threads editing the records
            int finalI = i;
            threads.add(new Thread(() -> {
                try {
                    new ManagerClient(Config.MTLServerRegistryPort)
                            .editRecord(
                                    "TR" + String.format("%05d", finalI + 1),
                                    "phone",
                                    String.valueOf(finalI + 1));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }));
        }

        startAndWaitUntilThreadFinish(threads);

        boolean correct = false;
        try {
            ArrayList<Record> serverRecords = mtlServer.getRecords().get('A');
            if (serverRecords != null) {
                int i = 1;
                for (Record serverSR : serverRecords) {
                    correct = ((TeacherRecord) serverSR).getPhone().equals(String.valueOf(i));
                    if (!correct)
                        break;
                    i++;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        assertTrue(correct);
    }


    /**
     * Tests when multiple clients edit different fields of the same record on a same server
     */
    @Test
    public void editSameRecord() {
        ArrayList<Thread> threads = new ArrayList<>();
        ManagerClient client = new ManagerClient(Config.MTLServerRegistryPort);
        TeacherRecord templateTR = new TeacherRecord(null, null, null, null, null, null, null);

        // add one TR records
        TeacherRecord tr = new TeacherRecord(
                "",
                "Mazen" + 1,
                "A" + 1,
                "Concordia",
                "12345678",
                "CS",
                Location.MTL);
        try {
            client.createTRecord(
                    tr.getFirstName(),
                    tr.getLastName(),
                    tr.getAddress(),
                    tr.getPhone(),
                    tr.getSpecialization(),
                    tr.getLocation());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // define 6 threads editing 6 different fields of the same record setting them all to "1"
        Set<String> fields = templateTR.getFieldNames();
        fields.remove("recordID");
        fields.remove("lastName");
        for (String field : fields) {
            if (field.equals("location"))
                addEditThreads(threads, "TR" + String.format("%05d", 1), field, Location.DDO);
            else
                addEditThreads(threads, "TR" + String.format("%05d", 1), field, "1");
        }

        startAndWaitUntilThreadFinish(threads);

        boolean testsPassed = false;
        try {
            TeacherRecord serverRecord = (TeacherRecord) mtlServer.getRecords().get('A').get(0);
            if (serverRecord != null) {
                testsPassed = true;
                assertEquals(serverRecord.getFirstName(), String.valueOf(1));
                assertEquals(serverRecord.getAddress(), String.valueOf(1));
                assertEquals(serverRecord.getPhone(), String.valueOf(1));
                assertEquals(serverRecord.getSpecialization(), String.valueOf(1));
                assertEquals(serverRecord.getLocation(), Location.DDO);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        assertTrue(testsPassed);
    }


    /**
     * Tests when multiple clients invoke createTR & createSR operations on a same server
     */
    @Test
    public void simultaneousMultipleOps() {
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<Record> records = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            TeacherRecord tr = new TeacherRecord(
                    "",
                    "Mazen" + i,
                    "A" + i,
                    "Concordia",
                    "12345678",
                    "CS",
                    Location.MTL);

            StudentRecord sr = new StudentRecord(
                    "",
                    "firstName" + i,
                    "A" + i,
                    new ArrayList<>(Collections.singletonList("" + i)),
                    Status.ACTIVE,
                    new Date(new Date().getTime()));

            records.add(sr);
            records.add(tr);

            threads.add(new Thread(() -> {
                try {
                    ManagerClient cl = new ManagerClient(Config.MTLServerRegistryPort);
                    cl.createTRecord(
                            tr.getFirstName(),
                            tr.getLastName(),
                            tr.getAddress(),
                            tr.getPhone(),
                            tr.getSpecialization(),
                            tr.getLocation());
                    cl.createSRecord(
                            sr.getFirstName(),
                            sr.getLastName(),
                            sr.getCourses(),
                            sr.getStatus(),
                            sr.getStatusDate());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }));
        }


        startAndWaitUntilThreadFinish(threads);

        boolean correct = false;
        try {
            ArrayList<Record> serverRecords = mtlServer.getRecords().get('A');
            if (serverRecords != null) {
                for (int i = 0; i < records.size(); i++) {
                    for (Record serverR : serverRecords) {
                        if ((records.get(i) instanceof TeacherRecord) && (serverR instanceof TeacherRecord))
                            correct = isEqualTR((TeacherRecord) serverR, (TeacherRecord) records.get(i));
                        else if ((records.get(i) instanceof StudentRecord) && (serverR instanceof StudentRecord))
                            correct = isEqualSR((StudentRecord) serverR, (StudentRecord) records.get(i));
                        if (correct)
                            break;
                    }
                    if (!correct)
                        break;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        assertTrue(correct);
    }

    // creates threads editing different fields of a same record
    private void addEditThreads(ArrayList<Thread> threads, String recordID, String field, Object newVal) {
        threads.add(new Thread(() -> {
            try {
                new ManagerClient(Config.MTLServerRegistryPort)
                        .editRecord(
                                recordID,
                                field,
                                newVal);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }));
    }

    private void startAndWaitUntilThreadFinish(ArrayList<Thread> threads) {
        for (Thread thread : threads) {
            thread.start();
        }

        // wait until all threads finish their requests
        boolean finished = false;
        while (!finished) {
            finished = true;
            for (Thread thread : threads) {
                if (thread.isAlive())
                    finished = false;
            }
        }
    }

    public boolean isEqualSR(StudentRecord r1, StudentRecord r2) {
        return (r1.getCourses().equals(r2.getCourses())
                && r1.getStatus().equals(r2.getStatus())
                && r1.getStatusDate().equals(r2.getStatusDate())
                && r1.getFirstName().equals(r2.getFirstName())
                && r1.getLastName().equals(r2.getLastName()));
    }

    public boolean isEqualTR(TeacherRecord r1, TeacherRecord r2) {
        return (r1.getAddress().equals(r2.getAddress())
                && r1.getLocation().equals(r2.getLocation())
                && r1.getSpecialization().equals(r2.getSpecialization())
                && r1.getFirstName().equals(r2.getFirstName())
                && r1.getLastName().equals(r2.getLastName())
                && r1.getPhone().equals(r2.getPhone()));
    }
}
