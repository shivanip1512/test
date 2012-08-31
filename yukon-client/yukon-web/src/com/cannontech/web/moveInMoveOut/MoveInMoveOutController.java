package com.cannontech.web.moveInMoveOut;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.moveInMoveOut.bean.MoveInForm;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResult;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutForm;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResult;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutEmailService;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;

/**
 * Widget used to display basic device information
 */
@CheckRoleProperty(YukonRoleProperty.MOVE_IN_MOVE_OUT)
public class MoveInMoveOutController extends MultiActionController {

    private DateFormattingService dateFormattingService = null;
    private MeterDao meterDao = null;
    private DeviceDao deviceDao = null;
    private PaoLoadingService paoLoadingService = null;
    private MoveInMoveOutEmailService moveInMoveOutEmailService = null;
    private MoveInMoveOutService moveInMoveOutService = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;

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
        SimpleDevice device = deviceDao.getYukonDevice(meter.getDeviceId());
        LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(request);

        // Adds the group to the mav object
        mav.addObject("meter", meter);
        mav.addObject("deviceId", meter.getDeviceId());
        mav.addObject("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        mav.addObject("currentDate", new Date());
        
        // readable?
        boolean readable = moveInMoveOutService.isAuthorized(liteYukonUser, meter);
        mav.addObject("readable", readable);

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
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

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
                moveInDate = dateFormattingService.flexibleDateParserWithSystemTimeZone(moveInDateStr,
                                                                      DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                                      userContext);
            } catch (ParseException pe) {
                moveInDate = null;
            }
        }

        MoveInForm moveInForm = new MoveInForm();
        moveInForm.setEmailAddress(emailAddress);
        moveInForm.setUserContext(userContext);
        moveInForm.setMeterName(deviceName);
        moveInForm.setMeterNumber(meterNumber);
        moveInForm.setMoveInDate(moveInDate);
        moveInForm.setPreviousMeter(prevMeter);
        if (!varifiedParametersMoveIn(moveInForm, mav)) {
            return mav;
        }

        MoveInResult moveInResult = null;
        moveInResult = moveInMoveOutService.scheduleMoveIn(moveInForm);
        moveInResult.setMoveInDate(moveInDate);
        moveInMoveOutEmailService.createMoveInEmail(moveInResult, userContext);

        mav.addObject("currentReading", moveInResult.getCurrentReading());
        mav.addObject("calculatedDifference",
                      moveInResult.getCalculatedDifference());
        mav.addObject("previousReadingValue",
                      moveInResult.getCalculatedPreviousReading());
        mav.addObject("beginDate",
                      dateFormattingService.format(moveInForm.getMoveInDate(),
                                                       DateFormatEnum.BOTH,
                                                       userContext));
        mav.addObject("prevMeter", moveInResult.getPreviousMeter());
        mav.addObject("newMeter", moveInResult.getNewMeter());
        mav.addObject("errors", moveInResult.getErrors());
        mav.addObject("errorMessage", moveInResult.getErrorMessage());
        mav.addObject("deviceGroups", moveInResult.getDeviceGroupsRemoved());
        mav.addObject("submissionType", moveInResult.getSubmissionType());
        mav.addObject("scheduled", moveInResult.isScheduled());
        
        //Build i18n strings with multiple arguments
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String moveInSuccessMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.moveIn.success", 
                                                                          moveInResult.getNewMeter().getName(),
                                                                          dateFormattingService.format(moveInForm.getMoveInDate(),
                                                                                                       DateFormatEnum.BOTH,
                                                                                                       userContext));
        mav.addObject("moveInSuccessMsg", moveInSuccessMsg);
        if(moveInResult.getPreviousMeter().getName() != moveInResult.getNewMeter().getName()) {
            String renameSuccessMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.moveIn.rename",
                                                                       moveInResult.getPreviousMeter().getName(),
                                                                       moveInResult.getNewMeter().getName());
            mav.addObject("renameSuccessMsg", renameSuccessMsg);
        }
        if(moveInResult.getPreviousMeter().getMeterNumber() != moveInResult.getNewMeter().getMeterNumber()) {
            String newNumberSuccessMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.moveIn.newNumber",
                                                                          moveInResult.getPreviousMeter().getMeterNumber(),
                                                                          moveInResult.getNewMeter().getMeterNumber());
            mav.addObject("newNumberSuccessMsg", newNumberSuccessMsg);
        }
        
        return mav;
    }

    public ModelAndView moveOut(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("moveOut.jsp");
        Meter meter = getMeter(request);
        SimpleDevice device = deviceDao.getYukonDevice(meter.getDeviceId());
        LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(request);

        // Adds the group to the mav object
        mav.addObject("meter", meter);
        mav.addObject("deviceId", meter.getDeviceId());
        mav.addObject("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        mav.addObject("currentDate", new Date());
        
        // readable?
        boolean readable = moveInMoveOutService.isAuthorized(liteYukonUser, meter);
        mav.addObject("readable", readable);
        
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
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        String emailAddress = ServletRequestUtils.getStringParameter(request,
                                                                     "emailAddress");
        String moveOutDateStr = ServletRequestUtils.getStringParameter(request,
                                                                       "moveOutDate");

        Date moveOutDate = null;
        if (moveOutDateStr != null) {
            try {
                moveOutDate = dateFormattingService.flexibleDateParserWithSystemTimeZone(moveOutDateStr,
                                                                       DateFormattingService.DateOnlyMode.END_OF_DAY,
                                                                       userContext);
            } catch (ParseException pe) {
                moveOutDate = null;
            }

        }

        MoveOutForm moveOutForm = new MoveOutForm();
        moveOutForm.setEmailAddress(emailAddress);
        moveOutForm.setUserContext(userContext);
        moveOutForm.setMeter(prevMeter);
        moveOutForm.setMoveOutDate(moveOutDate);

        if (!verifiedParametersMoveOut(moveOutForm, mav)) {
            return mav;
        }

        MoveOutResult moveOutResult = null;
        moveOutResult = moveInMoveOutService.scheduleMoveOut(moveOutForm);
        moveOutResult.setMoveOutDate(moveOutDate);
        mav.addObject("endDate",
                      dateFormattingService.format(new Date(moveOutDate.getTime()),
                                                   DateFormatEnum.BOTH,
                                                   userContext));
        moveInMoveOutEmailService.createMoveOutEmail(moveOutResult,
                                                     userContext);

        mav.addObject("currentReading", moveOutResult.getCurrentReading());
        mav.addObject("calculatedUsage", moveOutResult.getCalculatedReading());
        mav.addObject("calculatedDifference",
                      moveOutResult.getCalculatedDifference());
        mav.addObject("meter", moveOutResult.getPreviousMeter());
        mav.addObject("errors", moveOutResult.getErrors());
        mav.addObject("errorMessage", moveOutResult.getErrorMessage());
        mav.addObject("deviceGroups", moveOutResult.getDeviceGroupsAdded());
        mav.addObject("submissionType", moveOutResult.getSubmissionType());
        mav.addObject("scheduled", moveOutResult.isScheduled());
        //Build i18n strings with multiple arguments
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String moveOutSuccessMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.moveOut.success", 
                                                                          moveOutResult.getPreviousMeter().getName(),
                                                                          dateFormattingService.format(moveOutForm.getMoveOutDate(),
                                                                                                       DateFormatEnum.BOTH,
                                                                                                       userContext));
        mav.addObject("moveOutSuccessMsg", moveOutSuccessMsg);
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
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    @Required
    public void setMoveInMoveOutEmailService(
            MoveInMoveOutEmailService moveInMoveOutEmailService) {
        this.moveInMoveOutEmailService = moveInMoveOutEmailService;
    }

    @Required
    public void setMoveInMoveOutService(
            MoveInMoveOutService moveInMoveOutService) {
        this.moveInMoveOutService = moveInMoveOutService;
    }
    
    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
}
