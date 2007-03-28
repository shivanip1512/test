package com.cannontech.web.test.capcontrolarea;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;

import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.web.editor.model.CBCAreaDataModel;
import com.cannontech.web.test.Util;

public class TestAreaDateModel extends TestCase {

    private CapControlArea area;
    private CBCAreaDataModel model;
    private Connection connection;

    protected void setUp() throws Exception {
        initArea();
        initModel();
    }

    public void testGetAssigned() {
        List<Integer> assignedList = model.getAssigned();
        assertNotNull(assignedList);
    }

    public void testGetUnAssigned() {
        List<Integer> unassignedList = model.getUnassigned();
        assertNotNull(unassignedList);
    }
    
    public void testAdd () {
        
    }

    protected void tearDown() throws Exception {
        Util.closeConnection(connection);
    }

    private void initModel() {
        model = new CBCAreaDataModel(area);
    }

    private void initArea() throws SQLException {
        area = new CapControlArea();
        area.setCapControlPAOID(Util.getAreaID());
        connection = Util.getConnection();
        area.setDbConnection(connection);
        area.retrieve();
    }

}
