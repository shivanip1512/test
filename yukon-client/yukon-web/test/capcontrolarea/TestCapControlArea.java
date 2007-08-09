package capcontrolarea;

import java.sql.Connection;
import java.sql.SQLException;

import util.CBCTestUtil;

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
        areaID = CBCTestUtil.getAreaID ();
    }

    public void testDeleteArea() {
        CapControlArea area = new CapControlArea();
        area.setCapControlPAOID(62);
        area.setDbConnection(connection);
        try {
            area.delete();
        } catch (SQLException e) {
            area = null;
        }
        assertNotNull(area);
    }

    public void testCapControlAreaCreate() {
        CapControlArea area = new CapControlArea();
        area.setPAOName("CapControlArea " + 1);
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
        area.setCapControlPAOID(CBCTestUtil.getAreaID());
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
        area.setAreaSubs(CBCTestUtil.getFirstFourSubs(area.getCapControlPAOID()));
        try {
            area.update();
        } catch (SQLException e) {
            area = null;
        }
        area = retrieve();
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
