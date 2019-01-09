package com.cannontech.web.amr.meter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
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
import com.cannontech.common.i18n.ObjectFormattingService;
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

@Controller
@RequestMapping("/highBill/*")
@CheckRoleProperty(YukonRoleProperty.HIGH_BILL_COMPLAINT)
public class HighBillController {

    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoDao paoDao;
    @Autowired private AttributeService attributeService;
    @Autowired private PeakReportService peakReportService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private MeterDao meterDao;
    @Autowired private PaoCommandAuthorizationService commandAuthorizationService;
    @Autowired private LoadProfileService loadProfileService;
    @Autowired private FriendlyExceptionResolver friendlyExceptionResolver;
    @Autowired private SimpleReportService simpleReportService;
    @Autowired private EmailService emailService;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private ContactDao contactDao;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private TemplateProcessorFactory templateProcessorFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ObjectFormattingService objectFormattingService;

    final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;
    
    @RequestMapping("view")
    public String view(HttpServletRequest request, ModelMap model) throws ParseException, ServletRequestBindingException {
        
        // BASICS
        //-------------------------------------------
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        model.addAttribute("deviceId", String.valueOf(deviceId));
        PlcMeter meter = meterDao.getPlcMeterForId(deviceId);
        
        // readable?
        boolean readable = commandAuthorizationService.isAuthorized(userContext.getYukonUser(), "getvalue lp peak", meter);
        model.addAttribute("readable", readable);
        
        // point id
        addPointIdToModel(model, meter);
        
        // new report or previous?
        boolean analyze = ServletRequestUtils.getBooleanParameter(request, "analyze", false);
        model.addAttribute("analyze", analyze);
        
        // user email address
        LiteContact contact = yukonUserDao.getLiteContact(userContext.getYukonUser().getUserID());
        String email = "";
        if (contact != null) {
            String[] allEmailAddresses = contactDao.getAllEmailAddresses(contact.getContactID());
            if (allEmailAddresses.length > 0) {
                email = allEmailAddresses[0];
            }
        }
        model.addAttribute("email", email);
        
        // CREATE POINT IF NEEDED
        //-------------------------------------------
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        boolean createLPPoint = ServletRequestUtils.getBooleanParameter(request, "createLPPoint", false);
        if (createLPPoint) {
            attributeService.createPointForAttribute(device, BuiltInAttribute.LOAD_PROFILE);
        }
        boolean lmPointExists = attributeService.pointExistsForAttribute(device, BuiltInAttribute.LOAD_PROFILE);
        model.addAttribute("lmPointExists", lmPointExists);
        
        // DATE RANGE
        //-------------------------------------------
        DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormattingService.DateFormatEnum.DATE, userContext);
        
        Date defaultStopDate = TimeUtil.addDays(new Date(), -1);
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
        
        model.addAttribute("startDate", startDate);
        model.addAttribute("stopDate", stopDate);
        model.addAttribute("deviceName", meter.getName());
        
        
        // GATHER ARCHIVED RESULTS
        //-------------------------------------------
        PeakReportResult preResult = peakReportService.retrieveArchivedPeakReport(deviceId, PeakReportRunType.PRE, userContext);
        PeakReportResult postResult = peakReportService.retrieveArchivedPeakReport(deviceId, PeakReportRunType.POST, userContext);
        
        model.addAttribute("preResult", preResult);
        model.addAttribute("postResult", postResult);
        model.addAttribute("preAvailableDaysAfterPeak", getAvailableDaysAfterPeak(preResult));
        model.addAttribute("postAvailableDaysAfterPeak", getAvailableDaysAfterPeak(postResult));
        
        // remaining items are only required during "step 2" (analyze=true)
        if (analyze) {
            
            // FOR LOAD_PROFILE CHART
            //-------------------------------------------
            LitePoint point = attributeService.getPointForAttribute(device, BuiltInAttribute.LOAD_PROFILE);
            int pointId = point.getPointID();
            
            // applys to both
            model.addAttribute("pointId", pointId);
            model.addAttribute("converterType", ConverterType.RAW);
            model.addAttribute("graphType", GraphType.LINE);
            
            String chartRange = ServletRequestUtils.getStringParameter(request, "chartRange", "PEAK");
            model.addAttribute("chartRange", chartRange);
            
            // PRE CHART
            if (preResult != null && !preResult.isNoData()) {
                
                ChartStartStopDateHolder preChartStartStopDateHolder = getChartStartStopDate(preResult, chartRange, userContext);
                
                Date preChartStartDate = preChartStartStopDateHolder.getChartStartDate();
                Date preChartStopDate = preChartStartStopDateHolder.getChartStopDate();
                
                model.addAttribute("preChartStartDate", preChartStartDate);
                model.addAttribute("preChartStopDate", preChartStopDate);
                model.addAttribute("preChartStartDateMillis", preChartStartDate.getTime());
                model.addAttribute("preChartStopDateMillis", preChartStopDate.getTime());
                model.addAttribute("preChartInterval", calcIntervalForPeriod(preChartStartDate, preChartStopDate));
                
                // POST CHART
                if (postResult != null && !postResult.isNoData()) {
                    
                    ChartStartStopDateHolder postChartStartStopDateHolder = getChartStartStopDate(postResult, chartRange, userContext);
                    
                    Date postChartStartDate = postChartStartStopDateHolder.getChartStartDate();
                    Date postChartStopDate = postChartStartStopDateHolder.getChartStopDate();
                    
                    model.addAttribute("postChartStartDate", postChartStartDate);
                    model.addAttribute("postChartStopDate", postChartStopDate);
                    model.addAttribute("postChartStartDateMillis", postChartStartDate.getTime());
                    model.addAttribute("postChartStopDateMillis", postChartStopDate.getTime());
                    model.addAttribute("postChartInterval", calcIntervalForPeriod(postChartStartDate, postChartStopDate));
                    
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
                    model.addAttribute("yMin", min.toString());
                    model.addAttribute("yMax", max.toString());
                }
            }
        }
        
