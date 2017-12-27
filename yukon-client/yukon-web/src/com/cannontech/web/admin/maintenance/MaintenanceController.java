package com.cannontech.web.admin.maintenance;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

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
import com.cannontech.maintenance.MaintenanceHelper;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingEditorDao;
import com.cannontech.system.dao.GlobalSettingUpdateDao;
import com.cannontech.system.model.GlobalSetting;
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
import com.cannontech.web.support.MappedPropertiesHelper;
import com.cannontech.web.support.MappedPropertiesHelper.MappableProperty;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
    @Autowired private GlobalSettingUpdateDao globalSettingUpdateDao;
    @Autowired private GlobalSettingEditorDao globalSettingEditorDao;
    @Autowired private MaintenanceHelper maintenanceHelper;
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

    @Autowired private YukonUserContextMessageSourceResolver resolver;
    private final static String RPH_DUPLICATE_CRON = "0 0 21 ? * *"; // every night at 9:00pm
    private final static String RPH_DANGLING_CRON = "0 15 21 ? * *"; // every night at 9:15pm
    private final static String SYSTEM_LOG_DANGLING_CRON = "0 30 21 ? * *"; // every night at 9:30pm
    private final static String ESTIMATED_LOAD_UPDATE_CRON = "0 0 * * * ? *"; //every hour
    private final static String WEATHER_DATA_UPDATE_CRON = "0 0/10 * * * ? *"; //every 10 minutes
    private final static String EXPORT_HISTORY_UPDATE_CRON = "0 45 21 ? * *"; // every night at 9:45pm
    private final static String SP_SMART_INDEX_MAINT_UPDATE_CRON = "0 0 22 ? * SAT"; // every saturday at 10:00pm
    
    private MappedPropertiesHelper<GlobalSetting> getHelper(Set<GlobalSettingType> all) {
        Map<GlobalSettingType, GlobalSetting> settings = globalSettingEditorDao.getSettings(all);
        MappedPropertiesHelper<GlobalSetting> mappedPropertiesHelper = new MappedPropertiesHelper<>("values");
        for (GlobalSetting setting : settings.values()) {
            GlobalSettingType type = setting.getType();
            mappedPropertiesHelper.add(type.name(), setting, type.getType());
        }
        MappedPropertiesHelper<GlobalSetting> result = mappedPropertiesHelper;
        return result;
    }
    @RequestMapping("view")
    public String view(ModelMap model, YukonUserContext userContext) throws ExecutionException{
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
        setUpModelForPointDataPruning(model, userContext);
        return "maintenance/home.jsp";
    }

    @RequestMapping(value = "edit", method = RequestMethod.GET)
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

    private void setUpModelForPointDataPruning(ModelMap model, YukonUserContext context) throws ExecutionException {
        List<MaintenanceTask> tasks = maintenanceTaskDao.getMaintenanceTasks(false);
        model.addAttribute("tasks", tasks);
        final MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
       Set<GlobalSettingType> all = maintenanceHelper.getGlobalSettingsForMaintenance();
        MappedPropertiesHelper<GlobalSetting> mappedPropertiesHelper = getHelper(all);
        Comparator<MappedPropertiesHelper.MappableProperty<GlobalSetting, ?>> comparator =
            new Comparator<MappedPropertiesHelper.MappableProperty<GlobalSetting, ?>>() {
                @Override
                public int compare(MappedPropertiesHelper.MappableProperty<GlobalSetting, ?> o1,
                        MappedPropertiesHelper.MappableProperty<GlobalSetting, ?> o2) {
                    String o1Text = accessor.getMessage(o1.getExtra().getType().getFormatKey());
                    String o2Text = accessor.getMessage(o2.getExtra().getType().getFormatKey());
                    return o1Text.compareToIgnoreCase(o2Text);
                }
            };

        Collections.sort(mappedPropertiesHelper.getMappableProperties(), comparator);
        model.addAttribute("mappedPropertiesHelper", mappedPropertiesHelper);
        ExclusionSettingsBean command = new ExclusionSettingsBean();
        Map<GlobalSettingType, Pair<Object, String>> settings = maintenanceTaskDao.getValuesAndComments();
        command.setValues(Maps.transformValues(settings, new Function<Pair<Object, String>, Object>() {
            @Override
            public Object apply(Pair<Object, String> input) {
                return input.getLeft();
            }
        }));
        command.setComments(Maps.transformValues(settings, new Function<Pair<Object, String>, String>() {
            @Override
            public String apply(Pair<Object, String> input) {
                return input.getRight();
            }
        }));
        model.addAttribute("command", command);
    }

    @RequestMapping(value = "updateMaintenanceSettings", method = RequestMethod.POST, params = "save")
    public String save(HttpServletRequest request,
            @ModelAttribute("exclusionSettingsBean") ExclusionSettingsBean exclusionSettingsBean, BindingResult result,
            YukonUserContext context, ModelMap map, FlashScope flash) throws Exception {

        List<GlobalSetting> settings = Lists.newArrayList(adjustSettings(exclusionSettingsBean));
        try {
            globalSettingUpdateDao.updateSettings(settings, context.getYukonUser());
        } catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.maintenance.updateFailed"));
            return "redirect:view";
        }
        flash.setConfirm(
            new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.maintenance.exclusionHoursSettingsUpdated"));

        return "redirect:view";
    }

    private List<GlobalSetting> adjustSettings(final ExclusionSettingsBean command) throws ExecutionException {
        Set<GlobalSettingType> all = maintenanceHelper.getGlobalSettingsForMaintenance();
        MappedPropertiesHelper<GlobalSetting> helper = getHelper(all);
        List<GlobalSetting> settings = Lists.transform(helper.getMappableProperties(),
            new Function<MappableProperty<GlobalSetting, ?>, GlobalSetting>() {
                @Override
                public GlobalSetting apply(MappableProperty<GlobalSetting, ?> input) {
                    GlobalSetting setting = input.getExtra();
                    if (setting.getType() == GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME
                        || (setting.getType() == GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME)) {
                        String[] values = (String[]) command.getValues().get(setting.getType());
                        setting.setValue(StringUtils.join(values, ","));
                    } else {
                        setting.setValue(command.getValues().get(setting.getType()));
                    }
                    
                    return setting;
                }
            });

        return settings;
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
    public String editTask(ModelMap model, YukonUserContext userContext, int taskId, FlashScope flashScope,
            String taskName) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        MaintenanceTaskType maintenanceTaskType = MaintenanceTaskType.valueOf(taskName);
        MaintenanceTask taskDetails = maintenanceTaskDao.getMaintenanceTask(maintenanceTaskType);
        List<MaintenanceSetting> settings = maintenanceTaskDao.getSettingsForMaintenanceTaskType(taskDetails.getTaskName());
        
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
        private String nextRun;
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

        public String getNextRun() {
            return nextRun;
        }

        public void setNextRun(String nextRun) {
            this.nextRun = nextRun;
        }

    }

    public static class ExclusionSettingsBean {
        private Map<GlobalSettingType, Object> values = Maps.newLinkedHashMap();
        private Map<GlobalSettingType, String> comments = Maps.newLinkedHashMap();
        public Map<GlobalSettingType, Object> getValues() {
            return values;
        }

        public void setValues(Map<GlobalSettingType, Object> values) {
            this.values = values;
        }

        public Map<GlobalSettingType, String> getComments() {
            return comments;
        }

        public void setComments(Map<GlobalSettingType, String> comments) {
            this.comments = comments;
        }
        
        
    }

}