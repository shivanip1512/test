package com.cannontech.web.support.systemMetrics.criteria;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.support.dao.SystemMetricCriteriaDao;
import com.cannontech.web.support.systemMetrics.MetricStatus;
import com.cannontech.web.support.systemMetrics.QueueData;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;

/**
 * System health criteria that checks to see if the number of RF DA archive requests exceed the number of RF DA devices
 * in the system.
 */
public class RfDaQueueSizeCriteria extends MetricHealthCriteriaBase<QueueData> {
    private static final Logger log = YukonLogManager.getLogger(RfDaQueueSizeCriteria.class);
    @Autowired SystemMetricCriteriaDao systemMetricCriteriaDao;
    
    public RfDaQueueSizeCriteria() {
        super(QueueData.class, "rfDaQueueSize");
    }
    
    @Override
    public SystemHealthMetricIdentifier getPertainsTo() {
        return SystemHealthMetricIdentifier.RF_DA;
    }
    
    @Override
    protected MetricStatus doMetricCheck(QueueData metric) {
        int rfDaCount = systemMetricCriteriaDao.getRfDaCount();
        long queueSize = metric.getQueueSize();
        
        log.debug("RF DA Requests Queue Size Criteria checked. RfDaCount: " + rfDaCount + " queueSize: " 
                + queueSize);
        
        if (queueSize > rfDaCount) {
            return MetricStatus.ERROR;
        } else if ((queueSize * 2) > rfDaCount) {
            return MetricStatus.WARN;
        }
        return MetricStatus.GOOD;
    }
}
