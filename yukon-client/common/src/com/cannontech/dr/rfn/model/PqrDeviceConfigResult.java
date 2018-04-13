package com.cannontech.dr.rfn.model;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;

public class PqrDeviceConfigResult {
    private LiteLmHardwareBase hardware;
    private Map<LmHardwareCommandType, PqrConfigCommandStatus> commandStatuses = new HashMap<>();
    
    public PqrDeviceConfigResult(LiteLmHardwareBase hardware, PqrConfig config) {
        this.hardware = hardware;
        config.getPqrEnable().ifPresent(e -> {
            commandStatuses.put(LmHardwareCommandType.PQR_ENABLE, PqrConfigCommandStatus.IN_PROGRESS);
        });
        //TODO LOV Parameters
        //TODO LOV Event Duration
        //TODO LOV Delay Duration
        //TODO LOF Parameters
        //TODO LOF Event Duration
        //TODO LOF Delay Duration
        //TODO General Event Separation
    }
    
    public LiteLmHardwareBase getInventory() {
        return hardware;
    }
    
    public Map<LmHardwareCommandType, PqrConfigCommandStatus> getCommandStatuses() {
        return commandStatuses;
    }
    
    public boolean isComplete() {
        return commandStatuses.entrySet()
                              .stream()
                              .filter(entry -> entry.getValue() == PqrConfigCommandStatus.IN_PROGRESS)
                              .findAny()
                              .isPresent();
    }
    
    public void setStatus(LmHardwareCommandType commandType, PqrConfigCommandStatus status) {
        commandStatuses.put(commandType, status);
    }
    
    public void complete() {
        commandStatuses.entrySet().forEach(entry -> {
            if (entry.getValue() == PqrConfigCommandStatus.IN_PROGRESS) {
                entry.setValue(PqrConfigCommandStatus.FAILED);
            }
        });
    }
    
    public PqrConfigCommandStatus getOverallStatus() {
        if (commandStatuses.containsValue(PqrConfigCommandStatus.IN_PROGRESS)) {
            return PqrConfigCommandStatus.IN_PROGRESS;
        } else if (commandStatuses.containsValue(PqrConfigCommandStatus.FAILED)) {
            return PqrConfigCommandStatus.FAILED;
        } else {
            return PqrConfigCommandStatus.SUCCESS;
        }
    }
}
