package com.cannontech.amr.deviceDataMonitor.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.AdvancedFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SimpleTableAccessTemplate.CascadeMode;
import com.cannontech.database.SqlParameterChildSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.incrementer.NextValueHelper;

public class DeviceDataMonitorDaoImpl implements DeviceDataMonitorDao {

    @Autowired private AttributeService attributeService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private StateDao stateDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorDaoImpl.class);
    
    private SimpleTableAccessTemplate<DeviceDataMonitor> monitorTemplate;
    private SimpleTableAccessTemplate<DeviceDataMonitorProcessor> processorTemplate;

    @PostConstruct
    public void init() throws Exception {
        monitorTemplate = new SimpleTableAccessTemplate<DeviceDataMonitor>(yukonJdbcTemplate, nextValueHelper);
        monitorTemplate.setTableName("DeviceDataMonitor");
        monitorTemplate.setPrimaryKeyField("MonitorId");
        monitorTemplate.setAdvancedFieldMapper(monitorFieldMapper);

        processorTemplate = new SimpleTableAccessTemplate<DeviceDataMonitorProcessor>(yukonJdbcTemplate, nextValueHelper);
        processorTemplate.setTableName("DeviceDataMonitorProcessor");
        processorTemplate.setPrimaryKeyField("ProcessorId");
        processorTemplate.setParentForeignKeyField("MonitorId", CascadeMode.DELETE_ALL_CHILDREN_BEFORE_UPDATE);
        processorTemplate.setAdvancedFieldMapper(processorFieldMapper);
    }

    private final YukonRowMapper<DeviceDataMonitor> monitorRowMapper = new YukonRowMapper<DeviceDataMonitor>() {
        @Override
        public DeviceDataMonitor mapRow(YukonResultSet rs) throws SQLException {
            return new DeviceDataMonitor(rs.getInt("monitorId"),
                                         rs.getString("name"),
                                         rs.getString("groupName"),
                                         rs.getInt("enabled") == 1 ? true : false,
                                                 null);
        }
    };

    private final YukonRowMapper<DeviceDataMonitorProcessor> processorRowMapper = new YukonRowMapper<DeviceDataMonitorProcessor>() {
        @Override
        public DeviceDataMonitorProcessor mapRow(YukonResultSet rs) throws SQLException {
            LiteStateGroup stateGroup = stateDao.getLiteStateGroup(rs.getInt("stateGroupId"));
            int rawState = rs.getInt("state");
            LiteState liteState = stateDao.findLiteState(stateGroup.getStateGroupID(), rawState);
            if (liteState == null) throw new RuntimeException("could not find matching state of " + rawState + " within state group " + stateGroup.getStateGroupName());
            return new DeviceDataMonitorProcessor(rs.getInt("processorId"),
                                                  rs.getInt("monitorId"),
                                                  attributeService.resolveAttributeName(rs.getString("attribute")),
                                                  stateGroup,
                                                  liteState);
        }
    };

    @Override
    public List<DeviceDataMonitor> getAllMonitors() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MonitorId, Name, GroupName, Enabled");
        sql.append("FROM DeviceDataMonitor");
        sql.append("ORDER BY MonitorId");

        List<DeviceDataMonitor> monitorList = yukonJdbcTemplate.query(sql, monitorRowMapper);

        for (DeviceDataMonitor monitor : monitorList) {
            List<DeviceDataMonitorProcessor> processors = getProcessorsByMonitorId(monitor.getId());
            monitor.setProcessors(processors);
        }

        return monitorList;
    }

    @Override
    public DeviceDataMonitor getMonitorById(final Integer monitorId) throws NotFoundException {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MonitorId, Name, GroupName, Enabled");
        sql.append("FROM DeviceDataMonitor");
        sql.append("WHERE MonitorId").eq(monitorId);

        DeviceDataMonitor monitor = null;

        try {
            monitor = yukonJdbcTemplate.queryForObject(sql, monitorRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Device Data Monitor not found.", e);
        }

        List<DeviceDataMonitorProcessor> processors = getProcessorsByMonitorId(monitorId);
        monitor.setProcessors(processors);

        return monitor;
    }

    private List<DeviceDataMonitorProcessor> getProcessorsByMonitorId(int monitorId) {
        SqlStatementBuilder ruleSql = new SqlStatementBuilder();
        ruleSql.append("SELECT ProcessorId, MonitorId, Attribute, StateGroupId, State");
        ruleSql.append("FROM DeviceDataMonitorProcessor");
        ruleSql.append("WHERE monitorId").eq(monitorId);
        ruleSql.append("ORDER BY Attribute, StateGroupId, State");

        List<DeviceDataMonitorProcessor> processorList = yukonJdbcTemplate.query(ruleSql, processorRowMapper);
        return processorList;
    }

    @Override
    @Transactional
    public boolean deleteMonitor(int monitorId) {
        DeviceDataMonitor monitor = getMonitorById(monitorId);
        
        /* remove the violation device group */
        DeviceGroup monitorViolationGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.DEVICE_DATA, monitor.getViolationsDeviceGroupName());
        StoredDeviceGroup monitorViolationStoredGroup = deviceGroupEditorDao.getStoredGroup(monitorViolationGroup);
        deviceGroupEditorDao.removeGroup(monitorViolationStoredGroup);
        log.info("Deleted device data monitor violations group: " + monitorViolationStoredGroup.getFullName());
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DeviceDataMonitor");
        sql.append("WHERE MonitorId").eq(monitorId);
        int rowsAffected = yukonJdbcTemplate.update(sql);
        log.info("Deleted device data monitor: " + monitor.getName());
        
        userSubscriptionDao.deleteSubscriptionsForItem(SubscriptionType.DEVICE_DATA_MONITOR, monitorId);
        return rowsAffected > 0;
    }

    @Override
    @Transactional
    public void save(DeviceDataMonitor monitor) {
        try {
            monitorTemplate.save(monitor);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to save Device Data Monitor.", e);
        }
    }

    private final AdvancedFieldMapper<DeviceDataMonitor> monitorFieldMapper = new AdvancedFieldMapper<DeviceDataMonitor>() {
        @Override
        public void extractValues(SqlParameterChildSink p, DeviceDataMonitor monitor) {
            p.addValue("Name", monitor.getName());
            p.addValue("GroupName", monitor.getGroupName());
            p.addValue("Enabled", monitor.isEnabled());
            p.addChildren(processorTemplate, monitor.getProcessors());
        }

        @Override
        public Number getPrimaryKey(DeviceDataMonitor monitor) {
            return monitor.getId();
        }

        @Override
        public void setPrimaryKey(DeviceDataMonitor monitor, int value) {
            monitor.setId(value);
        }
    };

    private final AdvancedFieldMapper<DeviceDataMonitorProcessor> processorFieldMapper = new AdvancedFieldMapper<DeviceDataMonitorProcessor>() {
        @Override
        public void extractValues(SqlParameterChildSink p, DeviceDataMonitorProcessor processor) {
            p.addValue("Attribute", processor.getAttribute());
            p.addValue("StateGroupId", processor.getStateGroup().getStateGroupID());
            p.addValue("State", processor.getState().getLiteID());
        }

        @Override
        public Number getPrimaryKey(DeviceDataMonitorProcessor processor) {
            return processor.getProcessorId();
        }

        @Override
        public void setPrimaryKey(DeviceDataMonitorProcessor processor, int value) {
            processor.setProcessorId(value);
        }
    };
}