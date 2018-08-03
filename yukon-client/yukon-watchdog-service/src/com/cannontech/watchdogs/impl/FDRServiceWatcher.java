package com.cannontech.watchdogs.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
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
 * This class will validate Foreign Data Service Status in every 5 min and generate warning.
 */

@Service
public class FDRServiceWatcher extends ServiceStatusWatchdogImpl implements PointDataListener {

    private static final Logger log = YukonLogManager.getLogger(FDRServiceWatcher.class);

    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    /** Point for process memory usage */
    private LitePoint memoryPoint;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private volatile Instant receivedLatestMessageTimeStamp;

    /**
     * Load memory Point for Foreign Data Service.
     */

    @PostConstruct
    private void init() {
        BuiltInAttribute memoryAttribute = BuiltInAttribute.FDR_MEMORY_UTILIZATION;
        try {
            memoryPoint = attributeService.getPointForAttribute(PaoUtils.SYSTEM_PAOIDENTIFIER, memoryAttribute);
            if (memoryPoint != null) {
                asyncDynamicDataSource.registerForPointData(this, ImmutableSet.of(memoryPoint.getPointID()));
            }
        } catch (IllegalUseOfAttribute e) {
            log.error("Attribute: [" + memoryAttribute + "] not found for pao type: [SYSTEM]");
        }
    }

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus serviceStatus = getFDRServiceStatus();
        log.debug("Status of FDR service " + serviceStatus);
        return generateWarning(WatchdogWarningType.FDR_SERVICE_STATUS, serviceStatus);
    }

    /**
     * Get Foreign Data Service Status based on latest memory point data and current time.
     * if memory point data is received with in 2 min than Service status is RUNNING otherwise STOPPED.
     */

    private ServiceStatus getFDRServiceStatus() {
        Instant startedListening = Instant.now();
        try {
            countDownLatch.await(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}

        return Optional.ofNullable(receivedLatestMessageTimeStamp)
                       .filter(ts -> ts.compareTo(startedListening) >= 0)
                       .map(ts -> ServiceStatus.RUNNING)
                       .orElse(ServiceStatus.STOPPED);
    }

    @Override
    public void pointDataReceived(PointValueQualityHolder pointData) {
        receivedLatestMessageTimeStamp = pointData.getPointDataTimeStamp().toInstant();
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.FDR;
    }

}
