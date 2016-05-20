package com.cannontech.web.support.systemMetrics;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

/**
 * Abstract base class for functionality shared between MetricHealthCriteria implementations.
 */
public abstract class MetricHealthCriteriaBase<T extends SystemHealthMetric> implements MetricHealthCriteria {
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private Class<T> metricClass;
    private String warnKeySuffix;
    private String errorKeySuffix;
    
    /**
     * @param metricClass The class representing the type of metric this criteria pertains to.
     * @param warnKeySuffix The portion of the i18n key after the last period, used when the metric state is WARN.
     * @param errorKeySuffix The portion of the i18n key after the last period, used when the metric state is ERROR.
     */
    public MetricHealthCriteriaBase(Class<T> metricClass, String warnKeySuffix, String errorKeySuffix) {
        this.metricClass = metricClass;
        this.warnKeySuffix = warnKeySuffix;
        this.errorKeySuffix = errorKeySuffix;
    }
    
    @Override
    public MetricStatusWithMessages checkMetric(SystemHealthMetric metric) {
        
        if (metricClass.isInstance(metric) && metric.getMetricIdentifier() == getPertainsTo()) {
            MetricStatus baseStatus = doMetricCheck(metricClass.cast(metric));
            
            if (baseStatus == MetricStatus.WARN){
                return buildMetricStatusWithMessages(baseStatus, warnKeySuffix);
            } else if (baseStatus == MetricStatus.ERROR) {
                return buildMetricStatusWithMessages(baseStatus, errorKeySuffix);
            } else {
                return new MetricStatusWithMessages(baseStatus);
            }
        }
        
        throw new IllegalArgumentException("Criteria only supports " + getPertainsTo() + " metric");
    }
    
    private MetricStatusWithMessages buildMetricStatusWithMessages(MetricStatus status, String keySuffix) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        String message = accessor.getMessage(keyPrefix + keySuffix);
        return new MetricStatusWithMessages(status, Lists.newArrayList(message));
    }
    
    /**
     * Implementors should implement this method to evaluate the 'health' of the metric in some way, then return an 
     * appropriate MetricStatus for the state of the metric.
     */
    protected abstract MetricStatus doMetricCheck(T metric);
    
}
