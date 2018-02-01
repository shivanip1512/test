package com.cannontech.web.scheduledFileExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.scheduledFileExport.BillingFileExportGenerationParameters;
import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledBillingFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledFileExportTask;
import com.cannontech.web.scheduledFileExport.validator.ScheduledFileExportValidator;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/*")
@CheckRole(YukonRole.APPLICATION_BILLING)
public class ScheduledBillingFileExportController {

    @Autowired private ContactDao contactDao;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private JobManager jobManager;
    @Autowired private ScheduledFileExportService scheduledFileExportService;
    @Autowired private ScheduledFileExportHelper exportHelper;
    @Autowired private ToolsEventLogService toolsEventLogService;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    private final static String baseKey = "yukon.web.modules.amr.billing.jobs";
    private static final int MAX_GROUPS_DISPLAYED = 2;
    private ScheduledFileExportValidator scheduledFileExportValidator;

    @RequestMapping("showForm")
    public String showForm(ModelMap model, YukonUserContext userContext, 
            @ModelAttribute("exportData") ScheduledFileExportData exportData, Integer jobId, Integer fileFormat, 
            Integer demandDays, Integer energyDays, @RequestParam(defaultValue="false") boolean removeMultiplier,
            @RequestParam(defaultValue="null") String[] billGroup) {

        if (billGroup == null) {
            billGroup = new String[0];
        }

        CronExpressionTagState cronExpressionTagState = new CronExpressionTagState();

        if (jobId != null) {
            //edit existing schedule
            ScheduledRepeatingJob job = jobManager.getRepeatingJob(jobId);
            ScheduledBillingFileExportTask task = (ScheduledBillingFileExportTask) jobManager.instantiateTask(job);

            billGroup = task.getDeviceGroupNames().toArray(new String[task.getDeviceGroupNames().size()]);
            fileFormat = task.getFileFormatId();
            demandDays = task.getDemandDays();
            energyDays = task.getEnergyDays();
            removeMultiplier = task.isRemoveMultiplier();

            exportData.setScheduleName(task.getName());
            exportData.setExportFileName(task.getExportFileName());
            exportData.setAppendDateToFileName(task.isAppendDateToFileName());
            exportData.setTimestampPatternField(task.getTimestampPatternField());
            exportData.setOverrideFileExtension(task.isOverrideFileExtension());
            exportData.setExportFileExtension(task.getExportFileExtension());
            exportData.setIncludeExportCopy(task.isIncludeExportCopy());
            exportData.setExportPath(task.getExportPath());
            if (task.getNotificationEmailAddresses() != null) {
                exportData.setNotificationEmailAddresses(task.getNotificationEmailAddresses());
                exportData.setSendEmail(task.isSendEmail());
            } else {
                exportData.setNotificationEmailAddresses(contactDao.getUserEmail(userContext.getYukonUser()));
            }
            cronExpressionTagState = cronExpressionTagService.parse(job.getCronString(), job.getUserContext());
            model.addAttribute("jobId", jobId);
        } else {
            exportData.setNotificationEmailAddresses(contactDao.getUserEmail(userContext.getYukonUser()));
        }

        model.addAttribute("exportData", exportData);
        model.addAttribute("deviceGroups", billGroup);
        model.addAttribute("fileFormat", fileFormat);
        model.addAttribute("demandDays", demandDays);
        model.addAttribute("energyDays", energyDays);
        model.addAttribute("removeMultiplier", removeMultiplier);
        model.addAttribute("cronExpressionTagState", cronExpressionTagState);
        model.addAttribute("fileExtensionChoices", exportHelper.setupFileExtChoices(exportData));
        model.addAttribute("exportPathChoices", exportHelper.setupExportPathChoices(exportData));
        Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(Arrays.asList(billGroup));
        String groupNames = getGroupNamesString(deviceGroups);
        model.addAttribute("groupNames", groupNames);

        String formatName = FileFormatTypes.getFormatType(fileFormat);
        model.addAttribute("formatName", formatName);
        boolean isSmtpConfigured = StringUtils.isBlank(globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
        model.addAttribute("isSmtpConfigured", isSmtpConfigured);
        
        return "_schedule.jsp";
    }

    @RequestMapping(value="scheduleExport.json")
    public @ResponseBody Map<String, Object> scheduleExport(YukonUserContext userContext,
            HttpServletRequest request, @ModelAttribute("exportData") ScheduledFileExportData exportData,
            BindingResult bindingResult, String[] deviceGroups, int fileFormat, int demandDays, int energyDays,
            boolean removeMultiplier, Integer jobId) {

        Set<? extends DeviceGroup> fullDeviceGroups = deviceGroupService.resolveGroupNames(Arrays.asList(deviceGroups));

        BillingFileExportGenerationParameters billingParameters = 
                new BillingFileExportGenerationParameters(fileFormat, fullDeviceGroups,
                                                          demandDays, energyDays, removeMultiplier);
        exportData.setParameters(billingParameters);

        scheduledFileExportValidator = new ScheduledFileExportValidator(this.getClass());
        exportData.setScheduleName(StringUtils.trim(exportData.getScheduleName()));
        exportData.setExportFileName(StringUtils.trim(exportData.getExportFileName()));
        scheduledFileExportValidator.validate(exportData, bindingResult);
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        
        try {
            String scheduleCronString = cronExpressionTagService.build("scheduleCronString", request, userContext);
            exportData.setScheduleCronString(scheduleCronString);
        } catch (CronException e) {
            bindingResult.rejectValue("scheduleCronString", "yukon.common.invalidCron");
        }
        
        if (bindingResult.hasErrors()) {
            return getErrorJson(bindingResult, accessor);
        }

        MessageSourceResolvable msgObj = null;

        String scheduledCronDescription = cronExpressionTagService.getDescription(exportData.getScheduleCronString(), userContext);

        if (jobId == null) {
            //new schedule
            scheduledFileExportService.scheduleFileExport(exportData, userContext, request);
            toolsEventLogService.billingFormatCreated(userContext.getYukonUser(), exportData.getScheduleName(),
                exportData.getExportFileName(), scheduledCronDescription);
            msgObj = new YukonMessageSourceResolvable("yukon.web.modules.amr.billing.jobs.jobCreated", exportData.getScheduleName());
        } else {
            //edit schedule
            scheduledFileExportService.updateFileExport(exportData, userContext, request, jobId);
            toolsEventLogService.billingFormatUpdated(userContext.getYukonUser(), exportData.getScheduleName(),
                exportData.getExportFileName(), scheduledCronDescription);
            msgObj = new YukonMessageSourceResolvable("yukon.web.modules.amr.billing.jobs.jobUpdated", exportData.getScheduleName());
        }
        
        String message = accessor.getMessage(msgObj);
        Map<String, Object> json = new HashMap<>();
        json.put("success", true);
        json.put("message", message);
        
        return json;
    }

    @RequestMapping("jobs")
    public String jobs(ModelMap model) {

        List<ScheduledFileExportJobData> jobs
            = scheduledFileExportService.getScheduledFileExportJobData(ScheduledExportType.BILLING);

        model.addAttribute("jobType", FileExportType.BILLING);
        model.addAttribute("jobs", jobs);
        
        return "jobs.jsp";
    }

    @RequestMapping("/jobs/{jobId}/enable")
    public String enableJob(@PathVariable int jobId, ModelMap model, FlashScope flashScope, YukonUserContext userContext) {
        YukonJob job = jobManager.getJob(jobId);
        ScheduledBillingFileExportTask task = (ScheduledBillingFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        jobManager.enableJob(job);
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".enableJobSuccess", jobName));
        return "redirect:/billing/home";
    }

    @RequestMapping("/jobs/{jobId}/disable")
    public String disableJob(@PathVariable int jobId, ModelMap model, FlashScope flashScope,
            YukonUserContext userContext) {
        YukonJob job = jobManager.getJob(jobId);
        ScheduledBillingFileExportTask task = (ScheduledBillingFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        jobManager.disableJob(job);
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".disableJobSuccess", jobName));
        return "redirect:/billing/home";
    }
    
    @RequestMapping(value = "delete.json")
    public @ResponseBody Map<String, Object> delete(int jobId, YukonUserContext userContext) {
        
        YukonJob job = jobManager.getJob(jobId);
        ScheduledFileExportTask task = (ScheduledFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        jobManager.deleteJob(job);
        toolsEventLogService.billingFormatDeleted(userContext.getYukonUser(), jobName);

        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        MessageSourceResolvable msgObj = new YukonMessageSourceResolvable("yukon.web.modules.amr.billing.jobs.deletedSuccess", jobName);
        
        String message = accessor.getMessage(msgObj);
        Map<String, Object> json = new HashMap<>();
        json.put("success", true);
        json.put("message", message);
        
        return json;
    }

    public static String getGroupNamesString(Set<? extends DeviceGroup> deviceGroups) {
        String groupNames = "";
        int maxDisplayedGroupNames = deviceGroups.size() > MAX_GROUPS_DISPLAYED ? MAX_GROUPS_DISPLAYED : deviceGroups.size();
        Iterator<? extends DeviceGroup> iterator = deviceGroups.iterator();
        for(int i = 0; i < maxDisplayedGroupNames; i++) {
            DeviceGroup group = iterator.next();
            if (i > 0) {
                groupNames += ", ";
            }
            groupNames += group.getName();
        }
        
        if (maxDisplayedGroupNames < deviceGroups.size()) {
            groupNames += "...";
        }
        
        return groupNames;
    }
    
    /**
     * Call this when a JSON-based action needs to fail and returns the list of errors as Map<String, Object>s.
     * @param errors  Errors or BindingResult
     * @param accessor MessageSourceAccessor to interpret message keys.
     * @return result a Map<String, Object>
     * 
     * @postcondition result['success'] = false
     * @postcondition result['errors'] = List<Object>[ 0+ Map<String, Object>[field:{String}, message:{String}, severity:"ERROR"]]
     */
    private Map<String, Object> getErrorJson(Errors errors, MessageSourceAccessor accessor) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", false);
        List<Object> errorList = new ArrayList<>();

        for (ObjectError err : errors.getGlobalErrors()) {
            final String msg = accessor.getMessage(err.getCode(), err.getArguments());
            Map<String, Object> errorJson = Maps.newHashMapWithExpectedSize(3);
            errorJson.put("field", "GLOBAL");
            errorJson.put("message", msg);
            errorJson.put("severity", "ERROR");
            errorList.add(errorJson);
        }
        for (FieldError err : errors.getFieldErrors()) {
            final String msg = accessor.getMessage(err.getCode(), err.getArguments());
            Map<String, Object> errorJson = Maps.newHashMapWithExpectedSize(3);
            errorJson.put("field", err.getField());
            errorJson.put("message", msg);
            errorJson.put("severity", "ERROR");
            errorList.add(errorJson);
        }
        result.put("errors", errorList);

        return result;
    }

}
