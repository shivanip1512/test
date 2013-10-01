package com.cannontech.web.updater.dr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.weather.WeatherDataService;
import com.cannontech.loadcontrol.weather.WeatherLocation;
import com.cannontech.loadcontrol.weather.WeatherObservation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class WeatherDataBackingService implements UpdateBackingService {
    private Logger log = YukonLogManager.getLogger(WeatherDataBackingService.class);
    private String baseKey = "yukon.web.modules.adminSetup.config.weather.weatherInput.";

    // data should be less than 1 hour old, this gives us some wiggle room
    private Duration weatherDataValidDuration = Duration.standardMinutes(90);
    @Autowired private WeatherDataService weatherDataService;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private DateFormattingService dateFormattingService;

    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = resolver.getMessageSourceAccessor(userContext);

        int paoId = getPaoId(identifier);
        WeatherLocation weatherLocation = weatherDataService.findWeatherLocationForPao(paoId);

        if (weatherLocation == null) {
            if (Field.getFrom(identifier) == Field.JSON_META_DATA) {
                return JSONObject.fromObject(Collections.singletonMap("invalidPaoError", "true")).toString();
            }
            return messageAccessor.getMessage("yukon.web.defaults.na");
        }

        WeatherObservation weatherObs;
        try {
            weatherObs = weatherDataService.getCurrentWeatherObservation(weatherLocation);
        } catch(DynamicDataAccessException e) {
            if (Field.getFrom(identifier) == Field.JSON_META_DATA) {
                return JSONObject.fromObject(Collections.singletonMap("dispatchError", "true")).toString();
            }
            return messageAccessor.getMessage("yukon.web.defaults.na");
        }

        String value;
        switch(Field.getFrom(identifier)) {
        case HUMIDITY:
            Double humidity = weatherObs.getHumidity();
            if (humidity != null) {
                value = messageAccessor.getMessage(baseKey + "humidity", humidity);
            } else {
                value = messageAccessor.getMessage(baseKey + "noData");
            }
            break;
        case TEMPERATURE:
            Double temperature = weatherObs.getTemperature();
            if (temperature != null) {
                value = messageAccessor.getMessage(baseKey + "fahrenheit", temperature);
            } else {
                value = messageAccessor.getMessage(baseKey + "noData");
            }
            break;
        case TIMESTAMP:
            Instant timestamp = weatherObs.getTimestamp();
            String localTimestamp = dateFormattingService.format(timestamp,
                                                                 DateFormattingService.DateFormatEnum.BOTH,
                                                                 userContext);
            value = messageAccessor.getMessage(baseKey + "timestamp", localTimestamp);
            break;
        case JSON_META_DATA:
            Map<String, String> metaData = new HashMap<>();
            metaData.put("paoId", Integer.toString(paoId));

            if (weatherObs.getTemperature() == null) {
                metaData.put("temperature", "nodata");
            } else {
                metaData.put("temperature", "valid");
            }

            if (weatherObs.getHumidity() == null) {
                metaData.put("humidity", "nodata");
             } else {
                 metaData.put("humidity", "valid");
             }

            if (weatherObs.getTimestamp().isBefore(Instant.now().minus(weatherDataValidDuration))) {
                metaData.put("timestamp", "old");
            } else {
                metaData.put("timestamp", "valid");
            }

            value = JSONObject.fromObject(metaData).toString();
            break;
        default:
        case UNKNOWN_FIELD:
            value = messageAccessor.getMessage("yukon.web.defaults.na");
            log.error("Identifier: " + identifier + " is invalid for a WEATHER_STATION data updater");
            break;
        }

        return value;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate,
                                               YukonUserContext userContext) {
        return true;
    }

    private int getPaoId(String identifier) {
        return Integer.valueOf(identifier.split("\\/")[0]);
    }

    static enum Field {
        TEMPERATURE, HUMIDITY, TIMESTAMP, JSON_META_DATA, UNKNOWN_FIELD
        ;
        /** Converts the field part of the identifier to a Field.*/
        public static Field getFrom(String identifier) {
            Field field;
            try {
                field = Field.valueOf(identifier.split("\\/")[1]);
            } catch (NullPointerException | IllegalArgumentException e) {
                field = UNKNOWN_FIELD;
            }
            return field;
        }
    }
}