package com.cannontech.watchdogs.impl;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.message.macs.message.RetrieveSchedule;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.util.ConnectionFactoryService;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdogs.message.WatchdogMessageListener;
import com.cannontech.watchdogs.util.WatchdogMACSConnection;

public class MACSServiceWatcher extends ServiceStatusWatchdogImpl implements WatchdogMessageListener {
    private static final Logger log = YukonLogManager.getLogger(MACSServiceWatcher.class);

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private ConnectionFactoryService connectionFactorySvc;

    private BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1);
    private static WatchdogMACSConnection macsConnection;

    @Override
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            createConnection();
            sendAllScheduleCommandtoServer();
        }, 0, 5, TimeUnit.MINUTES);
    }

    private void sendAllScheduleCommandtoServer() {
        if (macsConnection.isValid()) {
            RetrieveSchedule newSchedules = new RetrieveSchedule();
            newSchedules.setUserName(CtiUtilities.getUserName());
            newSchedules.setPriority(14);
            newSchedules.setScheduleId(RetrieveSchedule.ALL_SCHEDULES);
            macsConnection.write(newSchedules);
        } else {
            log.debug("Invalid connection with MAC Scheduler Service");
        }
        watchAndNotify();
    }

    public WatchdogMACSConnection createConnection() {
        if (macsConnection == null) {
            macsConnection = new WatchdogMACSConnection();
            macsConnection.addWatchdogMessageListener(this);
            macsConnection.setConnectionFactory(connectionFactorySvc.findConnectionFactory("MACS"));
            macsConnection.connectWithoutWait();
        } else {
            return macsConnection;
        }

        for (int retry = 0; retry < 5; retry++) {
            if (macsConnection.isValid()) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }

        return macsConnection;
    }

    @Override
    public List<WatchdogWarnings> watch() {
        Message message = null;
        try {
            message = queue.poll(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            /* ignore */
        }
        if (message == null) {
            log.debug("Status of MACS service " + ServiceStatus.STOPPED);
            return generateWarning(WatchdogWarningType.MACS_SERVICE_STATUS, ServiceStatus.STOPPED);
        } else {
            log.debug("Status of MACS service " + ServiceStatus.RUNNING);
            return generateWarning(WatchdogWarningType.MACS_SERVICE_STATUS, ServiceStatus.RUNNING);
        }
    }

    @Override
    public void handleMessage(Message message) {
        log.debug("messageReceived: " + message.toString());
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            /* ignore */
        }
    }

    public YukonServices getServiceName() {
        return YukonServices.MACS;
    }

}
