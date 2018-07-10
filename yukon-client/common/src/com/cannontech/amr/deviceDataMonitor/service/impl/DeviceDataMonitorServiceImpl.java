package com.cannontech.amr.deviceDataMonitor.service.impl;

import java.util.concurrent.ExecutionException;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.amr.monitors.message.DeviceDataMonitorMessage;
import com.cannontech.amr.monitors.message.DeviceDataMonitorMessage.Action;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusRequest;
import com.cannontech.amr.monitors.message.DeviceDataMonitorStatusResponse;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserPageType;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.common.util.jms.RequestTemplateImpl;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;

public class DeviceDataMonitorServiceImpl implements DeviceDataMonitorService {
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
    @Autowired private ConfigurationSource configSource;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    @Autowired private SmartNotificationSubscriptionService smartNotificationSubscriptionService;
    @Autowired private UserPageDao userPageDao;

    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorServiceImpl.class);
    private JmsTemplate jmsTemplate;
    private static final String recalcQueueName = "yukon.qr.obj.amr.dataDeviceMonitor.RecalculateRequest";
    private static final String statusRequestQueueName = "yukon.qr.obj.amr.dataDeviceMonitor.RecalculateStatus";
    
    private RequestTemplateImpl<DeviceDataMonitorStatusResponse> statusRequestTemplate;

    @Override
    public DeviceDataMonitor create(DeviceDataMonitor monitor) throws DuplicateException {
        deviceDataMonitorDao.save(monitor);
        jmsTemplate.convertAndSend(recalcQueueName, new DeviceDataMonitorMessage(monitor, null, Action.CREATE));
        return monitor;
    }
    
    @Override
    public DeviceDataMonitor update(DeviceDataMonitor monitor) throws DuplicateException {
        DeviceDataMonitor existingMonitor = deviceDataMonitorDao.getMonitorById(monitor.getId());
        deviceDataMonitorDao.save(monitor);
        jmsTemplate.convertAndSend(recalcQueueName, new DeviceDataMonitorMessage(monitor, existingMonitor,  Action.UPDATE));
        return monitor;
    }
    
    @Override
    public void delete(DeviceDataMonitor monitor, YukonUserContext userContext) {
        userSubscriptionDao.deleteSubscriptionsForItem(SubscriptionType.DEVICE_DATA_MONITOR, monitor.getId());
        smartNotificationSubscriptionService.deleteSubscriptions(SmartNotificationEventType.DEVICE_DATA_MONITOR, 
                                                                 monitor.getId().toString(), 
                                                                 monitor.getName(), userContext);
        deviceDataMonitorDao.deleteMonitor(monitor);
        userPageDao.deleteUserPages(monitor.getId(), UserPageType.MONITOR);
    }
    
    @Override
    public void recaclulate(DeviceDataMonitor monitor) {
        jmsTemplate.convertAndSend(recalcQueueName, new DeviceDataMonitorMessage(monitor, null, Action.RECALCULATE));
    }

    @Override
    public boolean toggleEnabled(int monitorId) throws NotFoundException {
        DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
        boolean newStatus = !monitor.isEnabled();
        monitor.setEnabled(newStatus);
        Action action = monitor.isEnabled()? Action.ENABLE : Action.DISABLE;
        deviceDataMonitorDao.save(monitor);
        jmsTemplate.convertAndSend(recalcQueueName, new DeviceDataMonitorMessage(monitor, action));
        log.info("Updated deviceDataMonitor enabled status: status=" + newStatus + ", deviceDataMonitor=" + monitor);
        return newStatus;
    }

    
    @Override
    public String getViolations(Integer monitorId) throws ExecutionException {
        log.debug("Check if violations being calculated for monitor id: " + monitorId);

        DeviceDataMonitorStatusRequest request = new DeviceDataMonitorStatusRequest(monitorId);
        BlockingJmsReplyHandler<DeviceDataMonitorStatusResponse> replyHandler =
            new BlockingJmsReplyHandler<>(DeviceDataMonitorStatusResponse.class);
        statusRequestTemplate.send(request, replyHandler);
    
        try {
            DeviceDataMonitorStatusResponse response = replyHandler.waitForCompletion();
            if(response.isWorkingOnObject()){
                return "CALCULATING";
            }
            if(response.getViolationCount() == null) {
                return "NA"; 
            }
           
            return String.valueOf(response.getViolationCount());
        } catch (ExecutionException e) {
            log.error("Error sending message to Service Manager", e);
            throw e;
        }
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
        
        statusRequestTemplate =
            new RequestTemplateImpl<>("DEVICE_DATA_MONITOR_CALC_STATUS",
                configSource, connectionFactory, statusRequestQueueName, false, true);
    }
}