package com.cannontech.web.amr.csr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.roles.operator.MeteringRole;
import com.cannontech.roles.yukon.MultispeakRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.yc.bean.YCBean;

/**
 * Spring controller class for csr
 */
public class CsrController extends MultiActionController {

    private AuthDao authDao = null;
    private RoleDao roleDao = null;
    private CsrService csrService = null;
    private AttributeService attributeService = null;
    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;
    private CommandDao commandDao = null;

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

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public void setCommandDao(CommandDao commandDao) {
        this.commandDao = commandDao;
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
        boolean highBillSupported = DeviceTypesFuncs.isMCT410(device.getType());

        mav.addObject("deviceId", deviceId);
        mav.addObject("highBillSupported", highBillSupported);

        Set<Attribute> availableAttributes = attributeService.getAvailableAttributes(device);

        boolean outageSupported = (availableAttributes.contains(BuiltInAttribute.OUTAGE_LOG) || availableAttributes.contains(BuiltInAttribute.BLINK_COUNT));
        mav.addObject("outageSupported", outageSupported);

        mav.addObject("mspSupported",
                      Integer.valueOf(roleDao.getGlobalPropertyValue(MultispeakRole.MSP_PRIMARY_CB_VENDORID))
                             .intValue() > 0);

        boolean disconnectSupported = DeviceTypesFuncs.isDisconnectEnabled(device);
        mav.addObject("disconnectSupported", disconnectSupported);

        LiteYukonUser user = ServletUtil.getYukonUser(request);

        boolean deviceGroupEnabled = Boolean.parseBoolean(authDao.getRolePropertyValue(user,
                                                                                       MeteringRole.DEVICE_GROUP_ENABLED));
        mav.addObject("deviceGroupsSupported", deviceGroupEnabled);

        boolean touSupported = DeviceTypesFuncs.isTouEnabled(device);
        boolean touEnabled = Boolean.parseBoolean(authDao.getRolePropertyValue(user,
                                                                               MeteringRole.TOU_ENABLED));
        mav.addObject("touSupported", (touSupported && touEnabled));

        boolean moveSupported = DeviceTypesFuncs.isMCT410(device.getType());
        boolean moveEnabled = Boolean.parseBoolean(authDao.getRolePropertyValue(user,
                                                                                MeteringRole.MOVE_IN_MOVE_OUT_ENABLED));
        mav.addObject("moveSupported", (moveSupported && moveEnabled));

        boolean lpSupported = DeviceTypesFuncs.isMCT4XX(device.getType());
        mav.addObject("lpSupported", lpSupported);

        boolean lpEnabled = Boolean.parseBoolean(authDao.getRolePropertyValue(user,
                                                                              MeteringRole.PROFILE_REQUEST_ENABLED));
        mav.addObject("lpEnabled", lpEnabled);

        boolean peakReportSupported = DeviceTypesFuncs.isMCT410(device.getType());
        mav.addObject("peakReportSupported", peakReportSupported);

        boolean isMCT4XX = DeviceTypesFuncs.isMCT4XX(device.getType());
        mav.addObject("isMCT4XX", isMCT4XX);

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

    public ModelAndView profile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("profile.jsp");

        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        mav.addObject("deviceId", deviceId);

        return mav;
    }

    public ModelAndView voltageAndTou(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("voltageAndTou.jsp");

        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        mav.addObject("deviceId", deviceId);

        // Get or create the YCBean and put it into the session
        YCBean ycBean = (YCBean) request.getSession().getAttribute("YC_BEAN");
        if (ycBean == null) {
            ycBean = new YCBean();
        }
        request.getSession().setAttribute("YC_BEAN", ycBean);

        LiteYukonUser user = ServletUtil.getYukonUser(request);

        ycBean.setUserID(user.getUserID());
        ycBean.setDeviceID(deviceId);

        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        mav.addObject("device", device);

        // Get the point data for the page
        PointData data = ycBean.getRecentPointData(deviceId, 4, PointTypes.DEMAND_ACCUMULATOR_POINT);
        String lastIntervalTime = ycBean.getFormattedTimestamp(data, "---");
        mav.addObject("lastIntervalTime", lastIntervalTime);
        String lastIntervalValue = ycBean.getFormattedValue(data, "#0.000", "&nbsp;");
        mav.addObject("lastIntervalValue", lastIntervalValue);

        data = (PointData) ycBean.getRecentPointData(deviceId,
                                                     15,
                                                     PointTypes.DEMAND_ACCUMULATOR_POINT);
        String minimumTime = ycBean.getFormattedTimestamp(data, "---");
        mav.addObject("minimumTime", minimumTime);
        String minimumValue = ycBean.getFormattedValue(data, "#0.000", "&nbsp;");
        mav.addObject("minimumValue", minimumValue);

        data = (PointData) ycBean.getRecentPointData(deviceId,
                                                     14,
                                                     PointTypes.DEMAND_ACCUMULATOR_POINT);
        String maximumTime = ycBean.getFormattedTimestamp(data, "---");
        mav.addObject("maximumTime", maximumTime);
        String maximumValue = ycBean.getFormattedValue(data, "#0.000", "&nbsp;");
        mav.addObject("maximumValue", maximumValue);

        List<LiteTOUSchedule> schedules = DefaultDatabaseCache.getInstance().getAllTOUSchedules();
        mav.addObject("schedules", schedules);

        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
        if (errorMsg == null && ycBean.getErrorMsg() != null) {
            errorMsg = ycBean.getErrorMsg();
            ycBean.setErrorMsg("");
        }

        mav.addObject("errorMsg", errorMsg);

        return mav;
    }

    @SuppressWarnings("unchecked")
    public ModelAndView manualCommand(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("manualCommand.jsp");

        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        mav.addObject("deviceId", deviceId);

        // Get or create the YCBean and put it into the session
        YCBean ycBean = (YCBean) request.getSession().getAttribute("YC_BEAN");
        if (ycBean == null) {
            ycBean = new YCBean();
        }
        request.getSession().setAttribute("YC_BEAN", ycBean);

        LiteYukonUser user = ServletUtil.getYukonUser(request);
        ycBean.setUserID(user.getUserID());
        ycBean.setDeviceID(deviceId);
        ycBean.setDeviceType(deviceId);

        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        mav.addObject("device", device);
        mav.addObject("deviceType", ycBean.getDeviceType());

        // Get the list of commands
        Vector<LiteDeviceTypeCommand> commands = ycBean.getLiteDeviceTypeCommandsVector();
        List<LiteCommand> commandList = new ArrayList<LiteCommand>();
        for (LiteDeviceTypeCommand ldtc : commands) {

            LiteCommand command = commandDao.getCommand(ldtc.getCommandID());
            if (ldtc.isVisible() && ycBean.isAllowCommand(command.getCommand(), user, device)) {
                commandList.add(command);
            }
        }
        mav.addObject("commandList", commandList);

        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
        if (errorMsg == null && ycBean.getErrorMsg() != null) {
            errorMsg = ycBean.getErrorMsg();
            ycBean.setErrorMsg("");
        }

        mav.addObject("errorMsg", errorMsg);

        return mav;
    }

}
