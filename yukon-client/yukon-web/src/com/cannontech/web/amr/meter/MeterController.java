package com.cannontech.web.amr.meter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.OrderBy;
import com.cannontech.amr.meter.search.model.StandardFilterByGenerator;
import com.cannontech.amr.meter.search.service.MeterSearchService;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.model.PreviousReadings;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.search.SearchResult;
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
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;
import com.cannontech.web.bulk.model.collection.DeviceFilterCollectionHelper;
import com.cannontech.web.common.pao.PaoDetailUrlHelper;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.updater.point.PointUpdateBackingService;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Spring controller class
 */
@CheckRole({YukonRole.METERING,YukonRole.APPLICATION_BILLING,YukonRole.SCHEDULER,YukonRole.DEVICE_ACTIONS})
public class MeterController extends MultiActionController {

    private MeterSearchService meterSearchService = null;
    private AttributeService attributeService = null;
    private DeviceDao deviceDao = null;
    private MultispeakFuncs multispeakFuncs;
    private PaoDetailUrlHelper paoDetailUrlHelper;
    private PointDao pointDao = null;
    private PointService pointService = null;
    private PaoLoadingService paoLoadingService = null;
    private DeviceFilterCollectionHelper filterCollectionHelper = null;
    private CachingPointFormattingService cachingPointFormattingService = null;
    private PointUpdateBackingService pointUpdateBackingService = null;
    private RolePropertyDao rolePropertyDao = null;
    private PaoDefinitionDao paoDefinitionDao = null;
    private MspMeterSearchService mspMeterSearchService;
    private DeviceConfigService deviceConfigService;

    public MeterController() {
        super();
    }
    
    public ModelAndView start(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("start.jsp");
    }

    public ModelAndView search(HttpServletRequest request, HttpServletResponse response)
    throws ServletException {

        // Set the request url and parameters as a session attribute
        String url = request.getRequestURL().toString();
        String urlParams = request.getQueryString();
        request.getSession().setAttribute("searchResults",
                                          url + ((urlParams != null) ? "?" + urlParams : ""));
        
        // Get the search result count
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 25);
        
        // Get the search start index
        int page = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (page - 1) * itemsPerPage;
        if (request.getParameter("Filter") != null) {
            startIndex = 0;
        }

        boolean isQuickSearch = StringUtils.isNotBlank(request.getParameter("quickSearch"));
        MeterSearchField defaultField = isQuickSearch ? MeterSearchField.METERNUMBER : MeterSearchField.PAONAME;

        // Get the order by field
        String orderByField = ServletRequestUtils.getStringParameter(request, "orderBy", defaultField.toString());
        OrderBy orderBy = new OrderBy(orderByField, ServletRequestUtils.getBooleanParameter(request, "descending", false));

        // all filters
        List<FilterBy> filterByList = new ArrayList<FilterBy>();
        filterByList.addAll(StandardFilterByGenerator.getStandardFilterByList());
        filterByList.addAll(mspMeterSearchService.getMspFilterByList());
        
        // query filter
        List<FilterBy> queryFilter = MeterSearchUtils.getQueryFilter(request, filterByList);
        
        // Perform the search
        SearchResult<Meter> meterSearchResults = 
            meterSearchService.search(queryFilter, orderBy, startIndex, itemsPerPage);

        ModelAndView mav;
        // Redirect to device home page if only one result is found
        if (meterSearchResults.getHitCount() == 1) {
            mav = new ModelAndView();

            Meter meter = meterSearchResults.getResultList().get(0);
            
            String urlForPaoDetailPage = paoDetailUrlHelper.getUrlForPaoDetailPage(meter);
            if (!StringUtils.isBlank(urlForPaoDetailPage)) {
                mav.setView(new RedirectView(urlForPaoDetailPage));
                return mav;
            }
        }
        
        mav = new ModelAndView("meters.jsp");
        mav.addObject("defaultSearchField", defaultField);
        // Create a device collection (only used to generate a link)
        DeviceCollection deviceGroupCollection = filterCollectionHelper.createDeviceGroupCollection(queryFilter, orderBy);
        
        mav.addObject("deviceGroupCollection", deviceGroupCollection);
        
        mav.addObject("orderBy", orderBy);
        mav.addObject("meterSearchResults", meterSearchResults);
        mav.addObject("orderByFields", MeterSearchField.values());
        mav.addObject("filterByList", filterByList);
        
        ImmutableMap<String,Meter> paoIdToMeterMap = 
            Maps.uniqueIndex(meterSearchResults.getResultList(), new Function<Meter, String>() {
                @Override
                public String apply(Meter meter) {
                    return String.valueOf(meter.getDeviceId());
                }
            });
        
        mav.addObject("paoIdToMeterMap", paoIdToMeterMap);
            
