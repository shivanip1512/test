package com.cannontech.jobs.model;

import java.util.List;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.Lists;

/**
 * The JobStatus.JobState values.
 * These represent the status of a running or previously run _instance_ of a Job.
 * Not to be confused with the status of the job itself (see {@link JobState}
 */
public enum JobRunStatus implements DisplayableEnum {

    STARTED(false), // the job has been started, but has not completed or failed
    COMPLETED(true), // the job has completed without "job level" errors, it will not be restarted
    FAILED(false), // the job has failed (i.e. threw an exception while running), it will not be restarted
    RESTARTED(false), // the job did not finish, but a new job has since been created as a 2nd attempt
    DISABLED(false), // the job did not finish, but the job entry was marked as disabled in the interim (it
                     // was not restarted)
    STOPPING(false), // the job was cancelled and is in the process of stopping
    CANCELLED(true), // the job was cancelled and has stopped
    ;

    private final boolean isCompleted;
    private final static List<String> completedStates;

    static {
        completedStates = Lists.newArrayList();
        for (JobRunStatus state : values()) {
            if (state.isCompleted) {
                completedStates.add(state.name());
            }
        }
    }

    private JobRunStatus(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public static List<String> getCompletedJobStateNames() {
        return completedStates;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.jobRunStatus." + this.name();

    }
}
