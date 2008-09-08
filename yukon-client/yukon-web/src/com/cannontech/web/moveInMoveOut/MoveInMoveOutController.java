package com.cannontech.web.moveInMoveOut;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
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
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

/**
 * Widget used to display basic device information
 */
public class MoveInMoveOutController extends MultiActionController {

    private DateFormattingService dateFormattingService = null;
    private MeterDao meterDao = null;
    private MoveInMoveOutEmailService moveInMoveOutEmailService = null;
    private MoveInMoveOutService moveInMoveOutService = null;

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
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        Date currentDate = new Date();
        String currentDateFormatted = dateFormattingService.formatDate(currentDate,
                                                                       DateFormattingService.DateFormatEnum.DATE,
                                                                       userContext);

        // Adds the group to the mav object
        mav.addObject("meter", meter);
        mav.addObject("deviceId", meter.getDeviceId());
        mav.addObject("currentDate", currentDateFormatted);
        
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
                moveInDate = dateFormattingService.flexibleDateParser(moveInDateStr,
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

        Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        MoveInResult moveInResult = null;
        if (!currentDate.before(moveInDate)) {
            moveInResult = moveInMoveOutService.moveIn(moveInForm);
            moveInResult.setMoveInDate(moveInDate);
            if (moveInResult.getErrors().isEmpty() && StringUtils.isBlank(moveInResult.getErrorMessage())) {
                mav.addObject("endDate",
                              dateFormattingService.formatDate(moveInResult.getCurrentReading()
                                                                           .getPointDataTimeStamp(),
                                                               DateFormatEnum.BOTH,
                                                               userContext));
            }
        } else {
            moveInResult = moveInMoveOutService.scheduleMoveIn(moveInForm);
            moveInResult.setMoveInDate(moveInDate);
        }
        moveInMoveOutEmailService.createMoveInEmail(moveInResult, userContext);

        mav.addObject("currentReading", moveInResult.getCurrentReading());
        mav.addObject("calculatedDifference",
                      moveInResult.getCalculatedDifference());
        mav.addObject("previousReadingValue",
                      moveInResult.getCalculatedPreviousReading());
        mav.addObject("beginDate",
                      dateFormattingService.formatDate(moveInForm.getMoveInDate(),
                                                       DateFormatEnum.DATE,
                                                       userContext));
        mav.addObject("prevMeter", moveInResult.getPreviousMeter());
        mav.addObject("newMeter", moveInResult.getNewMeter());
        mav.addObject("errors", moveInResult.getErrors());
        mav.addObject("errorMessage", moveInResult.getErrorMessage());
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
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        Date currentDate = new Date();
        String currentDateFormatted = dateFormattingService.formatDate(currentDate,
                                                                       DateFormattingService.DateFormatEnum.DATE,
                                                                       userContext);

        // Adds the group to the mav object
        mav.addObject("meter", meter);
        mav.addObject("deviceId", meter.getDeviceId());
        mav.addObject("currentDate", currentDateFormatted);
        
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
                moveOutDate = dateFormattingService.flexibleDateParser(moveOutDateStr,
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
        if ((new Date()).after(moveOutDate)) {
            moveOutResult = moveInMoveOutService.moveOut(moveOutForm);
            moveOutResult.setMoveOutDate(moveOutDate);
            if (moveOutResult == null) {
                mav.addObject("validationErrors",
                              "Move out must have previous readings to process!");
                return mav;
            }

            if (moveOutResult.getErrors().isEmpty() && StringUtils.isBlank(moveOutResult.getErrorMessage()) ) {
                Date currentReadingDate = moveOutResult.getCurrentReading()
                                                       .getPointDataTimeStamp();
                Date calcReadingDate = new Date(moveOutResult.getCalculatedReading()
                                                             .getPointDataTimeStamp()
                                                             .getTime() - 1);

                mav.addObject("beginDate",
                              dateFormattingService.formatDate(calcReadingDate,
                                                               DateFormatEnum.DATE,
                                                               userContext));

                mav.addObject("endDate",
                              dateFormattingService.formatDate(currentReadingDate,
                                                               DateFormatEnum.BOTH,
                                                               userContext));

            }
        } else {
            moveOutResult = moveInMoveOutService.scheduleMoveOut(moveOutForm);
            moveOutResult.setMoveOutDate(moveOutDate);
            mav.addObject("endDate",
                          dateFormattingService.formatDate(new Date(moveOutDate.getTime() - 1),
                                                           DateFormatEnum.DATE,
                                                           userContext));
        }
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
}
