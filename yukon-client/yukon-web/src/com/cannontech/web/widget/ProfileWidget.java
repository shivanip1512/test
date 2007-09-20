package com.cannontech.web.widget;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.common.util.TemplateProcessor;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.core.service.LongLoadProfileService.ProfileRequestInfo;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tools.email.DefaultEmailMessage;
import com.cannontech.tools.email.EmailCompletionCallback;
import com.cannontech.tools.email.EmailService;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display point data in a trend
 */
public class ProfileWidget extends WidgetControllerBase {

    private LongLoadProfileService loadProfileService = null;
    private EmailService emailService = null;
    private PaoDao paoDao = null;
    private DeviceDao deviceDao = null;
    private YukonUserDao yukonUserDao = null;
    private ContactDao contactDao = null;
    private DateFormattingService dateFormattingService = null;
    private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mma");
    /*
     * Long load profile email message format NOTE: Outlook will sometimes strip
     * extra line breaks of its own accord. For some reason putting extra spaces
     * in front of the line breaks seems to prevent this. Not sure about other
     * email clients.
     */
    private final String lpNotificationFormat_Success = "{statusMsg}\n\n" + "Device Summary\n" + "Device Name: {deviceName}    \n" + "Meter Number: {meterNumber}    \n" + "Physical Address: {physAddress}\n\n" + "Request Range: {startDate} to {stopDate}    \n" + "Total Requested Days: {totalDays}\n\n" + "Data is now available online.";
    private final String lpNotificationFormat_Failure = "{statusMsg}\n\n" + "Device Summary\n" + "Device Name: {deviceName}    \n" + "Meter Number: {meterNumber}    \n" + "Physical Address: {physAddress}\n\n" + "Request Range: {startDate} to {stopDate}    \n" + "Total Requested Days: {totalDays}\n\n" + "Reason:\n\n";
    private final String lpNotificationFormat_Cancel = "{statusMsg}\n\n" + "Device Summary\n" + "Device Name: {deviceName}    \n" + "Meter Number: {meterNumber}    \n" + "Physical Address: {physAddress}\n\n" + "Request Range: {startDate} to {stopDate}    \n" + "Total Requested Days: {totalDays}";

    final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;

    @Required
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Required
    public void setLoadProfileService(LongLoadProfileService loadProfileService) {
        this.loadProfileService = loadProfileService;
    }

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Required
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    @Required
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    @Required
    public DeviceDao getDeviceDao() {
        return deviceDao;
    }

    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Required
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("profileWidget/render.jsp");

        LiteYukonUser user = ServletUtil.getYukonUser(request);

        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        String startDateStr = WidgetParameterHelper.getStringParameter(request,
                                                                       "pastProfile_start",
                                                                       "");
        String stopDateStr = ServletRequestUtils.getStringParameter(request,
                                                                    "pastProfile_stop",
                                                                    "");

        // initialize dates
        if (startDateStr == "" && stopDateStr == "") {
            mav.addObject("startDateStr",
                          dateFormattingService.formatDate(DateUtils.addDays(new Date(),
                                                                             -5),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           user));
            mav.addObject("stopDateStr",
                          dateFormattingService.formatDate(new Date(),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           user));
        }

        mav.addObject("chan1Interval", 15);
        mav.addObject("chan4Interval", 60);

        // device name
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        mav.addObject("subject", "Data collection for " + device.getPaoName());

        // Get pending requests
        Thread.sleep(1500); // loads too fast to show newly added requests in
        // pending if we dont chill for a bit

