package com.cannontech.web.editor.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.database.data.capcontrol.CapControlSubstation;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.web.editor.data.CBCAreaData;
import com.cannontech.web.util.CBCDBUtil;

public class CBCAreaDataModel extends EditorDataModelImpl {
    CapControlArea areaPers;
    JSFAssignmentModel assignmentModel;

    public boolean isAreaNone() {
        return areaPers.getPAOName().equalsIgnoreCase("(none)");
    }

    public CBCAreaDataModel(CapControlArea area) {
        super(area);
        areaPers = area;
        init();
    }

    private void init() {
        initAssignmentModel();
    }

    private void initAssignmentModel() {
        assignmentModel = new JSFAssignmentModel() {

            FacesContext getContext() {
                return FacesContext.getCurrentInstance();
            }

            void populateAssigned() {
                List<CCSubAreaAssignment> allAreaSubs = CCSubAreaAssignment.getAllAreaSubs(areaPers.getPAObjectID());
                for (CCSubAreaAssignment assignment : allAreaSubs) {
                    assignedSubs.add(assignment.getSubstationBusID());
                }
            }

            void populateUnassigned() {
                unassignedSubs = CapControlSubstation.getAllUnassignedSubstations();
            }
        };
    }

    public void add() {
        assignmentModel.add();
    }

    public void remove() {
        assignmentModel.remove();
    }

    public List<CBCAreaData> getAssigned() {
        List<Integer> list = assignmentModel.getAssigned();
        List<CBCAreaData> assigned = CBCAreaData.toList(list);
        return assigned;
    }

    public List<CBCAreaData> getUnassigned() {
        List<Integer> list = assignmentModel.getUnassigned();
        List<CBCAreaData> unassigned = CBCAreaData.toList(list);
        return unassigned;
    }

    public void updateDataModel() {
        List<Integer> assignedIDs = CBCAreaData.toIntegerList(getAssigned());
        List<Integer> unassignedIDs = CBCAreaData.toIntegerList(getUnassigned());
        Connection connection = CBCDBUtil.getConnection();
        handleAssignedIDs(assignedIDs, connection);
        handleUnassignedIDs(unassignedIDs, connection);
        assignNewSubs(assignedIDs, connection);

        CBCDBUtil.closeConnection(connection);
    }

    public void assignNewSubs(List<Integer> assignedIDs, Connection connection) {
        // create assignment persistent objects (CCSubAreaAssignment) from the
        // assigned list
        // and persist them to DB
        List<CCSubAreaAssignment> newAssignments = CCSubAreaAssignment.asList(assignedIDs,
                                                                              areaPers.getPAObjectID());
        areaPers.setAreaSubs((ArrayList) newAssignments);

    }

    public void handleUnassignedIDs(List<Integer> unassignedIDs,
            Connection connection) {
        // see if it belongs to the current area
        // delete it from sub assignment

        Integer areaID = areaPers.getPAObjectID();
        List<CCSubAreaAssignment> childList = CCSubAreaAssignment.getAllAreaSubs(areaID);
        ArrayList<Integer> idsToRemove = CCSubAreaAssignment.getAsIntegerList(childList);
        idsToRemove.retainAll(unassignedIDs);
        CCSubAreaAssignment.deleteSubs(idsToRemove, areaID);
    }

    public void handleAssignedIDs(List<Integer> assignedIDs,
            Connection connection) {
        for (Integer id : assignedIDs) {
            // see if it belongs to a different area than current
            // if yes - delete assignment from the old sub
            Integer areaID = CCSubAreaAssignment.getAreaIDForSub(id);
            if (areaID != null && !areaID.equals(areaPers.getPAObjectID())) {
                CCSubAreaAssignment objToDelete = new CCSubAreaAssignment();
                objToDelete.setAreaID(areaID);
                objToDelete.setSubstationBusID(id);
                objToDelete.setDbConnection(connection);
                try {
                    objToDelete.delete();
                } catch (SQLException e) {
                    CTILogger.error(e);
                }
            }
        }
    }

    public JSFAssignmentModel getAssignmentModel() {
        return assignmentModel;
    }

    public void setAssignmentModel(JSFAssignmentModel assignmentModel) {
        this.assignmentModel = assignmentModel;
    }

}
