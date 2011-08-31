package com.cannontech.web.editor.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.capcontrol.CapControlSpecialArea;
import com.cannontech.database.db.capcontrol.CCSubSpecialAreaAssignment;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.editor.data.CBCSpecialAreaData;
import com.cannontech.web.util.CBCDBUtil;

public class CBCSpecialAreaDataModel extends EditorDataModelImpl {
    CapControlSpecialArea area;
    SubstationDao substationDao = YukonSpringHook.getBean("ccSubstationDao", SubstationDao.class);
    PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
    
    List<CBCSpecialAreaData> assignedSubstations = new ArrayList<CBCSpecialAreaData>();
    List<CBCSpecialAreaData> unassingedSubstations = new ArrayList<CBCSpecialAreaData>();

    public boolean isAreaNone() {
        return area.getPAOName().equalsIgnoreCase("(none)");
    }

    public CBCSpecialAreaDataModel(CapControlSpecialArea area) {
        super(area);
        this.area = area;
        init();
    }

    private void init() {
        List<CCSubSpecialAreaAssignment> allAreaSubs = CCSubSpecialAreaAssignment.getAllSpecialAreaSubs(area.getPAObjectID());
        synchronized (allAreaSubs) {
            for (CCSubSpecialAreaAssignment assignment : allAreaSubs) {
                Integer subId = assignment.getSubstationBusID();
                String subName = paoDao.getYukonPAOName(subId);
                Integer displayOrder = assignment.getDisplayOrder();
                CBCSpecialAreaData data = new CBCSpecialAreaData(subId, subName, displayOrder + 1);
                assignedSubstations.add(data);
            }
        }
        
        List<Integer> availableSubs = substationDao.getAllSpecialAreaUnassignedSubstationIds(area.getPAObjectID());
        synchronized (availableSubs) {
            for(Integer subId : availableSubs) {
                String subName = paoDao.getYukonPAOName(subId);
                Integer displayOrder = 0;
                CBCSpecialAreaData data = new CBCSpecialAreaData(subId, subName, displayOrder);
                unassingedSubstations.add(data);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void add() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        Map paramMap = context.getExternalContext().getRequestParameterMap();
        Integer subId = new Integer((String) paramMap.get("id")).intValue();
        
        CBCSpecialAreaData data = new CBCSpecialAreaData(subId, paoDao.getYukonPAOName(subId), 1000);
        assignedSubstations.add(data);
        
        reOrderAssignedSubs(getAssigned());
        
        Iterator<CBCSpecialAreaData> iter = unassingedSubstations.iterator();
        while (iter.hasNext()) {
            CBCSpecialAreaData current = iter.next();
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
        
        Iterator<CBCSpecialAreaData> iter = assignedSubstations.iterator();
        while(iter.hasNext()) {
            CBCSpecialAreaData current = iter.next();
            if(current.getSubID().intValue() == subId.intValue()) {
                getUnassigned().add(current);
                iter.remove();
                break;
            }
        }
        reOrderAssignedSubs(getAssigned());
    }
    
    private void reOrderAssignedSubs(List<CBCSpecialAreaData> list) {
        synchronized (list) {
            for(int i = 0; i < list.size(); i++) {
                CBCSpecialAreaData data = list.get(i);
                data.setDisplayOrder(i+1);
            }
        }
    }

    public List<CBCSpecialAreaData> getAssigned() {
        Collections.sort(assignedSubstations, new Comparator<CBCSpecialAreaData>() {
            public int compare(CBCSpecialAreaData o1, CBCSpecialAreaData o2) {
                Integer intA = o1.getDisplayOrder();
                Integer intB = o2.getDisplayOrder();

                return intA.compareTo(intB);
            }
        });
        return assignedSubstations;
    }

    public List<CBCSpecialAreaData> getUnassigned() {
        return unassingedSubstations;
    }

    public void updateDataModel() {
        List<Integer> unassignedIds = CBCSpecialAreaData.toIntegerList(getUnassigned());
        Connection connection = CBCDBUtil.getConnection();
        handleUnassignedIds(unassignedIds, connection);
        assignNewSubs(getAssigned(), connection);
        CBCDBUtil.closeConnection(connection);
    }

    public void assignNewSubs(List<CBCSpecialAreaData> assignedSubs, Connection connection) {
        ArrayList<CCSubSpecialAreaAssignment> newAssignments = new ArrayList<CCSubSpecialAreaAssignment>();
        synchronized (newAssignments) {
            for(CBCSpecialAreaData data : assignedSubs) {
                CCSubSpecialAreaAssignment assignment = new CCSubSpecialAreaAssignment(area.getPAObjectID(), data.getSubID(), data.getDisplayOrder() - 1);
                newAssignments.add(assignment);
            }
            area.setSpecialAreaSubs(newAssignments);
        }
    }

    public void handleUnassignedIds(List<Integer> unassignedIDs, Connection connection) {
        // see if it belongs to the current area
        // delete it from sub assignment

        Integer areaID = area.getPAObjectID();
        List<CCSubSpecialAreaAssignment> childList = CCSubSpecialAreaAssignment.getAllSpecialAreaSubs(areaID);
        ArrayList<Integer> idsToRemove = CCSubSpecialAreaAssignment.getAsIntegerList(childList);
        idsToRemove.retainAll(unassignedIDs);
        CCSubSpecialAreaAssignment.deleteSubs(idsToRemove, areaID);
    }

}