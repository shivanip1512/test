package com.cannontech.watchdogs.impl;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.message.macs.message.RetrieveSchedule;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdogs.message.WatchdogMessageListener;
import com.cannontech.watchdogs.util.WatchdogMACSConnection;

/**
 * This class will validate MAC Scheduler Service Status in every 5 min and generate warning.
 */

@Service
public class MACSServiceWatcher extends ServiceStatusWatchdogImpl implements WatchdogMessageListener {
    public MACSServiceWatcher(ConfigurationSource configSource) {
        super(configSource);
    }

    private static final Logger log = YukonLogManager.getLogger(MACSServiceWatcher.class);

    @Autowired private ConnectionFactoryService connectionFactorySvc;

    private static WatchdogMACSConnection macsConnection;
    private volatile Instant receivedLatestMessageTimeStamp;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void doWatchAction() {
        receivedLatestMessageTimeStamp = null;
        createConnection();
        sendAllScheduleCommandToServer();
    }

    /**
     * Send ALL_SCHEDULES command to MAC Scheduler Service and based on Status,generate warning.
     */

    private void sendAllScheduleCommandToServer() {
        if (macsConnection.isValid()) {
            RetrieveSchedule newSchedules = new RetrieveSchedule();
            newSchedules.setUserName(CtiUtilities.getUserName());
            newSchedules.setPriority(14);
            newSchedules.setScheduleId(RetrieveSchedule.ALL_SCHEDULES);
            macsConnection.write(newSchedules);
        } else {
            log.info("Invalid connection with MAC Scheduler Service");
        }
        watchAndNotify();
    }

    /**
     * Creating Port Control client connection.
     */

    private WatchdogMACSConnection createConnection() {
        if (macsConnection == null) {
            macsConnection = new WatchdogMACSConnection();
            macsConnection.addWatchdogMessageListener(this);
            macsConnection.setConnectionFactory(connectionFactorySvc.findConnectionFactory("MACS"));
            macsConnection.connectWithoutWait();
        } else {
            return macsConnection;
        }

        for (int retry = 0; retry < 3; retry++) {
            if (macsConnection.isValid()) {
                break;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {}
        }

        return macsConnection;
    }

    @Override
    public List<WatchdogWarnings> watch() {
        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}

        if (receivedLatestMessageTimeStamp == null || !macsConnection.isValid()) {
            log.info("Status of MACS service " + ServiceStatus.STOPPED);
            return generateWarning(WatchdogWarningType.YUKON_MAC_SCHEDULER_SERVICE, ServiceStatus.STOPPED);
        } else {
            log.info("Status of MACS service " + ServiceStatus.RUNNING);
            return generateWarning(WatchdogWarningType.YUKON_MAC_SCHEDULER_SERVICE, ServiceStatus.RUNNING);
        }
    }

    @Override
    public void handleMessage(Message message) {
        log.debug("messageReceived: " + message.toString());
        receivedLatestMessageTimeStamp = Instant.now();
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.MACS;
    }

}