        return mav;
    }
    
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("meterHome.jsp");

        int deviceId = ServletRequestUtils.getIntParameter(request, "deviceId");

        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        mav.addObject("deviceId", deviceId);
        mav.addObject("deviceName",  paoLoadingService.getDisplayablePao(device).getName());
        
        boolean isRFMesh = device.getDeviceType().getPaoClass() == PaoClass.RFMESH;
        
        // do some hinting to speed loading
        List<LitePoint> litePoints = pointDao.getLitePointsByPaObjectId(deviceId);
        cachingPointFormattingService.addLitePointsToCache(litePoints);
        pointUpdateBackingService.notifyOfImminentPoints(litePoints);

        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        Set<Attribute> availableAttributes = attributeService.getAvailableAttributes(device);
        
        boolean highBillSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.HIGH_BILL);
        mav.addObject("highBillSupported", highBillSupported);

        boolean outageSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.OUTAGE) 
            && (availableAttributes.contains(BuiltInAttribute.OUTAGE_LOG) 
                    || availableAttributes.contains(BuiltInAttribute.BLINK_COUNT));
        mav.addObject("outageSupported", outageSupported && !isRFMesh);
        mav.addObject("rfnOutageSupported", outageSupported && isRFMesh);
        
        String cisInfoWidgetName = multispeakFuncs.getCisDetailWidget(user);
        mav.addObject("cisInfoWidgetName", cisInfoWidgetName);

        boolean disconnectSupported = DeviceTypesFuncs.isDisconnectMCTOrHasCollar(device);
        mav.addObject("disconnectSupported", disconnectSupported);
        
        boolean rfnDisconnectSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.DISCONNECT_RFN);
        mav.addObject("rfnDisconnectSupported", rfnDisconnectSupported);

        boolean touSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.TOU);
        mav.addObject("touSupported", touSupported);

        boolean moveSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.MOVE_SUPPORTED);
        boolean moveEnabled = rolePropertyDao.checkProperty(YukonRoleProperty.MOVE_IN_MOVE_OUT, user);
        mav.addObject("moveSupported", (moveSupported && moveEnabled));

        boolean lpSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.LOAD_PROFILE);
        mav.addObject("lpSupported", lpSupported);

        boolean peakReportSupported = paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.PEAK_REPORT);
        mav.addObject("peakReportSupported", peakReportSupported);

        boolean threePhaseVoltageOrCurrentSupported = (paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(),
                                                                                      PaoTag.THREE_PHASE_VOLTAGE) ||
                                                       paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(),
                                                                                      PaoTag.THREE_PHASE_CURRENT));
        mav.addObject("threePhaseVoltageOrCurrentSupported", threePhaseVoltageOrCurrentSupported);

        boolean singlePhaseVoltageSupported = availableAttributes.contains(BuiltInAttribute.VOLTAGE);
        boolean showVoltageAndTou = (DeviceTypesFuncs.isMCT4XX(device.getType()) || device.getDeviceType().isRfn()) 
                && (singlePhaseVoltageSupported || threePhaseVoltageOrCurrentSupported);
        mav.addObject("showVoltageAndTou", showVoltageAndTou);

        boolean configSupported = deviceConfigService.isDeviceConfigAvailable(device.getDeviceType());
        mav.addObject("configSupported", configSupported);
        
        if (isRFMesh) {
            mav.addObject("showRfMetadata", true);
        }
        
        if (paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.RFN_EVENTS)) {
            mav.addObject("rfnEventsSupported", true);
        }
        
        if (paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.PORTER_COMMAND_REQUESTS)) {
            mav.addObject("porterCommandRequestsSupported", true);
        }

        return mav;
    }

    public ModelAndView touPreviousReadings(HttpServletRequest request,
                                            HttpServletResponse response) throws ServletException {
        ModelAndView mav = new ModelAndView("touPreviousReadings.jsp");
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);

        // Find the existing TOU attributes
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(device);
        Set<Attribute> existingTouAttributes =
            Sets.intersection(allExistingAttributes,AttributeHelper.getTouUsageAttributes());
         
        // Get the previous values for TOU points and set them to the mav.
        if (existingTouAttributes.size() > 0){
            for (Attribute touAttribute : existingTouAttributes) {
                LitePoint touPoint = attributeService.getPointForAttribute(device, touAttribute);
                PreviousReadings previousReadings = pointService.getPreviousReadings(touPoint);
                previousReadings.setAttribute(touAttribute);
                mav.addObject(touAttribute.getKey(), previousReadings);

            }
        }
        
        return mav;
    }

    // DI Setters
    @Autowired
    public void setMeterSearchService(MeterSearchService meterSearchService) {
        this.meterSearchService = meterSearchService;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Autowired
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    
    @Autowired
    public void setPaoDetailUrlHelper(PaoDetailUrlHelper paoDetailUrlHelper) {
        this.paoDetailUrlHelper = paoDetailUrlHelper;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Autowired
    public void setPointService(PointService pointService) {
        this.pointService = pointService;
    }
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
    
    @Autowired
    public void setFilterCollectionHelper(DeviceFilterCollectionHelper filterCollectionHelper) {
        this.filterCollectionHelper = filterCollectionHelper;
    }
    
    @Autowired
    public void setCachingPointFormattingService(CachingPointFormattingService cachingPointFormattingService) {
        this.cachingPointFormattingService = cachingPointFormattingService;
    }
    
    @Autowired
    public void setPointUpdateBackingService(PointUpdateBackingService pointUpdateBackingService) {
        this.pointUpdateBackingService = pointUpdateBackingService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Autowired
    public void setMspMeterSearchService(MspMeterSearchService mspMeterSearchService) {
        this.mspMeterSearchService = mspMeterSearchService;
    }

    @Autowired
    public void setDeviceConfigService(DeviceConfigService deviceConfigService) {
		this.deviceConfigService = deviceConfigService;
	}
}
