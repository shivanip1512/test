package com.cannontech.jobs.service.impl;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
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
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.ScheduledOneTimeJobDao;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.YukonJobDao;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobContext;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.SystemUserContext;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputUtil;
import com.google.common.collect.Lists;


public class JobManagerImpl implements JobManager {
    private Logger log = YukonLogManager.getLogger(JobManagerImpl.class);
    private TimeSource timeSource;
    private JobStatusDao jobStatusDao;
    private YukonJobDao yukonJobDao;
    private ScheduledOneTimeJobDao scheduledOneTimeJobDao;
    private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    private ScheduledExecutor scheduledExecutor;
    private TransactionTemplate transactionTemplate;

    private ConcurrentMap<YukonJob, YukonTask> currentlyRunning = new ConcurrentHashMap<YukonJob, YukonTask>(10, .75f, 2);
    
    // JobId -> ScheduledJobInfoImpl
    private ConcurrentMap<Integer, ScheduledInfo> scheduledJobs = new ConcurrentHashMap<Integer, ScheduledInfo>();

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
            if (status.getJob().isDisabled()) {
                handleDisabledRestart(status);
            } else {
                // we want to rerun this immediately and when it's done, schedule its next run
                final RunnableRetryJob runnable = new RunnableRetryJob(status) {
                    @Override
                    protected void afterRun() {
                        doScheduleScheduledJob(status.getJob(), null);
                    }
                };
                Date nextStartupTime = getNextStartupTime();
                doSchedule(status.getJob(), runnable, nextStartupTime);
                log.info("restart repeating job " + status + " scheduled for " + nextStartupTime + "ms from now");
            }
        }

        for (ScheduledRepeatingJob job : allRepeatingJobs) {
            // this means we never do a catchup if the server was
            // off when a job was supposed to run
            if (!job.isDisabled()) {
                doScheduleScheduledJob(job, null);
            } else {
                log.debug("Skipping disabled repeating job: " + job);
            }
        }

        // get all jobs
        Set<ScheduledOneTimeJob> allUnstartedOneTimeJobs = scheduledOneTimeJobDao.getAllUnstarted();

        // check current job state table to see if anything needs to be restarted
        Set<JobStatus<ScheduledOneTimeJob>> unFinishedOneTimeJobs = scheduledOneTimeJobDao.getAllUnfinished();
        for (final JobStatus<ScheduledOneTimeJob> status : unFinishedOneTimeJobs) {
            boolean gotIt = allUnstartedOneTimeJobs.remove(status.getJob());
            if (!gotIt) {
                log.warn("tried to restart a one time job status that wasn't in the unstarted set: " + status);
                continue;
            }
            if (status.getJob().isDisabled()) {
                handleDisabledRestart(status);
            } else {
                // we want to schedule this immediately
                final RunnableRetryJob runnable = new RunnableRetryJob(status);
                Date nextStartupTime = getNextStartupTime();
                doSchedule(status.getJob(), runnable, nextStartupTime);
                log.info("restart one time job " + status + " scheduled for " + nextStartupTime + "ms from now");
            }
        }

        for (ScheduledOneTimeJob job : allUnstartedOneTimeJobs) {
            if (!job.isDisabled()) {
                doScheduleOneTimeJob(job);
            } else {
                log.debug("Skipping disabled one time job: " + job);
            }
        }

    }

    private void handleDisabledRestart(final JobStatus<?> status) {
        // this is weird, so we're going to use a special case for it
        status.setJobState(JobState.DISABLED);
        jobStatusDao.saveOrUpdate(status);
        log.info("tried to restart a disabled job status, changed status do DISABLED: " + status);
    }

    private synchronized Date getNextStartupTime() {
        int temp = startOffsetMs.getAndAdd(startOffsetIncrement);
        Date time = new Date(timeSource.getCurrentMillis() + temp);
        return time;
    }

    public YukonJob getJob(int jobId) {
        return yukonJobDao.getById(jobId);
    }
    
    public JobDisabledStatus getJobDisabledStatus(int jobId) {
        return yukonJobDao.getJobDisabledStatusById(jobId);
    }
    
    public ScheduledRepeatingJob getRepeatingJob(int jobId) {
        return (ScheduledRepeatingJob)yukonJobDao.getById(jobId);
    }
    
    public Set<ScheduledOneTimeJob> getUnRunOneTimeJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition) {
        Set<ScheduledOneTimeJob> defJobs =  scheduledOneTimeJobDao.getJobsByDefinition(definition);
        
        Set<ScheduledOneTimeJob> unrunJobs = new HashSet<ScheduledOneTimeJob>();
        for (ScheduledOneTimeJob j : defJobs) {
            if(isJobStillRunnable(j)) {
                unrunJobs.add(j);
            }
        }
        return unrunJobs;
    }
    
    public Set<ScheduledRepeatingJob> getUnRunRepeatingJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition) {
        Set<ScheduledRepeatingJob> defJobs = scheduledRepeatingJobDao.getJobsByDefinition(definition);
        
        Set<ScheduledRepeatingJob> unrunJobs = new HashSet<ScheduledRepeatingJob>();
        for (ScheduledRepeatingJob j : defJobs) {
            if(isJobStillRunnable(j)) {
                unrunJobs.add(j);
            }
        }
        return unrunJobs;
    }
    
    public List<ScheduledRepeatingJob> getNotDeletedRepeatingJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition) {
        Set<ScheduledRepeatingJob> repeatingJobs = scheduledRepeatingJobDao.getJobsByDefinition(definition);
        List<ScheduledRepeatingJob> jobsNotDeleted = Lists.newArrayList();
        for (ScheduledRepeatingJob job: repeatingJobs) {
            if (!job.isDeleted()) {
                jobsNotDeleted.add(job);
            }
        }
        return jobsNotDeleted;
    }
    
    private boolean isJobStillRunnable(YukonJob job) {
        if (!job.isDisabled()) {
            int jobId = job.getId();
            JobStatus<YukonJob> jobStatus = jobStatusDao.findLatestStatusByJobId(jobId);
            if (jobStatus == null || jobStatus.getJobState() == JobState.RESTARTED) {
                return true;
            }
        }
        return false;
    }
    
    // SCHEDULE JOB
    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time) {
        return scheduleJob(jobDefinition, task, time, new SystemUserContext());
    }

    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time, YukonUserContext userContext) {
        log.info("scheduling onetime job: jobDefinition=" + jobDefinition + ", task=" + task + ", time=" + time);
        ScheduledOneTimeJob oneTimeJob = new ScheduledOneTimeJob();
        scheduleJobCommon(oneTimeJob, jobDefinition, task, userContext);

        oneTimeJob.setStartTime(time);
        scheduledOneTimeJobDao.save(oneTimeJob);

        doScheduleOneTimeJob(oneTimeJob);
        
        return oneTimeJob;
    }
    
    public YukonJob replaceScheduledJob(int jobId, YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression, YukonUserContext userContext) {
        YukonJob job = getJob(jobId);
        deleteJob(job);
        YukonJob scheduledJob = scheduleJob(jobDefinition, task, cronExpression, userContext);
        return scheduledJob;
    }

    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression) {
        return scheduleJob(jobDefinition, task, cronExpression, new SystemUserContext());
    }

    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression, YukonUserContext userContext) {
        log.info("scheduling repeating job: jobDefinition=" + jobDefinition + ", task=" + task + ", cronExpression=" + cronExpression);
        ScheduledRepeatingJob repeatingJob = new ScheduledRepeatingJob();
        scheduleJobCommon(repeatingJob, jobDefinition, task, userContext);

        repeatingJob.setCronString(cronExpression);
        scheduledRepeatingJobDao.save(repeatingJob);

        doScheduleScheduledJob(repeatingJob, null);
        
        return repeatingJob;
    }
    
    // PRIVATE HELPERS
    private void scheduleJobCommon(YukonJob job, YukonJobDefinition<?> jobDefinition, YukonTask task, YukonUserContext userContext) throws BeansException {
        InputRoot inputRoot = jobDefinition.getInputs();

        Map<String, String> properties = InputUtil.extractProperties(inputRoot, task);
        log.debug("extracted properties for " + jobDefinition + ": " + properties);

        job.setUserContext(userContext);
        job.setJobDefinition(jobDefinition);
        job.setBeanName(jobDefinition.getName());
        job.setJobProperties(properties);
    }

    private void doScheduleScheduledJob(final ScheduledRepeatingJob job, Date lastTimeScheduled) {
        log.debug("doScheduleScheduledJob for " + job);
        try {
            Date getAfterTime = lastTimeScheduled != null ? lastTimeScheduled : timeSource.getCurrentTime();
            final Date nextRuntime = getNextRuntime(job, getAfterTime);
            Runnable runnable = new BaseRunnableJob(job) {
                @Override
                protected void afterRun() {
                    doScheduleScheduledJob(job, nextRuntime);
                }
            };

            if (nextRuntime != null) {
            	
            	doSchedule(job, runnable, nextRuntime);
                log.info("job " + job + " scheduled for " + nextRuntime);
            } else {
                log.debug("job " + job + " has no next runtime, it will not be scheduled.");
            }
        } catch (Exception e) {
            log.error("unable to schedule job " + job, e);
        }
    }

    private void doScheduleOneTimeJob(final ScheduledOneTimeJob job) {
        // we could just hold onto the job id and then look up the full YukonJob when the run
        // method is called, this would reduce the memory footprint
        Runnable runnable = new BaseRunnableJob(job);

        doSchedule(job, runnable, job.getStartTime());
    }

    private void doSchedule(YukonJob job, final Runnable runnable, Date nextRuntime) {
        final int jobId = job.getId();
        long delay = nextRuntime.getTime() - timeSource.getCurrentMillis();
        final ScheduledInfo info = new ScheduledInfo(jobId, nextRuntime);
        ScheduledInfo oldValue = scheduledJobs.put(jobId, info);
        if (oldValue != null) {
            throw new RuntimeException("scheduledJobs map already contained job: " + job);
        }
        try {
            Runnable runnerToSchedule = new Runnable() {
                public void run() {
                    ScheduledInfo removed = scheduledJobs.remove(jobId);
                    if (removed != null) {
                        runnable.run();
                    } else {
                        log.info("time came to run schedule and it wasn't in scheduledJobs, must have been removed: jobId=" + jobId);
                    }
                }
            };
            ScheduledFuture<?> future =
                scheduledExecutor.schedule(runnerToSchedule, delay, TimeUnit.MILLISECONDS);
            info.future = future;
        } catch (RuntimeException e) {
            log.error("Couldn't add runnable to the scheduledExecutor", e);
            scheduledJobs.remove(jobId);
        }
    }
    
    // MISC PUBLIC
    public void disableJob(YukonJob job) {
        log.info("disabling job: " + job);
        job.setDisabled(true);
        yukonJobDao.update(job);
        
        // see if we can cancel it
        ScheduledInfo jobInfo = scheduledJobs.remove(job.getId());
        
        if (jobInfo != null) {
            // this should unschedule it, but since we removed it from the map
            // the is no longer any way it could run
            
            // there is no need to pass true because it is taken out of scheduledJobs
            // before it actually runs
            if (jobInfo.future != null) {
            	jobInfo.future.cancel(false);
            }
        } else {
            log.info("tried to remove job while disabling, it no longer existed: " + job);
        }
        
        
    }
    
    public void deleteJob(YukonJob job) {
    	
    	disableJob(job);
    	
    	log.info("deleting job: " + job);
    	job.setDeleted(true);
        yukonJobDao.update(job);
    }
    
    public void enableJob(YukonJob job) {
        log.info("enabling job: " + job);
        job.setDisabled(false);
        yukonJobDao.update(job);
        
        // not great, but I don't really have a better way
        if (job instanceof ScheduledOneTimeJob) {
            ScheduledOneTimeJob oneTimeJob = (ScheduledOneTimeJob) job;
            
            // put it into the schedule
            doScheduleOneTimeJob(oneTimeJob);
        } else if (job instanceof ScheduledRepeatingJob) {
            ScheduledRepeatingJob repeatingJob = (ScheduledRepeatingJob) job;
            
            // put it into the schedule
            doScheduleScheduledJob(repeatingJob, null);
        }
        
        
    }

    public Date getNextRuntime(ScheduledRepeatingJob job, Date from) throws ScheduleException {
        try {
            CronExpression cronExpression = new CronExpression(job.getCronString());
            // is this the right thing to do?
            TimeZone userTimeZone = job.getUserContext().getTimeZone();
            cronExpression.setTimeZone(userTimeZone);
            Date nextValidTimeAfter = cronExpression.getNextValidTimeAfter(from);
            return nextValidTimeAfter;

        } catch (ParseException e) {
            throw new ScheduleException("Could not calculate next runtime for " + job.getBeanName()
                + " with " + job.getCronString(), e);
        } catch (UnsupportedOperationException e) {
        	throw new ScheduleException("Could not calculate next runtime for " + job.getBeanName()
                    + " with " + job.getCronString(), e);
        }
    }

    private void executeJob(final JobStatus<?> status) throws TransactionException {
    	// assume the best
    	status.setJobState(JobState.COMPLETED);
    	try {
    		YukonTask task = instantiateTask(status.getJob());
    		
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
    }

    public synchronized YukonTask instantiateTask(YukonJob job) {
        YukonJobDefinition<? extends YukonTask> jobDefinition = job.getJobDefinition();

        log.info("instantiating task for " + jobDefinition);
        YukonTask task = jobDefinition.createBean();

        InputRoot inputRoot = jobDefinition.getInputs();
        if(inputRoot != null) {
        	// Set the inputs if there are any
        	InputUtil.applyProperties(inputRoot, task, job.getJobProperties());
        }
        
        JobContext jobContext = new JobContext(job);
        task.setJobContext(jobContext);

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

    // PRIVATE CLASSES
    private class BaseRunnableJob implements Runnable {
        private final int jobId;

        private BaseRunnableJob(YukonJob job) {
            this.jobId = job.getId();
        }

        public void run() {
            // record startup in database
            try {
                final JobStatus<YukonJob> status = new JobStatus<YukonJob>();
                transactionTemplate.execute(new TransactionCallback() {
                    public Object doInTransaction(TransactionStatus transactionStatus) {
                        log.info("starting runnable: jobId=" + jobId);
                        
                        // fetch the job
                        YukonJob job = yukonJobDao.getById(jobId);
                        
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

    private class ScheduledInfo {
        Date time;
        ScheduledFuture<?> future;
        int jobId;

        public ScheduledInfo(int jobId, Date time) {
            this.jobId = jobId;
            this.time = time;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + jobId;
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
            final ScheduledInfo other = (ScheduledInfo) obj;
            if (jobId != other.jobId)
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
    public void setYukonJobDao(YukonJobDao yukonJobDao) {
        this.yukonJobDao = yukonJobDao;
    }
}
