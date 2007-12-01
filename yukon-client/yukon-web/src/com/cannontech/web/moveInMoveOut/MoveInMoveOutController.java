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

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.moveInMoveOut.bean.MoveInFormObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResultObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutFormObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResultObj;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutEmailService;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutService;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

/**
 * Widget used to display basic device information
 */
public class MoveInMoveOutController extends MultiActionController {

    public static final int tries = 3;
    public static final String ACTIVATED_EMAIL_LISTED = "A confirmation message will be sent to the email address listed above.";
    public static final String ACTIVATED_MOVE_IN_DESC = "<br />A meter reading is being calculated based on usage since ${endDate}." + "<br />" + ACTIVATED_EMAIL_LISTED;
    public static final String ACTIVATED_MOVE_OUT_DESC = "<br />An usage reading is being calculated based on the consumption since the usage read selected." + "<br />" + ACTIVATED_EMAIL_LISTED;

    private AttributeService attributeService = null;
    private DateFormattingService dateFormattingService = null;
    private MeterDao meterDao = null;
    private MeterReadService meterReadService = null;
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

        Date currentDate = new Date();
        String currentDateFormatted = dateFormattingService.formatDate(currentDate,
                                                                       DateFormattingService.DateFormatEnum.DATE,
                                                                       liteYukonUser);

        // Adds the group to the mav object
        mav.addObject("meter", meter);
        mav.addObject("deviceId", meter.getDeviceId());
        mav.addObject("currentDate", currentDateFormatted);
        mav.addObject("activatedMoveDesc", ACTIVATED_MOVE_IN_DESC);

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

        MoveInFormObj moveInFormObj = new MoveInFormObj();
        moveInFormObj.setEmailAddress(emailAddress);
        moveInFormObj.setLiteYukonUser(liteYukonUser);
        moveInFormObj.setMeterName(deviceName);
        moveInFormObj.setMeterNumber(meterNumber);
        moveInFormObj.setMoveInDate(moveInDate);
        moveInFormObj.setPreviousMeter(prevMeter);
        if (!varifiedParametersMoveIn(moveInFormObj, mav)) {
            return mav;
        }

        Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        MoveInResultObj moveInResultObj = null;
        if (!currentDate.before(moveInDate)) {
            moveInResultObj = moveInMoveOutService.moveIn(moveInFormObj);
            if (moveInResultObj.getErrors().isEmpty()) {
                if (StringUtils.isNotBlank(moveInFormObj.getEmailAddress())) {
                    moveInMoveOutEmailService.createMoveInSuccessEmail(moveInResultObj,
                                                                       liteYukonUser);
                }
                mav.addObject("endDate",
                              dateFormattingService.formatDate(moveInResultObj.getCurrentReading()
                                                                              .getPointDataTimeStamp(),
                                                               DateFormatEnum.BOTH,
                                                               liteYukonUser));
            } else {
                if (StringUtils.isNotBlank(moveInFormObj.getEmailAddress())) {

                    moveInMoveOutEmailService.createMoveInFailureEmail(moveInResultObj,
                                                                       liteYukonUser);
                }
            }
        } else {
            moveInResultObj = moveInMoveOutService.scheduleMoveIn(moveInFormObj);
            if (StringUtils.isNotBlank(moveInFormObj.getEmailAddress())) {
                moveInMoveOutEmailService.createMoveInScheduleEmail(moveInFormObj,
                                                                    liteYukonUser);
            }
        }

        mav.addObject("currentReading", moveInResultObj.getCurrentReading());
        mav.addObject("calculatedDifference",
                      moveInResultObj.getCalculatedDifference());
        mav.addObject("previousReadingValue",
                      moveInResultObj.getCalculatedPreviousReading());
        mav.addObject("beginDate",
                      dateFormattingService.formatDate(moveInFormObj.getMoveInDate(),
                                                       DateFormatEnum.DATE,
                                                       liteYukonUser));
        mav.addObject("prevMeter", moveInResultObj.getPreviousMeter());
        mav.addObject("newMeter", moveInResultObj.getNewMeter());
        mav.addObject("errors", moveInResultObj.getErrors());
        mav.addObject("deviceGroups", moveInResultObj.getDeviceGroupsRemoved());
        mav.addObject("submissionType", moveInResultObj.getSubmissionType());
        mav.addObject("scheduled", moveInResultObj.isScheduled());

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

        LitePoint lp = attributeService.getPointForAttribute(meter,
                                                             BuiltInAttribute.USAGE);
        meterReadService.fillInPreviousReadings(mav, lp, "DATE");

        // Adds the group to the mav object
        mav.addObject("meter", meter);
        mav.addObject("deviceId", meter.getDeviceId());
        mav.addObject("currentDate", currentDateFormatted);
        mav.addObject("activatedMoveDesc", ACTIVATED_MOVE_OUT_DESC);
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

