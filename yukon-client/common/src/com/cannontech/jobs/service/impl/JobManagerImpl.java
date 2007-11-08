package com.cannontech.jobs.service.impl;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.TimeSource;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.ScheduledOneTimeJobDao;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputUtil;


public class JobManagerImpl implements JobManager {
    private Logger log = YukonLogManager.getLogger(JobManagerImpl.class);
    private TimeSource timeSource;
    private YukonUserDao yukonUserDao;
    private JobStatusDao jobStatusDao;
    private ScheduledOneTimeJobDao scheduledOneTimeJobDao;
    private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    private ScheduledExecutor scheduledExecutor;
    private TransactionTemplate transactionTemplate;

    private ConcurrentMap<YukonJob, YukonTask> currentlyRunning = new ConcurrentHashMap<YukonJob, YukonTask>(10, .75f, 2);
    private ConcurrentSkipListSet<ScheduledJobInfo> scheduledJobs = new ConcurrentSkipListSet<ScheduledJobInfo>();

    private AtomicInteger startOffsetMs = new AtomicInteger(5000);
    private int startOffsetIncrement = 1000;

    public void initialize() {

        // get all jobs
        Set<ScheduledRepeatingJob> allRepeatingJobs = scheduledRepeatingJobDao.getAll();

        // check current job state table to see if anything needs to be restarted
        Set<JobStatus<ScheduledRepeatingJob>> unFinishedRepeatingJobs = scheduledRepeatingJobDao.getAllUnfinished();
        for (final JobStatus<ScheduledRepeatingJob> status : unFinishedRepeatingJobs) {
            boolean gotIt = allRepeatingJobs.remove(status.getJob());
            if (!gotIt) {
                log.warn("tried to restart a repeating job status that wasn't in the all set: " + status);
                continue;
            }
            // we want to rerun this immediately and when it's done, schedule its next run
            final RunnableRetryJob runnable = new RunnableRetryJob(status) {
                @Override
                protected void afterRun() {
                    doScheduleScheduledJob(status.getJob());
                }
            };
            Date nextStartupTime = getNextStartupTime();
            doSchedule(runnable, nextStartupTime);
            log.info("restart repeating job " + status + " scheduled for " + nextStartupTime + "ms from now");
        }

        for (ScheduledRepeatingJob job : allRepeatingJobs) {
            // this means we never do a catchup if the server was
            // off when a job was supposed to run
            doScheduleScheduledJob(job);
        }

        // get all jobs
        Set<ScheduledOneTimeJob> allOneTimeJobs = scheduledOneTimeJobDao.getAll();

        // check current job state table to see if anything needs to be restarted
        Set<JobStatus<ScheduledOneTimeJob>> unFinishedOneTimeJobs = scheduledOneTimeJobDao.getAllUnfinished();
        for (final JobStatus<ScheduledOneTimeJob> status : unFinishedOneTimeJobs) {
            boolean gotIt = allOneTimeJobs.remove(status.getJob());
            if (!gotIt) {
                log.warn("tried to restart a one time job status that wasn't in the all set: " + status);
                continue;
            }
            // we want to rerun this immediately and when it's done, schedule its next run
            final RunnableRetryJob runnable = new RunnableRetryJob(status);
            Date nextStartupTime = getNextStartupTime();
            doSchedule(runnable, nextStartupTime);
            log.info("restart one time job " + status + " scheduled for " + nextStartupTime + "ms from now");
        }

        for (ScheduledOneTimeJob job : allOneTimeJobs) {
            // this means we never do a catchup if the server was
            // off when a job was supposed to run
            doScheduleOneTimeJob(job);
        }

    }

