package com.cannontech.web.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.chart.model.AttributeGraphType;
import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.peakReport.model.PeakReportPeakType;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.common.device.peakReport.service.PeakReportService;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display point data in a trend
 */
public class ProfilePeriodTrendWidget extends WidgetControllerBase {

    private static final SimpleDateFormat COMMAND_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    private PeakReportService peakReportService = null;
    private DateFormattingService dateFormattingService = null;
    private MeterDao meterDao = null;
    private PaoCommandAuthorizationService commandAuthorizationService = null;
    private AttributeService attributeService = null;
    
    // TREND
    private DeviceDao deviceDao = null;
    private Map<String, AttributeGraphType> supportedAttributeGraphMap = null;
    
    
    
    /**
     * Initial state of widget is to retrieve any archived results.
     * Display any archived results.
     * Set default report range to past five days
     */
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView();
        
        // BASICS
        //-------------------------------------------
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        
        // readable?
        boolean readable = commandAuthorizationService.isAuthorized(userContext.getYukonUser(), "getvalue lp peak", meter);
        mav.addObject("readable", readable);
        
        // point id
        addPointIdToMav(mav, meter);
        
        // DATE RANGE
        //-------------------------------------------
        Date stopDate = new Date();
        Date startDate = TimeUtil.addDays(stopDate, -5);
        String startDateStr = COMMAND_FORMAT.format(startDate);
        String stopDateStr = COMMAND_FORMAT.format(stopDate);
        mav.addObject("startDateDate", startDate);
        mav.addObject("stopDateDate", stopDate);
        mav.addObject("startDate", startDateStr);
        mav.addObject("stopDate", stopDateStr);
        
        // GATHER ARCHIVED RESULTS
        //-------------------------------------------
        PeakReportResult preResult = peakReportService.retrieveArchivedPeakReport(deviceId, PeakReportRunType.PRE, userContext.getYukonUser());
        PeakReportResult postResult = peakReportService.retrieveArchivedPeakReport(deviceId, PeakReportRunType.POST, userContext.getYukonUser());

        // PARSE RESULTS AND ADD TO MAV
        //-------------------------------------------
        addProfileResultsToMav(mav, deviceId, userContext, preResult, postResult);
        
        // TREND
        //-------------------------------------------
        addTrendToMav(request, mav, startDateStr, stopDateStr);
        
