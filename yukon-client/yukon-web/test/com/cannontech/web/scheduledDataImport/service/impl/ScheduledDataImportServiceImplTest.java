package com.cannontech.web.scheduledDataImport.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;

import org.easymock.EasyMock;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.scheduledFileImport.ScheduledDataImport;
import com.cannontech.common.scheduledFileImport.ScheduledImportType;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobRunStatus;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.common.schedule.ScheduleControllerHelper;
import com.cannontech.web.scheduledDataImport.tasks.ScheduledDataImportTask;

public class ScheduledDataImportServiceImplTest {

    private ScheduledDataImportServiceImpl scheduledDataImportServiceImplTest;
    private ScheduledRepeatingJob job;
    private ScheduledDataImportTask task;
    private YukonUserContext context;
    private JobStatus<YukonJob> jobStatus;
    private CronExpressionTagService cronExpressionTagService;
    private JobManager jobManager;
    private ScheduleControllerHelper scheduleControllerHelper;

    @BeforeEach
    public void setUp() throws Exception {
        scheduledDataImportServiceImplTest = new ScheduledDataImportServiceImpl();
        cronExpressionTagService = EasyMock.createMock(CronExpressionTagService.class);
        scheduleControllerHelper = EasyMock.createMock(ScheduleControllerHelper.class);
        jobManager = EasyMock.createMock(JobManager.class);
        LiteYukonUser user2 = new LiteYukonUser(-1);
        context = new SimpleYukonUserContext(user2, Locale.US, DateTimeZone.forID("GMT").toTimeZone(), "");
        job = new ScheduledRepeatingJob();
        job.setId(911);
        job.setCronString("0 1 * * *");
        job.setUserContext(context);
        task = new ScheduledDataImportTask();
        task.setErrorFileOutputPath("C:\\Yukon\\Server\\Import\\Error");
        task.setImportPath("C:\\Yukon\\Server\\Import");
        task.setScheduleName("DataImportJob1");
        task.setImportType("AssetImport");
        jobStatus = new JobStatus<YukonJob>();
        jobStatus.setId(911);
        jobStatus.setJob(job);
        jobStatus.setJobRunStatus(JobRunStatus.STARTED);
        ReflectionTestUtils.setField(scheduledDataImportServiceImplTest, "cronExpressionTagService",
            cronExpressionTagService);
        EasyMock.expect(cronExpressionTagService.getDescription("0 1 * * *", context)).andReturn("Daily, at 01:00 AM");
        EasyMock.replay(cronExpressionTagService);
        ReflectionTestUtils.setField(scheduledDataImportServiceImplTest, "jobManager", jobManager);
        EasyMock.expect(jobManager.getJobDisabledStatus(job.getId())).andReturn(JobDisabledStatus.N);
        EasyMock.expect(jobManager.getRepeatingJob(job.getId())).andReturn(job);
        EasyMock.expect(jobManager.instantiateTask(job)).andReturn(task);
        EasyMock.replay(jobManager);
        ReflectionTestUtils.setField(scheduledDataImportServiceImplTest, "scheduleControllerHelper",
            scheduleControllerHelper);
        EasyMock.expect(scheduleControllerHelper.getJobState(job.getId())).andReturn(JobState.RUNNING);
        EasyMock.replay(scheduleControllerHelper);
    }

    @Test
    public void test_getScheduleDescription() {
        String scheduleDescription =
            ReflectionTestUtils.invokeMethod(scheduledDataImportServiceImplTest, "getScheduleDescription", job);
        assertEquals(scheduleDescription, "Daily, at 01:00 AM");
    }

    @Test
    public void test_getScheduleImportData() {
        ScheduledDataImport scheduledDataImport =
            ReflectionTestUtils.invokeMethod(scheduledDataImportServiceImplTest, "getScheduleImportData", job, task);
        assertEquals(scheduledDataImport.getJobId(), job.getId());
        assertEquals(scheduledDataImport.getCronString(), job.getCronString());
        assertEquals(scheduledDataImport.getErrorFileOutputPath(), task.getErrorFileOutputPath());
        assertEquals(scheduledDataImport.getImportPath(), task.getImportPath());
        assertEquals(scheduledDataImport.getImportType(), ScheduledImportType.fromImportTypeMap(task.getImportType()));
        assertEquals(scheduledDataImport.getScheduleDescription(), "Daily, at 01:00 AM");
        assertEquals(scheduledDataImport.getJobState(), JobState.RUNNING);
    }

    @Test
    public void test_getJobById() {
        ScheduledDataImport scheduledDataImport = scheduledDataImportServiceImplTest.getJobById(job.getId());
        assertNotNull(scheduledDataImport);
        assertEquals(scheduledDataImport.getJobId(), job.getId());
    }

}
