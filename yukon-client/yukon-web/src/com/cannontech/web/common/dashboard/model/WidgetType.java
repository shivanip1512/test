package com.cannontech.web.common.dashboard.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.web.common.dashboard.widget.validator.MeterPickerValidator;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * Enum of the types of widgets that can be displayed on customizable dashboards.
 */
public enum WidgetType implements DisplayableEnum {
    //Main Dashboard
    FAVORITES(DashboardScope.GENERAL, WidgetCategory.OTHER, "favoritesWidget", "image-coming-soon"),
    MONITOR_SUBSCRIPTIONS(DashboardScope.GENERAL, WidgetCategory.OTHER, "subscribedMonitorsWidget", "image-monitor-subscriptions"),
    MESSAGING_STATISTICS(DashboardScope.GENERAL, WidgetCategory.OTHER, "messagingStatisticsWidget", "image-coming-soon"),
    
    TREND(DashboardScope.GENERAL, WidgetCategory.AMI, "csrTrendWidget", "image-trends"), 
    SYSTEM_HEALTH(DashboardScope.GENERAL, WidgetCategory.OTHER, "systemHealthWidget", "image-ami-actions"),

    //AMI Dashboard
    MONITORS(DashboardScope.GENERAL, WidgetCategory.AMI, "allMonitorsWidget", "image-monitors"),
    METER_SEARCH(DashboardScope.GENERAL, WidgetCategory.AMI, "meterSearchWidget", "image-meter-search"),
    AMI_ACTIONS(DashboardScope.GENERAL, WidgetCategory.AMI, "systemActionsMenuWidget", "image-ami-actions"),
    SCHEDULED_REQUESTS(DashboardScope.GENERAL, WidgetCategory.AMI, "scheduledGroupRequestExecutionWidget", "image-scheduled-requests"),
    GATEWAY_STREAMING_CAPACITY(DashboardScope.GENERAL, WidgetCategory.AMI, "overloadedGatewaysWidget", "image-gateway-streaming"),

    
    /*
    //Meter Detail
    METER_INFORMATION(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    METER_READINGS(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI),
    DEVICE_TREND(DashboardScope.DEVICE_SPECIFIC, WidgetCategory.AMI), //for device-specific dashboard
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
    
    private static final String formatKeyBase = "yukon.web.modules.dashboard.widgetType.";
    private static final Multimap<WidgetType, String> widgetSpecificJavascript;
    private static final Multimap<WidgetType, String> widgetSpecificCss;
    private static final Multimap<WidgetType, WidgetParameter> widgetParameters;
    
    static {
        widgetSpecificJavascript = ImmutableListMultimap.of(
            MESSAGING_STATISTICS, "yukon.support.systemHealth.js"
        );
        
        widgetSpecificCss = ImmutableListMultimap.of(
            //Add specialized CSS (that only needs to be loaded when the widget is present)
        );
        
        widgetParameters = ImmutableListMultimap.of(
            TREND, new WidgetParameter("deviceId", WidgetInputType.METER_PICKER, MeterPickerValidator.get())
        );
    }
    
    public static Stream<WidgetType> stream() {
        return Arrays.stream(values());
    }
    
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
    
    /**
     * @return The help text key, if help text is defined for this widget.
     */
    public String getHelpTextKey() {
        return formatKeyBase + name() + ".helpText";
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
    
    /**
     * @return The names of javscript libraries required by this widget.
     */
    public Set<String> getRequiredJavascript() {
        return ImmutableSet.copyOf(widgetSpecificJavascript.get(this));
    }
    
    /**
     * @return The names of css libraries required by this widget.
     */
    public Set<String> getRequiredCss() {
        return ImmutableSet.copyOf(widgetSpecificCss.get(this));
    }
    
    /**
     * @return The names of the user-specified parameters required by this widget.
     */
    public Set<String> getParameterNames() {
        return widgetParameters.get(this)
                               .stream()
                               .map(WidgetParameter::getName)
                               .collect(Collectors.toSet());
    }
    
    /**
     * @return The set of user-specified parameters required by this widget.
     */
    public Set<WidgetParameter> getParameters() {
        return ImmutableSet.copyOf(widgetParameters.get(this));
    }
}
