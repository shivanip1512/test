package com.cannontech.web.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.device.peakReport.model.PeakReportPeakType;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.common.device.peakReport.service.PeakReportService;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display point data in a trend
 */
public class ProfilePeakWidget extends WidgetControllerBase {

    private static final SimpleDateFormat COMMAND_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
//    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    private PeakReportService peakReportService = null;
    private DateFormattingService dateFormattingService = null;

    @Required
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Required
    public void setPeakReportService(PeakReportService peakReportService) {
        this.peakReportService = peakReportService;
    }

    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView();
        
        // user
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        // Add any previous results for the device into the mav
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        // pre
        PeakReportResult preResult = peakReportService.retrieveArchivedPeakReport(deviceId, PeakReportRunType.PRE, user);
        mav.addObject("preResult", preResult);
        
        // post
        PeakReportResult postResult = peakReportService.retrieveArchivedPeakReport(deviceId, PeakReportRunType.POST, user);
        mav.addObject("postResult", postResult);

        addHighlightedFields(request, mav);

        // Add the default report settings to the mav
        mav.addObject("reportType", "day");

        Date now = new Date();
        mav.addObject("startDate", COMMAND_FORMAT.format(TimeUtil.addDays(now, -5)));
        mav.addObject("stopDate", COMMAND_FORMAT.format(now));
        
        if(preResult != null) {
            Map<String,String> preMap = getParsedPeakResultValuesMap(preResult, user, deviceId, 1);
            mav.addObject("displayName", preMap.get("displayName"));
            mav.addObject("prePeriodStartDateDisplay",preMap.get("periodStartDateDisplay"));
            mav.addObject("prePeriodStopDateDisplay",preMap.get("periodStopDateDisplay"));
            mav.addObject("prePeakValue",preMap.get("peakValueStr"));
        }
        
        if(postResult != null) {
            Map<String,String> postMap = getParsedPeakResultValuesMap(postResult, user, deviceId, 1);
            mav.addObject("postPeriodStartDateDisplay",postMap.get("periodStartDateDisplay"));
            mav.addObject("postPeriodStopDateDisplay",postMap.get("periodStopDateDisplay"));
            mav.addObject("postPeakValue",postMap.get("peakValueStr"));
        }
        

