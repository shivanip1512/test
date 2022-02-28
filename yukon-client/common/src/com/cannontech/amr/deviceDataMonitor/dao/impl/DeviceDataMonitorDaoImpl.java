package com.cannontech.amr.deviceDataMonitor.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.model.ProcessorType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateGroupDao;
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
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public class DeviceDataMonitorDaoImpl implements DeviceDataMonitorDao {

    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorDaoImpl.class);
    
    private SimpleTableAccessTemplate<DeviceDataMonitor> monitorTemplate;
    private SimpleTableAccessTemplate<DeviceDataMonitorProcessor> processorTemplate;
    @Autowired private DbChangeManager dbChangeManager;

    @PostConstruct
    public void init() {
        monitorTemplate = new SimpleTableAccessTemplate<>(yukonJdbcTemplate, nextValueHelper);
        monitorTemplate.setTableName("DeviceDataMonitor");
        monitorTemplate.setPrimaryKeyField("MonitorId");
        monitorTemplate.setAdvancedFieldMapper(monitorFieldMapper);

        processorTemplate = new SimpleTableAccessTemplate<>(yukonJdbcTemplate, nextValueHelper);
        processorTemplate.setTableName("DeviceDataMonitorProcessor");
        processorTemplate.setPrimaryKeyField("ProcessorId");
        processorTemplate.setParentForeignKeyField("MonitorId", CascadeMode.DELETE_ALL_CHILDREN_BEFORE_UPDATE);
        processorTemplate.setAdvancedFieldMapper(processorFieldMapper);
    }

    private final YukonRowMapper<DeviceDataMonitor> monitorRowMapper = new YukonRowMapper<>() {
        @Override
        public DeviceDataMonitor mapRow(YukonResultSet rs) throws SQLException {
            DeviceDataMonitor ddm = new DeviceDataMonitor(
                    rs.getInt("monitorId"), 
                    rs.getString("name"),
                    rs.getString("groupName"), 
                    deviceGroupService.findGroupName(rs.getString("groupName")),
                    isEnabled(rs.getInt("enabled")), 
                    null, //no processors
                    isEnabled(rs.getInt("notifyOnAlarmOnly")));
            StoredDeviceGroup violationGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA,
                ddm.getViolationsDeviceGroupName(), true);
            ddm.setViolationGroup(violationGroup);
            return ddm;
        }
        
        private boolean isEnabled(int value) {
            return value != 0;
        }
    };

    private final YukonRowMapper<DeviceDataMonitorProcessor> processorRowMapper =
        new YukonRowMapper<>() {
            @Override
            public DeviceDataMonitorProcessor mapRow(YukonResultSet rs) throws SQLException {

                DeviceDataMonitorProcessor processor = new DeviceDataMonitorProcessor(rs.getInt("processorId"),
                    rs.getEnum("ProcessorType", ProcessorType.class), rs.getInt("monitorId"),
                    rs.getEnum("Attribute", BuiltInAttribute.class));

                if (processor.getType().isStateBased()) {
                    LiteStateGroup stateGroup = stateGroupDao.getStateGroup(rs.getInt("stateGroupId"));
                    int rawState = rs.getInt("ProcessorValue");
                    LiteState liteState = stateGroupDao.findLiteState(stateGroup.getStateGroupID(), rawState);
                    if (liteState == null) {
                        throw new RuntimeException("could not find matching state of " + rawState
                            + " within state group " + stateGroup.getStateGroupName());
                    }
                    processor.setState(liteState);
                    processor.setStateGroup(stateGroup);
                }
                if (processor.getType().isValueBased()) {
                    if (processor.getType() == ProcessorType.RANGE || processor.getType() == ProcessorType.OUTSIDE) {
                        processor.setRangeMin(rs.getDouble("RangeMin"));
                        processor.setRangeMax(rs.getDouble("RangeMax"));
                    } else {
                        processor.setProcessorValue(rs.getDouble("ProcessorValue"));
                    }
                }
                return processor;
            }
        };

    @Override
    public List<DeviceDataMonitor> getAllMonitors() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MonitorId, Name, GroupName, Enabled, NotifyOnAlarmOnly");
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
        sql.append("SELECT MonitorId, Name, GroupName, Enabled, NotifyOnAlarmOnly");
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
        ruleSql.append("SELECT ProcessorId, ProcessorType, MonitorId, ProcessorValue, RangeMin, RangeMax, Attribute, StateGroupId");
        ruleSql.append("FROM DeviceDataMonitorProcessor");
        ruleSql.append("WHERE monitorId").eq(monitorId);
        ruleSql.append("ORDER BY Attribute");

        List<DeviceDataMonitorProcessor> processorList = yukonJdbcTemplate.query(ruleSql, processorRowMapper);
        return processorList;
    }

    @Override
    @Transactional
    public boolean deleteMonitor(DeviceDataMonitor monitor) {
        /* remove the violation device group */
        DeviceGroup monitorViolationGroup = deviceGroupService.resolveGroupName(SystemGroupEnum.DEVICE_DATA, monitor.getViolationsDeviceGroupName());
        StoredDeviceGroup monitorViolationStoredGroup = deviceGroupEditorDao.getStoredGroup(monitorViolationGroup);
        deviceGroupEditorDao.removeGroup(monitorViolationStoredGroup);
        log.info("Deleted device data monitor violations group: " + monitorViolationStoredGroup.getFullName());
        
        // Remove the monitor
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DeviceDataMonitor");
        sql.append("WHERE MonitorId").eq(monitor.getId());
        int rowsAffected = yukonJdbcTemplate.update(sql);
        log.info("Deleted device data monitor: " + monitor.getName());
        
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.DEVICE_DATA_MONITOR, monitor.getId());
        return rowsAffected > 0;
    }

    @Override
    @Transactional
    public void save(DeviceDataMonitor monitor) {
        try {      
            DbChangeType type = monitor.getId() == null ? DbChangeType.ADD : DbChangeType.UPDATE;
            monitorTemplate.save(monitor);
            dbChangeManager.processDbChange(type, DbChangeCategory.DEVICE_DATA_MONITOR, monitor.getId());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to save Device Data Monitor.", e);
        }
    }

    @Override
    public boolean processorExistsWithName(String name) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM DeviceDataMonitor WHERE UPPER(Name) = UPPER(").appendArgument(StringUtils.trim(name)).append(")");
        return yukonJdbcTemplate.queryForInt(sql) > 0;
    }

    private final AdvancedFieldMapper<DeviceDataMonitor> monitorFieldMapper = new AdvancedFieldMapper<>() {
        @Override
        public void extractValues(SqlParameterChildSink p, DeviceDataMonitor monitor) {
            p.addValue("Name", monitor.getName());
            p.addValue("GroupName", monitor.getGroupName());
            p.addValue("Enabled", monitor.isEnabled());
            p.addValue("NotifyOnAlarmOnly", monitor.isNotifyOnAlarmOnly());
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

    private final AdvancedFieldMapper<DeviceDataMonitorProcessor> processorFieldMapper = new AdvancedFieldMapper<>() {
        @Override
        public void extractValues(SqlParameterChildSink p, DeviceDataMonitorProcessor processor) {
            p.addValue("Attribute", processor.getAttribute());
            p.addValue("ProcessorType", processor.getType().name());
            if(processor.getType().isStateBased()) {
                p.addValue("StateGroupId", processor.getStateGroup().getStateGroupID());
                p.addValue("ProcessorValue", processor.getState().getLiteID());
            } else if (processor.getType().isValueBased()) {
                if (processor.getType() == ProcessorType.RANGE || processor.getType() == ProcessorType.OUTSIDE) {
                    p.addValue("RangeMin", processor.getRangeMin());
                    p.addValue("RangeMax", processor.getRangeMax());
                } else {
                    p.addValue("ProcessorValue", processor.getProcessorValue());
                }
            }
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