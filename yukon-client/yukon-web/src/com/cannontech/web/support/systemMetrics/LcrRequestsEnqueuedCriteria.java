package com.cannontech.web.support.systemMetrics;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.support.dao.SystemMetricCriteriaDao;

/**
 * System health criteria that checks to see if the number of LCR archive requests exceed the number of LCRs in the
 * system.
 */
public class LcrRequestsEnqueuedCriteria extends MetricHealthCriteriaBase<ExtendedQueueData> {
    private static final Logger log = YukonLogManager.getLogger(MetricHealthCriteria.class);
    @Autowired SystemMetricCriteriaDao systemMetricCriteriaDao;
    
    public LcrRequestsEnqueuedCriteria() {
        super(ExtendedQueueData.class, "lcrEnqueuedCount", "");
    }
    
    @Override
    public SystemHealthMetricIdentifier getPertainsTo() {
        return SystemHealthMetricIdentifier.RFN_LCR;
    }
    
    @Override
    protected MetricStatus doMetricCheck(ExtendedQueueData metric) {
        int rfnLcrCount = systemMetricCriteriaDao.getRfnLcrCount();
        long enqueuedCount = metric.getEnqueuedCount();
        
        log.debug("Lcr Requests Enqueued Criteria checked. RfnLcrCount: " + rfnLcrCount + " enqueuedCount: " 
                + enqueuedCount);
        
        if (enqueuedCount > rfnLcrCount) {
            return MetricStatus.WARN;
        }
        return MetricStatus.GOOD;
    }
}
