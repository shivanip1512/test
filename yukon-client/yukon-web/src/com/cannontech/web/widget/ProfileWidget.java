package com.cannontech.web.widget;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.toggleProfiling.service.ToggleProfilingService;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.core.service.LongLoadProfileService.ProfileRequestInfo;
import com.cannontech.core.service.impl.LongLoadProfileServiceEmailCompletionCallbackImpl;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.tools.email.EmailService;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display point data in a trend
 */
public class ProfileWidget extends WidgetControllerBase {

//    private LongLoadProfileService.EmailCompletionCallback emailCompletionCallback = null;
    private LongLoadProfileService loadProfileService = null;
    private EmailService emailService = null;
    private PaoDao paoDao = null;
    private DeviceDao deviceDao = null;
    private MeterDao meterDao = null;
    private DateFormattingService dateFormattingService = null;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao = null;
    private AttributeService attributeService = null;
    private YukonUserDao yukonUserDao = null;
    private ContactDao contactDao = null;
    private SimpleReportService simpleReportService = null;
    private ToggleProfilingService toggleProfilingService = null;
    
    private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mma");
    /*
     * Long load profile email message format NOTE: Outlook will sometimes strip
     * extra line breaks of its own accord. For some reason putting extra spaces
     * in front of the line breaks seems to prevent this. Not sure about other
     * email clients.
     */

    final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;


    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("profileWidget/render.jsp");

        LiteYukonUser user = ServletUtil.getYukonUser(request);

        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        String startDateStr = WidgetParameterHelper.getStringParameter(request,
                                                                       "pastProfile_start",
                                                                       "");
        String stopDateStr = WidgetParameterHelper.getStringParameter(request,
                                                                    "pastProfile_stop",
                                                                    "");

        
       
        // get lite device, set name
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        Meter meter = meterDao.getForId(deviceId);
        
        // get load profile
        DeviceLoadProfile deviceLoadProfile = toggleProfilingService.getDeviceLoadProfile(deviceId);
        
