package com.cannontech.jobs.model;

import java.util.List;

import com.google.common.collect.Lists;

public enum JobState {
    STARTED(false),   // the job has been started, but has not completed or failed
    COMPLETED(true),  // the job has completed without "job level" errors, it will not be restarted
    FAILED(false),    // the job has failed (i.e. threw an exception while running), it will not be restarted
    RESTARTED(false), // the job did not finish, but a new job has since been created as a 2nd attempt
    DISABLED(false),  // the job did not finish, but the job entry was marked as disabled in the interim (it was not restarted)
    STOPPING(false),  // the job was cancelled and is in the process of stopping
    CANCELLED(true),  // the job was cancelled and has stopped
    ;
    
    private boolean isCompleted;
    private static List<String> completedStates = null;
    
    private JobState(boolean isCompleted) {
    	this.isCompleted = isCompleted;
    }
    
    public static List<String> getCompletedJobStateNames() {
    	if(completedStates == null) {
    		completedStates = Lists.newArrayList();
	    	for(JobState state : values()) {
	    		if(state.isCompleted) {
	    			completedStates.add(state.name());
	    		}
	    	}
    	}
    	return completedStates;
    }
}
