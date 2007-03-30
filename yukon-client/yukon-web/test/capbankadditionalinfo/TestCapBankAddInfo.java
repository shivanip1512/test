package capbankadditionalinfo;

import java.sql.Connection;
import java.sql.SQLException;

import util.CBCTestUtil;

import junit.framework.TestCase;

import com.cannontech.database.db.capcontrol.CapBankAdditional;

public class TestCapBankAddInfo extends TestCase {

    private Connection connection;
    private CapBankAdditional info;

    protected void setUp() throws Exception {
        connection = CBCTestUtil.getConnection();
        info = new CapBankAdditional();
        info.setDbConnection(connection);

    }

    public void testAdd() {
        info.setDeviceID(CBCTestUtil.getValidCapBankId());
        try {
            info.add();
        } catch (SQLException e) {
            info = null;
        }
        assertNotNull(info);
    }

    public void testRetrieve() {
        Integer validCapBankId = CBCTestUtil.getValidCapBankId();
        retrieveAddInfo(validCapBankId);
        assertNotNull(info);
        assertEquals(validCapBankId, info.getDeviceID());
    }

    public void testUpdate() {
        String newDriveDirections = "123 New Opcount Drive, Capbankville MN 23345";
        Integer validCapBankId = CBCTestUtil.getValidCapBankId();
        retrieveAddInfo(validCapBankId);
        String oldDriveDir = info.getDriveDir();
        info.setDriveDir(newDriveDirections);
        try {
            info.update();
        } catch (SQLException e) {
            info = null;
        }
        retrieveAddInfo(validCapBankId);
        assertNotNull(info);
        assertNotSame(oldDriveDir, info.getDriveDir());
        assertEquals(newDriveDirections, info.getDriveDir());
    }

    public void testDelete() {
        info.setDeviceID(CBCTestUtil.getValidCapBankId());
        try {
            info.delete();
        } catch (SQLException e) {
            info = null;
        }
        assertNotNull(info);

    }

    private void retrieveAddInfo(Integer validCapBankId) {
        info.setDeviceID(validCapBankId);
        try {
            info.retrieve();
        } catch (SQLException e) {
            info = null;
        }
    }

    protected void tearDown() throws Exception {
        CBCTestUtil.closeConnection(connection);
    }
}
