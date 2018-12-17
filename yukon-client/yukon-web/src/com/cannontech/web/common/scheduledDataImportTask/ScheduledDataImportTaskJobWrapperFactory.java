package com.cannontech.web.common.scheduledDataImportTask;

import java.util.Comparator;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.scheduledFileImport.ScheduledImportType;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.scheduledDataImport.tasks.ScheduledDataImportTask;
import com.google.common.collect.Ordering;

public class ScheduledDataImportTaskJobWrapperFactory {
    @Autowired private JobManager jobManager;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private JobStatusDao jobStatusDao;
    private static Logger log = YukonLogManager.getLogger(ScheduledDataImportTaskJobWrapperFactory.class);

    public ScheduledDataImportTaskJobWrapper createJobWrapper(ScheduledRepeatingJob scheduledRepeatingJob,
            YukonUserContext userContext) {
        return new ScheduledDataImportTaskJobWrapper(scheduledRepeatingJob, userContext);
    }

    public class ScheduledDataImportTaskJobWrapper implements Comparable<ScheduledDataImportTaskJobWrapper> {
        private ScheduledRepeatingJob job;
        private YukonUserContext yukonUserContext;
        private ScheduledDataImportTask task;
        private JobState jobState;

        public ScheduledDataImportTaskJobWrapper(ScheduledRepeatingJob scheduledRepeatingJob,
                YukonUserContext yukonUserContext) {
            job = scheduledRepeatingJob;
            this.yukonUserContext = yukonUserContext;
            task = (ScheduledDataImportTask) jobManager.instantiateTask(this.job);
            JobStatus<YukonJob> status = jobStatusDao.findLatestStatusByJobId(scheduledRepeatingJob.getId());
            JobDisabledStatus jobDisabledStatus = jobManager.getJobDisabledStatus(scheduledRepeatingJob.getId());
            jobState = JobState.of(jobDisabledStatus, status);
        }

        public Date getNextRun() {
            Date nextRun = null;
            try {
                nextRun = jobManager.getNextRuntime(job, new Date());
            } catch (ScheduleException e) {
                log.error(e.getMessage());
            }
            return nextRun;
        }

        public String getScheduleDescription() {
            return cronExpressionTagService.getDescription(this.job.getCronString(), this.yukonUserContext);
        }

        @Override
        public int compareTo(ScheduledDataImportTaskJobWrapper o) {
            return this.task.getScheduleName().compareToIgnoreCase(o.getTask().getScheduleName());
        }

        public ScheduledRepeatingJob getJob() {
            return job;
        }

        public void setJob(ScheduledRepeatingJob scheduledRepeatingJob) {
            this.job = scheduledRepeatingJob;
        }

        public YukonUserContext getYukonUserContext() {
            return yukonUserContext;
        }

        public void setYukonUserContext(YukonUserContext yukonUserContext) {
            this.yukonUserContext = yukonUserContext;
        }

        public ScheduledDataImportTask getTask() {
            return task;
        }

        public void setTask(ScheduledDataImportTask task) {
            this.task = task;
        }

        public JobState getJobState() {
            return jobState;
        }

        public String getName() {
            return this.task.getScheduleName();
        }

        public ScheduledImportType getImportType() {
            return ScheduledImportType.fromName(this.task.getImportType());
        }
    }

    public static Comparator<ScheduledDataImportTaskJobWrapper> getJobNameComparator() {
        return Ordering.natural().onResultOf(ScheduledDataImportTaskJobWrapper::getName);
    }

    public static Comparator<ScheduledDataImportTaskJobWrapper> getRunScheduleComparator() {
        return Ordering.natural().onResultOf(ScheduledDataImportTaskJobWrapper::getScheduleDescription);
    }

    public static Comparator<ScheduledDataImportTaskJobWrapper> getNextRunComparator() {
        return Ordering.natural().onResultOf(ScheduledDataImportTaskJobWrapper::getNextRun);
    }

    public static Comparator<ScheduledDataImportTaskJobWrapper> getStatusComparator() {
        return Ordering.natural().onResultOf(wrapper -> wrapper.getJobState().name());
    }

    public static Comparator<ScheduledDataImportTaskJobWrapper> getJobTypeComparator() {
        return Ordering.natural().onResultOf(wrapper -> wrapper.task.getImportType());
    }
}
