package com.cannontech.amr.scheduledGroupRequestExecution.tasks;

import java.util.Set;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;

public class ScheduledGroupRequestExecutionJobTaskContainer {

    private String taskGroupName;
    private String taskName;
    private CommandRequestExecutionType taskCreType;
    private String jobCron;
    private int jobUserId;
    private String command;
    private Set<Attribute> attributes;
    
    public ScheduledGroupRequestExecutionJobTaskContainer(String taskGroupName,
                                                          String taskName,
                                                          CommandRequestExecutionType taskCreType,
                                                          String jobCron,
                                                          int jobUserId,
                                                          String command, 
                                                          Set<Attribute> attributes) {
        this.taskGroupName = taskGroupName;
        this.taskName = taskName;
        this.taskCreType = taskCreType;
        this.jobCron = jobCron;
        this.jobUserId = jobUserId;
        this.command = command;
        this.attributes = attributes;
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
        if ((existing.getAttributes() != null && existing.getAttributes().size() > 0 && other.getAttributes() != null && other.getAttributes().size() > 0 && !existing.getAttributes().containsAll(other.getAttributes())) // new attribute
            || (existing.getCommand() != null && other.getCommand() != null && !existing.getCommand().equals(other.getCommand())) // new command
            || (existing.getAttributes() != null && existing.getAttributes().size() > 0 && other.getCommand() != null) // switching from command to attribute
            || (existing.getCommand() != null && other.getAttributes() != null) // switching from attribute to command 
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
    public Set<Attribute> getAttributes() {
        return attributes;
    }
    
    
}
