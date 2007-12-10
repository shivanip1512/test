package com.cannontech.web.amr.csr;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.csr.model.CsrSearchField;
import com.cannontech.amr.csr.model.ExtendedMeter;
import com.cannontech.amr.csr.model.FilterBy;
import com.cannontech.amr.csr.model.FilterByGenerator;
import com.cannontech.amr.csr.model.OrderBy;
import com.cannontech.amr.csr.service.CsrService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.moveInMoveOut.bean.MoveInForm;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResult;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutForm;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResult;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutEmailService;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutService;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.MeteringRole;
import com.cannontech.roles.yukon.MultispeakRole;
import com.cannontech.util.ServletUtil;

/**
 * Spring controller class for csr
 */
public class CsrController extends MultiActionController {

    private AuthDao authDao = null;
    private AttributeService attributeService = null;
    private CsrService csrService = null;
    private DateFormattingService dateFormattingService = null;
    private DeviceDao deviceDao = null;
    private MeterDao meterDao = null;
    private MoveInMoveOutEmailService moveInMoveOutEmailService = null;
    private MoveInMoveOutService moveInMoveOutService = null;
    private RoleDao roleDao = null;
    
    public CsrController() {
        super();
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
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);

        boolean deviceGroupEnabled = Boolean.parseBoolean(authDao.getRolePropertyValue(user, MeteringRole.DEVICE_GROUP_ENABLED));
        mav.addObject("deviceGroupsSupported", deviceGroupEnabled);

        boolean touSupported = DeviceTypesFuncs.isTouEnabled(device);
        boolean touEnabled = Boolean.parseBoolean(authDao.getRolePropertyValue(user, MeteringRole.TOU_ENABLED));
        mav.addObject("touSupported", (touSupported && touEnabled));

        boolean moveSupported = DeviceTypesFuncs.isMCT410(device.getType());
        boolean moveEnabled = Boolean.parseBoolean(authDao.getRolePropertyValue(user, MeteringRole.MOVE_IN_MOVE_OUT_ENABLED));
        mav.addObject("moveSupported", (moveSupported && moveEnabled));
        
        boolean lpSupported = DeviceTypesFuncs.isMCT4XX(device.getType());
        mav.addObject("lpSupported", lpSupported);

        boolean lpEnabled = Boolean.parseBoolean(authDao.getRolePropertyValue(user, MeteringRole.PROFILE_REQUEST_ENABLED));
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
    
    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView moveIn(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("moveIn.jsp");
        Meter meter = getMeter(request);
        LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(request);

        Date currentDate = new Date();
        String currentDateFormatted = dateFormattingService.formatDate(currentDate,
                                                                       DateFormattingService.DateFormatEnum.DATE,
                                                                       liteYukonUser);

        // Adds the group to the mav object
        mav.addObject("meter", meter);
        mav.addObject("deviceId", meter.getDeviceId());
        mav.addObject("currentDate", currentDateFormatted);

        return mav;
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView moveInRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = moveIn(request, response);

        // Getting all the needed form values
        Meter prevMeter = getMeter(request);
        LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(request);

        String deviceName = ServletRequestUtils.getStringParameter(request,
                                                                   "deviceName");
        String meterNumber = ServletRequestUtils.getStringParameter(request,
                                                                    "meterNumber");
        String emailAddress = ServletRequestUtils.getStringParameter(request,
                                                                     "emailAddress");
        String moveInDateStr = ServletRequestUtils.getStringParameter(request,
                                                                      "moveInDate");

        Date moveInDate = null;
        if (moveInDateStr != null) {
            try {
                moveInDate = dateFormattingService.flexibleDateParser(moveInDateStr,
                                                                      DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                                      liteYukonUser);
            } catch (ParseException pe) {
                moveInDate = null;
            }
        }

        MoveInForm moveInForm = new MoveInForm();
        moveInForm.setEmailAddress(emailAddress);
        moveInForm.setLiteYukonUser(liteYukonUser);
        moveInForm.setMeterName(deviceName);
        moveInForm.setMeterNumber(meterNumber);
        moveInForm.setMoveInDate(moveInDate);
        moveInForm.setPreviousMeter(prevMeter);
        if (!varifiedParametersMoveIn(moveInForm, mav)) {
            return mav;
        }

        Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        MoveInResult moveInResult = null;
        if (!currentDate.before(moveInDate)) {
            moveInResult = moveInMoveOutService.moveIn(moveInForm);
            moveInResult.setMoveInDate(moveInDate);
            if (moveInResult.getErrors().isEmpty()) {
                mav.addObject("endDate",
                              dateFormattingService.formatDate(moveInResult.getCurrentReading()
                                                                           .getPointDataTimeStamp(),
                                                               DateFormatEnum.BOTH,
                                                               liteYukonUser));
            }
        } else {
            moveInResult = moveInMoveOutService.scheduleMoveIn(moveInForm);
            moveInResult.setMoveInDate(moveInDate);
        }
        moveInMoveOutEmailService.createMoveInEmail(moveInResult, liteYukonUser);

        mav.addObject("currentReading", moveInResult.getCurrentReading());
        mav.addObject("calculatedDifference",
                      moveInResult.getCalculatedDifference());
        mav.addObject("previousReadingValue",
                      moveInResult.getCalculatedPreviousReading());
        mav.addObject("beginDate",
                      dateFormattingService.formatDate(moveInForm.getMoveInDate(),
                                                       DateFormatEnum.DATE,
                                                       liteYukonUser));
        mav.addObject("prevMeter", moveInResult.getPreviousMeter());
        mav.addObject("newMeter", moveInResult.getNewMeter());
        mav.addObject("errors", moveInResult.getErrors());
        mav.addObject("deviceGroups", moveInResult.getDeviceGroupsRemoved());
        mav.addObject("submissionType", moveInResult.getSubmissionType());
        mav.addObject("scheduled", moveInResult.isScheduled());

        return mav;
    }

