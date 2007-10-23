package com.cannontech.web.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.device.peakReport.dao.PeakReportDao;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.common.device.peakReport.service.PeakReportService;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display point data in a trend
 */
public class ProfilePeakWidget extends WidgetControllerBase {

    private static final SimpleDateFormat COMMAND_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
//    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    private PeakReportDao peakReportDao = null;
    private PeakReportService peakReportService = null;
    private CommandRequestExecutor commandRequestExecutor = null;
    private MeterDao meterDao = null;
    private DateFormattingService dateFormattingService = null;

    @Required
    public void setPeakReportDao(PeakReportDao peakReportDao) {
        this.peakReportDao = peakReportDao;
    }

    @Required
    public void setCommandRequestExecutor(CommandRequestExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
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
        PeakReportResult preResult = peakReportDao.getResult(deviceId, PeakReportRunType.PRE);
        if(preResult != null){
            int channel = preResult.getChannel();
            int interval = peakReportService.getChannelIntervalForDevice(deviceId, channel);
            String resultString = preResult.getResultString();
            preResult = peakReportService.parseResultString(preResult, resultString, interval, user);
        }
        mav.addObject("preResult", preResult);
        
        // post
        PeakReportResult postResult = peakReportDao.getResult(deviceId, PeakReportRunType.POST);
        if(postResult != null){
            int channel = postResult.getChannel();
            int interval = peakReportService.getChannelIntervalForDevice(deviceId, channel);
            String resultString = postResult.getResultString();
            postResult = peakReportService.parseResultString(postResult, resultString, interval, user);
        }
        mav.addObject("postResult", postResult);

        addHighlightedFields(request, mav);

        // Add the default report settings to the mav
        mav.addObject("reportType", "day");

        Date now = new Date();
        mav.addObject("startDate", COMMAND_FORMAT.format(TimeUtil.addDays(now, -5)));
        mav.addObject("stopDate", COMMAND_FORMAT.format(now));

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
        
        try {

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
            StringBuffer preCommand = new StringBuffer();
            preCommand.append("getvalue lp peak");
            preCommand.append(" " + reportType);
            preCommand.append(" channel 1");
            preCommand.append(" " + COMMAND_FORMAT.format(preCommandStopDate));
            preCommandDays = (getDaysBetween(preCommandStopDate, preCommandStartDate) + 1);
            preCommand.append(" " + preCommandDays);

            StringBuffer postCommand = new StringBuffer();
            // Build post command only if days are 1 or more
            postCommandDays = getDaysBetween(postCommandStopDate, preCommandStopDate);
            if (postCommandDays > 0) {
                postCommand.append("getvalue lp peak");
                postCommand.append(" " + reportType);
                postCommand.append(" channel 1");
                postCommand.append(" " + COMMAND_FORMAT.format(postCommandStopDate));
                postCommand.append(" " + postCommandDays);
            }

            int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");

            int interval = peakReportService.getChannelIntervalForDevice(deviceId, 1);
            
            Meter meter = meterDao.getForId(deviceId);
            
            
            
            // PRE
            //----------------------------------------------------------------------------------------
            
            // execute command to get profile peak summary results for user request
            CommandResultHolder preCommandResultHolder = commandRequestExecutor.execute(meter, preCommand.toString(), user);
            
            // setup basics of PeakReportResult
            prePeakResult = new PeakReportResult();
            prePeakResult.setDeviceId(deviceId);
            prePeakResult.setChannel(1);
            prePeakResult.setPeakType(reportType.toUpperCase());
            prePeakResult.setRunType(PeakReportRunType.PRE);
            prePeakResult.setRunDate(new Date());
            
            // get result string from command, set result string
            String preResultString = preCommandResultHolder.getLastResultString();
            prePeakResult.setResultString(preResultString);
            
            // device errors!
            if (preCommandResultHolder.isErrorsExist()) {
                
                StringBuffer sb = new StringBuffer();
                List<DeviceErrorDescription> errors = preCommandResultHolder.getErrors();
                for (DeviceErrorDescription ded : errors) {
                    sb.append(ded.toString() + "\n");
                }
                prePeakResult.setDeviceError(sb.toString());
                prePeakResult.setNoData(true);
                
            // results exist, parse result string into peakResult
            } else {
                prePeakResult = peakReportService.parseResultString(prePeakResult, preResultString, interval, user);
                
            }
            
            // persist the results
            if(!prePeakResult.isNoData()){
                peakReportDao.saveResult(prePeakResult);
            }
            
            
            // POST
            //----------------------------------------------------------------------------------------
            if (!StringUtils.isBlank(postCommand.toString())) {
                
               
                // execute command to get profile peak summary results for user request
                CommandResultHolder postCommandResultHolder = commandRequestExecutor.execute(meter, postCommand.toString(), user);
                
                // setup basics of PeakReportResult
                postPeakResult = new PeakReportResult();
                postPeakResult.setDeviceId(deviceId);
                postPeakResult.setChannel(1);
                postPeakResult.setPeakType(reportType.toUpperCase());
                postPeakResult.setRunType(PeakReportRunType.POST);
                postPeakResult.setRunDate(new Date());
                
                // get result string from command, set result string
                String postResultString = postCommandResultHolder.getLastResultString();
                postPeakResult.setResultString(postResultString);
                
                // device errors!
                if (postCommandResultHolder.isErrorsExist()) {
                    
                    StringBuffer sb = new StringBuffer();
                    List<DeviceErrorDescription> errors = postCommandResultHolder.getErrors();
                    for (DeviceErrorDescription ded : errors) {
                        sb.append(ded.toString() + "\n");
                    }
                    postPeakResult.setDeviceError(sb.toString());
                    postPeakResult.setNoData(true);
                    
                // results exist, parse result string into peakResult
                } else {
                    postPeakResult = peakReportService.parseResultString(postPeakResult, postResultString, interval, user);
                    
                }
                
                // persist the results
                if(!postPeakResult.isNoData()){
                    peakReportDao.saveResult(postPeakResult);
                }
                
            }

        } catch (CommandCompletionException e) {
            mav.addObject("errorMsg", e.getMessage());
        } catch (ConnectionException e) {
            mav.addObject("errorMsg", e.getMessage());
        }

        mav.addObject("preCommandDays", preCommandDays);
        mav.addObject("postCommandDays", postCommandDays);
        
        mav.addObject("preResult", prePeakResult);
        mav.addObject("postResult", postPeakResult);

        return mav;
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
