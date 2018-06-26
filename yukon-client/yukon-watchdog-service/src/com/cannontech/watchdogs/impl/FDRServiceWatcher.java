package com.cannontech.watchdogs.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

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
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

/**
 * This class will validate Foreign Data Service Status in every 5 min and generate warning.
 */

@Service
public class FDRServiceWatcher extends ServiceStatusWatchdogImpl {

    private static final Logger log = YukonLogManager.getLogger(FDRServiceWatcher.class);

    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    /** Point for process memory usage */
    private LitePoint memoryPoint;

    /**
     * Load memory Point for Foreign Data Service.
     */

    @PostConstruct
    private void init() {
        BuiltInAttribute memoryAttribute = BuiltInAttribute.FDR_MEMORY_UTILIZATION;
        try {
            memoryPoint = attributeService.getPointForAttribute(PaoUtils.SYSTEM_PAOIDENTIFIER, memoryAttribute);
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
        return YukonServices.FDR;
    }

}