    private synchronized Date getNextStartupTime() {
        int temp = startOffsetMs.getAndAdd(startOffsetIncrement);
        Date time = new Date(timeSource.getCurrentMillis() + temp);
        return time;
    }

    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time) {
        LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(-2);
        scheduleJob(jobDefinition, task, time, liteYukonUser);
    }

    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time, LiteYukonUser runAs) {
        log.info("scheduling onetime job: jobDefinitio=" + jobDefinition + ", task=" + task + ", time=" + time);
        ScheduledOneTimeJob oneTimeJob = new ScheduledOneTimeJob();
        scheduleJobCommon(oneTimeJob, jobDefinition, task, runAs);

        oneTimeJob.setStartTime(time);
        scheduledOneTimeJobDao.save(oneTimeJob);

        doScheduleOneTimeJob(oneTimeJob);
    }

    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression) {
        LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(-2);
        scheduleJob(jobDefinition, task, cronExpression, liteYukonUser);
    }

    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression, LiteYukonUser runAs) {
        log.info("scheduling repeating job: jobDefinitio=" + jobDefinition + ", task=" + task + ", cronExpression=" + cronExpression);
        ScheduledRepeatingJob repeatingJob = new ScheduledRepeatingJob();
        scheduleJobCommon(repeatingJob, jobDefinition, task, runAs);

        repeatingJob.setCronString(cronExpression);
        scheduledRepeatingJobDao.save(repeatingJob);

        doScheduleScheduledJob(repeatingJob);
    }

    private void scheduleJobCommon(YukonJob job, YukonJobDefinition<?> jobDefinition, YukonTask task, LiteYukonUser runAs) throws BeansException {
        InputRoot inputRoot = jobDefinition.getInputs();

        HashMap<String, String> properties = InputUtil.extractProperties(inputRoot, task);
        log.debug("extracted properties for " + jobDefinition + ": " + properties);

        job.setRunAsUser(runAs);
        job.setJobDefinition(jobDefinition);
        job.setBeanName(jobDefinition.getName());
        job.setJobProperties(properties);
    }

    private void doScheduleScheduledJob(final ScheduledRepeatingJob job) {
        log.debug("doScheduleScheduledJob for " + job);
        try {
            RunnableJob runnable = new BaseRunnableJob(job) {
                @Override
                protected void afterRun() {
                    doScheduleScheduledJob(job);
                }
            };

            Date nextRuntime = getNextRuntime(job, timeSource.getCurrentTime());
            doSchedule(runnable, nextRuntime);
            log.info("job " + job + " scheduled for " + nextRuntime);
        } catch (Exception e) {
            log.error("unable to schedule job " + job, e);
        }
    }

    private void doScheduleOneTimeJob(final ScheduledOneTimeJob job) {
        // we could just hold onto the job id and then look up the full YukonJob when the run
        // method is called, this would reduce the memory footprint
        RunnableJob runnable = new BaseRunnableJob(job);

        doSchedule(runnable, job.getStartTime());
    }

    private void doSchedule(final RunnableJob runnable, Date nextRuntime) {
        long delay = nextRuntime.getTime() - System.currentTimeMillis();
        final ScheduledJobInfoImpl impl = new ScheduledJobInfoImpl(runnable.getJob(), nextRuntime);
        boolean b = scheduledJobs.add(impl);
        if (!b) {
            throw new RuntimeException("scheduledJobs set already containd job: "
                + runnable.getJob());
        }
        try {
            scheduledExecutor.schedule(new Runnable() {
                public void run() {
                    impl.setRunning(true);
                    try {
                        runnable.run();
                    } finally {
                        scheduledJobs.remove(impl);
                    }
                }

            }, delay, TimeUnit.MILLISECONDS);
        } catch (RuntimeException e) {
            log.error("Couldn't add runnable to the scheduledExecutor", e);
            scheduledJobs.remove(impl);
        }
    }

    public SortedSet<ScheduledJobInfo> getScheduledJobInfo() {
        return scheduledJobs;
    }

    private Date getNextRuntime(ScheduledRepeatingJob job, Date from) throws ScheduleException {
        try {
            CronExpression cronExpression = new CronExpression(job.getCronString());
            // is this the right thing to do?
            TimeZone userTimeZone = yukonUserDao.getUserTimeZone(job.getRunAsUser());
            cronExpression.setTimeZone(userTimeZone);
            Date nextValidTimeAfter = cronExpression.getNextValidTimeAfter(from);
            return nextValidTimeAfter;

        } catch (ParseException e) {
            throw new ScheduleException("Could not calculate next runtime for " + job.getBeanName()
                + " with " + job.getCronString(), e);
        }
    }

    private void executeJob(final JobStatus<?> status) throws TransactionException {
        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus transactionStatus) {
                // assume the best
                status.setJobState(JobState.COMPLETED);
                try {
                    YukonTask task = instantiateTask(status.getJob());
                    task.setRunAsUser(status.getJob().getRunAsUser());
                    YukonTask existingTask = currentlyRunning.putIfAbsent(status.getJob(), task);
                    if (existingTask != null) {
                        // this should have been caught before the job was
                        // executed
                        throw new IllegalStateException("a task for " + status.getJob()
                            + " is already running: " + existingTask);
                    }
                    task.start(); // this should block until task is complete
                } catch (Exception e) {
                    log.error("YukonTask failed", e);
                    status.setJobState(JobState.FAILED);
                    status.setMessage(e.toString());
                } finally {
                    currentlyRunning.remove(status.getJob());
                }

                // record status in database
                status.setStopTime(timeSource.getCurrentTime());
                jobStatusDao.saveOrUpdate(status);
                return null;
            }
        });
    }

    private YukonTask instantiateTask(YukonJob job) {
        YukonJobDefinition<? extends YukonTask> jobDefinition = job.getJobDefinition();

        log.info("instantiating task for " + jobDefinition);
        YukonTask task = jobDefinition.createBean();

        InputRoot inputRoot = jobDefinition.getInputs();
        InputUtil.applyProperties(inputRoot, task, job.getJobProperties());

        return task;
    }

    public Collection<YukonJob> getCurrentlyExecuting() {
        return currentlyRunning.keySet();
    }

    public boolean abortJob(YukonJob job) {
        YukonTask task = currentlyRunning.get(job);
        if (task == null) {
            return false;
        }
        try {
            task.stop();
        } catch (UnsupportedOperationException e) {
            log.warn("tried to stop an unstoppable task, job was: " + job, e);
            return false;
        }
        return true;
    }


    private interface RunnableJob extends Runnable {
        public YukonJob getJob();
    }

    private class BaseRunnableJob implements RunnableJob {
        private final YukonJob job;

        private BaseRunnableJob(YukonJob job) {
            this.job = job;
        }

        public YukonJob getJob() {
            return job;
        }

        public void run() {
            // record startup in database
            try {
                final JobStatus<YukonJob> status = new JobStatus<YukonJob>();
                transactionTemplate.execute(new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus transactionStatus) {
                        log.info("starting runnable for " + job);
                        beforeRun();
                        status.setStartTime(timeSource.getCurrentTime());
                        status.setJobState(JobState.STARTED);
                        status.setMessage("");
                        status.setJob(job);
                        jobStatusDao.saveOrUpdate(status);
                        return null;
                    }
                });
                executeJob(status);
            } catch (Exception e) {
                log.error("Possible DB error while executing YukonTask", e);
            }
            afterRun();
        }

        protected void afterRun() {
            // no op by default
        }

        protected void beforeRun() {
            // no op by default
        }
    }

    private class RunnableRetryJob extends BaseRunnableJob {

        private final JobStatus<?> oldStatus;

        private RunnableRetryJob(JobStatus<?> status) {
            super(status.getJob());
            this.oldStatus = status;
        }

        @Override
        protected void beforeRun() {
            oldStatus.setJobState(JobState.RESTARTED);
            jobStatusDao.saveOrUpdate(oldStatus);
        }
    }

    private class ScheduledJobInfoImpl implements ScheduledJobInfo {
        private YukonJob job;
        private Date time;
        private boolean running;

        public YukonJob getJob() {
            return job;
        }

        public Date getTime() {
            return time;
        }

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public ScheduledJobInfoImpl(YukonJob job, Date time) {
            this.job = job;
            this.time = time;
            this.running = false;
        }

        public int compareTo(ScheduledJobInfo o) {
            if (this.time.equals(o.getTime())) {
                return job.getId().compareTo(o.getJob().getId());
            }
            return time.compareTo(o.getTime());
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((job == null) ? 0 : job.hashCode());
            result = prime * result + ((time == null) ? 0 : time.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final ScheduledJobInfoImpl other = (ScheduledJobInfoImpl) obj;
            if (job == null) {
                if (other.job != null)
                    return false;
            } else if (!job.equals(other.job))
                return false;
            if (time == null) {
                if (other.time != null)
                    return false;
            } else if (!time.equals(other.time))
                return false;
            return true;
        }
    }

    @Required
    public void setJobStatusDao(JobStatusDao jobStatusDao) {
        this.jobStatusDao = jobStatusDao;
    }

    @Required
    public void setScheduledExecutor(ScheduledExecutor scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
    }

    @Required
    public void setScheduledRepeatingJobDao(ScheduledRepeatingJobDao scheduledRepeatingJobDao) {
        this.scheduledRepeatingJobDao = scheduledRepeatingJobDao;
    }

    @Required
    public void setScheduledOneTimeJobDao(ScheduledOneTimeJobDao scheduledOneTimeJobDao) {
        this.scheduledOneTimeJobDao = scheduledOneTimeJobDao;
    }

    public void setStartOffsetIncrement(int startOffsetIncrement) {
        this.startOffsetIncrement = startOffsetIncrement;
    }

    public void setStartOffsetMs(int startOffsetMs) {
        this.startOffsetMs.set(startOffsetMs);
    }

    @Required
    public void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

    @Required
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Required
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
}
