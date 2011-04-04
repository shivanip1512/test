package com.cannontech.web.highBill;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.peakReport.model.PeakReportPeakType;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.common.device.peakReport.service.PeakReportService;
import com.cannontech.common.exception.InitiateLoadProfileRequestException;
import com.cannontech.common.exception.PeakSummaryReportRequestException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.FriendlyExceptionResolver;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LoadProfileService;
import com.cannontech.core.service.impl.LoadProfileServiceEmailCompletionCallbackImpl;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;

@CheckRoleProperty(YukonRoleProperty.HIGH_BILL_COMPLAINT)
public class HighBillController extends MultiActionController {

    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;
    private AttributeService attributeService = null;
    private PeakReportService peakReportService = null;
    private DateFormattingService dateFormattingService = null;
    private MeterDao meterDao = null;
    private PaoCommandAuthorizationService commandAuthorizationService = null;
    private LoadProfileService loadProfileService = null;
    private FriendlyExceptionResolver friendlyExceptionResolver = null;
    private SimpleReportService simpleReportService = null;
    private EmailService emailService = null;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao = null;
    private YukonUserDao yukonUserDao = null;
    private ContactDao contactDao = null;
    private RawPointHistoryDao rphDao = null;
    private TemplateProcessorFactory templateProcessorFactory = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;
    
    final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;
    
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception, ServletException {
        
        // mav
        ModelAndView mav = new ModelAndView("highBill.jsp");
        
        // BASICS
        //-------------------------------------------
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        mav.addObject("deviceId", String.valueOf(deviceId));
        Meter meter = meterDao.getForId(deviceId);
        
        // readable?
        boolean readable = commandAuthorizationService.isAuthorized(userContext.getYukonUser(), "getvalue lp peak", meter);
        mav.addObject("readable", readable);
        
        // point id
        addPointIdToMav(mav, meter);
        
        // new report or previous?
        boolean analyze = ServletRequestUtils.getBooleanParameter(request, "analyze", false);
        mav.addObject("analyze", analyze);
        
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
        
        // CREATE POINT IF NEEDED
        //-------------------------------------------
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        boolean createLPPoint = ServletRequestUtils.getBooleanParameter(request, "createLPPoint", false);
        if (createLPPoint) {
            attributeService.createPointForAttribute(device, BuiltInAttribute.LOAD_PROFILE);
        }
        boolean lmPointExists = attributeService.pointExistsForAttribute(device, BuiltInAttribute.LOAD_PROFILE);
        mav.addObject("lmPointExists", lmPointExists);
        
        // DATE RANGE
        //-------------------------------------------
        DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormattingService.DateFormatEnum.DATE, userContext);
        
        Date defaultStopDate = new Date();
        Date defaultStartDate = TimeUtil.addDays(defaultStopDate, -5);
        
        String startDateStr = ServletRequestUtils.getStringParameter(request, "getReportStartDate");
        String stopDateStr = ServletRequestUtils.getStringParameter(request, "getReportStopDate");
        
        Date startDate = null;
        Date stopDate = null;
        
        // start date
        if (startDateStr == null) {
            startDate = defaultStartDate;
            startDateStr = dateFormatter.format(startDate);
        }
        else {
            startDate = dateFormattingService.flexibleDateParser(startDateStr, userContext);
        }
        
        // stop date
        if (stopDateStr == null) {
            stopDate = defaultStopDate;
            stopDateStr = dateFormatter.format(stopDate);
        }
        else {
            stopDate = dateFormattingService.flexibleDateParser(stopDateStr, userContext);
        }
        
        mav.addObject("startDate", startDate);
        mav.addObject("stopDate", stopDate);
        mav.addObject("deviceName", meter.getName());
        
        
        // GATHER ARCHIVED RESULTS
        //-------------------------------------------
        PeakReportResult preResult = peakReportService.retrieveArchivedPeakReport(deviceId, PeakReportRunType.PRE, userContext);
        PeakReportResult postResult = peakReportService.retrieveArchivedPeakReport(deviceId, PeakReportRunType.POST, userContext);
        
