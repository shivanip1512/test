package com.cannontech.services.rfn.endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.RfnDeviceEventLogService;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.location.LocationResponse;
import com.cannontech.common.rfn.message.location.LocationResponseAck;
import com.cannontech.common.rfn.model.RfnDevice;
import com.google.common.collect.ImmutableList;

@ManagedResource
public class LocationArchiveResponseListener extends ArchiveRequestListenerBase<LocationResponse> {
    private static final Logger log = YukonLogManager.getLogger(LocationArchiveResponseListener.class);
    private static final String archiveResponseQueueName = "yukon.qr.obj.amr.rfn.LocationResponseAck";

    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private RfnDeviceEventLogService rfnDeviceEventLogService;

    private List<Worker> workers;
    private AtomicInteger processedAlarmArchiveRequest = new AtomicInteger();

    public class Worker extends ConverterBase {
        public Worker(int workerNumber, int queueSize) {
            super("LocationArchiveResponse", workerNumber, queueSize);
        }

        @Override
        protected void processData(RfnDevice device, LocationResponse location) {
            if (log.isDebugEnabled()) {
               log.debug("Recieved location for " + device.getName() + " " + device.getRfnIdentifier() +" "+location);
            }
            sendAcknowledgement(location);
            PaoLocation paoLocation = paoLocationDao.getLocation(device.getPaoIdentifier().getPaoId());
            if (paoLocation == null || location.getLastChangedDate().isAfter(paoLocation.getLastChangedDate())) {
                paoLocation =
                    new PaoLocation(device.getPaoIdentifier(), location.getLatitude(), location.getLongitude(),
                        location.getOrigin(), location.getLastChangedDate());
                paoLocationDao.save(paoLocation);
                rfnDeviceEventLogService.locationUpdated(device.getRfnIdentifier(), device.getName(),
                    device.getPaoIdentifier(), String.valueOf(location.getLatitude()),
                    String.valueOf(location.getLongitude()), location.getOrigin().name());
            }
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
    protected String getRfnArchiveResponseQueueName() {
        return archiveResponseQueueName;
    }

    @Override
    protected List<Worker> getConverters() {
        return workers;
    }

    @ManagedAttribute
    public int getProcessedAlarmArchiveRequest() {
        return processedAlarmArchiveRequest.get();
    }

    @Override
    protected Object getRfnArchiveResponse(LocationResponse location) {
        LocationResponseAck ack = new LocationResponseAck();
        ack.setLocationId(location.getLocationId());
        return ack;
    }
}
