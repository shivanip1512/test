package com.cannontech.web.admin.maintenance;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.fileExportHistory.task.RepeatingExportHistoryDeletionTask;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.maintenance.MaintenanceTaskName;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;
import com.cannontech.system.model.MaintenanceSetting;
import com.cannontech.system.model.MaintenanceTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.loadcontrol.tasks.RepeatingEstimatedLoadTask;
import com.cannontech.web.maintenance.tasks.RepeatingWeatherDataTask;
import com.cannontech.web.maintenance.tasks.ScheduledRphDanglingEntriesDeletionExecutionTask;
import com.cannontech.web.maintenance.tasks.ScheduledRphDuplicateDeletionExecutionTask;
import com.cannontech.web.maintenance.tasks.ScheduledSmartIndexMaintenanceExecutionTask;
import com.cannontech.web.maintenance.tasks.ScheduledSystemLogDanglingEntriesDeletionExecutionTask;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/maintenance/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class MaintenanceController {

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private DatabaseVendorResolver dbVendorResolver;
    @Autowired private JobManager jobManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private MaintenanceTaskDao maintenanceTaskDao;

    @Autowired @Qualifier("rphDuplicateDeletion")
        private YukonJobDefinition<ScheduledRphDuplicateDeletionExecutionTask> rphDuplicateJobDef;
    @Autowired @Qualifier("rphDanglingDeletion")
        private YukonJobDefinition<ScheduledRphDanglingEntriesDeletionExecutionTask> rphDanglingEntriesJobDef;
    @Autowired @Qualifier("systemLogDanglingDeletion")
        private YukonJobDefinition<ScheduledSystemLogDanglingEntriesDeletionExecutionTask> systemLogDanglingEntriesJobDef;
    @Autowired @Qualifier("estimatedLoadData")
        private YukonJobDefinition<RepeatingEstimatedLoadTask> estimatedLoadDataJobDef;
    @Autowired @Qualifier("weatherData")
        private YukonJobDefinition<RepeatingWeatherDataTask> weatherDataJobDef;
    @Autowired @Qualifier("exportHistoryDeletion")
        private YukonJobDefinition<RepeatingExportHistoryDeletionTask> exportHistoryJobDef;
    @Autowired @Qualifier("spSmartIndexMaintanence")
        private YukonJobDefinition<ScheduledSmartIndexMaintenanceExecutionTask> spSmartIndexMaintanenceJobDef;
    
    private final static String RPH_DUPLICATE_CRON = "0 0 21 ? * *"; // every night at 9:00pm
    private final static String RPH_DANGLING_CRON = "0 15 21 ? * *"; // every night at 9:15pm
    private final static String SYSTEM_LOG_DANGLING_CRON = "0 30 21 ? * *"; // every night at 9:30pm
    private final static String ESTIMATED_LOAD_UPDATE_CRON = "0 0 * * * ? *"; //every hour
    private final static String WEATHER_DATA_UPDATE_CRON = "0 0/10 * * * ? *"; //every 10 minutes
    private final static String EXPORT_HISTORY_UPDATE_CRON = "0 45 21 ? * *"; // every night at 9:45pm
    private final static String SP_SMART_INDEX_MAINT_UPDATE_CRON = "0 0 22 ? * SAT"; // every saturday at 10:00pm
    
    @RequestMapping("view")
    public String view(ModelMap model, YukonUserContext userContext) {
        List<ScheduledRepeatingJob> jobs = Lists.newArrayList();
        jobs.add(getJob(userContext, rphDuplicateJobDef, RPH_DUPLICATE_CRON));
        jobs.add(getJob(userContext, rphDanglingEntriesJobDef, RPH_DANGLING_CRON));
        jobs.add(getJob(userContext, systemLogDanglingEntriesJobDef, SYSTEM_LOG_DANGLING_CRON));
        if (dbVendorResolver.getDatabaseVendor().isSqlServer()) {
            jobs.add(getJob(userContext, spSmartIndexMaintanenceJobDef, SP_SMART_INDEX_MAINT_UPDATE_CRON));
        }
        jobs.add(getJob(userContext, weatherDataJobDef, WEATHER_DATA_UPDATE_CRON));
        jobs.add(getJob(userContext, exportHistoryJobDef, EXPORT_HISTORY_UPDATE_CRON));
        if (configurationSource.getBoolean(MasterConfigBoolean.ENABLE_ESTIMATED_LOAD, false)) {
            jobs.add(getJob(userContext, estimatedLoadDataJobDef, ESTIMATED_LOAD_UPDATE_CRON));
        }

        model.addAttribute("jobs", jobs);
        setUpModelForPointDataPruning(model);
        return "maintenance/home.jsp";
    }

    @RequestMapping("edit")
    public String edit(ModelMap model, YukonUserContext userContext, int jobId) {
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        CronExpressionTagState expressionTagState = cronExpressionTagService.parse(job.getCronString(), job.getUserContext());
        model.addAttribute("expressionTagState", expressionTagState);
        model.addAttribute("job", job);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String crumb = messageSourceAccessor.getMessage("yukon.web.modules.adminSetup.maintenance."+job.getBeanName()+ ".title");
        model.addAttribute("jobNameMsg", crumb);
        return "maintenance/edit.jsp";
    }

    @RequestMapping("update")
    public String update(ModelMap model, YukonUserContext userContext, HttpServletRequest request,
                         FlashScope flashScope, int jobId, String cronUniqueId) {
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        String cronExpression;
        try {
            cronExpression = cronExpressionTagService.build(cronUniqueId, request, userContext);
        } catch (Exception e) {
            MessageSourceResolvable invalidCronMsg =
                new YukonMessageSourceResolvable("yukon.common.invalidCron");
            flashScope.setError(invalidCronMsg);
            return edit(model, userContext, jobId);
        }
        if (job.isDisabled()) {
            job.setCronString(cronExpression);
            scheduledRepeatingJobDao.update(job);
        } else {
            jobManager.replaceScheduledJob(jobId,
                                           job.getJobDefinition(),
                                           job.getJobDefinition().createBean(),
                                           cronExpression,
                                           userContext);
        }
        return "redirect:view";
    }

    @RequestMapping("toggleJobEnabled")
    public String toggleJobEnabled(int jobId) {
        toggle(jobId);
        return "redirect:view";
    }

    @RequestMapping("toggleJobEnabledAjax")
    public @ResponseBody Boolean toggleJobEnabledAjax(int jobId) {
        return toggle(jobId);
    }

    private boolean toggle(int jobId) {
        boolean isEnabled = true;
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        if (job.isDisabled()) {
            jobManager.enableJob(job);
        } else {
            jobManager.disableJob(job);
            isEnabled = false;
        }
        return isEnabled;
    }

    private ScheduledRepeatingJob getJob(YukonUserContext userContext, YukonJobDefinition<? extends YukonTask> jobDefinition, String cronString) {
        List<ScheduledRepeatingJob> jobsNotDeleted = jobManager
                .getNotDeletedRepeatingJobsByDefinition(jobDefinition);
        ScheduledRepeatingJob repeatingJob;
        if (jobsNotDeleted == null || jobsNotDeleted.isEmpty()) {
            // If this page is being accessed for the first time
            repeatingJob = createNewDefaultJob(userContext, jobDefinition, cronString);
        } else {
            // There should only ever be one job in the database that is not deleted (enabled or disabled)
            repeatingJob = Iterables.getOnlyElement(jobsNotDeleted);
        }
        return repeatingJob;
    }

    private ScheduledRepeatingJob createNewDefaultJob(YukonUserContext userContext, YukonJobDefinition<? extends YukonTask> jobDefinition, String cronString) {
        ScheduledRepeatingJob job = new ScheduledRepeatingJob();
        job.setBeanName(jobDefinition.getName());
        job.setCronString(cronString);
        job.setDisabled(true);
        job.setUserContext(userContext);
        job.setJobGroupId(nextValueHelper.getNextValue("Job"));
        job.setJobDefinition(jobDefinition);
        job.setJobProperties(new HashMap<String,String>());
        scheduledRepeatingJobDao.save(job);
        jobManager.instantiateTask(job);
        return job;
    }

    private void setUpModelForPointDataPruning(ModelMap model) {
        List<MaintenanceTask> tasks = maintenanceTaskDao.getMaintenanceTasks(false);
        model.addAttribute("tasks", tasks);
    }

    @RequestMapping(value = "toggleDataPruningJobEnabled", method = RequestMethod.GET)
    public String toggleDataPruningJobEnabled(int taskId) {
        toggleDataPruningJob(taskId);
        return "redirect:view";
    }

    private boolean toggleDataPruningJob(int taskId) {
        boolean isEnabled = true;
        MaintenanceTask job = maintenanceTaskDao.getMaintenanceTaskById(taskId);
        if (!job.isDisabled()) {
            isEnabled = false;
        }
        job.setDisabled(!job.isDisabled());
        maintenanceTaskDao.updateTaskStatus(job);
        return isEnabled;
    }

    @RequestMapping(value = "editTask", method = RequestMethod.GET)
    public String editTask(ModelMap model, YukonUserContext userContext, int taskId, String taskName) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        MaintenanceTaskName maintenanceTaskName = MaintenanceTaskName.valueOf(taskName);
        MaintenanceTask taskDetails = maintenanceTaskDao.getMaintenanceTask(maintenanceTaskName);
        List<MaintenanceSetting> settings = maintenanceTaskDao.getSettingsForMaintenanceTaskName(maintenanceTaskName);
        MaintenanceEditorBean maintenanceEditorBean = new MaintenanceEditorBean();
        maintenanceEditorBean.setTaskDetails(taskDetails);
        maintenanceEditorBean.setSettings(settings);
        model.addAttribute("maintenanceEditorBean", maintenanceEditorBean);
        String taskNameMsg = messageSourceAccessor.getMessage("yukon.web.modules.adminSetup.maintenance."
            + maintenanceEditorBean.getTaskDetails().getTaskName() + ".title");
        model.addAttribute("taskNameMsg", taskNameMsg);
        return "maintenance/editTask.jsp";
    }

    @RequestMapping(value = "updateTask", method = RequestMethod.POST)
    public String save(@ModelAttribute("maintenanceEditorBean") MaintenanceEditorBean maintenanceEditorBean,
            BindingResult result, FlashScope flash, YukonUserContext userContext) {
        maintenanceTaskDao.updateSettings(maintenanceEditorBean.getSettings());
        return "redirect:view";
    }

    public static class MaintenanceEditorBean {

        private MaintenanceTask taskDetails;
        private List<MaintenanceSetting> settings = Lists.newArrayList();

        public List<MaintenanceSetting> getSettings() {
            return settings;
        }

        public void setSettings(List<MaintenanceSetting> settings) {
            this.settings = settings;
        }

        public MaintenanceTask getTaskDetails() {
            return taskDetails;
        }

        public void setTaskDetails(MaintenanceTask taskDetails) {
            this.taskDetails = taskDetails;
        }

    }

}