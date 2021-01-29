package com.cannontech.watchdogs.impl;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.message.tree.NetworkTreeUpdateTimeRequest;
import com.cannontech.common.rfn.message.tree.NetworkTreeUpdateTimeResponse;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.service.WatchdogWatcherService;

@Service
public class NetworkManagerWatcher extends ServiceStatusWatchdogImpl implements MessageListener{
    public NetworkManagerWatcher(ConfigurationSource configSource) {
        super(configSource);
    }

    private static final Logger log = YukonLogManager.getLogger(NetworkManagerWatcher.class);

    @Autowired private WatchdogWatcherService watcherService;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private YukonJmsTemplate jmsTemplate;
    private ServiceStatus currentStatus;

    public ServiceStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(ServiceStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    @PostConstruct
    public void initialize() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.NETWORK_TREE_UPDATE_REQUEST);
    }

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getNetworkManagerStatus();
        log.info("Communication status of network manager and yukon " + connectionStatus);
        return generateWarning(WatchdogWarningType.YUKON_NETWORK_MANAGER, connectionStatus);
    }

    /*
     * To check network manager status, Send NetworkTreeUpdateTimeRequest and wait for NetworkTreeUpdateTimeResponse.
     */
    private ServiceStatus getNetworkManagerStatus() {
        // Send the request and wait for the response
        setCurrentStatus(ServiceStatus.STOPPED);
        jmsTemplate.convertAndSend(new NetworkTreeUpdateTimeRequest());
        for (int retry = 0; retry < 3; retry++) {
            if (getCurrentStatus() == ServiceStatus.RUNNING) {
                break;
            }
            try {
                Thread.sleep(10000); // 10 seconds sleep.
            } catch (InterruptedException e) {
            }
        }
        return getCurrentStatus();
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.NETWORKMANAGER;
    }

    @Override
    public boolean shouldRun() {
        return watcherService.isServiceRequired(getServiceName());
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof NetworkTreeUpdateTimeResponse) {
                    setCurrentStatus(ServiceStatus.RUNNING);
                }
            } catch (JMSException e) {
                log.warn("Unable to extract NetworkTreeUpdateTimeResponse from message", e);
            }
        }
    }
}
