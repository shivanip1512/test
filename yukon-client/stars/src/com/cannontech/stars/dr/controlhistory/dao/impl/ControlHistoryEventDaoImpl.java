package com.cannontech.stars.dr.controlhistory.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class ControlHistoryEventDaoImpl implements ControlHistoryEventDao {
    private EnrollmentDao enrollmentDao;
    private LoadGroupDao loadGroupDao;

    protected static class Holder {
        int groupId;
        int inventoryId;
    }
    
    public ControlHistoryEvent getLastControlHistoryEntry(int accountId,
                                                          int programId,
                                                          int inventoryId,
                                                          YukonUserContext yukonUserContext){
        List<LoadGroup> loadGroupList = loadGroupDao.getByStarsProgramId(programId);
       
        ControlHistoryEvent lastControlHistoryEvent = null;
        for (LoadGroup loadGroup : loadGroupList) {
            StarsLMControlHistory starsLMControlHistory = 
                getEventsByGroup(accountId, loadGroup.getLoadGroupId(), inventoryId, ControlPeriod.ALL, yukonUserContext);
            
            Holder holder = new Holder();
            holder.inventoryId = inventoryId;
            holder.groupId = loadGroup.getLoadGroupId();

            removeInvalidEnrollmentControlHistory(starsLMControlHistory, holder);
            
            List<ControlHistoryEvent> controlHistoryEventList = toEventList(starsLMControlHistory);
            for (ControlHistoryEvent controlHistoryEvent : controlHistoryEventList) {
                if (lastControlHistoryEvent == null ||
                    controlHistoryEvent.getEndDate().isAfter(lastControlHistoryEvent.getEndDate())) {
                    lastControlHistoryEvent = controlHistoryEvent;
                }
            }
        }
 
        return lastControlHistoryEvent;
    }

    
    
    public StarsLMControlHistory getEventsByGroup(final int customerAccountId,  int lmGroupId, int inventoryId,
                                                  final ControlPeriod period, final YukonUserContext yukonUserContext) {

        Holder holder = new Holder();
        holder.groupId = lmGroupId;
        holder.inventoryId = inventoryId;
    
        return getEventsByGroup(customerAccountId, holder, period, yukonUserContext);
    }
    
    private StarsLMControlHistory getEventsByGroup(final int customerAccountId, 
                                                    Holder holder, 
                                                    final ControlPeriod period, 
                                                    final YukonUserContext yukonUserContext) {
        StarsCtrlHistPeriod starsControlPeriod = StarsCtrlHistPeriod.valueOf(period.starsName());
        StarsLMControlHistory controlHistory = 
            LMControlHistoryUtil.getStarsLMControlHistory(holder.groupId,
                                                          holder.inventoryId,
                                                          customerAccountId,
                                                          starsControlPeriod,
                                                          yukonUserContext);
        
        removeInvalidEnrollmentControlHistory(controlHistory, holder);

        return controlHistory;
    }

    /**
     * This method removes any invalid control history in regards to enrollment.
     * 
     * @param controlHistory
     * @param holder
     */
    public void removeInvalidEnrollmentControlHistory(StarsLMControlHistory controlHistory,
                                                      int inventoryId,
                                                      int groupId) {
    	
    	Holder holder = new Holder();
    	holder.inventoryId = inventoryId;
    	holder.groupId = groupId;
    	
    	removeInvalidEnrollmentControlHistory(controlHistory, holder);
    }

    /**
     * This method removes any invalid control history in regards to enrollment.
     * 
     * @param controlHistory
     * @param holder
     */
    private void removeInvalidEnrollmentControlHistory(StarsLMControlHistory controlHistory,
                                                         Holder holder) {
        ReadableInstant enrollmentStartInstant = 
            enrollmentDao.findCurrentEnrollmentStartDate(holder.inventoryId, holder.groupId);

        // The inventory is not currently enrolled.  Remove all the past control history.
        if (enrollmentStartInstant == null){
            controlHistory.removeAllControlHistory();
        }
        
        List<com.cannontech.stars.xml.serialize.ControlHistory> removeControlHistoryList = Lists.newArrayList();
        for (int i = 0;  i < controlHistory.getControlHistoryCount(); i++) {
            com.cannontech.stars.xml.serialize.ControlHistory controlHistoryEntry = controlHistory.getControlHistory(i);
            DateTime controlHistoryStartDateTime = controlHistoryEntry.getStartDateTime();
            if (controlHistoryStartDateTime.isBefore(enrollmentStartInstant) ){
                DateTime controlHistoryEndDateTime = 
                    controlHistoryStartDateTime.plus(controlHistoryEntry.getControlDuration());

                // Remove any control history that was before the hardware was ever enrolled
                if (controlHistoryEndDateTime.isBefore(enrollmentStartInstant)) {
                    removeControlHistoryList.add(controlHistoryEntry);
                    
                // Update any control history that was already started when the hardware was enrolled
                } else {
                    Duration newDuration = calculateNewDuration(controlHistoryEntry, enrollmentStartInstant);
                    controlHistoryEntry.setStartDateTime(new DateTime(enrollmentStartInstant,
                                                                      controlHistoryStartDateTime.getZone()));
                    controlHistoryEntry.setControlDuration(newDuration);
                }
            }
        }

        for (com.cannontech.stars.xml.serialize.ControlHistory removeControlHistoryEntry : removeControlHistoryList) {
            controlHistory.removeControlHistory(removeControlHistoryEntry);
        }
    }

    private Duration calculateNewDuration(com.cannontech.stars.xml.serialize.ControlHistory controlHistoryEntry,
                                           ReadableInstant enrollmentStartDate) {
        
        Duration startTimeDifference = 
            new Duration(enrollmentStartDate, controlHistoryEntry.getStartDateTime());
        
        return controlHistoryEntry.getControlDuration().minus(startTimeDifference);

    }

    @Override
    public List<ControlHistoryEvent> toEventList(StarsLMControlHistory controlHistory) {
        if (controlHistory == null) return Collections.emptyList();

        final List<ControlHistoryEvent> eventList = new ArrayList<ControlHistoryEvent>();

        for (int j = controlHistory.getControlHistoryCount() - 1; j >= 0; j--) {
            com.cannontech.stars.xml.serialize.ControlHistory history = controlHistory.getControlHistory(j);

            DateTime startDateTime = history.getStartDateTime();
            DateTime endDateTime = startDateTime.plus(history.getControlDuration());
            
            final ControlHistoryEvent event = new ControlHistoryEvent();
            event.setDuration(history.getControlDuration());
            event.setStartDate(startDateTime);
            event.setEndDate(endDateTime);
            eventList.add(event);
        }  

        return eventList;
    }

    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
        this.enrollmentDao = enrollmentDao;
    }
    
    @Autowired
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }

}
