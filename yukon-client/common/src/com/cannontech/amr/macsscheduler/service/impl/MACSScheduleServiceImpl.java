package com.cannontech.amr.macsscheduler.service.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.macsscheduler.service.MACSScheduleService;
import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonResultSet;
import com.cannontech.message.macs.message.OverrideRequest;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IMACSConnection;

public class MACSScheduleServiceImpl implements MACSScheduleService<Schedule> {
    private IMACSConnection connection;
    @Autowired private FilterDao filterDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private final RowMapperWithBaseQuery<DisplayablePao> rowMapper =
            new AbstractRowMapperWithBaseQuery<DisplayablePao>() {

                @Override
                public SqlFragmentSource getBaseQuery() {
                    Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_SCHEDULE);
                    SqlStatementBuilder retVal = new SqlStatementBuilder();
                    retVal.append("SELECT paObjectId, paoName, type");
                    retVal.append("FROM yukonPAObject");
                    retVal.append("WHERE type").in(paoTypes);

                    return retVal;
                }
                
                @Override
                public DisplayablePao mapRow(YukonResultSet rs) throws SQLException {
                    PaoIdentifier paoId = rs.getPaoIdentifier("paobjectId", "type");
                    DisplayablePao retVal = new DisplayablePaoBase(paoId, rs.getString("paoName"));
                    
                    return retVal;
                }
            };

    public void start(final Schedule schedule, final Date startDate, final Date stopDate) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        Validate.notNull(startDate, "startDate cannot be null");
        Validate.notNull(stopDate, "stopDate cannot be null");

        if (startDate.after(stopDate)) {
            throw new java.lang.IllegalArgumentException("startDate cannot be after stopDate");
        }

        schedule.setUpdatingState(true);
        connection.sendStartStopSchedule(schedule, startDate, stopDate, OverrideRequest.OVERRIDE_START);
    }

    public void stop(final Schedule schedule, final Date stopDate) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        Validate.notNull(stopDate, "stopDate cannot be null");

        schedule.setUpdatingState(true);
        connection.sendStartStopSchedule(schedule, stopDate, stopDate, OverrideRequest.OVERRIDE_STOP);
    }
    
    public void enable(final Schedule schedule) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        
        schedule.setUpdatingState(true);
        connection.sendEnableDisableSchedule(schedule);
    }
    
    public void disable(final Schedule schedule) throws IOException {
        Validate.notNull(schedule, "schedule cannot be null");
        
        schedule.setUpdatingState(true);
        connection.sendEnableDisableSchedule(schedule);
    }

    public Schedule getById(final int scheduleId) throws NotFoundException,IOException {
        for (final Schedule schedule : connection.retrieveSchedules()) {
            if (schedule.getId() == scheduleId) return schedule;
        }
        throw new NotFoundException("Schedule with ID " + scheduleId + " not found!");
    }

    public List<Schedule> getAll() throws IOException {
        return Arrays.asList(connection.retrieveSchedules());
    }

    public void setConnection(final IMACSConnection connection) {
        this.connection = connection;
    }
    
    @Override
    public SearchResults<DisplayablePao> filterScripts(UiFilter<DisplayablePao> filter,
                                                       Comparator<DisplayablePao> sorter, 
                                                       int startIndex, int count,
                                                       YukonUserContext userContext) {

        SearchResults<DisplayablePao> searchResult =
            filterDao.filter(filter, sorter, startIndex, count, rowMapper);
        return searchResult;
    }
    
  

}
