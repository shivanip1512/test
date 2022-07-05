package com.cannontech.dr.eatonCloud.job.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.cannontech.dr.eatonCloud.service.EatonCloudSendControlService.CommandParam;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;

public class ShedParamHeper {
    
    private ShedParamHeper() {

    }
    
    /**
     * Returns restore parameters
     */
    public static Map<String, Object> getRestoreParams(LMEatonCloudStopCommand command, int eventId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(CommandParam.EVENT_ID.getParamName(), eventId);
        params.put(CommandParam.FLAGS.getParamName(), 0);
        return params;
    }
    
    /**
     * Returns shed parameters
     */
    public static Map<String, Object> getShedParams(LMEatonCloudScheduledCycleCommand command, int eventId) {
        Map<String, Object> params = new LinkedHashMap<>();
        long startTimeSeconds = System.currentTimeMillis() / 1000;
        long stopTimeSeconds = 0;

        if (command.getControlStartDateTime() != null) {
            startTimeSeconds = command.getControlStartDateTime().getMillis() / 1000;
        }

        if (command.getControlEndDateTime() != null) {
            stopTimeSeconds = command.getControlEndDateTime().getMillis() / 1000;
        }

        double durationSeconds = stopTimeSeconds - startTimeSeconds;
        int cycleCount = 1;
        if (stopTimeSeconds != 0) {
            cycleCount = (int) Math.ceil(durationSeconds / command.getDutyCyclePeriod());
        }

        /*
         * See LCR Control Command Payloads reference:
         * https://confluence-prod.tcc.etn.com/pages/viewpage.action?pageId=137056391
         * 
         * randomization | stop flag
         * No Ramp | 0 | 0
         * Ramp In | 2 | 0
         * Ramp Out | 1 | 0
         * Ramp In/Out | 2 | 1
         */

        int randomization = 0;
        int stopFlag = 0;

        if (command.getIsRampIn()) {
            randomization = 2;
        }
        if (command.getIsRampOut() && !command.getIsRampIn()) {
            randomization = 1;
        }
        if (command.getIsRampIn() && command.getIsRampOut()) {
            stopFlag = 1;
        }

        params.put(CommandParam.VRELAY.getParamName(), command.getVirtualRelayId() - 1);
        params.put(CommandParam.CYCLE_PERCENT.getParamName(), command.getDutyCyclePercentage());
        params.put(CommandParam.CYCLE_PERIOD.getParamName(), command.getDutyCyclePeriod() / 60);
        params.put(CommandParam.CYCLE_COUNT.getParamName(), cycleCount);
        params.put(CommandParam.START_TIME.getParamName(), startTimeSeconds);
        params.put(CommandParam.EVENT_ID.getParamName(), eventId);
        params.put(CommandParam.CRITICALITY.getParamName(), command.getCriticality());
        params.put(CommandParam.RANDOMIZATION.getParamName(), randomization);
        params.put(CommandParam.CONTROL_FLAGS.getParamName(), 0);
        params.put(CommandParam.STOP_TIME.getParamName(), stopTimeSeconds);
        params.put(CommandParam.STOP_FLAGS.getParamName(), stopFlag);
        return params;
    }
}