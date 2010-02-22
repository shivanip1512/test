package com.cannontech.stars.dr.controlhistory.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.program.model.Program;
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
                                                          Program program,
                                                          int inventoryId,
                                                          YukonUserContext yukonUserContext){
        List<LoadGroup> loadGroupList = loadGroupDao.getByStarsProgramId(program.getProgramId());
       
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
                    controlHistoryEvent.getEndDate().after(lastControlHistoryEvent.getEndDate())) {
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
    
    private StarsLMControlHistory getEventsByGroup(final int customerAccountId,  Holder holder,
                                                   final ControlPeriod period, final YukonUserContext yukonUserContext) {
        StarsCtrlHistPeriod starsControlPeriod = StarsCtrlHistPeriod.valueOf(period.starsName());
        StarsLMControlHistory controlHistory = LMControlHistoryUtil.getStarsLMControlHistory(holder.groupId,
                                                                                             customerAccountId,
                                                                                             starsControlPeriod,
                                                                                             yukonUserContext.getTimeZone(),
                                                                                             yukonUserContext.getYukonUser());
        
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
        Date enrollmentStartDate = enrollmentDao.findCurrentEnrollmentStartDate(holder.inventoryId, holder.groupId);

        // The inventory is not currently enrolled.  Remove all the past control history.
        if (enrollmentStartDate == null){
            controlHistory.removeAllControlHistory();
        }
        
        List<com.cannontech.stars.xml.serialize.ControlHistory> removeControlHistoryList = Lists.newArrayList();
        for (int i = 0;  i < controlHistory.getControlHistoryCount(); i++) {
            com.cannontech.stars.xml.serialize.ControlHistory controlHistoryEntry = controlHistory.getControlHistory(i);
            Date controlHistoryStartDate = controlHistoryEntry.getStartDateTime();
            if (controlHistoryStartDate.before(enrollmentStartDate) ){
                Date controlHistoryEndDate = DateUtils.addSeconds(controlHistoryStartDate, controlHistoryEntry.getControlDuration());

                // Remove any control history that was before the hardware was ever enrolled
                if (controlHistoryEndDate.before(enrollmentStartDate)) {
                    removeControlHistoryList.add(controlHistoryEntry);
                    
                // Update any control history that was already started when the hardware was enrolled
                } else {
                    int newDuration = calculateNewDuration(controlHistoryEntry, enrollmentStartDate);
                    controlHistoryEntry.setStartDateTime(enrollmentStartDate);
                    controlHistoryEntry.setControlDuration(newDuration);
                }
            }
        }

        for (com.cannontech.stars.xml.serialize.ControlHistory removeControlHistoryEntry : removeControlHistoryList) {
            controlHistory.removeControlHistory(removeControlHistoryEntry);
        }
    }

    private int calculateNewDuration(com.cannontech.stars.xml.serialize.ControlHistory controlHistoryEntry,
                                      Date enrollmentStartDate) {
        int result;
        
        Long startTimeDifference = controlHistoryEntry.getStartDateTime().getTime() - enrollmentStartDate.getTime();
        result = controlHistoryEntry.getControlDuration() - startTimeDifference.intValue();
        
        return result;
    }

    @Override
    public List<ControlHistoryEvent> toEventList(StarsLMControlHistory controlHistory) {
        if (controlHistory == null) return Collections.emptyList();

        final List<ControlHistoryEvent> eventList = new ArrayList<ControlHistoryEvent>();

        for (int j = controlHistory.getControlHistoryCount() - 1; j >= 0; j--) {
            com.cannontech.stars.xml.serialize.ControlHistory history = controlHistory.getControlHistory(j);

            Date startDate = history.getStartDateTime();
            int durationInSeconds = history.getControlDuration();
            Date endDate = DateUtils.addSeconds(startDate, durationInSeconds);
            
            final ControlHistoryEvent event = new ControlHistoryEvent();
            event.setDuration(durationInSeconds);
            event.setStartDate(startDate);
            event.setEndDate(endDate);
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
