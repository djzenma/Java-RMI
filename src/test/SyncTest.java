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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertTrue;

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
    public void createSR(){
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<StudentRecord> records = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            StudentRecord sr = new StudentRecord(
                    "",
                    "firstName"+i,
                    "A"+i,
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
            if(serverRecords != null) {
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
    public void createTR(){
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<TeacherRecord> records = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            TeacherRecord tr = new TeacherRecord(
                    "",
                    "Mazen"+i,
                    "A"+i,
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
            if(serverRecords != null) {
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

    private void startAndWaitUntilThreadFinish(ArrayList<Thread> threads ) {
        for (Thread thread : threads) {
            thread.start();
        }

        // wait until all threads finish their requests
        boolean finished = false;
        while(!finished) {
            finished = true;
            for (Thread thread : threads) {
                if(thread.isAlive())
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
