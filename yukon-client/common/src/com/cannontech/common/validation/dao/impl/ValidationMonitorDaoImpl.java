package com.cannontech.common.validation.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.dao.ValidationMonitorNotFoundException;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;


public class ValidationMonitorDaoImpl implements ValidationMonitorDao  {

	private final Logger log = YukonLogManager.getLogger(ValidationMonitorDaoImpl.class);

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    private static final RowMapper<ValidationMonitor> rowMapper;
    private SimpleTableAccessTemplate<ValidationMonitor> template;
    
    static {
        rowMapper = ValidationMonitorDaoImpl.createRowMapper();
    }
    
    @Override
    public SetMultimap<ValidationMonitor, Integer> loadEnabledValidationMonitors() {
        SetMultimap<ValidationMonitor, Integer> deviceGroupCache = HashMultimap.create();
        for (ValidationMonitor validationMonitor : getValidationMonitors()) {
            
            if (validationMonitor.getEvaluatorStatus() == MonitorEvaluatorStatus.ENABLED) {
                DeviceGroup groupName = deviceGroupService.findGroupName(validationMonitor.getDeviceGroupName());
                if (groupName != null) {	// check if group name still exists.	
                	Set<Integer> deviceIds = deviceGroupService.getDeviceIds(Collections.singleton(groupName));
                	deviceGroupCache.putAll(validationMonitor, deviceIds);
                } else {
                	LogHelper.warn(log, "Device Group %s no longer exists. Monitor %s is not validating any readings.", 
                			validationMonitor.getDeviceGroupName(), validationMonitor.getName());
                }
            }
            
        }
        return deviceGroupCache;
    }

    @Override
    public Set<ValidationMonitor> getValidationMonitors() {
        List<ValidationMonitor> monitors = getAll();
        
        return ImmutableSet.copyOf(monitors.toArray(new ValidationMonitor[monitors.size()]));
    }
    
    @Override
    public void saveOrUpdate(ValidationMonitor validationMonitor) {
        try {
            template.save(validationMonitor);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to save validation monitor.", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ValidationMonitor getById(int validationMonitorId) throws ValidationMonitorNotFoundException {
        ValidationMonitor validationMonitor = null;
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * ");
        sql.append("FROM ValidationMonitor ");
        sql.append("WHERE ValidationMonitorId ").eq(validationMonitorId);

        try {
            validationMonitor = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationMonitorNotFoundException();
        }
        
        return validationMonitor;
    }
    
    @Override
    public boolean processorExistsWithName(String name) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM ValidationMonitor ");
        sql.append("WHERE ValidationMonitorName ").eq(name);
        
        int c = yukonJdbcTemplate.queryForInt(sql);
        
        return c > 0;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ValidationMonitor> getAll() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * ");
        sql.append("FROM ValidationMonitor");
        
        return yukonJdbcTemplate.query(sql, rowMapper);
    }
    
    @Override
    public boolean delete(int validationMonitorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ValidationMonitor ");
        sql.append("WHERE ValidationMonitorId ").eq(validationMonitorId);
        
        userSubscriptionDao.deleteSubscriptionsForItem(SubscriptionType.VALIDATION_MONITOR, validationMonitorId);
        return yukonJdbcTemplate.update(sql) > 0;
    }
    
    private static final RowMapper<ValidationMonitor> createRowMapper() {
        final RowMapper<ValidationMonitor> rowMapper = new RowMapper<ValidationMonitor>() {
            public ValidationMonitor mapRow(ResultSet rs, int rowNum) throws SQLException {
                ValidationMonitor validationMonitor = new ValidationMonitor();
                
                validationMonitor.setValidationMonitorId(rs.getInt("ValidationMonitorId"));
                validationMonitor.setName(rs.getString("ValidationMonitorName"));
                validationMonitor.setDeviceGroupName(rs.getString("GroupName"));
                validationMonitor.setReasonableMaxKwhPerDay(rs.getDouble("Threshold"));
                validationMonitor.setReReadOnUnreasonable(rs.getInt("ReRead") > 0 ? true : false);
                validationMonitor.setKwhSlopeError(rs.getDouble("SlopeError"));
                validationMonitor.setKwhReadingError(rs.getDouble("ReadingError"));
                validationMonitor.setPeakHeightMinimum(rs.getDouble("PeakHeightMinimum"));
                validationMonitor.setQuestionableOnPeak(rs.getInt("QuestionableQuality") > 0 ? true : false);
                validationMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.valueOf(rs.getString("EvaluatorStatus")));
                return validationMonitor;
            }
        };
        return rowMapper;
    }
    
    private FieldMapper<ValidationMonitor> validationMonitorFieldMapper = new FieldMapper<ValidationMonitor>() {
        public void extractValues(MapSqlParameterSource p, ValidationMonitor validationMonitor) {
            p.addValue("ValidationMonitorName", validationMonitor.getName());
            p.addValue("GroupName", validationMonitor.getDeviceGroupName());
            p.addValue("Threshold", validationMonitor.getReasonableMaxKwhPerDay());
            p.addValue("ReRead", validationMonitor.isReReadOnUnreasonable() ? 1 : 0);
            p.addValue("SlopeError", validationMonitor.getKwhSlopeError());
            p.addValue("ReadingError", validationMonitor.getKwhReadingError());
            p.addValue("PeakHeightMinimum", validationMonitor.getPeakHeightMinimum());
            p.addValue("QuestionableQuality", validationMonitor.isSetQuestionableOnPeak() ? 1 : 0);
            p.addValue("EvaluatorStatus", validationMonitor.getEvaluatorStatus().name());
            
        }
        public Number getPrimaryKey(ValidationMonitor validationMonitor) {
            return validationMonitor.getValidationMonitorId();
        }
        public void setPrimaryKey(ValidationMonitor validationMonitor, int value) {
            validationMonitor.setValidationMonitorId(value);
        }
    };
    
    @PostConstruct
    public void init() throws Exception {
        template = 
            new SimpleTableAccessTemplate<ValidationMonitor>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("ValidationMonitor");
        template.setPrimaryKeyField("ValidationMonitorId");
        template.setFieldMapper(validationMonitorFieldMapper); 
    }
}