        return "highBill.jsp";
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
    
    @RequestMapping("getReport")
    public String getReport(HttpServletRequest request, ModelMap model) throws ServletRequestBindingException {
        
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
        Date postCommandStopDate = TimeUtil.addDays(new Date(), -1);
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        try {
            preCommandStartDate = dateFormattingService.flexibleDateParser(startDateStr, userContext);
        } catch (ParseException e) {
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.highBill.errorMsgStart1", startDateStr);
            model.addAttribute("errorMsg", errorMsg);
            return "meterReadErrors.jsp";
        }
        
        try {
            preCommandStopDate = dateFormattingService.flexibleDateParser(stopDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
        } catch (ParseException e) {
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.highBill.errorMsgStop1", stopDateStr);
            model.addAttribute("errorMsg", errorMsg);
            return "meterReadErrors.jsp";
        }

        if (preCommandStopDate.before(preCommandStartDate) || preCommandStartDate.getTime() == preCommandStopDate.getTime()) {
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.highBill.errorMsgStart2");
            model.addAttribute("errorMsg", errorMsg);
            return "meterReadErrors.jsp";
        }
        
        Date yesterday = TimeUtil.addDays(new Date(), -1);
        if (preCommandStartDate.after(yesterday)) {
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.highBill.errorMsgStart3");
            model.addAttribute("errorMsg", errorMsg);
            return "meterReadErrors.jsp";
        }
        
        if (preCommandStopDate.after(yesterday)) {
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.highBill.errorMsgStop2");
            model.addAttribute("errorMsg", errorMsg);
            return "meterReadErrors.jsp";
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
            model.addAttribute("errorMsg", readError);
            return "meterReadErrors.jsp";
        }
        
        // error results
        List<PeakReportResult> results = new ArrayList<PeakReportResult>();
        if (preResult != null && preResult.isNoData()) {
            results.add(preResult);
        }
        if (postResult != null && postResult.isNoData()) {
            results.add(postResult);
        }
        model.addAttribute("results", results);
        
        return "meterReadErrors.jsp";
    }
    
    @RequestMapping("initiateLoadProfile")
    public @ResponseBody Map<String, String> initiateLoadProfile(HttpServletRequest request, ModelMap model) throws Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        Map<String, String> json = new HashMap<>();
        
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
            if (device.getPaoType().isRfMeter()) {
                RfnMeter rfMeter = meterDao.getRfnMeterForId(deviceId);
                msgData.put("physAddress/serialNumber", rfMeter.getRfnIdentifier().getSensorSerialNumber());
            } else {
                msgData.put("physAddress/serialNumber", device.getAddress());
            }
            msgData.put("startDate", startDate);
            msgData.put("stopDate", stopDate);
            long numDays = (stopDate.getTime() - startDate.getTime()) / MS_IN_A_DAY;
            msgData.put("totalDays", Long.toString(numDays));
            String channelName = objectFormattingService.formatObjectAsString(BuiltInAttribute.LOAD_PROFILE.getMessage(), userContext);
            msgData.put("channelName", channelName);
            
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

        json.put("returnMsg", returnMsg);
        
        return json;
    }

    /**
     * HELPER to get pointId, and set to mav
     */
    private void addPointIdToModel(ModelMap model, PlcMeter meter) {
        
        LitePoint point = attributeService.getPointForAttribute(meter, BuiltInAttribute.LOAD_PROFILE);
        int pointId = point.getPointID();
        model.addAttribute("pointId", pointId);
    }
    
    private List<String> getAvailableDaysAfterPeak(PeakReportResult result) {
        
        Date yesterday = TimeUtil.addDays(new Date(), -1);
        List<String> postAvailableDaysAfterPeak = new ArrayList<String>();
        if(result != null && !result.isNoData()) {
            
            // how many days after peak should be available to gather lp data? 0,1,2,3?
            Date peakDate = DateUtils.truncate(result.getPeakStopDate(), Calendar.DATE);
            postAvailableDaysAfterPeak.add("0");
            long daysBetween = getDaysBetween(yesterday, peakDate) + 1;
            for (int d = 1; d < daysBetween && d <= 3; d++) {
                postAvailableDaysAfterPeak.add(Integer.valueOf(d).toString());
            }
            Date rangeStopDate = DateUtils.truncate(result.getRangeStopDate(), Calendar.DATE);
            if (rangeStopDate.compareTo(yesterday) <= 0) {
                postAvailableDaysAfterPeak.add("All");
            }
        }
        
        return postAvailableDaysAfterPeak;
    }
    
    /**
     * HELPER to calculate days between two dates
     */
    private long getDaysBetween(Date date1, Date date2) {

        long delta = date1.getTime() - date2.getTime();
        // Convert delta to days (86400000 milliseconds in a day)
        long days = delta / 86400000;

        return days;
    }
    
}