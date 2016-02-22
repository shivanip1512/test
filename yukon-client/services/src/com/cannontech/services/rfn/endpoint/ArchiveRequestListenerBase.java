package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.cannontech.amr.rfn.message.archive.RfnMeterReadingArchiveRequest;
import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.amr.rfn.service.RfnChannelDataConverter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.rfn.Acknowledgeable;
import com.cannontech.common.rfn.endpoint.IgnoredTemplateException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;

public abstract class ArchiveRequestListenerBase<T extends RfnIdentifyingMessage> {
    
    private static final Logger log = YukonLogManager.getLogger(ArchiveRequestListenerBase.class);
    private static final Logger rfnCommsLog = YukonLogManager.getRfnCommsLogger();

    @Autowired protected AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired protected RfnChannelDataConverter pointDataProducer;
    @Autowired protected RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private ConfigurationSource configurationSource;

    protected JmsTemplate jmsTemplate;
    private AtomicInteger processedArchiveRequest = new AtomicInteger();
    
    protected abstract class ConverterBase extends Thread {
        private ArrayBlockingQueue<T> inQueue;
        private volatile boolean shutdown = false;
        private int converterNumber;

        protected ConverterBase(String threadNameBase, int converterNumber, int queueSize) {
            super(threadNameBase + ": " + converterNumber);
            inQueue = new ArrayBlockingQueue<T>(queueSize);
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
                    processRequest(request);
                    if (log.isDebugEnabled()) {
                        log.debug("Processed Archive Request for " + request.getRfnIdentifier() + " on " + getName()
                                + ", queue size is: " + inQueue.size());
                    }
                    doCommsLogging(request);
                } catch (InterruptedException e) {
                    log.warn("received shutdown signal, queue size: " + inQueue.size());
                    break;
                } catch (Exception e) {
                    // consider using more "named" exceptions throughout this code
                    log.warn("Unknown exception while processing request", e);
                }
            }
        }

        /**
         * Decide how to handle RFN comms logging based on request type and log level.
         * @param request The RFN archive request sent from network manager.
         */
        private void doCommsLogging(T request) {
            Level logLevel = rfnCommsLog.getLevel();
            if (request instanceof RfnMeterReadingArchiveRequest) {
                if (Level.DEBUG.isGreaterOrEqual(logLevel)) {
                    rfnCommsLog.debug(">>> " + request.toString());
                } else if (Level.INFO.isGreaterOrEqual(logLevel)) {
                    RfnMeterReadingArchiveRequest meterRequest = (RfnMeterReadingArchiveRequest) request;
                    rfnCommsLog.info(">>> " +
                        String.format("RfnMeterReadingArchiveRequest [rfnIdentifier=%s, dataPointId=%s, readingType=%s]",
                                meterRequest.getRfnIdentifier(),
                                meterRequest.getDataPointId(),
                                meterRequest.getReadingType()));
                }
            } else {
                if (Level.INFO.isGreaterOrEqual(logLevel)) {
                    rfnCommsLog.info(">>> " + request.toString());
                }
            }
        }

        protected void processRequest(T request) {
            RfnIdentifier rfnIdentifier = request.getRfnIdentifier();
            if ("_EMPTY_".equals(rfnIdentifier.getSensorSerialNumber())
                || "_EMPTY_".equals(rfnIdentifier.getSensorManufacturer())
                || "_EMPTY_".equals(rfnIdentifier.getSensorModel())) {
                if (log.isInfoEnabled()) {
                    log.info("Serial Number:" + rfnIdentifier.getSensorSerialNumber() + " Sensor Manufacturer:"
                             + rfnIdentifier.getSensorManufacturer() + " Sensor Model:" + rfnIdentifier.getSensorModel());
                    log.info("Sending Acknowledgement");
                }
                sendAcknowledgement(request);
                return;
            }
            RfnDevice rfnDevice;
            try {
                rfnDeviceCreationService.incrementDeviceLookupAttempt();
                rfnDevice = rfnDeviceLookupService.getDevice(rfnIdentifier);
            } catch (NotFoundException e1) {
                // looks like we need to create the device
                try {
                    rfnDevice = processCreation(request, rfnIdentifier);
                } catch (RuntimeException e) {
                    boolean isDev = configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
                    boolean isAcknowledgeable = e.getCause() instanceof Acknowledgeable;
                    if (isDev || isAcknowledgeable) {
                        log.info("Exception:" + e.getMessage());
                        log.info("Sending Acknowledgement");
                        sendAcknowledgement(request);
                        return;
                    }
                    throw e;
                }
            }
            processData(rfnDevice, request);
        }

        /**
         * @param request The original request message.  This is used by subclasses.
         */
        protected RfnDevice processCreation(T request, RfnIdentifier identifier) {
            try {
                RfnDevice device = rfnDeviceCreationService.create(identifier);
                rfnDeviceCreationService.incrementNewDeviceCreated();
                if (log.isDebugEnabled()) {
                    log.debug("Created new device: " + device);
                }
                return device;
            } catch (IgnoredTemplateException e) {
                throw new RuntimeException("Unable to create device for " + identifier + " because template is ignored", e);
            } catch (BadTemplateDeviceCreationException e) {
                log.warn("Creation failed for " + identifier + ". Manufacture, Model and Serial Number combination do "
                    + "not match the template. This could be caused by an existing device with the same serial and "
                    + "manufacturer as the new device.", e);
                throw new RuntimeException("Creation failed for " + identifier, e);
            } catch (Exception e) {
                if (log.isTraceEnabled()) {
                    // Only log full exception when trace is on so lots of failed creations don't kill performance.
                    log.warn("Creation failed for " + identifier, e);
                } else {
                    log.warn("Creation failed for " + identifier +":" + e);
                }
                throw new RuntimeException("Creation failed for " + identifier, e);
            }
        }

        protected abstract void processData(RfnDevice device, T request);

        public void shutdown() {
            // shutdown mechanism assumes that
            shutdown = true;
            interrupt();
            drainQueue();
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
            inQueue = new ArrayBlockingQueue<List<CalculationData>>(queueSize);
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
    public int getProcessedArchiveRequest() {
        return processedArchiveRequest.get();
    }
    
    public void incrementProcessedArchiveRequest() {
        processedArchiveRequest.incrementAndGet();
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
    protected abstract String getRfnArchiveResponseQueueName();
    
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
            converter.queue(request);
        } catch (InterruptedException e) {
            log.warn("interrupted while queuing archive request", e);
            Thread.currentThread().interrupt();
        }
    }

    protected void sendAcknowledgement(T request) {
        Object response = getRfnArchiveResponse(request);
        String queueName = getRfnArchiveResponseQueueName();
        jmsTemplate.convertAndSend(queueName, response);
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}