        List<Map<String, String>> pendingRequests = new ArrayList<Map<String, String>>();
        Collection<ProfileRequestInfo> loadProfileRequests = loadProfileService.getPendingLongLoadProfileRequests(device);
        for (ProfileRequestInfo info : loadProfileRequests) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("email", info.runner.toString());
            data.put("from",
                     dateFormattingService.formatDate(info.from,
                                                      DateFormattingService.DateFormatEnum.DATE,
                                                      user));
            data.put("to",
                     dateFormattingService.formatDate(DateUtils.addDays(info.to,
                                                                        -1),
                                                      DateFormattingService.DateFormatEnum.DATE,
                                                      user));
            data.put("command", info.request.getCommandString());
            data.put("requestId",
                     Long.toString(info.request.getUserMessageID()));
            data.put("channel", ((Integer) info.channel).toString());
            pendingRequests.add(data);
        }

        mav.addObject("pendingRequests", pendingRequests);

        return mav;
    }

    public ModelAndView cancelLoadProfile(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = render(request, response);

        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        int requestId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                      "requestId");
        String widgetId = WidgetParameterHelper.getRequiredStringParameter(request,
                                                                           "widgetId");

        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);

        loadProfileService.removePendingLongLoadProfileRequests(device,
                                                                requestId);
        
        mav.addObject("widgetId", widgetId);

        return mav;

    }

    public ModelAndView initiateLoadProfile(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = render(request, response);

        boolean success = false;
        String initiateMessage = "Unknown Error";

        try {

            LiteYukonUser user = ServletUtil.getYukonUser(request);
            TemplateProcessor tp = new SimpleTemplateProcessor();

            String email = WidgetParameterHelper.getRequiredStringParameter(request,
                                                                            "email");
            String baseSubject = WidgetParameterHelper.getRequiredStringParameter(request,
                                                                                  "subject");
            int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                         "deviceId");
            int channel = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                        "channel");
            String startDateStr = WidgetParameterHelper.getStringParameter(request,
                                                                           "startDateStr",
                                                                           "");
            String stopDateStr = ServletRequestUtils.getStringParameter(request,
                                                                        "stopDateStr",
                                                                        "");

            mav.addObject("startDateStr", startDateStr);
            mav.addObject("stopDateStr", stopDateStr);

            Date startDate = dateFormattingService.flexibleDateParser(startDateStr,
                                                                      DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                                      user);

            Date stopDate = dateFormattingService.flexibleDateParser(stopDateStr,
                                                                     DateFormattingService.DateOnlyMode.END_OF_DAY,
                                                                     user);

            boolean datesOk = false;

            if (startDate == null) {
                success = false;
                datesOk = false;
                initiateMessage = "Start Date Required";
            } else if (stopDate == null) {
                success = false;
                datesOk = false;
                initiateMessage = "Stop Date Required";
            } else {

                String todayStr = dateFormattingService.formatDate(new Date(),
                                                                   DateFormattingService.DateFormatEnum.DATE,
                                                                   user);
                Date today = dateFormattingService.flexibleDateParser(todayStr,
                                                                      DateFormattingService.DateOnlyMode.END_OF_DAY,
                                                                      user);

                if (startDate.after(stopDate)) {
                    success = false;
                    datesOk = false;
                    initiateMessage = "Start Date Must Be Before Stop Date";
                } else if (stopDate.compareTo(today) > 0) {
                    success = false;
                    datesOk = false;
                    initiateMessage = "Stop Date Must Be On Or Before Today";
                } else {
                    datesOk = true;
                }

            }

            if (datesOk) {

                LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
                LiteDeviceMeterNumber meterNum = deviceDao.getLiteDeviceMeterNumber(deviceId);

                // general body
                Map<String, Object> msgData = new HashMap<String, Object>();
                msgData.put("deviceName", device.getPaoName());
                msgData.put("meterNumber", meterNum.getMeterNumber());
                msgData.put("physAddress", device.getAddress());
                msgData.put("startDate", dateFormat.format(startDate));
                msgData.put("stopDate", dateFormat.format(stopDate));
                long numDays = (stopDate.getTime() - startDate.getTime()) / MS_IN_A_DAY;
                msgData.put("totalDays", Long.toString(numDays));

                // successEmailer
                DefaultEmailMessage successEmailer = new DefaultEmailMessage();
                successEmailer.setRecipient(email);
                successEmailer.setSubject(baseSubject + " (Completed)");
                msgData.put("statusMsg",
                            "Your long load profile request has completed.");
                successEmailer.setBody(tp.process(lpNotificationFormat_Success,
                                                  msgData));

                // failureEmailer
                DefaultEmailMessage failureEmailer = new DefaultEmailMessage();
                failureEmailer.setRecipient(email);
                failureEmailer.setSubject(baseSubject + " (Failed)");
                msgData.put("statusMsg",
                            "Your long load profile request has encountered an unknown error.");
                failureEmailer.setBody(tp.process(lpNotificationFormat_Failure,
                                                  msgData));

                // cancelEmailer
                DefaultEmailMessage cancelEmailer = new DefaultEmailMessage();
                cancelEmailer.setRecipient(email);
                cancelEmailer.setSubject(baseSubject + " (Canceled)");
                msgData.put("statusMsg",
                            "Your long load profile request was canceled.");
                cancelEmailer.setBody(tp.process(lpNotificationFormat_Cancel,
                                                 msgData));

                // completion callbacks
                EmailCompletionCallback callback = new EmailCompletionCallback(emailService);
                callback.setSuccessMessage(successEmailer);
                callback.setFailureMessage(failureEmailer);
                callback.setCancelMessage(cancelEmailer);

                loadProfileService.initiateLongLoadProfile(device,
                                                           channel,
                                                           startDate,
                                                           stopDate,
                                                           callback);

                success = true;
                initiateMessage = "";
                mav.addObject("channel", channel);
            }

        } catch (ServletRequestBindingException e) {
            success = false;
            initiateMessage = "Missing parameter: " + e.getMessage();
        } catch (ParseException e) {
            success = false;
            initiateMessage = "Unable to parse: " + e.getMessage();
        }

        mav.addObject("success", success);
        mav.addObject("initiateMessage", initiateMessage);

        return mav;
    }

}
