package capcontrolarea;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import util.CBCTestUtil;

import junit.framework.TestCase;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;

public class TestSubAreaAssignment extends TestCase {

    private CapControlArea area;
    private Connection connection;

    protected void setUp() throws Exception {
        area = new CapControlArea();
        area.setPAOName("CapControlArea " + 1);
        connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        area.setDbConnection(connection);
        try {
            area.add();
        } catch (SQLException e) {
            area = null;
        }

    }

    public void testCreateSubAssignment() {
        CCSubAreaAssignment newAssignment = createNewAssignment();
        assertNotNull(newAssignment);
    }
    
    public void testRetrieveSubAssignment () {
        CCSubAreaAssignment newAssignment = createNewAssignment();
        //save the info
        Integer areaID = newAssignment.getAreaID();
        Integer subID = newAssignment.getSubstationBusID();
        Integer displayOrder = newAssignment.getDisplayOrder();
       
        List<CCSubAreaAssignment> allAreaSubs = CCSubAreaAssignment.getAllAreaSubStations(areaID);
        assertEquals(areaID, allAreaSubs.get(0).getAreaID());
        assertEquals(subID, allAreaSubs.get(0).getSubstationBusID());
        assertEquals(displayOrder, allAreaSubs.get(0).getDisplayOrder());
                
    }

    public void testDeleteSubAssignment() {
        CCSubAreaAssignment newAssignment = createNewAssignment();
        Connection connection = CBCTestUtil.getConnection();
        newAssignment.setDbConnection(connection);
        try {
            newAssignment.delete();
        } catch (SQLException e) {
            newAssignment = null;
        } finally {
            CBCTestUtil.closeConnection(connection);
        }
        assertNotNull(newAssignment);
    }

    public void testUpdateSubAssignment() {
        CCSubAreaAssignment newAssignment = createNewAssignment();
        Integer oldSubId = newAssignment.getSubstationBusID();
        Integer newSubID = CBCTestUtil.getSubIDToUpdate();

        if (newSubID != null) {
            newAssignment.setSubstationBusID(newSubID);
        }
        newAssignment.setDbConnection(CBCTestUtil.getConnection());
        try {
            newAssignment.update();
        } catch (SQLException e) {
            newAssignment = null;
        }
        assertNotNull(newAssignment);
        assertEquals(newSubID, newAssignment.getSubstationBusID());
        assertNotSame(oldSubId, newAssignment.getSubstationBusID());
        CBCTestUtil.closeConnection(newAssignment.getDbConnection());
    }

    protected void tearDown() throws Exception {
        area.delete();
        if (connection != null) {
            connection.close();
        }

    }

    private CCSubAreaAssignment createNewAssignment() {
        CCSubAreaAssignment newAssignment = new CCSubAreaAssignment();
        newAssignment.setAreaID(area.getCapControlPAOID());
        newAssignment.setSubstationBusID(CBCTestUtil.getValidSubId());
        newAssignment.setDisplayOrder(1);
        Connection connection = CBCTestUtil.getConnection();
        newAssignment.setDbConnection(connection);
        try {
            newAssignment.add();
        } catch (SQLException e) {
            newAssignment = null;
        } finally {
            CBCTestUtil.closeConnection(connection);
        }
        return newAssignment;
    }

}
