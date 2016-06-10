package com.cannontech.web.support.systemMetrics.criteria;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.support.dao.SystemMetricCriteriaDao;
import com.cannontech.web.support.systemMetrics.ExtendedQueueData;
import com.cannontech.web.support.systemMetrics.MetricStatus;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;

/**
 * System health criteria that checks to see if the number of LCR archive requests exceed the number of LCRs in the
 * system.
 */
public class LcrQueueSizeCriteria extends MetricHealthCriteriaBase<ExtendedQueueData> {
    private static final Logger log = YukonLogManager.getLogger(MetricHealthCriteria.class);
    @Autowired SystemMetricCriteriaDao systemMetricCriteriaDao;
    
    public LcrQueueSizeCriteria() {
        super(ExtendedQueueData.class, "lcrQueueSizeWarn", "lcrQueueSizeError");
    }
    
    @Override
    public SystemHealthMetricIdentifier getPertainsTo() {
        return SystemHealthMetricIdentifier.RFN_LCR;
    }
    
    @Override
    protected MetricStatus doMetricCheck(ExtendedQueueData metric) {
        int rfnLcrCount = systemMetricCriteriaDao.getRfnLcrCount();
        long queueSize = metric.getQueueSize();
        
        log.debug("Lcr Requests Queue Size Criteria checked. RfnLcrCount: " + rfnLcrCount + " queueSize: " 
                + queueSize);
        
        if (queueSize > rfnLcrCount) {
            return MetricStatus.ERROR;
        } else if ((queueSize * 2) > rfnLcrCount) {
            return MetricStatus.WARN;
        }
        return MetricStatus.GOOD;
    }
}
