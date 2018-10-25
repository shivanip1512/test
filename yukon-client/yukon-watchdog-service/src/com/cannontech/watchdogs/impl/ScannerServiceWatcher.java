package com.cannontech.watchdogs.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.google.common.collect.ImmutableSet;

/**
 * This class will validate Real-Time Scan Service Status in every 5 min and generate warning.
 */

@Service
public class ScannerServiceWatcher extends ServiceStatusWatchdogImpl implements PointDataListener {

    private static final Logger log = YukonLogManager.getLogger(ScannerServiceWatcher.class);

    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private DispatchServiceWatcher dispatchServiceWatcher;
    /** Point for process memory usage */
    private LitePoint memoryPoint;
    private Semaphore pointDataReceived = new Semaphore(0);
    private volatile Instant startedListening;
    /**
     * Load memory Point for Real-Time Scan Service.
     */

    @PostConstruct
    private void init() {
        BuiltInAttribute memoryAttribute = BuiltInAttribute.SCANNER_MEMORY_UTILIZATION;
        try {
            memoryPoint = attributeService.getPointForAttribute(PaoUtils.SYSTEM_PAOIDENTIFIER, memoryAttribute);
            asyncDynamicDataSource.registerForPointData(this, ImmutableSet.of(memoryPoint.getPointID()));

        } catch (IllegalUseOfAttribute e) {
            log.error("Attribute: [" + memoryAttribute + "] not found for pao type: [SYSTEM]");
        }
    }

    @Override
    public List<WatchdogWarnings> watch() {
        startedListening = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        pointDataReceived.drainPermits();
        ServiceStatus serviceStatus = getScannerServiceStatus();
        log.info("Status of SCANNER service " + serviceStatus);
        return generateWarning(WatchdogWarningType.YUKON_REAL_TIME_SCAN_SERVICE, serviceStatus);
    }

    /**
     * Get Real-Time Scan Service Status based on latest memory point data and current time.
     * if memory point data is received with in 2 min than Service status is RUNNING otherwise STOPPED.
     */

    private ServiceStatus getScannerServiceStatus() {
        try {
            boolean checkServiceStatus = pointDataReceived.tryAcquire(120, TimeUnit.SECONDS);
            if (checkServiceStatus || dispatchServiceWatcher.isServiceRunning()) {
                return checkServiceStatus ? ServiceStatus.RUNNING : ServiceStatus.STOPPED;
            }
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for service status", e);
        }

        return ServiceStatus.UNKNOWN;
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.REALTIMESCANNER;
    }

    @Override
    public void pointDataReceived(PointValueQualityHolder pointData) {
        Instant pointDataTimestamp = pointData.getPointDataTimeStamp().toInstant();
        if (startedListening != null && pointDataTimestamp.isAfter(startedListening)) {
            pointDataReceived.release();
        }
    }
}
