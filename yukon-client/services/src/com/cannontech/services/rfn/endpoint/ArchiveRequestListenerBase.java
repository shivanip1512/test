package com.cannontech.services.rfn.endpoint;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.amr.rfn.service.RfnChannelDataConverter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.rfn.Acknowledgeable;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataTracker;
import com.cannontech.message.dispatch.message.PointData;

public abstract class ArchiveRequestListenerBase<T extends RfnIdentifyingMessage> {
    
    private static final Logger log = YukonLogManager.getLogger(ArchiveRequestListenerBase.class);

    @Autowired protected AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired protected RfnChannelDataConverter pointDataProducer;
    @Autowired protected RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private PointDataTracker pointDataTracker; 
    private static final Logger rfnLogger = YukonLogManager.getRfnLogger();
 
    protected abstract class ConverterBase extends Thread {
        private ArrayBlockingQueue<T> inQueue;
        private volatile boolean shutdown = false;
        private int converterNumber;

        protected ConverterBase(String threadNameBase, int converterNumber, int queueSize) {
            super(threadNameBase + ": " + converterNumber);
            inQueue = new ArrayBlockingQueue<>(queueSize);
            this.converterNumber = converterNumber;
        }
        
        public int getConverterNumber() {
            return converterNumber;
        }

        protected void queue(T request) throws InterruptedException {
            if (shutdown) {
                throw new IllegalStateException("converter has already shutdown");
            }
            inQueue.put(request);
            if (shutdown) {
                // oops, that was awkward timing
                drainQueue();
            }
        }