        return mav;
    }
    
    
    /**
     * When user request a new report be ran.
     * Parse dates for errors.
     * Run commands.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView getReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("profilePeriodTrendWidget/render.jsp");

        // BASICS
        //-------------------------------------------
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);

        // readable?
        boolean readable = commandAuthorizationService.isAuthorized(userContext.getYukonUser(), "getvalue lp peak", meter);
        mav.addObject("readable", readable);
        
        // point id
        addPointIdToMav(mav, meter);
        
        // DATE RANGE - get requested
        //-------------------------------------------
        String startDateStr = WidgetParameterHelper.getRequiredStringParameter(request, "startDate");
        String stopDateStr = WidgetParameterHelper.getRequiredStringParameter(request, "stopDate");
        mav.addObject("startDate", startDateStr);
        mav.addObject("stopDate", stopDateStr);

        // DATE RANGE - parse for errors first
        //-------------------------------------------
        Date preCommandStartDate = null;
        Date postCommandStopDate = new Date();
        
        try {
            preCommandStartDate = dateFormattingService.flexibleDateParser(startDateStr, userContext);
        } catch (ParseException e) {
            mav.addObject("errorMsg", "Start date: " + startDateStr + " is not formatted correctly - example (mm/dd/yyyy).  Please try again.");
            return mav;
        }
        
        Date preCommandStopDate = null;
        try {
            preCommandStopDate = dateFormattingService.flexibleDateParser(stopDateStr, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
        } catch (ParseException e) {
            mav.addObject("errorMsg", "Stop date: " + stopDateStr + " is not formatted correctly - example (mm/dd/yyyy).");
            return mav;
        }

        if (preCommandStopDate.before(preCommandStartDate) || preCommandStartDate.getTime() == preCommandStopDate.getTime()) {
            mav.addObject("errorMsg", "Start date must be before stop date.");
            return mav;
        }
        
        Date today = new Date();
        if (preCommandStartDate.after(today)) {
            mav.addObject("errorMsg", "Start date must be before today.");
            return mav;
        }
        
        if (preCommandStopDate.after(today)) {
            mav.addObject("errorMsg", "Stop date must on or before today.");
            return mav;
        }
        
        mav.addObject("startDateDate", preCommandStartDate);
        mav.addObject("stopDateDate", preCommandStopDate);
        
        // EXECUTE COMMANDS TO GET RESULTS
        //-------------------------------------------
        PeakReportResult preResult = null;
        PeakReportResult postResult = null;
        
        preResult = peakReportService.requestPeakReport(deviceId, PeakReportPeakType.DAY, PeakReportRunType.PRE, 1, preCommandStartDate, preCommandStopDate, true, userContext);
        if (getDaysBetween(postCommandStopDate, preCommandStopDate) > 0) {
            postResult = peakReportService.requestPeakReport(deviceId, PeakReportPeakType.DAY, PeakReportRunType.POST, 1, preCommandStopDate, postCommandStopDate, true, userContext);
        }
        
        // PARSE RESULTS AND ADD TO MAV
        //-------------------------------------------
        addProfileResultsToMav(mav, deviceId, userContext, preResult, postResult);
        
        // TREND
        //-------------------------------------------
        addTrendToMav(request, mav, startDateStr, stopDateStr);
        
        return mav;
    }
    
    /**
     * TREND
     * @param request
     * @param mav
     * @return
     * @throws Exception
     */
    private ModelAndView addTrendToMav(HttpServletRequest request, ModelAndView mav, String profileStartDateParam, String profileStopDateParam) throws Exception {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // DEVICE
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        YukonDevice device = deviceDao.getYukonDevice(deviceId);

        // ATTRIBUTE, ATTRIBUTE GRAPH TYPE
        // - if selected attribute does not exist, choose first valid
        String defaultAttribute = WidgetParameterHelper.getStringParameter(request, "defaultAttribute", "LOAD_PROFILE");
        String attributeStr = WidgetParameterHelper.getStringParameter(request, "attribute", defaultAttribute);
        BuiltInAttribute attribute = BuiltInAttribute.valueOf(attributeStr);
        
        List<AttributeGraphType> availableAttributeGraphs = new ArrayList<AttributeGraphType>();
        Set<Attribute> existingAttributes = attributeService.getAllExistingAttributes(device);
        
        AttributeGraphType attributeGraphType = null;
        
        for (AttributeGraphType agt : supportedAttributeGraphMap.values()) {
            if (existingAttributes.contains(agt.getAttribute())) {
                availableAttributeGraphs.add(agt);
                if (agt.getAttribute() == attribute){
                    attributeGraphType = agt;
                }
            }
        }
        
        if (attributeGraphType == null) {
            attributeGraphType = availableAttributeGraphs.get(0);
            attribute = attributeGraphType.getAttribute();
        }
        
        // GET PERIOD
        String period = WidgetParameterHelper.getStringParameter(request, "period", "NOPERIOD");
        ChartPeriod chartPeriod = ChartPeriod.valueOf(period);

        // SET START/END DATE
        Date startDate = new Date();
        Date stopDate = new Date();

        if (period.equalsIgnoreCase("YEAR")) { 
            startDate = DateUtils.addYears(startDate, -1); 
        }
        else if (period.equalsIgnoreCase("THREEMONTH")) {
            startDate = DateUtils.addMonths(startDate, -3);
        }
        else if (period.equalsIgnoreCase("MONTH")) {
            startDate = DateUtils.addMonths(startDate, -1);
        }
        else if (period.equalsIgnoreCase("WEEK")) {
            startDate = DateUtils.addWeeks(startDate, -1);
        }
        else if (period.equalsIgnoreCase("DAY")) {
            startDate = DateUtils.addDays(startDate, -1);
        }
        
        if (period.equalsIgnoreCase("NOPERIOD")) {
            
            String startDateParam = WidgetParameterHelper.getStringParameter(request, "startDateParam", profileStartDateParam);
            String stopDateParam = WidgetParameterHelper.getStringParameter(request, "stopDateParam", profileStopDateParam);
        
            if (startDateParam != null) {
                startDate = dateFormattingService.flexibleDateParser(startDateParam, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
            }
            if (stopDateParam != null) {
                stopDate = dateFormattingService.flexibleDateParser(stopDateParam, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
            }
            
        }

        // GET DATES STRINGS
        String startDateStr = dateFormattingService.formatDate(startDate, DateFormattingService.DateFormatEnum.DATE, userContext);
        String stopDateStr = dateFormattingService.formatDate(stopDate, DateFormattingService.DateFormatEnum.DATE, userContext);
        
        // CHART SYTLE (LINE/COLUMN)
        String defaultGraphType = attributeGraphType.getGraphType().toString();
        String graphTypeString = WidgetParameterHelper.getStringParameter(request, "graphType", defaultGraphType);
        GraphType graphType = GraphType.valueOf(graphTypeString);

        // TABULAR DATA LINK REQUIREMENTS 
        // - point id
        // - start/stop date as seconds
        // - view controller method name
        LitePoint point = attributeService.getPointForAttribute(device, attribute);
        int pointId = point.getPointID();
        
        Long startDateMillis = startDate.getTime();
        Long stopDateMillis = stopDate.getTime();
        
        //String tabularDataViewer = WidgetParameterHelper.getRequiredStringParameter(request, "tabularDataViewer");
        String tabularDataViewer = "hbcArchivedDataReport";


        // SET MAV
        mav.addObject("attributeGraphType", attributeGraphType);
        mav.addObject("availableAttributeGraphs", availableAttributeGraphs);
        mav.addObject("period", period);
        mav.addObject("startDateStr", startDateStr);
        mav.addObject("stopDateStr", stopDateStr);
        mav.addObject("graphType", graphType);

        if (!period.equals("NOPERIOD")) {
            mav.addObject("title",
                          "Previous " + chartPeriod.getPeriodLabel() + "'s " + 
                          attributeGraphType.getConverterType().getLabel() + " " + attribute.getKey());
        } else {
            mav.addObject("title",
                          attributeGraphType.getConverterType().getLabel() + " " + attribute.getKey() + 
                          ": " + startDateStr + " - " + stopDateStr);
        }
        
        mav.addObject("pointId", pointId);
        mav.addObject("startDateMillis", startDateMillis);
        mav.addObject("stopDateMillis", stopDateMillis);
        mav.addObject("tabularDataViewer", tabularDataViewer);
        

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
    
    /**
     * HELPER to call parse and add parsed results to mav.
     * @param mav
     * @param deviceId
     * @param userContext
     * @param preResult
     * @param postResult
     * @return
     */
    private void addProfileResultsToMav(ModelAndView mav, int deviceId, YukonUserContext userContext, PeakReportResult preResult, PeakReportResult postResult) {
        
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        mav.addObject("preResult", preResult);
        mav.addObject("postResult", postResult);
        
        
        List<String> preAvailableDaysAfterPeak = new ArrayList<String>();
        if(preResult != null && !preResult.isNoData()) {
            Map<String, Object> preMap = getParsedPeakResultValuesMap(preResult, userContext, deviceId, 1);
            mav.addObject("displayName", preMap.get("displayName"));
            mav.addObject("prePeriodStartDate",preMap.get("periodStartDate"));
            mav.addObject("prePeriodStopDate",preMap.get("periodStopDate"));
            mav.addObject("prePeriodStartDateDisplay",preMap.get("periodStartDateDisplay"));
            mav.addObject("prePeriodStopDateDisplay",preMap.get("periodStopDateDisplay"));
            mav.addObject("prePeakValue",preMap.get("peakValueStr"));
            
            // how many days after peak should be available to gather lp data? 0,1,2,3?
            Date peakDate = DateUtils.truncate(preResult.getPeakStopDate(), Calendar.DATE);
            preAvailableDaysAfterPeak.add("0");
            long daysBetween = getDaysBetween(today, peakDate) + 1;
            for (int d = 1; d < daysBetween && d <= 3; d++) {
                preAvailableDaysAfterPeak.add(Integer.valueOf(d).toString());
            }
            Date rangeStopDate = DateUtils.truncate(preResult.getRangeStopDate(), Calendar.DATE);
            if (rangeStopDate.compareTo(today) <= 0) {
                preAvailableDaysAfterPeak.add("All");
            }
        }
        mav.addObject("preAvailableDaysAfterPeak", preAvailableDaysAfterPeak);
        
        List<String> postAvailableDaysAfterPeak = new ArrayList<String>();
        if(postResult != null && !postResult.isNoData()) {
            Map<String, Object> postMap = getParsedPeakResultValuesMap(postResult, userContext, deviceId, 1);
            mav.addObject("postPeriodStartDate",postMap.get("periodStartDate"));
            mav.addObject("postPeriodStopDate",postMap.get("periodStopDate"));
            mav.addObject("postPeriodStartDateDisplay",postMap.get("periodStartDateDisplay"));
            mav.addObject("postPeriodStopDateDisplay",postMap.get("periodStopDateDisplay"));
            mav.addObject("postPeakValue",postMap.get("peakValueStr"));
            
            // how many days after peak should be available to gather lp data? 0,1,2,3?
            Date peakDate = DateUtils.truncate(postResult.getPeakStopDate(), Calendar.DATE);
            postAvailableDaysAfterPeak.add("0");
            long daysBetween = getDaysBetween(today, peakDate) + 1;
            for (int d = 1; d < daysBetween && d <= 3; d++) {
                postAvailableDaysAfterPeak.add(Integer.valueOf(d).toString());
            }
            Date rangeStopDate = DateUtils.truncate(preResult.getRangeStopDate(), Calendar.DATE);
            if (rangeStopDate.compareTo(today) <= 0) {
                postAvailableDaysAfterPeak.add("All");
            }
        }
        mav.addObject("postAvailableDaysAfterPeak", postAvailableDaysAfterPeak);
    }
    
    /**
     * HELPER to parse PeakReportResult objects.
     * @param peakResult
     * @param userContext
     * @param deviceId
     * @param channel
     * @return
     */
    private Map<String, Object> getParsedPeakResultValuesMap(PeakReportResult peakResult, YukonUserContext userContext, int deviceId, int channel) {
        
        // init hash
        Map<String, Object> parsedVals = new HashMap<String, Object>();
        
        // special formatting of peakResult dates for display purposes
        String runDateDisplay = dateFormattingService.formatDate(peakResult.getRunDate(), DateFormattingService.DateFormatEnum.DATEHM, userContext);
        parsedVals.put("runDateDisplay", runDateDisplay);
        
        String periodStartDateDisplay = dateFormattingService.formatDate(peakResult.getRangeStartDate(), DateFormattingService.DateFormatEnum.DATE, userContext);
        parsedVals.put("periodStartDate", peakResult.getRangeStartDate());
        parsedVals.put("periodStartDateDisplay", periodStartDateDisplay);
        
        String periodStopDateDisplay = dateFormattingService.formatDate(peakResult.getRangeStopDate(), DateFormattingService.DateFormatEnum.DATE, userContext);
        parsedVals.put("periodStopDate", peakResult.getRangeStopDate());
        parsedVals.put("periodStopDateDisplay", periodStopDateDisplay);
        
        parsedVals.put("displayName", peakResult.getPeakType().getDisplayName());
        parsedVals.put("reportTypeDisplayName",peakResult.getPeakType().getReportTypeDisplayName());
        
        String peakValueStr = "";
        if(peakResult.getPeakType() == PeakReportPeakType.DAY) {
            peakValueStr = dateFormattingService.formatDate(peakResult.getPeakStopDate(), DateFormattingService.DateFormatEnum.DATE, userContext);
        }
        else if(peakResult.getPeakType() == PeakReportPeakType.HOUR) {
            peakValueStr = new SimpleDateFormat("MM/dd/yy").format(peakResult.getPeakStopDate());
            peakValueStr += " ";
            peakValueStr += new SimpleDateFormat("Ka").format(peakResult.getPeakStartDate());
            peakValueStr += " - ";
            peakValueStr += new SimpleDateFormat("Ka").format(DateUtils.addMinutes(peakResult.getPeakStopDate(), 1));
        }
        else if(peakResult.getPeakType() == PeakReportPeakType.INTERVAL) {
            int interval = peakReportService.getChannelIntervalForDevice(deviceId,channel);
            peakValueStr = new SimpleDateFormat("MM/dd/yy").format(peakResult.getPeakStopDate());
            peakValueStr += " ";
            if(interval == 60){
                peakValueStr += new SimpleDateFormat("Ha").format(peakResult.getPeakStartDate());
                peakValueStr += " - ";
                peakValueStr += new SimpleDateFormat("Ha").format(DateUtils.addMinutes(peakResult.getPeakStopDate(), 1));
            }
            else{
                peakValueStr += new SimpleDateFormat("K:mma").format(peakResult.getPeakStartDate());
                peakValueStr += " - ";
                peakValueStr += new SimpleDateFormat("K:mma").format(DateUtils.addMinutes(peakResult.getPeakStopDate(), 1));
            }
        }
        parsedVals.put("peakValueStr", peakValueStr);
        
        return parsedVals;
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
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Required
    public void setPeakReportService(PeakReportService peakReportService) {
        this.peakReportService = peakReportService;
    }
    
    @Required
    public void setCommandAuthorizationService(
			PaoCommandAuthorizationService commandAuthorizationService) {
		this.commandAuthorizationService = commandAuthorizationService;
	}
    
    @Required
    public void setMeterDao(MeterDao meterDao) {
		this.meterDao = meterDao;
	}
    
    @Required
	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}

    // TREND
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Required
    public void setSupportedAttributeGraphSet(Set<AttributeGraphType> supportedAttributeGraphSet) {
        supportedAttributeGraphMap = new HashMap<String, AttributeGraphType>();
        for (AttributeGraphType agt : supportedAttributeGraphSet) {
            supportedAttributeGraphMap.put(agt.getLabel(), agt);
        }
    }
}
