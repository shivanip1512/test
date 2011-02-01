package com.cannontech.amr.statusPointMonitoring.model;

public class StatusPointMonitorProcessor {

    private Integer statusPointMonitorProcessorId;
    private String prevState;
    private String nextState;
    private OutageActionType actionType;
    
    public void setStatusPointMonitorProcessorId(Integer statusPointMonitorProcessorId) {
        this.statusPointMonitorProcessorId = statusPointMonitorProcessorId;
    }
    
    public Integer getStatusPointMonitorProcessorId() {
        return statusPointMonitorProcessorId;
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
    
    /**
     * This getter is prefixed with "transient" so JSON does not serialize it and throw an
     * exception on states that are not able to be converted to integers
     * 
     * @return int
     * @throws NumberFormatException
     */
    public int transientGetPrevStateInt() throws NumberFormatException {
        return convertStateToInt(prevState);
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
    
    /**
     * This getter is prefixed with "transient" so JSON does not serialize it and throw an
     * exception on states that are not able to be converted to integers
     * 
     * @return int
     * @throws NumberFormatException
     */
    public int transientGetNextStateInt() throws NumberFormatException {
        return convertStateToInt(nextState);
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
        StatusPointMonitorProcessor other = (StatusPointMonitorProcessor) obj;
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
        return String.format("StatusPointMonitorProcessor [actionType=%s, nextState=%s, prevState=%s, statusPointMonitorProcessorId=%s]",
                             actionType, nextState, prevState,
                             statusPointMonitorProcessorId);
    }
}