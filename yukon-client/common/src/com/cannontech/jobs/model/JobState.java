package com.cannontech.jobs.model;

public enum JobState {
    STARTED, // the job has been started, but has not completed or failed
    COMPLETED, // the job has completed without "job level" errors, it will not be restarted
    FAILED, // the job has failed (i.e. threw an exception while running), it will not be restarted
    RESTARTED, // the job did not finish, but a new job has since been created as a 2nd attempt
}
