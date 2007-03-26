package com.cannontech.web.test.capbankadditionalinfo;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.web.test.Util;

public class TestCapBankAddInfo extends TestCase {

    private Connection connection;
    private CapBankAdditional info;

    protected void setUp() throws Exception {
        connection = Util.getConnection();
        info = new CapBankAdditional();
        info.setDbConnection(connection);

    }

    public void testAdd() {
        info.setDeviceID(Util.getValidCapBankId());
        try {
            info.add();
        } catch (SQLException e) {
            info = null;
        }
        assertNotNull(info);
    }

    public void testRetrieve() {
        Integer validCapBankId = Util.getValidCapBankId();
        retrieveAddInfo(validCapBankId);
        assertNotNull(info);
        assertEquals(validCapBankId, info.getDeviceID());
    }

    public void testUpdate() {
        String newDriveDirections = "123 New Opcount Drive, Capbankville MN 23345";
        Integer validCapBankId = Util.getValidCapBankId();
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
        info.setDeviceID(Util.getValidCapBankId());
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
        Util.closeConnection(connection);
    }
}
