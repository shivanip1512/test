package com.cannontech.common.util.xml;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.xml.xpath.XPathException;

import com.cannontech.common.temperature.CelsiusTemperature;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.temperature.TemperatureUnit;

public class YukonXPathTemplate extends SimpleXPathTemplate {

    public static final DateTimeFormatter PERIOD_START_TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
    
    public Temperature evaluateAsTemperature(String expression) {
        Temperature temperature = null;
        
        Double temperatureValue = evaluateAsDouble(expression, false);
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
        String enumString = evaluateAsString(expression);
        try {
            return XmlUtils.toEnum(enumString, enumClass);
        } catch (IllegalArgumentException e) {
            throw new XPathException(enumString + " is not a legal representation");
        }
    }

    /**
     * This method will attempt to return the EnumSet that may be stored in an xpath expression.  This method also handles
     * upper casing the text in the XPath expression and also putting underscores in for spaces.
     * @throws XPathException
     */
    public <E extends Enum<E>> EnumSet<E> evaluateAsEnumSet(String expression, Class<E> enumClass) throws XPathException {
        List<String> enumStrings = evaluateAsStringList(expression);
        EnumSet<E> enumSet = EnumSet.noneOf(enumClass);
        for (String enumString : enumStrings) {
            try {
                enumSet.add(XmlUtils.toEnum(enumString, enumClass));
            } catch (IllegalArgumentException e) {
                throw new XPathException(enumString + " is not a legal representation");
            }
        }
        return enumSet;
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