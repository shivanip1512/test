package com.cannontech.web.widget;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.Days;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.toggleProfiling.service.ToggleProfilingService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LoadProfileService;
import com.cannontech.core.service.impl.LoadProfileServiceEmailCompletionCallbackImpl;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Widget used to display point data in a trend
 */
public class ProfileWidget extends WidgetControllerBase {

//    private LoadProfileService.EmailCompletionCallback emailCompletionCallback = null;
    private LoadProfileService loadProfileService = null;
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
    private TemplateProcessorFactory templateProcessorFactory = null;
    private RolePropertyDao rolePropertyDao;

    private final static Logger log = YukonLogManager.getLogger(ProfileWidget.class);
    /*
     * Long load profile email message format NOTE: Outlook will sometimes strip
     * extra line breaks of its own accord. For some reason putting extra spaces
     * in front of the line breaks seems to prevent this. Not sure about other
     * email clients.
     */

    final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;
    
    private enum ProfileAttributeChannelEnum {
        
        LOAD_PROFILE(BuiltInAttribute.LOAD_PROFILE, 1) {
            public int getRate(DeviceLoadProfile deviceLoadProfile) {
                return deviceLoadProfile.getLoadProfileDemandRate();
            }
        },
        PROFILE_CHANNEL_2(BuiltInAttribute.PROFILE_CHANNEL_2, 2) {
            public int getRate(DeviceLoadProfile deviceLoadProfile) {
                return deviceLoadProfile.getLoadProfileDemandRate();
            }
        },
        PROFILE_CHANNEL_3(BuiltInAttribute.PROFILE_CHANNEL_3, 3) {
            public int getRate(DeviceLoadProfile deviceLoadProfile) {
                return deviceLoadProfile.getLoadProfileDemandRate();
            }
        },
        VOLTAGE_PROFILE(BuiltInAttribute.VOLTAGE_PROFILE, 4) {
            public int getRate(DeviceLoadProfile deviceLoadProfile) {
                return deviceLoadProfile.getVoltageDmdRate();
            }
        };
        
        private BuiltInAttribute attribute;
        private Integer channel;
        
        private final static ImmutableMap<Integer, ProfileAttributeChannelEnum> lookupByChannel;
        
        static {
            try {
                Builder<Integer, ProfileAttributeChannelEnum> channelBuilder = ImmutableMap.builder();
                for (ProfileAttributeChannelEnum profileAttributeChannel: values()) {
                    channelBuilder.put(profileAttributeChannel.channel, profileAttributeChannel);
                }
                lookupByChannel = channelBuilder.build();
            } catch (IllegalArgumentException e) {
                log.warn("Caught exception while building lookup maps, look for a duplicate channel.", e);
                throw e;
            }
        }
        private ProfileAttributeChannelEnum(BuiltInAttribute attribute, Integer channel) {
            this.attribute = attribute;
            this.channel = channel;
        }

        public BuiltInAttribute getAttribute() {
            return attribute;
        }

        public Integer getChannel() {
            return channel;
        }
        
        public int getRate(DeviceLoadProfile deviceLoadProfile) {
            return this.getRate(deviceLoadProfile);
        }
        
        /**
         * Looks up the ProfileAttributeChannelEnum based on its channel.
         * @param channel
         * @return
         * @throws IllegalArgumentException - if no match for channel
         */
        public static ProfileAttributeChannelEnum getForChannel(Integer channel) throws IllegalArgumentException {
            ProfileAttributeChannelEnum profileAttributeChannel = lookupByChannel.get(channel);
            Validate.notNull(profileAttributeChannel, Integer.toString(channel));
            return profileAttributeChannel;
        }
    }
    
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
        
        Set<Attribute> supportedProfileAttributes = getSupportedProfileAttributes(meter);
        List<Map<String, Object>> availableChannels = new ArrayList<Map<String, Object>>();
        for (ProfileAttributeChannelEnum attrChanEnum : ProfileAttributeChannelEnum.values()) {
            
            if (supportedProfileAttributes.contains(attrChanEnum.getAttribute())) {
                
                Map<String, Object> channelInfo = new HashMap<String, Object>();
                channelInfo.put("channelProfilingOn", toggleProfilingService.getToggleValueForDevice(deviceId, attrChanEnum.getChannel()));
                channelInfo.put("jobInfos", toggleProfilingService.getToggleJobInfos(deviceId, attrChanEnum.getChannel()));
                channelInfo.put("channelNumber", attrChanEnum.getChannel().toString());
                channelInfo.put("channelDescription", attrChanEnum.getAttribute().getDescription());
                channelInfo.put("channelProfileRate", calcIntervalStr(attrChanEnum.getRate(deviceLoadProfile)));
                
                availableChannels.add(channelInfo); 
            }
        }
       
