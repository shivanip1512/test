package com.cannontech.web.updater.job.handler;


import java.text.ParseException;
import java.util.Date;

import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionStatusService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.job.JobUpdaterTypeEnum;

public class StateTextJobUpdaterHandler implements JobUpdaterHandler {

    @Autowired private ScheduledGroupRequestExecutionStatusService executionStatusService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;

    @Override
    public String handle(int jobId, YukonUserContext userContext) {

        ScheduledGroupRequestExecutionStatus state = executionStatusService.getStatus(jobId);
        ScheduledRepeatingJob job = null;
        try {
            job = scheduledRepeatingJobDao.getById(jobId);
            CronExpression cronExpression = new CronExpression(job.getCronString());
            if (cronExpression.getNextValidTimeAfter(new Date()) == null) {
                // The status MANUAL is only used for display
                state = ScheduledGroupRequestExecutionStatus.MANUAL;
            }
        } catch (EmptyResultDataAccessException | ParseException e) {
            // ignore, this job is a ScheduledOneTimeJob
        }
        
        System.out.println(messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(state));

        return messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(state);
    }

    @Override
    public JobUpdaterTypeEnum getUpdaterType() {
        return JobUpdaterTypeEnum.STATE_TEXT;
    }
}
