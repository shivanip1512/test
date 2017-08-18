package com.cannontech.services.smartNotification.service;

import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.common.worker.QueueProcessorSupervisor;
import com.cannontech.common.worker.SupervisedQueueProcessor;

/**
 * Responsible for supervising SmartNotificationMessageAssemblers, which pull SmartNotificationMessageParameters from
 * the assembler queue, build up SmartNotificationMessage objects, and push them to the Notification service to be
 * sent out into the outside world.
 */
public class SmartNotificationMessageAssemblerSupervisor extends QueueProcessorSupervisor {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationMessageAssemblerSupervisor.class);
    private static final String assemblerQueue = JmsApiDirectory.SMART_NOTIFICATION_ASSEMBLER.getQueue().getName();
    @Autowired private ConnectionFactory connectionFactory;
    
    public SmartNotificationMessageAssemblerSupervisor() {
        super(new WorkerSupervisorBuilder(assemblerQueue));
    }

    @Override
    protected SupervisedQueueProcessor getNewWorker() {
        return new SmartNotificationMessageAssembler(connectionFactory);
    }
}
