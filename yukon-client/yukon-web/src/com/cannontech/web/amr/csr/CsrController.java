package com.cannontech.web.amr.csr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.csr.model.CsrSearchField;
import com.cannontech.amr.csr.model.ExtendedMeter;
import com.cannontech.amr.csr.model.FilterBy;
import com.cannontech.amr.csr.model.FilterByGenerator;
import com.cannontech.amr.csr.model.OrderBy;
import com.cannontech.amr.csr.service.CsrService;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.MeteringRole;
import com.cannontech.roles.yukon.MultispeakRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.bulk.model.collection.DeviceFilterCollectionHelper;

/**
 * Spring controller class for csr
 */
public class CsrController extends MultiActionController {

    private AuthDao authDao = null;
    private RoleDao roleDao = null;
    private CsrService csrService = null;
    private AttributeService attributeService = null;
    private DeviceDao deviceDao = null;
    private DeviceFilterCollectionHelper filterCollectionHelper = null;

    public CsrController() {
        super();
    }

    public void setCsrService(CsrService csrService) {
        this.csrService = csrService;
    }

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    public void setFilterCollectionHelper(DeviceFilterCollectionHelper filterCollectionHelper) {
        this.filterCollectionHelper = filterCollectionHelper;
    }

    public ModelAndView search(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {


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

        // Get the order by field
        String orderByField = ServletRequestUtils.getStringParameter(request,
                                                                     "orderBy",
                                                                     CsrSearchField.PAONAME.toString());
        OrderBy orderBy = new OrderBy(orderByField,
                                      ServletRequestUtils.getBooleanParameter(request,
                                                                              "descending",
                                                                              false));

        // Build up filter by list
        List<FilterBy> filterByList = FilterByGenerator.getFilterByList();
        List<FilterBy> queryFilter = CsrUtils.getQueryFilter(request, filterByList);
        
        // Make friendly csr filter string
        List<String> filterByStringList = new ArrayList<String>();
        for (FilterBy filterBy : queryFilter) {
            filterByStringList.add(filterBy.toCsrString());
        }
        String filterByString = StringUtils.join(filterByStringList, " and ");

        // Perform the search
        SearchResult<ExtendedMeter> results = csrService.search(queryFilter,
                                                                orderBy,
                                                                startIndex,
                                                                count);
        

        ModelAndView mav;
        // Redirect to device home page if only one result is found
        if (results.getResultCount() == 1) {
            mav = new ModelAndView("redirect:/spring/csr/home");

            ExtendedMeter meter = results.getResultList().get(0);
            mav.addObject("deviceId", meter.getDeviceId());

        } else {
            mav = new ModelAndView("deviceSelection.jsp");
            // Create a device collection (only used to generate a link)
            DeviceCollection deviceGroupCollection = filterCollectionHelper.createDeviceGroupCollection(queryFilter, orderBy);
            
            mav.addObject("deviceGroupCollection", deviceGroupCollection);
            
            mav.addObject("filterByString", filterByString);
            mav.addObject("orderBy", orderBy);
            mav.addObject("results", results);
            mav.addObject("orderByFields", CsrSearchField.values());
            mav.addObject("filterByList", filterByList);
        }

        return mav;
    }

    public ModelAndView home(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("deviceHome.jsp");

        int deviceId = ServletRequestUtils.getIntParameter(request, "deviceId");

        YukonDevice device = deviceDao.getYukonDevice(deviceId);
        boolean highBillSupported = DeviceTypesFuncs.isMCT410(device.getType());

        mav.addObject("deviceId", deviceId);
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
