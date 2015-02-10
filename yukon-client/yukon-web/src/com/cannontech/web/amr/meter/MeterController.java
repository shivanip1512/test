package com.cannontech.web.amr.meter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
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
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@CheckRole({ YukonRole.METERING, YukonRole.APPLICATION_BILLING, YukonRole.SCHEDULER, YukonRole.DEVICE_ACTIONS })
public class MeterController {

    @Autowired private MeterSearchService meterSearchService;
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private PaoDetailUrlHelper paoDetailUrlHelper;
    @Autowired private PointDao pointDao;
    @Autowired private PointService pointService;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private DeviceFilterCollectionHelper filterCollectionHelper;
    @Autowired private CachingPointFormattingService cachingPointFormattingService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private MspMeterSearchService mspMeterSearchService;
    @Autowired private DeviceConfigService deviceConfigService;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DisconnectService disconnectService;
   
    private static final String baseKey = "yukon.web.modules.amr.meterSearchResults";
    
    @RequestMapping("start")
    public String start() {
        return "start.jsp";
    }

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
        SearchResults<YukonMeter> meterSearchResults = 
            meterSearchService.search(queryFilter, orderBy, paging.getStartIndex(), paging.getItemsPerPage());

        // Redirect to device home page if only one result is found
        if (meterSearchResults.getHitCount() == 1) {
            YukonMeter meter = meterSearchResults.getResultList().get(0);
            
            String urlForPaoDetailPage = paoDetailUrlHelper.getUrlForPaoDetailPage(meter);
            if (!StringUtils.isBlank(urlForPaoDetailPage)) {
                String meterUrl = "redirect:" + urlForPaoDetailPage;
                return meterUrl;
            }
        }
        
        // Create a device collection (only used to generate a link)
        DeviceCollection deviceGroupCollection = filterCollectionHelper.createDeviceGroupCollection(queryFilter, orderBy);
        model.addAttribute("deviceGroupCollection", deviceGroupCollection);
        model.addAttribute("meterSearchResults", meterSearchResults);
        model.addAttribute("filterByList", filterByList);
        
        ImmutableMap<String, YukonMeter> paoIdToMeterMap = 
            Maps.uniqueIndex(meterSearchResults.getResultList(), new Function<YukonMeter, String>() {
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
    
    @RequestMapping("home")
    public String home(HttpServletRequest request, ModelMap model) throws ServletException {

        int deviceId = ServletRequestUtils.getIntParameter(request, "deviceId");

        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        model.addAttribute("deviceId", deviceId);
        //we are redirecting request for water meter to WaterMeterController.java
        if (device.getDeviceType() == PaoType.RFWMETER) {
            return "redirect:/meter/water/home";
        }
        
        model.addAttribute("deviceName",  paoLoadingService.getDisplayablePao(device).getName());
        
        boolean isRFMesh = device.getDeviceType().getPaoClass() == PaoClass.RFMESH;
        
        // do some hinting to speed loading
        List<LitePoint> litePoints = pointDao.getLitePointsByPaObjectId(deviceId);
        cachingPointFormattingService.addLitePointsToCache(litePoints);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        Set<Attribute> availableAttributes = attributeService.getAvailableAttributes(device);
        
        boolean highBillSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.HIGH_BILL);
        model.addAttribute("highBillSupported", highBillSupported);

        boolean outageSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.OUTAGE) 
            && (availableAttributes.contains(BuiltInAttribute.OUTAGE_LOG) 
                    || availableAttributes.contains(BuiltInAttribute.BLINK_COUNT));
        model.addAttribute("outageSupported", outageSupported && !isRFMesh);
        model.addAttribute("rfnOutageSupported", outageSupported && isRFMesh);
        
        String cisInfoWidgetName = multispeakFuncs.getCisDetailWidget(user);
        model.addAttribute("cisInfoWidgetName", cisInfoWidgetName);

        boolean disconnectSupported = disconnectService.supportsDisconnect(Lists.newArrayList(device));
        model.addAttribute("disconnectSupported", disconnectSupported);
       
        boolean touSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.TOU);
        model.addAttribute("touSupported", touSupported);

        boolean moveSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.MOVE_SUPPORTED);
        boolean moveEnabled = rolePropertyDao.checkProperty(YukonRoleProperty.MOVE_IN_MOVE_OUT, user);
        model.addAttribute("moveSupported", (moveSupported && moveEnabled));

        boolean lpSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.LOAD_PROFILE);
        model.addAttribute("lpSupported", lpSupported);

        boolean peakReportSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.PEAK_REPORT);
        model.addAttribute("peakReportSupported", peakReportSupported);

        boolean threePhaseVoltageOrCurrentSupported = (paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(),
                                                                                      PaoTag.THREE_PHASE_VOLTAGE) ||
                                                       paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(),
                                                                                      PaoTag.THREE_PHASE_CURRENT));
        model.addAttribute("threePhaseVoltageOrCurrentSupported", threePhaseVoltageOrCurrentSupported);

        boolean singlePhaseVoltageSupported = availableAttributes.contains(BuiltInAttribute.VOLTAGE);
        boolean showVoltageAndTou = (DeviceTypesFuncs.isMCT4XX(device.getDeviceType()) || device.getDeviceType().isRfn()) 
                && (singlePhaseVoltageSupported || threePhaseVoltageOrCurrentSupported);
        model.addAttribute("showVoltageAndTou", showVoltageAndTou);

        boolean configSupported = !deviceConfigurationDao.getAllConfigurationsByType(device.getDeviceType()).isEmpty();
        model.addAttribute("configSupported", configSupported);
        
        if (isRFMesh) {
            model.addAttribute("showRfMetadata", true);
        }
        
        if (paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.RFN_EVENTS)) {
            model.addAttribute("rfnEventsSupported", true);
        }
        
        boolean porterCommandRequestsSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.PORTER_COMMAND_REQUESTS);
        model.addAttribute("porterCommandRequestsSupported", porterCommandRequestsSupported);

        boolean hasActions = 
                moveSupported ||
                (highBillSupported && rolePropertyDao.checkProperty(YukonRoleProperty.HIGH_BILL_COMPLAINT, user)) || 
                lpSupported || peakReportSupported ||
                showVoltageAndTou ||
                rolePropertyDao.checkProperty(YukonRoleProperty.ENABLE_WEB_COMMANDER, user) ||
                (porterCommandRequestsSupported && rolePropertyDao.checkProperty(YukonRoleProperty.LOCATE_ROUTE, user));
        
        model.addAttribute("hasActions", hasActions);

        return "meterHome.jsp";
    }

    @RequestMapping("touPreviousReadings")
    public String touPreviousReadings(ModelMap model, int deviceId) {
        
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

            }
        }
        
        return "touPreviousReadings.jsp";
    }
}
