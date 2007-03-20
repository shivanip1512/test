package com.cannontech.web.test.capcontrolarea;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapControlArea;

public class TestCapControlArea extends TestCase {
    Integer areaID;
    Connection connection;
    
    protected void setUp() throws Exception {
        connection = PoolManager.getInstance()
                                .getConnection(CtiUtilities.getDatabaseAlias());
        areaID = Util.getAreaID ();
    }

    

    public void testCapControlAreaCreate() {
        CapControlArea area = new CapControlArea();
        area.setPAOName("CapControlArea " + 1);
        area.getCapControlArea().setStrategyID(Util.getStrategyID());
        area.setDbConnection(connection);
        areaID = area.getPAObjectID();
        try {
            area.add();
        } catch (SQLException e) {
            area = null;
        }
        assertNotNull(area);
    }



   
    
    public void testRetrieveArea () {
        CapControlArea area = retrieve();
        assertNotNull(area);
    }



    private CapControlArea retrieve() {
        CapControlArea area = new CapControlArea();
        area.setCapControlPAOID(Util.getAreaID());
        area.setDbConnection(connection);
        try {
            area.retrieve();
        } catch (SQLException e) {
            area = null;
        }
        return area;
    }

    public void testUpdateArea () {
        CapControlArea area = retrieve();
        Integer oldStrategyID = area.getCapControlArea().getStrategyID();
        Integer newStratID = Util.getStratIDToUpdate();
        area.getCapControlArea().setStrategyID(newStratID);
        area.setAreaSubs(Util.getFirstFiveSubs(area.getCapControlPAOID()));
        try {
            area.update();
        } catch (SQLException e) {
            area = null;
        }
        area = retrieve();
        assertNotNull(area);
        assertEquals(newStratID, area.getCapControlArea().getStrategyID());
        assertNotSame(oldStrategyID, area.getCapControlArea());
        
    }
    public void testDeleteArea() {
        CapControlArea area = new CapControlArea();
        area.setCapControlPAOID(areaID);
        area.setDbConnection(connection);
        try {
            area.delete();
        } catch (SQLException e) {
            area = null;
        }
        assertNotNull(area);
    }

    protected void tearDown() throws Exception {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {}
        }
        
    }

}
