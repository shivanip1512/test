package com.cannontech.stars.dr.controlhistory.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.xml.serialize.ControlHistory;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class ControlHistoryEventDaoImpl implements ControlHistoryEventDao {
    private EnrollmentDao enrollmentDao;
    private LoadGroupDao loadGroupDao;
    private YukonJdbcTemplate yukonJdbcTemplate;

    protected static class Holder {
        int groupId;
        int inventoryId;
    }
    
    public ControlHistoryEvent getLastControlHistoryEntry(int accountId, int programId, int inventoryId, YukonUserContext userContext, boolean past){
        List<LoadGroup> loadGroupList = loadGroupDao.getByStarsProgramId(programId);
       
        ControlHistoryEvent lastControlHistoryEvent = null;
        for (LoadGroup loadGroup : loadGroupList) {
            StarsLMControlHistory starsLMControlHistory = getEventsByGroup(accountId, loadGroup.getLoadGroupId(), inventoryId, ControlPeriod.ALL, userContext, past);
            
            Holder holder = new Holder();
            holder.inventoryId = inventoryId;
            holder.groupId = loadGroup.getLoadGroupId();

            removeInvalidEnrollmentControlHistory(starsLMControlHistory, holder, past);
            
            List<ControlHistoryEvent> controlHistoryEventList = toEventList(programId, starsLMControlHistory);
            for (ControlHistoryEvent controlHistoryEvent : controlHistoryEventList) {
                if (lastControlHistoryEvent == null ||
                    controlHistoryEvent.getEndDate().isAfter(lastControlHistoryEvent.getEndDate())) {
                    lastControlHistoryEvent = controlHistoryEvent;
                }
            }
        }
 
        return lastControlHistoryEvent;
    }

    
    
    public StarsLMControlHistory getEventsByGroup(int customerAccountId, int lmGroupId, int inventoryId, ControlPeriod period, YukonUserContext userContext, boolean past) {

        Holder holder = new Holder();
        holder.groupId = lmGroupId;
        holder.inventoryId = inventoryId;
    
        return getEventsByGroup(customerAccountId, holder, period, userContext, past);
    }
    
    private StarsLMControlHistory getEventsByGroup(int accountId, Holder holder, ControlPeriod period, YukonUserContext userContext, boolean past) {
        StarsCtrlHistPeriod starsControlPeriod = StarsCtrlHistPeriod.valueOf(period.starsName());
        StarsLMControlHistory controlHistory = LMControlHistoryUtil.getStarsLMControlHistory(holder.groupId,
                                                          holder.inventoryId,
                                                          accountId,
                                                          starsControlPeriod,
                                                          userContext);
        
        removeInvalidEnrollmentControlHistory(controlHistory, holder, past);

        return controlHistory;
    }

    /**
     * This method removes any invalid control history in regards to enrollment.
     * @param controlHistory
     * @param holder
     */
    public void removeInvalidEnrollmentControlHistory(StarsLMControlHistory controlHistory, int inventoryId, int groupId, boolean past) {
    	Holder holder = new Holder();
    	holder.inventoryId = inventoryId;
    	holder.groupId = groupId;
    	
    	removeInvalidEnrollmentControlHistory(controlHistory, holder, past);
    }
    
    /**
     * This method removes any invalid control history in regards to enrollment. If past is true,
     * control history will be filtered for only past enrollements. If past is false, control history
     * will be filtered for only current enrollments.
     * @param controlHistory The control history to filter.
     * @param holder The holder of inventory and load group.
     * @param past If true, calculates past enrollment control history. If false, calculates current
     * control history.
     */
    private void removeInvalidEnrollmentControlHistory(StarsLMControlHistory controlHistory, Holder holder, boolean past) {
        ReadableInstant currentEnrollmentStart = enrollmentDao.findCurrentEnrollmentStartDate(holder.inventoryId, holder.groupId);
        List<ControlHistory> removeControlHistoryList = Lists.newArrayList();

        /* The inventory is not currently enrolled.  Remove all the past control history when filtering for current control history. */
        if (currentEnrollmentStart == null && !past){
            controlHistory.removeAllControlHistory();
        }
        
        if (!past) {
            /* Remove all control history that did NOT happen during the current enrollment. */
            for (int i = 0;  i < controlHistory.getControlHistoryCount(); i++) {
                ControlHistory controlHistoryEntry = controlHistory.getControlHistory(i);
                Instant controlHistoryStart = controlHistoryEntry.getStartInstant();
                if (controlHistoryStart.isBefore(currentEnrollmentStart) ){
                    Instant controlHistoryEnd = controlHistoryStart.plus(controlHistoryEntry.getControlDuration());
    
                    // Remove any control history that was before the hardware was ever enrolled
                    if (controlHistoryEnd.isBefore(currentEnrollmentStart)) {
                        removeControlHistoryList.add(controlHistoryEntry);
                        
                    // Update any control history that was already started when the hardware was enrolled
                    } else {
                        Duration newDuration = calculateNewDuration(controlHistoryEntry, currentEnrollmentStart);
                        controlHistoryEntry.setStartInstant(new Instant(currentEnrollmentStart));
                        controlHistoryEntry.setControlDuration(newDuration);
                    }
                }
            }
        } else {
            if(currentEnrollmentStart == null) {
                /* Not currently enrolled, means no current control history to remove. */
                return;
            } else {
                /* Remove all control history that DID happen during the current enrollment. */
                for (int i = 0;  i < controlHistory.getControlHistoryCount(); i++) {
                    ControlHistory controlHistoryEntry = controlHistory.getControlHistory(i);
                    Instant controlHistoryStart = controlHistoryEntry.getStartInstant();
                    
                    if(controlHistoryStart.isAfter(currentEnrollmentStart)) {
                        /* Control started during the current enrollment, remove it. */
                        removeControlHistoryList.add(controlHistoryEntry);
                    }
                }
            }
        }

        for (ControlHistory removeControlHistoryEntry : removeControlHistoryList) {
            controlHistory.removeControlHistory(removeControlHistoryEntry);
        }
    }

    private Duration calculateNewDuration(ControlHistory controlHistoryEntry,
                                           ReadableInstant enrollmentStartDate) {
        
        Duration startTimeDifference = 
            new Duration(enrollmentStartDate, controlHistoryEntry.getStartInstant());
        
        return controlHistoryEntry.getControlDuration().minus(startTimeDifference);

    }

    @Override
    public List<ControlHistoryEvent> toEventList(Integer programId, StarsLMControlHistory controlHistory) {
        if (controlHistory == null) return Collections.emptyList();

        final List<ControlHistoryEvent> eventList = new ArrayList<ControlHistoryEvent>();

        for (int j = controlHistory.getControlHistoryCount() - 1; j >= 0; j--) {
            ControlHistory history = controlHistory.getControlHistory(j);

            Instant startDateTime = history.getStartInstant();
            Instant endDateTime = startDateTime.plus(history.getControlDuration());
            
            final ControlHistoryEvent event = new ControlHistoryEvent();
            event.setDuration(history.getControlDuration());
            event.setStartDate(startDateTime);
            event.setEndDate(endDateTime);
            
            String gears;
            if(programId != null) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT DISTINCT gh.GearName");
                sql.append("FROM LMProgramGearHistory gh");
                sql.append("  JOIN LMProgramHistory h on h.LMProgramHistoryId = gh.LMProgramHistoryId");
                sql.append("  JOIN LMProgramWebPublishing wp on wp.DeviceID = h.ProgramId");
                sql.append("WHERE wp.ProgramId").eq(programId);
                sql.append("  AND gh.EventTime").gte(startDateTime);
                sql.append("  AND gh.EventTime").lt(endDateTime);
                sql.append("  AND (gh.Action").eq("Start").append("OR gh.Action").eq("Gear Change").append(")");
                
                List<String> gearNames = yukonJdbcTemplate.query(sql, new StringRowMapper());
                
                if(gearNames.isEmpty()){
                    sql = new SqlStatementBuilder();
                    sql.append("SELECT gh.GearName");
                    sql.append("FROM LMProgramGearHistory gh");
                    sql.append("  JOIN LMProgramHistory h on h.LMProgramHistoryId = gh.LMProgramHistoryId");
                    sql.append("  JOIN LMProgramWebPublishing wp on wp.DeviceID = h.ProgramId");
                    sql.append("WHERE wp.ProgramId").eq(programId);
                    sql.append("  AND gh.EventTime").lt(startDateTime);
                    sql.append("ORDER BY gh.EventTime desc");
                    
                    gearNames = yukonJdbcTemplate.query(sql, new StringRowMapper());
                    gears = gearNames.isEmpty() ? "NA" : gearNames.get(0);
                } else {
                    gears = StringUtils.join(gearNames, ",");  
                }
            } else {
                gears = "NA";
            }
            
            event.setGears(gears);
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
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}