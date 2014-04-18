package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.rfn.endpoint.IgnoredTemplateException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfDaCreationService;
import com.cannontech.da.rfn.message.archive.RfDaArchiveRequest;
import com.cannontech.da.rfn.message.archive.RfDaArchiveResponse;
import com.google.common.collect.ImmutableList;

@ManagedResource
public class RfDaArchiveRequestListener extends ArchiveRequestListenerBase<RfDaArchiveRequest> {
    
    private static final Logger log = YukonLogManager.getLogger(ArchiveRequestListenerBase.class);
    
    @Autowired protected RfDaCreationService rfdaCreationService;
    
    private static final String archiveResponseQueueName = "yukon.qr.obj.da.rfn.RfDaArchiveResponse";
    
    private List<Worker> workers;
    private final AtomicInteger archivedReadings = new AtomicInteger();
    
    public class Worker extends ConverterBase {
        public Worker(int workerNumber, int queueSize) {
            super(workerNumber, queueSize);
        }
        
        @Override
        protected RfnDevice processCreation(RfDaArchiveRequest archiveRequest, RfnIdentifier identifier) {
            try {
                RfnDevice device = rfdaCreationService.create(identifier);
                rfdaCreationService.incrementNewDeviceCreated();
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
        
        @Override
        public void processData(RfnDevice rfnDevice, RfDaArchiveRequest archiveRequest) {
        }
    }
    
    @Override
    @PostConstruct
    public void init() {
     // setup as many workers as requested
        ImmutableList.Builder<Worker> workerBuilder = ImmutableList.builder();
        int workerCount = getWorkerCount();
        int queueSize = getQueueSize();
        for (int i = 0; i < workerCount; ++i) {
            Worker worker = new Worker(i, queueSize);
            workerBuilder.add(worker);
            worker.start();
        }
        workers = workerBuilder.build();
    }
    
    @PreDestroy
    @Override
    protected void shutdown() {
        // should handle listener container here as well
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }
    
    @Override
    protected List<Worker> getConverters() {
        return workers;
    }

    @Override
    protected Object getRfnArchiveResponse(RfDaArchiveRequest archiveRequest) {
        RfDaArchiveResponse response = new RfDaArchiveResponse();
        response.setSensorId(archiveRequest.getSensorId());
        return response;
    }

    @Override
    protected String getRfnArchiveResponseQueueName() {
        return archiveResponseQueueName;
    }

    @ManagedAttribute
    public int getArchivedReadings() {
        return archivedReadings.get();
    }
    
}