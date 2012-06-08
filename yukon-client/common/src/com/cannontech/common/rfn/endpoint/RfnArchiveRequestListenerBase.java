package com.cannontech.common.rfn.endpoint;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.PreDestroy;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.service.RfnMeterReadService;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifingMessage;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnArchiveRequestService;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.DynamicDataSource;

public abstract class RfnArchiveRequestListenerBase<T extends RfnIdentifingMessage> {

    private static final Logger log = YukonLogManager.getLogger(RfnArchiveRequestListenerBase.class);

    @Autowired protected DynamicDataSource dynamicDataSource;
    @Autowired protected RfnMeterReadService rfnMeterReadService;
    @Autowired protected RfnArchiveRequestService rfnArchiveRequestService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;

    protected JmsTemplate jmsTemplate;

    protected abstract class WorkerBase extends Thread {
        private ArrayBlockingQueue<T> inQueue;
        private volatile boolean shutdown = false;

        protected WorkerBase(int workerNumber, int queueSize) {
            super("RfnProcessor: " + workerNumber);
            inQueue = new ArrayBlockingQueue<T>(queueSize);
        }

        protected void queue(T request) throws InterruptedException {
            if (shutdown) {
                throw new IllegalStateException("worker has already shutdown");
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
                    processInitial(request);
                    log.debug("Proccessed Archive Request on " + this.getName()
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

        protected void processInitial(T archiveRequest) {
            RfnIdentifier rfnIdentifier = archiveRequest.getRfnIdentifier();
            RfnDevice rfnDevice;
            try {
                rfnArchiveRequestService.incrementDeviceLookupAttempt();
                rfnDevice = rfnDeviceLookupService.getDevice(rfnIdentifier);
            } catch (NotFoundException e1) {
                // looks like we need to create the device
                rfnDevice = processCreation(archiveRequest, rfnIdentifier);
            }
            processPointDatas(rfnDevice, archiveRequest);
        }

        protected RfnDevice processCreation(T archiveRequest, RfnIdentifier rfnIdentifier) {
            try {
                RfnDevice rfnDevice = rfnArchiveRequestService.createDevice(rfnIdentifier);
                rfnArchiveRequestService.incrementNewDeviceCreated();
                LogHelper.debug(log, "Created new device: %s", rfnDevice);
                return rfnDevice;
            } catch (IgnoredTemplateException e) {
                throw new RuntimeException("Unable to create device for " + rfnIdentifier + " because template is ignored", e);
            } catch (Exception e) {
                LogHelper.debug(log, "Creation failed for %s: %s", rfnIdentifier, e);
                throw new RuntimeException("Creation failed for " + rfnIdentifier, e);
            }
        }

        protected abstract void processPointDatas(RfnDevice rfnDevice, T archiveRequest);

        public void shutdown() {
            // shutdown mechanism assumes that
            shutdown = true;
            this.interrupt();
            drainQueue();
        }
    }

    public abstract void init();
    protected abstract List<? extends WorkerBase> getWorkers();
    protected abstract Object getRfnArchiveResponse(T archiveRequest);
    protected abstract String getRfnArchiveResponseQueueName();

    @PreDestroy
    protected abstract void shutdown();

    public void handleArchiveRequest(T archiveRequest) {
        // determine which worker will handle the request by hashing the serial number
        int hashCode = archiveRequest.getRfnIdentifier().getSensorSerialNumber().hashCode();
        List<? extends WorkerBase> workers = getWorkers();
        int workerIndex = Math.abs(hashCode) % workers.size();
        WorkerBase worker = workers.get(workerIndex);
        try {
            worker.queue(archiveRequest);
        } catch (InterruptedException e) {
            log.warn("interrupted while queuing archive request", e);
            Thread.currentThread().interrupt();
        }
    }

    protected void sendAcknowledgement(T archiveRequest) {
        Object response = getRfnArchiveResponse(archiveRequest);
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