        mav.addObject("preResult", preResult);
        mav.addObject("postResult", postResult);
        mav.addObject("preAvailableDaysAfterPeak", getAvailableDaysAfterPeak(preResult));
        mav.addObject("postAvailableDaysAfterPeak", getAvailableDaysAfterPeak(postResult));
        
        // remaining items are only required during "step 2" (analyze=true)
        if (analyze) {
            
            // FOR LOAD_PROFILE CHART
            //-------------------------------------------
            LitePoint point = attributeService.getPointForAttribute(device, BuiltInAttribute.LOAD_PROFILE);
            int pointId = point.getPointID();
            
            // applys to both
            mav.addObject("pointId", pointId);
            mav.addObject("converterType", ConverterType.RAW);
            mav.addObject("graphType", GraphType.LINE);
            
            String chartRange = ServletRequestUtils.getStringParameter(request, "chartRange", "PEAK");
            mav.addObject("chartRange", chartRange);
            
            // PRE CHART
            if (preResult != null && !preResult.isNoData()) {
                
                ChartStartStopDateHolder preChartStartStopDateHolder = getChartStartStopDate(preResult, chartRange, userContext);
                
                Date preChartStartDate = preChartStartStopDateHolder.getChartStartDate();
                Date preChartStopDate = preChartStartStopDateHolder.getChartStopDate();
                
                mav.addObject("preChartStartDate", preChartStartDate);
                mav.addObject("preChartStopDate", preChartStopDate);
                mav.addObject("preChartStartDateMillis", preChartStartDate.getTime());
                mav.addObject("preChartStopDateMillis", preChartStopDate.getTime());
                mav.addObject("preChartInterval", calcIntervalForPeriod(preChartStartDate, preChartStopDate));
                
                // POST CHART
                if (postResult != null && !postResult.isNoData()) {
                    
                    ChartStartStopDateHolder postChartStartStopDateHolder = getChartStartStopDate(postResult, chartRange, userContext);
                    
                    Date postChartStartDate = postChartStartStopDateHolder.getChartStartDate();
                    Date postChartStopDate = postChartStartStopDateHolder.getChartStopDate();
                    
                    mav.addObject("postChartStartDate", postChartStartDate);
                    mav.addObject("postChartStopDate", postChartStopDate);
                    mav.addObject("postChartStartDateMillis", postChartStartDate.getTime());
                    mav.addObject("postChartStopDateMillis", postChartStopDate.getTime());
                    mav.addObject("postChartInterval", calcIntervalForPeriod(postChartStartDate, postChartStopDate));
                    
                    // Get point values for each range to determine the min/max y values
                    double preMin = 0.0;
                    double postMin = 0.0;
                    double preMax = 0.0;
                    double postMax = 0.0;
                    
                    // pre min/max
                    List<PointValueHolder> prePointData = rphDao.getPointData(pointId, preChartStartDate, preChartStopDate);
                    for (PointValueHolder data : prePointData) {
                        double val = data.getValue();
                        if (val < preMin) {
                            preMin = val;
                        }
                        if (val > preMax) {
                            preMax = val;
                        }
                    }
                    
                    // post min/max
                    List<PointValueHolder> postPointData = rphDao.getPointData(pointId, postChartStartDate, postChartStopDate);
                    for (PointValueHolder data : postPointData) {
                        double val = data.getValue();
                        if (val < postMin) {
                            postMin = val;
                        }
                        if (val > postMax) {
                            postMax = val;
                        }
                    }
                    
                    Double min = Math.min(preMin, postMin);
                    Double max = Math.max(preMax, postMax);
                    mav.addObject("yMin", min.toString());
                    mav.addObject("yMax", max.toString());
                }
            }
        }
        
