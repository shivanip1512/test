package com.cannontech.web.scheduledFileExport;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.scheduledFileExport.BillingFileExportGenerationParameters;
import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledBillingFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledFileExportTask;
import com.cannontech.web.scheduledFileExport.validator.ScheduledFileExportValidator;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@RequestMapping("/*")
@CheckRole(YukonRole.APPLICATION_BILLING)
public class ScheduledBillingFileExportController {

    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private JobManager jobManager;
    @Autowired private ScheduledFileExportService scheduledFileExportService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private ScheduledFileExportHelper exportHelper;

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
            exportData.setNotificationEmailAddresses(task.getNotificationEmailAddresses());
            cronExpressionTagState = cronExpressionTagService.parse(job.getCronString(), job.getUserContext());
            model.addAttribute("jobId", jobId);
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
        scheduledFileExportValidator.validate(exportData, bindingResult);
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        
        try {
            String scheduleCronString = cronExpressionTagService.build("scheduleCronString", request, userContext);
            exportData.setScheduleCronString(scheduleCronString);
        } catch (ServletRequestBindingException | IllegalArgumentException | ParseException e) {
            bindingResult.rejectValue("scheduleCronString", "yukon.common.invalidCron");
        }
        
        if (bindingResult.hasErrors()) {
            return JsonUtils.getErrorJson(bindingResult, accessor);
        }

        MessageSourceResolvable msgObj = null;

        if (jobId == null) {
            //new schedule
            scheduledFileExportService.scheduleFileExport(exportData, userContext, request);
            msgObj = new YukonMessageSourceResolvable("yukon.web.modules.amr.billing.jobs.jobCreated", exportData.getScheduleName());
        } else {
            //edit schedule
            scheduledFileExportService.updateFileExport(exportData, userContext, request, jobId);
            msgObj = new YukonMessageSourceResolvable("yukon.web.modules.amr.billing.jobs.jobUpdated", exportData.getScheduleName());
        }

        return JsonUtils.getSuccessJson(msgObj, accessor);
    }

    @RequestMapping("jobs")
    public String jobs(ModelMap model) {

        List<ScheduledFileExportJobData> jobs
            = scheduledFileExportService.getScheduledFileExportJobData(ScheduledExportType.BILLING);

        model.addAttribute("jobType", FileExportType.BILLING);
        model.addAttribute("jobs", jobs);
        
        return "jobs.jsp";
    }

    @RequestMapping(value = "delete.json")
    public @ResponseBody Map<String, Object> delete(int jobId, YukonUserContext userContext) {
        
        YukonJob job = jobManager.getJob(jobId);
        ScheduledFileExportTask task = (ScheduledFileExportTask) jobManager.instantiateTask(job);
        String jobName = task.getName();
        jobManager.deleteJob(job);

        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        MessageSourceResolvable msgObj = new YukonMessageSourceResolvable("yukon.web.modules.amr.billing.jobs.deletedSuccess", jobName);
        
        return JsonUtils.getSuccessJson(msgObj, accessor);
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

}
