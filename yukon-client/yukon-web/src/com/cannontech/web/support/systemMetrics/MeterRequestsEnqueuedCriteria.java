package com.cannontech.web.support.systemMetrics;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.support.dao.SystemMetricCriteriaDao;

/**
 * System health criteria that checks to see if the number of meter archive requests exceed the number of meters in the
 * system.
 */
public class MeterRequestsEnqueuedCriteria extends MetricHealthCriteriaBase<ExtendedQueueData> {
    private static final Logger log = YukonLogManager.getLogger(MetricHealthCriteria.class);
    @Autowired SystemMetricCriteriaDao systemMetricCriteriaDao;
    
    public MeterRequestsEnqueuedCriteria() {
        super(ExtendedQueueData.class, "meterEnqueuedCount", "");
    }
    
    @Override
    public SystemHealthMetricIdentifier getPertainsTo() {
        return SystemHealthMetricIdentifier.RFN_METER;
    }
    
    @Override
    protected MetricStatus doMetricCheck(ExtendedQueueData metric) {
        int rfnMeterCount = systemMetricCriteriaDao.getRfnMeterCount();
        long enqueuedCount = metric.getEnqueuedCount();
        
        log.debug("Meter Requests Enqueued Criteria checked. RfnMeterCount: " + rfnMeterCount + " enqueuedCount: " 
                  + enqueuedCount);
        
        if (enqueuedCount > rfnMeterCount) {
            return MetricStatus.WARN;
        }
        return MetricStatus.GOOD;
    }

}
