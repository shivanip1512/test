package com.cannontech.web.editor.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.editor.data.CBCAreaData;
import com.cannontech.web.util.CBCDBUtil;

public class CBCAreaDataModel extends EditorDataModelImpl {
    CapControlArea area;
    SubstationDao substationDao = YukonSpringHook.getBean("ccSubstationDao", SubstationDao.class);
    PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
    
    List<CBCAreaData> assignedSubstations = new ArrayList<CBCAreaData>();
    List<CBCAreaData> unassingedSubstations = new ArrayList<CBCAreaData>();

    public boolean isAreaNone() {
        return area.getPAOName().equalsIgnoreCase("(none)");
    }

    public CBCAreaDataModel(CapControlArea area) {
        super(area);
        this.area = area;
        init();
    }

    private void init() {
        List<CCSubAreaAssignment> allAreaSubs = CCSubAreaAssignment.getAllAreaSubStations(area.getPAObjectID());
        synchronized (allAreaSubs) {
            for (CCSubAreaAssignment assignment : allAreaSubs) {
                Integer subId = assignment.getSubstationBusID();
                String subName = paoDao.getYukonPAOName(subId);
                Integer displayOrder = assignment.getDisplayOrder();
                CBCAreaData data = new CBCAreaData(subId,subName,displayOrder + 1);
                assignedSubstations.add(data);
            }

            List<Integer> availableSubs = substationDao.getAllUnassignedSubstationIds();
            for (Integer subId : availableSubs) {
                String subName = paoDao.getYukonPAOName(subId);
                Integer displayOrder = 0;
                CBCAreaData data = new CBCAreaData(subId, subName, displayOrder);
                unassingedSubstations.add(data);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void add() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        Map paramMap = context.getExternalContext().getRequestParameterMap();
        Integer subId = new Integer((String) paramMap.get("id")).intValue();
        
        CBCAreaData data = new CBCAreaData(subId, paoDao.getYukonPAOName(subId), 1000);
        assignedSubstations.add(data);
        
        reOrderAssignedSubs(getAssigned());
        
        Iterator<CBCAreaData> iter = unassingedSubstations.iterator();
        while (iter.hasNext()) {
            CBCAreaData current = iter.next();
            if(current.getSubID().intValue() == subId.intValue()) {
                iter.remove();
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void remove() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map paramMap = context.getExternalContext().getRequestParameterMap();
        Integer subId = new Integer((String) paramMap.get("id")).intValue();
        
        Iterator<CBCAreaData> iter = assignedSubstations.iterator();
        while(iter.hasNext()) {
            CBCAreaData current = iter.next();
            if(current.getSubID().intValue() == subId.intValue()) {
                getUnassigned().add(current);
                iter.remove();
                break;
            }
        }
        
        reOrderAssignedSubs(getAssigned());
    }
    
    private void reOrderAssignedSubs(List<CBCAreaData> list) {
        for(int i = 0; i < list.size(); i++) {
            CBCAreaData data = list.get(i);
            data.setDisplayOrder(i+1);
        }
    }

    public List<CBCAreaData> getAssigned() {
        Collections.sort(assignedSubstations, new Comparator<CBCAreaData>() {
            public int compare(CBCAreaData o1, CBCAreaData o2) {
                Integer intA = o1.getDisplayOrder();
                Integer intB = o2.getDisplayOrder();

                return intA.compareTo(intB);
            }
        });
        return assignedSubstations;
    }

    public List<CBCAreaData> getUnassigned() {
        return unassingedSubstations;
    }

    public void updateDataModel() {
        List<Integer> assignedIds = CBCAreaData.toIntegerList(getAssigned());
        List<Integer> unassignedIds = CBCAreaData.toIntegerList(getUnassigned());
        Connection connection = CBCDBUtil.getConnection();
        handleAssignedIds(assignedIds, connection);
        handleUnassignedIds(unassignedIds, connection);
        assignNewSubs(getAssigned(), connection);
        CBCDBUtil.closeConnection(connection);
    }

    public void assignNewSubs(List<CBCAreaData> assignedSubs, Connection connection) {
        ArrayList<CCSubAreaAssignment> newAssignments = new ArrayList<CCSubAreaAssignment>();
        synchronized (newAssignments) {
            for (CBCAreaData data : assignedSubs) {
                CCSubAreaAssignment assignment = new CCSubAreaAssignment(area.getPAObjectID(), data.getSubID(), data.getDisplayOrder() - 1);
                newAssignments.add(assignment);
            }
            area.setAreaSubs(newAssignments);
        }
    }

    public void handleUnassignedIds(List<Integer> unassignedIDs, Connection connection) {
        // see if it belongs to the current area
        // delete it from sub assignment
        Integer areaID = area.getPAObjectID();
        List<CCSubAreaAssignment> childList = CCSubAreaAssignment.getAllAreaSubStations(areaID);
        ArrayList<Integer> idsToRemove = CCSubAreaAssignment.getAsIntegerList(childList);
        idsToRemove.retainAll(unassignedIDs);
        CCSubAreaAssignment.deleteSubs(idsToRemove, areaID);
    }

    public void handleAssignedIds(List<Integer> assignedIDs, Connection connection) {
        synchronized (assignedIDs) {
            for (Integer id : assignedIDs) {
                // see if it belongs to a different area than current
                // if yes - delete assignment from the old sub
                Integer areaID = CCSubAreaAssignment.getAreaIDForSubStation(id);
                if ((areaID != null || areaID > 0) && !areaID.equals(area.getPAObjectID())) {
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
    }
}
