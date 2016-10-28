package com.cannontech.web.amr.meter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.amr.meter.search.model.StandardFilterByGenerator;
import com.cannontech.amr.meter.search.service.MeterSearchService;
import com.cannontech.common.bulk.collection.DeviceFilterCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.CisDetailRolePropertyEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.rfn.dataStreaming.DataStreamingConfigException;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Controller
@CheckRole({ YukonRole.METERING, YukonRole.APPLICATION_BILLING, YukonRole.SCHEDULER, YukonRole.DEVICE_ACTIONS })
public class MeterController {
    
    @Autowired private AttributeService attributeService;
    @Autowired private CachingPointFormattingService pointFormattingService;
    @Autowired private DeviceConfigurationDao deviceConfigDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceFilterCollectionHelper filterCollectionHelper;
    @Autowired private DisconnectService disconnectService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MeterSearchService meterSearchService;
    @Autowired private MspMeterSearchService mspMeterSearchService;
    @Autowired private PaoDefinitionDao paoDefDao;
    @Autowired private PaoDetailUrlHelper paoDetailUrlHelper;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private PointDao pointDao;
    @Autowired private PointService pointService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DataStreamingAttributeHelper dataStreamingAttributeHelper;
    @Autowired private DataStreamingService dataStreamingService;
    @Autowired private ConfigurationSource configurationSource;

   
    private static final String baseKey = "yukon.web.modules.amr.meterSearchResults";
    
    @RequestMapping("start")
    public String start(ModelMap model) {
        List<RfnGateway> overloadedGateways = new ArrayList<>();
        try {
            overloadedGateways = dataStreamingService.getOverloadedGateways();
        } catch (DataStreamingConfigException e) {}
        
        model.addAttribute("showOverloadedGatewaysWidget", overloadedGateways.size() > 0 ? true : false);
        
        return "start.jsp";
    }
        
    @CheckRole({ YukonRole.METERING })
    @RequestMapping("search")
    public String search(HttpServletRequest request, ModelMap model, YukonUserContext userContext,
            SortingParameters sorting, PagingParameters paging) {
        
        // Set the request url and parameters as a session attribute
        String url = request.getRequestURL().toString();
        String urlParams = request.getQueryString();
        request.getSession().setAttribute("searchResults", url + ((urlParams != null) ? "?" + urlParams : ""));
        
        boolean isQuickSearch = StringUtils.isNotBlank(request.getParameter("quickSearch"));
        MeterSearchField sort = isQuickSearch ? MeterSearchField.METERNUMBER : MeterSearchField.PAONAME;
        boolean desc = false;
        
        // Get the order by field
        if (sorting != null) {
            sort = MeterSearchField.valueOf(sorting.getSort());
            desc = sorting.getDirection() == Direction.desc;
        }
        MeterSearchOrderBy orderBy = new MeterSearchOrderBy(sort.toString(), desc);
        
        // all filters
        List<FilterBy> filterByList = new ArrayList<FilterBy>();
        filterByList.addAll(StandardFilterByGenerator.getStandardFilterByList());
        filterByList.addAll(mspMeterSearchService.getMspFilterByList());
        
        // query filter
        List<FilterBy> queryFilter = MeterSearchUtils.getQueryFilter(request, filterByList);
        
        // Perform the search
        SearchResults<YukonMeter> results = meterSearchService.search(queryFilter, orderBy, paging);
        
        // Redirect to device home page if only one result is found
        if (results.getHitCount() == 1) {
            YukonMeter meter = results.getResultList().get(0);
            
            String urlForPaoDetailPage = paoDetailUrlHelper.getUrlForPaoDetailPage(meter);
            if (!StringUtils.isBlank(urlForPaoDetailPage)) {
                String meterUrl = "redirect:" + urlForPaoDetailPage;
                return meterUrl;
            }
        }
        
        // Create a device collection (only used to generate a link)
        DeviceCollection deviceGroupCollection = filterCollectionHelper.createDeviceGroupCollection(queryFilter, orderBy);
        model.addAttribute("deviceGroupCollection", deviceGroupCollection);
        model.addAttribute("meterSearchResults", results);
        model.addAttribute("filterByList", filterByList);
        
        ImmutableMap<String, YukonMeter> paoIdToMeterMap = 
            Maps.uniqueIndex(results.getResultList(), new Function<YukonMeter, String>() {
                @Override
                public String apply(YukonMeter meter) {
                    return String.valueOf(meter.getDeviceId());
                }
            });
        
        model.addAttribute("paoIdToMeterMap", paoIdToMeterMap);
        
        Direction dir = desc ? Direction.desc : Direction.asc;
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String nameText = accessor.getMessage(baseKey + ".columnHeader.deviceName.linkText");
        SortableColumn name = SortableColumn.of(dir, sort == MeterSearchField.PAONAME, nameText, "PAONAME");
        String numberText = accessor.getMessage(baseKey + ".columnHeader.meterNumber.linkText");
        SortableColumn meterNumber = SortableColumn.of(dir, sort == MeterSearchField.METERNUMBER, numberText, "METERNUMBER");
        String typeText = accessor.getMessage(baseKey + ".columnHeader.deviceType.linkText");
        SortableColumn type = SortableColumn.of(dir, sort == MeterSearchField.TYPE, typeText, "TYPE");
        String addressText = accessor.getMessage(baseKey + ".columnHeader.address.linkText");
        SortableColumn address = SortableColumn.of(dir, sort == MeterSearchField.ADDRESS, addressText, "ADDRESS");
        String routeText = accessor.getMessage(baseKey + ".columnHeader.route.linkText");
        SortableColumn route = SortableColumn.of(dir, sort == MeterSearchField.ROUTE, routeText, "ROUTE");
        
        model.addAttribute("nameColumn", name);
        model.addAttribute("meterNumberColumn", meterNumber);
        model.addAttribute("typeColumn", type);
        model.addAttribute("addressColumn", address);
        model.addAttribute("routeColumn", route);
        
        return "meters.jsp";
    }
    
