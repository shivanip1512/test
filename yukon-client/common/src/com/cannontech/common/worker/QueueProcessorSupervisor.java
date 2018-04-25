package com.cannontech.common.worker;

import static com.cannontech.common.config.MasterConfigString.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * This class provides a base for dynamically scaling the number of worker objects servicing a JMS queue.
 * A supervisor extending this class will monitor the specified queue's size at a configurable frequency
 * and create or destroy new workers depending on the quantity of messages in the queue.
 * 
 * Subclasses may configure the supervisor parameters via the Builder methods, or only specify a queue name to use
 * the default parameters.
 */
public abstract class QueueProcessorSupervisor {
    private static final Logger log = YukonLogManager.getLogger(QueueProcessorSupervisor.class);
    private static final List<SupervisedQueueProcessor> workers = new ArrayList<>();
    @Autowired private ConfigurationSource config;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired @Qualifier("main") private ScheduledExecutorService executor;
    private final int maxNumberOfWorkers;
    private final int minNumberOfWorkers;
    private final int maxQueuedMessagesPerWorker;
    private final int minQueuedMessagesPerWorker;
    private final int monitorFrequencySeconds;
    private final String queueName;
    private String messageBrokerJMXConnectionUrl;
    
    /**
     * The sub-class must implement this method to return a new, fully configured worker so that the supervisor may
     * instantiate new workers. The worker should not start processing until its start() method has been called.
     */
    protected abstract SupervisedQueueProcessor getNewWorker();
    
    /**
     * The sub-class should call this constructor, with a configured WorkerSupervisorBuilder, in its own constructor.
     */
    protected QueueProcessorSupervisor(WorkerSupervisorBuilder builder) {
        queueName = builder.queueName;
        maxNumberOfWorkers = builder.maxNumberOfWorkers;
        minNumberOfWorkers = builder.minNumberOfWorkers;
        maxQueuedMessagesPerWorker = builder.maxQueuedMessagesPerWorker;
        minQueuedMessagesPerWorker = builder.minQueuedMessagesPerWorker;
        monitorFrequencySeconds = builder.monitorFrequencySeconds;
        log.info("Created new worker supervisior for queue " + queueName);
        log.debug(this);
    }
    
    /**
     * This builder allows configuration of the supervisor's behavior.<br>
     * maxNumberOfWorkers - The supervisor will not create more workers than this, regardless of queue size.
     * minNumberOfWorkers - The supervisor will not destroy workers unless there are more than this number.
     * maxQueuedMessagesPerWorker - The supervisor will create a new worker if (messages on queue) divided by
     *   (number of workers) is greater than this number, and maxNumberOfWorkers has not been reached.
     * minQueuedMessagesPerWorker - The supervisor will destroy an existing worker if (messages on queue) divided by
     *   (number of workers) is less than this number, and minNumberOfWorkers has not been reached.
     */
    protected static class WorkerSupervisorBuilder {
        private int maxNumberOfWorkers = 4;
        private int minNumberOfWorkers = 1;
        private int maxQueuedMessagesPerWorker = 200;
        private int minQueuedMessagesPerWorker = 100;
        private int monitorFrequencySeconds = 30;
        private String queueName;
        
        public WorkerSupervisorBuilder(String queueName) {
            this.queueName = queueName;
        }
        
        public WorkerSupervisorBuilder maxNumberOfWorkers(int workers) {
            maxNumberOfWorkers = workers;
            return this;
        }
        
        public WorkerSupervisorBuilder minNumberOfWorkers(int workers) {
            minNumberOfWorkers = workers;
            return this;
        }
        
        public WorkerSupervisorBuilder maxQueuedMessagesPerWorker(int messages) {
            maxQueuedMessagesPerWorker = messages;
            return this;
        }
        
        public WorkerSupervisorBuilder minQueuedMessagesPerWorker(int messages) {
            minQueuedMessagesPerWorker = messages;
            return this;
        }
        
        public WorkerSupervisorBuilder monitorFrequencySeconds(int seconds) {
            monitorFrequencySeconds = seconds;
            return this;
        }
    }
    
    @PostConstruct
    public void init() {
        String hostUri = globalSettingDao.getString(GlobalSettingType.JMS_BROKER_HOST);
        String serverBrokerConnection = config.getString(JMS_SERVER_BROKER_LISTEN_CONNECTION);
        if (serverBrokerConnection != null) {
            hostUri = StringUtils.substringBetween(serverBrokerConnection, "//", ":");
        }
        messageBrokerJMXConnectionUrl = "service:jmx:rmi:///jndi/rmi://" + hostUri + ":1097/jmxrmi";
        
        initWorkers();
    }
    
    public void initWorkers() {
        //Create the minimum number of workers and start it
        for (int i = 0; i < minNumberOfWorkers; i++) {
            SupervisedQueueProcessor worker = getNewWorker();
            workers.add(worker);
            worker.start();
        }
        //Periodically, evaluate the number of messages in the queue
        executor.scheduleWithFixedDelay(() -> {
            getQueueSize().ifPresent(queueSize -> {
                //If there are not enough recorders, create and start a new one (up to a maximum)
                //If there are too many recorders, stop one and remove the reference (down to a minimum)
                if (!isMaxWorkersReached() && isTooManyMessagesPerWorker(queueSize)) {
                    SupervisedQueueProcessor worker = getNewWorker();
                    workers.add(worker);
                    worker.start();
                } else if (!isMinWorkersReached() && isTooFewMessagesPerWorker(queueSize)) {
                    workers.get(0).stop();
                    workers.remove(0);
                }
            });
        }, monitorFrequencySeconds, monitorFrequencySeconds, TimeUnit.SECONDS);
    }
    
    private boolean isTooManyMessagesPerWorker(int queueSize) {
        return queueSize / workers.size() > maxQueuedMessagesPerWorker;
    }
    
    private boolean isMaxWorkersReached() {
        return workers.size() == maxNumberOfWorkers;
    }
    
    private boolean isTooFewMessagesPerWorker(int queueSize) {
        return queueSize / workers.size() < minQueuedMessagesPerWorker;
    }
    
    private boolean isMinWorkersReached() {
        return workers.size() == minNumberOfWorkers;
    }
    
    /**
     * Uses JMX to retrieve the number of messages on the supervised queue. If the JMX call fails for any reason, the
     * supervisor will not adjust the worker count.
     */
    private Optional<Integer> getQueueSize() {
        try {
            JMXServiceURL messageBrokerServiceUrl = new JMXServiceURL(messageBrokerJMXConnectionUrl);
            JMXConnector messageBrokerJmxConnector = JMXConnectorFactory.connect(messageBrokerServiceUrl, null);
            MBeanServerConnection mBeanConn = messageBrokerJmxConnector.getMBeanServerConnection();
            Object object = mBeanConn.getAttribute(ObjectName.getInstance(queueName), "QueueSize");
            return Optional.of((Integer) object);
        } catch (Exception e) {
            log.debug("Unable to determine queue size. JMX error. Worker count will not be adjusted.", e);
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return "WorkerSupervisor [config=" + config + ", globalSettingDao=" + globalSettingDao + ", executor="
               + executor + ", maxNumberOfWorkers=" + maxNumberOfWorkers + ", minNumberOfWorkers=" + minNumberOfWorkers
               + ", maxQueuedMessagesPerWorker=" + maxQueuedMessagesPerWorker + ", minQueuedMessagesPerWorker="
               + minQueuedMessagesPerWorker + ", queueName=" + queueName + ", messageBrokerJMXConnectionUrl="
               + messageBrokerJMXConnectionUrl + "]";
    }
}
