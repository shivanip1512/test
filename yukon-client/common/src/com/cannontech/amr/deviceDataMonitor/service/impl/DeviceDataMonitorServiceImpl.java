package com.cannontech.amr.deviceDataMonitor.service.impl;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.amr.monitors.message.DeviceDataMonitorMessage;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusRequest;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusResponse;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.jms.RequestTemplateImpl;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;

public class DeviceDataMonitorServiceImpl implements DeviceDataMonitorService {
    
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private ConfigurationSource configSource;

    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorServiceImpl.class);
    private JmsTemplate jmsTemplate;
    private static final String recalcQueueName = "yukon.qr.obj.amr.dataDeviceMonitor.RecalculateRequest";
    private static final String statusRequestQueueName = "yukon.qr.obj.amr.dataDeviceMonitor.RecalculateStatus";
    
    private RequestTemplateImpl<DeviceDataMonitorStatusResponse> statusRequestTemplate;
        
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
    public boolean areViolationsBeingCalculatedForMonitor(Integer monitorId) throws ExecutionException {
        
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
            throw e;
        }
    }

    private void asyncRecalculateViolatingPaosForMonitorBeforeSave(DeviceDataMonitor updatedMonitor, DeviceDataMonitor existingMonitor) {
        jmsTemplate.convertAndSend(recalcQueueName, new DeviceDataMonitorMessage(updatedMonitor, existingMonitor));
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