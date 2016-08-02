package com.cannontech.web.support.systemMetrics.criteria;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.support.dao.SystemMetricCriteriaDao;
import com.cannontech.web.support.systemMetrics.ExtendedQueueData;
import com.cannontech.web.support.systemMetrics.MetricStatus;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;

/**
 * System health criteria that checks to see if the number of meter archive requests exceed the number of meters in the
 * system.
 */
public class MeterRequestsQueueSizeCriteria extends MetricHealthCriteriaBase<ExtendedQueueData> {
    private static final Logger log = YukonLogManager.getLogger(MetricHealthCriteria.class);
    @Autowired SystemMetricCriteriaDao systemMetricCriteriaDao;
    
    public MeterRequestsQueueSizeCriteria() {
        super(ExtendedQueueData.class, "meterQueueSize");
    }
    
    @Override
    public SystemHealthMetricIdentifier getPertainsTo() {
        return SystemHealthMetricIdentifier.RFN_METER;
    }
    
    @Override
    protected MetricStatus doMetricCheck(ExtendedQueueData metric) {
        int rfnMeterCount = systemMetricCriteriaDao.getRfnMeterCount();
        long queueSize = metric.getQueueSize();
        
        log.debug("Meter Requests Queue Size Criteria checked. RfnMeterCount: " + rfnMeterCount + " queueSize: " 
                  + queueSize);
        
        if (queueSize > rfnMeterCount) {
            return MetricStatus.ERROR;
        } else if ((queueSize * 2) > rfnMeterCount) {
            return MetricStatus.WARN;
        }
        return MetricStatus.GOOD;
    }

}
