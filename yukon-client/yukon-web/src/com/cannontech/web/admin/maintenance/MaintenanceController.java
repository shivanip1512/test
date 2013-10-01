package com.cannontech.web.admin.maintenance;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.scheduledRphDanglingEntriesDeletionExecutionTask.tasks.ScheduledRphDanglingEntriesDeletionExecutionTask;
import com.cannontech.amr.scheduledRphDuplicateDeletionExecution.tasks.ScheduledRphDuplicateDeletionExecutionTask;
import com.cannontech.amr.scheduledSystemLogDanglingEntriesDeletionExecutionTask.tasks.ScheduledSystemLogDanglingEntriesDeletionExecutionTask;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.loadcontrol.tasks.RepeatingEstimatedLoadTask;
import com.cannontech.web.loadcontrol.tasks.RepeatingWeatherDataTask;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/maintenance/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class MaintenanceController {

    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private JobManager jobManager;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ConfigurationSource configurationSource;

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

    private final static String RPH_DUPLICATE_CRON = "0 0 21 ? * *"; // every night at 9:00pm
    private final static String RPH_DANGLING_CRON = "0 15 21 ? * *"; // every night at 9:15pm
    private final static String SYSTEM_LOG_DANGLING_CRON = "0 30 21 ? * *"; // every night at 9:30pm
    private final static String ESTIMATED_LOAD_UPDATE_CRON = "0 0 * * * ? *"; //every hour
    private final static String WEATHER_DATA_UPDATE_CRON = "0 0/10 * * * ? *"; //every 10 minutes

    @RequestMapping
    public String view(ModelMap model, YukonUserContext userContext) {
        List<ScheduledRepeatingJob> jobs = Lists.newArrayList();
        jobs.add(getJob(userContext, rphDuplicateJobDef, RPH_DUPLICATE_CRON));
        jobs.add(getJob(userContext, rphDanglingEntriesJobDef, RPH_DANGLING_CRON));
        jobs.add(getJob(userContext, systemLogDanglingEntriesJobDef, SYSTEM_LOG_DANGLING_CRON));
        jobs.add(getJob(userContext, weatherDataJobDef, WEATHER_DATA_UPDATE_CRON));
        if (configurationSource.getBoolean(MasterConfigBooleanKeysEnum.ENABLE_ESTIMATED_LOAD, false)) {
            jobs.add(getJob(userContext, estimatedLoadDataJobDef, ESTIMATED_LOAD_UPDATE_CRON));
        }

        model.addAttribute("jobs", jobs);
        return "maintenance/home.jsp";
    }

    @RequestMapping
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

    @RequestMapping
    public String update(ModelMap model, YukonUserContext userContext, HttpServletRequest request,
                         FlashScope flashScope, int jobId, String cronUniqueId) {
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        String cronExpression;
        try {
            cronExpression = cronExpressionTagService.build(cronUniqueId, request, userContext);
        } catch (Exception e) {
            MessageSourceResolvable invalidCronMsg =
                new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.maintenance.invalidCron");
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

    @RequestMapping
    public String toggleJobEnabled(int jobId) {
        toggle(jobId);
        return "redirect:view";
    }

    @RequestMapping
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
        job.setJobDefinition(jobDefinition);
        job.setJobProperties(new HashMap<String,String>());
        scheduledRepeatingJobDao.save(job);
        jobManager.instantiateTask(job);
        return job;
    }

}