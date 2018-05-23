package com.cannontech.amr.monitors.impl;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.Lists;

public class MonitorCacheServiceImpl implements MonitorCacheService {
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
    @Autowired private OutageMonitorDao outageMonitorDao;
    @Autowired private TamperFlagMonitorDao tamperFlagMonitorDao;
    @Autowired private StatusPointMonitorDao statusPointMonitorDao;
    @Autowired private PorterResponseMonitorDao porterResponseMonitorDao;
    @Autowired private ValidationMonitorDao validationMonitorDao;
    
    private Set<DbChangeType> addType = EnumSet.of(DbChangeType.ADD, DbChangeType.UPDATE);
    private Set<DbChangeType> deleteType = EnumSet.of(DbChangeType.DELETE);

    private Map<Integer, DeviceDataMonitor> deviceMonitors;
    private Map<Integer, OutageMonitor> outageMonitors;
    private Map<Integer, TamperFlagMonitor> tamperFlagMonitors;
    private Map<Integer, StatusPointMonitor> statusPointMonitors;
    private Map<Integer, PorterResponseMonitor> porterResponseMonitors;
    private Map<Integer, ValidationMonitor> validationMonitors;
    
    @PostConstruct
    public void initialize() {
        loadCache();
        createDatabaseChangeListeners();
    }

    private void loadCache(){
        deviceMonitors = deviceDataMonitorDao.getAllMonitors().stream().collect(
            Collectors.toConcurrentMap(DeviceDataMonitor::getId, Function.identity()));
        outageMonitors = outageMonitorDao.getAll().stream().collect(
            Collectors.toConcurrentMap(OutageMonitor::getOutageMonitorId, Function.identity()));
        tamperFlagMonitors = tamperFlagMonitorDao.getAll().stream().collect(
            Collectors.toConcurrentMap(TamperFlagMonitor::getTamperFlagMonitorId, Function.identity()));
        statusPointMonitors = statusPointMonitorDao.getAllStatusPointMonitors().stream().collect(
            Collectors.toConcurrentMap(StatusPointMonitor::getStatusPointMonitorId, Function.identity()));
        porterResponseMonitors = porterResponseMonitorDao.getAllMonitors().stream().collect(
            Collectors.toConcurrentMap(PorterResponseMonitor::getMonitorId, Function.identity()));
        validationMonitors = validationMonitorDao.getAll().stream().collect(
            Collectors.toConcurrentMap(ValidationMonitor::getValidationMonitorId, Function.identity())); 
    }
    
    private void createDatabaseChangeListeners() {
        /* listen for monitor adds/updates */
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.DEVICE_DATA_MONITOR, addType, e -> {
            DeviceDataMonitor newMonitor = deviceDataMonitorDao.getMonitorById(e.getPrimaryKey());
            deviceMonitors.put(newMonitor.getId(), newMonitor);
        });
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.OUTAGE_MONITOR, addType, e -> {
            OutageMonitor newMonitor = outageMonitorDao.getById(e.getPrimaryKey());
            outageMonitors.put(newMonitor.getOutageMonitorId(), newMonitor);
        });
                
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.TAMPER_FLAG_MONITOR, addType, e -> {
            TamperFlagMonitor newMonitor = tamperFlagMonitorDao.getById(e.getPrimaryKey());
            tamperFlagMonitors.put(newMonitor.getTamperFlagMonitorId(), newMonitor);
        });
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.STATUS_POINT_MONITOR, addType, e -> {
            StatusPointMonitor newMonitor = statusPointMonitorDao.getStatusPointMonitorById(e.getPrimaryKey());
            statusPointMonitors.put(newMonitor.getStatusPointMonitorId(), newMonitor);
        });
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.PORTER_RESPONSE_MONITOR, addType, e -> {
            PorterResponseMonitor newMonitor = porterResponseMonitorDao.getMonitorById(e.getPrimaryKey());
            porterResponseMonitors.put(newMonitor.getMonitorId(), newMonitor);
        });
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.VALIDATION_MONITOR, addType, e -> {
            ValidationMonitor newMonitor = validationMonitorDao.getById(e.getPrimaryKey());
            validationMonitors.put(newMonitor.getValidationMonitorId(), newMonitor);
        });

        /* listen for monitor deletions */
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.DEVICE_DATA_MONITOR, deleteType, e -> {
            deviceMonitors.remove(e.getPrimaryKey());
        });
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.OUTAGE_MONITOR, deleteType, e -> {
            outageMonitors.remove(e.getPrimaryKey());
        });
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.TAMPER_FLAG_MONITOR, deleteType, e -> {
            tamperFlagMonitors.remove(e.getPrimaryKey());
        });
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.STATUS_POINT_MONITOR, deleteType, e -> {
            statusPointMonitors.remove(e.getPrimaryKey());
        });
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.PORTER_RESPONSE_MONITOR, deleteType, e -> {
            porterResponseMonitors.remove(e.getPrimaryKey());
        });
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.VALIDATION_MONITOR, deleteType, e -> {
            validationMonitors.remove(e.getPrimaryKey());
        });
    }
    
    @Override
    public List<DeviceDataMonitor> getDeviceDataMonitors() {
        return Lists.newArrayList(deviceMonitors.values());
    }

    @Override
    public List<DeviceDataMonitor> getEnabledDeviceDataMonitors() {
        return getDeviceDataMonitors().stream().filter(DeviceDataMonitor::isEnabled).collect(Collectors.toList());
    }
    
    @Override
    public DeviceDataMonitor getDeviceMonitor(int id) {
        return deviceMonitors.get(id);
    }
    
    @Override
    public List<OutageMonitor> getOutageMonitors() {
        return Lists.newArrayList(outageMonitors.values());
    }
    
    @Override
    public OutageMonitor getOutageMonitor(int id) {
        return outageMonitors.get(id);
    }
    
    @Override
    public List<TamperFlagMonitor> getTamperFlagMonitors() {
        return Lists.newArrayList(tamperFlagMonitors.values());
    }

    @Override
    public TamperFlagMonitor getTamperFlagMonitor(int id) {
        return tamperFlagMonitors.get(id);
    }
    
    @Override
    public List<StatusPointMonitor> getStatusPointMonitors() {
        return Lists.newArrayList(statusPointMonitors.values());
    }

    @Override
    public StatusPointMonitor getStatusPointMonitor(int id) {
        return statusPointMonitors.get(id);
    }

    @Override
    public List<PorterResponseMonitor> getPorterResponseMonitors() {
        return Lists.newArrayList(porterResponseMonitors.values());
    }

    @Override
    public List<PorterResponseMonitor> getEnabledPorterResponseMonitors() {
        return getPorterResponseMonitors().stream().filter(
            m -> m.getEvaluatorStatus() == MonitorEvaluatorStatus.ENABLED).collect(Collectors.toList());
    }
    
    @Override
    public PorterResponseMonitor getPorterResponseMonitor(int id) {
        return porterResponseMonitors.get(id);
    }
    
    @Override
    public List<ValidationMonitor> getValidationMonitors() {
        return Lists.newArrayList(validationMonitors.values());
    }

    @Override
    public ValidationMonitor getValidationMonitor(int id) {
        return validationMonitors.get(id);
    }
    
    /** 
     * Only for testing
     */
    public void setMonitors(Map<Integer, PorterResponseMonitor> monitors) {
        porterResponseMonitors = monitors;
    }
}
