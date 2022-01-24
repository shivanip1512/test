package com.cannontech.dr.eatonCloud.service;

import java.util.Set;

import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;

public interface EatonCloudSendControlService {
   
    public enum CommandParam {
        FLAGS("flags"),
        VRELAY("vrelay"),
        CYCLE_PERCENT("cycle percent"),
        CYCLE_PERIOD("cycle period"),
        CYCLE_COUNT("cycle count"),
        START_TIME("start time"),
        CONTROL_FLAGS("control flags"),
        STOP_TIME("stop time"),
        STOP_FLAGS("stop flags"), 
        CRITICALITY("criticality"),
        EVENT_ID("event id"),
        RANDOMIZATION("randomization");
        
        private String paramName;

        private CommandParam(String paramName) {
            this.paramName = paramName;
        }

        public String getParamName() {
            return paramName;
        } 
    }
    
    void sendInitialShedCommand(int programId, Set<Integer> devices, LMEatonCloudScheduledCycleCommand command,
            Integer eventId);
    
    void sendRestoreCommands(Set<Integer> devices, LMEatonCloudStopCommand command, Integer eventId);
}
