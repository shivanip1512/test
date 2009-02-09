package com.cannontech.web.amr.meter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.meter.dao.impl.MeterDisplayFieldEnum;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.FilterByGenerator;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.OrderBy;
import com.cannontech.amr.meter.search.service.MeterSearchService;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.MeteringRole;
import com.cannontech.roles.yukon.ConfigurationRole;
import com.cannontech.roles.yukon.MultispeakRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.bulk.model.collection.DeviceFilterCollectionHelper;
import com.cannontech.web.updater.point.PointUpdateBackingService;

/**
 * Spring controller class
 */
public class MeterController extends MultiActionController {

    private AuthDao authDao = null;
    private RoleDao roleDao = null;
    private MeterSearchService meterSearchService = null;
    private AttributeService attributeService = null;
    private DeviceDao deviceDao = null;
    private PointDao pointDao = null;
    private DeviceFilterCollectionHelper filterCollectionHelper = null;
    private CachingPointFormattingService cachingPointFormattingService = null;
    private PointUpdateBackingService pointUpdateBackingService = null;

    public MeterController() {
        super();
    }
    
    @Autowired
    public void setMeterSearchService(MeterSearchService meterSearchService) {
        this.meterSearchService = meterSearchService;
    }
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
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
    public void setPointDao(PointDao pointDao) {
		this.pointDao = pointDao;
	}
    
    @Autowired
    public void setFilterCollectionHelper(DeviceFilterCollectionHelper filterCollectionHelper) {
        this.filterCollectionHelper = filterCollectionHelper;
    }
    
    @Autowired
    public void setCachingPointFormattingService(
			CachingPointFormattingService cachingPointFormattingService) {
		this.cachingPointFormattingService = cachingPointFormattingService;
	}
    
    @Autowired
    public void setPointUpdateBackingService(
			PointUpdateBackingService pointUpdateBackingService) {
		this.pointUpdateBackingService = pointUpdateBackingService;
	}
    
    public ModelAndView start(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("start.jsp");
    }

    public ModelAndView search(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        LiteYukonUser user = ServletUtil.getYukonUser(request);

        // Set the request url and parameters as a session attribute
        String url = request.getRequestURL().toString();
        String urlParams = request.getQueryString();
        request.getSession().setAttribute("searchResults",
                                          url + ((urlParams != null) ? "?" + urlParams : ""));

        // Get the search start index
        int startIndex = ServletRequestUtils.getIntParameter(request, "startIndex", 0);
        if (request.getParameter("Filter") != null) {
            startIndex = 0;
        }

        // Get the search result count
        int count = ServletRequestUtils.getIntParameter(request, "count", 25);
        
        boolean isQuickSearch = request.getParameter("Quick Search") != null;
        MeterSearchField defaultField = isQuickSearch ? MeterSearchField.METERNUMBER : MeterSearchField.PAONAME;

        // Get the order by field
        String orderByField = ServletRequestUtils.getStringParameter(request,
                                                                     "orderBy",
                                                                     defaultField.toString());
        OrderBy orderBy = new OrderBy(orderByField,
                                      ServletRequestUtils.getBooleanParameter(request,
                                                                              "descending",
                                                                              false));

        // Build up filter by list
        List<FilterBy> filterByList = FilterByGenerator.getFilterByList();
        List<FilterBy> queryFilter = MeterSearchUtils.getQueryFilter(request, filterByList);
        String filterByString = MeterSearchUtils.getFilterByString(queryFilter);

        // Perform the search
        SearchResult<Meter> results = meterSearchService.search(queryFilter,
                                                                orderBy,
                                                                startIndex,
                                                                count);
        

        ModelAndView mav;
        // Redirect to device home page if only one result is found
        if (results.getHitCount() == 1) {
            mav = new ModelAndView("redirect:/spring/meter/home");

            Meter meter = results.getResultList().get(0);
            mav.addObject("deviceId", meter.getDeviceId());

        } else {
            mav = new ModelAndView("meters.jsp");
            // Create a device collection (only used to generate a link)
            DeviceCollection deviceGroupCollection = filterCollectionHelper.createDeviceGroupCollection(queryFilter, orderBy);
            
            mav.addObject("deviceGroupCollection", deviceGroupCollection);
            
            mav.addObject("filterByString", filterByString);
            mav.addObject("orderBy", orderBy);
            mav.addObject("results", results);
            mav.addObject("orderByFields", MeterSearchField.values());
            mav.addObject("filterByList", filterByList);
            
            List<MeterDisplayFieldEnum> defaultDispEnumsOrder = new ArrayList<MeterDisplayFieldEnum>();
            defaultDispEnumsOrder.add(MeterDisplayFieldEnum.METER_NUMBER);
            defaultDispEnumsOrder.add(MeterDisplayFieldEnum.DEVICE_NAME);
            defaultDispEnumsOrder.add(MeterDisplayFieldEnum.DEVICE_TYPE);
            defaultDispEnumsOrder.add(MeterDisplayFieldEnum.ADDRESS);
            defaultDispEnumsOrder.add(MeterDisplayFieldEnum.ROUTE);
            List<MeterDisplayFieldEnum> orderedDispEnums = orderColumnsByDisplayTemplate(defaultDispEnumsOrder, user);
            mav.addObject("orderedDispEnums", orderedDispEnums);
            
            
            List<Map<MeterDisplayFieldEnum, String>> resultColumnsList = new ArrayList<Map<MeterDisplayFieldEnum, String>>();
            List<Meter> resultMeterList = new ArrayList<Meter>();
            
            for (Meter meter : results.getResultList()) {
                
                Map<MeterDisplayFieldEnum, String> rowMap = new HashMap<MeterDisplayFieldEnum, String>();
                for (MeterDisplayFieldEnum meterDisplayFieldEnum : MeterDisplayFieldEnum.values()) {
                    rowMap.put(meterDisplayFieldEnum, meterDisplayFieldEnum.getField(meter));
                }
                resultColumnsList.add(rowMap);
                resultMeterList.add(meter);
            }
            mav.addObject("idEnum", MeterDisplayFieldEnum.ID);
            mav.addObject("resultColumnsList", resultColumnsList);
            mav.addObject("resultMeterList", resultMeterList);
            
            
            
        }

        return mav;
    }
    
