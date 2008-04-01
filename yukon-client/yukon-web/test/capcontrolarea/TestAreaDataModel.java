package capcontrolarea;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import util.CBCTestUtil;

import junit.framework.TestCase;

import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.web.editor.data.CBCAreaData;
import com.cannontech.web.editor.model.CBCAreaDataModel;

public class TestAreaDataModel extends TestCase {

    private CapControlArea area;
    private CBCAreaDataModel model;
    private Connection connection;

    protected void setUp() throws Exception {
        initArea();
        initModel();
    }

    public void testGetAssigned() {
        List<CBCAreaData> assignedList = model.getAssigned();
        assertNotNull(assignedList);
    }

    public void testGetUnAssigned() {
        List<CBCAreaData> unassignedList = model.getUnassigned();
        assertNotNull(unassignedList);
    }
    
    // TODO fix these after progress
    public void testAdd () {
//        Integer settableID = CBCTestUtil.getSubIdNotInArea (area.getCapControlPAOID());
//        model.getAssignmentModel().setSettableID(settableID);
//        model.add();
//        JSFAssignmentModel assignmentModel = model.getAssignmentModel();
//        assertTrue( assignmentModel.getAssigned().contains(settableID));
//        assertFalse(assignmentModel.getUnassigned().contains(settableID));
    }
    
    // TODO fix these after progress
    public void testRemove () {
//        ArrayList<CCSubAreaAssignment> childList = area.getChildList();
//        Integer busID = childList.get(0).getSubstationBusID();
//        model.getAssignmentModel().setSettableID(busID);
//        model.remove();
//        JSFAssignmentModel assignmentModel = model.getAssignmentModel();
//        assertTrue( assignmentModel.getUnassigned().contains(busID));
//        assertFalse(assignmentModel.getAssigned().contains(busID));
        
    }

    public void testUpdate () {
        model.updateDataModel();
    }
    protected void tearDown() throws Exception {
        CBCTestUtil.closeConnection(connection);
    }

    private void initModel() {
        model = new CBCAreaDataModel(area);
    }

    private void initArea() throws SQLException {
        area = new CapControlArea();
        area.setCapControlPAOID(CBCTestUtil.getAreaID());
        connection = CBCTestUtil.getConnection();
        area.setDbConnection(connection);
        area.retrieve();
    }

}
