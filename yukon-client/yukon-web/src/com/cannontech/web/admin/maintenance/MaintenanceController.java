package com.cannontech.web.admin.maintenance;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.scheduledRphDuplicateDeletionExecution.tasks.ScheduledRphDuplicateDeletionExecutionTask;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/maintenance/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class MaintenanceController {
    private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    private YukonJobDefinition<ScheduledRphDuplicateDeletionExecutionTask> scheduledRphDuplicateDeletionExecutionJobDefinition;
    private CronExpressionTagService cronExpressionTagService;
    private JobManager jobManager;
    
    @RequestMapping
    public String view(ModelMap model, YukonUserContext userContext) {
        ScheduledRepeatingJob job = getRphDuplicateDeletionJob(userContext);
        model.addAttribute("mode", PageEditMode.VIEW);
        setupPage(job, model, userContext);
        return "maintenance/home.jsp";
    }
    
    @RequestMapping
    public String edit(ModelMap model, YukonUserContext userContext) {
        ScheduledRepeatingJob job = getRphDuplicateDeletionJob(userContext);
        model.addAttribute("mode", PageEditMode.EDIT);
        setupPage(job, model, userContext);
        return "maintenance/home.jsp";
    }
    
    private ScheduledRepeatingJob setupPage(ScheduledRepeatingJob job, ModelMap model, YukonUserContext userContext) {
        model.addAttribute("job", job);
        CronExpressionTagState expressionTagState = cronExpressionTagService.parse(job.getCronString(), job.getUserContext());
        model.addAttribute("expressionTagState", expressionTagState);
        return job;
    }

    @RequestMapping
    public String update(ModelMap model, YukonUserContext userContext, HttpServletRequest request,
                         FlashScope flashScope, int jobId, String cronUniqueId) {
        ScheduledRepeatingJob job = getRphDuplicateDeletionJob(userContext);
        String cronExpression;
        try {
            cronExpression = cronExpressionTagService.build(cronUniqueId, request, userContext);
        } catch (Exception e) {
            setupPage(job, model, userContext);
            MessageSourceResolvable invalidCronMsg =
                new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.maintenance.invalidCron");
            flashScope.setError(invalidCronMsg);
            model.addAttribute("mode", PageEditMode.EDIT);
            return "maintenance/home.jsp";
        }
        if (job.isDisabled()) {
            job.setCronString(cronExpression);
            scheduledRepeatingJobDao.update(job);
        } else {
            jobManager.replaceScheduledJob(jobId,
                                           scheduledRphDuplicateDeletionExecutionJobDefinition,
                                           scheduledRphDuplicateDeletionExecutionJobDefinition
                                               .createBean(),
                                           cronExpression,
                                           userContext);
        }
        return "redirect:view";
    }
    
    @RequestMapping
    public String toggleJobEnabled(ModelMap model, int jobId, YukonUserContext userContext) throws ServletException {
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        if (job.isDisabled()) {
            jobManager.enableJob(job);
        } else {
            jobManager.disableJob(job);
        }
        return "redirect:edit";
    }
    
    private ScheduledRepeatingJob getRphDuplicateDeletionJob(YukonUserContext userContext) {
        Set<ScheduledRepeatingJob> repeatingJobs = scheduledRepeatingJobDao
                .getJobsByDefinition(scheduledRphDuplicateDeletionExecutionJobDefinition);
        ScheduledRepeatingJob repeatingJob;
        if (repeatingJobs == null || repeatingJobs.isEmpty()) {
            repeatingJob = createNewDefaultRphDuplicateJob(userContext);
        } else {
            List<ScheduledRepeatingJob> jobsNotDeleted = Lists.newArrayList();
            for (ScheduledRepeatingJob job: repeatingJobs) {
                if (!job.isDeleted()) {
                    jobsNotDeleted.add(job);
                }
            }
            repeatingJob = Iterables.getOnlyElement(jobsNotDeleted);
        }
        return repeatingJob;
    }
    
    private ScheduledRepeatingJob createNewDefaultRphDuplicateJob(YukonUserContext userContext) {
        ScheduledRepeatingJob job = new ScheduledRepeatingJob();
        job.setBeanName(scheduledRphDuplicateDeletionExecutionJobDefinition.getName());
        job.setCronString("0 0 21 ? * *"); //every night at 9:00pm
        job.setDisabled(true);
        job.setUserContext(userContext);
        job.setJobDefinition(scheduledRphDuplicateDeletionExecutionJobDefinition);
        job.setJobProperties(new HashMap<String,String>());
        scheduledRepeatingJobDao.save(job);
        jobManager.instantiateTask(job);
        return job;
    }

    @Autowired
    public void setScheduledRepeatingJobDao(ScheduledRepeatingJobDao scheduledRepeatingJobDao) {
        this.scheduledRepeatingJobDao = scheduledRepeatingJobDao;
    }
    @Resource(name = "scheduledRphDuplicateDeletionExecutionJobDefinition")
    public void setScheduledRphDuplicateDeletionExecutionJobDefinition(YukonJobDefinition<ScheduledRphDuplicateDeletionExecutionTask> scheduledRphDuplicateDeletionExecutionJobDefinition) {
        this.scheduledRphDuplicateDeletionExecutionJobDefinition =
            scheduledRphDuplicateDeletionExecutionJobDefinition;
    }
    @Autowired
    public void setCronExpressionTagService(CronExpressionTagService cronExpressionTagService) {
        this.cronExpressionTagService = cronExpressionTagService;
    }
    @Autowired
    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }
}