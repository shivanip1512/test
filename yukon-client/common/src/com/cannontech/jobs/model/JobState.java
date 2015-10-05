package com.cannontech.jobs.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.google.common.collect.ImmutableSet;

/**
 * The status of a job itself
 * not to be confused with a running or previously run _instance_ of a Job (see {@link JobRunStatus})
 */
public enum JobState implements DisplayableEnum {

    SCHEDULED(true), // an enabled job that _should_ run in the future
    RUNNING(true), // an enabled job that is currently executing
    DISABLED(false), // a disabled job, it is not expected to run again until
                     // enabled
    DELETED(false), // a deleted job, it will never run again
    ;

    private final boolean active; // represents an "enabled" job
    private static final ImmutableSet<JobState> editableStates = ImmutableSet.of(SCHEDULED);
    
    private JobState(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isEditable() {
        return editableStates.contains(this);
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.jobState." + this.name();
    }

    /**
     * Returns a JobState based on the job's disabledStatus AND the job's
     * current/last run If jobStatus is null, then JobState.Scheduled shall be
     * assumed.
     */
    public static JobState of(JobDisabledStatus jobDisabledStatus, JobStatus<YukonJob> jobStatus) {

        switch (jobDisabledStatus) {
        case Y:
            return DISABLED;
        case D:
            return DELETED;
        case N: {
            if (jobStatus != null) {
                JobRunStatus jobRunStatus = jobStatus.getJobRunStatus();
                switch (jobRunStatus) {
                case STARTED:
                case STOPPING:
                    return RUNNING;
                case COMPLETED:
                case CANCELLED:

                case FAILED:
                case RESTARTED:

                }
            }
          }
        }
        return SCHEDULED; // returning as default (want to keep the warnings
                          // active in the switch statements)
    }
}