        return availableChannels;
    }
    
    private void addFutureScheduleDateToMav(ModelAndView mav, YukonUserContext userContext) {
        
        mav.addObject("futureScheduleDate",
                      dateFormattingService.format(DateUtils.addDays(new Date(),
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
    
    private Set<Attribute> getSupportedProfileAttributes(Meter meter) {
        
        Set<Attribute> supportedProfileAttributes = new HashSet<Attribute>(attributeService.getAllExistingAttributes(meter));
        supportedProfileAttributes.retainAll(CtiUtilities.asSet(BuiltInAttribute.LOAD_PROFILE,
                                                                BuiltInAttribute.PROFILE_CHANNEL_2,
                                                                BuiltInAttribute.PROFILE_CHANNEL_3,
                                                                BuiltInAttribute.VOLTAGE_PROFILE));
        
        return supportedProfileAttributes;
    }
    
    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("profileWidget/render.jsp");

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        String startDateStr = WidgetParameterHelper.getStringParameter(request, "pastProfile_start", "");
        String stopDateStr = WidgetParameterHelper.getStringParameter(request, "pastProfile_stop", "");
       
        // get lite device, set name
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        Meter meter = meterDao.getForId(deviceId);
        
        // get info about each channels scanning status
        List<Map<String, Object>> availableChannels = getAvailableChannelInfo(deviceId);
        mav.addObject("availableChannels", availableChannels);
        
        // initialize past profile dates
        if (StringUtils.isBlank(startDateStr) && StringUtils.isBlank(stopDateStr)) {
            mav.addObject("startDateStr",
                          dateFormattingService.format(DateUtils.addDays(new Date(),
                                                                             -5),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           userContext));
            mav.addObject("stopDateStr",
                          dateFormattingService.format(new Date(),
                                                           DateFormattingService.DateFormatEnum.DATE,
                                                           userContext));
        }

        // init future schedule date
        addFutureScheduleDateToMav(mav, userContext);
        
        // email
        mav.addObject("email", getUserEmail(userContext));

        // pending requests
        List<Map<String, String>> pendingRequests = loadProfileService.getPendingRequests(device, userContext);
        mav.addObject("pendingRequests", pendingRequests);
        mav.addObject("deviceId", deviceId);
        
        return mav;
    }

    
    public ModelAndView initiateLoadProfile(HttpServletRequest request, HttpServletResponse response) throws Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.PROFILE_COLLECTION, userContext.getYukonUser());
        
        // init to basic refresh of render
        // after command runs, pending list will be re-got, and dates will be set to requested
        // and error requesting will be added to mav also
        ModelAndView mav = render(request, response);

        String errorMsg = "";

        String email = WidgetParameterHelper.getRequiredStringParameter(request, "email");
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        int channel = WidgetParameterHelper.getRequiredIntParameter(request, "channel");
        String startDateStr = WidgetParameterHelper.getStringParameter(request, "startDateStr", "");
        String stopDateStr = WidgetParameterHelper.getStringParameter(request, "stopDateStr", "");
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        LiteDeviceMeterNumber meterNum = deviceDao.getLiteDeviceMeterNumber(deviceId);
        
        try {
            mav.addObject("startDateStr", startDateStr);
            mav.addObject("stopDateStr", stopDateStr);

            LocalDate startDate = dateFormattingService.parseLocalDate(startDateStr, userContext);
            LocalDate stopDate = dateFormattingService.parseLocalDate(stopDateStr, userContext);
            
            if (startDate == null) {
                errorMsg = "Start Date Required";
            } else if (stopDate == null) {
                errorMsg = "Stop Date Required";
            } else {
                LocalDate today = new LocalDate(userContext.getJodaTimeZone());
                
                Instant startInstant = TimeUtil.toMidnightAtBeginningOfDay(startDate, userContext.getJodaTimeZone());
                Instant stopInstant = TimeUtil.toMidnightAtEndOfDay(stopDate, userContext.getJodaTimeZone());

                if (stopDate.isBefore(startDate)) {
                    errorMsg = "Start Date Must Be On Or Before Stop Date";
                } else if (stopDate.isAfter(today)) {
                    errorMsg = "Stop Date Must Be On Or Before Today";
                } else {
                    stopDate = stopDate.plusDays(1);  //move stop date to end of specified day
                    // map of email elements
                    Map<String, Object> msgData = new HashMap<String, Object>();
                    msgData.put("email", email);
                    msgData.put("formattedDeviceName", meterDao.getFormattedDeviceName(meterDao.getForId(device.getLiteID())));
                    msgData.put("deviceName", device.getPaoName());
                    msgData.put("meterNumber", meterNum.getMeterNumber());
                    msgData.put("physAddress", device.getAddress());
                    msgData.put("startDate", startDate.toDateTimeAtStartOfDay(userContext.getJodaTimeZone()).toDate());  //e-mail callback service expects type Date
                    msgData.put("stopDate", stopDate.toDateTimeAtStartOfDay(userContext.getJodaTimeZone()).toDate());
                    long numDays = Days.daysBetween(startDate, stopDate).getDays();
                    
                    msgData.put("totalDays", Long.toString(numDays));
                    
                    // determine pointId in order to build report URL
                    Attribute attribute = null;
                    String channelName = "";
                    
                    ProfileAttributeChannelEnum profileAttributeChannel = ProfileAttributeChannelEnum.getForChannel(channel);
                    attribute = profileAttributeChannel.getAttribute();
                    channelName = profileAttributeChannel.getAttribute().getDescription();
    
                    LitePoint litePoint = attributeService.getPointForAttribute(deviceDao.getYukonDevice(device), attribute);
                    msgData.put("channelName", channelName);
                    
                    Map<String, Object> inputValues = new HashMap<String, Object>();
                    inputValues.put("startDate", startInstant.getMillis());
                    inputValues.put("stopDate", stopInstant.getMillis());
                    inputValues.put("pointId", litePoint.getPointID());
                    
                    Map<String, String> optionalAttributeDefaults = new HashMap<String, String>();
                    optionalAttributeDefaults.put("module", "amr");
                    optionalAttributeDefaults.put("showMenu", "true");
                    optionalAttributeDefaults.put("menuSelection", "meters");
                    optionalAttributeDefaults.put("viewJsp", "MENU");
                    
                    String reportHtmlUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition", inputValues, optionalAttributeDefaults, "extView", true);
                    String reportCsvUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition", inputValues, optionalAttributeDefaults, "csvView", true);
                    String reportPdfUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition", inputValues, optionalAttributeDefaults, "pdfView", true);
                    msgData.put("reportHtmlUrl", reportHtmlUrl);
                    msgData.put("reportCsvUrl", reportCsvUrl);
                    msgData.put("reportPdfUrl", reportPdfUrl);
    
                    //completion callbacks
                    LoadProfileServiceEmailCompletionCallbackImpl callback = 
                        new LoadProfileServiceEmailCompletionCallbackImpl(emailService, dateFormattingService, templateProcessorFactory, deviceErrorTranslatorDao);
                    
                    callback.setUserContext(userContext);
                    callback.setEmail(email);
                    callback.setMessageData(msgData);
                    
                    // will throw InitiateLoadProfileRequestException if connection problem
                    loadProfileService.initiateLoadProfile(device,
                                                               channel,
                                                               startInstant.toDate(),
                                                               stopInstant.toDate(),
                                                               callback,
                                                               userContext);
    
                    errorMsg = "";
                    mav.addObject("channel", channel);
                
                }
            }
        } catch (ParseException e) {
            errorMsg = "Invalid Date: " + e.getMessage();
        }

        // RE-GET PENDING
        //-----------------------------------------------------------------------------
        List<Map<String, String>> pendingRequests = loadProfileService.getPendingRequests(device, userContext);
        mav.addObject("pendingRequests", pendingRequests);
        
        // ERRORS
        //-----------------------------------------------------------------------------
        mav.addObject("errorMsgRequest", errorMsg);
        
        return mav;
    }
    
    public ModelAndView toggleProfiling(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.PROFILE_COLLECTION_SCANNING, userContext.getYukonUser());
        
        String toggleErrorMsg = null;
        
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
            
            Date scheduledStartDate = null;
            Date scheduledStopDate = null;
            Date today = DateUtils.round(new Date(), Calendar.MINUTE);
            
            // validate scheduled start date
            if (startRadio.equalsIgnoreCase("future")) {
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
            }

            // validate scheduled stop date
            if(stopRadio.equalsIgnoreCase("future")) {
                try {
                    scheduledStopDate = dateFormattingService.flexibleDateParser(stopDate, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
                    scheduledStopDate = DateUtils.addHours(scheduledStopDate, stopHour);
                    scheduledStopDate = DateUtils.addMinutes(scheduledStopDate, 0);
                    if (scheduledStopDate == null) {
                        toggleErrorMsg = "Stop Date Required";
                    } else if (scheduledStopDate.compareTo(today) <= 0) {
                        toggleErrorMsg = "Stop Date Date Must Be After Today";
                    }
                } catch (ParseException e) {
                    toggleErrorMsg = "Invalid Stop Date: " + e.getMessage();
                }
            }
            
            // start now
            if (startRadio.equalsIgnoreCase("now")) {

                // already scheduled to start? cancel it
                if(toggleErrorMsg == null) {
                    toggleProfilingService.disableScheduledJob(deviceId, channelNum, true);
                    toggleProfilingService.toggleProfilingForDevice(deviceId, channelNum, true);
                }
            }
            
            // start later
            else if (startRadio.equalsIgnoreCase("future")) {
                
                // was starting scheduled for later as well? make sure its before this scheduled stop date
                if (stopRadio.equalsIgnoreCase("future") && toggleErrorMsg == null) {
                    
                    if (scheduledStopDate.compareTo(scheduledStartDate) <= 0) {
                        toggleErrorMsg = "Stop Date Date Must Be After Start Date";
                    }
                }
                
                // schedule it!, already scheduled? cancel it
                if (toggleErrorMsg == null) {
                    toggleProfilingService.disableScheduledJob(deviceId, channelNum, true);
                    toggleProfilingService.scheduleToggleProfilingForDevice(deviceId, channelNum, true, scheduledStartDate, userContext);
                }
            }
            
            // stop now?
            // - kill any scheduled stops
            if (stopRadio.equalsIgnoreCase("now")) {
                toggleProfilingService.disableScheduledJob(deviceId, channelNum, false);
            }
            
            // stop later?
            // - don't bother if there was an error scheduling the start date
            else if (stopRadio.equalsIgnoreCase("future")) {
                                
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

    public ModelAndView viewDailyUsageReport(HttpServletRequest request, HttpServletResponse response) throws Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // get pointId
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        SimpleDevice device = deviceDao.getYukonDeviceObjectById(deviceId);
        
        LitePoint point = attributeService.getPointForAttribute(device, BuiltInAttribute.LOAD_PROFILE);
        Integer pointId = point.getLiteID();
        
        // get date range
        String reportStartDateStr = ServletRequestUtils.getRequiredStringParameter(request, "reportStartDateStr");
        String reportStopDateStr = ServletRequestUtils.getRequiredStringParameter(request, "reportStopDateStr");
        
        // end date should be inclusive
        Date stopDate = dateFormattingService.flexibleDateParser(reportStopDateStr,
                                                                 DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                                 userContext);
        stopDate = DateUtils.truncate(stopDate, Calendar.DATE);
        stopDate = DateUtils.addDays(stopDate, 1);
        reportStopDateStr = dateFormattingService.format(stopDate, DateFormattingService.DateFormatEnum.DATE, userContext);
       
        // build query
        Map<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.put("def", "dailyUsageDefinition");
        propertiesMap.put("viewJsp", "MENU");
        propertiesMap.put("module", "amr");
        propertiesMap.put("menuSelection", "meters");
        propertiesMap.put("showMenu", "true");
        propertiesMap.put("pointId", pointId.toString());
        propertiesMap.put("startDate", reportStartDateStr);
        propertiesMap.put("stopDate", reportStopDateStr);
        String queryString = ServletUtil.buildSafeQueryStringFromMap(propertiesMap, true);
        
        String url = "/spring/reports/simple/extView?" + queryString;
        url = ServletUtil.createSafeUrl(request, url);
        
        // redirect
        ModelAndView mav = new ModelAndView("redirect:" + url);
        return mav;
    }
    
    private String getUserEmail(YukonUserContext userContext) {
        
        // user email address
        LiteContact contact = yukonUserDao.getLiteContact(userContext.getYukonUser().getUserID());
        String email = "";
        if (contact != null) {
            String[] allEmailAddresses = contactDao.getAllEmailAddresses(contact.getContactID());
            if (allEmailAddresses.length > 0) {
                email = allEmailAddresses[0];
            }
        }
        
        return email;
    }
    
 

    
    @Required
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Required
    public void setLoadProfileService(LoadProfileService loadProfileService) {
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

    @Autowired
    public void setTemplateProcessorFactory(TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
}
