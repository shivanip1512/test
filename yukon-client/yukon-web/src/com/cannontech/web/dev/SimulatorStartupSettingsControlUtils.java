package com.cannontech.web.dev;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.SimulatorStartupSettingsRequest;
import com.cannontech.simulators.message.request.SimulatorStartupSettingsStatusRequest;
import com.cannontech.simulators.message.response.SimulatorStartupSettingsResponse;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;

@Controller
@RequestMapping("/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)

/**
 * This class is used by simulator controllers to communicate with the SimulatorsService about the
 * startup settings for a given SimulatorType.
 */
public class SimulatorStartupSettingsControlUtils {
    
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
   
    private static final Logger log = YukonLogManager.getLogger(SimulatorStartupSettingsControlUtils.class);

    public SimulatorStartupSettingsResponseOrError getSimulatorStartupSettingsResponse(boolean runOnStartup, SimulatorType affectedSimulator) {
        try {
            SimulatorStartupSettingsResponse response = simulatorsCommunicationService.sendRequest(
                new SimulatorStartupSettingsRequest(runOnStartup, affectedSimulator), SimulatorStartupSettingsResponse.class);
            if (response.isSuccessful()) {
                return new SimulatorStartupSettingsResponseOrError(response);
            }
            else {
                Map<String, Object> json = new HashMap<>();
                json.put("hasError", true);
                if (runOnStartup) {
                    json.put("errorMessage", "Unable to enable automatic startup for the simulator of type: " + affectedSimulator.name() + "."); 
                } else {
                    json.put("errorMessage", "Unable to disable automatic startup for the simulator of type: " + affectedSimulator.name() + "."); 
                }
                return new SimulatorStartupSettingsResponseOrError(json);
            }
        } catch (Exception e) {
            log.error(e);
            Map<String, Object> json = new HashMap<>();
            json.put("hasError", true);
            json.put("errorMessage", "Unable to send message to Simulator Service: " + e.getMessage());
            return new SimulatorStartupSettingsResponseOrError(json);
        }
    }
    
    public SimulatorStartupSettingsResponseOrError getSimulatorStartupSettingsResponse(SimulatorType affectedSimulator) {
        try {
            SimulatorStartupSettingsResponse response = simulatorsCommunicationService.sendRequest(
                new SimulatorStartupSettingsStatusRequest(affectedSimulator), SimulatorStartupSettingsResponse.class);
            if (response.isSuccessful()) {
                return new SimulatorStartupSettingsResponseOrError(response);
            }
            else {
                Map<String, Object> json = new HashMap<>();
                json.put("hasError", true);
                json.put("errorMessage", "Unable to retrieve simulator startup settings for the simulator of type: " + affectedSimulator.name() + ".");
                return new SimulatorStartupSettingsResponseOrError(json);
            }
        } catch (Exception e) {
            log.error(e);
            Map<String, Object> json = new HashMap<>();
            json.put("hasError", true);
            json.put("errorMessage", "Unable to send message to Simulator Service: " + e.getMessage());
            return new SimulatorStartupSettingsResponseOrError(json);
        }
    }
    
    @RequestMapping("updateStartup")
    @ResponseBody
    public Map<String, Object> updateStartup(SimulatorType simulatorType, boolean runOnStartup, FlashScope flash) {
        SimulatorStartupSettingsResponseOrError startupResponse = getSimulatorStartupSettingsResponse(runOnStartup, simulatorType);
        if (startupResponse.response == null) {
            return startupResponse.errorJson;
        }
        return null; 
    }
    
    @RequestMapping("existingStartupStatus")
    @ResponseBody
    public Map<String, Object> existingStartupStatus(SimulatorType simulatorType, FlashScope flash) {
        SimulatorStartupSettingsResponseOrError startupResponse = getSimulatorStartupSettingsResponse(simulatorType);
        if (startupResponse.response == null) {
            return startupResponse.errorJson;
        }
        Map<String, Object> json = new HashMap<>();
        json.put("hasError", false);
        json.put("runOnStartup", startupResponse.response.isRunOnStartup());
        return json;
    }
}
