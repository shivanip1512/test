package com.cannontech.web.support.systemMetrics;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.support.dao.SystemMetricCriteriaDao;
import com.google.common.collect.Lists;

/**
 * System health criteria that checks to see if the number of meter archive requests exceed the number of meters in the
 * system.
 */
public class MeterRequestsEnqueuedCriteria implements MetricHealthCriteria {
    private static final String warningKey = "yukon.web.modules.support.systemHealth.criteria.meterEnqueuedCount";
    @Autowired SystemMetricCriteriaDao systemMetricCriteriaDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public SystemHealthMetricIdentifier getPertainsTo() {
        return SystemHealthMetricIdentifier.RFN_METER;
    }

    @Override
    public MetricStatusWithMessages checkMetric(SystemHealthMetric metric) {
        if (metric instanceof ExtendedQueueData && metric.getMetricIdentifier() == getPertainsTo()) {
            return doMetricCheck((ExtendedQueueData) metric);
        } else {
            throw new IllegalArgumentException("Criteria only supports " + getPertainsTo() + " metric");
        }
    }
    
    private MetricStatusWithMessages doMetricCheck(ExtendedQueueData metric) {
        
        int rfnMeterCount = systemMetricCriteriaDao.getRfnMeterCount();
        long enqueuedCount = metric.getEnqueuedCount();
        
        MetricStatusWithMessages status;
        if (enqueuedCount > rfnMeterCount) {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
            String message = accessor.getMessage(warningKey);
            status = new MetricStatusWithMessages(MetricStatus.WARN, Lists.newArrayList(message));
        } else {
            status = new MetricStatusWithMessages(MetricStatus.GOOD, new ArrayList<>());
        }
        
        return status;
    }

}
