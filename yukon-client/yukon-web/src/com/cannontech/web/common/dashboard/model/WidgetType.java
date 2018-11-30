package com.cannontech.web.common.dashboard.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.common.dashboard.widget.service.WidgetService;
import com.cannontech.web.common.dashboard.widget.validator.ControlAreaOrProgramOrScenarioPickerValidator;
import com.cannontech.web.common.dashboard.widget.validator.DeviceGroupPickerValidator;
import com.cannontech.web.common.dashboard.widget.validator.MeterPickerValidator;
import com.cannontech.web.common.dashboard.widget.validator.MonitorPickerValidator;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * Enum of the types of widgets that can be displayed on customizable dashboards.
 */
public enum WidgetType implements DisplayableEnum {
    //Main Dashboard
    FAVORITES(DashboardScope.GENERAL, WidgetCategory.OTHER, "favoritesWidget", "image-favorites"),
    MONITOR_SUBSCRIPTIONS(DashboardScope.GENERAL, WidgetCategory.OTHER, "subscribedMonitorsWidget", "image-monitor-subscriptions"),
    SYSTEM_MESSAGING(DashboardScope.GENERAL, WidgetCategory.OTHER, "systemMessagingWidget", "image-system-messaging"),
    INFRASTRUCTURE_WARNINGS(DashboardScope.GENERAL, WidgetCategory.OTHER, "infrastructureWarningsWidget", "image-infrastructure-warnings"),
    PAO_NOTES_SEARCH(DashboardScope.GENERAL, WidgetCategory.OTHER, "paoNotesSearchWidget", "image-notes-search"),

    TREND(DashboardScope.GENERAL, WidgetCategory.AMI, "csrTrendWidget", "image-trends"), 
    PORTER_QUEUE_COUNTS(DashboardScope.GENERAL, WidgetCategory.AMI, "porterQueueCountsWidget", "image-porter-queue-counts"),

    //AMI Dashboard
    MONITORS(DashboardScope.GENERAL, WidgetCategory.AMI, "allMonitorsWidget", "image-monitors"),
    METER_SEARCH(DashboardScope.GENERAL, WidgetCategory.AMI, "meterSearchWidget", "image-meter-search"),
    AMI_ACTIONS(DashboardScope.GENERAL, WidgetCategory.AMI, "systemActionsMenuWidget", "image-ami-actions"),
    SCHEDULED_REQUESTS(DashboardScope.GENERAL, WidgetCategory.AMI, "scheduledGroupRequestExecutionWidget", "image-scheduled-requests"),
    GATEWAY_STREAMING_CAPACITY(DashboardScope.GENERAL, WidgetCategory.AMI, "overloadedGatewaysWidget", "image-gateway-streaming", MasterConfigBoolean.RF_DATA_STREAMING_ENABLED.name()),
    DATA_COLLECTION(DashboardScope.GENERAL, WidgetCategory.AMI, "dataCollectionWidget", "image-data-collection"),
    ASSET_AVAILABILITY(DashboardScope.GENERAL, WidgetCategory.DR, "assetAvailabilityWidget", "image-asset-availability"),
    RF_BROADCAST(DashboardScope.GENERAL, WidgetCategory.DR, "rfBroadcastWidget", "image-rf-broadcast", GlobalSettingType.RF_BROADCAST_PERFORMANCE.name());
    
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
    private static final String baseJSPath = "/resources/js/widgets/";
    private static final ImmutableListMultimap<WidgetType, String> widgetSpecificJavascript;
    private static final Multimap<WidgetType, String> widgetSpecificCss;
    private static final Multimap<WidgetType, WidgetParameter> widgetParameters;
    
