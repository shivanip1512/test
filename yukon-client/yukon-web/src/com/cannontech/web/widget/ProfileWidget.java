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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.device.ProfileAttributeChannel;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.toggleProfiling.model.RfnVoltageProfile;
import com.cannontech.amr.toggleProfiling.service.ProfilingService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.LoadProfileService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.core.service.impl.LoadProfileServiceEmailCompletionCallbackImpl;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Widget used to display point data in a trend
 */
@Controller
@RequestMapping("/profileWidget/*")
public class ProfileWidget extends WidgetControllerBase {
    
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private LoadProfileService loadProfileService;
    @Autowired private EmailService emailService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private MeterDao meterDao;
    @Autowired private DurationFormattingService durationFormattingService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private AttributeService attributeService;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private ContactDao contactDao;
    @Autowired private SimpleReportService simpleReportService;
    @Autowired private ProfilingService profilingService;
    @Autowired private TemplateProcessorFactory templateProcessorFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private PaoDefinitionDao paoDefDao;

    
    @Autowired
    public ProfileWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        String checkRole = YukonRole.METERING.name();
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
    /*
     * Long load profile email message format NOTE: Outlook will sometimes strip
     * extra line breaks of its own accord. For some reason putting extra spaces
     * in front of the line breaks seems to prevent this. Not sure about other
     * email clients.
     */

