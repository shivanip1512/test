package com.cannontech.web.amr.meter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
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
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.MOVE_IN_MOVE_OUT)
public class MoveInMoveOutController {

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private MeterDao meterDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private MoveInMoveOutEmailService moveInMoveOutEmailService;
    @Autowired private MoveInMoveOutService moveInMoveOutService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private EmailService emailService;

    @RequestMapping("moveIn")
    public String moveIn(HttpServletRequest request, ModelMap model) throws ServletRequestBindingException {
        PlcMeter meter = getMeter(request);
        SimpleDevice device = deviceDao.getYukonDevice(meter.getDeviceId());
        LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(request);
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        // Adds the group to the mav object
        model.addAttribute("meter", meter);
        model.addAttribute("deviceId", meter.getDeviceId());
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        model.addAttribute("currentDate", new Date());
        model.addAttribute("email", emailService.getUserEmail(userContext));
        model.addAttribute("isSMTPConfigured", emailService.isSmtpConfigured());
        
        // readable?
        boolean readable = moveInMoveOutService.isAuthorized(liteYukonUser, meter);
        model.addAttribute("readable", readable);

        return "moveIn.jsp";
    }

    @RequestMapping("moveInRequest")
    public String moveInRequest(HttpServletRequest request, ModelMap model) throws Exception {

        PlcMeter meter = getMeter(request);
        SimpleDevice device = deviceDao.getYukonDevice(meter.getDeviceId());
        LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(request);      

        // Adds the group to the mav object
        model.addAttribute("meter", meter);
        model.addAttribute("deviceId", meter.getDeviceId());
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        model.addAttribute("currentDate", new Date());
        
        // readable?
        boolean readable = moveInMoveOutService.isAuthorized(liteYukonUser, meter);
        model.addAttribute("readable", readable);

        // Getting all the needed form values
        PlcMeter prevMeter = getMeter(request);
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        String deviceName = ServletRequestUtils.getStringParameter(request, "deviceName");
        String meterNumber = ServletRequestUtils.getStringParameter(request, "meterNumber");
        String emailAddress = ServletRequestUtils.getStringParameter(request, "emailAddress");
        String moveInDateStr = ServletRequestUtils.getStringParameter(request, "moveInDate");

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
        if (!varifiedParametersMoveIn(moveInForm, model)) {
            return "moveIn.jsp";
        }

        MoveInResult moveInResult = null;
        moveInResult = moveInMoveOutService.scheduleMoveIn(moveInForm);
        moveInResult.setMoveInDate(moveInDate);
        boolean sendEmailNotification = ServletRequestUtils.getBooleanParameter(request, "sendEmail", false);
        if (sendEmailNotification) {
        moveInMoveOutEmailService.createMoveInEmail(moveInResult, userContext);
        }

        model.addAttribute("currentReading", moveInResult.getCurrentReading());
        model.addAttribute("calculatedDifference",
                      moveInResult.getCalculatedDifference());
        model.addAttribute("previousReadingValue",
                      moveInResult.getCalculatedPreviousReading());
        model.addAttribute("beginDate",
                      dateFormattingService.format(moveInForm.getMoveInDate(),
                                                       DateFormatEnum.BOTH,
                                                       userContext));
        model.addAttribute("prevMeter", moveInResult.getPreviousMeter());
        model.addAttribute("newMeter", moveInResult.getNewMeter());
        model.addAttribute("errors", moveInResult.getErrors());
        model.addAttribute("errorMessage", moveInResult.getErrorMessage());
        model.addAttribute("deviceGroups", moveInResult.getDeviceGroupsRemoved());
        model.addAttribute("submissionType", moveInResult.getSubmissionType());
        model.addAttribute("scheduled", moveInResult.isScheduled());
        
        //Build i18n strings with multiple arguments
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String moveInSuccessMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.moveIn.success", 
                                                                          moveInResult.getNewMeter().getName(),
                                                                          dateFormattingService.format(moveInForm.getMoveInDate(),
                                                                                                       DateFormatEnum.BOTH,
                                                                                                       userContext));
        model.addAttribute("moveInSuccessMsg", moveInSuccessMsg);
        if(moveInResult.getPreviousMeter().getName() != moveInResult.getNewMeter().getName()) {
            String renameSuccessMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.moveIn.rename",
                                                                       moveInResult.getPreviousMeter().getName(),
                                                                       moveInResult.getNewMeter().getName());
            model.addAttribute("renameSuccessMsg", renameSuccessMsg);
        }
        if(moveInResult.getPreviousMeter().getMeterNumber() != moveInResult.getNewMeter().getMeterNumber()) {
            String newNumberSuccessMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.moveIn.newNumber",
                                                                          moveInResult.getPreviousMeter().getMeterNumber(),
                                                                          moveInResult.getNewMeter().getMeterNumber());
            model.addAttribute("newNumberSuccessMsg", newNumberSuccessMsg);
        }
        
        return "moveIn.jsp";
    }
    
    @RequestMapping("moveOut")
    public String moveOut(HttpServletRequest request, ModelMap model) throws ServletRequestBindingException {
        
        PlcMeter meter = getMeter(request);
        SimpleDevice device = deviceDao.getYukonDevice(meter.getDeviceId());
        LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(request);
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        // Adds the group to the mav object
        model.addAttribute("meter", meter);
        model.addAttribute("deviceId", meter.getDeviceId());
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        model.addAttribute("currentDate", new Date());
        model.addAttribute("email", emailService.getUserEmail(userContext));
        model.addAttribute("isSMTPConfigured", emailService.isSmtpConfigured());
        
        // readable?
        boolean readable = moveInMoveOutService.isAuthorized(liteYukonUser, meter);
        model.addAttribute("readable", readable);
        
        return "moveOut.jsp";
    }

    @RequestMapping("moveOutRequest")
    public String moveOutRequest(HttpServletRequest request, ModelMap model) throws ServletRequestBindingException {

        PlcMeter meter = getMeter(request);
        SimpleDevice device = deviceDao.getYukonDevice(meter.getDeviceId());
        LiteYukonUser liteYukonUser = ServletUtil.getYukonUser(request);

        // Adds the group to the mav object
        model.addAttribute("meter", meter);
        model.addAttribute("deviceId", meter.getDeviceId());
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        model.addAttribute("currentDate", new Date());
        
        // readable?
        boolean readable = moveInMoveOutService.isAuthorized(liteYukonUser, meter);
        model.addAttribute("readable", readable);

        // Getting all the needed form values
        PlcMeter prevMeter = getMeter(request);
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

        if (!verifiedParametersMoveOut(moveOutForm, model)) {
            return "moveOut.jsp";
        }

        MoveOutResult moveOutResult = null;
        moveOutResult = moveInMoveOutService.scheduleMoveOut(moveOutForm);
        moveOutResult.setMoveOutDate(moveOutDate);
        model.addAttribute("endDate",
                      dateFormattingService.format(new Date(moveOutDate.getTime()),
                                                   DateFormatEnum.BOTH,
                                                   userContext));
        moveInMoveOutEmailService.createMoveOutEmail(moveOutResult,
                                                     userContext);

        model.addAttribute("currentReading", moveOutResult.getCurrentReading());
        model.addAttribute("calculatedUsage", moveOutResult.getCalculatedReading());
        model.addAttribute("calculatedDifference",
                      moveOutResult.getCalculatedDifference());
        model.addAttribute("meter", moveOutResult.getPreviousMeter());
        model.addAttribute("errors", moveOutResult.getErrors());
        model.addAttribute("errorMessage", moveOutResult.getErrorMessage());
        model.addAttribute("deviceGroups", moveOutResult.getDeviceGroupsAdded());
        model.addAttribute("submissionType", moveOutResult.getSubmissionType());
        model.addAttribute("scheduled", moveOutResult.isScheduled());
        //Build i18n strings with multiple arguments
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String moveOutSuccessMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.moveOut.success", 
                                                                          moveOutResult.getPreviousMeter().getName(),
                                                                          dateFormattingService.format(moveOutForm.getMoveOutDate(),
                                                                                                       DateFormatEnum.BOTH,
                                                                                                       userContext));
        model.addAttribute("moveOutSuccessMsg", moveOutSuccessMsg);
        
        return "moveOut.jsp";
    }

    /**
     * This function verifies that all the information was correctly entered on
     * the desired move in service
     */
    private boolean varifiedParametersMoveIn(MoveInForm moveInForm, ModelMap model) {

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

        model.addAttribute("validationErrors", validationErrors);

        return verified;
    }

    /**
     * This function verifies that all the information was correctly entered on
     * the desired move in service
     * @param moveInFormObj
     * @param mav
     * @return
     */
    private boolean verifiedParametersMoveOut(MoveOutForm moveOutForm, ModelMap model) {

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
        model.addAttribute("validationErrors", validationErrors);

        return verified;
    }

    private PlcMeter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        PlcMeter meter = meterDao.getPlcMeterForId(deviceId);
        return meter;
    }
    
}