        // ALL Channel Names / Attributes
        // - for all possible channels
        Integer channelNums[] = {1, 4};
        Map<Integer, String> channelDisplayNames = new HashMap<Integer, String>();
        Map<Integer, Attribute> channelAttributes = new HashMap<Integer, Attribute>();
        Map<Integer, Integer> channelProfileRates = new HashMap<Integer, Integer>();
        Map<Integer, Boolean> channelProfilingOn = new HashMap<Integer, Boolean>();
        Map<Integer, Map<String, Object>> channelJobInfo = new HashMap<Integer, Map<String, Object>>();
        for (Integer channelNum : channelNums) {
            if (channelNum == 1) {
                channelDisplayNames.put(channelNum, "Channel 1 (Usage)");
                channelAttributes.put(channelNum, BuiltInAttribute.LOAD_PROFILE);
                channelProfileRates.put(channelNum, deviceLoadProfile.getLoadProfileDemandRate() / 60);
                channelProfilingOn.put(channelNum, toggleProfilingService.getToggleValueForDevice(deviceId, channelNum));
                channelJobInfo.put(channelNum, toggleProfilingService.getToggleJobInfo(deviceId, channelNum));
            }
            else if (channelNum == 4) {
                channelDisplayNames.put(channelNum, "Channel 4 (Voltage)");
                channelAttributes.put(channelNum, BuiltInAttribute.VOLTAGE_PROFILE);
                channelProfileRates.put(channelNum, deviceLoadProfile.getVoltageDmdRate() / 60);
                channelProfilingOn.put(channelNum, toggleProfilingService.getToggleValueForDevice(deviceId, channelNum));
                channelJobInfo.put(channelNum, toggleProfilingService.getToggleJobInfo(deviceId, channelNum));
            }
        }
        
        
        // AVAILABLE channel infos
        // - only channels which are supported by this device
        // - this list of channels info is order by channel number
        List<Map<String, Object>> availableChannels = new ArrayList<Map<String, Object>>();
        for(Integer channelNum : channelNums){
            
            if(attributeService.isAttributeSupported(meter, channelAttributes.get(channelNum))){
                
                Map<String, Object> channelInfo = new HashMap<String, Object>();
                channelInfo.put("channelNumber", channelNum.toString());
                channelInfo.put("channelDescription", channelDisplayNames.get(channelNum));
                channelInfo.put("channelProfileRate", channelProfileRates.get(channelNum));
                channelInfo.put("channelProfilingOn", channelProfilingOn.get(channelNum));
                channelInfo.put("jobInfo", toggleProfilingService.getToggleJobInfo(deviceId, channelNum));
                availableChannels.add(channelInfo);
            }
        }
        mav.addObject("availableChannels", availableChannels);
        
        
        // initialize past profile dates
        if (StringUtils.isBlank(startDateStr) && StringUtils.isBlank(stopDateStr)) {
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

        
        // init furture schedule date
        mav.addObject("futureScheduleDate",
                      dateFormattingService.formatDate(DateUtils.addDays(new Date(),
                                                                         7),
                                                       DateFormattingService.DateFormatEnum.DATE,
                                                       user));
        List<String> hours = new ArrayList<String>();
        List<String> minutes = new ArrayList<String>();
        DecimalFormat df = new DecimalFormat("00");
        for (int i = 0; i <= 23; i++)
            hours.add(df.format(i));
        for (int i = 0; i <= 59; i++)
            minutes.add(df.format(i));
        mav.addObject("hours", hours);
        mav.addObject("minutes", minutes);
        
        // user email address
        LiteContact contact = yukonUserDao.getLiteContact(user.getUserID());
        String email = "";
        if (contact != null) {
            String[] allEmailAddresses = contactDao.getAllEmailAddresses(contact.getContactID());
            if (allEmailAddresses.length > 0) {
                email = allEmailAddresses[0];
            }
        }
        mav.addObject("email", email);
        
        // pending requests
        List<Map<String, String>> pendingRequests = getPendingRequests(device, user);
        mav.addObject("pendingRequests", pendingRequests);

        return mav;
    }

    
    
    
    public ModelAndView cancelLoadProfile(HttpServletRequest request,
            HttpServletResponse response) throws Exception {


        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        int requestId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                      "requestId");
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);

        loadProfileService.removePendingLongLoadProfileRequest(device, requestId, user);
        List<Map<String, String>> pendingRequests = getPendingRequests(device, user);
        
        ModelAndView mav = render(request, response);

        // reload pending request
        mav.addObject("pendingRequests", pendingRequests);
        
        return mav;

    }

    
    
    
    public ModelAndView initiateLoadProfile(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("profileWidget/ongoingProfiles.jsp");

        String dateErrorMessage = "Unknown Error";

        String email = WidgetParameterHelper.getRequiredStringParameter(request,
        "email");
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
        "deviceId");
        int channel = WidgetParameterHelper.getRequiredIntParameter(request,
        "channel");
        String startDateStr = WidgetParameterHelper.getStringParameter(request,
                                                                       "startDateStr",
        "");
        String stopDateStr = WidgetParameterHelper.getStringParameter(request,
                                                                      "stopDateStr",
        "");
        try {

            LiteYukonUser user = ServletUtil.getYukonUser(request);


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
                datesOk = false;
                dateErrorMessage = "Start Date Required";
            } else if (stopDate == null) {
                datesOk = false;
                dateErrorMessage = "Stop Date Required";
            } else {

                String todayStr = dateFormattingService.formatDate(new Date(),
                                                                   DateFormattingService.DateFormatEnum.DATE,
                                                                   user);
                Date today = dateFormattingService.flexibleDateParser(todayStr,
                                                                      DateFormattingService.DateOnlyMode.END_OF_DAY,
                                                                      user);

                if (startDate.after(stopDate)) {
                    datesOk = false;
                    dateErrorMessage = "Start Date Must Be Before Stop Date";
                } else if (stopDate.after(today)) {
                    datesOk = false;
                    dateErrorMessage = "Stop Date Must Be On Or Before Today";
                } else {
                    datesOk = true;
                }

            }

            if (datesOk) {

                LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
                LiteDeviceMeterNumber meterNum = deviceDao.getLiteDeviceMeterNumber(deviceId);

                // map of email elements
                Map<String, Object> msgData = new HashMap<String, Object>();
                msgData.put("email", email);
                msgData.put("formattedDeviceName", meterDao.getFormattedDeviceName(meterDao.getForId(device.getLiteID())));
                msgData.put("deviceName", device.getPaoName());
                msgData.put("meterNumber", meterNum.getMeterNumber());
                msgData.put("physAddress", device.getAddress());
                msgData.put("startDate", dateFormat.format(startDate));
                msgData.put("stopDate", dateFormat.format(stopDate));
                long numDays = (stopDate.getTime() - startDate.getTime()) / MS_IN_A_DAY;
                msgData.put("totalDays", Long.toString(numDays));
                
                // determine pointId in order to build report URL
                Attribute attribute = null;
                if(channel == 1){
                    attribute = BuiltInAttribute.LOAD_PROFILE;
                }
                else if(channel == 4) {
                    attribute = BuiltInAttribute.VOLTAGE_PROFILE;
                }
                LitePoint litePoint = attributeService.getPointForAttribute(deviceDao.getYukonDevice(device), attribute);
                
                Map<String, Object> inputValues = new HashMap<String, Object>();
                inputValues.put("startDate", startDate.getTime());
                inputValues.put("stopDate", stopDate.getTime());
                inputValues.put("pointId", litePoint.getPointID());
                
                Map<String, String> optionalAttributeDefaults = new HashMap<String, String>();
                optionalAttributeDefaults.put("module", "amr");
                optionalAttributeDefaults.put("showMenu", "true");
                optionalAttributeDefaults.put("menuSelection", "deviceselection");
                optionalAttributeDefaults.put("deviceId", ((Integer)deviceId).toString());
                
                String reportHtmlUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition", inputValues, optionalAttributeDefaults, "htmlView");
                String reportCsvUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition", inputValues, optionalAttributeDefaults, "csvView");
                String reportPdfUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition", inputValues, optionalAttributeDefaults, "pdfView");
                msgData.put("reportHtmlUrl", reportHtmlUrl);
                msgData.put("reportCsvUrl", reportCsvUrl);
                msgData.put("reportPdfUrl", reportPdfUrl);

                // completion callbacks
                LongLoadProfileServiceEmailCompletionCallbackImpl callback = 
                    new LongLoadProfileServiceEmailCompletionCallbackImpl(emailService, dateFormattingService, deviceErrorTranslatorDao);
                
                callback.setSuccessMessage(msgData);
                callback.setFailureMessage(msgData);
                callback.setCancelMessage(msgData);
                
                // will throw InitiateLoadProfileRequestException if connection problem
                loadProfileService.initiateLongLoadProfile(device,
                                                           channel,
                                                           startDate,
                                                           stopDate,
                                                           callback,
                                                           user);

                dateErrorMessage = "";
                mav.addObject("channel", channel);
                
                // reload pending request
                List<Map<String, String>> pendingRequests = getPendingRequests(device, user);
                mav.addObject("pendingRequests", pendingRequests);
            }

        } catch (ParseException e) {
            dateErrorMessage = "Unable to parse: " + e.getMessage();
        }

        mav.addObject("dateErrorMessage", dateErrorMessage);
        
        return mav;
    }

    public ModelAndView toggleProfiling(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String errorMsg = null;
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        // get parameters
        int channelNum = WidgetParameterHelper.getRequiredIntParameter(request, "channelNum");
        boolean newToggleVal = WidgetParameterHelper.getRequiredBooleanParameter(request, "newToggleVal");
        String scheduleType = WidgetParameterHelper.getRequiredStringParameter(request, "toggleRadio" + channelNum);
        String toggleOnDateStr = WidgetParameterHelper.getRequiredStringParameter(request, "toggleOnDate" + channelNum);
        int toggleOnHour = WidgetParameterHelper.getRequiredIntParameter(request, "toggleOnHour" + channelNum);
        int toggleOnMinute = WidgetParameterHelper.getRequiredIntParameter(request, "toggleOnMinute" + channelNum);
        
        // get device
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        // toggle now
        if (scheduleType.equalsIgnoreCase("now")) {
            
            // already scheduled? cancel it
            toggleProfilingService.disableScheduledJob(deviceId, channelNum);
            toggleProfilingService.toggleProfilingForDevice(deviceId, channelNum, newToggleVal);
        }
        
        // toggle later
        else if (scheduleType.equalsIgnoreCase("future")) {
            
            // validate schedule date
            Date toggleDate = null;
            Date today = DateUtils.round(new Date(), Calendar.MINUTE);
            try {
                toggleDate = dateFormattingService.flexibleDateParser(toggleOnDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, user);
                toggleDate = DateUtils.addHours(toggleDate, toggleOnHour);
                toggleDate = DateUtils.addMinutes(toggleDate, toggleOnMinute);
                if (toggleDate == null) {
                    errorMsg = "Future Date Required";
                } 
                else if (toggleDate.compareTo(today) <= 0) {
                    errorMsg = "Future Date Must Be After Today";
                }
            } catch (ParseException e) {
                errorMsg = "Unable To Parse Future Date: " + e.getMessage();
            }
            
            // schedule it!, already scheduled? cancel it
            if (errorMsg == null) {
                toggleProfilingService.disableScheduledJob(deviceId, channelNum);
                toggleProfilingService.scheduleToggleProfilingForDevice(deviceId, channelNum, newToggleVal, toggleDate, user);
            }
            
        }
        
        // re-load page values into mav, add any error from scheduling
        ModelAndView mav = render(request, response);
        mav.addObject("errorMsg", errorMsg);
        
        return mav;
     
    }
    
    
    private List<Map<String, String>> getPendingRequests(LiteYukonPAObject device,  LiteYukonUser user){
    
        //  pending past profiles
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
            data.put("requestId", Long.toString(info.request.getUserMessageID()));
            data.put("channel", ((Integer) info.channel).toString());
            data.put("userName", info.userName);
            data.put("percentDone", info.percentDone.toString());
            pendingRequests.add(data);
        }

        // loadProfileService.printSizeOfCollections(device.getLiteID());
        
        return pendingRequests;
        
    }
    
    
    public ModelAndView percentDoneProgressBarHTML(HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = new ModelAndView("profileWidget/progressBar.jsp");
        
        long requestId = WidgetParameterHelper.getLongParameter(request, "requestId", 0);
        Double percentDone = loadProfileService.calculatePercentDone(requestId);
        
        mav.addObject("percentDone", percentDone);
        
        if(percentDone != null){
            DecimalFormat df = new DecimalFormat("#.#");
            mav.addObject("requestId", requestId);
            mav.addObject("percentDone", df.format(percentDone));
        }
        else{
            mav.addObject("requestId", requestId);
            mav.addObject("percentDone", percentDone);
            mav.addObject("lastReturnMsg", loadProfileService.getLastReturnMsg(requestId));
        }
        
        return mav;
    }

    
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
    public DeviceDao getDeviceDao() {
        return deviceDao;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
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

    @Required
    public void setDeviceErrorTranslatorDao(
            DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
    }

    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
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
    public void setSimpleReportService(SimpleReportService simpleReportService) {
        this.simpleReportService = simpleReportService;
    }

    @Required
    public void setToggleProfilingService(
            ToggleProfilingService toggleProfilingService) {
        this.toggleProfilingService = toggleProfilingService;
    }
}
