package com.cannontech.dr.rfn.model;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;

public class PqrDeviceConfigResult {
    private final LiteLmHardwareBase hardware;
    private final boolean unsupported;
    private final Map<LmHardwareCommandType, PqrConfigCommandStatus> commandStatuses = new HashMap<>();
    
    /**
     * Generates a result for an unsupported device.
     */
    public static PqrDeviceConfigResult unsupportedResult(LiteLmHardwareBase hardware) {
        return new PqrDeviceConfigResult(hardware);
    }
    
    /**
     * Generates a result for an unsupported device. Private, to force the use of the more explicitly named 
     * .unsupportedResult method.
     */
    private PqrDeviceConfigResult(LiteLmHardwareBase hardware) {
        this.hardware = hardware;
        unsupported = true;
    }
    
    /**
     * Generates a result for a supported device, with command statuses set to "in progress".
     */
    public PqrDeviceConfigResult(LiteLmHardwareBase hardware, PqrConfig config) {
        this.hardware = hardware;
        unsupported = false;
        config.getPqrEnable().ifPresent(e -> {
            commandStatuses.put(LmHardwareCommandType.PQR_ENABLE, PqrConfigCommandStatus.IN_PROGRESS);
        });
        
        // LOV Params: trigger, restore, trigger time, restore time
        if (config.hasLovParams()) {
            commandStatuses.put(LmHardwareCommandType.PQR_LOV_PARAMETERS, PqrConfigCommandStatus.IN_PROGRESS);
        }
        
        //LOV Event Durations: min duration, max duration
        if (config.hasLovEventDurations()) {
            commandStatuses.put(LmHardwareCommandType.PQR_LOV_EVENT_DURATION, PqrConfigCommandStatus.IN_PROGRESS);
        }
        
        //LOV Delay Duration: start random time, end random time
        if (config.hasLovDelayDurations()) {
            commandStatuses.put(LmHardwareCommandType.PQR_LOV_DELAY_DURATION, PqrConfigCommandStatus.IN_PROGRESS);
        }
        
        //LOF Parameters: trigger, restore, trigger time, restore time
        if (config.hasLofParams()) {
            commandStatuses.put(LmHardwareCommandType.PQR_LOF_PARAMETERS, PqrConfigCommandStatus.IN_PROGRESS);
        }
        //LOF Event Durations: min duration, max duration
        if (config.hasLofEventDurations()) {
            commandStatuses.put(LmHardwareCommandType.PQR_LOF_EVENT_DURATION, PqrConfigCommandStatus.IN_PROGRESS);
        }
        //LOF Delay Duration: start random time, end random time
        if (config.hasLofDelayDurations()) {
            commandStatuses.put(LmHardwareCommandType.PQR_LOF_DELAY_DURATION, PqrConfigCommandStatus.IN_PROGRESS);
        }
        //General Event Separation
        config.getMinimumEventSeparationOptional().ifPresent(e -> {
            commandStatuses.put(LmHardwareCommandType.PQR_EVENT_SEPARATION, PqrConfigCommandStatus.IN_PROGRESS);
        });
    }
    
    public LiteLmHardwareBase getHardware() {
        return hardware;
    }
    
    /**
     * Get the status of each individual command that must be sent to the device for this configuration operation.
     */
    public Map<LmHardwareCommandType, PqrConfigCommandStatus> getCommandStatuses() {
        return commandStatuses;
    }
    
    /**
     * Check to see if all messages are complete (i.e. not "in progress") for this device.
     */
    public boolean isComplete() {
        return unsupported == true
               || !commandStatuses.entrySet()
                                  .stream()
                                  .filter(entry -> entry.getValue() == PqrConfigCommandStatus.IN_PROGRESS)
                                  .findAny()
                                  .isPresent();
    }
    
    /**
     * Check if the device does not support PQR.
     */
    public boolean isUnsupported() {
        return unsupported;
    }
    
    /**
     * Set the status for a particular configuration message for this device.
     */
    public void setStatus(LmHardwareCommandType commandType, PqrConfigCommandStatus status) {
        commandStatuses.put(commandType, status);
    }
    
    /**
     * Force this device into a completed state by changing all the "in progress" commands to "failed".
     */
    public void complete() {
        commandStatuses.entrySet().forEach(entry -> {
            if (entry.getValue() == PqrConfigCommandStatus.IN_PROGRESS) {
                entry.setValue(PqrConfigCommandStatus.FAILED);
            }
        });
    }
    
    /**
     * Synthesizes an overall status for the device, based on the statuses of the individual messages. If any message is
     * "in progress", the device is "in progress". If the device is not in progress, and one or more commands failed,
     * the device is considered failed. If the device is not in progress and no commands failed, the device is 
     * successful.
     */
    public PqrConfigCommandStatus getOverallStatus() {
        if (unsupported == true) {
            return PqrConfigCommandStatus.UNSUPPORTED;
        } else if (commandStatuses.containsValue(PqrConfigCommandStatus.IN_PROGRESS)) {
            return PqrConfigCommandStatus.IN_PROGRESS;
        } else if (commandStatuses.containsValue(PqrConfigCommandStatus.FAILED)) {
            return PqrConfigCommandStatus.FAILED;
        } else {
            return PqrConfigCommandStatus.SUCCESS;
        }
    }
}
