package com.cannontech.dr.eatonCloud.job.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.smartNotification.model.EatonCloudDrEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobReadService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobSmartNotifService;
import com.cannontech.yukon.IDatabaseCache;

public class EatonCloudJobSmartNotifServiceImpl implements EatonCloudJobSmartNotifService {
    
    private static final Logger log = YukonLogManager.getLogger(EatonCloudJobSmartNotifServiceImpl.class);
    
    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired EatonCloudJobReadService eatonCloudJobReadService;

    private int failureNotificationPercent;

    @PostConstruct
    public void init() {
        failureNotificationPercent = configurationSource.getInteger(
                MasterConfigInteger.EATON_CLOUD_NOTIFICATION_COMMAND_FAILURE_PERCENT, 25);
    }

    @Override
    public void sendSmartNotifications(int programId, int groupId, int totalDevices, int totalFailed, String debugText) {
        if (totalFailed == 0) {
            return;
        }
        boolean sendNotification = (totalFailed * 100) / totalDevices > failureNotificationPercent;
        if (sendNotification) {
            String program = dbCache.getAllPaosMap().get(programId).getPaoName();
            String group = dbCache.getAllPaosMap().get(groupId).getPaoName();
            SmartNotificationEvent event = EatonCloudDrEventAssembler.assemble(group, program, totalDevices, totalFailed);

            log.info(debugText+ " Sending smart notification event: {}", event);
            smartNotificationEventCreationService.send(SmartNotificationEventType.EATON_CLOUD_DR, List.of(event));
        }
    }
}