    private String calcIntervalStr(Duration duration, YukonUserContext userContext) {
        if (duration == null || duration.getMillis() == 0) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            return messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.unknown");
        } 
        return durationFormattingService.formatDuration(duration, DurationFormat.DHMS_REDUCED, userContext);
    }

    private List<Map<String, Object>> getAvailableChannelInfo(int deviceId,
                                                              YukonUserContext userContext) {

        // get load profile
        DeviceLoadProfile deviceLoadProfile = null;
        YukonMeter meter = meterDao.getForId(deviceId);
        deviceLoadProfile = profilingService.getDeviceLoadProfile(deviceId);
     
        Set<Attribute> supportedProfileAttributes = getSupportedProfileAttributes(meter);
        
        List<Map<String, Object>> availableChannels = new ArrayList<Map<String, Object>>();
        for (ProfileAttributeChannel attrChanEnum : ProfileAttributeChannel.values()) {

            if (supportedProfileAttributes.contains(attrChanEnum.getAttribute())) {

                Map<String, Object> channelInfo = new HashMap<String, Object>();
                if (!meter.getPaoType().isRfMeter()) {
                    channelInfo.put("channelProfilingOn", profilingService.isProfilingOnNow(deviceId, attrChanEnum.getChannel()));
                    channelInfo.put("jobInfos", profilingService.getToggleJobInfos(deviceId, attrChanEnum.getChannel()));
                    channelInfo.put("channelNumber", Integer.toString(attrChanEnum.getChannel()));
                    channelInfo.put("channelDescription", messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(attrChanEnum.getAttribute().getMessage()));
                    channelInfo.put("channelProfileRate", calcIntervalStr(attrChanEnum.getRate(deviceLoadProfile), userContext));
                } 
                availableChannels.add(channelInfo);
            }
        }

        return availableChannels;
    }

    private List<Map<String, Object>> getAvailableChannelInfoRfn(int deviceId,
            YukonUserContext userContext) throws Exception {

        YukonMeter meter = meterDao.getForId(deviceId);

        Set<Attribute> supportedProfileAttributes = getSupportedProfileAttributes(meter);

        List<Map<String, Object>> availableChannels = new ArrayList<Map<String, Object>>();
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String intervalString = messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.unknown");
        
        for (ProfileAttributeChannel attrChanEnum : ProfileAttributeChannel.values()) {

            if (supportedProfileAttributes.contains(attrChanEnum.getAttribute())) {
                Map<String, Object> channelInfo = new HashMap<String, Object>();
                RfnVoltageProfile rfnVoltageProfile = loadProfileService.getRfnVoltageProfileDetails(deviceId);
                String channelProfileRate = calcIntervalStr(rfnVoltageProfile.getVoltageProfilingRate(),
                                                            userContext);
                boolean channelProfileRateKnown;
                if (intervalString.equals(channelProfileRate)) {
                    channelProfileRateKnown = false;
                } else {
                    channelProfileRateKnown = true;
                }
                channelInfo.put("channelProfilingStatus", rfnVoltageProfile.getProfilingStatus());
                channelInfo.put("channelStopDate", rfnVoltageProfile.getStopDate());
                channelInfo.put("channelNumber", attrChanEnum.getChannel());
                channelInfo.put("channelDescription",
                                messageSourceResolver.getMessageSourceAccessor(userContext)
                                                     .getMessage(attrChanEnum.getAttribute()
                                                                             .getMessage()));
                channelInfo.put("channelProfileRateKnown", channelProfileRateKnown);
                channelInfo.put("channelProfileRate", channelProfileRate);
                availableChannels.add(channelInfo);
            }
        }

        return availableChannels;
    }
    
    @RequestMapping("refreshChannelScanningInfoRfn")
    public ModelAndView getRfnChannelInfo(HttpServletRequest request, HttpServletResponse response,
            int deviceId, YukonUserContext userContext) throws Exception {
        loadProfileService.loadDynamicRfnVoltageProfileCache(deviceId);
        return refreshChannelScanningInfo(request, response);
    }
    
    private void addFutureScheduleDateToMav(ModelAndView mav, YukonUserContext userContext) {

        mav.addObject("futureScheduleDate", DateUtils.addDays(new Date(), 7));
        List<LocalTime> hours = Lists.newArrayList();
        LocalTime localTime = new LocalTime(0,0,0);
        for (Integer i = 1; i <= 24; i++) {
            hours.add(localTime);
            localTime = localTime.plusHours(1);
        }
        mav.addObject("hours", hours);
    }

    private Set<Attribute> getSupportedProfileAttributes(YukonMeter meter) {
        
        Set<Attribute> profileAttributes = new HashSet<Attribute>();
        if (paoDefDao.isTagSupported(meter.getPaoType(), PaoTag.VOLTAGE_PROFILE)) {
            profileAttributes.addAll(ProfileAttributeChannel.getVoltageProfileAttributes());
        }
        if (paoDefDao.isTagSupported(meter.getPaoType(), PaoTag.LOAD_PROFILE)) {
            profileAttributes.addAll(ProfileAttributeChannel.getLoadProfileAttributes());
        }
        
        return attributeService.getExistingAttributes(meter, profileAttributes);
    }

    @Override
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("profileWidget/render.jsp");

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");

        // get lite device, set name
        LiteYukonPAObject device = databaseCache.getAllPaosMap().get(deviceId);
        PaoType paoType = device.getPaoType();
        List<Map<String, Object>> availableChannels;
        // get info about each channels scanning status
        if (paoType.isRfMeter()) {
            availableChannels = getAvailableChannelInfoRfn(deviceId, userContext);
        } else {
            availableChannels = getAvailableChannelInfo(deviceId, userContext);
        }
        mav.addObject("availableChannels", availableChannels);

        // initialize daily usage report dates
        String dailyUsageReportStartDateStr =
            WidgetParameterHelper.getStringParameter(request, "dailyUsageStartDateStr", "");
        String dailyUsageReportStopDateStr =
            WidgetParameterHelper.getStringParameter(request, "dailyUsageStopDateStr", "");
        if (StringUtils.isBlank(dailyUsageReportStartDateStr)
            && StringUtils.isBlank(dailyUsageReportStopDateStr)) {
        	Date dailyUsageReportStartDate = DateUtils.addDays(new Date(), -5);
        	Date dailyUsageReportStopDate = new Date();

            mav.addObject("dailyUsageStartDate", dailyUsageReportStartDate);
            mav.addObject("dailyUsageStopDate", dailyUsageReportStopDate);
        }

        // initialize past profile dates
        String stopDateStr = WidgetParameterHelper.getStringParameter(request, "stopDateStr", "");
        String startDateStr = WidgetParameterHelper.getStringParameter(request, "startDateStr", "");
        // start date
    	Date startDate;
        try {
            startDate = dateFormattingService.parseLocalDate(startDateStr, userContext).toDate();
    	} catch (ParseException e){
    	    startDate = DateUtils.addDays(new Date(), -5);
    	}

    	mav.addObject("startDate", startDate);

        // stop date
        Date stopDate;
        try {
            stopDate = dateFormattingService.parseLocalDate(stopDateStr, userContext).toDate();
        } catch (ParseException e){
            stopDate = new Date();
        }

		mav.addObject("stopDate", stopDate);

        // init future schedule date
        addFutureScheduleDateToMav(mav, userContext);

        // email
        mav.addObject("email", getUserEmail(userContext));

        // pending requests
        List<Map<String, String>> pendingRequests = loadProfileService.getPendingRequests(device, userContext);
        mav.addObject("pendingRequests", pendingRequests);
        mav.addObject("deviceId", deviceId);
        mav.addObject("isRfn", paoType.isRfMeter());

        if (paoType.isRfMeter()) {
            DateTime minDate = DateTime.now().minus(Days.SEVEN);
            DateTime maxDate = DateTime.now();

            mav.addObject("minDate", minDate);
            mav.addObject("maxDate", maxDate);
        }
        return mav;
    }
    
    @RequestMapping(value = "initiateLoadProfile", method = RequestMethod.POST)
    public ModelAndView initiateLoadProfile(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.PROFILE_COLLECTION,
                                       userContext.getYukonUser());

        // init to basic refresh of render
        // after command runs, pending list will be re-got, and dates will be set to requested
        // and error requesting will be added to mav also
        ModelAndView mav = render(request, response);

        List<String> errorMsg = Lists.newArrayList();

        String email = WidgetParameterHelper.getRequiredStringParameter(request, "email");
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        int channel = WidgetParameterHelper.getRequiredIntParameter(request, "channel");
        String startDateStr = WidgetParameterHelper.getStringParameter(request, "startDateStr", "");
        String stopDateStr = WidgetParameterHelper.getStringParameter(request, "stopDateStr", "");

        LiteYukonPAObject device = databaseCache.getAllPaosMap().get(deviceId);
        LiteDeviceMeterNumber meterNum = deviceDao.getLiteDeviceMeterNumber(deviceId);

        errorMsg = validateDateRange(startDateStr, stopDateStr, userContext); // validate dates

        if (errorMsg.isEmpty()) {
            LocalDate startDate = dateFormattingService.parseLocalDate(startDateStr, userContext);
            LocalDate stopDate = dateFormattingService.parseLocalDate(stopDateStr, userContext);
            Instant startInstant = TimeUtil.toMidnightAtBeginningOfDay(startDate, userContext.getJodaTimeZone());
            Instant stopInstant = null; 
            if (Days.daysBetween(stopDate, new LocalDate()).getDays() == 0) {
                stopInstant = Instant.now();
            } else {
                stopInstant = TimeUtil.toMidnightAtEndOfDay(stopDate, userContext.getJodaTimeZone());
            }
            // map of email elements
            Map<String, Object> msgData = Maps.newHashMap();
            msgData.put("email", email);
            msgData.put("formattedDeviceName", meterDao.getFormattedDeviceName(meterDao.getForId(device.getLiteID())));
            msgData.put("deviceName", device.getPaoName());
            msgData.put("meterNumber", meterNum.getMeterNumber());
            if (device.getPaoType().isRfMeter()) {
                RfnMeter rfMeter = meterDao.getRfnMeterForId(deviceId);
                msgData.put("physAddress/serialNumber", rfMeter.getRfnIdentifier().getSensorSerialNumber());
            } else {
                msgData.put("physAddress/serialNumber", device.getAddress());
            }
            // e-mail callback service expects startDate & stopDate of type Date
            msgData.put("startDate", startDate.toDateTimeAtStartOfDay(userContext.getJodaTimeZone()).toDate()); 
            msgData.put("stopDate", stopDate.toDateTimeAtStartOfDay(userContext.getJodaTimeZone()).toDate());
            long numDays = Days.daysBetween(startDate, stopDate).getDays();

            msgData.put("totalDays", Long.toString(numDays));

            // determine pointId in order to build report URL
            Attribute attribute = null;
            String channelName = "";

            ProfileAttributeChannel profileAttributeChannel = ProfileAttributeChannel.getForChannel(channel);
            attribute = profileAttributeChannel.getAttribute();
            channelName = objectFormattingService.formatObjectAsString(profileAttributeChannel.getAttribute().getMessage(), userContext);

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

            String reportHtmlUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition",
                                                 inputValues, optionalAttributeDefaults, "extView", true);
            String reportCsvUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition",
                                                 inputValues, optionalAttributeDefaults, "csvView", true);
            String reportPdfUrl = simpleReportService.getReportUrl(request, "rawPointHistoryDefinition",
                                                 inputValues, optionalAttributeDefaults, "pdfView", true);
            msgData.put("reportHtmlUrl", reportHtmlUrl);
            msgData.put("reportCsvUrl", reportCsvUrl);
            msgData.put("reportPdfUrl", reportPdfUrl);
            // completion callbacks
            LoadProfileServiceEmailCompletionCallbackImpl callback = null;
            if (!StringUtils.isEmpty(email)) {
                callback = new LoadProfileServiceEmailCompletionCallbackImpl(emailService,
                                                                      dateFormattingService,
                                                                      templateProcessorFactory,
                                                                      deviceErrorTranslatorDao);
                callback.setEmail(email);
                callback.setMessageData(msgData);
                callback.setUserContext(userContext);
            }

            // will throw InitiateLoadProfileRequestException if connection problem
            loadProfileService.initiateLoadProfile(device, channel, startInstant.toDate(), 
                                                   stopInstant.toDate(), callback, userContext);
            mav.addObject("channel", channel);
        } else {
            // Errors found
            mav.addObject("startDateStr", startDateStr);
            mav.addObject("stopDateStr", stopDateStr);
            mav.addObject("errorMsgRequest", errorMsg);
        }

        // RE-GET PENDING
        // -----------------------------------------------------------------------------
        List<Map<String, String>> pendingRequests = loadProfileService.getPendingRequests(device, userContext);
        mav.addObject("pendingRequests", pendingRequests);

        return mav;
    }

    @RequestMapping("toggleProfiling")
    public ModelAndView toggleProfiling(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        YukonMeter device = meterDao.getForId(deviceId);
        PaoType paoType = device.getPaoType();
        
        if(paoType.isRfMeter()) {
            return toggleProfilingRfn(request, response); 
        } else {
            return toggleProfilingPlc(request, response);
        }
    }
    
    private ModelAndView toggleProfilingPlc(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.PROFILE_COLLECTION_SCANNING, userContext.getYukonUser());
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        String toggleErrorMsg = null;

        // get device
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        // get parameters
        int channelNum = WidgetParameterHelper.getRequiredIntParameter(request, "channelNum");
        boolean newToggleVal = WidgetParameterHelper.getRequiredBooleanParameter(request, "newToggleVal");

        String startRadio = WidgetParameterHelper.getStringParameter(request, "startRadio" + channelNum);
        String stopRadio = WidgetParameterHelper.getRequiredStringParameter(request, "stopRadio" + channelNum);

        String startDate = WidgetParameterHelper.getStringParameter(request, "startDate" + channelNum);
        String stopDate = WidgetParameterHelper.getRequiredStringParameter(request, "stopDate" + channelNum);

        String startTimeString = WidgetParameterHelper.getStringParameter(request, "startHour" + channelNum);
        String stopTimeString = WidgetParameterHelper.getStringParameter(request, "stopHour" + channelNum);

        LocalTime startTime = null;
        if (startTimeString != null) {
             startTime =  dateFormattingService.parseLocalTime(startTimeString, userContext);
        }
        LocalTime stopTime = null;
        if (stopTimeString != null) {
            stopTime = dateFormattingService.parseLocalTime(stopTimeString, userContext);
        }
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
                    scheduledStartDate = dateFormattingService.flexibleDateParser(startDate,
                                                DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                userContext);

                    if (scheduledStartDate == null) {
                        toggleErrorMsg = messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.startRequired");
                    } else if (scheduledStartDate.compareTo(today) <= 0) {
                        toggleErrorMsg = messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.startAfterToday", startDate);
                    } else {
                        scheduledStartDate = DateUtils.addHours(scheduledStartDate, startTime.getHourOfDay());
                        scheduledStartDate = DateUtils.addMinutes(scheduledStartDate, startTime.getMinuteOfHour());
                    }
                } catch (ParseException e) {
                    toggleErrorMsg = messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.startInvalid", e.getMessage());
                }
            }
            // validate scheduled stop date
            if (stopRadio.equalsIgnoreCase("future")) {
                try {
                    scheduledStopDate = dateFormattingService.flexibleDateParser(stopDate,
                                                DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                userContext);

                    if (scheduledStopDate == null) {
                        toggleErrorMsg = messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.stopRequired");
                    } else if (scheduledStopDate.compareTo(today) <= 0) {
                        toggleErrorMsg = messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.stopAfterToday", stopDate);
                    } else {
                        scheduledStopDate = DateUtils.addHours(scheduledStopDate, stopTime.getHourOfDay());
                        scheduledStopDate = DateUtils.addMinutes(scheduledStopDate, stopTime.getMinuteOfHour());
                    }
                } catch (ParseException e) {
                    toggleErrorMsg = messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.stopInvalid", e.getMessage());
                }
            }
            // start now
            if (startRadio.equalsIgnoreCase("now")) {
                // already scheduled to start? cancel it
                profilingService.disableScheduledStart(deviceId, channelNum);
                profilingService.startProfilingForDevice(deviceId, channelNum);
            }
            // start later
            else if (startRadio.equalsIgnoreCase("future") && toggleErrorMsg == null) {
                // was starting scheduled for later as well? make sure its before this scheduled
                // stop date
                if (stopRadio.equalsIgnoreCase("future") && scheduledStopDate.compareTo(scheduledStartDate) <= 0) {
                    toggleErrorMsg = messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.stopAfterStart", stopDate, startDate);
                }
                // schedule it!, already scheduled? cancel it
                if (toggleErrorMsg == null) {
                    profilingService.disableScheduledStart(deviceId, channelNum);
                    profilingService.scheduleStartProfilingForDevice(deviceId, channelNum,
                                                                           scheduledStartDate, userContext);
                }
            }
            // stop now?
            // - kill any scheduled stops
            if (stopRadio.equalsIgnoreCase("now")) {
                profilingService.disableScheduledStop(deviceId, channelNum);
            }
            // stop later?
            // - don't bother if there was an error scheduling the start date
            else if (stopRadio.equalsIgnoreCase("future") && toggleErrorMsg == null) {
                // schedule it!, already scheduled? cancel it
                profilingService.disableScheduledStop(deviceId, channelNum);
                profilingService.scheduleStopProfilingForDevice(deviceId, channelNum,
                                                                      scheduledStopDate, userContext);
            }
        }
        // STOP
        // - stop now or later
        // - no option to start
        else {
            Date scheduledStopDate = null;
            // stop now
            if (stopRadio.equalsIgnoreCase("now")) {
                // already scheduled to start? cancel it
                profilingService.disableScheduledStop(deviceId, channelNum);
                profilingService.stopProfilingForDevice(deviceId, channelNum);
            }
            // stop later
            else if (stopRadio.equalsIgnoreCase("future")) {
                // validate schedule date
                Date today = DateUtils.round(new Date(), Calendar.MINUTE);
                try {
                    scheduledStopDate = dateFormattingService.flexibleDateParser(stopDate,
                                                DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                userContext);
                    if (scheduledStopDate == null) {
                        toggleErrorMsg = messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.stopRequired");
                    } else if (scheduledStopDate.compareTo(today) <= 0) {
                        toggleErrorMsg = messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.stopAfterToday", stopDate);
                    } else {
                        scheduledStopDate = DateUtils.addHours(scheduledStopDate, stopTime.getHourOfDay());
                        scheduledStopDate = DateUtils.addMinutes(scheduledStopDate, stopTime.getMinuteOfHour());
                    }
                } catch (ParseException e) {
                    toggleErrorMsg = messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.stopInvalid", e.getMessage());
                }
                // schedule it!, already scheduled? cancel it
                if (toggleErrorMsg == null) {
                    profilingService.disableScheduledStop(deviceId, channelNum);
                    profilingService.scheduleStopProfilingForDevice(deviceId, channelNum,
                                                                          scheduledStopDate, userContext);
                }
            }
        }
        // re-load page values into mav, add any error from scheduling
        ModelAndView mav = render(request, response);
        mav.addObject("toggleErrorMsg", toggleErrorMsg);
        return mav;
    }
    
    private ModelAndView toggleProfilingRfn(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.PROFILE_COLLECTION_SCANNING,
                                       userContext.getYukonUser());

        // get device
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        boolean enableProfiling = WidgetParameterHelper.getRequiredBooleanParameter(request, "newToggleVal");
        
        if (enableProfiling) {
            loadProfileService.startVoltageProfilingForDevice(deviceId);
        } else {
            loadProfileService.stopVoltageProfilingForDevice(deviceId);
        }

        ModelAndView mav = render(request, response);
        return mav;
    }

    @RequestMapping("refreshChannelScanningInfo")
    public ModelAndView refreshChannelScanningInfo(HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("profileWidget/channelScanning.jsp");
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        YukonMeter meter = meterDao.getForId(deviceId);
        List<Map<String, Object>> availableChannels;
        // get info about each channels scanning status
        if(meter.getPaoType().isRfMeter()) {
             availableChannels = getAvailableChannelInfoRfn(deviceId, userContext);
        } else {
             availableChannels = getAvailableChannelInfo(deviceId, userContext);
        }
        mav.addObject("availableChannels", availableChannels);
        mav.addObject("isRfn", meter.getPaoType().isRfMeter());
        addFutureScheduleDateToMav(mav, userContext);

        return mav;
    }

    @RequestMapping(value = "viewDailyUsageReport", method = RequestMethod.POST)
    public ModelAndView viewDailyUsageReport(HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        ModelAndView mav = render(request, response);
        List<String> errorMessages = new ArrayList<String>();
        String reportStartDateStr = ServletRequestUtils.getRequiredStringParameter(request, "dailyUsageStartDate");
        String reportStopDateStr = ServletRequestUtils.getRequiredStringParameter(request, "dailyUsageStopDate");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        LitePoint point = null;
        try {
            point = attributeService.getPointForAttribute(device, BuiltInAttribute.LOAD_PROFILE);
        } catch (IllegalUseOfAttribute e) {
            YukonMeter meter = meterDao.getForId(device.getDeviceId());
            PaoType paoType = meter.getPaoType();
            errorMessages.add(messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.operationNotSupported",
                                                               paoType.getPaoTypeName()));
        }
        // validate dates
        errorMessages = validateDateRange(reportStartDateStr, reportStopDateStr, userContext);

        if (errorMessages.isEmpty()) {
            // end date should be inclusive
            LocalDate stopDate = dateFormattingService.parseLocalDate(reportStopDateStr, userContext);
            stopDate = stopDate.plusDays(1);
            reportStopDateStr = dateFormattingService.format(stopDate, DateFormattingService.DateFormatEnum.DATE, userContext);
            // build report query
            Map<String, String> propertiesMap = new HashMap<String, String>();
            propertiesMap.put("def", "dailyUsageDefinition");
            propertiesMap.put("viewJsp", "MENU");
            propertiesMap.put("module", "amr");
            propertiesMap.put("menuSelection", "meters");
            propertiesMap.put("showMenu", "true");
            propertiesMap.put("pointId", String.valueOf(point.getLiteID()));
            propertiesMap.put("startDate", reportStartDateStr);
            propertiesMap.put("stopDate", reportStopDateStr);

            String queryString = ServletUtil.buildSafeQueryStringFromMap(propertiesMap, true);
            String url = "/reports/simple/extView?" + queryString;
            url = ServletUtil.createSafeUrl(request, url);
            mav.addObject("reportQueryString", url);
        } else {
        	try {
        		LocalDate reportStartDate = dateFormattingService.parseLocalDate(reportStartDateStr, userContext);
        		mav.addObject("dailyUsageStartDate", reportStartDate);
            } catch (ParseException e) {
                errorMessages.add(messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.startInvalid",
                                                                   reportStartDateStr));
            }
            try {
                LocalDate reportStopDate = dateFormattingService.parseLocalDate(reportStopDateStr, userContext);
                mav.addObject("dailyUsageStopDate", reportStopDate);
            } catch (ParseException e) {
                errorMessages.add(messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.stopInvalid", 
                                                                   reportStopDateStr));
            }
            mav.addObject("errorMsgDailyUsage", errorMessages);
        }
        return mav;
    }

    private List<String> validateDateRange(String reportStartDateStr, String reportStopDateStr,
                                           YukonUserContext userContext)
            throws Exception {
        List<String> errorMessages = new ArrayList<String>();
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        if (StringUtils.isBlank(reportStartDateStr)) {
            errorMessages.add(messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.startRequired"));
        }
        if (StringUtils.isBlank(reportStopDateStr)) {
            errorMessages.add(messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.stopRequired"));
        }
        if (!StringUtils.isBlank(reportStartDateStr) && !StringUtils.isBlank(reportStopDateStr)) {
            LocalDate startDate = null;
            LocalDate stopDate = null;
            try {
                startDate = dateFormattingService.parseLocalDate(reportStartDateStr, userContext);
            } catch (ParseException e) {
                errorMessages.add(messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.startInvalid",
                                                                   reportStartDateStr));
            }
            try {
                stopDate = dateFormattingService.parseLocalDate(reportStopDateStr, userContext);
            } catch (ParseException e) {
                errorMessages.add(messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.stopInvalid", 
                                                                   reportStopDateStr));
            }
            if (startDate != null && stopDate != null) {
                LocalDate today = new LocalDate(userContext.getJodaTimeZone());
                if (stopDate.isBefore(startDate)) {
                    errorMessages.add(messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.startOnOrBeforeStop", reportStartDateStr));
                }
                if (stopDate.isAfter(today)) {
                    errorMessages.add(messageSourceAccessor.getMessage("yukon.web.widgets.profileWidget.stopOnOrBeforeToday", reportStopDateStr));
                }
            }
        }
        return errorMessages;
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
}
