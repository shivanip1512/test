package com.cannontech.amr.macsscheduler.model;

import java.util.Arrays;
import java.util.Date;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.macs.message.Schedule;

public class MacsSchedule implements YukonPao{

    public enum State {
        RUNNING(Schedule.STATE_RUNNING),
        WAITING(Schedule.STATE_WAITING),
        PENDING(Schedule.STATE_PENDING),
        DISABLED(Schedule.STATE_DISABLED);

        private String state;

        State(String state) {
            this.state = state;
        }

        public String getStateString() {
            return state;
        }
        public static State getState(String value) {
            return Arrays.stream(State.values())
                    .filter(s -> s.getStateString().equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Could not find state=" + value));
        }
    }
        
    public enum Status {
        NONE(Schedule.LAST_STATUS_NONE),
        ERROR(Schedule.LAST_STATUS_ERROR),
        FINISHED(Schedule.LAST_STATUS_FINISHED);

        private String status;

        Status(String status) {
            this.status = status;
        }

        public String getStatusString() {
            return status;
        }
        public static Status getStatus(String value) {
            return Arrays.stream(Status.values())
                    .filter(s -> s.getStatusString().equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Could not find status=" + value));
        }
    }
        
    private int id;
    private String scheduleName;
    private String categoryName;
    private PaoType type = PaoType.SCRIPT;
    private State state = State.WAITING;
    private boolean updatingState;
    private MacsStartPolicy startPolicy = new MacsStartPolicy();
    private MacsStopPolicy stopPolicy = new MacsStopPolicy();
    private MacsSimpleOptions simpleOptions;
    private MacsScriptOptions scriptOptions;
    private MacsScriptTemplate template = MacsScriptTemplate.NO_TEMPLATE;
    
    private Status status = Status.NONE;
    private Date lastRunTime;    
    private Date nextRunTime;
    private Date nextStopTime;
    
    private boolean editable;

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Date getNextStopTime() {
        return nextStopTime;
    }

    public void setNextStopTime(Date nextStopTime) {
        this.nextStopTime = nextStopTime;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }
    
    public MacsStartPolicy getStartPolicy() {
        return startPolicy;
    }

    public void setStartPolicy(MacsStartPolicy startPolicy) {
        this.startPolicy = startPolicy;
    }

    public MacsStopPolicy getStopPolicy() {
        return stopPolicy;
    }

    public void setStopPolicy(MacsStopPolicy stopPolicy) {
        this.stopPolicy = stopPolicy;
    }

    public MacsSimpleOptions getSimpleOptions() {
        return simpleOptions;
    }

    public void setSimpleOptions(MacsSimpleOptions simpleOptions) {
        this.simpleOptions = simpleOptions;
    }

    public MacsScriptOptions getScriptOptions() {
        return scriptOptions;
    }

    public void setScriptOptions(MacsScriptOptions scriptOptions) {
        this.scriptOptions = scriptOptions;
    }

    public MacsScriptTemplate getTemplate() {
        return template;
    }

    public void setTemplate(MacsScriptTemplate template) {
        this.template = template;
    }

    public Date getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(Date lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getNextRunTime() {
        return nextRunTime;
    }

    public void setNextRunTime(Date nextRunTime) {
        this.nextRunTime = nextRunTime;
    }
            
    public boolean getShowControllable() {
        return isEditable() && !isUpdating() && !isDisabled();
    }
    
    public boolean getShowToggleButton() {
        return isEditable() && !isUpdating();
    }
    
    public boolean isRunning() {
        return state == State.RUNNING;
    }
    
    public boolean isWaiting() {
        return state == State.RUNNING;
    }
    
    public boolean isPending() {
        return state == State.PENDING;
    }
    
    public boolean isDisabled() {
        return state == State.DISABLED;
    }

    public boolean isUpdating() {
        return updatingState;
    }

    public void setUpdatingState(boolean updatingState) {
        this.updatingState = updatingState;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public boolean isScript(){
        return type == PaoType.SCRIPT;
    }
    
    public boolean isSimple(){
        return type == PaoType.SIMPLE_SCHEDULE;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        try {
            return new PaoIdentifier(id, type);
        } catch (IllegalArgumentException e) {
            // We don't have a valid PaoType yet.
            return null;
        }
    }
}
