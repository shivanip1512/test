package com.cannontech.common.util.xml;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.xml.xpath.XPathException;

import com.cannontech.common.temperature.CelsiusTemperature;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.util.EnumUtils;

public class YukonXPathTemplate extends SimpleXPathTemplate {

    private static final DateTimeFormatter PERIOD_START_TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
    
    public Temperature evaluateAsTemperature(String expression) {
        Temperature temperature = null;
        
        Double temperatureValue = evaluateAsDouble(expression);
        if(temperatureValue == null) {
            return null;
        }
        
        String temperatureUnit = evaluateAsString(expression+"/@unit");
        if (TemperatureUnit.fromAbbreviation(temperatureUnit) == TemperatureUnit.CELSIUS) {
            temperature = new CelsiusTemperature(temperatureValue);
        } else {
            temperature = new FahrenheitTemperature(temperatureValue);
        }
        return temperature;
    }
    
    /**
     * This method will attempt to return the Enum that may be stored in an xpath expression.  This method also handles
     * upper casing the text in the XPath expression and also putting underscores in for spaces.  This way a user can put in a more 
     * readable 'Weekday Weekend' for the enum 'WEEKDAY_WEEKEND'.
     * 
     * @throws XPathException
     */
    public <E extends Enum<E>> E evaluateAsEnum(String expression, Class<E> enumClass) throws XPathException {
        String originalStringValue = evaluateAsString(expression);
        if (StringUtils.isEmpty(originalStringValue)) {
            return null;
        }

        String stringValue = EnumUtils.convertToEnumFormat(originalStringValue);

        // Try getting the value from the enum
        try {
            return Enum.valueOf(enumClass, stringValue);
        } catch (IllegalArgumentException e) {}
        
        // See if there is an xmlRepresentation of this value.
        E enumValue = XmlUtils.findEnumFromXmlRepresentation(originalStringValue, enumClass);
        if (enumValue != null) {
            return enumValue;
        }
        
        throw new XPathException(stringValue + " is not a legal representation of " + enumClass);
    }

    /**
     * This method takes the expression supplied and tries to return a LocalTime using the format hh:mm.
     */
    public LocalTime evaluateAsLocalTime(String expression) {
        String stringValue = evaluateAsString(expression);
        if (StringUtils.isBlank(stringValue)) {
            return null;
        }

        try {
            return PERIOD_START_TIME_FORMATTER.parseLocalTime(stringValue);
        } catch (IllegalArgumentException e) {
            throw new XPathException(stringValue + " is not a legal representation. [hh:mm]");
        }
    }
}