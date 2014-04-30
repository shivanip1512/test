package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.amr.rfn.service.RfnChannelDataConverter;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.rfn.endpoint.IgnoredTemplateException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.DynamicDataSource;

public abstract class ArchiveRequestListenerBase<T extends RfnIdentifyingMessage> {

    private static final Logger log = YukonLogManager.getLogger(ArchiveRequestListenerBase.class);

    @Autowired protected DynamicDataSource dynamicDataSource;
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

        protected ConverterBase(int converterNumber, int queueSize) {
            super("RfnConverter: " + converterNumber);
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
                    log.debug("Proccessed Archive Request for " + request.getRfnIdentifier() + " on " + this.getName()
                              + ", queue size is: " + inQueue.size());
                } catch (InterruptedException e) {
                    log.warn("received shutdown signal, queue size: " + inQueue.size());
                    break;
                } catch (Exception e) {
                    // consider using more "named" exceptions throughout this code
                    log.warn("Unknown exception while processing request", e);
                }
            }
        }

        protected void processRequest(T request) {
            RfnIdentifier rfnIdentifier = request.getRfnIdentifier();
            if ("_EMPTY_".equals(rfnIdentifier.getSensorSerialNumber())) {
                if (configurationSource.getBoolean(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)) {
                    sendAcknowledgement(request);
                    return;
                }
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
                    if (configurationSource.getBoolean(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)) {
                        sendAcknowledgement(request);
                        return;
                    } else {
                        throw e;
                    }
                }
            }
            processData(rfnDevice, request);
        }

        protected RfnDevice processCreation(T request, RfnIdentifier identifier) {
            try {
                RfnDevice device = rfnDeviceCreationService.create(identifier);
                rfnDeviceCreationService.incrementNewDeviceCreated();
                LogHelper.debug(log, "Created new device: %s", device);
                return device;
            } catch (IgnoredTemplateException e) {
                throw new RuntimeException("Unable to create device for " + identifier + " because template is ignored", e);
            } catch (BadTemplateDeviceCreationException e) {
                LogHelper.warn(log, "Creation failed for %s. Manufacture, Model and Serial Number combination do not match the template. This could be caused by an existing device with the same serial and manufacturer as the new device.  %s", identifier, e);
                throw new RuntimeException("Creation failed for " + identifier, e);
            } catch (Exception e) {
                LogHelper.warn(log, "Creation failed for %s: %s", identifier, e);
                throw new RuntimeException("Creation failed for " + identifier, e);
            }
        }

        protected abstract void processData(RfnDevice device, T request);

        public void shutdown() {
            // shutdown mechanism assumes that
            shutdown = true;
            this.interrupt();
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
            this.interrupt();
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