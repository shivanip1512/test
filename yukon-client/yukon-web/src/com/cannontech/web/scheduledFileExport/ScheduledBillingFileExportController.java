package com.cannontech.web.scheduledFileExport;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.scheduledFileExport.BillingFileExportGenerationParameters;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledBillingFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledFileExportTask;
import com.cannontech.web.scheduledFileExport.validator.ScheduledBillingFileExportValidator;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/*")
@CheckRole(YukonRole.APPLICATION_BILLING)
public class ScheduledBillingFileExportController {
	@Autowired JobManager jobManager;
	@Autowired ScheduledFileExportService scheduledFileExportService;
	@Autowired DeviceGroupService deviceGroupService;
	@Autowired ScheduledBillingFileExportValidator scheduledBillingFileExportValidator;
	@Autowired private CronExpressionTagService cronExpressionTagService;
	
	private static final int MAX_GROUPS_DISPLAYED = 2;
	
	@RequestMapping
	public String showForm(ModelMap model, YukonUserContext userContext, HttpServletRequest request,
			@ModelAttribute("exportData") ScheduledFileExportData exportData, Integer jobId, Integer fileFormat, 
			Integer demandDays, Integer energyDays, @RequestParam(defaultValue="false") boolean removeMultiplier,
			@RequestParam(defaultValue="null") String[] billGroup) throws ServletRequestBindingException {
		
		if(billGroup == null) billGroup = new String[0];
		
		CronExpressionTagState cronExpressionTagState = new CronExpressionTagState();
		
		if(jobId != null) {
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
			exportData.setExportPath(task.getExportPath());
			exportData.setAppendDateToFileName(task.isAppendDateToFileName());
			
			exportData.setNotificationEmailAddresses(task.getNotificationEmailAddresses());
			cronExpressionTagState = cronExpressionTagService.parse(job.getCronString(), job.getUserContext());
			model.addAttribute("jobId", jobId);
		}
		
		model.addAttribute("deviceGroups", billGroup);
		model.addAttribute("fileFormat", fileFormat);
		model.addAttribute("demandDays", demandDays);
		model.addAttribute("energyDays", energyDays);
		model.addAttribute("removeMultiplier", removeMultiplier);
		model.addAttribute("cronExpressionTagState", cronExpressionTagState);
		
		Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(Arrays.asList(billGroup));
		String groupNames = getGroupNamesString(deviceGroups, userContext);
		model.addAttribute("groupNames", groupNames);
		
		String formatName = FileFormatTypes.getFormatType(fileFormat);
		model.addAttribute("formatName", formatName);
		
		return "schedule.jsp";
	}
	
	@RequestMapping
	public String scheduleExport(ModelMap model, YukonUserContext userContext, HttpServletRequest request, FlashScope flashScope,
			@ModelAttribute("exportData") ScheduledFileExportData exportData, BindingResult bindingResult,
			String[] deviceGroups, int fileFormat, int demandDays, int energyDays, boolean removeMultiplier, Integer jobId) {
		
		String scheduleCronString = null;
		try {
			scheduleCronString = cronExpressionTagService.build("scheduleCronString", request, userContext);
		} catch(Exception e) {
			flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.amr.billing.schedule.invalidCron"));
			if(jobId == null) {
				repopulateScheduleModel(model, deviceGroups, fileFormat, demandDays, energyDays, removeMultiplier);
			} else {
				model.addAttribute("jobId", jobId);
			}
			return "redirect:showForm";
		}
		exportData.setScheduleCronString(scheduleCronString);
		
		Set<? extends DeviceGroup> fullDeviceGroups = deviceGroupService.resolveGroupNames(Arrays.asList(deviceGroups));
		
		BillingFileExportGenerationParameters billingParameters = new BillingFileExportGenerationParameters(fileFormat, fullDeviceGroups, demandDays, energyDays, removeMultiplier);
		exportData.setParameters(billingParameters);
		
		scheduledBillingFileExportValidator.validate(exportData, bindingResult);
		if(bindingResult.hasErrors()) {
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(messages);
            model.addAttribute("deviceGroups", deviceGroups);
    		model.addAttribute("fileFormat", fileFormat);
    		model.addAttribute("demandDays", demandDays);
    		model.addAttribute("energyDays", energyDays);
    		model.addAttribute("removeMultiplier", removeMultiplier);
            model.addAttribute("jobId", jobId);
            return "redirect:showForm";
		}
		
		if(jobId == null) {
			//new schedule
			scheduledFileExportService.scheduleFileExport(exportData, userContext);
			flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.billing.jobs.jobCreated", exportData.getScheduleName()));
		} else {
			//edit schedule
			scheduledFileExportService.updateFileExport(exportData, userContext, jobId);
			flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.billing.jobs.jobUpdated", exportData.getScheduleName()));
		}
		
		return "redirect:jobs";
	}
	
	@RequestMapping
	public String jobs(ModelMap model, @RequestParam(defaultValue="25") int itemsPerPage,
			@RequestParam(defaultValue="1") int page) {
		List<ScheduledRepeatingJob> billingExportJobs = scheduledFileExportService.getBillingExportJobs();
		List<ScheduledBillingJobData> jobDataObjects = Lists.newArrayListWithCapacity(billingExportJobs.size());
		
		int startIndex = (page -1) * itemsPerPage;
		
		for(ScheduledRepeatingJob job : billingExportJobs) {
			jobDataObjects.add(scheduledFileExportService.getBillingJobData(job));
		}
		Collections.sort(jobDataObjects);
		int endIndex = startIndex + itemsPerPage > billingExportJobs.size() ? billingExportJobs.size() : startIndex + itemsPerPage;
		jobDataObjects = jobDataObjects.subList(startIndex, endIndex);
		
		SearchResult<ScheduledBillingJobData> filterResult = new SearchResult<ScheduledBillingJobData>();
	    filterResult.setBounds(startIndex, itemsPerPage, billingExportJobs.size());
		filterResult.setResultList(jobDataObjects);
        model.addAttribute("filterResult", filterResult);
		
		return "jobs.jsp";
	}
	
	@RequestMapping
	public String delete(ModelMap model, int jobId, FlashScope flashScope) {
		YukonJob job = jobManager.getJob(jobId);
		ScheduledFileExportTask task = (ScheduledFileExportTask) jobManager.instantiateTask(job);
		String jobName = task.getName();
		jobManager.deleteJob(job);
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.amr.billing.jobs.deletedSuccess", jobName));
		return "redirect:jobs";
	}
	
	private String getGroupNamesString(Set<? extends DeviceGroup> deviceGroups, YukonUserContext userContext) {
		String groupNames = "";
		int maxDisplayedGroupNames = deviceGroups.size() > MAX_GROUPS_DISPLAYED ? MAX_GROUPS_DISPLAYED : deviceGroups.size();
		Iterator<? extends DeviceGroup> iterator = deviceGroups.iterator();
		for(int i = 0; i < maxDisplayedGroupNames; i++) {
			DeviceGroup group = iterator.next();
			if(i > 0) {
				groupNames += ", ";
			}
			groupNames += group.getName(userContext, "");
		}
		
		if(maxDisplayedGroupNames < deviceGroups.size()) {
			groupNames += "...";
		}
		
		return groupNames;
	}
	
	private void repopulateScheduleModel(ModelMap model, String[] billGroup, int fileFormat, int demandDays, int energyDays, boolean removeMultiplier) {
		model.addAttribute("billGroup", billGroup);
		model.addAttribute("fileFormat", fileFormat);
		model.addAttribute("demandDays", demandDays);
		model.addAttribute("energyDays", energyDays);
		model.addAttribute("removeMultiplier", removeMultiplier);
	}
}
