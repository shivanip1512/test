package com.cannontech.web.common.scheduledDataImportTask;

import java.util.Comparator;
import java.util.Date;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.scheduledDataImport.tasks.ScheduledDataImportTask;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public class ScheduledDataImportTaskJobWrapperFactory {
    private JobManager jobManager;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private JobStatusDao jobStatusDao;

    public ScheduledDataImportTaskJobWrapper createJobWrapper(ScheduledRepeatingJob scheduledRepeatingJob,
            YukonUserContext userContext) {
        return new ScheduledDataImportTaskJobWrapper(scheduledRepeatingJob, userContext);
    }

    public class ScheduledDataImportTaskJobWrapper implements Comparable<ScheduledDataImportTaskJobWrapper> {
        private ScheduledRepeatingJob job;
        private YukonUserContext yukonUserContext;
        private ScheduledDataImportTask task;

        private CronExpressionTagState tagState;
        private JobState jobState;

        public ScheduledDataImportTaskJobWrapper(ScheduledRepeatingJob scheduledRepeatingJob,
                YukonUserContext yukonUserContext) {
            this.job = scheduledRepeatingJob;
            this.yukonUserContext = yukonUserContext;
            this.task = (ScheduledDataImportTask) jobManager.instantiateTask(this.job);
            this.setTagState(cronExpressionTagService.parse(scheduledRepeatingJob.getCronString(), yukonUserContext));
            JobStatus<YukonJob> status = jobStatusDao.findLatestStatusByJobId(scheduledRepeatingJob.getId());
            JobDisabledStatus jobDisabledStatus = jobManager.getJobDisabledStatus(scheduledRepeatingJob.getId());
            this.jobState = JobState.of(jobDisabledStatus, status);
        }

        public Date getLastRun() {
            JobStatus<YukonJob> lastRunJob = jobManager.getLatestStatusByJobId(this.job.getId());
            if (lastRunJob == null) {
                return null;
            }
            return lastRunJob.getStartTime();
        }

        public Date getNextRun() {
            Date nextRun = null;
            try {
                nextRun = jobManager.getNextRuntime(job, new Date());
            } catch (ScheduleException e) {}

            return nextRun;
        }

        public String getName() {
            return this.task.getScheduleName();
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

        public CronExpressionTagState getTagState() {
            return tagState;
        }

        public void setTagState(CronExpressionTagState tagState) {
            this.tagState = tagState;
        }

        public JobState getJobState() {
            return jobState;
        }

        public String getShortName() {
            return this.task.getScheduleName();
        }
    }

    public static Comparator<ScheduledDataImportTaskJobWrapper> getJobNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<ScheduledDataImportTaskJobWrapper> jobNameOrdering =
            normalStringComparer.onResultOf(new Function<ScheduledDataImportTaskJobWrapper, String>() {
                @Override
                public String apply(ScheduledDataImportTaskJobWrapper from) {
                    return from.getName();
                }
            });
        return jobNameOrdering;
    }

    public static Comparator<ScheduledDataImportTaskJobWrapper> getRunScheduleComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<ScheduledDataImportTaskJobWrapper> jobRunScheduleOrdering =
            normalStringComparer.onResultOf(new Function<ScheduledDataImportTaskJobWrapper, String>() {
                @Override
                public String apply(ScheduledDataImportTaskJobWrapper from) {
                    return from.getScheduleDescription();
                }
            });
        return jobRunScheduleOrdering;
    }

    public static Comparator<ScheduledDataImportTaskJobWrapper> getNextRunComparator() {
        Ordering<Date> dateComparer = Ordering.natural().nullsLast();
        Ordering<ScheduledDataImportTaskJobWrapper> jobNextRunOrdering =
            dateComparer.onResultOf(new Function<ScheduledDataImportTaskJobWrapper, Date>() {
                @Override
                public Date apply(ScheduledDataImportTaskJobWrapper from) {
                    return from.getNextRun();
                }
            });
        return jobNextRunOrdering;
    }

    public static Comparator<ScheduledDataImportTaskJobWrapper> getStatusComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<ScheduledDataImportTaskJobWrapper> jobStatusOrdering =
            normalStringComparer.onResultOf(new Function<ScheduledDataImportTaskJobWrapper, String>() {
                @Override
                public String apply(ScheduledDataImportTaskJobWrapper from) {
                    return from.getJobState().name();
                }
            });
        return jobStatusOrdering;
    }

    public static Comparator<ScheduledDataImportTaskJobWrapper> getJobTypeComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<ScheduledDataImportTaskJobWrapper> jobTypeOrdering =
            normalStringComparer.onResultOf(new Function<ScheduledDataImportTaskJobWrapper, String>() {
                @Override
                public String apply(ScheduledDataImportTaskJobWrapper from) {
                    return from.task.getImportType();
                }
            });
        return jobTypeOrdering;
    }

    @Resource(name = "jobManager")
    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }

}
