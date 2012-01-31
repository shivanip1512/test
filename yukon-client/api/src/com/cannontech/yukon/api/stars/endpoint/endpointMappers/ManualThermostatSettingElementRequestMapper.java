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
        manualThermostatSetting.setTemperature(yukonTemplate.evaluateAsTemperature("//y:temperature"));
//        Double temperatureValue = yukonTemplate.evaluateAsDouble("//y:temperature");
//        String temperatureUnit = yukonTemplate.evaluateAsString("//y:temperature/@unit");
//        Temperature temperature = null;
//        if (TemperatureUnit.fromAbbreviation(temperatureUnit) == TemperatureUnit.CELSIUS) {
//            temperature = new CelsiusTemperature(temperatureValue);
//        } else {
//            temperature = new FahrenheitTemperature(temperatureValue);
//        }
//        manualThermostatSetting.setTemperature(temperature);
        
        return manualThermostatSetting ;
    }
}