    public ModelAndView moveOut(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("moveOut.jsp");
        Meter meter = getMeter(request);
        LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(request);

        Date currentDate = new Date();
        String currentDateFormatted = dateFormattingService.formatDate(currentDate,
                                                                       DateFormattingService.DateFormatEnum.DATE,
                                                                       liteYukonUser);

        // Adds the group to the mav object
        mav.addObject("meter", meter);
        mav.addObject("deviceId", meter.getDeviceId());
        mav.addObject("currentDate", currentDateFormatted);
        return mav;
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView moveOutRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = moveOut(request, response);

        // Getting all the needed form values
        Meter prevMeter = getMeter(request);
        LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(request);

        String emailAddress = ServletRequestUtils.getStringParameter(request,
                                                                     "emailAddress");
        String moveOutDateStr = ServletRequestUtils.getStringParameter(request,
                                                                       "moveOutDate");

        Date moveOutDate = null;
        if (moveOutDateStr != null) {
            try {
                moveOutDate = dateFormattingService.flexibleDateParser(moveOutDateStr,
                                                                       DateFormattingService.DateOnlyMode.END_OF_DAY,
                                                                       liteYukonUser);
            } catch (ParseException pe) {
                moveOutDate = null;
            }

        }

        MoveOutForm moveOutForm = new MoveOutForm();
        moveOutForm.setEmailAddress(emailAddress);
        moveOutForm.setLiteYukonUser(liteYukonUser);
        moveOutForm.setMeter(prevMeter);
        moveOutForm.setMoveOutDate(moveOutDate);

        if (!verifiedParametersMoveOut(moveOutForm, mav)) {
            return mav;
        }

        MoveOutResult moveOutResult = null;
        if ((new Date()).after(moveOutDate)) {
            moveOutResult = moveInMoveOutService.moveOut(moveOutForm);
            moveOutResult.setMoveOutDate(moveOutDate);
            if (moveOutResult == null) {
                mav.addObject("validationErrors",
                              "Move out must have previous readings to process!");
                return mav;
            }
            
            if (moveOutResult.getErrors().isEmpty()) {
                Date currentReadingDate = moveOutResult.getCurrentReading()
                                                       .getPointDataTimeStamp();
                Date calcReadingDate = new Date(moveOutResult.getCalculatedReading()
                                                             .getPointDataTimeStamp()
                                                             .getTime() - 1);

                mav.addObject("beginDate",
                              dateFormattingService.formatDate(calcReadingDate,
                                                               DateFormatEnum.DATE,
                                                               liteYukonUser));

                mav.addObject("endDate",
                              dateFormattingService.formatDate(currentReadingDate,
                                                               DateFormatEnum.BOTH,
                                                               liteYukonUser));

            }
        } else {
            moveOutResult = moveInMoveOutService.scheduleMoveOut(moveOutForm);
            moveOutResult.setMoveOutDate(moveOutDate);
            mav.addObject("endDate",
                          dateFormattingService.formatDate(new Date(moveOutDate.getTime() - 1),
                                                           DateFormatEnum.DATE,
                                                           liteYukonUser));
        }
        moveInMoveOutEmailService.createMoveOutEmail(moveOutResult,
                                                     liteYukonUser);

        mav.addObject("currentReading", moveOutResult.getCurrentReading());
        mav.addObject("calculatedUsage", moveOutResult.getCalculatedReading());
        mav.addObject("calculatedDifference",
                      moveOutResult.getCalculatedDifference());
        mav.addObject("meter", moveOutResult.getPreviousMeter());
        mav.addObject("errors", moveOutResult.getErrors());
        mav.addObject("deviceGroups", moveOutResult.getDeviceGroupsAdded());
        mav.addObject("submissionType", moveOutResult.getSubmissionType());
        mav.addObject("scheduled", moveOutResult.isScheduled());

        return mav;
    }

