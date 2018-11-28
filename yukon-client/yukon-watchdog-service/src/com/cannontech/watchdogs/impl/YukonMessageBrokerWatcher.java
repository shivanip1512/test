package com.cannontech.watchdogs.impl;

import static com.cannontech.common.config.MasterConfigString.JMS_SERVER_BROKER_LISTEN_CONNECTION;

import java.io.IOException;
import java.util.List;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

@Service
public class YukonMessageBrokerWatcher extends ServiceStatusWatchdogImpl {
    public YukonMessageBrokerWatcher(ConfigurationSource configSource) {
        super(configSource);
    }

    private static final Logger log = YukonLogManager.getLogger(YukonMessageBrokerWatcher.class);

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired protected ConfigurationSource configurationSource;

    private static volatile boolean isMsgBrokerRunning = false;

    @Override
    public void start() {
        startMessageBrokerListener();
    }
    
    /**
     * Starts listener for broker connection.
     */
    private void startMessageBrokerListener() {
        String brokerConnection = getBrokerConnection();
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerConnection);
        ActiveMQConnection connection;
        try {
            connection = (ActiveMQConnection) factory.createConnection();
            connection.addTransportListener(new ConnectionStateMonitor());

            if (!(connection.getBrokerInfo() == null)) {
                isMsgBrokerRunning = true;
            }
            log.info("Started listener on broker url: " + brokerConnection);
        } catch (JMSException e) {
            log.error("Could not start listener for broker " + brokerConnection);
        }
    }

    @Override
    public List<WatchdogWarnings> watch() {
        return generateWarning(WatchdogWarningType.YUKON_MESSAGE_BROKER, ServiceStatus.STOPPED);
    }

    /**
     * Creates a failover connection string to Yukon message broker.
     */
    private String getBrokerConnection() {
        String hostUri = "failover:tcp://" + globalSettingDao.getString(GlobalSettingType.JMS_BROKER_HOST);
        Integer port = globalSettingDao.getNullableInteger(GlobalSettingType.JMS_BROKER_PORT);

        String maxInactivityDuration = "wireFormat.MaxInactivityDuration="
            + (globalSettingDao.getInteger(GlobalSettingType.MAX_INACTIVITY_DURATION) * 1000);

        String jmsHost = hostUri + (port == null ? ":61616" : ":" + port) + "?" + maxInactivityDuration;
        String brokerConnection = configurationSource.getString(JMS_SERVER_BROKER_LISTEN_CONNECTION, jmsHost);
        log.debug("Broker connection url: ", brokerConnection);
        return brokerConnection;
    }

    private class ConnectionStateMonitor implements TransportListener {
        @Override
        public void onCommand(Object command) {
            log.debug("Command detected");
        }

        @Override
        public void onException(IOException exception) {
            log.info("Exception, broker may be down", exception);
            isMsgBrokerRunning = false;
            watchAndNotify();
        }

        @Override
        public void transportInterupted() {
            log.info("Transport interupted, broker may be down.");
            isMsgBrokerRunning = false;
            watchAndNotify();
        }

        @Override
        public void transportResumed() {
            log.info("Connected to broker");
            isMsgBrokerRunning = true;
        }
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.MESSAGEBROKER;
    }

    @Override
    public boolean isServiceRunning() {
        return isMsgBrokerRunning;
    }
}
