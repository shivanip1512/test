package com.cannontech.amr.statusPointMonitoring.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitorProcessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class StatusPointMonitorDaoImpl implements StatusPointMonitorDao  {

    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private AttributeService attributeService;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<StatusPointMonitor> statusPointMonitorTemplate;
    private SimpleTableAccessTemplate<StoredStatusPointMonitorProcessor> statusPointMonitorProcessorTemplate;

    
    private final YukonRowMapper<StatusPointMonitor> statusPointMonitorRowMapper =
        new YukonRowMapper<StatusPointMonitor>() {
        @Override
        public StatusPointMonitor mapRow(YukonResultSet rs) throws SQLException {
            
            StatusPointMonitor retVal = new StatusPointMonitor();
            retVal.setStatusPointMonitorId(rs.getInt("statusPointMonitorId"));
            retVal.setName(rs.getString("statusPointMonitorName"));
            retVal.setGroupName(rs.getString("groupName"));
            String attributeKey = rs.getString("attribute");
            Attribute attribute = attributeService.resolveAttributeName(attributeKey);
            retVal.setAttribute(attribute);
            retVal.setStateGroup(stateGroupDao.getStateGroup(rs.getInt("stateGroupId")));
            retVal.setEvaluatorStatus(rs.getEnum("evaluatorStatus", MonitorEvaluatorStatus.class));
            return retVal;
        }
    };
    
    private final YukonRowMapper<StatusPointMonitorProcessor> processorRowMapper =
        new YukonRowMapper<StatusPointMonitorProcessor>() {
        @Override
        public StatusPointMonitorProcessor mapRow(YukonResultSet rs) throws SQLException {
            StatusPointMonitorProcessor retVal = new StatusPointMonitorProcessor();
            retVal.setStatusPointMonitorProcessorId(rs.getInt("statusPointMonitorProcessorId"));
            retVal.setPrevState(rs.getString("prevState"));
            retVal.setNextState(rs.getString("nextState"));
            retVal.setActionType(rs.getString("actionType"));
            return retVal;
        }
    };
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public StatusPointMonitor getStatusPointMonitorById(final Integer statusPointMonitorId) throws NotFoundException {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT statusPointMonitorId, StatusPointMonitorName, GroupName, Attribute, StateGroupId, EvaluatorStatus");
        sql.append("FROM StatusPointMonitor");
        sql.append("WHERE statusPointMonitorId").eq(statusPointMonitorId);
        sql.append("ORDER BY StatusPointMonitorName");
        
        StatusPointMonitor statusPointMonitor = null;
        
        try {
            statusPointMonitor = yukonJdbcTemplate.queryForObject(sql, statusPointMonitorRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Status Point Monitor not found.", e);
        }
        
        List<StatusPointMonitorProcessor> statusPointMonitorProcessors = getProcessorsByMonitorId(statusPointMonitorId);
        statusPointMonitor.setProcessors(statusPointMonitorProcessors);
        
        return statusPointMonitor;
    }
    
    @Override
    @Transactional
    public void save(StatusPointMonitor statusPointMonitor) {
        try {
            List<StatusPointMonitorProcessor> existingProcessorsList = Lists.newArrayList();
            if (statusPointMonitorTemplate.saveWillUpdate(statusPointMonitor)) {
                // get existing Processors
                existingProcessorsList = getProcessorsByMonitorId(statusPointMonitor.getStatusPointMonitorId());
            }
            statusPointMonitorTemplate.save(statusPointMonitor);
            
            //delete all processors
            for (StatusPointMonitorProcessor processor : existingProcessorsList) {
                deleteProcessor(processor.getStatusPointMonitorProcessorId());
            }
            
            List<StatusPointMonitorProcessor> newProcessorsList = statusPointMonitor.getProcessors();
            Set<StatusPointMonitorProcessor> newProcessors = Sets.newHashSet(newProcessorsList);
            
            //re-add them... plus the new ones (with duplicates removed)
            for (StatusPointMonitorProcessor processor : newProcessors) {
                StoredStatusPointMonitorProcessor holder = new StoredStatusPointMonitorProcessor();
                holder.statusPointMonitorProcessor = processor;
                holder.parent = statusPointMonitor;
                
                //we can't do a save here, but it will try to do an update, which will fail
                //--deliberately re-using the ID of the row we just deleted above
                statusPointMonitorProcessorTemplate.insert(holder);
            }

        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to save Status Point Monitor.", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<StatusPointMonitor> getAllStatusPointMonitors() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT statusPointMonitorId, StatusPointMonitorName, GroupName, Attribute, StateGroupId, EvaluatorStatus");
        sql.append("FROM StatusPointMonitor");
        sql.append("ORDER BY StatusPointMonitorName");
        
        List<StatusPointMonitor> statusPointMonitorList = yukonJdbcTemplate.query(sql, statusPointMonitorRowMapper); 
        
        for (StatusPointMonitor statusPointMonitor : statusPointMonitorList) {
            List<StatusPointMonitorProcessor> processors = getProcessorsByMonitorId(statusPointMonitor.getStatusPointMonitorId());
            statusPointMonitor.setProcessors(processors);
        }
        
        return statusPointMonitorList;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<StatusPointMonitorProcessor> getProcessorsByMonitorId(int statusPointMonitorId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT StatusPointMonitorProcessorId, StatusPointMonitorId, PrevState, NextState, ActionType");
        sql.append("FROM StatusPointMonitorProcessor");
        sql.append("WHERE StatusPointMonitorId").eq(statusPointMonitorId);
        sql.append("ORDER BY StatusPointMonitorProcessorId");
        
        List<StatusPointMonitorProcessor> processorList = yukonJdbcTemplate.query(sql, processorRowMapper); 
        
        return processorList;
    }
    
    @Override
    @Transactional
    public boolean deleteStatusPointMonitor(int statusPointMonitorId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM StatusPointMonitor");
        sql.append("WHERE StatusPointMonitorId").eq(statusPointMonitorId);
        int rowsAffected = yukonJdbcTemplate.update(sql);

        return rowsAffected > 0;
    }
    
    @Override
    public boolean deleteProcessor(int processorId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM StatusPointMonitorProcessor");
        sql.append("WHERE StatusPointMonitorProcessorId").eq(processorId);
        int rowsAffected = yukonJdbcTemplate.update(sql);
        
        return rowsAffected > 0;
    }
    
    private final FieldMapper<StatusPointMonitor> statusPointMonitorFieldMapper = new FieldMapper<StatusPointMonitor>() {
        @Override
        public void extractValues(MapSqlParameterSource p, StatusPointMonitor statusPointMonitor) {
            p.addValue("StatusPointMonitorName", statusPointMonitor.getName());
            p.addValue("GroupName", statusPointMonitor.getGroupName());
            p.addValue("Attribute", statusPointMonitor.getAttribute());
            p.addValue("StateGroupId", statusPointMonitor.getStateGroup().getStateGroupID());
            p.addValue("EvaluatorStatus", statusPointMonitor.getEvaluatorStatus());
        }
        @Override
        public Number getPrimaryKey(StatusPointMonitor statusPointMonitor) {
            return statusPointMonitor.getStatusPointMonitorId();
        }
        @Override
        public void setPrimaryKey(StatusPointMonitor statusPointMonitor, int value) {
        	statusPointMonitor.setStatusPointMonitorId(value);
        }
    };
    
    private final FieldMapper<StoredStatusPointMonitorProcessor> statusPointMonitorProcessorFieldMapper = new FieldMapper<StoredStatusPointMonitorProcessor>() {
        @Override
        public void extractValues(MapSqlParameterSource p, StoredStatusPointMonitorProcessor holder) {
            p.addValue("StatusPointMonitorId", holder.parent.getStatusPointMonitorId());
            p.addValue("PrevState", holder.statusPointMonitorProcessor.getPrevState());
            p.addValue("NextState", holder.statusPointMonitorProcessor.getNextState());
            p.addValue("ActionType", holder.statusPointMonitorProcessor.getActionTypeEnum().name());
        }
        @Override
        public Number getPrimaryKey(StoredStatusPointMonitorProcessor holder) {
            return holder.statusPointMonitorProcessor.getStatusPointMonitorProcessorId();
        }
        @Override
        public void setPrimaryKey(StoredStatusPointMonitorProcessor holder, int value) {
            holder.statusPointMonitorProcessor.setStatusPointMonitorProcessorId(value);
        }
    };
    
    @PostConstruct
    public void init() throws Exception {
        statusPointMonitorTemplate = new SimpleTableAccessTemplate<StatusPointMonitor>(yukonJdbcTemplate, nextValueHelper);
        statusPointMonitorTemplate.setTableName("StatusPointMonitor");
        statusPointMonitorTemplate.setPrimaryKeyField("StatusPointMonitorId");
        statusPointMonitorTemplate.setFieldMapper(statusPointMonitorFieldMapper);
        
        statusPointMonitorProcessorTemplate = new SimpleTableAccessTemplate<StoredStatusPointMonitorProcessor>(yukonJdbcTemplate, nextValueHelper);
        statusPointMonitorProcessorTemplate.setTableName("StatusPointMonitorProcessor");
        statusPointMonitorProcessorTemplate.setPrimaryKeyField("StatusPointMonitorProcessorId");
        statusPointMonitorProcessorTemplate.setFieldMapper(statusPointMonitorProcessorFieldMapper);
    }
    
    private class StoredStatusPointMonitorProcessor {
        StatusPointMonitor parent;
        StatusPointMonitorProcessor statusPointMonitorProcessor;
    }

    @Override
    public boolean monitorExistsWithName(String name) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM StatusPointMonitor");
        sql.append("WHERE UPPER(StatusPointMonitorName) = UPPER(").appendArgument(name).append(")");
        return yukonJdbcTemplate.queryForInt(sql) > 0;
    }
}