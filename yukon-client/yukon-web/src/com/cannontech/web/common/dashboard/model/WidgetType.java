package com.cannontech.web.common.dashboard.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Enum of the types of widgets that can be displayed on customizable dashboards.
 */
public enum WidgetType implements DisplayableEnum {
    //Main Dashboard
    FAVORITES(DashboardScope.GENERAL, WidgetCategory.OTHER),
    MONITOR_SUBSCRIPTIONS(DashboardScope.GENERAL, WidgetCategory.OTHER),
    MESSAGING_STATISTICS(DashboardScope.GENERAL, WidgetCategory.OTHER),
    /*
    //AMI Dashboard
    MONITORS(DashboardScope.GENERAL, WidgetCategory.AMI),
    METER_SEARCH(DashboardScope.GENERAL, WidgetCategory.AMI),
    AMI_ACTIONS(DashboardScope.GENERAL, WidgetCategory.AMI),
    SCHEDULED_REQUESTS(DashboardScope.GENERAL, WidgetCategory.AMI),
    //Meter Detail
    METER_INFORMATION(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    METER_READINGS(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    TREND(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI), //for device-specific dashboard
    DISCONNECT(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    CIS_INFORMATION(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    OUTAGES(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    TIME_OF_USE(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    DEVICE_GROUPS(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    METER_EVENTS(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    NETWORK_INFORMATION(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    DEVICE_CONFIGURATION(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    */
    ;
    
    private static String formatKeyBase = "yukon.web.modules.dashboard.widgetType.";
    
    private DashboardScope scope;
    private WidgetCategory category;
    
    //TODO: do we need to allow multiple scopes or categories?
    private WidgetType(DashboardScope scope, WidgetCategory category) {
        this.scope = scope;
        this.category = category;
    }
    
    @Override
    public String getFormatKey() {
        return formatKeyBase + name();
    }
    
    public DashboardScope getScope() {
        return scope;
    }
    
    public WidgetCategory getCategory() {
        return category;
    }
}