    private List<MeterDisplayFieldEnum> orderColumnsByDisplayTemplate(List<MeterDisplayFieldEnum> defaultColumnOrder, LiteYukonUser user) {
        
        List<MeterDisplayFieldEnum> newOrder = new ArrayList<MeterDisplayFieldEnum>();
        
        try {
            MeterDisplayFieldEnum roleDispFieldEnumVal = authDao.getRolePropertyValue(MeterDisplayFieldEnum.class, user,
                                                                                          ConfigurationRole.DEVICE_DISPLAY_TEMPLATE);
            
            newOrder.addAll(defaultColumnOrder);
            newOrder.remove(roleDispFieldEnumVal); // remove if present
            newOrder.add(0,roleDispFieldEnumVal); // force to the front of the list 
            
        } catch (IllegalArgumentException e) {
            return defaultColumnOrder;
        }
        
        return newOrder;
    }

    public ModelAndView home(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("meterHome.jsp");

        int deviceId = ServletRequestUtils.getIntParameter(request, "deviceId");

        YukonDevice device = deviceDao.getYukonDevice(deviceId);
        mav.addObject("deviceId", deviceId);
        
        // do some hinting to speed loading
        List<LitePoint> litePoints = pointDao.getLitePointsByPaObjectId(deviceId);
        cachingPointFormattingService.addLitePointsToCache(litePoints);
        pointUpdateBackingService.notifyOfImminentPoints(litePoints);
        
        boolean highBillSupported = DeviceTypesFuncs.isMCT410(device.getType());

        mav.addObject("highBillSupported", highBillSupported);

        Set<Attribute> availableAttributes = attributeService.getAvailableAttributes(device);

        boolean outageSupported = (availableAttributes.contains(BuiltInAttribute.OUTAGE_LOG) || availableAttributes.contains(BuiltInAttribute.BLINK_COUNT));
        mav.addObject("outageSupported", outageSupported);

        mav.addObject("mspSupported",
                      Integer.valueOf(roleDao.getGlobalPropertyValue(MultispeakRole.MSP_PRIMARY_CB_VENDORID))
                             .intValue() > 0);

        boolean disconnectSupported = DeviceTypesFuncs.isDisconnectMCTOrHasCollar(device);
        mav.addObject("disconnectSupported", disconnectSupported);

        LiteYukonUser user = ServletUtil.getYukonUser(request);

        boolean touSupported = DeviceTypesFuncs.isTouMCT(device.getType());
        mav.addObject("touSupported", touSupported);

        boolean moveSupported = DeviceTypesFuncs.isMCT410(device.getType());
        boolean moveEnabled = Boolean.parseBoolean(authDao.getRolePropertyValue(user, MeteringRole.MOVE_IN_MOVE_OUT));
        mav.addObject("moveSupported", (moveSupported && moveEnabled));

        boolean lpSupported = DeviceTypesFuncs.isLoadProfile4Channel(device.getType());
        mav.addObject("lpSupported", lpSupported);

        boolean profileCollection = Boolean.parseBoolean(authDao.getRolePropertyValue(user, MeteringRole.PROFILE_COLLECTION));
        boolean profileCollectionScanning = Boolean.parseBoolean(authDao.getRolePropertyValue(user, MeteringRole.PROFILE_COLLECTION_SCANNING));
        mav.addObject("profileCollection", profileCollection);
        mav.addObject("profileCollectionScanning", profileCollectionScanning);
        
        boolean peakReportSupported = DeviceTypesFuncs.isMCT410(device.getType());
        mav.addObject("peakReportSupported", peakReportSupported);

        boolean isMCT4XX = DeviceTypesFuncs.isMCT4XX(device.getType());
        mav.addObject("isMCT4XX", isMCT4XX);
        
        mav.addObject("voltageSupported", availableAttributes.contains(BuiltInAttribute.VOLTAGE));

        return mav;
    }

}
