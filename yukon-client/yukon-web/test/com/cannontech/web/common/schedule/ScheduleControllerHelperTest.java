package com.cannontech.web.common.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import org.easymock.EasyMock;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolverMock;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobRunStatus;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;

public class ScheduleControllerHelperTest {

    private ScheduleControllerHelper scheduleControllerHelperTest;
    private ScheduledRepeatingJob job;
    private YukonUserContext context;
    private JobStatusDao jobStatusDao;
    private JobStatus<YukonJob> jobStatus;
    private JobManager jobManager;
    private ScheduledRepeatingJobDao scheduledRepeatingJobDao;

    private void setMockData(ScheduledRepeatingJob scheduledRepeatingJob) throws Exception {
        job = scheduledRepeatingJob;
        job.setId(911);
        job.setCronString("0 1 * * *");
        scheduleControllerHelperTest = new ScheduleControllerHelper();
        jobManager = EasyMock.createMock(JobManager.class);
        LiteYukonUser user2 = new LiteYukonUser(-1);
        context = new SimpleYukonUserContext(user2, Locale.US, DateTimeZone.forID("GMT").toTimeZone(), "");
        job.setUserContext(context);
        jobStatus = new JobStatus<YukonJob>();
        jobStatus.setId(911);
        jobStatus.setJob(job);
        jobStatus.setJobRunStatus(JobRunStatus.STARTED);
        jobStatusDao = EasyMock.createMock(JobStatusDao.class);
        scheduledRepeatingJobDao = EasyMock.createMock(ScheduledRepeatingJobDao.class);
        ReflectionTestUtils.setField(scheduleControllerHelperTest, "jobStatusDao", jobStatusDao);
        ReflectionTestUtils.setField(scheduleControllerHelperTest, "scheduledRepeatingJobDao",
            scheduledRepeatingJobDao);
        EasyMock.expect(jobStatusDao.findLatestStatusByJobId(911)).andReturn(jobStatus);
        EasyMock.replay(jobStatusDao);
        EasyMock.expect(scheduledRepeatingJobDao.getById(911)).andReturn(job);
        EasyMock.replay(scheduledRepeatingJobDao);
        ReflectionTestUtils.setField(scheduleControllerHelperTest, "jobManager", jobManager);
        EasyMock.expect(jobManager.getJobDisabledStatus(job.getId())).andReturn(JobDisabledStatus.N);
        EasyMock.expect(jobManager.getRepeatingJob(job.getId())).andReturn(job);
        jobManager.startJob(job, job.getCronString());
        EasyMock.expect(jobManager.toggleJobStatus(job)).andReturn(true);
        EasyMock.replay(jobManager);
        StaticMessageSource messageSource = new StaticMessageSource();
        {
            messageSource.addMessage("yukon.web.modules.tools.schedules.VIEW.results.jobDetail.error.editDeletedJob",
                Locale.US, "Edit Deleted Job");
        }
        YukonUserContextMessageSourceResolverMock messageResolver = new YukonUserContextMessageSourceResolverMock();
        {
            messageResolver.setMessageSource(messageSource);
        }
        ReflectionTestUtils.setField(scheduleControllerHelperTest, "messageResolver", messageResolver);

    }

    private ScheduledRepeatingJob getJob() {
        return new ScheduledRepeatingJob();
    }

    private ScheduledRepeatingJob getDeletedJob() {
        ScheduledRepeatingJob job = new ScheduledRepeatingJob();
        job.setDeleted(true);
        return job;
    }

    @Test
    public void test_getJobState() throws Exception {
        setMockData(getJob());
        JobState state = scheduleControllerHelperTest.getJobState(job.getId());
        assertThat(state, equalTo(JobState.RUNNING));
    }

    @Test
    public void test_startJob() throws Exception {
        setMockData(getJob());
        Map<String, Object> json =
            scheduleControllerHelperTest.startJob(job.getId(), job.getCronString(), job.getUserContext());
        assertThat(json.size(), equalTo(0));
    }

    @Test
    public void test_startJob_Deleted() throws Exception {
        setMockData(getDeletedJob());
        Map<String, Object> json =
            scheduleControllerHelperTest.startJob(job.getId(), job.getCronString(), job.getUserContext());
        assertThat(json.get("error"), equalTo("Edit Deleted Job"));
    }

    @Test
    public void test_toggleJob() throws Exception {
        setMockData(getJob());
        Map<String, Object> json = null;
        try {
            json = scheduleControllerHelperTest.toggleJob(job.getId(), job.getUserContext());
        } catch (ServletException e) {
            e.printStackTrace();
        }
        assertThat(json, equalTo(null));
    }

    @Test
    public void test_toggleJob_Deleted() throws Exception {
        setMockData(getDeletedJob());
        Map<String, Object> json = null;
        try {
            json = scheduleControllerHelperTest.toggleJob(job.getId(), job.getUserContext());
        } catch (ServletException e) {
            e.printStackTrace();
        }
        assertThat(json.get("error"), equalTo("Edit Deleted Job"));
    }

}
