package com.cannontech.watchdogs.impl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.service.WatchdogWatcherService;
import com.cannontech.watchdogs.message.WatchdogMessageListener;
import com.cannontech.watchdogs.util.WatchdogCapControlClientConnection;

/**
 * This class will validate Cap Control Status in every 5 min and generate warning.
 */

@Service
public class CapControlServiceWatcher extends ServiceStatusWatchdogImpl implements WatchdogMessageListener {

    public CapControlServiceWatcher(ConfigurationSource configSource) {
        super(configSource);
    }

    private static final Logger log = YukonLogManager.getLogger(CapControlServiceWatcher.class);

    @Autowired private ConnectionFactoryService connectionFactorySvc;
    @Autowired private WatchdogWatcherService watcherService;

    private static WatchdogCapControlClientConnection clientConnection;
    private volatile Instant receivedLatestMessageTimeStamp;
    private Instant sendMessageTimeStamp;
    
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void doWatchAction() {
        receivedLatestMessageTimeStamp = null;
        createConnection();
        sendRequestAllDataCommandToServer();

    }

    /**
     * Send REQUEST_ALL_DATA command to Cap Control Service and based on Status,
     * generate warning and disconnect from service.
     */
    private void sendRequestAllDataCommandToServer() {
        // Received timestamp from c++ with 0 nanoseconds and Instant.now() gives timestamp with
        // nanoseconds precision
        sendMessageTimeStamp = OffsetDateTime.now().withNano(0).toInstant();
        if (clientConnection.isValid()) {
            clientConnection.sendCommand(new CapControlCommand(CommandType.REQUEST_ALL_DATA.getCommandId()));
        } else {
            log.info("Invalid connection with CapControl Service");
        }
        watchAndNotify();
    }

    /**
     * Creating Cap Control client connection.
     */

    private WatchdogCapControlClientConnection createConnection() {
        if (clientConnection == null) {
            clientConnection = new WatchdogCapControlClientConnection();
            clientConnection.addWatchdogMessageListener(this);
            clientConnection.setConnectionFactory(connectionFactorySvc.findConnectionFactory("CBC"));
            clientConnection.connectWithoutWait();
        } else {
            return clientConnection;
        }
        for (int retry = 0; retry < 3; retry++) {
            if (clientConnection.isValid()) {
                break;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {}
        }
        return clientConnection;
    }

    @Override
    public List<WatchdogWarnings> watch() {
        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}

        if (receivedLatestMessageTimeStamp == null || !clientConnection.isValid()) {
            log.info("Status of CapControl service " + ServiceStatus.STOPPED);
            return generateWarning(WatchdogWarningType.YUKON_CAP_CONTROL_SERVICE, ServiceStatus.STOPPED);
        } else {
            log.info("Status of CapControl service " + ServiceStatus.RUNNING);
            return generateWarning(WatchdogWarningType.YUKON_CAP_CONTROL_SERVICE, ServiceStatus.RUNNING);
        }

    }

    @Override
    public void handleMessage(Message message) {
        log.debug("messageReceived: " + message.toString());
        Instant timeStamp = message.getTimeStamp().toInstant();
        if (sendMessageTimeStamp != null) {
            Instant compareTimeStamp = Optional.ofNullable(receivedLatestMessageTimeStamp).orElse(sendMessageTimeStamp);
            if (timeStamp.compareTo(compareTimeStamp) >= 0) {
                receivedLatestMessageTimeStamp = timeStamp;
            }
        }
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.CAPCONTROL;
    }

    @Override
    public boolean shouldRun() {
        return watcherService.isServiceRequired(getServiceName());
    }
}
