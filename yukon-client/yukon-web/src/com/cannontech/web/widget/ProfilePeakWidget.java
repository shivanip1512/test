package com.cannontech.web.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.device.commands.CommandRequest;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.YukonUserDao;
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
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    private YukonUserDao yukonUserDao = null;
    private CommandRequestExecutor commandRequestExecutor = null;

    // Result map is a cache of device peak profile reads - the map will save up
    // to 5 readings for up to 100 devices
    private Map<Integer, List<ProfilePeakResult>> resultMap = new LinkedHashMap<Integer, List<ProfilePeakResult>>() {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 100;
        }
    };

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    public void setCommandRequestExecutor(CommandRequestExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }

    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ModelAndView mav = new ModelAndView();

        boolean collectLPVisible = WidgetParameterHelper.getBooleanParameter(request,
                                                                             "collectLPVisible",
                                                                             false);
        mav.addObject("collectLPVisible", collectLPVisible);

        // Add any previous results for the device into the mav
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        List deviceResults = resultMap.get(deviceId);
        mav.addObject("deviceResults", deviceResults);

        // Add the default report settings to the mav
        mav.addObject("reportType", "day");
        mav.addObject("startDate", COMMAND_FORMAT.format(new Date()));
        mav.addObject("days", "5");

        return mav;
    }

    public ModelAndView getReport(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView mav = new ModelAndView("profilePeakWidget/render.jsp");

        boolean collectLPVisible = WidgetParameterHelper.getBooleanParameter(request,
                                                                             "collectLPVisible",
                                                                             false);
        mav.addObject("collectLPVisible", collectLPVisible);

        // Build the command to be executed
        StringBuffer command = new StringBuffer();
        command.append("getvalue lp peak");

        String reportType = WidgetParameterHelper.getStringParameter(request, "reportType", "day");
        mav.addObject("reportType", reportType);
        command.append(" " + reportType);

        command.append(" channel 1");

        // Get the number of days for the command
        int days = WidgetParameterHelper.getIntParameter(request, "days", 5);
        mav.addObject("days", String.valueOf(days));

        // Get the user's timezone to parse the start date
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        TimeZone timeZone = yukonUserDao.getUserTimeZone(user);

        // Get and parse the start date for the command
        String startDateStr = WidgetParameterHelper.getStringParameter(request,
                                                                       "startDate",
                                                                       COMMAND_FORMAT.format(new Date()));
        mav.addObject("startDate", startDateStr);

        // Get the id of the device to send the command for
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        try {
            Date startDate = TimeUtil.flexibleDateParser(startDateStr,
                                                         TimeUtil.NO_TIME_MODE.START_OF_DAY,
                                                         timeZone);
            Calendar cal = new GregorianCalendar();
            cal.setTime(startDate);
            cal.add(Calendar.DAY_OF_YEAR, days);
            Date stopDate = cal.getTime();
            command.append(" " + COMMAND_FORMAT.format(stopDate));
            command.append(" " + days);
            mav.addObject("command", command.toString());

            // Execute command to get profile peak summary results
            CommandRequest commandRequest = new CommandRequest();
            commandRequest.setCommand(command.toString());
            commandRequest.setDeviceId(deviceId);
            CommandResultHolder resultHolder = null;
            resultHolder = commandRequestExecutor.execute(commandRequest);

            // Parse result and add it to the device result list
            ProfilePeakResult result = parseResults(resultHolder);
            result.setRunDate(DISPLAY_FORMAT.format(new Date()));
            result.setFromDate(DISPLAY_FORMAT.format(startDate));
            result.setToDate(DISPLAY_FORMAT.format(stopDate));
            addResultToMap(deviceId, result);
        } catch (CommandCompletionException e) {
            mav.addObject("errorMsg",
                          e.getMessage());
        } catch (ConnectionException e) {
            mav.addObject("errorMsg",
                          e.getMessage());
        } catch (ParseException e) {
            mav.addObject("errorMsg",
                          "Date entered: '" + startDateStr + "' is not formatted correctly - example(mm/dd/yyyy).  Please try again.");
        }

        List<ProfilePeakResult> deviceResults = resultMap.get(deviceId);
        mav.addObject("deviceResults", deviceResults);

        return mav;
    }

    /**
     * Helper method to parse a result string into a ProfilePeakResult
     * @param resultHolder - Object containing result string
     * @return - ProfilePeakResult containing the result
     */
    private ProfilePeakResult parseResults(CommandResultHolder resultHolder) {

        ProfilePeakResult result = new ProfilePeakResult();

        String resultString = resultHolder.getLastResultString();

        if (resultString.startsWith("Peak timestamp")) {
            // No results
            result.setNoData(true);
        } else {
            // There are results

            String[] strings = resultString.split("\n");
            for (String string : strings) {

                if (string.startsWith("Report range: ")) {
                    result.setRange(string.replaceFirst("Report range: ", ""));
                } else if (string.startsWith("Peak day: ")) {
                    result.setPeakDate(string.replaceFirst("Peak day: ", ""));
                } else if (string.startsWith("Usage:  ")) {
                    result.setUsage(string.replaceFirst("Usage:  ", ""));
                } else if (string.startsWith("Demand: ")) {
                    result.setDemand(string.replaceFirst("Demand: ", ""));
                } else if (string.startsWith("Average daily usage over range: ")) {
                    result.setAverageDailyUsage(string.replaceFirst("Average daily usage over range: ",
                                                                    ""));
                } else if (string.startsWith("Total usage over range: ")) {
                    result.setTotalUsage(string.replaceFirst("Total usage over range: ", ""));
                }
            }
        }

        return result;
    }

    /**
     * Helper method to add the result to the result map
     * @param deviceId - Device the result is for
     * @param result - The result
     */
    private void addResultToMap(Integer deviceId, ProfilePeakResult result) {

        if (!resultMap.containsKey(deviceId)) {
            resultMap.put(deviceId, new ArrayList<ProfilePeakResult>());
        }
        List<ProfilePeakResult> deviceResultList = resultMap.get(deviceId);

        // Remove any duplicate results
        ProfilePeakResult removeResult = null;
        for (ProfilePeakResult existingResult : deviceResultList) {
            if (result.getFromDate().equals(existingResult.getFromDate()) && result.getToDate()
                                                                                   .equals(existingResult.getToDate())) {
                removeResult = existingResult;
                break;
            }
        }
        if (removeResult != null) {
            deviceResultList.remove(removeResult);
        }

        deviceResultList.add(0, result);
        // Only keep track of the previous 5 reads
        if (deviceResultList.size() > 5) {
            deviceResultList.remove(deviceResultList.size() - 1);
        }
    }

    public static class ProfilePeakResult {

        private String range = null;
        private String peakDate = "No Data available";
        private String usage = "No Data available";
        private String demand = "No Data available";
        private String averageDailyUsage = "No Data available";
        private String totalUsage = "No Data available";

        private boolean noData = false;
        private String runDate = null;
        private String fromDate = null;
        private String toDate = null;

        public String getRunDate() {
            return runDate;
        }

        public void setRunDate(String runDate) {
            this.runDate = runDate;
        }

        public String getAverageDailyUsage() {
            return averageDailyUsage;
        }

        public void setAverageDailyUsage(String averageDailyUsage) {
            this.averageDailyUsage = averageDailyUsage;
        }

        public String getDemand() {
            return demand;
        }

        public void setDemand(String demand) {
            this.demand = demand;
        }

        public String getPeakDate() {
            return peakDate;
        }

        public void setPeakDate(String peakDate) {
            this.peakDate = peakDate;
        }

        public String getRange() {
            return range;
        }

        public void setRange(String range) {
            this.range = range;
        }

        public String getTotalUsage() {
            return totalUsage;
        }

        public void setTotalUsage(String totalUsage) {
            this.totalUsage = totalUsage;
        }

        public String getUsage() {
            return usage;
        }

        public void setUsage(String usage) {
            this.usage = usage;
        }

        public boolean isNoData() {
            return noData;
        }

        public void setNoData(boolean noData) {
            this.noData = noData;
        }

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        public String getToDate() {
            return toDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

    }

}