        MoveOutFormObj moveOutFormObj = new MoveOutFormObj();
        moveOutFormObj.setEmailAddress(emailAddress);
        moveOutFormObj.setLiteYukonUser(liteYukonUser);
        moveOutFormObj.setMeter(prevMeter);
        moveOutFormObj.setMoveOutDate(moveOutDate);

        if (!varifiedParametersMoveOut(moveOutFormObj, mav)) {
            return mav;
        }

        MoveOutResultObj moveOutResultObj = null;
        if ((new Date()).after(moveOutDate)) {
            moveOutResultObj = moveInMoveOutService.moveOut(moveOutFormObj);
            if (moveOutResultObj == null) {
                mav.addObject("validationErrors",
                              "Move out must have previous readings to process!");
                return mav;
            }
            if (moveOutResultObj.getErrors().isEmpty()) {
                if (StringUtils.isNotBlank(moveOutFormObj.getEmailAddress())) {
                    moveInMoveOutEmailService.createMoveOutSuccessEmail(moveOutResultObj,
                                                                        liteYukonUser);
                }

                Date currentReadingDate = moveOutResultObj.getCurrentReading()
                                                          .getPointDataTimeStamp();
                Date calcReadingDate = new Date(moveOutResultObj.getCalculatedReading()
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

            } else {
                if (StringUtils.isNotBlank(moveOutFormObj.getEmailAddress())) {
                    moveInMoveOutEmailService.createMoveOutFailureEmail(moveOutResultObj,
                                                                        liteYukonUser);
                }
            }

        } else {
            moveOutResultObj = moveInMoveOutService.scheduleMoveOut(moveOutFormObj);
            if (StringUtils.isNotBlank(moveOutFormObj.getEmailAddress())) {
                moveInMoveOutEmailService.createMoveOutScheduleEmail(moveOutFormObj,
                                                                     liteYukonUser);
            }
            mav.addObject("endDate",
                          dateFormattingService.formatDate(new Date(moveOutDate.getTime() - 1),
                                                           DateFormatEnum.DATE,
                                                           liteYukonUser));
        }

        mav.addObject("currentReading", moveOutResultObj.getCurrentReading());
        mav.addObject("calculatedUsage",
                      moveOutResultObj.getCalculatedReading());
        mav.addObject("calculatedDifference",
                      moveOutResultObj.getCalculatedDifference());
        mav.addObject("meter", moveOutResultObj.getPreviousMeter());
        mav.addObject("errors", moveOutResultObj.getErrors());
        mav.addObject("deviceGroups", moveOutResultObj.getDeviceGroupsAdded());
        mav.addObject("submissionType", moveOutResultObj.getSubmissionType());
        mav.addObject("scheduled", moveOutResultObj.isScheduled());

        return mav;
    }

    /**
     * This function verifies that all the information was correctly entered on
     * the desired move in service
     * @param moveInFormObj
     * @param mav
     * @return
     */
    private boolean varifiedParametersMoveIn(MoveInFormObj moveInFormObj,
            ModelAndView mav) {

        boolean verified = true;
        List<String> validationErrors = new ArrayList<String>();

        // Checks for null values
        if (StringUtils.isBlank(moveInFormObj.getMeterName()) || StringUtils.isBlank(moveInFormObj.getMeterNumber()) || moveInFormObj.getMoveInDate() == null) {
            validationErrors.add("Please make sure to fill in all spaces other than the optional email field.");

            verified = false;
        } else {
            // Checks for invalid entries
            if (!moveInFormObj.getPreviousMeter()
                              .getName()
                              .equals(moveInFormObj.getMeterName())) {
                try {
                    meterDao.getForPaoName(moveInFormObj.getMeterName());

                    validationErrors.add("The meter name you have entered will create a duplicate name entry.  Please submit a different name.");
                    verified = false;
                } catch (NotFoundException nfe) {}
            }

            // Checks to see if a date is with in a 90 day time frame
            long ninetyDays = 1000L * 60L * 60L * 24L * 90L;
            Date moveInDate = new Date(moveInFormObj.getMoveInDate().getTime() + ninetyDays);
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
    private boolean varifiedParametersMoveOut(MoveOutFormObj moveOutFormObj,
            ModelAndView mav) {

        boolean verified = true;
        List<String> validationErrors = new ArrayList<String>();

        // Checks for null values
        if (moveOutFormObj.getMoveOutDate() == null) {
            validationErrors.add("Please enter or select a move out date.");

            verified = false;
        } else {

            // Checks to see if a date is with in a 90 day time frame
            long ninetyDays = 1000L * 60L * 60L * 24L * 90L;
            Date moveOutDate = new Date(moveOutFormObj.getMoveOutDate()
                                                      .getTime() + ninetyDays);
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
        // int deviceId = ServletRequestUtils.getIntParameter(request,
        // "deviceId");
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
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Required
    public void setMeterReadService(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
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
