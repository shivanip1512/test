package com.cannontech.amr.statusPointProcessing.model;

public class StatusPointMonitorMessageProcessor {

    private Integer statusPointMonitorMessageProcessorId;
    private String prevState;
    private String nextState;
    private OutageActionType actionType;
    
    public void setStatusPointMonitorMessageProcessorId(Integer statusPointMonitorMessageProcessorId) {
        this.statusPointMonitorMessageProcessorId = statusPointMonitorMessageProcessorId;
    }
    
    public Integer getStatusPointMonitorMessageProcessorId() {
        return statusPointMonitorMessageProcessorId;
    }
    
    public void setPrevState(String prevState) {
        this.prevState = prevState;
    }
    
    public String getPrevState() {
        return prevState;
    }
    
    public StatusPointMonitorStateType getPrevStateType() {
        return convertStateToSpecial(prevState);
    }
    
    public int getPrevStateInt() {
        try {
            return convertStateToInt(prevState);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public void setNextState(String nextState) {
        this.nextState = nextState;
    }
    
    public String getNextState() {
        return nextState;
    }
    
    public StatusPointMonitorStateType getNextStateType() {
        return convertStateToSpecial(nextState);
    }
    
    public int getNextStateInt() {
        try {
            return convertStateToInt(nextState);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public String getActionType() {
        return actionType.name();
    }
    
    public void setActionType(String actionType) {
        this.actionType = OutageActionType.valueOf(actionType);
    }
    
    public OutageActionType getActionTypeEnum() {
        return actionType;
    }

    public void setActionTypeEnum(OutageActionType actionType) {
        this.actionType = actionType;
    }
    
    public int convertStateToInt(String state) throws NumberFormatException {
        return Integer.parseInt(state);
    }
    
    public StatusPointMonitorStateType convertStateToSpecial(String state) {
        try {
            convertStateToInt(state);
            return StatusPointMonitorStateType.EXACT;
        } catch (NumberFormatException e) {
            return StatusPointMonitorStateType.valueOf(state);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((actionType == null) ? 0 : actionType.hashCode());
        result = prime * result
                 + ((nextState == null) ? 0 : nextState.hashCode());
        result = prime * result
                 + ((prevState == null) ? 0 : prevState.hashCode());
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
        StatusPointMonitorMessageProcessor other = (StatusPointMonitorMessageProcessor) obj;
        if (actionType == null) {
            if (other.actionType != null)
                return false;
        } else if (!actionType.equals(other.actionType))
            return false;
        if (nextState == null) {
            if (other.nextState != null)
                return false;
        } else if (!nextState.equals(other.nextState))
            return false;
        if (prevState == null) {
            if (other.prevState != null)
                return false;
        } else if (!prevState.equals(other.prevState))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("StatusPointMonitorMessageProcessor [actionType=%s, nextState=%s, prevState=%s, statusPointMonitorMessageProcessorId=%s]",
                             actionType, nextState, prevState,
                             statusPointMonitorMessageProcessorId);
    }
}