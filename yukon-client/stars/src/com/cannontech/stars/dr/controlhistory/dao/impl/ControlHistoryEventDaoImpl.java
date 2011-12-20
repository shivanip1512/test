package com.cannontech.stars.dr.controlhistory.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.stars.dr.controlhistory.model.ObservedControlHistory;
import com.cannontech.stars.dr.controlhistory.service.LmControlHistoryUtilService;
import com.cannontech.stars.xml.serialize.ControlHistoryEntry;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.user.YukonUserContext;

public class ControlHistoryEventDaoImpl implements ControlHistoryEventDao {
    private LoadGroupDao loadGroupDao;
    private LmControlHistoryUtilService lmControlHistoryUtilService;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    protected static class Holder {
        int accountId;
        int inventoryId;
        int groupId;
        int programId;
    }
    
    public ControlHistoryEvent getLastControlHistoryEntry(int accountId, int programId, int inventoryId, YukonUserContext userContext, boolean past){
        List<LoadGroup> loadGroupList = loadGroupDao.getByStarsProgramId(programId);
       
        ControlHistoryEvent lastControlHistoryEvent = null;
        for (LoadGroup loadGroup : loadGroupList) {
            StarsLMControlHistory starsLMControlHistory = getEventsByGroup(accountId, loadGroup.getLoadGroupId(), inventoryId, ControlPeriod.ALL, userContext, past);
            
            Holder holder = new Holder();
            holder.accountId = accountId;
            holder.inventoryId = inventoryId;
            holder.programId = programId;
            holder.groupId = loadGroup.getLoadGroupId();

            List<ControlHistoryEvent> controlHistoryEventList = toEventList(programId, starsLMControlHistory, userContext);
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
                                                  final ControlPeriod period, final YukonUserContext yukonUserContext, boolean past) {

        StarsCtrlHistPeriod starsControlPeriod = StarsCtrlHistPeriod.valueOf(period.starsName());
        ObservedControlHistory observedControlHistory = 
            lmControlHistoryUtilService.getObservedControlHistory(lmGroupId, inventoryId, customerAccountId,
                                                          starsControlPeriod, yukonUserContext.getJodaTimeZone(),
                                                          yukonUserContext.getYukonUser(), past);
        
        StarsLMControlHistory starsLmControlHistory = 
            lmControlHistoryUtilService.getStarsLmControlHistory(observedControlHistory, starsControlPeriod, yukonUserContext.getJodaTimeZone());

        return starsLmControlHistory;
    }

    @Override
    public List<ControlHistoryEvent> toEventList(Integer programId, StarsLMControlHistory controlHistory, YukonUserContext userContext) {
        if (controlHistory == null) return Collections.emptyList();

        final List<ControlHistoryEvent> eventList = new ArrayList<ControlHistoryEvent>();

        for (int j = controlHistory.getControlHistoryCount() - 1; j >= 0; j--) {
            ControlHistoryEntry history = controlHistory.getControlHistory(j);

            Instant startDateTime = history.getStartInstant();
            Instant endDateTime = startDateTime.plus(history.getControlDuration());
            
            final ControlHistoryEvent event = new ControlHistoryEvent();
            event.setDuration(history.getControlDuration());
            event.setStartDate(startDateTime);
            event.setEndDate(endDateTime);
            event.setControlling(history.isCurrentlyControlling());
            
            String gears;
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String na = messageSourceAccessor.getMessage("yukon.web.components.controlHistoryEvent.na");
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
                    gears = gearNames.isEmpty() ? na : gearNames.get(0);
                } else {
                    gears = StringUtils.join(gearNames, ",");  
                }
            } else {
                gears = na;
            }
            
            event.setGears(gears);
            eventList.add(event);
        }  

        return eventList;
    }

    @Autowired
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    @Autowired
    public void setLmControlHistoryUtilService(LmControlHistoryUtilService lmControlHistoryUtilService) {
        this.lmControlHistoryUtilService = lmControlHistoryUtilService;
    }
}