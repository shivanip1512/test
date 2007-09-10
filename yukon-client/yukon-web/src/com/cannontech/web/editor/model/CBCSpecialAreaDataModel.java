package com.cannontech.web.editor.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.capcontrol.CapControlSpecialArea;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.db.capcontrol.CCSubSpecialAreaAssignment;
import com.cannontech.web.editor.data.CBCSpecialAreaData;
import com.cannontech.web.util.CBCDBUtil;

public class CBCSpecialAreaDataModel extends EditorDataModelImpl {
    CapControlSpecialArea areaPers;
    JSFAssignmentModel assignmentModel;

    public boolean isAreaNone() {
        return areaPers.getPAOName().equalsIgnoreCase("(none)");
    }

    public CBCSpecialAreaDataModel(CapControlSpecialArea area) {
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
                List<CCSubSpecialAreaAssignment> allAreaSubs = CCSubSpecialAreaAssignment.getAllSpecialAreaSubs(areaPers.getPAObjectID());
                for (CCSubSpecialAreaAssignment assignment : allAreaSubs) {
                    assignedSubs.add(assignment.getSubstationBusID());
                }
            }

            void populateUnassigned() {
                unassignedSubs = CapControlSubBus.getAllUnassignedBuses();
            }
        };
    }

    public void add() {
        assignmentModel.add();
    }

    public void remove() {
        assignmentModel.remove();
    }

    public List<CBCSpecialAreaData> getAssigned() {
        List<Integer> list = assignmentModel.getAssigned();
        List<CBCSpecialAreaData> assigned = CBCSpecialAreaData.toList(list);
        return assigned;
    }

    public List<CBCSpecialAreaData> getUnassigned() {
        List<Integer> list = assignmentModel.getUnassigned();
        List<CBCSpecialAreaData> unassigned = CBCSpecialAreaData.toList(list);
        return unassigned;
    }

    public void updateDataModel() {
        List<Integer> assignedIDs = CBCSpecialAreaData.toIntegerList(getAssigned());
        List<Integer> unassignedIDs = CBCSpecialAreaData.toIntegerList(getUnassigned());
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
        List<CCSubSpecialAreaAssignment> newAssignments = CCSubSpecialAreaAssignment.asList(assignedIDs,
                                                                              areaPers.getPAObjectID());
        areaPers.setSpecialAreaSubs((ArrayList) newAssignments);

    }

    public void handleUnassignedIDs(List<Integer> unassignedIDs,
            Connection connection) {
        // see if it belongs to the current area
        // delete it from sub assignment

        Integer areaID = areaPers.getPAObjectID();
        List<CCSubSpecialAreaAssignment> childList = CCSubSpecialAreaAssignment.getAllSpecialAreaSubs(areaID);
        ArrayList<Integer> idsToRemove = CCSubSpecialAreaAssignment.getAsIntegerList(childList);
        idsToRemove.retainAll(unassignedIDs);
        CCSubSpecialAreaAssignment.deleteSubs(idsToRemove, areaID);
    }

    public void handleAssignedIDs(List<Integer> assignedIDs,
            Connection connection) {
        for (Integer id : assignedIDs) {
            // see if it belongs to a different area than current
            // if yes - delete assignment from the old sub
            Integer areaID = CCSubSpecialAreaAssignment.getAreaIDForSub(id);
            if (areaID != null && !areaID.equals(areaPers.getPAObjectID())) {
                CCSubSpecialAreaAssignment objToDelete = new CCSubSpecialAreaAssignment();
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
