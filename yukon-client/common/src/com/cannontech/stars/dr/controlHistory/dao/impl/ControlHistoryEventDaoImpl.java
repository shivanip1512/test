package com.cannontech.stars.dr.controlHistory.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.dao.LmProgramGearHistory;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.dr.controlHistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlHistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlHistory.model.ControlPeriod;
import com.cannontech.stars.dr.controlHistory.model.ObservedControlHistory;
import com.cannontech.stars.dr.controlHistory.service.LmControlHistoryUtilService;
import com.cannontech.stars.xml.serialize.ControlHistoryEntry;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.user.YukonUserContext;

public class ControlHistoryEventDaoImpl implements ControlHistoryEventDao {
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private LmControlHistoryUtilService lmControlHistoryUtilService;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public ControlHistoryEvent getLastControlHistoryEntry(int accountId, int programId,
                                                          int loadGroupId, int inventoryId,
                                                          YukonUserContext userContext, boolean past) {
        LoadGroup loadGroup;
        try {
            loadGroup = loadGroupDao.getById(loadGroupId);
        } catch (NotFoundException e) {
            // The load group has been deleted, there isn't much we can do.
            return null;
        }

        ControlHistoryEvent lastControlHistoryEvent = null;
        StarsLMControlHistory starsLMControlHistory = getEventsByGroup(accountId, loadGroup.getLoadGroupId(), inventoryId, ControlPeriod.ALL, userContext, past);
        List<ControlHistoryEvent> controlHistoryEventList = toEventList(programId, starsLMControlHistory, userContext);
        for (ControlHistoryEvent controlHistoryEvent : controlHistoryEventList) {
            if (lastControlHistoryEvent == null ||
                controlHistoryEvent.getEndDate().isAfter(lastControlHistoryEvent.getEndDate())) {
                lastControlHistoryEvent = controlHistoryEvent;
            }
        }
 
        return lastControlHistoryEvent;
    }

    @Override
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
        if (controlHistory == null) {
            return Collections.emptyList();
        }
        
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
            
            eventList.add(event);
        }  

        return eventList;
    }

    @Override
    public String getHistoricalGearName(int programId, DateTime startDateTime, DateTime endDateTime, String defaultName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        List<String> name = null;
        
        sql.append("select pgh.GearName");
        sql.append("from LMProgramGearHistory pgh");
        sql.append("join LMProgramHistory ph on ph.LMProgramHistoryId = pgh.LMProgramHistoryId");
        sql.append("join LMProgramWebPublishing pwp on pwp.DeviceID = ph.ProgramId");
        sql.append("where pwp.ProgramId").eq(programId);
        sql.append("and pgh.EventTime").gte(startDateTime);
        sql.append("and pgh.EventTime").lt(endDateTime);
        sql.append("and (pgh.Action").eq_k(LmProgramGearHistory.GearAction.START);
        sql.append("or pgh.Action").eq_k(LmProgramGearHistory.GearAction.GEAR_CHANGE).append(")");
        
        name = yukonJdbcTemplate.query(sql, TypeRowMapper.STRING);
        
        if(name.size() == 0){
            // Didn't find gear name within interval. Try to find the most recent gear name prior to startDateTime.
            sql = new SqlStatementBuilder();
            sql.append("select GearName from (");
            sql.append("    select pgh.GearName, row_number() over (order by pgh.EventTime desc) as RowNumber");
            sql.append("    from LMProgramGearHistory pgh");
            sql.append("    join LMProgramHistory ph on ph.LMProgramHistoryId = pgh.LMProgramHistoryId");
            sql.append("    join LMProgramWebPublishing pwp on pwp.DeviceID = ph.ProgramId");
            sql.append("    where pwp.ProgramId").eq(programId);
            sql.append("    and pgh.EventTime").lt(startDateTime).append(") T");
            sql.append("where T.RowNumber = 1");
            
            name = yukonJdbcTemplate.query(sql, TypeRowMapper.STRING);
        }
        return name.size() > 0 ? name.get(0) : defaultName;
    }

    protected static class Holder {
        int accountId;
        int inventoryId;
        int groupId;
        int programId;
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + accountId;
            result = prime * result + groupId;
            result = prime * result + inventoryId;
            result = prime * result + programId;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Holder other = (Holder) obj;
            if (accountId != other.accountId)
                return false;
            if (groupId != other.groupId)
                return false;
            if (inventoryId != other.inventoryId)
                return false;
            if (programId != other.programId)
                return false;
            return true;
        }
    }
}