package com.cannontech.amr.deviceDataMonitor.service.impl;

import java.util.Collections;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.amr.monitors.DeviceDataMonitorCacheService;
import com.cannontech.amr.monitors.message.DeviceDataMonitorMessage;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusRequest;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusResponse;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.jms.RequestTemplateImpl;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public class DeviceDataMonitorServiceImpl implements DeviceDataMonitorService {
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private DeviceDataMonitorCacheService deviceDataMonitorCacheService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupProviderDao deviceGroupProviderDao;
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private ConfigurationSource configSource;

    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorServiceImpl.class);
    private JmsTemplate jmsTemplate;
    private static final String recalcQueueName = "yukon.qr.obj.amr.dataDeviceMonitor.RecalculateRequest";
    private static final String statusRequestQueueName = "yukon.qr.obj.amr.dataDeviceMonitor.RecalculateStatus";
    
    private RequestTemplateImpl<DeviceDataMonitorStatusResponse> statusRequestTemplate;
    
    @PostConstruct
    public void init() {
        
        createDatabaseChangeListener();
        
        for (DeviceDataMonitor monitor : deviceDataMonitorCacheService.getAllMonitors()) {
            asyncRecalculateViolatingPaosForMonitor(monitor);
        }
    }
    
    @Override
    public DeviceDataMonitor saveAndProcess(DeviceDataMonitor monitor) throws DuplicateException {
        DeviceDataMonitor existingMonitor = monitor.getId() != null ? deviceDataMonitorDao.getMonitorById(monitor.getId()) : null;
        asyncRecalculateViolatingPaosForMonitorBeforeSave(monitor, existingMonitor);
        deviceDataMonitorDao.save(monitor);
        return monitor;
    }
    
    @Override
    public int getMonitorViolationCountById(int monitorId) {
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        StoredDeviceGroup violationsGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.DEVICE_DATA, monitor.getViolationsDeviceGroupName(), false);
        int violationsCount = deviceGroupService.getDeviceCount(Collections.singleton(violationsGroup));
        return violationsCount;
    }

    @Override
    public boolean toggleEnabled(int monitorId) throws NotFoundException {
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        boolean newStatus = !monitor.isEnabled();
        monitor.setEnabled(newStatus);

        saveAndProcess(monitor);
        log.info("Updated deviceDataMonitor enabled status: status=" + newStatus + ", deviceDataMonitor=" + monitor);
        return newStatus;
    }
    
    @Override
    public void asyncRecalculateViolatingPaosForMonitor(DeviceDataMonitor monitor) {
        jmsTemplate.convertAndSend(recalcQueueName, new DeviceDataMonitorMessage(monitor));
    }
    
    @Override
    public boolean areViolationsBeingCalculatedForMonitor(Integer monitorId) {
        
        log.debug("Check if violations being calculated for monitor id: " + monitorId);
        boolean isWorkingOnObject = false;
        DeviceDataMonitorStatusRequest request = new DeviceDataMonitorStatusRequest(monitorId);
        BlockingJmsReplyHandler<DeviceDataMonitorStatusResponse> replyHandler =
            new BlockingJmsReplyHandler<>(DeviceDataMonitorStatusResponse.class);
        statusRequestTemplate.send(request, replyHandler);
    
        try {
            DeviceDataMonitorStatusResponse response = replyHandler.waitForCompletion();
            isWorkingOnObject = response.isWorkingOnObject();
            log.debug("monitor id: " + monitorId + " isWorkingOnObject=" + isWorkingOnObject);
            return isWorkingOnObject;
        } catch (ExecutionException e) {
            log.error("Error sending message to Service Manager", e);
        }
        return isWorkingOnObject;
    }

    private void asyncRecalculateViolatingPaosForMonitorBeforeSave(DeviceDataMonitor updatedMonitor, DeviceDataMonitor existingMonitor) {
        jmsTemplate.convertAndSend(recalcQueueName, new DeviceDataMonitorMessage(updatedMonitor, existingMonitor));
    }
    
    private void createDatabaseChangeListener() {
        // listen for devices added/removed from device groups
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.DEVICE_GROUP_MEMBER,
                                                              EnumSet.of(DbChangeType.ADD,
                                                                         DbChangeType.DELETE),
                                                                         new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                StoredDeviceGroup deviceGroupWithModifiedDevices = deviceGroupEditorDao.getGroupById(event.getPrimaryKey());
                String basePath = deviceGroupService.getFullPath(SystemGroupEnum.DEVICE_DATA);
                if (deviceGroupWithModifiedDevices.getFullName().contains(basePath)) {
                    // don't trigger violation count updates based on devices in the Device Data Monitor groups
                    return;
                }
                // get a list of our cached monitors from the processor factory. The factory is holding onto these instead of
                // this service because he needs to access it MUCH more frequently then this listener will
                // and it is an expensive operation to send this serialized list "over the wire"
                for (DeviceDataMonitor monitor: deviceDataMonitorCacheService.getAllEnabledMonitors()) {
                    if (!monitor.isEnabled()) {
                        continue;
                    }
                    try {
                        // this resolveGroupName uses the cache - so we're good
                        DeviceGroup monitoringGroup = deviceGroupService.resolveGroupName(monitor.getGroupName());
                        if (deviceGroupWithModifiedDevices.isEqualToOrDescendantOf(monitoringGroup)) {
                            // this deviceGroupWithModifiedDevices is a child of our monitoring group - so we need to recalculate our violations
                            LogHelper.debug(log, "detected add or remove from group [id: %s] - recalculating DDM [%s]", event.getPrimaryKey(), monitor.getName());
                            asyncRecalculateViolatingPaosForMonitor(monitor);
                        }
                    } catch (NotFoundException e) {
                        // user deleted monitoring group - oh well!
                        log.error("Could not find device group " + monitor.getGroupName() + " for device data monitor " + monitor.getName());
                    }
                }
            }
        });
        // listen for all ADD/UPDATE pao db change messages (there may be some overlap between this one and the
        // DEVICE_GROUP_MEMBER one above, which should be fine since the ServiceWorker queue will
        // handle that for us (duplicates))
        // we don't care about delete's b/c those will get removed automatically from our violation's group
        // if they were in there
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if (dbChange.getDatabase() == DBChangeMsg.CHANGE_PAO_DB && dbChange.getDbChangeType() != DbChangeType.DELETE) {
                    int paoId = dbChange.getId();
                    SimpleDevice yukonDevice = deviceDao.getYukonDevice(paoId);

                    for (DeviceDataMonitor monitor: deviceDataMonitorCacheService.getAllEnabledMonitors()) {
                        DeviceGroup monitoringGroup = deviceGroupService.resolveGroupName(monitor.getGroupName());
                        boolean deviceInMonitoringGroup = deviceGroupProviderDao.isDeviceInGroup(monitoringGroup, yukonDevice);
                        
                        if (deviceInMonitoringGroup) {
                            // recalculate the violating paos for this monitor if this paoId belongs to the monitoring group
                            LogHelper.debug(log, "pao change detected [id: %s] - recalculating DDM [%s]", paoId, monitor.getName());
                            asyncRecalculateViolatingPaosForMonitor(monitor);
                        }
                    }
                }
            }
        });
    }
    
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
        
        statusRequestTemplate =
            new RequestTemplateImpl<DeviceDataMonitorStatusResponse>("DEVICE_DATA_MONITOR_CALC_STATUS",
                configSource, connectionFactory, statusRequestQueueName, false);
    }
}