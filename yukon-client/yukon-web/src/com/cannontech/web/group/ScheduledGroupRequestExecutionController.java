package com.cannontech.web.group;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionService;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.RetryStrategy;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.schedule.ScheduleControllerHelper;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.AttributeSelectorHelperService;

@CheckRoleProperty(YukonRoleProperty.MANAGE_SCHEDULES)
@RequestMapping("/scheduledGroupRequestExecution/*")
@Controller
public class ScheduledGroupRequestExecutionController {

    @Autowired private AttributeSelectorHelperService attributeSelectorHelperService;
    @Autowired private AttributeService attributeService;
    @Autowired private CommandDao commandDao;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private PaoCommandAuthorizationService paoCommandAuthorizationService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
    @Autowired private ScheduledGroupRequestExecutionService scheduledGroupRequestExecutionService;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    @Autowired private ToolsEventLogService toolsEventLogService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private ScheduleControllerHelper scheduleControllerHelper;

    private List<LiteCommand> meterCommands;
    private JobManager jobManager;
    private YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition;

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response, FlashScope flashScope)
            throws ServletException {
        int editJobId = ServletRequestUtils.getIntParameter(request, "editJobId", 0);
        if (editJobId != 0 && jobManager.getJob(editJobId).isDeleted()) {
            flashScope.setError(new YukonMessageSourceResolvable(
                "yukon.web.modules.tools.schedules.VIEW.results.jobDetail.error.editDeletedJob"));
            return new ModelAndView("redirect:/group/scheduledGroupRequestExecutionResults/detail?jobId=" + editJobId);
        }

        ModelAndView mav = new ModelAndView("scheduledGroupRequestExecution/home.jsp");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

        // pass-through
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
        String requestType = ServletRequestUtils.getStringParameter(request, "requestType", null);
        Set<? extends Attribute> selectedAttributes =
            attributeSelectorHelperService.getAttributeSet(request, null, null);
        String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue", null);
        String commandString = ServletRequestUtils.getStringParameter(request, "commandString", null);
        String scheduleName = ServletRequestUtils.getStringParameter(request, "scheduleName", null);
        String cronExpression = ServletRequestUtils.getStringParameter(request, "cronExpression", null);
        boolean retryCheckbox = ServletRequestUtils.getBooleanParameter(request, "retryCheckbox", false);
        String queuedRetryCount = ServletRequestUtils.getStringParameter(request, "queuedRetryCount", null);
        String nonQueuedRetryCount = ServletRequestUtils.getStringParameter(request, "nonQueuedRetryCount", null);
        String maxTotalRunTimeHours = ServletRequestUtils.getStringParameter(request, "maxTotalRunTimeHours", null);
        if (maxTotalRunTimeHours != null && maxTotalRunTimeHours.equals("0")) {
            maxTotalRunTimeHours = null;
        }
        String deviceGroupName = ServletRequestUtils.getStringParameter(request, "deviceGroupName", null);
        PageEditMode pageEditMode = PageEditMode.CREATE;

        // edit existing job
        boolean editMode = false;
        if (editJobId > 0) {
            editMode = true;
        }

        // set the parameters to those of the current job if they are not already present in the request
        // (which may exist due to error pass-through)
        ScheduledRepeatingJob existingJob = null;
        if (editMode) {

            existingJob = jobManager.getRepeatingJob(editJobId);
            ScheduledGroupRequestExecutionTask existingTask = new ScheduledGroupRequestExecutionTask();
            InputRoot inputRoot = scheduledGroupRequestExecutionJobDefinition.getInputs();
            InputUtil.applyProperties(inputRoot, existingTask, existingJob.getJobProperties());

            if (requestType == null) {
                requestType = existingTask.getCommandRequestExecutionType().name();
            }
            if (selectedAttributes.size() == 0 && existingTask.getAttributes() != null) {
                selectedAttributes = existingTask.getAttributes();
            }
            if (commandString == null && existingTask.getCommand() != null) {

                commandString = existingTask.getCommand();

                // attempt to select the command to default the drop down to (commandSelectValue)
                int maxDifferenceIndex = 0;
                for (LiteCommand liteCommand : meterCommands) {

                    String thisCommand = liteCommand.getCommand();
                    int indexOfDifference = StringUtils.indexOfDifference(commandString, thisCommand);
                    if (indexOfDifference == -1 || (indexOfDifference > maxDifferenceIndex && maxDifferenceIndex != -1)) {
                        maxDifferenceIndex = indexOfDifference;
                        commandSelectValue = liteCommand.getCommand();
                    }
                }
            }
            if (scheduleName == null) {
                scheduleName = existingTask.getName();
            }
            if (cronExpression == null) {
                cronExpression = existingJob.getCronString();
            }
            if (queuedRetryCount == null && existingTask.getTurnOffQueuingAfterRetryCount() != null
                && existingTask.getTurnOffQueuingAfterRetryCount() > 0) {
                queuedRetryCount = String.valueOf(existingTask.getTurnOffQueuingAfterRetryCount());
            }
            if (nonQueuedRetryCount == null && existingTask.getTurnOffQueuingAfterRetryCount() != null
                && (existingTask.getRetryCount() - existingTask.getTurnOffQueuingAfterRetryCount()) > 0) {
                nonQueuedRetryCount =
                    String.valueOf(existingTask.getRetryCount() - existingTask.getTurnOffQueuingAfterRetryCount());
            }
            if (maxTotalRunTimeHours == null && existingTask.getStopRetryAfterHoursCount() != null
                && existingTask.getStopRetryAfterHoursCount() > 0) {
                maxTotalRunTimeHours = String.valueOf(existingTask.getStopRetryAfterHoursCount());
            }
            if (!StringUtils.isBlank(queuedRetryCount) || !StringUtils.isBlank(nonQueuedRetryCount)
                || !StringUtils.isBlank(maxTotalRunTimeHours)) {
                retryCheckbox = true;
            }
            if (deviceGroupName == null) {
                DeviceGroup existingDeviceGroup = existingTask.getDeviceGroup();
                if (existingDeviceGroup != null) {
                    deviceGroupName = existingTask.getDeviceGroup().getFullName();
                }
            }
        }

        mav.addObject("editJobId", editJobId);
        mav.addObject("futureStart", false);

        if (editMode) {
            pageEditMode = PageEditMode.EDIT;
            mav.addObject("disabled", existingJob.isDisabled());
            mav.addObject("status", scheduledGroupRequestExecutionDao.getStatusByJobId(existingJob.getId()));
            mav.addObject("futureStart", true);
        }
        mav.addObject("mode", pageEditMode);
        mav.addObject("errorMsg", errorMsg);
        mav.addObject("requestType", requestType);
        mav.addObject("selectedAttributes", selectedAttributes);
        mav.addObject("commandSelectValue", commandSelectValue);
        mav.addObject("commandString", commandString);
        mav.addObject("scheduleName", scheduleName);
        mav.addObject("cronExpression", cronExpression);
        mav.addObject("retryCheckbox", retryCheckbox);
        mav.addObject("queuedRetryCount", queuedRetryCount);
        mav.addObject("nonQueuedRetryCount", nonQueuedRetryCount);
        mav.addObject("maxTotalRunTimeHours", maxTotalRunTimeHours);
        mav.addObject("deviceGroupName", deviceGroupName);

        // attributes
        Set<Attribute> allReadableAttributes = attributeService.getReadableAttributes();
        Map<AttributeGroup, List<BuiltInAttribute>> allGroupedReadableAttributes =
            attributeService.getGroupedAttributeMapFromCollection(allReadableAttributes, userContext);
        mav.addObject("allGroupedReadableAttributes", allGroupedReadableAttributes);

        // commands
        List<LiteCommand> commands = commandDao.filterCommandsForUser(meterCommands, userContext.getYukonUser());
        mav.addObject("commands", commands);

        // cron
        CronExpressionTagState cronExpressionTagState = cronExpressionTagService.parse(cronExpression, userContext);
        mav.addObject("cronExpressionTagState", cronExpressionTagState);

        return mav;
    }

    @RequestMapping("schedule")
    public ModelAndView schedule(HttpServletRequest request, HttpServletResponse response, FlashScope flashScope)
            throws ServletException {
        int editJobId = ServletRequestUtils.getIntParameter(request, "editJobId", 0);
        if (editJobId != 0 && jobManager.getJob(editJobId).isDeleted()) {
            flashScope.setError(new YukonMessageSourceResolvable(
                "yukon.web.modules.tools.schedules.VIEW.results.jobDetail.error.editDeletedJob"));
            return new ModelAndView("redirect:/group/scheduledGroupRequestExecutionResults/detail?jobId=" + editJobId);
        }

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        // create variables for schedule validation and error messages
        String errorMsg = null;
        String requestTypeStr = ServletRequestUtils.getRequiredStringParameter(request, "requestType");
        DeviceRequestType requestType = DeviceRequestType.valueOf(requestTypeStr);
        String scheduleName = ServletRequestUtils.getStringParameter(request, "scheduleName");
        String cronExpression = null;
        Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
        String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue");
        String commandString = ServletRequestUtils.getStringParameter(request, "commandString");
        String formUniqueId = ServletRequestUtils.getRequiredStringParameter(request, "formUniqueId");
        String deviceGroupName = ServletRequestUtils.getStringParameter(request, "deviceGroupName_" + formUniqueId);
        boolean retryCheckbox = ServletRequestUtils.getBooleanParameter(request, "retryCheckbox", false);
        String queuedRetryCountStr = ServletRequestUtils.getStringParameter(request, "queuedRetryCount", null);
        String nonQueuedRetryCountStr = ServletRequestUtils.getStringParameter(request, "nonQueuedRetryCount", null);
        String maxTotalRunTimeHoursStr = ServletRequestUtils.getStringParameter(request, "maxTotalRunTimeHours", null);
        Integer queuedRetryCount = null;
        Integer nonQueuedRetryCount = null;
        Integer maxTotalRunTimeHours = null;
        
        // perform validation tests
        try {
            cronExpression = cronExpressionTagService.build(formUniqueId, request, userContext);
        } catch (Exception e) {
            cronExpression = null;
            errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.time.invalid");
            return makeErrorMav(errorMsg, requestType, scheduleName, cronExpression,
                makeSelectedAttributeStrsParameter(selectedAttributes), commandSelectValue, commandString,
                deviceGroupName, retryCheckbox, queuedRetryCountStr, nonQueuedRetryCountStr, maxTotalRunTimeHoursStr,
                editJobId);
        }
        
        if (deviceGroupService.findGroupName(deviceGroupName) == null) {
            errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.invalidGroupName");
        } else if (StringUtils.isBlank(scheduleName)) { 
            errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.name.blank");
        } else if (StringUtils.length(scheduleName) > 200) {
            errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.name.length");
            scheduleName = StringUtils.substring(scheduleName, 0, 200);
        } else {
            if (retryCheckbox) {
                // parse retry options
                if (!StringUtils.isBlank(queuedRetryCountStr) && (errorMsg == null)) {
                    try {
                        queuedRetryCount = Integer.valueOf(queuedRetryCountStr);
                    } catch (NumberFormatException e) {
                        errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.queuedRetryCount", queuedRetryCountStr);
                    }
                }
                if (!StringUtils.isBlank(nonQueuedRetryCountStr) && (errorMsg == null)) {
                    try {
                        nonQueuedRetryCount = Integer.valueOf(nonQueuedRetryCountStr);
                    } catch (NumberFormatException e) {
                        errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.nonQueuedRetryCount", nonQueuedRetryCountStr);
                    }
                }
                if (!StringUtils.isBlank(maxTotalRunTimeHoursStr) && (errorMsg == null)) {
                    try {
                        maxTotalRunTimeHours = Integer.valueOf(maxTotalRunTimeHoursStr);
                    } catch (NumberFormatException e) {
                        errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.maxRunTime", maxTotalRunTimeHoursStr);
                    }
                }
    
                // additional retry options validation
                if (errorMsg == null && queuedRetryCount != null && queuedRetryCount < 0) {
                    errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.queuedRetryCount", queuedRetryCountStr);
                }
                if (errorMsg == null && nonQueuedRetryCount != null && nonQueuedRetryCount < 0) {
                    errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.nonQueuedRetryCount", nonQueuedRetryCountStr);
                }
    
                int totalRetryCount = 0;
                if (queuedRetryCount != null) {
                    totalRetryCount += queuedRetryCount;
                }
                if (nonQueuedRetryCount != null) {
                    totalRetryCount += nonQueuedRetryCount;
                }
    
                if (errorMsg == null && totalRetryCount < 1 || totalRetryCount > 10) {
                    errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.totalRetryCount.bounds");
                }
                if (errorMsg == null && maxTotalRunTimeHours != null && maxTotalRunTimeHours < 1) {
                    errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.maxRunTime", maxTotalRunTimeHoursStr);
                }
            }
        }
        
        if (errorMsg != null) {
            return makeErrorMav(errorMsg, requestType, scheduleName, cronExpression,
                makeSelectedAttributeStrsParameter(selectedAttributes), commandSelectValue, commandString,
                deviceGroupName, retryCheckbox, queuedRetryCountStr, nonQueuedRetryCountStr, maxTotalRunTimeHoursStr,
                editJobId);
        }

        // bridge UI parameters with actual retry back end
        // calculate retryCount, stopRetryAfterHoursCount, and turnOffQueuingAfterRetryCount
        // in terms of queuedRetryCount, maxTotalRunTimeHours, and nonQueuedRetryCount
        queuedRetryCount = queuedRetryCount == null ? 0 : queuedRetryCount;
        nonQueuedRetryCount = nonQueuedRetryCount == null ? 0 : nonQueuedRetryCount;

        int retryCount = queuedRetryCount + nonQueuedRetryCount;
        Integer stopRetryAfterHoursCount = maxTotalRunTimeHours;
        Integer turnOffQueuingAfterRetryCount = queuedRetryCount;

        // schedule / edit
        if (requestType.equals(DeviceRequestType.SCHEDULED_GROUP_ATTRIBUTE_READ)) {
            return scheduleAttributeRead(request, scheduleName, cronExpression, deviceGroupName, editJobId,
                retryCheckbox, retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);
        } else if (requestType.equals(DeviceRequestType.SCHEDULED_GROUP_COMMAND)) {
            return scheduleCommand(request, scheduleName, cronExpression, deviceGroupName, editJobId, retryCheckbox,
                retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);
        } else {
            throw new IllegalArgumentException("Unsupported requestType: " + requestType);
        }
    }

    // SCHEDULE ATTRIBUTE READ
    private ModelAndView scheduleAttributeRead(HttpServletRequest request, String scheduleName, String cronExpression,
            String deviceGroupName, int editJobId, boolean retryCheckbox, int retryCount,
            Integer stopRetryAfterHoursCount, Integer turnOffQueuingAfterRetryCount) throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/group/scheduledGroupRequestExecutionResults/detail");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        // attribute
        Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
        if (selectedAttributes.size() == 0) {
            return makeErrorMav(messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.attribute.blank"), DeviceRequestType.SCHEDULED_GROUP_ATTRIBUTE_READ,
                scheduleName, cronExpression, null, null, null, deviceGroupName, retryCheckbox, retryCount == 0 ? ""
                    : String.valueOf(retryCount),
                stopRetryAfterHoursCount == null ? "" : stopRetryAfterHoursCount.toString(),
                turnOffQueuingAfterRetryCount == null ? "" : turnOffQueuingAfterRetryCount.toString(), editJobId);
        }

        // schedule / re-schedule
        YukonJob job = null;
        RetryStrategy retryStrategy =
            new RetryStrategy(retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);

        String cronDescription = cronExpressionTagService.getDescription(cronExpression, userContext);

        if (editJobId <= 0) {
            job =
                scheduledGroupRequestExecutionService.schedule(scheduleName, deviceGroupName, selectedAttributes,
                    DeviceRequestType.SCHEDULED_GROUP_ATTRIBUTE_READ, cronExpression, userContext, retryStrategy);
            toolsEventLogService.groupRequestByAttributeScheduleCreated(userContext.getYukonUser(), scheduleName,
                cronDescription);
        } else {
            boolean isDisabled = scheduledRepeatingJobDao.getById(editJobId).isDisabled();
            job =
                scheduledGroupRequestExecutionService.scheduleReplacement(editJobId, scheduleName, deviceGroupName,
                    selectedAttributes, DeviceRequestType.SCHEDULED_GROUP_ATTRIBUTE_READ, cronExpression, userContext,
                    retryStrategy);
            toolsEventLogService.groupRequestByAttributeScheduleUpdated(userContext.getYukonUser(), scheduleName,
                cronDescription);
            if (isDisabled) {
                jobManager.disableJob(job);
            }
        }

        mav.addObject("jobId", job.getId());
        return mav;
    }

    // SCHEDULE COMMAND
    private ModelAndView scheduleCommand(HttpServletRequest request, String scheduleName, String cronExpression,
            String deviceGroupName, int editJobId, boolean retryCheckbox, int retryCount,
            Integer stopRetryAfterHoursCount, Integer turnOffQueuingAfterRetryCount) throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/group/scheduledGroupRequestExecutionResults/detail");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        String errorMsg = null;

        // validate command string
        String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue");
        String commandString = ServletRequestUtils.getStringParameter(request, "commandString");
        if (StringUtils.isBlank(commandString)) {
            errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.command.blank");
        } else if (rolePropertyDao.checkProperty(YukonRoleProperty.EXECUTE_MANUAL_COMMAND, userContext.getYukonUser())) {
            // check that it is authorized
            if (!paoCommandAuthorizationService.isAuthorized(userContext.getYukonUser(), commandString)) {
                errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.command.authorized", commandString);
            }

        } else {
            // check that the command is in the authorized list (implies that it is authorized)
            List<LiteCommand> commands = commandDao.filterCommandsForUser(meterCommands, userContext.getYukonUser());
            List<String> commandStrings =
                new MappingList<LiteCommand, String>(commands, new ObjectMapper<LiteCommand, String>() {
                    @Override
                    public String map(LiteCommand from) throws ObjectMappingException {
                        return from.getCommand();
                    }
                });
            if (!commandStrings.contains(commandString)) {
                errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.error.command.authorized", commandString);
            }
        }

        if (errorMsg != null) {
            return makeErrorMav(errorMsg, DeviceRequestType.SCHEDULED_GROUP_COMMAND, scheduleName, cronExpression,
                null, commandSelectValue, commandString, deviceGroupName, retryCheckbox,
                retryCount == 0 ? "" : String.valueOf(retryCount), stopRetryAfterHoursCount == null ? ""
                    : stopRetryAfterHoursCount.toString(), turnOffQueuingAfterRetryCount == null ? ""
                    : turnOffQueuingAfterRetryCount.toString(), editJobId);
        }

        // schedule / re-schedule
        YukonJob job = null;
        RetryStrategy retryStrategy =
            new RetryStrategy(retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);

        String cronDescription = cronExpressionTagService.getDescription(cronExpression, userContext);

        if (editJobId <= 0) {
            job =
                scheduledGroupRequestExecutionService.schedule(scheduleName, deviceGroupName, commandString,
                    DeviceRequestType.SCHEDULED_GROUP_COMMAND, cronExpression, userContext, retryStrategy);
            toolsEventLogService.groupRequestByCommandScheduleCreated(userContext.getYukonUser(), scheduleName,
                cronDescription);
        } else {
            boolean isDisabled = scheduledRepeatingJobDao.getById(editJobId).isDisabled();
            job =
                scheduledGroupRequestExecutionService.scheduleReplacement(editJobId, scheduleName, deviceGroupName,
                    commandString, DeviceRequestType.SCHEDULED_GROUP_COMMAND, cronExpression, userContext,
                    retryStrategy);
            toolsEventLogService.groupRequestByCommandScheduleUpdated(userContext.getYukonUser(), scheduleName,
                cronDescription);
            if (isDisabled) {
                jobManager.disableJob(job);
            }
        }
        mav.addObject("jobId", job.getId());

        return mav;
    }

    // ERROR MAV
    private ModelAndView makeErrorMav(String errorMsg, DeviceRequestType requestType, String scheduleName,
            String cronExpression, String selectedAttributeStrs, String commandSelectValue, String commandString,
            String deviceGroupName, boolean retryCheckbox, String queuedRetryCountStr, String nonQueuedRetryCountStr,
            String maxTotalRunTimeHoursStr, int editJobId) {

        ModelAndView mav = new ModelAndView("redirect:home");
        if (editJobId != 0) {
            mav.addObject("editJobId", editJobId);
        }
        mav.addObject("errorMsg", errorMsg);
        mav.addObject("requestType", requestType.name());
        mav.addObject("selectedAttributeStrs", selectedAttributeStrs);
        mav.addObject("commandSelectValue", commandSelectValue);
        mav.addObject("commandString", commandString);
        mav.addObject("scheduleName", scheduleName);
        mav.addObject("cronExpression", cronExpression);
        mav.addObject("retryCheckbox", retryCheckbox);
        mav.addObject("queuedRetryCount", queuedRetryCountStr);
        mav.addObject("nonQueuedRetryCount", nonQueuedRetryCountStr);
        mav.addObject("maxTotalRunTimeHours", maxTotalRunTimeHoursStr);
        mav.addObject("deviceGroupName", deviceGroupName);

        return mav;
    }

    @RequestMapping(value = "toggleJobEnabled", method = RequestMethod.POST)
    public ModelAndView toggleJobEnabled(HttpServletRequest request, HttpServletResponse response, FlashScope flashScope)
            throws ServletException {
        int toggleJobId = ServletRequestUtils.getRequiredIntParameter(request, "toggleJobId");
        if (toggleJobId != 0 && jobManager.getJob(toggleJobId).isDeleted()) {
            flashScope.setError(new YukonMessageSourceResolvable(
                "yukon.web.modules.tools.schedules.VIEW.results.jobDetail.error.editDeletedJob"));
            return new ModelAndView("redirect:/group/scheduledGroupRequestExecutionResults/detail?jobId=" + toggleJobId);
        }

        ModelAndView mav = new ModelAndView("redirect:home");

        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(toggleJobId);

        jobManager.toggleJobStatus(job);

        mav.addObject("editJobId", toggleJobId);

        return mav;

    }

    @RequestMapping("toggleJob")
    public @ResponseBody Map<String, Object> toggleJob(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        int toggleJobId = ServletRequestUtils.getRequiredIntParameter(request, "toggleJobId");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        return scheduleControllerHelper.toggleJob(toggleJobId, userContext);
    }

    @RequestMapping("startDialog")
    public String startDialog(ModelMap model, HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        int toggleJobId = ServletRequestUtils.getRequiredIntParameter(request, "jobId");
        ScheduledRepeatingJob job = jobManager.getRepeatingJob(toggleJobId);
        DateTime nextRunTime = new DateTime().plusHours(1);
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        try {
            CronExpression cronExpression = new CronExpression(job.getCronString());
            Date cronDate = cronExpression.getNextValidTimeAfter(new Date());
            if (cronDate != null) {
                nextRunTime = new DateTime(cronDate);
            }
        } catch (ParseException e) {
            model.addAttribute("error", messageSourceAccessor.getMessage("yukon.web.modules.tools.schedules.VIEW.results.jobDetail.error.date"));
        }

        DateTimeFormatter format = DateTimeFormat.forPattern("a");
        String amPm = format.print(nextRunTime);

        model.addAttribute("jobId", toggleJobId);
        int hours = nextRunTime.getHourOfDay();
        int minutes = nextRunTime.getMinuteOfHour();

        model.addAttribute("hours", hours);
        model.addAttribute("amPm", amPm);
        model.addAttribute("minutes", minutes);
        model.addAttribute("now", nextRunTime);
        return "scheduledGroupRequestExecution/startSchedule.jsp";

    }

    @RequestMapping(value = "startJob")
    public @ResponseBody Map<String, Object> startJob(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        int jobId = ServletRequestUtils.getRequiredIntParameter(request, "toggleJobId");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String cronExpression = null;
        try {
            cronExpression = cronExpressionTagService.build(Integer.toString(jobId), request, userContext);
        } catch (CronException e) {}
        return scheduleControllerHelper.startJob(jobId, cronExpression, userContext);
    }

    @RequestMapping("cancelScheduledJob")
    public void cancelScheduledJob(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        int toggleJobId = ServletRequestUtils.getRequiredIntParameter(request, "toggleJobId");
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(toggleJobId);
        jobManager.unscheduleJob(job);

        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @RequestMapping("cancelJob")
    public void cancelJob(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        int toggleJobId = ServletRequestUtils.getRequiredIntParameter(request, "toggleJobId");
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(toggleJobId);
        jobManager.abortJob(job);

        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    /**
     * (not really a hard delete, but set Job.Disabled = 'D' to hide it)
     */
    @RequestMapping(value = "deleteJob", method = RequestMethod.POST)
    public ModelAndView deleteJob(HttpServletRequest request, HttpServletResponse response, FlashScope flashScope)
            throws ServletException {
        int deleteJobId = ServletRequestUtils.getRequiredIntParameter(request, "deleteJobId");
        if (deleteJobId != 0 && jobManager.getJob(deleteJobId).isDeleted()) {
            flashScope.setError(new YukonMessageSourceResolvable(
                "yukon.web.modules.tools.schedules.VIEW.results.jobDetail.error.editDeletedJob"));
            return new ModelAndView("redirect:/group/scheduledGroupRequestExecutionResults/detail?jobId=" + deleteJobId);
        }
        
        ModelAndView mav = new ModelAndView("redirect:/group/scheduledGroupRequestExecutionResults/jobs");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(deleteJobId);
        String jobName = job.getJobProperties().get("name");
        jobManager.deleteJob(job);

        toolsEventLogService.groupRequestScheduleDeleted(userContext.getYukonUser(), jobName);

        return mav;
    }

    // HELPERS
    private String makeSelectedAttributeStrsParameter(Set<Attribute> attributeParameters) {
        return StringUtils.join(attributeParameters, ",");
    }

    // INIT
    @PostConstruct
    public void init() {

        this.meterCommands = new ArrayList<LiteCommand>();

        List<LiteDeviceTypeCommand> devTypeCmds = commandDao.getAllDevTypeCommands(PaoType.MCT410IL.getDbString());
        for (LiteDeviceTypeCommand devTypeCmd : devTypeCmds) {
            int cmdId = devTypeCmd.getCommandId();
            LiteCommand liteCmd = commandDao.getCommand(cmdId);
            this.meterCommands.add(liteCmd);
        }
    }

    @Resource(name = "jobManager")
    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Resource(name = "scheduledGroupRequestExecutionJobDefinition")
    public void setScheduledGroupRequestExecutionjobDefinition(
            YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition) {
        this.scheduledGroupRequestExecutionJobDefinition = scheduledGroupRequestExecutionJobDefinition;
    }
}
