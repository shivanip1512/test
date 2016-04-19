package com.cannontech.web.support.service;

import java.util.Collection;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.support.SystemHealthMetric;
import com.cannontech.web.support.SystemHealthMetricIdentifier;
import com.cannontech.web.support.SystemHealthMetricType;
import com.google.common.collect.Multimap;

/**
 * Service that handles system health metrics.
 */
public interface SystemHealthService {
    
    /**
     * Retrieves the current data for the specified metric.
     */
    SystemHealthMetric getMetric(SystemHealthMetricIdentifier metric);
    
    /**
     * Retrieves the current data for all metrics of the specified type.
     */
    List<SystemHealthMetric> getMetricsByType(SystemHealthMetricType type);
    
    /**
     * Retrieves the current data for all metrics with the specified identifiers.
     */
    Multimap<SystemHealthMetricType, SystemHealthMetric> getMetricsByIdentifiers(Collection<SystemHealthMetricIdentifier> metricIds);
    
    /**
     * Retrieves the identifiers of favorite metrics for the specified user.
     */
    List<SystemHealthMetricIdentifier> getFavorites(LiteYukonUser user);
    
    /**
     * Favorites or un-favorites the specified metric for the user.
     * @param isFavorite If true, makes the metric a favorite. If false, un-favorites the metric.
     */
    void setFavorite(LiteYukonUser user, SystemHealthMetricIdentifier metric, boolean isFavorite);
    
}
