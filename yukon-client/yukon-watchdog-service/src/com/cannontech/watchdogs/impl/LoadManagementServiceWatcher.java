package com.cannontech.watchdogs.impl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.service.WatchdogWatcherService;
import com.cannontech.watchdogs.message.WatchdogMessageListener;
import com.cannontech.watchdogs.util.WatchdogLoadManagementClientConnection;

/**
 * This class will validate Load Management Service Status in every 5 min and generate warning.
 */

@Service
public class LoadManagementServiceWatcher extends ServiceStatusWatchdogImpl implements WatchdogMessageListener {
    private static final Logger log = YukonLogManager.getLogger(LoadManagementServiceWatcher.class);

    @Autowired private ConnectionFactoryService connectionFactorySvc;
    @Autowired private WatchdogWatcherService watcherService;

    private Instant receivedLatestMessageTimeStamp;
    private Instant sendMessageTimeStamp;
    private static WatchdogLoadManagementClientConnection clientConnection;
    
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void doWatchAction() {
        receivedLatestMessageTimeStamp = null;
        createConnection();
        retrieveAllControlAreasFromServer();
    }

    /**
     * Send RETRIEVE_ALL_CONTROL_AREAS command to Load Management Service and based on Status, generate
     * warning and disconnect from service.
     */

    private void retrieveAllControlAreasFromServer() {
        sendMessageTimeStamp =  OffsetDateTime.now().withNano(0).toInstant();
        if (clientConnection.isValid()) {
            LMCommand cmd = new LMCommand();
            cmd.setPriority(14);
            cmd.setCommand(LMCommand.RETRIEVE_ALL_CONTROL_AREAS);
            clientConnection.write(cmd);
        } else {
            log.debug("Invalid connection with LoadManagement Service ");
        }
        watchAndNotify();
        clientConnection.disconnect();
    }

    /**
     * Creating Load Management client connection.
     */

    private WatchdogLoadManagementClientConnection createConnection() {

        clientConnection = new WatchdogLoadManagementClientConnection();
        clientConnection.addWatchdogMessageListener(this);
        clientConnection.setConnectionFactory(connectionFactorySvc.findConnectionFactory("LC"));
        clientConnection.connectWithoutWait();

        for (int retry = 0; retry < 5; retry++) {
            if (clientConnection.isValid()) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        return clientConnection;
    }

    @Override
    public List<WatchdogWarnings> watch() {

        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}

        if (receivedLatestMessageTimeStamp == null || (receivedLatestMessageTimeStamp.isBefore(sendMessageTimeStamp)) || !clientConnection.isValid()) {
            log.debug("Status of LoadManagement service " + ServiceStatus.STOPPED);
            return generateWarning(WatchdogWarningType.LOADMANAGEMENT_SERVICE_STATUS, ServiceStatus.STOPPED);
        } else {
            log.debug("Status of LoadManagement service " + ServiceStatus.RUNNING);
            return generateWarning(WatchdogWarningType.LOADMANAGEMENT_SERVICE_STATUS, ServiceStatus.RUNNING);
        }

    }

    @Override
    public void handleMessage(Message message) {
        log.debug("messageReceived: " + message.toString());
        Instant timeStamp = message.getTimeStamp().toInstant();
        synchronized (this) {
            if (receivedLatestMessageTimeStamp == null || timeStamp.isAfter(receivedLatestMessageTimeStamp)) {
                receivedLatestMessageTimeStamp = timeStamp;
            }
        }
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.LOADMANAGEMENT;
    }
    
    @Override
    public boolean shouldRun() {
        return watcherService.isServiceRequired(getServiceName());
    }

}
