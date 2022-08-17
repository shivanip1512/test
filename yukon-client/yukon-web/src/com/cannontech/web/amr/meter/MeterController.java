package com.cannontech.web.amr.meter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.amr.meter.search.model.StandardFilterByGenerator;
import com.cannontech.amr.meter.search.service.MeterSearchService;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceFilterCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.events.loggers.MeteringEventLogService;
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
import com.cannontech.common.pao.meter.model.MeterTypeHelper;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.CisDetailRolePropertyEnum;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;
import com.cannontech.web.common.dashboard.model.Dashboard;
import com.cannontech.web.common.dashboard.model.DashboardPageType;
import com.cannontech.web.common.dashboard.service.DashboardService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.widget.meterInfo.model.CreateMeterModel;
import com.cannontech.web.widget.meterInfo.model.CreateMeterModel.PointCreation;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@CheckRole({ YukonRole.METERING, YukonRole.APPLICATION_BILLING, YukonRole.SCHEDULER, YukonRole.DEVICE_ACTIONS })

public class MeterController {
    
    @Autowired private AttributeService attributeService;
    @Autowired private CachingPointFormattingService pointFormattingService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DashboardService dashboardService;
    @Autowired private DataStreamingAttributeHelper dataStreamingAttributeHelper;
    @Autowired private DeviceConfigurationDao deviceConfigDao;
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceFilterCollectionHelper filterCollectionHelper;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private DisconnectService disconnectService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MeterDao meterDao;
    @Autowired private MeteringEventLogService meteringEventLogService;
    @Autowired private MeterSearchService meterSearchService;
    @Autowired private MeterTypeHelper meterTypeHelper;
    @Autowired private MeterValidator meterValidator;
    @Autowired private MspMeterSearchService mspMeterSearchService;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefDao;
    @Autowired private PaoDetailUrlHelper paoDetailUrlHelper;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private PointDao pointDao;
    @Autowired private PointService pointService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ServerDatabaseCache serverDatabaseCache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private PaoNotesService paoNotesService;
    
    private static final Logger log = YukonLogManager.getLogger(MeterController.class); 

    private static final String baseKey = "yukon.web.modules.amr.meterSearchResults";
    
