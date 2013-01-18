package com.cannontech.common.util.xml;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.xml.xpath.XPathException;

import com.cannontech.common.temperature.CelsiusTemperature;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class YukonXPathTemplate extends SimpleXPathTemplate {

    
    private static final DateTimeFormatter PERIOD_START_TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
    
    private static LoadingCache<Class<Enum<?>>, Map<String, Enum<?>>> enumValueLookup = 
            CacheBuilder.newBuilder().concurrencyLevel(10).build(new CacheLoader<Class<Enum<?>>, Map<String, Enum<?>>>() {
                @Override
                public Map<String, Enum<?>> load(Class<Enum<?>> key) throws Exception {
                    Enum<?>[] enumConstants = key.getEnumConstants();
                    Builder<String, Enum<?>> builder = ImmutableMap.builder();
                    for (Enum<?> enum1 : enumConstants) {
                        DatabaseRepresentationSource drs = (DatabaseRepresentationSource) enum1;
                        Object databaseRepresentation = drs.getDatabaseRepresentation();
                        String stringRepresentation;
                        if (databaseRepresentation instanceof Number) {
                            long longValue = ((Number) databaseRepresentation).longValue();
                            stringRepresentation = Long.toString(longValue);
                        } else if (databaseRepresentation instanceof String){
                            stringRepresentation = databaseRepresentation.toString();
                        } else {
                            throw new UnsupportedOperationException("DatabaseRepresentationSource for " + key + " returned a non-Number, non-String for " + enum1);
                        }
                        builder.put(stringRepresentation, enum1);
                    }
                    return builder.build();
                }
        });
    
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
        
        // Getting the string value from the expression and also converting it to an Enum format.
        String stringValue = evaluateAsString(expression);
        if (StringUtils.isBlank(stringValue)) {
            return null;
        }
        stringValue = stringValue.toUpperCase().replaceAll(" ", "_");

        // Converting the supplied string value to an Enum.
        E result;
        if (DatabaseRepresentationSource.class.isAssignableFrom(enumClass)) {
            @SuppressWarnings("unchecked") // This cast is needed since the LoadingCache class uses k and not ? extends k.  When they fix that we can remove this suppress warning.
            Map<String, Enum<?>> map = enumValueLookup.getUnchecked((Class<Enum<?>>) enumClass);
            Enum<?> enum1 = map.get(stringValue);
            result = enumClass.cast(enum1);
        } else {
            try {
                result = Enum.valueOf(enumClass, stringValue);
            } catch (IllegalArgumentException e) {
                throw new XPathException(stringValue + " is not a legal representation of " + enumClass);
            }
        }
        
        return result;
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