package com.cannontech.watchdogs.impl;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

@Service
public class YukonMessageBrokerWatcher extends ServiceStatusWatchdogImpl {
    private static final Logger log = YukonLogManager.getLogger(YukonMessageBrokerWatcher.class);
    private ActiveMQConnection connection;
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;

    @Override
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            if (connection == null) {
                startMessageBrokerListener();
            }
        }, 0, 2, TimeUnit.MINUTES);
    }

    private void startMessageBrokerListener() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        try {
            connection = (ActiveMQConnection) factory.createConnection();
            connection.addTransportListener(new ConnectionStateMonitor() {
            });
        } catch (JMSException e) {
            log.info("Broker Not Started");
        }
    }

    @Override
    public List<WatchdogWarnings> watch() {
        connection = null;
        return generateWarning(WatchdogWarningType.YUKON_MESSAGE_BROKER_CONNECTION_STATUS, ServiceStatus.STOPPED);
    }

    private class ConnectionStateMonitor implements TransportListener {
        @Override
        public void onCommand(Object command) {
            log.info("Command detected");
        }

        @Override
        public void onException(IOException exception) {
            log.info("Exception detected: '" + exception + "'");
            watchAndNotify();
        }

        @Override
        public void transportInterupted() {
            log.info("Transport Interupted detected");
        }

        @Override
        public void transportResumed() {
            log.info("Transport Resumed detected");
        }
    }
}