        return mav;
    }

    public ModelAndView getReport(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView mav = new ModelAndView("profilePeakWidget/render.jsp");

        addHighlightedFields(request, mav);

        PeakReportResult prePeakResult = null;
        PeakReportResult postPeakResult = null;
        
        long preCommandDays = 0;
        long postCommandDays = 0;
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        // Get the user's timezone
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        // Get the report type for the commands
        String reportType = ServletRequestUtils.getRequiredStringParameter(request,
                                                                           "reportType");
        mav.addObject("reportType", reportType);

        // Calculate the times and days for each command
        String startDateStr = ServletRequestUtils.getRequiredStringParameter(request,
                                                                             "startDate");
        mav.addObject("startDate", startDateStr);
        String stopDateStr = ServletRequestUtils.getRequiredStringParameter(request, "stopDate");
        mav.addObject("stopDate", stopDateStr);

        Date preCommandStartDate = null;
        try {
            preCommandStartDate = dateFormattingService.flexibleDateParser(startDateStr,
                                                                           DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                                           user);
        } catch (ParseException e) {
            mav.addObject("errorMsg",
                          "Start date: " + startDateStr + " is not formatted correctly - example (mm/dd/yyyy).  Please try again.");
            return mav;
        }
        Date preCommandStopDate = null;
        try {
            preCommandStopDate = dateFormattingService.flexibleDateParser(stopDateStr,
                                                                          DateFormattingService.DateOnlyMode.START_OF_DAY,
                                                                          user);
        } catch (ParseException e) {
            mav.addObject("errorMsg",
                          "Stop date: " + stopDateStr + " is not formatted correctly - example (mm/dd/yyyy).  Please try again.");
            return mav;
        }

        if (preCommandStopDate.before(preCommandStartDate) || preCommandStartDate.getTime() == preCommandStopDate.getTime()) {
            mav.addObject("errorMsg", "Start date must be before stop date.  Please try again.");
            return mav;
        }
        
        Date today = new Date();
        if (preCommandStartDate.after(today)) {
            mav.addObject("errorMsg", "Start date must be before today.  Please try again.");
            return mav;
        }
        

        // Post command is from today back to the pre command stop date
        Date postCommandStopDate = new Date();

        // Build the commands to be executed
        PeakReportPeakType peakType = PeakReportPeakType.valueOf(reportType.toUpperCase());
        preCommandDays = (getDaysBetween(preCommandStopDate, preCommandStartDate) + 1);
        
        prePeakResult = peakReportService.requestPeakReport(deviceId, peakType, PeakReportRunType.PRE, 1, preCommandStartDate, preCommandStopDate, true, user);
                   
        
        // Build post command only if days are 1 or more
        postCommandDays = getDaysBetween(postCommandStopDate, preCommandStopDate);
        if (postCommandDays > 0) {
            postPeakResult = peakReportService.requestPeakReport(deviceId, peakType, PeakReportRunType.POST, 1, preCommandStopDate, postCommandStopDate, true, user);
        }
           
        mav.addObject("preCommandDays", preCommandDays);
        mav.addObject("postCommandDays", postCommandDays);
        
        mav.addObject("preResult", prePeakResult);
        mav.addObject("postResult", postPeakResult);
        
        if(prePeakResult != null) {
            Map<String,String> preMap = getParsedPeakResultValuesMap(prePeakResult, user, deviceId, 1);
            mav.addObject("displayName", preMap.get("displayName"));
            mav.addObject("prePeriodStartDateDisplay",preMap.get("periodStartDateDisplay"));
            mav.addObject("prePeriodStopDateDisplay",preMap.get("periodStopDateDisplay"));
            mav.addObject("prePeakValue",preMap.get("peakValueStr"));
        }
        
        if(postPeakResult != null) {
            Map<String,String> postMap = getParsedPeakResultValuesMap(postPeakResult, user, deviceId, 1);
            mav.addObject("postPeriodStartDateDisplay",postMap.get("periodStartDateDisplay"));
            mav.addObject("postPeriodStopDateDisplay",postMap.get("periodStopDateDisplay"));
            mav.addObject("postPeakValue",postMap.get("peakValueStr"));
        }

        return mav;
    }
    
    private Map<String, String> getParsedPeakResultValuesMap(PeakReportResult peakResult, LiteYukonUser user, int deviceId, int channel) {
        
        // init hash
        Map<String, String> parsedVals = new HashMap<String, String>();
        
        // special formatting of peakResult dates for display purposes
        String runDateDisplay = dateFormattingService.formatDate(peakResult.getRunDate(), DateFormattingService.DateFormatEnum.DATEHM, user);
        parsedVals.put("runDateDisplay", runDateDisplay);
        
        String periodStartDateDisplay = dateFormattingService.formatDate(peakResult.getRangeStartDate(), DateFormattingService.DateFormatEnum.DATE, user);
        parsedVals.put("periodStartDateDisplay", periodStartDateDisplay);
        
        String periodStopDateDisplay = dateFormattingService.formatDate(peakResult.getRangeStopDate(), DateFormattingService.DateFormatEnum.DATE, user);
        parsedVals.put("periodStopDateDisplay", periodStopDateDisplay);
        
        parsedVals.put("displayName", peakResult.getPeakType().getDisplayName());
        parsedVals.put("reportTypeDisplayName",peakResult.getPeakType().getReportTypeDisplayName());
        
        String peakValueStr = "";
        if(peakResult.getPeakType() == PeakReportPeakType.DAY) {
            peakValueStr = dateFormattingService.formatDate(peakResult.getPeakStopDate(), DateFormattingService.DateFormatEnum.DATE, user);
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
     * Helper method to add highlighted fields to the ModelAndView
     * @param request - Current request
     * @param mav - Model and view to add to
     * @throws ServletRequestBindingException
     */
    private void addHighlightedFields(HttpServletRequest request, ModelAndView mav)
            throws ServletRequestBindingException {

        // Get highlighted fields - if any
        String highlightData = WidgetParameterHelper.getStringParameter(request, "highlight");
        String[] highlightDataArray = highlightData.split(",");

        Map<String, Boolean> highlightDataMap = new HashMap<String, Boolean>();
        for (String data : highlightDataArray) {
            highlightDataMap.put(data, true);
        }

        mav.addObject("highlight", highlightDataMap);
    }

    private long getDaysBetween(Date date1, Date date2) {

        long delta = date1.getTime() - date2.getTime();
        // Convert delta to days (86400000 milliseconds in a day)
        long days = delta / 86400000;

        return days;
    }

}
