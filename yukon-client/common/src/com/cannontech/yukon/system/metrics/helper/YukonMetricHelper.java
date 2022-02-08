package com.cannontech.yukon.system.metrics.helper;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.jmx.JmxConnectorHelper;

public class YukonMetricHelper {

    private static final Logger log = YukonLogManager.getLogger(YukonMetricHelper.class);
    @Autowired private JmxConnectorHelper helper;
    @Autowired private ConfigurationSource configurationSource;

    private static long thresholdValue;

    @PostConstruct
    public void init() {
        thresholdValue = configurationSource.getLong("RFN_METER_DATA_WORKER_COUNT", 5)
                * configurationSource.getLong("RFN_METER_DATA_WORKER_QUEUE_SIZE", 500);
    }

    /**
     * Return queue size for the specified broker name & destination name.
     */
    public long getQueueSize(ApplicationId applicationId, String destinationName) {
        long queueSize = 0;
        try {
            JMXConnector connector = helper.getJMXConnector(applicationId);
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            ObjectName objectName = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + applicationId.getApplicationName()
                            + ",destinationType=Queue,destinationName=" + destinationName);
            DestinationViewMBean mbView = MBeanServerInvocationHandler.newProxyInstance(connection, objectName,
                    DestinationViewMBean.class, true);
            queueSize = mbView.getQueueSize();
        } catch (MalformedObjectNameException | IOException e) {
            log.error("Error occurred while retreiving queue size for " + destinationName + ": ", e);
        }
        return queueSize;
    }

    /**
     * Return dynamically calculated threshold value for archive requests.
     */
    public long getThresholdValueForArchiveRequests() {
        return thresholdValue;
    }

    /**
     * Return true if "escape valve" condition is satisfied. For example switch to 5 minute interval
     * data(intervalAfterEscapeValve) after the first 15 minutes(escapeValveTime).
     */
    public boolean checkForEscapeValve(int escapeValveTime, DateTime firstNotifiedTime, int intervalAfterEscapeValve) {
        boolean escapeValveAcheived = DateTime.now().minusMinutes(escapeValveTime).isAfter(firstNotifiedTime);
        boolean shouldWatchAfterEscapevalve = false;
        if (escapeValveAcheived) {
            int minutesDifference = DateTime.now().getMinuteOfDay() - firstNotifiedTime.getMinuteOfDay();
            if (minutesDifference % intervalAfterEscapeValve == 0) {
                shouldWatchAfterEscapevalve = true;
            }
        }
        return !escapeValveAcheived || shouldWatchAfterEscapevalve;
    }
}
