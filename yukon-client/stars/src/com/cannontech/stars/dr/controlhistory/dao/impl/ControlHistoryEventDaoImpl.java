package com.cannontech.stars.dr.controlhistory.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.xml.serialize.ControlHistory;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class ControlHistoryEventDaoImpl implements ControlHistoryEventDao {
    private ProgramDao programDao;
    private ApplianceDao applianceDao;
    private InventoryBaseDao inventoryBaseDao;
    private EnrollmentDao enrollmentDao;

    protected class Holder {
        int groupId;
        int inventoryId;
    }
    
    @Override
    public Map<String, List<ControlHistoryEvent>> getEventsByProgram(final int customerAccountId, final int programId,
                                                                     final ControlPeriod period, final YukonUserContext yukonUserContext) {
        // Any appliance that has a groupId that does *not* belong in supportedGroupIdList will not used.
        final List<Integer> supportedGroupIdList = programDao.getGroupIdsByProgramId(programId);
        final List<Appliance> applianceList = applianceDao.getByAccountId(customerAccountId);

        final List<Holder> holderList = new ArrayList<Holder>();
        final Set<Integer> inventoryIds = new HashSet<Integer>();
        
        for (Appliance appliance : applianceList) {
            int applianceProgramId = appliance.getProgramId();
            if (applianceProgramId != programId) continue;

            Integer applianceGroupId = appliance.getGroupdId();
            if (!supportedGroupIdList.contains(applianceGroupId)) continue;

            Integer inventoryId = appliance.getInventoryId();
            inventoryIds.add(inventoryId);

            Holder holder = new Holder();
            holder.groupId = applianceGroupId;
            holder.inventoryId = inventoryId;
            holderList.add(holder);
        }

        final Map<Integer, InventoryBase> inventoryMap = inventoryBaseDao.getByIds(new ArrayList<Integer>(inventoryIds));
        
        final Map<String, List<ControlHistoryEvent>> displayNameToEventsMap = 
            new HashMap<String, List<ControlHistoryEvent>>(holderList.size());
        
        for (final Holder holder : holderList) {
            InventoryBase inventoryBase = inventoryMap.get(holder.inventoryId);
            String displayName = inventoryBaseDao.getDisplayName(inventoryBase);
            
            List<ControlHistoryEvent> eventList = displayNameToEventsMap.get(displayName);
            if (eventList == null) {
                eventList = new ArrayList<ControlHistoryEvent>();
                displayNameToEventsMap.put(displayName, eventList);
            }
            
            List<ControlHistoryEvent> eventsByGroupList = getEventsByGroup(customerAccountId,
                                                                           holder,
                                                                           period,
                                                                           yukonUserContext);
            eventList.addAll(eventsByGroupList);
        }

        return displayNameToEventsMap;
    }

    private List<ControlHistoryEvent> getEventsByGroup(final int customerAccountId, final Holder holder,
                                                       final ControlPeriod period, final YukonUserContext yukonUserContext) {

        StarsCtrlHistPeriod starsControlPeriod = StarsCtrlHistPeriod.valueOf(period.starsName());

        StarsLMControlHistory controlHistory = LMControlHistoryUtil.getStarsLMControlHistory(holder.groupId,
                                                                                             customerAccountId,
                                                                                             starsControlPeriod,
                                                                                             yukonUserContext.getTimeZone(),
                                                                                             yukonUserContext.getYukonUser());
        
        removeInvalidControlHistory(controlHistory, holder);
        
        List<ControlHistoryEvent> eventList = toEventList(controlHistory);
        return eventList;
    }

    /**
     * This method removes any invalid control history that was returned.
     * 
     * @param controlHistory
     * @param holder
     */
    private void removeInvalidControlHistory(StarsLMControlHistory controlHistory,
                                             Holder holder) {
        removeInvalidEnrollmentControllHistory(controlHistory, holder);
    }

    /**
     * This method removes any invalid control history in regards to enrollment.
     * 
     * @param controlHistory
     * @param holder
     */
    private void removeInvalidEnrollmentControllHistory(StarsLMControlHistory controlHistory,
                                                        Holder holder) {
        Date enrollmentStartDate = enrollmentDao.getEnrollmentStartDate(holder.inventoryId, holder.groupId);

        List<Integer> removeControlHistoryList = Lists.newArrayList();
        for (int i = 0;  i < controlHistory.getControlHistoryCount(); i++) {
            ControlHistory controlHistoryEntry = controlHistory.getControlHistory(i);
            Date controlHistoryStartDate = controlHistoryEntry.getStartDateTime();
            if (controlHistoryStartDate.before(enrollmentStartDate) ){
                Date controlHistoryEndDate = DateUtils.addSeconds(controlHistoryStartDate, controlHistoryEntry.getControlDuration());

                // Remove any control history that was before the hardware was ever enrolled
                if (controlHistoryEndDate.before(enrollmentStartDate)) {
                    removeControlHistoryList.add(i);
                    
                // Update any control history that was already started when the hardware was enrolled
                } else {
                    int newDuration = calculateNewDuration(controlHistoryEntry, enrollmentStartDate);
                    controlHistoryEntry.setStartDateTime(enrollmentStartDate);
                    controlHistoryEntry.setControlDuration(newDuration);
                }
            }
        }

        for (Integer removeIndex : removeControlHistoryList) {
            controlHistory.removeControlHistory(removeIndex);
        }
    }

    private int calculateNewDuration(ControlHistory controlHistoryEntry,
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
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }

    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
        this.enrollmentDao = enrollmentDao;
    }
}
