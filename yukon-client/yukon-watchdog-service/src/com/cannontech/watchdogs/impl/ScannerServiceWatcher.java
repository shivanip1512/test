package com.cannontech.watchdogs.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.device.Device;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

public class ScannerServiceWatcher extends ServiceStatusWatchdogImpl {

    private static final Logger log = YukonLogManager.getLogger(ScannerServiceWatcher.class);

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    /** Point for process memory usage */
    private LitePoint memoryPoint;

    @Override
    public void start() {
        init();
        executor.scheduleAtFixedRate(() -> {
            watchAndNotify();
        }, 0, 5, TimeUnit.MINUTES);
    }

    private void init() {
        PaoIdentifier paoIdentifier = new PaoIdentifier(Device.SYSTEM_DEVICE_ID, PaoType.SYSTEM);

        BuiltInAttribute memoryAttribute = BuiltInAttribute.SCANNER_MEMORY_UTILIZATION;

        try {
            memoryPoint = attributeService.getPointForAttribute(paoIdentifier, memoryAttribute);
        } catch (IllegalUseOfAttribute e) {
            log.error("Attribute: [" + memoryAttribute + "] not found for pao type: [SYSTEM]");
        }
    }

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus serviceStatus = getScannerServiceStatus();
        log.debug("Status of SCANNER service " + serviceStatus);
        return generateWarning(WatchdogWarningType.SCANNER_SERVICE_STATUS, serviceStatus);
    }

    private ServiceStatus getScannerServiceStatus() {
        if (memoryPoint != null) {
            PointValueHolder pointData = asyncDynamicDataSource.getPointValue(memoryPoint.getPointID());
            long diff = Duration.between(pointData.getPointDataTimeStamp().toInstant(), Instant.now()).getSeconds();
            // Difference between current time and latest point data received
            if (pointData != null && diff <= 120) {
                return ServiceStatus.RUNNING;
            }
        }
        return ServiceStatus.STOPPED;

    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.REALTIMESCANNER;
    }

}