    @CheckRole({ YukonRole.METERING })
    @RequestMapping("home")
    public String home(HttpServletRequest request, ModelMap model, LiteYukonUser user, int deviceId) {
        
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        PaoType type = device.getDeviceType();

        // Redirecting water meters to WaterMeterController
        if (type.isWaterMeter()) {
            return "redirect:" + paoDetailUrlHelper.getUrlForPaoDetailPage(device);
        }

        // The set of attributes in this device's definition. See paoDefinition.xml
        Set<Attribute> deviceAttributes = attributeService.getAvailableAttributes(device);
        
        /** User Permissions */
        boolean commanderUser = rolePropertyDao.checkProperty(YukonRoleProperty.ENABLE_WEB_COMMANDER, user);
        boolean highBillUser = rolePropertyDao.checkProperty(YukonRoleProperty.HIGH_BILL_COMPLAINT, user);
        boolean locateRouteUser = rolePropertyDao.checkProperty(YukonRoleProperty.LOCATE_ROUTE, user);
        boolean moveUser = rolePropertyDao.checkProperty(YukonRoleProperty.MOVE_IN_MOVE_OUT, user);
        /** Device Category */
        boolean mct4xxDevice = DeviceTypesFuncs.isMCT4XX(type);
        boolean rfDevice = type.isRfn();
        
        /** Device Tags */
        boolean commanderDevice = paoDefDao.isTagSupported(type, PaoTag.COMMANDER_REQUESTS);
        boolean highBillDevice = paoDefDao.isTagSupported(type, PaoTag.HIGH_BILL);
        boolean loadProfileDevice = paoDefDao.isTagSupported(type, PaoTag.LOAD_PROFILE);
        boolean voltageProfileDevice = paoDefDao.isTagSupported(type, PaoTag.VOLTAGE_PROFILE);
        boolean moveDevice = paoDefDao.isTagSupported(type, PaoTag.MOVE_SUPPORTED);
        boolean outageDevice = paoDefDao.isTagSupported(type, PaoTag.OUTAGE);
        boolean peakReportDevice = paoDefDao.isTagSupported(type, PaoTag.PEAK_REPORT);
        boolean porterCommandsDevice = paoDefDao.isTagSupported(type, PaoTag.PORTER_COMMAND_REQUESTS);
        boolean rfEventsDevice = paoDefDao.isTagSupported(type, PaoTag.RFN_EVENTS);
        boolean voltageThreePhaseDevice = paoDefDao.isTagSupported(type, PaoTag.THREE_PHASE_VOLTAGE); 
        boolean currentThreePhaseDevice = paoDefDao.isTagSupported(type, PaoTag.THREE_PHASE_CURRENT);
        boolean touDevice = paoDefDao.isTagSupported(type, PaoTag.TOU);
        
        /** Device Attributes */
        boolean outageLogAttribute = deviceAttributes.contains(BuiltInAttribute.OUTAGE_LOG);
        boolean blinkCountAttribute = deviceAttributes.contains(BuiltInAttribute.BLINK_COUNT);
        boolean voltageAttribute = deviceAttributes.contains(BuiltInAttribute.VOLTAGE);
        
        /** Other Device Properties */
        boolean configurableDevice = !deviceConfigDao.getAllConfigurationsByType(type).isEmpty();
        boolean dataStreamingEnabled = configurationSource.getBoolean(MasterConfigBoolean.RF_DATA_STREAMING_ENABLED, false);
        boolean streamableDevice = dataStreamingEnabled && !dataStreamingAttributeHelper.getSupportedAttributes(type).isEmpty();
        boolean outageSupported = outageDevice && (outageLogAttribute || blinkCountAttribute);
        // Device has internal disconnect or a disconnect collar attached
        boolean disconnectDevice = disconnectService.supportsDisconnect(Collections.singleton(device), true);
        boolean voltageAndTouDevice = (mct4xxDevice || rfDevice) 
                && (voltageAttribute || voltageThreePhaseDevice || currentThreePhaseDevice);
        
        /** Device Actions: Are there any options to show in the actions dropdown button. */
        boolean hasActions = moveDevice // Move In, Move Out Page
                || (highBillDevice && highBillUser) // High Bill Complaint Page
                || (loadProfileDevice || peakReportDevice) // Load Profile Page
                || voltageAndTouDevice // Voltage and Tou Page
                || (commanderUser && commanderDevice) // Web Commander Page
                || (porterCommandsDevice && locateRouteUser); // Locate Route Page
        
        model.addAttribute("deviceId", deviceId);
        
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        
        // Do some hinting to speed loading
        List<LitePoint> litePoints = pointDao.getLitePointsByPaObjectId(deviceId);
        pointFormattingService.addLitePointsToCache(litePoints);
        
        /** Page Widgets */
        CisDetailRolePropertyEnum cisDetail = globalSettingDao.getEnum(GlobalSettingType.CIS_DETAIL_TYPE, CisDetailRolePropertyEnum.class);
        model.addAttribute("cisInfoWidgetName", cisDetail.getWidgetName());
        model.addAttribute("showCis", cisDetail != CisDetailRolePropertyEnum.NONE);
        model.addAttribute("showConfig", configurableDevice || streamableDevice);
        model.addAttribute("configurableDevice", configurableDevice);
        model.addAttribute("streamableDevice", streamableDevice);
        model.addAttribute("showDisconnect", disconnectDevice);
        model.addAttribute("showEvents", rfEventsDevice);  
        model.addAttribute("showOutage", outageSupported && !rfDevice);
        model.addAttribute("showPolyphaseReadings", voltageThreePhaseDevice || currentThreePhaseDevice);
        model.addAttribute("showRfOutage", outageSupported && rfDevice);
        model.addAttribute("showRfMetadata", rfDevice);
        model.addAttribute("showTou", touDevice);
        
        /** Page Actions */
        model.addAttribute("showCommander", commanderDevice && commanderUser);
        model.addAttribute("showHighBill", highBillDevice && highBillUser);
        model.addAttribute("showLocateRoute", porterCommandsDevice && locateRouteUser);
        model.addAttribute("showMoveInOut", moveDevice && moveUser);
        model.addAttribute("showProfile", loadProfileDevice || peakReportDevice || voltageProfileDevice);
        model.addAttribute("showVoltageAndTou", voltageAndTouDevice);
        model.addAttribute("showMapNetwork", rfDevice);
        
        model.addAttribute("hasActions", hasActions);
        
        return "meterHome.jsp";
    }
    
    @CheckRole({ YukonRole.METERING })
    @RequestMapping("touPreviousReadings")
    public String touPreviousReadings(ModelMap model, int deviceId) {
        boolean previousReadingsExist = false;
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        // Find the existing TOU attributes
        Set<Attribute> existingTouAttributes = 
                attributeService.getExistingAttributes(device, AttributeHelper.getTouUsageAttributes());
         
        // Get the previous values for TOU points and set them to the mav.
        if (existingTouAttributes.size() > 0){
            for (Attribute touAttribute : existingTouAttributes) {
                LitePoint touPoint = attributeService.getPointForAttribute(device, touAttribute);
                PreviousReadings previousReadings = pointService.getPreviousReadings(touPoint);
                previousReadings.setAttribute(touAttribute);
                model.addAttribute(touAttribute.getKey(), previousReadings);
                if (!previousReadings.getPrevious36().isEmpty() || !previousReadings.getPrevious3Months().isEmpty()) {
                    previousReadingsExist = true;
                }
            }
        }
        model.addAttribute("previousReadingsExist", previousReadingsExist);
        return "touPreviousReadings.jsp";
    }
    
}