    @RequestMapping("start")
    public String start(ModelMap model, YukonUserContext userContext) {
        Dashboard amiDashboard = dashboardService.getAssignedDashboard(userContext.getYukonUser().getUserID(), DashboardPageType.AMI);
        model.addAttribute("dashboardPageType", DashboardPageType.AMI);
        return "redirect:/dashboards/" + amiDashboard.getDashboardId() + "/view";
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
        List<FilterBy> filterByList = new ArrayList<>();
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
        
        List<Integer> notesList = paoNotesService.getPaoIdsWithNotes(results.getResultList()
                                                                          .stream()
                                                                          .map(result -> result.getDeviceId())
                                                                          .collect(Collectors.toList()));
        model.addAttribute("notesList", notesList);
        
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
    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(HttpServletRequest request, ModelMap model, LiteYukonUser user, int deviceId) {
        
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        PaoType type = device.getDeviceType();

        // Redirecting water meters to WaterMeterController
        if (type.isWaterMeter() || type.isGasMeter()) {
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
        boolean showMapNetwork = rfDevice || type.isPlc();
        
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
        boolean disconnectDevice = disconnectService.supportsDisconnect(Lists.newArrayList(device), true);
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
        model.addAttribute("showWifiConnection", type.isWifiDevice());
        
        /** Page Actions */
        model.addAttribute("showCommander", commanderDevice && commanderUser);
        model.addAttribute("showHighBill", highBillDevice && highBillUser);
        model.addAttribute("showLocateRoute", porterCommandsDevice && locateRouteUser);
        model.addAttribute("showMoveInOut", moveDevice && moveUser);
        model.addAttribute("showProfile", loadProfileDevice || peakReportDevice || voltageProfileDevice);
        model.addAttribute("showVoltageAndTou", voltageAndTouDevice);
        model.addAttribute("showMapNetwork", showMapNetwork);
        
        model.addAttribute("hasActions", hasActions);
        
        return "meterHome.jsp";
    }
    
    @RequestMapping(value="create", method=RequestMethod.GET)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String create(ModelMap model, LiteYukonUser user) throws Exception {
        
        setupModel(model);
        CreateMeterModel meter = new CreateMeterModel();
        meter.setPointCreation(PointCreation.DEFAULT);
        model.addAttribute("meter", meter);
      
        return "create.jsp";
    }
    
    @RequestMapping(value="copy/{deviceId}", method=RequestMethod.GET)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String copy(ModelMap model, LiteYukonUser user, @PathVariable Integer deviceId) throws Exception {
        YukonMeter meterOriginal = meterDao.getForId(deviceId);
        CreateMeterModel meter = new CreateMeterModel();
        meter.setType(meterOriginal.getPaoType());
        if (meterOriginal instanceof RfnMeter) {
            meter.setManufacturer(((RfnMeter)meterOriginal).getRfnIdentifier().getSensorManufacturer());
            meter.setModel(((RfnMeter)meterOriginal).getRfnIdentifier().getSensorModel());
        }
        setupCopyModel(model, meter);
        meter.setPointCreation(PointCreation.COPY);
        model.addAttribute("meter", meter);
        return "copy.jsp";
    }
    
    private void setupCopyModel(ModelMap model, CreateMeterModel meter) {
        setupModel(model);
        if (meter.getType().isRfMeter()) {
            model.addAttribute("showRFMeshSettings", true);
        }
        else if (meter.getType().isPlc()) {
            model.addAttribute("showCarrierSettings", true);
        }
    }

    @RequestMapping(value="save", method=RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String save(@ModelAttribute("meter") CreateMeterModel meter, BindingResult result, HttpServletResponse resp, ModelMap model, LiteYukonUser user, FlashScope flash) throws Exception {
        meterValidator.validate(meter, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            setupModel(model);            
            return "create.jsp";
        }
        
        SimpleDevice device = null;
        try {
            device = createMeter(meter);
        }
        catch (DeviceCreationException e) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("errorMessage", e.getMessage());
            setupModel(model);
            return "create.jsp";
        }
        
        deviceDao.changeMeterNumber(device, meter.getMeterNumber());
        if (meter.isDisabled()) {
           deviceUpdateService.disableDevice(device); 
        }
        
        meteringEventLogService.meterCreated(meter.getName(), meter.getMeterNumber(), meter.getSerialOrAddress(), meter.getType(), 
                                             user.getUsername());
        
        Map<String, Object> json = new HashMap<>();
        json.put("deviceId", device.getDeviceId());
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.create.successful", meter.getName()));
        return null;
    }
    
    @RequestMapping(value="copy/{deviceId}", method=RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String copy(@ModelAttribute("meter") CreateMeterModel meter, BindingResult result, HttpServletResponse resp, ModelMap model, LiteYukonUser user, @PathVariable Integer deviceId, FlashScope flash) throws Exception {
        
        meterValidator.validate(meter, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            setupCopyModel(model, meter);        
            
            return "copy.jsp";
        }
        String templateName = serverDatabaseCache.getAllPaosMap().get(deviceId).getPaoName();
        SimpleDevice device = null;
        try {
            device = copyMeter(meter, templateName);
        }
        catch (DeviceCreationException e) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            setupCopyModel(model, meter);
            model.addAttribute("errorMessage", e.getMessage());
            
            return "copy.jsp";
        }
        
        deviceDao.changeMeterNumber(device, meter.getMeterNumber());
        if (meter.getType().isMct()) {
            deviceDao.changeAddress(device, meter.getAddress());
            deviceDao.changeRoute(device, meter.getRouteId());
        }
        if (meter.isDisabled()) {
            deviceUpdateService.disableDevice(device);
        } else {
            deviceDao.enableDevice(device);
        }
        
        meteringEventLogService.meterCreated(meter.getName(), meter.getMeterNumber(), meter.getSerialOrAddress(), meter.getType(), 
                                             user.getUsername());
        
        Map<String, Object> json = new HashMap<>();
        json.put("deviceId", device.getDeviceId());
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), json);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.create.successful", meter.getName()));
        return null;
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
    
    private void setupModel(ModelMap model) {

        model.addAttribute("meterTypes", meterTypeHelper.getCreateGroupedMeters());
        
        LiteYukonPAObject[] routes = paoDao.getRoutesByType(PaoType.ROUTE_CCU, PaoType.ROUTE_MACRO);
        model.addAttribute("routes", routes);
        
        List<LiteYukonPAObject> ports = serverDatabaseCache.getAllPorts();
        model.addAttribute("ports",ports);
        
        Set<PaoType> rfMeterTypes = PaoType.getRfMeterTypes();
        model.addAttribute("rfMeterTypes", rfMeterTypes);
        
        Set<PaoType> mctMeterTypes = PaoType.getMctTypes();
        model.addAttribute("mctMeterTypes", mctMeterTypes);
        
        model.addAttribute("pointCreateValues", PointCreation.values());
    }
    
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public String delete(FlashScope flash, @PathVariable int id, ModelMap model, LiteYukonUser user) {
        LiteYukonPAObject meter = serverDatabaseCache.getAllPaosMap().get(id);
        String meterName = serverDatabaseCache.getAllMeters().get(id).getMeterNumber();
        try {
            deviceDao.removeDevice(id);
            
            meteringEventLogService.meterDeleted(meter.getPaoName(), meterName, user.getUsername());
            
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.delete.successful", meter.getPaoName()));
            return "redirect:/meter/start";
        }
        catch (Exception e) {
            log.error("Unable to delete meter with id " + meter.getPaoName(), e);
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.amr.delete.failure", meter.getPaoName()));
            return "redirect:/meter/home?deviceId="+id;
        }
    }
    
    private SimpleDevice copyMeter(CreateMeterModel meter, String templateName) {
        SimpleDevice device;
        // create a new meter from a template meter and copy the templateName's points
        if (meter.getPointCreation() == PointCreation.DEFAULT) {
            device = createMeter(meter);
        }
        else if (meter.getType().isRfMeter()) {
            RfnIdentifier rfnId = new RfnIdentifier(meter.getSerialNumber(), meter.getManufacturer(), meter.getModel());
            device = deviceCreationService.createRfnDeviceByTemplate(templateName, meter.getName(), rfnId, meter.isCopyPoints());
        } else {
            device = deviceCreationService.createDeviceByTemplate(templateName, meter.getName(), meter.isCopyPoints());
        }
        return device;
    }
    
    private SimpleDevice createMeter(CreateMeterModel meter) {
        SimpleDevice device;
        if (meter.getType().isMct()) {
            device = deviceCreationService.createCarrierDeviceByDeviceType(meter.getType(), meter.getName(),
                meter.getAddress(), meter.getRouteId(), true);
        } else if (meter.getType().isRfMeter()) {
            RfnIdentifier rfnId = new RfnIdentifier(meter.getSerialNumber(), meter.getManufacturer(), meter.getModel());
            device = deviceCreationService.createRfnDeviceByDeviceType(meter.getType(), meter.getName(), rfnId,
                true);
        } else {
            device = deviceCreationService.createIEDDeviceByDeviceType(meter.getType(), meter.getName(),
                meter.getPortId(), true);
        }

        return  device;
    }
}