        protected void drainQueue() {
            // this method is called from multiple places, so it can't really do much
            inQueue.clear();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    T request = inQueue.take();
                    Optional<String> trackingInfo = Optional.empty();
                    try {
                        trackingInfo = processRequest(request);
                        if (log.isDebugEnabled()) {
                            log.debug("Processed Archive Request for " + request.getRfnIdentifier() + " on " + getName()
                                    + ", queue size is: " + inQueue.size());
                        } 
                    } catch (Exception e) {
                        // consider using more "named" exceptions throughout this code
                        log.warn("Unknown exception while processing request", e);
                    } 

                    doCommsLogging(request, trackingInfo);
                } catch (InterruptedException e) {
                    log.warn("received shutdown signal, queue size: " + inQueue.size());
                    return;
                }
            }
        }

        /**
         * Decide how to handle RFN comms logging based on request type and log level.
         * @param request The RFN archive request sent from network manager.
         * @param trackingInfo The tracking information for the request, if any.
         * @param pointDataIdentifiers 
         */
        private void doCommsLogging(T request, Optional<String> trackingInfo) {
            Logger rfnCommsLog = YukonLogManager.getRfnLogger();
            createLogEntry(request, trackingInfo, rfnCommsLog::isEnabled, rfnCommsLog::log);
        }
        
        /**
         * Creates a log entry for the request and tracking info, if any, depending on the enabled log level.
         * The log-related methods are passed as method references to limit coupling with the full Logger class. 
         * @param request The request to log.
         * @param trackingInfo The tracking info for the request, if any.
         * @param isEnabled A predicate to test whether the log level is enabled.
         * @param log A logging function that logs a string at a given log level.
         */
        protected void createLogEntry(T request, Optional<String> trackingInfo, Predicate<Level> isEnabled, BiConsumer<Level, String> log) {
            if (isEnabled.test(Level.INFO)) {
                log.accept(Level.INFO, ">>> " + request.toString() + delimited(trackingInfo));
            }
        }

        /**
         * Returns the string with a space separator if non-empty, otherwise returns an empty string.
         * @param field
         * @return The space-padded string, or an empty string if empty.
         */
        protected String delimited(Optional<String> field) {
            return field.map(t -> " " + t).orElse("");
        }
        
        /**
         * @param request The original request message.  This is used by subclasses.
         */
        protected RfnDevice processCreation(T request, RfnIdentifier identifier) {
           return rfnDeviceCreationService.createIfNotFound(identifier);
        }


        /**
         * Processes the request and returns any tracking information for the request
         * @param request the request to process
         * @return the tracking information, if any
         */
        protected Optional<String> processRequest(T request) {
            RfnIdentifier rfnIdentifier = request.getRfnIdentifier();
            if (rfnIdentifier.is_Empty_()) {
                if (log.isInfoEnabled()) {
                    log.info("Serial Number:" + rfnIdentifier.getSensorSerialNumber() + " Sensor Manufacturer:"
                            + rfnIdentifier.getSensorManufacturer() + " Sensor Model:" + rfnIdentifier.getSensorModel());
                }
                sendAcknowledgement(request);
                return Optional.empty();
            }
            try {
                RfnDevice rfnDevice = processCreation(request, rfnIdentifier);
                return processData(rfnDevice, request);
            } catch (RuntimeException e) {
                boolean isDev = configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
                boolean isAcknowledgeable = e.getCause() instanceof Acknowledgeable;
                if (isDev || isAcknowledgeable) {
                    log.info("Exception:" + e.getMessage());
                    sendAcknowledgement(request);
                    return Optional.empty();
                }
                throw e;
            }

        }

        /**
         * Processes the data in the request for the given device.
         * @return the tracking information for the request, if any. 
         */
        protected abstract Optional<String> processData(RfnDevice device, T request);

        public void shutdown() {
            // shutdown mechanism assumes that
            shutdown = true;
            interrupt();
            drainQueue();
        }

        /**
         * Adds tracking IDs to the pointData and returns them as a combined string for logging.
         * Optionally called by child classes to mark any pointData they generate.
         * @param messagesToSend The pointData to track.
         * @return The combined tracking information as a string, if any.
         */
        protected Optional<String> trackValues(Collection<PointData> messagesToSend) {
            return pointDataTracker.trackValues(messagesToSend);
        }
    }
    
    /**
     * These threads are for calculating point data based on other points being archived.
     * We do this in a separate thread pool to keep from bogging down the archiving pool.
     * We do not queue the calculated work until after the archive is done therefore avoiding
     * the need to create the meter since the archiving will have taken care of that; therefore
     * calculating should only be done if archive went well.
     */
    protected abstract class CalculatorBase extends Thread {
        private ArrayBlockingQueue<List<CalculationData>> inQueue;
        private volatile boolean shutdown = false;

        protected CalculatorBase(int calculatorNumber, int queueSize) {
            super("RfnCalculator: " + calculatorNumber);
            inQueue = new ArrayBlockingQueue<>(queueSize);
        }

        public void queue(List<CalculationData> toCalculate) throws InterruptedException {
            if (shutdown) {
                throw new IllegalStateException("calculator has already shutdown");
            }
            inQueue.put(toCalculate);
            if (shutdown) {
                // oops, that was awkward timing
                drainQueue();
            }
        }

        protected void drainQueue() {
            // this method is called from multiple places, so it can't really do much
            inQueue.clear();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    List<CalculationData> toCalculate = inQueue.take();
                    process(toCalculate);
                } catch (InterruptedException e) {
                    log.warn("received shutdown signal, queue size: " + inQueue.size());
                    break;
                } catch (Exception e) {
                    // consider using more "named" exceptions throughout this code
                    log.warn("Unknown exception while processing request", e);
                }
            }
        }

        protected abstract void process(List<CalculationData> toCalculate);

        public void shutdown() {
            shutdown = true;
            interrupt();
            drainQueue();
        }
    }
    
    
    @ManagedAttribute
    public int getWorkerCount() {
        return configurationSource.getInteger("RFN_METER_DATA_WORKER_COUNT", 5);
    }
    
    @ManagedAttribute
    public int getQueueSize() {
        return configurationSource.getInteger("RFN_METER_DATA_WORKER_QUEUE_SIZE", 500);
    }
    
    public abstract void init();
    protected abstract List<? extends ConverterBase> getConverters();
    protected abstract Object getRfnArchiveResponse(T archiveRequest);
    protected abstract YukonJmsTemplate getJmsTemplate();
    
    @PreDestroy
    protected abstract void shutdown();

    /**
     *  Determine which worker will handle the request by hashing the serial number.
     *  This ensures that only one thread will ever be used to create a particular
     *  meter, thus solving a bottleneck problem when multiple threads were trying
     *  to create the same meter.  The trade off is that if we recieve a large amount
     *  of readings for the same serial number in a row, only one thread will be 
     *  doing any work during that time.
     */
    public void handleArchiveRequest(T request) {
        int hashCode = request.getRfnIdentifier().getSensorSerialNumber().hashCode();
        List<? extends ConverterBase> converters = getConverters();
        int converterIndex = Math.abs(hashCode) % converters.size();
        ConverterBase converter = converters.get(converterIndex);
        try {
            log.debug(request);
            converter.queue(request);
        } catch (InterruptedException e) {
            log.warn("interrupted while queuing archive request", e);
            Thread.currentThread().interrupt();
        }
    }

    protected void sendAcknowledgement(T request) {
        Object response = getRfnArchiveResponse(request);
        YukonJmsTemplate jmsTemplate = getJmsTemplate();
        rfnLogger.info("<<< Sent " + response);
        jmsTemplate.convertAndSend(response);
    }

}
