package com.cannontech.amr.statusPointProcessing.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointProcessing.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitor;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitorMessageProcessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.StatusPointMonitorMessageProcessorNotFoundException;
import com.cannontech.core.dao.StatusPointMonitorNotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class StatusPointMonitorDaoImpl implements StatusPointMonitorDao, InitializingBean  {

    private StateDao stateDao;
    private AttributeService attributeService;

    private final YukonRowMapper<StatusPointMonitor> statusPointMonitorRowMapper =
        new YukonRowMapper<StatusPointMonitor>() {
        @Override
        public StatusPointMonitor mapRow(YukonResultSet rs) throws SQLException {
            
            StatusPointMonitor retVal = new StatusPointMonitor();
            retVal.setStatusPointMonitorId(rs.getInt("statusPointMonitorId"));
            retVal.setStatusPointMonitorName(rs.getString("statusPointMonitorName"));
            retVal.setGroupName(rs.getString("groupName"));
            String attributeKey = rs.getString("attribute");
            Attribute attribute = attributeService.resolveAttributeName(attributeKey);
            retVal.setAttribute(attribute);
            retVal.setStateGroup(stateDao.getLiteStateGroup(rs.getInt("stateGroupId")));
            retVal.setEvaluatorStatus(rs.getEnum("evaluatorStatus", MonitorEvaluatorStatus.class));
            return retVal;
        }
    };
    
    private final YukonRowMapper<StatusPointMonitorMessageProcessor> statusPointMonitorMessageProcessorRowMapper =
        new YukonRowMapper<StatusPointMonitorMessageProcessor>() {
        @Override
        public StatusPointMonitorMessageProcessor mapRow(YukonResultSet rs) throws SQLException {
            StatusPointMonitorMessageProcessor retVal = new StatusPointMonitorMessageProcessor();
            retVal.setStatusPointMonitorMessageProcessorId(rs.getInt("statusPointMonitorMessageProcessorId"));
            retVal.setPrevState(rs.getString("prevState"));
            retVal.setNextState(rs.getString("nextState"));
            retVal.setActionType(rs.getString("actionType"));
            return retVal;
        }
    };
    
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<StatusPointMonitor> statusPointMonitorTemplate;
    private SimpleTableAccessTemplate<StoredStatusPointMonitorMessageProcessor> statusPointMonitorMessageProcessorTemplate;
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public StatusPointMonitor getStatusPointMonitorById(final Integer statusPointMonitorId) throws StatusPointMonitorNotFoundException {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT statusPointMonitorId, StatusPointMonitorName, GroupName, Attribute, StateGroupId, EvaluatorStatus");
        sql.append("FROM StatusPointMonitor");
        sql.append("WHERE statusPointMonitorId").eq(statusPointMonitorId);
        sql.append("ORDER BY StatusPointMonitorName");
        
        StatusPointMonitor statusPointMonitor = null;
        
        try {
            statusPointMonitor = yukonJdbcTemplate.queryForObject(sql, statusPointMonitorRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new StatusPointMonitorNotFoundException();
        }
        
        List<StatusPointMonitorMessageProcessor> statusPointMonitorMessageProcessors = getStatusPointMonitorMessageProcessorsByStatusPointMonitorId(statusPointMonitorId);
        statusPointMonitor.setStatusPointMonitorMessageProcessors(statusPointMonitorMessageProcessors);
        
        return statusPointMonitor;
    }
    
    @Override
    @Transactional
    public void save(StatusPointMonitor statusPointMonitor) {
        try {
            List<StatusPointMonitorMessageProcessor> existingProcessorsList = Lists.newArrayList();
            if (statusPointMonitorTemplate.saveWillUpdate(statusPointMonitor)) {
                // get existing MessageProcessors
                existingProcessorsList = getStatusPointMonitorMessageProcessorsByStatusPointMonitorId(statusPointMonitor.getStatusPointMonitorId());
            }
            statusPointMonitorTemplate.save(statusPointMonitor);
            
            //delete all message processors
            for (StatusPointMonitorMessageProcessor messageProcessor : existingProcessorsList) {
                deleteStatusPointMonitorMessageProcessor(messageProcessor.getStatusPointMonitorMessageProcessorId());
            }
            
            List<StatusPointMonitorMessageProcessor> newProcessorsList = statusPointMonitor.getStatusPointMonitorMessageProcessors();
            Set<StatusPointMonitorMessageProcessor> newProcessors = Sets.newHashSet(newProcessorsList);
            
            //re-add them... plus the new ones (with duplicates removed)
            for (StatusPointMonitorMessageProcessor messageProcessor : newProcessors) {
                StoredStatusPointMonitorMessageProcessor holder = new StoredStatusPointMonitorMessageProcessor();
                holder.statusPointMonitorMessageProcessor = messageProcessor;
                holder.parent = statusPointMonitor;
                
                //we can't do a save here, but it will try to do an update, which will fail
                //--deliberately re-using the ID of the row we just deleted above
                statusPointMonitorMessageProcessorTemplate.insert(holder);
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
            List<StatusPointMonitorMessageProcessor> messageProcessors = getStatusPointMonitorMessageProcessorsByStatusPointMonitorId(statusPointMonitor.getStatusPointMonitorId());
            if (messageProcessors != null) {
                statusPointMonitor.setStatusPointMonitorMessageProcessors(messageProcessors);
            }
        }
        
        return statusPointMonitorList;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<StatusPointMonitorMessageProcessor> getStatusPointMonitorMessageProcessorsByStatusPointMonitorId(int statusPointMonitorId) throws StatusPointMonitorMessageProcessorNotFoundException {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT StatusPointMonitorMessageProcessorId, StatusPointMonitorId, PrevState, NextState, ActionType");
        sql.append("FROM StatusPointMonitorMessageProcessor");
        sql.append("WHERE StatusPointMonitorId").eq(statusPointMonitorId);
        sql.append("ORDER BY StatusPointMonitorMessageProcessorId");
        
        List<StatusPointMonitorMessageProcessor> messageProcessorList = yukonJdbcTemplate.query(sql, statusPointMonitorMessageProcessorRowMapper); 
        
        return messageProcessorList;
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
    public boolean deleteStatusPointMonitorMessageProcessor(int statusPointMonitorMessageProcessorId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM StatusPointMonitorMessageProcessor");
        sql.append("WHERE StatusPointMonitorMessageProcessorId").eq(statusPointMonitorMessageProcessorId);
        int rowsAffected = yukonJdbcTemplate.update(sql);
        
        return rowsAffected > 0;
    }
    
    private FieldMapper<StatusPointMonitor> statusPointMonitorFieldMapper = new FieldMapper<StatusPointMonitor>() {
        public void extractValues(MapSqlParameterSource p, StatusPointMonitor statusPointMonitor) {
            p.addValue("StatusPointMonitorName", statusPointMonitor.getStatusPointMonitorName());
            p.addValue("GroupName", statusPointMonitor.getGroupName());
            p.addValue("Attribute", statusPointMonitor.getAttribute());
            p.addValue("StateGroupId", statusPointMonitor.getStateGroup().getStateGroupID());
            p.addValue("EvaluatorStatus", statusPointMonitor.getEvaluatorStatus());
        }
        public Number getPrimaryKey(StatusPointMonitor statusPointMonitor) {
            return statusPointMonitor.getStatusPointMonitorId();
        }
        public void setPrimaryKey(StatusPointMonitor statusPointMonitor, int value) {
        	statusPointMonitor.setStatusPointMonitorId(value);
        }
    };
    
    private FieldMapper<StoredStatusPointMonitorMessageProcessor> statusPointMonitorMessageProcessorFieldMapper = new FieldMapper<StoredStatusPointMonitorMessageProcessor>() {
        public void extractValues(MapSqlParameterSource p, StoredStatusPointMonitorMessageProcessor holder) {
            p.addValue("StatusPointMonitorId", holder.parent.getStatusPointMonitorId());
            p.addValue("PrevState", holder.statusPointMonitorMessageProcessor.getPrevState());
            p.addValue("NextState", holder.statusPointMonitorMessageProcessor.getNextState());
            p.addValue("ActionType", holder.statusPointMonitorMessageProcessor.getActionType());
        }
        public Number getPrimaryKey(StoredStatusPointMonitorMessageProcessor holder) {
            if (holder.statusPointMonitorMessageProcessor == null) {
                return null;
            }
            return holder.statusPointMonitorMessageProcessor.getStatusPointMonitorMessageProcessorId();
        }
        public void setPrimaryKey(StoredStatusPointMonitorMessageProcessor holder, int value) {
            holder.statusPointMonitorMessageProcessor.setStatusPointMonitorMessageProcessorId(value);
        }
    };
    
    public void afterPropertiesSet() throws Exception {
        statusPointMonitorTemplate = new SimpleTableAccessTemplate<StatusPointMonitor>(yukonJdbcTemplate, nextValueHelper);
        statusPointMonitorTemplate.withTableName("StatusPointMonitor");
        statusPointMonitorTemplate.withPrimaryKeyField("StatusPointMonitorId");
        statusPointMonitorTemplate.withFieldMapper(statusPointMonitorFieldMapper);
        
        statusPointMonitorMessageProcessorTemplate = new SimpleTableAccessTemplate<StoredStatusPointMonitorMessageProcessor>(yukonJdbcTemplate, nextValueHelper);
        statusPointMonitorMessageProcessorTemplate.withTableName("StatusPointMonitorMessageProcessor");
        statusPointMonitorMessageProcessorTemplate.withPrimaryKeyField("StatusPointMonitorMessageProcessorId");
        statusPointMonitorMessageProcessorTemplate.withFieldMapper(statusPointMonitorMessageProcessorFieldMapper);
    }
    
    private class StoredStatusPointMonitorMessageProcessor {
        StatusPointMonitor parent;
        StatusPointMonitorMessageProcessor statusPointMonitorMessageProcessor;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}
    
    @Autowired
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}