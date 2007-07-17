package com.cannontech.web.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.device.commands.CommandRequest;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.device.profilePeak.dao.ProfilePeakDao;
import com.cannontech.common.device.profilePeak.model.ProfilePeakResult;
import com.cannontech.common.device.profilePeak.model.ProfilePeakResultType;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
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
    private ProfilePeakDao profilePeakDao = null;
    private CommandRequestExecutor commandRequestExecutor = null;

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    public void setProfilePeakDao(ProfilePeakDao profilePeakDao) {
        this.profilePeakDao = profilePeakDao;
    }

    public void setCommandRequestExecutor(CommandRequestExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }

    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ModelAndView mav = new ModelAndView();

        // Add any previous results for the device into the mav
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        ProfilePeakResult preResult = profilePeakDao.getResult(deviceId, ProfilePeakResultType.PRE);
        mav.addObject("preResult", preResult);

        ProfilePeakResult postResult = profilePeakDao.getResult(deviceId,
                                                                ProfilePeakResultType.POST);
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

        ProfilePeakResult preResult = null;
        ProfilePeakResult postResult = null;
        try {

            // Get the user's timezone
            LiteYukonUser user = ServletUtil.getYukonUser(request);
            TimeZone timeZone = yukonUserDao.getUserTimeZone(user);

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
                preCommandStartDate = TimeUtil.flexibleDateParser(startDateStr,
                                                                  TimeUtil.NO_TIME_MODE.START_OF_DAY,
                                                                  timeZone);
            } catch (ParseException e) {
                mav.addObject("errorMsg",
                              "Start date: " + startDateStr + " is not formatted correctly - example (mm/dd/yyyy).  Please try again.");
                return mav;
            }
            Date preCommandStopDate = null;
            try {
                preCommandStopDate = TimeUtil.flexibleDateParser(stopDateStr,
                                                                 TimeUtil.NO_TIME_MODE.START_OF_DAY,
                                                                 timeZone);
            } catch (ParseException e) {
                mav.addObject("errorMsg",
                              "Stop date: " + stopDateStr + " is not formatted correctly - example (mm/dd/yyyy).  Please try again.");
                return mav;
            }

            if (preCommandStopDate.before(preCommandStartDate) || preCommandStartDate.getTime() == preCommandStopDate.getTime()) {
                mav.addObject("errorMsg", "Start date must be before stop date.  Please try again.");
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
            long preCommandDays = (getDaysBetween(preCommandStopDate, preCommandStartDate) + 1);
            preCommand.append(" " + preCommandDays);

            StringBuffer postCommand = new StringBuffer();
            // Build post command only if days are 1 or more
            long postCommandDays = getDaysBetween(postCommandStopDate, preCommandStopDate);
            if (postCommandDays > 0) {
                postCommand.append("getvalue lp peak");
                postCommand.append(" " + reportType);
                postCommand.append(" channel 1");
                postCommand.append(" " + COMMAND_FORMAT.format(postCommandStopDate));
                postCommand.append(" " + postCommandDays);
            }

            int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");

            // Execute pre command to get profile peak summary results for user
            // request
            preResult = executeCommand(preCommand.toString(), deviceId, ProfilePeakResultType.PRE, user);
            preResult.setStartDate(DISPLAY_FORMAT.format(preCommandStartDate));

            // Stop date is actually 1 day later - stop date is inclusive
            preResult.setStopDate(DISPLAY_FORMAT.format(TimeUtil.addDays(preCommandStopDate, 1)));
            preResult.setDays(preCommandDays);

            // Execute post command to get profile peak summary results for
            // user request end date to now
            if (!StringUtils.isBlank(postCommand.toString())) {
                postResult = executeCommand(postCommand.toString(),
                                            deviceId,
                                            ProfilePeakResultType.POST, user);
                postResult.setStartDate(DISPLAY_FORMAT.format(TimeUtil.addDays(preCommandStopDate, 1)));

                // Stop date is actually 1 day later - stop date is inclusive
                postResult.setStopDate(DISPLAY_FORMAT.format(TimeUtil.addDays(postCommandStopDate,
                                                                              1)));
                postResult.setDays(postCommandDays);
            }

            // Persist the results
            List<ProfilePeakResult> results = new ArrayList<ProfilePeakResult>();
            if (!preResult.isNoData()) {
                results.add(preResult);
            }
            if (postResult != null && !postResult.isNoData()) {
                results.add(postResult);
            }

            profilePeakDao.saveResults(deviceId, results);

        } catch (CommandCompletionException e) {
            mav.addObject("errorMsg", e.getMessage());
        } catch (ConnectionException e) {
            mav.addObject("errorMsg", e.getMessage());
        }

        mav.addObject("preResult", preResult);
        mav.addObject("postResult", postResult);

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

    /**
     * Helper method to execute a profile peak result command and return the
     * resulting profile peak result
     * @param command - Command to execute
     * @param deviceId - Id of device to execute command for
     * @param type - Type of results
     * @param user 
     * @return Results
     * @throws CommandCompletionException
     * @throws PaoAuthorizationException 
     */
    private ProfilePeakResult executeCommand(String command, int deviceId,
            ProfilePeakResultType type, LiteYukonUser user) throws CommandCompletionException, PaoAuthorizationException {

        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setCommand(command);
        commandRequest.setDeviceId(deviceId);
        CommandResultHolder resultHolder = commandRequestExecutor.execute(commandRequest, user);

        ProfilePeakResult result = parseResults(resultHolder);
        result.setRunDate(DISPLAY_FORMAT.format(new Date()));
        result.setDeviceId(deviceId);
        result.setResultType(type);

        return result;
    }

    /**
     * Helper method to parse a result string into a ProfilePeakResult
     * @param resultHolder - Object containing result string
     * @return - ProfilePeakResult containing the result
     */
    private ProfilePeakResult parseResults(CommandResultHolder resultHolder) {

        ProfilePeakResult result = new ProfilePeakResult();

        String resultString = resultHolder.getLastResultString();

        if (resultHolder.isErrorsExist()) {
            StringBuffer sb = new StringBuffer();
            List<DeviceErrorDescription> errors = resultHolder.getErrors();
            for (DeviceErrorDescription ded : errors) {
                sb.append(ded.toString() + "\n");
            }

            result.setError(sb.toString());
            result.setNoData(true);
        } else {
            // Results exist

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

    private long getDaysBetween(Date date1, Date date2) {

        long delta = date1.getTime() - date2.getTime();
        // Convert delta to days (86400000 milliseconds in a day)
        long days = delta / 86400000;

        return days;
    }

}
