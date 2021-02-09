package com.cannontech.watchdogs.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.nmHeartbeat.message.NetworkManagerHeartbeatRequest;
import com.cannontech.common.nmHeartbeat.message.NetworkManagerHeartbeatResponse;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.service.WatchdogWatcherService;

@Service
public class NetworkManagerWatcher extends ServiceStatusWatchdogImpl {
    public NetworkManagerWatcher(ConfigurationSource configSource) {
        super(configSource);
    }

    private static final Logger log = YukonLogManager.getLogger(NetworkManagerWatcher.class);

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private WatchdogWatcherService watcherService;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private RequestReplyTemplate<NetworkManagerHeartbeatResponse> requestTemplate;
    private static final String NM_WATCHDOG_HEARTBEAT_MESSAGEID = "NM-Watchdog-Heartbeat";

    @PostConstruct
    public void initialize() {
        YukonJmsTemplate jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.NM_HEARTBEAT);
        requestTemplate = new RequestReplyTemplateImpl<>("NM_HEARTBEAT", configurationSource, jmsTemplate, true);
    }

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getNetworkManagerStatus();
        log.info("Communication status of network manager and yukon " + connectionStatus);
        return generateWarning(WatchdogWarningType.YUKON_NETWORK_MANAGER, connectionStatus);
    }

    /*
     * To check network manager status, Send NetworkManagerHeartbeatRequest and wait for NetworkManagerHeartbeatResponse.
     */
    private ServiceStatus getNetworkManagerStatus() {
        // Send the request and wait for the response
        BlockingJmsReplyHandler<NetworkManagerHeartbeatResponse> replyHandler =
                new BlockingJmsReplyHandler<>(NetworkManagerHeartbeatResponse.class);
            try {
                NetworkManagerHeartbeatRequest request = new NetworkManagerHeartbeatRequest();
                log.debug("Sending NM watchdog heartbeat message.");
                request.setMessageId(NM_WATCHDOG_HEARTBEAT_MESSAGEID);
                requestTemplate.send(request, replyHandler);
                replyHandler.waitForCompletion();
                return ServiceStatus.RUNNING;
            } catch (ExecutionException e) {
                log.error("Unable to send request due to a communication error between Yukon and Network Manager.");
                return ServiceStatus.STOPPED;
            }
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.NETWORKMANAGER;
    }

    @Override
    public boolean shouldRun() {
        return watcherService.isServiceRequired(getServiceName());
    }
}
