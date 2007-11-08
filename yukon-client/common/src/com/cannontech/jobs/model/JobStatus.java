package com.cannontech.jobs.model;

import java.util.Date;

public class JobStatus<J extends YukonJob> {
    private Integer id;
    private J job;
    private Date startTime;
    private Date stopTime;
    private JobState jobState;
    private String message;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public J getJob() {
        return job;
    }
    public void setJob(J job) {
        this.job = job;
    }
    public JobState getJobState() {
        return jobState;
    }
    public void setJobState(JobState jobStatus) {
        this.jobState = jobStatus;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getStopTime() {
        return stopTime;
    }
    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

}