        return mav;
    }
    
    private ChartStartStopDateHolder getChartStartStopDate(PeakReportResult result, String chartRange, YukonUserContext userContext) {
        
        Calendar dateCal = dateFormattingService.getCalendar(userContext);
        
        Date chartStartDate = null;
        Date chartStopDate = null;
        
        if (chartRange.equals("PEAK")) {
            
            dateCal.setTime(result.getPeakStopDate()); 
            chartStartDate = DateUtils.truncate(dateCal, Calendar.DATE).getTime();
            chartStopDate = DateUtils.addDays(chartStartDate, 1);
        }
        else if (chartRange.equals("PEAKPLUSMINUS3")) {
            
            dateCal.setTime(result.getPeakStopDate()); 
            chartStartDate = DateUtils.truncate(dateCal, Calendar.DATE).getTime();
            chartStartDate = DateUtils.addDays(chartStartDate, -3);
            
            dateCal.setTime(result.getPeakStopDate()); 
            chartStopDate = DateUtils.truncate(dateCal, Calendar.DATE).getTime();
            chartStopDate = DateUtils.addDays(chartStopDate, 4);
        }
        else if (chartRange.equals("ENTIRE")) {
            
            dateCal.setTime(result.getRangeStartDate()); 
            chartStartDate = DateUtils.truncate(dateCal, Calendar.DATE).getTime();
            
            dateCal.setTime(result.getRangeStopDate()); 
            chartStopDate = DateUtils.truncate(dateCal, Calendar.DATE).getTime();
        }
        
        return new ChartStartStopDateHolder(chartStartDate, chartStopDate);
    }
    
    private class ChartStartStopDateHolder {
        
        public Date chartStartDate = null;
        public Date chartStopDate = null;
        
        public ChartStartStopDateHolder(Date chartStartDate, Date chartStopDate) {
            
            this.chartStartDate = chartStartDate;
            this.chartStopDate = chartStopDate;
        }
        
        public Date getChartStartDate() {
            return chartStartDate;
        }
        public Date getChartStopDate() {
            return chartStopDate;
        }
    }
    
    private ChartInterval calcIntervalForPeriod(Date startDate, Date stopDate) {
        
        long diff = Math.abs( startDate.getTime() - stopDate.getTime() );
        int dayDiff = (int)Math.floor(diff/1000/60/60/24);  

        // week and day are smaller intervals, everything else uses a day interval
        if(dayDiff > 7){
            return ChartInterval.HOUR;
        }
        else if(dayDiff <=7 && dayDiff > 1){
            return ChartInterval.FIFTEENMINUTE;
        }
        else{
            return ChartInterval.FIVEMINUTE;
        }
    }
    
    public ModelAndView getReport(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("meterReadErrors.jsp");
        
        // basics
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        
        // DATE RANGE - get requested
        //-------------------------------------------
        String startDateStr = ServletRequestUtils.getRequiredStringParameter(request, "getReportStartDate");
        String stopDateStr = ServletRequestUtils.getRequiredStringParameter(request, "getReportStopDate");
        
        // DATE RANGE - parse for errors first
        // if error occurs, return mav which will redirect back to view without setting requestReport=true so we will be at step 1
        //-------------------------------------------
        Date preCommandStartDate = null;
        Date preCommandStopDate = null;
        Date postCommandStartDate = null;
        Date postCommandStopDate = new Date();
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        try {
            preCommandStartDate = dateFormattingService.flexibleDateParser(startDateStr, userContext);
        } catch (ParseException e) {
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.highBill.errorMsgStart1", startDateStr);
            mav.addObject("errorMsg", errorMsg);
            return mav;
        }
        
        try {
            preCommandStopDate = dateFormattingService.flexibleDateParser(stopDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
        } catch (ParseException e) {
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.highBill.errorMsgStop1", stopDateStr);
            mav.addObject("errorMsg", errorMsg);
            return mav;
        }

        if (preCommandStopDate.before(preCommandStartDate) || preCommandStartDate.getTime() == preCommandStopDate.getTime()) {
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.highBill.errorMsgStart2");
            mav.addObject("errorMsg", errorMsg);
            return mav;
        }
        
        Date today = new Date();
        if (preCommandStartDate.after(today)) {
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.highBill.errorMsgStart3");
            mav.addObject("errorMsg", errorMsg);
            return mav;
        }
        
        if (preCommandStopDate.after(today)) {
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.highBill.errorMsgStop2");
            mav.addObject("errorMsg", errorMsg);
            return mav;
        }
        
        postCommandStartDate = DateUtils.addDays(preCommandStopDate, 1);
        
        // EXECUTE COMMANDS TO GET RESULTS
        //-------------------------------------------
        PeakReportResult preResult = null;
        PeakReportResult postResult = null;
        
        try {
            preResult = peakReportService.requestPeakReport(deviceId, PeakReportPeakType.DAY, PeakReportRunType.PRE, 1, preCommandStartDate, preCommandStopDate, true, userContext);
            if (getDaysBetween(postCommandStopDate, preCommandStopDate) > 0) {
                postResult = peakReportService.requestPeakReport(deviceId, PeakReportPeakType.DAY, PeakReportRunType.POST, 1, postCommandStartDate, postCommandStopDate, true, userContext);
            }
            
            // if the range we ran for only gives us a peak report and not a post report
            // remove any lingering post reports that may be archived for this device, cause thats just confusing
            if (preResult != null && postResult == null) {
                peakReportService.deleteArchivedPeakReport(deviceId, PeakReportRunType.POST);
            }
            
        } 
        catch (PeakSummaryReportRequestException e) {
            String readError = friendlyExceptionResolver.getFriendlyExceptionMessage(e);
            mav.addObject("errorMsg", readError);
            return mav;
        }
        
        // error results
        List<PeakReportResult> results = new ArrayList<PeakReportResult>();
        if (preResult != null && preResult.isNoData()) {
            results.add(preResult);
        }
        if (postResult != null && postResult.isNoData()) {
            results.add(postResult);
        }
        mav.addObject("results", results);
        
        
        return mav;
    }
    
    public ModelAndView initiateLoadProfile(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView(new JsonView());

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        String returnMsg = "";
        
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        LiteDeviceMeterNumber meterNum = deviceDao.getLiteDeviceMeterNumber(deviceId);
        
        try {
            
            String email = ServletRequestUtils.getRequiredStringParameter(request, "email");
            
            // DATE PARAMETERS
            String peakDateStr = ServletRequestUtils.getRequiredStringParameter(request, "peakDate");
            String startDateStr = ServletRequestUtils.getRequiredStringParameter(request, "startDate");
            String stopDateStr = ServletRequestUtils.getRequiredStringParameter(request, "stopDate");
            
            String beforeDaysStr = ServletRequestUtils.getRequiredStringParameter(request, "beforeDays");
            String afterDaysStr = ServletRequestUtils.getRequiredStringParameter(request, "afterDays");
            
            // PARSE DATES
            Date startDate = null;
            Date stopDate = null;
            
            // before peak
            if (beforeDaysStr.equalsIgnoreCase("ALL")) {
                startDate = dateFormattingService.flexibleDateParser(startDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
                startDate = DateUtils.truncate(startDate, Calendar.DATE);
            }
            else {
                startDate = dateFormattingService.flexibleDateParser(peakDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
                startDate = DateUtils.truncate(startDate, Calendar.DATE);
                startDate = DateUtils.addDays(startDate, -Integer.parseInt(beforeDaysStr));
            }
            
            // after peak
            if (afterDaysStr.equalsIgnoreCase("ALL")) {
                stopDate = dateFormattingService.flexibleDateParser(stopDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
                stopDate = DateUtils.truncate(stopDate, Calendar.DATE);
                stopDate = DateUtils.addDays(stopDate, 1);
            }
            else {
                stopDate = dateFormattingService.flexibleDateParser(peakDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
                stopDate = DateUtils.truncate(stopDate, Calendar.DATE);
                stopDate = DateUtils.addDays(stopDate, Integer.parseInt(afterDaysStr));
                stopDate = DateUtils.addDays(stopDate, 1);
            }
         
            // map of email elements
            Map<String, Object> msgData = new HashMap<String, Object>();
            
            msgData.put("email", email);
            msgData.put("formattedDeviceName", meterDao.getFormattedDeviceName(meterDao.getForId(device.getLiteID())));
            msgData.put("deviceName", device.getPaoName());
            msgData.put("meterNumber", meterNum.getMeterNumber());
            msgData.put("physAddress", device.getAddress());
            msgData.put("startDate", startDate);
            msgData.put("stopDate", stopDate);
            long numDays = (stopDate.getTime() - startDate.getTime()) / MS_IN_A_DAY;
            msgData.put("totalDays", Long.toString(numDays));
            msgData.put("channelName", BuiltInAttribute.LOAD_PROFILE.getDescription());
            
            // determine pointId in order to build report URL
            LitePoint litePoint = attributeService.getPointForAttribute(deviceDao.getYukonDevice(device), BuiltInAttribute.LOAD_PROFILE);
            
            Map<String, Object> inputValues = new HashMap<String, Object>();
            inputValues.put("startDate", startDate.getTime());
            inputValues.put("stopDate", stopDate.getTime());
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

            // completion callbacks
            LoadProfileServiceEmailCompletionCallbackImpl callback = new LoadProfileServiceEmailCompletionCallbackImpl(emailService, dateFormattingService, templateProcessorFactory, deviceErrorTranslatorDao);
            
            callback.setUserContext(userContext);
            callback.setEmail(email);
            callback.setMessageData(msgData);
            
            // will throw InitiateLoadProfileRequestException if connection problem
            loadProfileService.initiateLoadProfile(device,
                                                       1,
                                                       startDate,
                                                       stopDate,
                                                       callback,
                                                       userContext);
                
            returnMsg = "Profile Data Collection has been requested and will begin shortly.\n\nSee the Pending Profile Collections area for status updates.";

        } catch (ParseException e) {
            returnMsg = "Invalid Date: " + e.getMessage();
            
        } catch (InitiateLoadProfileRequestException e) {
            returnMsg = friendlyExceptionResolver.getFriendlyExceptionMessage(e);
        } catch (ServletRequestBindingException e) {
            returnMsg = e.getMessage();
        }

        mav.addObject("returnMsg", returnMsg);
        
        return mav;
    }

    /**
     * HELPER to get pointId, and set to mav
     * @param mav
     * @param deviceId
     */
    private void addPointIdToMav(ModelAndView mav, Meter meter) {
        
        LitePoint point = attributeService.getPointForAttribute(meter, BuiltInAttribute.LOAD_PROFILE);
        int pointId = point.getPointID();
        mav.addObject("pointId", pointId);
    }
    
    private List<String> getAvailableDaysAfterPeak(PeakReportResult result) {
        
        Date today = new Date();
        List<String> postAvailableDaysAfterPeak = new ArrayList<String>();
        if(result != null && !result.isNoData()) {
            
            // how many days after peak should be available to gather lp data? 0,1,2,3?
            Date peakDate = DateUtils.truncate(result.getPeakStopDate(), Calendar.DATE);
            postAvailableDaysAfterPeak.add("0");
            long daysBetween = getDaysBetween(today, peakDate) + 1;
            for (int d = 1; d < daysBetween && d <= 3; d++) {
                postAvailableDaysAfterPeak.add(Integer.valueOf(d).toString());
            }
            Date rangeStopDate = DateUtils.truncate(result.getRangeStopDate(), Calendar.DATE);
            if (rangeStopDate.compareTo(today) <= 0) {
                postAvailableDaysAfterPeak.add("All");
            }
        }
        
        return postAvailableDaysAfterPeak;
    }
    
    /**
     * HELPER to calculate days between two dates
     * @param date1
     * @param date2
     * @return
     */
    private long getDaysBetween(Date date1, Date date2) {

        long delta = date1.getTime() - date2.getTime();
        // Convert delta to days (86400000 milliseconds in a day)
        long days = delta / 86400000;

        return days;
    }
    
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Required
    public void setPeakReportService(PeakReportService peakReportService) {
        this.peakReportService = peakReportService;
    }

    @Required
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setCommandAuthorizationService(PaoCommandAuthorizationService commandAuthorizationService) {
        this.commandAuthorizationService = commandAuthorizationService;
    }

    @Required
    public void setLoadProfileService(LoadProfileService loadProfileService) {
        this.loadProfileService = loadProfileService;
    }
    
    @Required
    public void setFriendlyExceptionResolver(FriendlyExceptionResolver friendlyExceptionResolver) {
        this.friendlyExceptionResolver = friendlyExceptionResolver;
    }
    
    @Required
    public void setSimpleReportService(SimpleReportService simpleReportService) {
        this.simpleReportService = simpleReportService;
    }
    
    @Required
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @Required
    public void setDeviceErrorTranslatorDao(
            DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
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
    public void setRphDao(RawPointHistoryDao rphDao) {
        this.rphDao = rphDao;
    }
    
    @Required
    public void setTemplateProcessorFactory(
            TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }
    
    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}
