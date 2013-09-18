package com.cannontech.web.updater.dr;

import org.apache.log4j.Logger;
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

    @Autowired private WeatherDataService weatherDataService;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private DateFormattingService dateFormattingService;

    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = resolver.getMessageSourceAccessor(userContext);
        String value = "";
        try {
            int paoId = getPaoId(identifier);
            WeatherLocation weatherLocation = weatherDataService.getWeatherLocationForPao(paoId);

            if (weatherLocation == null) {
                value = messageAccessor.getMessage("yukon.web.defaults.na");
            } else {
                WeatherObservation weatherObs = weatherDataService.getCurrentWeatherObservation(weatherLocation);
                String localTimestamp = dateFormattingService.format(weatherObs.getTimestamp(),
                                            DateFormattingService.DateFormatEnum.BOTH,
                                            userContext);
                if (isTemperatureField(identifier)) {
                    Double temperature = weatherObs.getTemperature();
                    if (temperature != null) {
                        value = messageAccessor.getMessage("yukon.web.modules.dr.estimatedLoad.weatherInput.fahrenheit", temperature, localTimestamp);
                    } else {
                        value = messageAccessor.getMessage("yukon.web.modules.dr.estimatedLoad.weatherInput.noData", localTimestamp);
                    }
                } else if (isHumidityField(identifier)) {
                    Double humidity = weatherObs.getHumidity();
                    if (humidity != null) {
                        value = messageAccessor.getMessage("yukon.web.modules.dr.estimatedLoad.weatherInput.humidity", humidity, localTimestamp);
                    } else {
                        value = messageAccessor.getMessage("yukon.web.modules.dr.estimatedLoad.weatherInput.noData", localTimestamp);
                    }
                } else {
                    value = messageAccessor.getMessage("yukon.web.defaults.na");
                    log.error("Identifier: " + identifier + " is invalid for a WEATHER_STATION data updater");
                }
            }
        } catch(DynamicDataAccessException e) {
            // dispatch not connected
            value = messageAccessor.getMessage("yukon.web.defaults.na");
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

    private boolean isTemperatureField(String identifier) {
        return "TEMP".equals(identifier.split("\\/")[1]);
    }

    private boolean isHumidityField(String identifier) {
        return "HUMIDITY".equals(identifier.split("\\/")[1]);
    }
}