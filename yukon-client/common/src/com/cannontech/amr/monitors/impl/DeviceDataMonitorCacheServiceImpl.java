package com.cannontech.amr.monitors.impl;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.monitors.DeviceDataMonitorCacheService;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.dispatch.DatabaseChangeEvent;
import com.cannontech.dispatch.DbChangeCategory;
import com.cannontech.dispatch.DbChangeType;
import com.google.common.collect.Lists;

public class DeviceDataMonitorCacheServiceImpl implements DeviceDataMonitorCacheService {
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;

    private Map<Integer, DeviceDataMonitor> monitorsMapEnabled = new ConcurrentHashMap<>();
    private Map<Integer, DeviceDataMonitor> monitorsMapAll = new ConcurrentHashMap<>();

    @PostConstruct
    public void initialize() {
        List<DeviceDataMonitor> monitorsList = deviceDataMonitorDao.getAllMonitors();
        for (DeviceDataMonitor monitor : monitorsList) {
            monitorsMapAll.put(monitor.getId(), monitor);
            if (monitor.isEnabled()) {
                monitorsMapEnabled.put(monitor.getId(), monitor);
            }
        }
        createDatabaseChangeListener();
    }

    private void createDatabaseChangeListener() {
        /* listen for monitor adds/updates */
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.DEVICE_DATA_MONITOR,
                                                              EnumSet.of(DbChangeType.ADD,
                                                                         DbChangeType.UPDATE),
                                                                         new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                DeviceDataMonitor newMonitor =
                    deviceDataMonitorDao.getMonitorById(event.getPrimaryKey());

                monitorsMapAll.put(newMonitor.getId(), newMonitor);
                if (newMonitor.isEnabled()) {
                    monitorsMapEnabled.put(newMonitor.getId(), newMonitor);
                } else {
                    monitorsMapEnabled.remove(newMonitor.getId());
                }
            }
        });

        /* listen for monitor deletions */
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.DEVICE_DATA_MONITOR,
                                                              EnumSet.of(DbChangeType.DELETE),
                                                              new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                monitorsMapAll.remove(event.getPrimaryKey());
                monitorsMapEnabled.remove(event.getPrimaryKey());
            }
        });
    }

    @Override
    public List<DeviceDataMonitor> getAllMonitors() {
        return Lists.newArrayList(monitorsMapAll.values());
    }

    @Override
    public List<DeviceDataMonitor> getAllEnabledMonitors() {
        return Lists.newArrayList(monitorsMapEnabled.values());
    }

}
