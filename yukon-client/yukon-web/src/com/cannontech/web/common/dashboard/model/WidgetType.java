package com.cannontech.web.common.dashboard.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Enum of the types of widgets that can be displayed on customizable dashboards.
 */
public enum WidgetType implements DisplayableEnum {
    //Main Dashboard
    FAVORITES(DashboardScope.GENERAL, WidgetCategory.OTHER, "favoritesWidget", "image-coming-soon"),
    MONITOR_SUBSCRIPTIONS(DashboardScope.GENERAL, WidgetCategory.OTHER, "subscribedMonitorsWidget", "image-monitor-subscriptions"),
    MESSAGING_STATISTICS(DashboardScope.GENERAL, WidgetCategory.OTHER, "messagingStatisticsWidget", "image-coming-soon"),
    
    TREND(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI, "csrTrendWidget", "image-coming-soon"), 
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
    private String beanName;
    private String imageName;
    
    //TODO: do we need to allow multiple scopes or categories?
    private WidgetType(DashboardScope scope, WidgetCategory category, String beanName, String imageName) {
        this.scope = scope;
        this.category = category;
        this.beanName = beanName;
        this.imageName = imageName;
    }
    
    @Override
    public String getFormatKey() {
        return formatKeyBase + name() + ".name";
    }
    
    public String getDescriptionKey() {
        return formatKeyBase + name() + ".description";
    }
    
    public DashboardScope getScope() {
        return scope;
    }
    
    public WidgetCategory getCategory() {
        return category;
    }
    
    public String getBeanName() {
        return beanName;
    }
    
    public String getImageName() {
        return imageName;
    }
}
