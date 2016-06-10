package com.cannontech.web.support.systemMetrics.criteria;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.support.dao.SystemMetricCriteriaDao;
import com.cannontech.web.support.systemMetrics.MetricStatus;
import com.cannontech.web.support.systemMetrics.QueueData;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;

/**
 * System health criteria that checks to see if the number of gateway data responses exceed the number of gateways in 
 * the system.
 */
public class GatewayDataQueueSizeCriteria extends MetricHealthCriteriaBase<QueueData> {
    private static final Logger log = YukonLogManager.getLogger(GatewayDataQueueSizeCriteria.class);
    @Autowired SystemMetricCriteriaDao systemMetricCriteriaDao;
    
    public GatewayDataQueueSizeCriteria() {
        super(QueueData.class, "gatewayQueueSizeWarn", "gatewayQueueSizeError");
    }
    
    @Override
    public SystemHealthMetricIdentifier getPertainsTo() {
        return SystemHealthMetricIdentifier.RF_GATEWAY_DATA;
    }
    
    @Override
    protected MetricStatus doMetricCheck(QueueData metric) {
        int gatewayCount = systemMetricCriteriaDao.getRfGatewayCount();
        long queueSize = metric.getQueueSize();
        
        log.debug("Gateway Data Response Queue Size Criteria checked. GatewayCount: " + gatewayCount + " queueSize: " 
                + queueSize);
        
        if (queueSize > gatewayCount) {
            return MetricStatus.ERROR;
        } else if ((queueSize * 2) > gatewayCount) {
            return MetricStatus.WARN;
        }
        return MetricStatus.GOOD;
    }
}
