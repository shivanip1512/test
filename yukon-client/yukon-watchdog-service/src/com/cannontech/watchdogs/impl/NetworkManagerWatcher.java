package com.cannontech.watchdogs.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayDataRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.core.dao.NotFoundException;
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

    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private WatchdogWatcherService watcherService;

    private RequestReplyTemplate<GatewayDataResponse> requestTemplate;
    private static final String gatewayDataRequestQueue = "yukon.qr.obj.common.rfn.GatewayDataRequest";

    @PostConstruct
    public void initialize() {
        requestTemplate = new RequestReplyTemplateImpl<>("RF_GATEWAY_DATA", configurationSource, connectionFactory,
            gatewayDataRequestQueue, false, true);
    }

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getNetworkManagerStatus();
        log.info("Status of network manager " + connectionStatus);
        return generateWarning(WatchdogWarningType.YUKON_NETWORK_MANAGER, connectionStatus);
    }

    /*
     * To check network manager status, latest gateway is picked from Yukon and GatewayDataRequest is send.
     * If a response is received then NM is in running status else it is stopped.
     * If no gateway is found in system, we wont send a request and will not check status of NM.
     */
    private ServiceStatus getNetworkManagerStatus() {
        RfnIdentifier rfnIdentifier;
        try {
            rfnIdentifier = watcherService.getGatewayRfnIdentifier();
        } catch (NotFoundException e) {
            log.info("No Rfn Gateway found in yukon. Not checking status of Network Manager");
            return ServiceStatus.UNKNOWN;
        }

        GatewayDataRequest request = new GatewayDataRequest();
        request.setRfnIdentifier(rfnIdentifier);

        // Send the request and wait for the response
        BlockingJmsReplyHandler<GatewayDataResponse> replyHandler =
            new BlockingJmsReplyHandler<>(GatewayDataResponse.class);
        try {
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
