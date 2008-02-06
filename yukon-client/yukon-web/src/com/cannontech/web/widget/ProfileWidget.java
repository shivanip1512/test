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
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.MeterReadService;
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
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
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
    private MeterReadService meterReadService = null;
    
    private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mma");
    /*
     * Long load profile email message format NOTE: Outlook will sometimes strip
     * extra line breaks of its own accord. For some reason putting extra spaces
     * in front of the line breaks seems to prevent this. Not sure about other
     * email clients.
     */

    final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;
    
    private String calcIntervalStr(int secs) {
        
        String iStr = "";
        
        int hrs = 0;
        int mins = secs / 60;
        
        if (mins >= 60) {
            hrs = mins / 60;
            mins = mins % 60;
        }
        
        if (hrs >= 1 ) {
            iStr += hrs + " hr ";
        }
       
        if (mins >= 1) {
            iStr += mins + " min";
        }
        
        return iStr;
    }

    private List<Map<String, Object>> getAvailableChannelInfo(int deviceId) {
        
        // get load profile
        DeviceLoadProfile deviceLoadProfile = toggleProfilingService.getDeviceLoadProfile(deviceId);
        Meter meter = meterDao.getForId(deviceId);
        
        // ALL Channel Names / Attributes
        // - for all possible channels
        Integer channelNums[] = {1, 4};
        Map<Integer, Boolean> channelProfilingOn = new HashMap<Integer, Boolean>();
        Map<Integer, List<Map<String, Object>>> channelJobInfos = new HashMap<Integer, List<Map<String, Object>>>();
        Map<Integer, String> channelDisplayNames = new HashMap<Integer, String>();
        Map<Integer, Attribute> channelAttributes = new HashMap<Integer, Attribute>();
        Map<Integer, String> channelProfileRates = new HashMap<Integer, String>();
        
        for (Integer channelNum : channelNums) {
            
            channelProfilingOn.put(channelNum, toggleProfilingService.getToggleValueForDevice(deviceId, channelNum));
            channelJobInfos.put(channelNum, toggleProfilingService.getToggleJobInfos(deviceId, channelNum));
            
            if (channelNum == 1) {
                channelDisplayNames.put(channelNum, "Load Profile");
                channelAttributes.put(channelNum, BuiltInAttribute.LOAD_PROFILE);
                channelProfileRates.put(channelNum, calcIntervalStr(deviceLoadProfile.getLoadProfileDemandRate()));
            }
            else if (channelNum == 4) {
                channelDisplayNames.put(channelNum, "Voltage Profile");
                channelAttributes.put(channelNum, BuiltInAttribute.VOLTAGE_PROFILE);
                channelProfileRates.put(channelNum, calcIntervalStr(deviceLoadProfile.getVoltageDmdRate()));
            }
        }
        
        
        // AVAILABLE channel infos
        // - only channels which are supported by this device
        // - this list of channels info is order by channel number
        List<Map<String, Object>> availableChannels = new ArrayList<Map<String, Object>>();
        for(Integer channelNum : channelNums){
            
            if(attributeService.isAttributeSupported(meter, channelAttributes.get(channelNum))){
                
                Map<String, Object> channelInfo = new HashMap<String, Object>();
                channelInfo.put("channelProfilingOn", channelProfilingOn.get(channelNum));
                channelInfo.put("jobInfos", channelJobInfos.get(channelNum));
                channelInfo.put("channelNumber", channelNum.toString());
                channelInfo.put("channelDescription", channelDisplayNames.get(channelNum));
                channelInfo.put("channelProfileRate", channelProfileRates.get(channelNum));
                
                availableChannels.add(channelInfo);
            }
        }
        
        return availableChannels;
    }
    
    private void addFutureScheduleDateToMav(ModelAndView mav, YukonUserContext userContext) {
        
        mav.addObject("futureScheduleDate",
                      dateFormattingService.formatDate(DateUtils.addDays(new Date(),
                                                                         7),
                                                       DateFormattingService.DateFormatEnum.DATE,
                                                       userContext));
        List<Map<String, String>> hours = new ArrayList<Map<String, String>>();
        for (Integer i = 1; i <= 24; i++) {
            //i18n this needs to be fixed
            Map<String, String> h = new HashMap<String, String>();
            
            String dispval = "";
            if (i <= 12) {
                dispval= i + ":00 AM";
            }
            else {
                dispval = (i - 12) + ":00 PM";
            }
            
            h.put("display", StringUtils.leftPad(dispval,8));
            h.put("val", i.toString());
            hours.add(h);
        }
        mav.addObject("hours", hours);
        
    }
    
    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("profileWidget/render.jsp");

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

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
        
        // get info about each channels scanning status
        List<Map<String, Object>> availableChannels = getAvailableChannelInfo(deviceId);
        mav.addObject("availableChannels", availableChannels);
        
        // initialize past profile dates
        if (StringUtils.isBlank(startDateStr) && StringUtils.isBlank(stopDateStr)) {
            mav.addObject("startDateStr",
                          dateFormattingService.formatDate(DateUtils.addDays(new Date(),
                                                                             -5),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           userContext));
            mav.addObject("stopDateStr",
                          dateFormattingService.formatDate(new Date(),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           userContext));
        }

        // init future schedule date
        addFutureScheduleDateToMav(mav, userContext);
        
        // user email address
        LiteContact contact = yukonUserDao.getLiteContact(userContext.getYukonUser().getUserID());
        String email = "";
        if (contact != null) {
            String[] allEmailAddresses = contactDao.getAllEmailAddresses(contact.getContactID());
            if (allEmailAddresses.length > 0) {
                email = allEmailAddresses[0];
            }
        }
        mav.addObject("email", email);

        // Checks to see if the meter is readable
        Set<Attribute> existingAttributes = attributeService.getAllExistingAttributes(meter);
        boolean isReadable = meterReadService.isReadable(meter, existingAttributes, userContext.getYukonUser());
        mav.addObject("isReadable", isReadable);
        
        // pending requests
        List<Map<String, String>> pendingRequests = getPendingRequests(device, userContext);
        mav.addObject("pendingRequests", pendingRequests);
        
        mav.addObject("deviceId", deviceId);
        
        return mav;
    }

    
    
    
    public ModelAndView cancelLoadProfile(HttpServletRequest request,
            HttpServletResponse response) throws Exception {


        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        int requestId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                      "requestId");
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);

        loadProfileService.removePendingLongLoadProfileRequest(device, requestId, userContext);
        List<Map<String, String>> pendingRequests = getPendingRequests(device, userContext);
        
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

            YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);


            mav.addObject("startDateStr", startDateStr);
            mav.addObject("stopDateStr", stopDateStr);

            Date startDate = dateFormattingService.flexibleDateParser(startDateStr,
                                                                      DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                                      userContext);

            Date stopDate = dateFormattingService.flexibleDateParser(stopDateStr,
                                                                     DateFormattingService.DateOnlyMode.END_OF_DAY,
                                                                     userContext);

            boolean datesOk = false;

            if (startDate == null) {
                datesOk = false;
                dateErrorMessage = "Start Date Required";
            } else if (stopDate == null) {
                datesOk = false;
                dateErrorMessage = "Stop Date Required";
            } else {

                //TEM DateUtils.round() should do this for you
                String todayStr = dateFormattingService.formatDate(new Date(),
                                                                   DateFormattingService.DateFormatEnum.DATE,
                                                                   userContext);
                Date today = dateFormattingService.flexibleDateParser(todayStr,
                                                                      DateFormattingService.DateOnlyMode.END_OF_DAY,
                                                                      userContext);

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
                optionalAttributeDefaults.put("viewJsp", "MENU");
                
                String reportHtmlUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition", inputValues, optionalAttributeDefaults, "htmlView", true);
                String reportCsvUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition", inputValues, optionalAttributeDefaults, "csvView", true);
                String reportPdfUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition", inputValues, optionalAttributeDefaults, "pdfView", true);
                msgData.put("reportHtmlUrl", reportHtmlUrl);
                msgData.put("reportCsvUrl", reportCsvUrl);
                msgData.put("reportPdfUrl", reportPdfUrl);

                // completion callbacks
                LongLoadProfileServiceEmailCompletionCallbackImpl callback = 
                    new LongLoadProfileServiceEmailCompletionCallbackImpl(emailService, dateFormattingService, deviceErrorTranslatorDao);
                
                callback.setUserContext(userContext);
                callback.setEmail(email);
                callback.setMessageData(msgData);
                
                // will throw InitiateLoadProfileRequestException if connection problem
                loadProfileService.initiateLongLoadProfile(device,
                                                           channel,
                                                           startDate,
                                                           stopDate,
                                                           callback,
                                                           userContext);

                dateErrorMessage = "";
                mav.addObject("channel", channel);
                
                // reload pending request
                List<Map<String, String>> pendingRequests = getPendingRequests(device, userContext);
                mav.addObject("pendingRequests", pendingRequests);
            }

        } catch (ParseException e) {
            dateErrorMessage = "Invalid Date: " + e.getMessage();
        }

        mav.addObject("dateErrorMessage", dateErrorMessage);
        
        return mav;
    }

    public ModelAndView toggleProfiling(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String toggleErrorMsg = null;
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // get parameters
        int channelNum = WidgetParameterHelper.getRequiredIntParameter(request, "channelNum");
        boolean newToggleVal = WidgetParameterHelper.getRequiredBooleanParameter(request, "newToggleVal");
        
        String startRadio = WidgetParameterHelper.getStringParameter(request, "startRadio" + channelNum);
        String stopRadio = WidgetParameterHelper.getRequiredStringParameter(request, "stopRadio" + channelNum);
        
        String startDate = WidgetParameterHelper.getStringParameter(request, "startDate" + channelNum);
        String stopDate = WidgetParameterHelper.getRequiredStringParameter(request, "stopDate" + channelNum);
        
        Integer startHour = WidgetParameterHelper.getIntParameter(request, "startHour" + channelNum);
        Integer stopHour = WidgetParameterHelper.getRequiredIntParameter(request, "stopHour" + channelNum);
        
        // get device
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        
        // START
        // - start now or later
        // - with option to schedule stop later
        if (newToggleVal == true) {
            
            boolean scheduledStartOk = false;
            Date scheduledStartDate = null;
            Date scheduledStopDate = null;
            
            // start now
            if (startRadio.equalsIgnoreCase("now")) {
                
                // already scheduled to start? cancel it
                toggleProfilingService.disableScheduledJob(deviceId, channelNum, true);
                toggleProfilingService.toggleProfilingForDevice(deviceId, channelNum, true);
            }
            
            // start later
            else if (startRadio.equalsIgnoreCase("future")) {
                
                // validate scheduled start date
                Date today = DateUtils.round(new Date(), Calendar.MINUTE);
                try {
                    scheduledStartDate = dateFormattingService.flexibleDateParser(startDate, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
                    scheduledStartDate = DateUtils.addHours(scheduledStartDate, startHour);
                    scheduledStartDate = DateUtils.addMinutes(scheduledStartDate, 0);
                    if (scheduledStartDate == null) {
                        toggleErrorMsg = "Start Date Required";
                    } 
                    else if (scheduledStartDate.compareTo(today) <= 0) {
                        toggleErrorMsg = "Start Date Must Be After Today";
                    }
                } catch (ParseException e) {
                    toggleErrorMsg = "Invalid Start Date: " + e.getMessage();
                }
                
                // schedule it!, already scheduled? cancel it
                if (toggleErrorMsg == null) {
                    toggleProfilingService.disableScheduledJob(deviceId, channelNum, true);
                    toggleProfilingService.scheduleToggleProfilingForDevice(deviceId, channelNum, true, scheduledStartDate, userContext);
                    scheduledStartOk = true;
                }
            }
            
            // stop now?
            // - kill any scheduled stops
            if (stopRadio.equalsIgnoreCase("now")) {
                toggleProfilingService.disableScheduledJob(deviceId, channelNum, false);
            }
            
            // stop later?
            // - don't bother if there was an error scheduling the start date
            else if (toggleErrorMsg == null && stopRadio.equalsIgnoreCase("future")) {
                
                // validate schedule date
                Date today = DateUtils.round(new Date(), Calendar.MINUTE);
                try {
                    scheduledStopDate = dateFormattingService.flexibleDateParser(stopDate, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
                    scheduledStopDate = DateUtils.addHours(scheduledStopDate, stopHour);
                    scheduledStopDate = DateUtils.addMinutes(scheduledStopDate, 0);
                    if (scheduledStopDate == null) {
                        toggleErrorMsg = "Stop Date Required";
                    } 
                    else if (scheduledStopDate.compareTo(today) <= 0) {
                        toggleErrorMsg = "Stop Date Date Must Be After Today";
                    }
                } catch (ParseException e) {
                    toggleErrorMsg = "Invalid Stop Date: " + e.getMessage();
                }
                
                // was starting scheduled for later as well? make sure its before this scheduled stop date
                if (startRadio.equalsIgnoreCase("future") && scheduledStartOk && toggleErrorMsg == null) {
                    if (scheduledStopDate.compareTo(scheduledStartDate) <= 0) {
                        toggleErrorMsg = "Stop Date Date Must Be After Start Date";
                    }
                }
                
                
                // schedule it!, already scheduled? cancel it
                if (toggleErrorMsg == null) {
                    toggleProfilingService.disableScheduledJob(deviceId, channelNum, false);
                    toggleProfilingService.scheduleToggleProfilingForDevice(deviceId, channelNum, false, scheduledStopDate, userContext);
                }
                
            }
            
            
        }
           
        // STOP
        // - stop now or later
        // - no option to start
        else{
            
            Date scheduledStopDate = null;
            
            // stop now
            if (stopRadio.equalsIgnoreCase("now")) {
                
                // already scheduled to start? cancel it
                toggleProfilingService.disableScheduledJob(deviceId, channelNum, false);
                toggleProfilingService.toggleProfilingForDevice(deviceId, channelNum, false);
            }
            
            // stop later
            else if (stopRadio.equalsIgnoreCase("future")) {
                
                // validate schedule date
                Date today = DateUtils.round(new Date(), Calendar.MINUTE);
                try {
                    scheduledStopDate = dateFormattingService.flexibleDateParser(stopDate, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
                    scheduledStopDate = DateUtils.addHours(scheduledStopDate, stopHour);
                    scheduledStopDate = DateUtils.addMinutes(scheduledStopDate, 0);
                    if (scheduledStopDate == null) {
                        toggleErrorMsg = "Stop Date Required";
                    } 
                    else if (scheduledStopDate.compareTo(today) <= 0) {
                        toggleErrorMsg = "Stop Date Must Be After Today";
                    }
                } catch (ParseException e) {
                    toggleErrorMsg = "Invalid Stop Date: " + e.getMessage();
                }
                
                // schedule it!, already scheduled? cancel it
                if (toggleErrorMsg == null) {
                    toggleProfilingService.disableScheduledJob(deviceId, channelNum, false);
                    toggleProfilingService.scheduleToggleProfilingForDevice(deviceId, channelNum, false, scheduledStopDate, userContext);
                }
            }
            
        }
        
        
        
        // re-load page values into mav, add any error from scheduling
        ModelAndView mav = render(request, response);
        //ModelAndView mav = refreshChannelScanningInfo(request, response);
        mav.addObject("toggleErrorMsg", toggleErrorMsg);
        
        return mav;
     
    }
    
    public ModelAndView refreshChannelScanningInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("profileWidget/channelScanning.jsp");
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        // get info about each channels scanning status
        List<Map<String, Object>> availableChannels = getAvailableChannelInfo(deviceId);
        mav.addObject("availableChannels", availableChannels);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        addFutureScheduleDateToMav(mav, userContext);
        
        return mav;
    }
    
    private List<Map<String, String>> getPendingRequests(LiteYukonPAObject device,  YukonUserContext userContext) {
    
        //  pending past profiles
        List<Map<String, String>> pendingRequests = new ArrayList<Map<String, String>>();
        Collection<ProfileRequestInfo> loadProfileRequests = loadProfileService.getPendingLongLoadProfileRequests(device);
        for (ProfileRequestInfo info : loadProfileRequests) {
            HashMap<String, String> data = new HashMap<String, String>();
                
            data.put("email", info.runner.toString());
            data.put("from",
                     dateFormattingService.formatDate(info.from,
                                                      DateFormattingService.DateFormatEnum.DATE,
                                                      userContext));
            data.put("to",
                     dateFormattingService.formatDate(DateUtils.addDays(info.to,
                                                                        -1),
                                                                        DateFormattingService.DateFormatEnum.DATE,
                                                                        userContext));
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

    @Required
    public void setMeterReadService(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
    }

}
