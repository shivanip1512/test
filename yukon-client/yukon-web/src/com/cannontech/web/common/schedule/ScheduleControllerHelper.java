package com.cannontech.web.common.schedule;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.user.YukonUserContext;

public class ScheduleControllerHelper {

    private static final String baseKey = "yukon.web.modules.tools.schedules.VIEW.results.jobDetail.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    @Autowired private JobManager jobManager;
    @Autowired private JobStatusDao jobStatusDao;
    private static Logger log = YukonLogManager.getLogger(ScheduleControllerHelper.class);

    /**
     * Starts the job with the specified Job ID.
     */
    public Map<String, Object> startJob(int jobId, String cronExpression, YukonUserContext userContext) {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        if (job.isDeleted()) {
            json.put("error", messageSourceAccessor.getMessage(baseKey + "error.editDeletedJob"));
            return json;
        }
        try {
            jobManager.startJob(job, cronExpression);
        } catch (ScheduleException e) {
            log.error(e.getMessage());
            json.put("error", messageSourceAccessor.getMessage("yukon.common.device.schedules.home.pastDate"));
        }
        return json;
    }

    /**
     * Toggles Job status. If disabled, make enabled. If enabled, make disabled.
     */
    public Map<String, Object> toggleJob(int jobId, YukonUserContext userContext) throws ServletException {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        if (job.isDeleted()) {
            json.put("error", messageSourceAccessor.getMessage(baseKey + "error.editDeletedJob"));
            return json;
        }
        jobManager.toggleJobStatus(job);
        return null;
    }

    /**
     * Return the state of job with the specified jobId.
     */
    public JobState getJobState(int jobId) {
        JobStatus<YukonJob> status = jobStatusDao.findLatestStatusByJobId(jobId);
        JobDisabledStatus jobDisabledStatus = jobManager.getJobDisabledStatus(jobId);
        JobState jobState = JobState.of(jobDisabledStatus, status);
        return jobState;
    }

}
