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
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.ecobee.model.EcobeeZeusDeviceReading;
import com.cannontech.dr.ecobee.service.EcobeeZeusPointUpdateService;
import com.cannontech.web.api.dr.ecobee.message.EcobeeZeusRuntimeData;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.protobuf.Timestamp;

@RestController
@CheckRoleProperty(YukonRoleProperty.SHOW_ECOBEE)
public class EcobeeZeusApiController {
    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusApiController.class);
    @Autowired private EcobeeZeusPointUpdateService ecobeeZeusPointUpdateService;

    @PostMapping(value = "/ecobee/runtimeData", consumes = "application/x-protobuf")
    public void runtimeData(@RequestBody EcobeeZeusRuntimeData.ecp_thermostat_message thermostatMessage) throws IOException {
        log.debug("Received Runtime data : " + thermostatMessage.toString());
        EcobeeZeusDeviceReading deviceReading = buildEcobeeZeusDeviceReading(thermostatMessage);
        ecobeeZeusPointUpdateService.updatePointData(deviceReading);
    }

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

            Integer commStatus = thermostatMessage.getThermostatState().getConnectionStateValue();
            ecobeeZeusDeviceReading.setCommStatus(commStatus);
        }

        if (thermostatMessage.getThermostatRuntime() != null) {
            Integer coolStage1 = thermostatMessage.getThermostatRuntime().getCoolStage1OnValue();
            ecobeeZeusDeviceReading.setCoolStage1(coolStage1);
            Integer heatStage1 = thermostatMessage.getThermostatRuntime().getHeatStage1OnValue();
            ecobeeZeusDeviceReading.setHeatStage1(heatStage1);
        }

        return ecobeeZeusDeviceReading;

    }

}
