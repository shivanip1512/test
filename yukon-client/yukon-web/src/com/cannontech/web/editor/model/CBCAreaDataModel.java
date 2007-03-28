package com.cannontech.web.editor.model;

import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;

public class CBCAreaDataModel extends EditorDataModelImpl {
    CapControlArea areaPers;
    JSFAssignmentModel assignmentModel;

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
                List<LiteYukonPAObject> allSubs = DaoFactory.getPaoDao()
                                                            .getAllCapControlSubBuses();
                for (Iterator iter = allSubs.iterator(); iter.hasNext();) {
                    LiteYukonPAObject sub = (LiteYukonPAObject) iter.next();
                    unassignedSubs.add(sub.getLiteID());
                }
            }

        };
    }

    public void add() {
        assignmentModel.add();
    }

    public void remove() {
        assignmentModel.remove();
    }

    public List<Integer> getAssigned() {
        return assignmentModel.getAssigned();
    }

    public List<Integer> getUnassigned() {
        return assignmentModel.getUnassigned();
    }

}
