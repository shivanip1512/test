package com.cannontech.web.updater.dr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.weather.WeatherDataService;
import com.cannontech.common.weather.WeatherLocation;
import com.cannontech.common.weather.WeatherObservation;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class WeatherDataBackingService implements UpdateBackingService {
    private Logger log = YukonLogManager.getLogger(WeatherDataBackingService.class);
    private String baseKey = "yukon.web.modules.adminSetup.config.weather.weatherInput.";

    @Autowired private WeatherDataService weatherDataService;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private DateFormattingService dateFormattingService;

    // data should be less than 1 hour old, this gives us some wiggle room
    private Duration weatherDataValidDuration = Duration.standardMinutes(90);

    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = resolver.getMessageSourceAccessor(userContext);

        int paoId = getPaoId(identifier);
        WeatherLocation weatherLocation = weatherDataService.findWeatherLocationForPao(paoId);

        if (weatherLocation == null) {
            if (Field.getFrom(identifier) == Field.JSON_META_DATA) {
                return getJson(Collections.singletonMap("invalidPaoError", "true"));
            }
            return messageAccessor.getMessage("yukon.common.na");
        }

        WeatherObservation weatherObs;
        try {
            weatherObs = weatherDataService.getCurrentWeatherObservation(weatherLocation);
        } catch(DynamicDataAccessException e) {
            if (Field.getFrom(identifier) == Field.JSON_META_DATA) {
                return getJson(Collections.singletonMap("dispatchError", "true"));
            }
            return messageAccessor.getMessage("yukon.common.na");
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
            if (timestamp != null) {
                String localTimestamp =
                    dateFormattingService.format(timestamp, DateFormattingService.DateFormatEnum.BOTH, userContext);
                value = messageAccessor.getMessage(baseKey + "timestamp", localTimestamp);
            } else {
                value = messageAccessor.getMessage(baseKey + "noData");
            }
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

            if (weatherObs.getTimestamp() == null) {
                metaData.put("timestamp", "nodata");
            } else if (weatherObs.getTimestamp().isBefore(Instant.now().minus(weatherDataValidDuration))) {
                metaData.put("timestamp", "old");
            } else {
                metaData.put("timestamp", "valid");
            }

            value = getJson(metaData);
            break;
        default:
        case UNKNOWN_FIELD:
            value = messageAccessor.getMessage("yukon.common.na");
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

    private String getJson(Map<String, String> map) {
        try {
            return JsonUtils.toJson(map);
        } catch (JsonProcessingException e) {
            log.error("jsonObjectMapper is unable to write json string from map", e);
            throw new RuntimeException(e);
        }
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