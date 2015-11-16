package com.cannontech.web.updater.job.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.web.updater.job.JobUpdaterTypeEnum;

public class JobStatusClassUpdaterHandler implements JobUpdaterHandler {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private JobStatusDao jobStatusDao;
    @Autowired private JobManager jobManager;

    @Override
    public String handle(int jobId, YukonUserContext userContext) {

        JobStatus<YukonJob> jobStatus = jobStatusDao.findLatestStatusByJobId(jobId);
        JobDisabledStatus jobDisabledStatus = jobManager.getJobDisabledStatus(jobId);
        JobState jobState = JobState.of(jobDisabledStatus, jobStatus);
        if (jobState == JobState.RUNNING) {
            return "success";
        } else if (jobState == JobState.DISABLED) {
            return "error";
        }
        return "";
    }

    @Override
    public JobUpdaterTypeEnum getUpdaterType() {
        return JobUpdaterTypeEnum.JOB_STATUS_CLASS;
    }

}