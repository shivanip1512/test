package com.cannontech.web.support.systemMetrics.criteria;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.support.systemMetrics.ExtendedQueueData;
import com.cannontech.web.support.systemMetrics.MetricStatus;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;

public class GatewayArchiveRequestQueueSizeCriteria extends MetricHealthCriteriaBase<ExtendedQueueData> {
    private static final Logger log = YukonLogManager.getLogger(GatewayArchiveRequestQueueSizeCriteria.class);
    
    public GatewayArchiveRequestQueueSizeCriteria() {
        super(ExtendedQueueData.class, "gatewayArchiveQueueSize");
    }
    
    @Override
    public SystemHealthMetricIdentifier getPertainsTo() {
        return SystemHealthMetricIdentifier.RF_GATEWAY_ARCHIVE;
    }
    
    @Override
    protected MetricStatus doMetricCheck(ExtendedQueueData metric) {
        long queueSize = metric.getQueueSize();
        
        log.debug("Gateway Archive Request Queue Size Criteria checked. Queue Size: " + queueSize);
        
        if (queueSize > 1) {
            return MetricStatus.WARN;
        }
        return MetricStatus.GOOD;
    }
}