    static {
        Builder<WidgetType, String> javascriptBuilder = new ImmutableListMultimap.Builder<WidgetType, String>()
            .put(SYSTEM_MESSAGING, "/resources/js/pages/yukon.support.systemHealth.js")
            .putAll(DATA_COLLECTION, baseJSPath + "yukon.widget.dataCollection.js",
                                     "HIGH_STOCK")
            .putAll(ASSET_AVAILABILITY, baseJSPath + "yukon.widget.assetAvailability.js",
                    "HIGH_STOCK")
            .put(INFRASTRUCTURE_WARNINGS, baseJSPath + "yukon.widget.infrastructureWarnings.js")
            .putAll(PORTER_QUEUE_COUNTS, baseJSPath + "yukon.widget.porterQueueCounts.js",
                                         "HIGH_STOCK",
                                         "HIGH_STOCK_NO_DATA");
        widgetSpecificJavascript = javascriptBuilder.build();
        
        widgetSpecificCss = ImmutableListMultimap.of(
            //Add specialized CSS (that only needs to be loaded when the widget is present)
        );

        Builder<WidgetType, WidgetParameter> builder = new ImmutableListMultimap.Builder<WidgetType, WidgetParameter>()
            .put(TREND, new WidgetParameter("deviceId", WidgetInputType.METER_PICKER, MeterPickerValidator.get()))
            .put(WidgetType.ASSET_AVAILABILITY,
                new WidgetParameter("controlAreaOrProgramOrScenarioId",
                    WidgetInputType.CONTROL_AREA_OR_PROGRAM_OR_SCENARIO_PICKER,
                    ControlAreaOrProgramOrScenarioPickerValidator.get()))
            .putAll(DATA_COLLECTION, new WidgetParameter("deviceGroup", WidgetInputType.DEVICE_GROUP, DeviceGroupPickerValidator.get()), 
                                     new WidgetParameter("includeDisabled", WidgetInputType.CHECKBOX, null, "false"))
            .putAll(SYSTEM_MESSAGING, new WidgetParameter("showRfnMeter", WidgetInputType.CHECKBOX, null, "false"),
                                      new WidgetParameter("showRfnLcr", WidgetInputType.CHECKBOX, null, "false"),
                                      new WidgetParameter("showRfGatewayArchive", WidgetInputType.CHECKBOX, null, "false"),
                                      new WidgetParameter("showRfDa", WidgetInputType.CHECKBOX, null, "false"),
                                      new WidgetParameter("showRfGatewayDataRequest", WidgetInputType.CHECKBOX, null, "false"),
                                      new WidgetParameter("showRfGatewayData", WidgetInputType.CHECKBOX, null, "false"))
            .put(MONITOR_SUBSCRIPTIONS, new WidgetParameter("selectMonitors", WidgetInputType.MONITOR_PICKER, MonitorPickerValidator.get()))
            .put(PORTER_QUEUE_COUNTS, new WidgetParameter("selectPorts", WidgetInputType.PORT_PICKER, null));
        widgetParameters = builder.build();
    }
    
    public static Stream<WidgetType> stream() {
        return Arrays.stream(values());
    }
    
    private DashboardScope scope;
    private WidgetCategory category;
    private String beanName;
    private String imageName;
    private String accessControl;
    
    //TODO: do we need to allow multiple scopes or categories?
    private WidgetType(DashboardScope scope, WidgetCategory category, String beanName, String imageName) {
        this.scope = scope;
        this.category = category;
        this.beanName = beanName;
        this.imageName = imageName;
    }
    
    /**
     * @param accessControl A list of enum names for Role Category, Role, Role Property,
     *        Global Setting, EC Setting or Master Config to determine if the widget is selectable for a given
     *        user.
     *        See {@link WidgetService.getTypesByCategory} for details.
     */
    private WidgetType(DashboardScope scope, WidgetCategory category, String beanName, String imageName, String accessControl) {
        this.scope = scope;
        this.category = category;
        this.beanName = beanName;
        this.imageName = imageName;
        this.accessControl = accessControl;
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

    public String getAccessControl() {
        return accessControl;
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
