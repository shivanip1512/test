package com.cannontech.loadcontrol.scheduledWeatherDataUpdateExecutionTask.tasks;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoInfo;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.pao.dao.StaticPaoInfoDao;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.loadcontrol.weather.NoaaWeatherDataService;
import com.cannontech.loadcontrol.weather.NoaaWeatherDataServiceException;
import com.cannontech.loadcontrol.weather.WeatherDataService;
import com.cannontech.loadcontrol.weather.WeatherObservation;
import com.cannontech.loadcontrol.weather.WeatherStation;
import com.cannontech.message.dispatch.message.PointData;

public class ScheduledWeatherDataUpdateExecutionTask extends YukonTaskBase {

    private Logger log = YukonLogManager.getLogger(ScheduledWeatherDataUpdateExecutionTask.class);

    @Autowired DynamicDataSource dynamicDataSource;
    @Autowired PaoDao paoDao;
    @Autowired PointDao pointDao;
    @Autowired StaticPaoInfoDao staticPaoInfoDao;
    @Autowired SystemEventLogService systemEventLogService;
    @Autowired WeatherDataService weatherService;
    @Autowired NoaaWeatherDataService noaaWeatherService;
    @Autowired AttributeService attributeService;

    @Override
    public void start() {
        Instant start = new Instant();

        List<LiteYukonPAObject> weatherLocations = paoDao.getLiteYukonPAObjectByType(PaoType.WEATHER_LOCATION.getDeviceTypeId());
        Map<String, WeatherStation> weatherStationMap = noaaWeatherService.getAllWeatherStations();

        for (LiteYukonPAObject weatherLocation : weatherLocations) {
            int singlePaoId = weatherLocation.getPaoIdentifier().getPaoId();

            String weatherStationId = "";
            try {
                weatherStationId = staticPaoInfoDao.getValue(PaoInfo.WEATHER_LOCATION_STATIONID, singlePaoId);
                if (weatherStationMap.containsKey(weatherStationId)) {
                    WeatherStation weatherStation = weatherStationMap.get(weatherStationId);
                    WeatherObservation currentObservation = noaaWeatherService.getCurrentWeatherObservation(weatherStation);

                    PaoIdentifier weatherPao = new PaoIdentifier(singlePaoId, PaoType.WEATHER_LOCATION);

                    if (currentObservation.getHumidity() != null) {
                        LitePoint humidityPoint = attributeService.getPointForAttribute(weatherPao, BuiltInAttribute.HUMIDITY);
                        PointData newData = new PointData();
                        newData.setId(humidityPoint.getPointID());
                        newData.setPointQuality(PointQuality.Normal);
                        newData.setType(humidityPoint.getLiteType());
                        newData.setValue(currentObservation.getHumidity());
                        newData.setTime(currentObservation.getTimestamp().toDate());
                        dynamicDataSource.putValue(newData);
                    }

                    if (currentObservation.getTemperature() != null) {
                        LitePoint temperaturePoint = attributeService.getPointForAttribute(weatherPao, BuiltInAttribute.TEMPERATURE);
                        PointData newData = new PointData();
                        newData.setId(temperaturePoint.getPointID());
                        newData.setPointQuality(PointQuality.Normal);
                        newData.setType(temperaturePoint.getLiteType());
                        newData.setValue(currentObservation.getTemperature());
                        newData.setTime(currentObservation.getTimestamp().toDate());
                        dynamicDataSource.putValue(newData);
                    }
                    log.debug("Updated weather station " + weatherStationId);
                } else {
                    log.warn("Unable to update weather observation for station: " + weatherStationId + ". NOAA weather service did not return information for this station.");
                }
            } catch (DispatchNotConnectedException e) {
                log.warn("Unable to update weather observation for station: " + weatherStationId + ". Dispatch not connected.");
            } catch (NoaaWeatherDataServiceException e) {
                log.warn("Unable to update weather observation for station: " + weatherStationId, e);
            } catch (DataAccessException e) {
                log.error("No Weather Station Id found for weather location. Pao id: " + singlePaoId + " will not be updated", e);
            }
        }
        Instant finish = new Instant();

        systemEventLogService.systemLogWeatherDataUpdate(weatherLocations.size(), start, finish);
    }
}