    /**
     * This function verifies that all the information was correctly entered on
     * the desired move in service
     * @param moveInFormObj
     * @param mav
     * @return
     */
    private boolean varifiedParametersMoveIn(MoveInForm moveInForm,
            ModelAndView mav) {

        boolean verified = true;
        List<String> validationErrors = new ArrayList<String>();

        // Checks for null values
        if (StringUtils.isBlank(moveInForm.getMeterName()) || StringUtils.isBlank(moveInForm.getMeterNumber()) || moveInForm.getMoveInDate() == null) {
            validationErrors.add("Please make sure to fill in all spaces other than the optional email field.");

            verified = false;
        } else {
            // Checks for invalid entries
            if (!moveInForm.getPreviousMeter()
                           .getName()
                           .equals(moveInForm.getMeterName())) {
                try {
                    meterDao.getForPaoName(moveInForm.getMeterName());

                    validationErrors.add("The meter name you have entered will create a duplicate name entry.  Please submit a different name.");
                    verified = false;
                } catch (NotFoundException nfe) {}
            }

            // Checks to see if a date is with in a 90 day time frame
            long ninetyDays = 1000L * 60L * 60L * 24L * 90L;
            Date moveInDate = new Date(moveInForm.getMoveInDate().getTime() + ninetyDays);
            Date currentDate = new Date();
            if (!moveInDate.after(currentDate)) {
                validationErrors.add("Please make sure that your move in date is not more than 90 days in the past. ");
                verified = false;
            }
        }

        mav.addObject("validationErrors", validationErrors);

        return verified;
    }

    /**
     * This function verifies that all the information was correctly entered on
     * the desired move in service
     * @param moveInFormObj
     * @param mav
     * @return
     */
    private boolean verifiedParametersMoveOut(MoveOutForm moveOutForm,
            ModelAndView mav) {

        boolean verified = true;
        List<String> validationErrors = new ArrayList<String>();

        // Checks for null values
        if (moveOutForm.getMoveOutDate() == null) {
            validationErrors.add("Please enter or select a move out date.");

            verified = false;
        } else {

            // Checks to see if a date is with in a 90 day time frame
            long ninetyDays = 1000L * 60L * 60L * 24L * 90L;
            Date moveOutDate = new Date(moveOutForm.getMoveOutDate().getTime() + ninetyDays);
            Date currentDate = new Date();
            if (!moveOutDate.after(currentDate)) {
                validationErrors.add("Please make sure that your move in date is not more than 90 days in the past. ");
                verified = false;
            }
        }
        mav.addObject("validationErrors", validationErrors);

        return verified;
    }

    /**
     * @param request
     * @return
     * @throws ServletRequestBindingException
     */
    private Meter getMeter(HttpServletRequest request)
            throws ServletRequestBindingException {
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request,
                                                                   "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }
    
    @Required
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Required
    public void setCsrService(CsrService csrService) {
        this.csrService = csrService;
    }

    @Required
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setMoveInMoveOutEmailService(
            MoveInMoveOutEmailService moveInMoveOutEmailService) {
        this.moveInMoveOutEmailService = moveInMoveOutEmailService;
    }

    @Required
    public void setMoveInMoveOutService(MoveInMoveOutService moveInMoveOutService) {
        this.moveInMoveOutService = moveInMoveOutService;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

}
