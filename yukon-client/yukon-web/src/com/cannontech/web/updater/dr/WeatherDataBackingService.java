package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.weather.WeatherDataService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class WeatherDataBackingService implements UpdateBackingService {

    @Autowired private WeatherDataService weatherDataService;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private YukonUserContextMessageSourceResolver resolver;

    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = resolver.getMessageSourceAccessor(userContext);
        String value = "";
        try {
            int pointId = getPointId(identifier);
            PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
            if (pointValue.getPointQuality() != PointQuality.Normal) {
                value = messageAccessor.getMessage("yukon.web.modules.dr.estimatedLoad.weatherStation.uninitialized");
            } else {
                value = Double.toString(pointValue.getValue());
                if (isTempField(identifier)) {
                    value += " " + messageAccessor.getMessage("yukon.web.modules.dr.estimatedLoad.weatherInput.fahrenheitUnit");
                } else if(isHumidityField(identifier)) {
                    value += " " + messageAccessor.getMessage("yukon.web.modules.dr.estimatedLoad.weatherInput.humidityUnit");
                }
                value += " - " + pointValue.getPointDataTimeStamp();
            }
        } catch(DynamicDataAccessException e) {
            value = messageAccessor.getMessage("yukon.web.defaults.na");
        }

        return value;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate,
                                               YukonUserContext userContext) {
        return true;
    }

    private int getPointId(String identifier) {
        return Integer.valueOf(identifier.split("\\/")[0]);
    }

    private boolean isTempField(String identifier) {
        return "temp".equals(identifier.split("\\/")[1]);
    }

    private boolean isHumidityField(String identifier) {
        return "humidity".equals(identifier.split("\\/")[1]);
    }
}