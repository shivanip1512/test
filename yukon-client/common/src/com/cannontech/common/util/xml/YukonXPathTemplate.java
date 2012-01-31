package com.cannontech.common.util.xml;

import com.cannontech.common.temperature.CelsiusTemperature;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.temperature.TemperatureUnit;

public class YukonXPathTemplate extends SimpleXPathTemplate {

    public Temperature evaluateAsTemperature(String expression) {
        Temperature temperature = null;
        
        Double temperatureValue = evaluateAsDouble(expression);
        String temperatureUnit = evaluateAsString(expression+"/@unit");
        if (TemperatureUnit.fromAbbreviation(temperatureUnit) == TemperatureUnit.CELSIUS) {
            temperature = new CelsiusTemperature(temperatureValue);
        } else {
            temperature = new FahrenheitTemperature(temperatureValue);
        }
        return temperature;
    }
}