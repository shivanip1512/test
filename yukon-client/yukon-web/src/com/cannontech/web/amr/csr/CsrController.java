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
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.roles.yukon.MultispeakRole;
import com.cannontech.roles.operator.MeteringRole;

/**
 * Spring controller class for csr
 */
public class CsrController extends MultiActionController {

    private RoleDao roleDao = null;
    private CsrService csrService = null;
    private AttributeService attributeService = null;
    private DeviceDao deviceDao = null;

    public CsrController() {
        super();
    }

    public void setCsrService(CsrService csrService) {
        this.csrService = csrService;
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

    public ModelAndView search(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("deviceSelection.jsp");

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
        List<FilterBy> queryFilter = new ArrayList<FilterBy>();
        List<String> filterByString = new ArrayList<String>();

        for (FilterBy filterBy : filterByList) {
            String filterValue = ServletRequestUtils.getStringParameter(request, filterBy.getName());
            if (!StringUtils.isBlank(filterValue)) {
                filterBy.setFilterValue(filterValue);
                queryFilter.add(filterBy);
                filterByString.add(filterBy.toCsrString());
            }
        }

        // Perform the search
        SearchResult<ExtendedMeter> results = csrService.search(queryFilter,
                                                                orderBy,
                                                                startIndex,
                                                                count);

        // Forward to device home page if only one result is found
        if (results.getResultCount() == 1) {
            mav.setViewName("deviceHome.jsp");

            ExtendedMeter meter = results.getResultList().get(0);
            mav.addObject("deviceId", meter.getDeviceId());

        } else {
            mav.addObject("filterByString", StringUtils.join(filterByString, " and "));
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
        boolean highBillSupported = DeviceTypesFuncs.isMCT4XX(device.getType());

        mav.addObject("deviceId", deviceId);
        mav.addObject("highBillSupported", highBillSupported);
        
        Set<Attribute> availableAttributes = attributeService.getAvailableAttributes(device);
        
        boolean outageSupported = (availableAttributes.contains(BuiltInAttribute.OUTAGE_LOG) ||
        		availableAttributes.contains(BuiltInAttribute.BLINK_COUNT));
        mav.addObject("outageSupported", outageSupported);
        
        mav.addObject("mspSupported",
                      Integer.valueOf(roleDao.getGlobalPropertyValue(MultispeakRole.MSP_PRIMARY_CB_VENDORID))
                             .intValue() > 0);

        boolean disconnectSupported = DeviceTypesFuncs.isDisconnectEnabled(device);
        mav.addObject("disconnectSupported", disconnectSupported);

        boolean deviceGroupEnabled = Boolean.parseBoolean(roleDao.getGlobalPropertyValue(MeteringRole.DEVICE_GROUP_ENABLED));
        mav.addObject("deviceGroupsSupported", deviceGroupEnabled);
        
        boolean touSupported = DeviceTypesFuncs.isTouEnabled(device);
        boolean touEnabled = Boolean.parseBoolean(roleDao.getGlobalPropertyValue(MeteringRole.TOU_ENABLED));
        mav.addObject("touSupported", (touSupported && touEnabled));
        
        return mav;
    }

    public ModelAndView highBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("highBill.jsp");

        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        mav.addObject("deviceId", String.valueOf(deviceId));

        YukonDevice device = deviceDao.getYukonDevice(deviceId);

        boolean createLPPoint = ServletRequestUtils.getBooleanParameter(request,
                                                                        "createLPPoint",
                                                                        false);
        if (createLPPoint) {
            attributeService.createPointForAttribute(device, BuiltInAttribute.LOAD_PROFILE);
        }

        boolean lmPointExists = attributeService.pointExistsForAttribute(device,
                                                                         BuiltInAttribute.LOAD_PROFILE);
        mav.addObject("lmPointExists", lmPointExists);

        return mav;
    }

}
