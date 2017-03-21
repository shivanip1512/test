package com.cannontech.web.common.dashboard.model;

/**
 * Describes the scope of widgets on this dashboard. For example, the main dashboard and AMI dashboard would be
 * GENERAL dashboards, but the meter detail page would be a DEVICE_SPECIFIC dashboard.  Widgets in may be usable in 
 * multiple scopes. 
 */
public enum DashboardScope {
    GENERAL,
    DEVICE_SPECIFIC,
    ;
}
