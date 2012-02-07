package com.cannontech.yukon.api.stars.endpoint.endpointMappers;

import org.jdom.Element;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.YukonXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.yukon.api.stars.model.ManualThermostatSetting;
import com.google.common.collect.Sets;

public class ManualThermostatSettingElementRequestMapper implements ObjectMapper<Element, ManualThermostatSetting> {

    @Override
    public ManualThermostatSetting map(Element sendManualThermostatSettingRequestElement) throws ObjectMappingException {

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(sendManualThermostatSettingRequestElement);

        ManualThermostatSetting manualThermostatSetting = new ManualThermostatSetting();
        manualThermostatSetting.setSerialNumbers(Sets.newHashSet(yukonTemplate.evaluateAsStringList("//y:serialNumber")));
        manualThermostatSetting.setThermostatMode(ThermostatMode.valueOf(yukonTemplate.evaluateAsString("//y:mode").toUpperCase()));
        manualThermostatSetting.setFanState(ThermostatFanState.valueOf(yukonTemplate.evaluateAsString("//y:fanState").toUpperCase()));
        manualThermostatSetting.setHoldTemperature(yukonTemplate.evaluateAsBoolean("//y:holdTemperature", false));
        manualThermostatSetting.setHeatTemperature(yukonTemplate.evaluateAsTemperature("//y:heatTemperature"));
        manualThermostatSetting.setCoolTemperature(yukonTemplate.evaluateAsTemperature("//y:coolTemperature"));

        // If both the cool and heat are set, treat this command as an auto mode command and send both temperatures.
        if (manualThermostatSetting.getCoolTemperature() != null  &&  manualThermostatSetting.getHeatTemperature() != null) {
            manualThermostatSetting.setAutoModeCommand(true);
        }
        
        return manualThermostatSetting;
    }
}