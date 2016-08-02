package com.cannontech.web.support.systemMetrics.criteria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.support.systemMetrics.MetricStatus;
import com.cannontech.web.support.systemMetrics.MetricStatusWithMessages;
import com.cannontech.web.support.systemMetrics.SystemHealthMetric;
import com.google.common.collect.Lists;

/**
 * Abstract base class for functionality shared between MetricHealthCriteria implementations.
 */
public abstract class MetricHealthCriteriaBase<T extends SystemHealthMetric> implements MetricHealthCriteria {
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private Class<T> metricClass;
    private String keySuffix;
    
    /**
     * @param metricClass The class representing the type of metric this criteria pertains to.
     * @param keySuffix i18n keys for this criteria are composed of keyPrefix + keySuffix + use, where use is "warn", 
     * "error", "name", or "description".
     */
    public MetricHealthCriteriaBase(Class<T> metricClass, String keySuffix) {
        this.metricClass = metricClass;
        this.keySuffix = keySuffix;
    }
    
    @Override
    public MetricStatusWithMessages checkMetric(SystemHealthMetric metric) {
        
        if (metricClass.isInstance(metric) && metric.getMetricIdentifier() == getPertainsTo()) {
            MetricStatus baseStatus = doMetricCheck(metricClass.cast(metric));
            
            if (baseStatus == MetricStatus.WARN){
                return buildMetricStatusWithMessages(baseStatus, keySuffix + ".warn");
            } else if (baseStatus == MetricStatus.ERROR) {
                return buildMetricStatusWithMessages(baseStatus, keySuffix + ".error");
            } else {
                return new MetricStatusWithMessages(baseStatus);
            }
        }
        
        throw new IllegalArgumentException("Criteria only supports " + getPertainsTo() + " metric");
    }
    
    @Override
    public MessageSourceResolvable getMessage() {
        return new YukonMessageSourceResolvable(keyPrefix + keySuffix + ".description");
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
