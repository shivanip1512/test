package com.cannontech.web.api.dr.ecobee;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.field.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.cannontech.dr.ecobee.model.EcobeeZeusDeviceReading;
import com.cannontech.dr.ecobee.service.EcobeeZeusPointUpdateService;
import com.cannontech.web.api.dr.ecobee.message.EcobeeZeusRuntimeData;
import com.cannontech.web.api.dr.ecobee.message.EcobeeZeusRuntimeData.ecp_thermostat_dr_event;
import com.cannontech.web.api.dr.ecobee.message.EcobeeZeusRuntimeData.ecp_thermostat_dr_event.dr_event_state;
import com.cannontech.web.api.dr.ecobee.message.EcobeeZeusRuntimeData.ecp_thermostat_runtime;
import com.cannontech.web.api.dr.ecobee.message.EcobeeZeusRuntimeData.ecp_thermostat_runtime.state_runtime;
import com.cannontech.web.api.dr.ecobee.message.EcobeeZeusRuntimeData.ecp_thermostat_state.ecp_thermostat_connection_state;
import com.google.protobuf.Timestamp;

@RestController
public class EcobeeZeusApiController {
    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusApiController.class);
    @Autowired private EcobeeZeusPointUpdateService ecobeeZeusPointUpdateService;

    @PostMapping(value = "/ecobee/runtimeData", consumes = "application/x-protobuf")
    public void runtimeData(@RequestBody EcobeeZeusRuntimeData.ecp_thermostat_message thermostatMessage) throws IOException {
        log.debug("Received Runtime data : " + thermostatMessage.toString());
        EcobeeZeusDeviceReading deviceReading = buildEcobeeZeusDeviceReading(thermostatMessage);
        ecobeeZeusPointUpdateService.updatePointData(deviceReading);
    }

    /**
     * Build EcobeeZeusDeviceReading from protobuf message.
     */
    private EcobeeZeusDeviceReading buildEcobeeZeusDeviceReading(EcobeeZeusRuntimeData.ecp_thermostat_message thermostatMessage) {

        EcobeeZeusDeviceReading ecobeeZeusDeviceReading = new EcobeeZeusDeviceReading();

        if (thermostatMessage.getThermostatInfo() != null) {
            String serialNumber = thermostatMessage.getThermostatInfo().getThermostatId();
            ecobeeZeusDeviceReading.setSerialNumber(serialNumber);
        }

        Timestamp timestamp = thermostatMessage.getMessageTimeUtc();
        if (timestamp != null) {
            Instant date = new Instant(FieldUtils.safeMultiply(timestamp.getSeconds(), 1000));
            ecobeeZeusDeviceReading.setDate(date);
        }

        if (thermostatMessage.getThermostatState() != null) {
            Float temperatureCoolSetpointDegF = thermostatMessage.getThermostatState().getTemperatureCoolSetpointDegF();
            ecobeeZeusDeviceReading.setSetCoolTempInF(temperatureCoolSetpointDegF);

            Float temperatureHeatSetpointDegF = thermostatMessage.getThermostatState().getTemperatureHeatSetpointDegF();
            ecobeeZeusDeviceReading.setSetHeatTempInF(temperatureHeatSetpointDegF);

            Float temperatureIndoorDegF = thermostatMessage.getThermostatState().getTemperatureIndoorDegF();
            ecobeeZeusDeviceReading.setIndoorTempInF(temperatureIndoorDegF);

            Float temperatureOutdoorDegF = thermostatMessage.getThermostatState().getTemperatureOutdoorDegF();
            ecobeeZeusDeviceReading.setOutdoorTempInF(temperatureOutdoorDegF);

            ecp_thermostat_connection_state connectionState = thermostatMessage.getThermostatState().getConnectionState();
            if (connectionState != null) {
                if (ecp_thermostat_connection_state.connected == connectionState) {
                    ecobeeZeusDeviceReading.setCommStatus(0); // 0 - connected
                } else if (ecp_thermostat_connection_state.disconnected == connectionState) {
                    ecobeeZeusDeviceReading.setCommStatus(1); // 1- disconnected
                }
            }
        }

        if (thermostatMessage.getThermostatRuntime() != null) {
            ecp_thermostat_runtime thermostat_runtime = thermostatMessage.getThermostatRuntime();
            state_runtime coolStage1 = thermostat_runtime.getCoolStage1On();
            if (coolStage1 != null && coolStage1 != state_runtime.non_applicable) {
                if (state_runtime.on == coolStage1) {
                    ecobeeZeusDeviceReading.setStateValue(1); // Cool-On
                } else {
                    ecobeeZeusDeviceReading.setStateValue(2); // Off
                }
            }

            state_runtime heatStage1 = thermostat_runtime.getHeatStage1On();
            if (heatStage1 != null && heatStage1 != state_runtime.non_applicable) {
                if (state_runtime.on == heatStage1) {
                    ecobeeZeusDeviceReading.setStateValue(0); // Heat-On
                } else {
                    ecobeeZeusDeviceReading.setStateValue(2); // Off
                }
            }
        }

        if (thermostatMessage.getThermostatProgram() != null) {
            ecp_thermostat_dr_event drEvent = thermostatMessage.getThermostatProgram().getEventDr();
            if (drEvent != null) {
                dr_event_state eventState = drEvent.getEventState();
                if (eventState != null) {
                    if (eventState == dr_event_state.dr_setback) {
                        ecobeeZeusDeviceReading.setControlStatus(TrueFalse.TRUE);
                    } else {
                        ecobeeZeusDeviceReading.setControlStatus(TrueFalse.FALSE); // False in case of dr_precool & None
                    }
                }
            }
        }
        return ecobeeZeusDeviceReading;

    }

}
