package com.cannontech.dr.controlarea.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.controlarea.filter.ForProgramFilter;
import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.dr.controlarea.model.ControlAreaDisplayField;
import com.cannontech.dr.controlarea.model.ControlAreaTrigger;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.util.Message;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

public class ControlAreaServiceImpl implements ControlAreaService {
    private ControlAreaDao controlAreaDao;
    private LoadControlClientConnection loadControlClientConnection;
    private FilterService filterService;

    private static class TriggerRowMapper implements RowMapperWithBaseQuery<ControlAreaTrigger> {
        Map<Integer, List<ControlAreaTrigger>> triggersByControlAreaId = Maps.newHashMap();

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT deviceId, triggerNumber, triggerType FROM lmControlAreaTrigger");
            return retVal;
        }

        @Override
        public SqlFragmentSource getOrderBy() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("ORDER BY deviceId, triggerNumber");
            return retVal;
        }

        @Override
        public boolean needsWhere() {
            return true;
        }

        @Override
        public ControlAreaTrigger mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            int controlAreaId = rs.getInt("deviceId");
            ControlAreaTrigger retVal = new ControlAreaTrigger(controlAreaId,
                                                               rs.getInt("triggerNumber"),
                                                               rs.getString("triggerType"));
            List<ControlAreaTrigger> triggers = triggersByControlAreaId.get(controlAreaId);
            if (triggers == null) {
                triggers = Lists.newArrayList();
                triggersByControlAreaId.put(controlAreaId, triggers);
            }
            triggers.add(retVal);

            return retVal;
        }
    }

    private static class ControlAreaRowMapper extends AbstractRowMapperWithBaseQuery<ControlArea> {
        Map<Integer, List<ControlAreaTrigger>> triggersByControlAreaId;

        ControlAreaRowMapper(Map<Integer, List<ControlAreaTrigger>> triggersByControlAreaId) {
            this.triggersByControlAreaId = triggersByControlAreaId;
        }

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT paObjectId, paoName FROM yukonPAObject"
                + " WHERE type = ");
            retVal.appendArgument(PaoType.LM_CONTROL_AREA.getDbString());
            return retVal;
        }

        @Override
        public ControlArea mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            int controlAreaId = rs.getInt("paObjectId");
            PaoIdentifier paoId = new PaoIdentifier(controlAreaId,
                                                    PaoType.LM_CONTROL_AREA);
            ControlArea retVal = new ControlArea(paoId, rs.getString("paoName"));
            retVal.setTriggers(triggersByControlAreaId.get(controlAreaId));
            return retVal;
        }
    }

    // We can use this class to wrap filters made for a control area.
    // Then, we can query specifically for triggers that are specific to the
    // control areas we are filtering on.
    private class TriggerFilter implements UiFilter<ControlAreaTrigger> {
        UiFilter<DisplayablePao> wrappedControlAreaFilter;

        class WrappedSqlFilter implements SqlFilter {
            SqlFilter wrapped;

            WrappedSqlFilter(SqlFilter wrapped) {
                this.wrapped = wrapped;
            }

            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("deviceId IN (SELECT paObjectId FROM yukonPAObject WHERE type = ");
                retVal.appendArgument(PaoType.LM_CONTROL_AREA.getDbString());
                retVal.append("AND");
                retVal.append(wrapped.getWhereClauseFragment());
                retVal.append(")");
                return retVal;
            }
        }

        TriggerFilter(UiFilter<DisplayablePao> wrappedControlAreaFiter) {
            this.wrappedControlAreaFilter = wrappedControlAreaFiter;
        }

        @Override
        public Iterable<PostProcessingFilter<ControlAreaTrigger>> getPostProcessingFilters() {
            // this is only meant as a SQL filter
            return null;
        }

        @Override
        public Iterable<SqlFilter> getSqlFilters() {
            Iterable<SqlFilter> toBeWrapped = wrappedControlAreaFilter.getSqlFilters();
            if (toBeWrapped == null || !toBeWrapped.iterator().hasNext()) {
                return null;
            }

            List<SqlFilter> retVal = new ArrayList<SqlFilter>();
            for (SqlFilter wrapped : toBeWrapped) {
                retVal.add(new WrappedSqlFilter(wrapped));
            }

            return retVal;
        }
    }

    @Override
    public LMControlArea map(DisplayablePao from) throws ObjectMappingException {
        DatedObject<LMControlArea> datedControlArea =
            loadControlClientConnection.getDatedControlArea(from.getPaoIdentifier().getPaoId());
        return datedControlArea == null ? null : datedControlArea.getObject();
    }

    @Override
    public DatedObject<LMControlArea> getDatedControlArea(int controlAreaId) {
        return loadControlClientConnection.getDatedControlArea(controlAreaId);
    }

    @Override
    public ControlArea findControlAreaForProgram(YukonUserContext userContext, int programId) {
        UiFilter<DisplayablePao> filter = new ForProgramFilter(programId);

        SearchResult<ControlArea> searchResult =
            filterControlAreas(userContext, filter, null, 0, Integer.MAX_VALUE);

        if (searchResult.getHitCount() > 0) {
            return searchResult.getResultList().get(0);
        }
        return null;
    }

    @Override
    public SearchResult<ControlArea> filterControlAreas(
            YukonUserContext userContext, UiFilter<DisplayablePao> filter,
            Comparator<DisplayablePao> sorter, int startIndex, int count) {

        UiFilter<ControlAreaTrigger> triggerFilter = null;
        if (filter != null) {
            triggerFilter = new TriggerFilter(filter);
        }

        // Need to retrieve all triggers as the request is for Control area records;
        // Control area may have 0 or more triggers; so no easy way to correlate the two
        // Also, note that TriggerRowMapper caches all the mapped triggers by controlAreaId; 
        // startIndex, count have little value as the plain returned results are not used
        TriggerRowMapper triggerRowMapper = new TriggerRowMapper();
        filterService.filter(triggerFilter, null, 0, Integer.MAX_VALUE,
                             triggerRowMapper);

        Comparator<DisplayablePao> defaultSorter =
            ControlAreaDisplayField.NAME.getSorter(this, userContext, false);
        if (sorter == null) {
            sorter = defaultSorter;
        } else {
            sorter = Ordering.from(sorter).compound(defaultSorter);
        }
        SearchResult<ControlArea> searchResult =
            filterService.filter(filter, sorter, startIndex, count,
                                 new ControlAreaRowMapper(triggerRowMapper.triggersByControlAreaId));

        return searchResult;
    }
    
    @Override
    public void resetPeak(int controlAreaId) {
        
        ControlArea controlArea = this.getControlArea(controlAreaId);
        
        Multi<LMCommand> multi = new Multi<LMCommand>();
        Vector<LMCommand> commandVector = multi.getVector();
        for(ControlAreaTrigger trigger : controlArea.getTriggers()) {
            
            LMCommand command = new LMCommand(LMCommand.RESET_PEAK_POINT_VALUE,
                                              controlAreaId,
                                              trigger.getTriggerNumber(), 
                                              0.0);
            commandVector.add(command);
        }
        loadControlClientConnection.write(multi);
        
    }
    
    @Override
    public void setEnabled(int controlAreaId, boolean isEnabled) {
        int loadControlCommand = isEnabled ? LMCommand.ENABLE_CONTROL_AREA
                : LMCommand.DISABLE_CONTROL_AREA;
        Message msg = new LMCommand(loadControlCommand, controlAreaId, 0, 0.0);
        loadControlClientConnection.write(msg);
    }
    
    @Override
    public void changeTriggers(int controlAreaId, Double threshold1, Double offset1, 
                               Double threshold2, Double offset2) {

        DatedObject<LMControlArea> datedControlArea = 
            loadControlClientConnection.getDatedControlArea(controlAreaId);
        
        LMControlArea controlArea = datedControlArea.getObject();
        
        Multi<LMCommand> multi = new Multi<LMCommand>();
        Vector<LMCommand> commandVector = multi.getVector();
        
        Vector<LMControlAreaTrigger> triggerVector = controlArea.getTriggerVector();
        
        // Add trigger change commands for trigger 1 if it exists
        LMControlAreaTrigger trigger1 = triggerVector.get(0);
        if(trigger1 != null) {
            List<LMCommand> triggerCommands = 
                this.getTriggerCommands(controlAreaId, trigger1, threshold1, offset1);
            commandVector.addAll(triggerCommands);
        }

        // Add trigger change commands for trigger 2 if it exists
        LMControlAreaTrigger trigger2 = triggerVector.get(1);
        if(trigger2 != null) {
            List<LMCommand> triggerCommands = 
                this.getTriggerCommands(controlAreaId, trigger2, threshold2, offset2);
            commandVector.addAll(triggerCommands);
        }

        loadControlClientConnection.write(multi);
    }
    
    @Override
    public void changeTimeWindow(int controlAreaId, Integer startSeconds, Integer stopSeconds) {
        
        Multi<LMCommand> multi = new Multi<LMCommand>();
        
        Vector<LMCommand> commandVector = multi.getVector();
        if(startSeconds != null){
            //send a message to the server telling it to change the START time
            LMCommand startDateCommand = new LMCommand(LMCommand.CHANGE_CURRENT_START_TIME,
                                                       controlAreaId,
                                                       0, 
                                                       startSeconds);
            commandVector.add(startDateCommand);
        }
        
        if(stopSeconds != null){
            //send a message to the server telling it to change the STOP time
            LMCommand stopDateCommand = new LMCommand(LMCommand.CHANGE_CURRENT_STOP_TIME,
                                                      controlAreaId,
                                                      0,
                                                      stopSeconds);
            commandVector.add(stopDateCommand);
        }
        
        loadControlClientConnection.write(multi);
    }
    
    @Override
    public boolean isEnabled(int controlAreaId) {
        LMControlArea controlArea = this.loadControlClientConnection.getControlArea(controlAreaId);
        Boolean disableFlag = controlArea.getDisableFlag();
        return !disableFlag;
    }

    @Override
    public ControlArea getControlArea(int controlAreaId) {
        return controlAreaDao.getControlArea(controlAreaId);
    }
    
    /**
     * Helper method to generate trigger change commands
     * @param controlAreaId - Control area for the trigger
     * @param trigger - Trigger to generate commands for
     * @param threshold - New threshold value
     * @param offset - New offset value
     * @return - List of trigger change commands
     */
    private List<LMCommand> getTriggerCommands(int controlAreaId, LMControlAreaTrigger trigger, 
                                               Double threshold, Double offset) {
        
        List<LMCommand> commandList = new ArrayList<LMCommand>();
        
        Integer triggerNumber = trigger.getTriggerNumber();
        if(offset != null && trigger.getMinRestoreOffset() != offset) {
            //create a new restore offset command message
            LMCommand offsetCommand = new LMCommand(LMCommand.CHANGE_RESTORE_OFFSET,
                                                    controlAreaId,
                                                    triggerNumber,
                                                    offset);
            commandList.add(offsetCommand);
        }

        if(threshold != null && trigger.getThreshold() != threshold) {
            //create a new threshold command message
            LMCommand thresholdCommand = new LMCommand(LMCommand.CHANGE_THRESHOLD,
                                                        controlAreaId,
                                                        triggerNumber,
                                                        threshold);
            commandList.add(thresholdCommand);
        }
        
        return commandList;
    }

    @Autowired
    public void setControlAreaDao(ControlAreaDao controlAreaDao) {
        this.controlAreaDao = controlAreaDao;
    }

    @Autowired
    public void setLoadControlClientConnection(
            LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
}
