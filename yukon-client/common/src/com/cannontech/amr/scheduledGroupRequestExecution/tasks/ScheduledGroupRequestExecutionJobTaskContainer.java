package com.cannontech.amr.scheduledGroupRequestExecution.tasks;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;

public class ScheduledGroupRequestExecutionJobTaskContainer {

    private String taskGroupName;
    private String taskName;
    private CommandRequestExecutionType taskCreType;
    private String jobCron;
    private int jobUserId;
    private String command;
    private Attribute attribute;
    
    public ScheduledGroupRequestExecutionJobTaskContainer(String taskGroupName,
                                                          String taskName,
                                                          CommandRequestExecutionType taskCreType,
                                                          String jobCron,
                                                          int jobUserId,
                                                          String command, 
                                                          Attribute attribute) {
        this.taskGroupName = taskGroupName;
        this.taskName = taskName;
        this.taskCreType = taskCreType;
        this.jobCron = jobCron;
        this.jobUserId = jobUserId;
        this.command = command;
        this.attribute = attribute;
    }
    
    @Override
    public boolean equals(Object o) {
        
        ScheduledGroupRequestExecutionJobTaskContainer existing = this;
        ScheduledGroupRequestExecutionJobTaskContainer other = (ScheduledGroupRequestExecutionJobTaskContainer)o;
        
        // basic differences
        if (!existing.getTaskGroupName().equals(other.getTaskGroupName())
            || !existing.getTaskName().equals(other.getTaskName())
            || !existing.getTaskCreType().equals(other.getTaskCreType())
            || !existing.getJobCron().equals(other.getJobCron())
            || (existing.getJobUserId() != other.getJobUserId())) {
            
            return false;
        }
        
        // new cmd/attr or switching cmd/attr?
        if ((existing.getAttribute() != null && other.getAttribute() != null && !existing.getAttribute().equals(other.getAttribute())) // new attribute
            || (existing.getCommand() != null && other.getCommand() != null && !existing.getCommand().equals(other.getCommand())) // new command
            || (existing.getAttribute() != null && other.getCommand() != null) // switching from command to attribute
            || (existing.getCommand() != null && other.getAttribute() != null) // switching from attribute to command 
        ) {
            return false;
        }
        
        return true;
    }

    public String getTaskGroupName() {
        return taskGroupName;
    }
    public String getTaskName() {
        return taskName;
    }
    public CommandRequestExecutionType getTaskCreType() {
        return taskCreType;
    }
    public String getJobCron() {
        return jobCron;
    }
    public int getJobUserId() {
        return jobUserId;
    }
    public String getCommand() {
        return command;
    }
    public Attribute getAttribute() {
        return attribute;
    }
    
    
}
