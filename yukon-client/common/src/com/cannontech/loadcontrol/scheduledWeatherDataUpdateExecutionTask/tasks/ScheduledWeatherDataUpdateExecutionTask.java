package com.cannontech.loadcontrol.scheduledWeatherDataUpdateExecutionTask.tasks;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.pao.PaoInfo;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.pao.dao.StaticPaoInfoDao;
import com.cannontech.database.db.point.Point;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.loadcontrol.weather.NoaaWeatherDataService;
import com.cannontech.loadcontrol.weather.NoaaWeatherDataServiceException;
import com.cannontech.loadcontrol.weather.WeatherDataService;
import com.cannontech.loadcontrol.weather.WeatherObservation;
import com.cannontech.loadcontrol.weather.WeatherStation;
import com.cannontech.loadcontrol.weather.impl.WeatherDataServiceImpl;
import com.cannontech.message.dispatch.message.PointData;

public class ScheduledWeatherDataUpdateExecutionTask extends YukonTaskBase {

    private Logger log = YukonLogManager.getLogger(WeatherDataServiceImpl.class);

    @Autowired DynamicDataSource dynamicDataSource;
    @Autowired PaoDao paoDao;
    @Autowired PointDao pointDao;
    @Autowired StaticPaoInfoDao staticPaoInfoDao;
    @Autowired SystemEventLogService systemEventLogService;
    @Autowired WeatherDataService weatherService;
    @Autowired NoaaWeatherDataService noaaWeatherService;

    @Override
    public void start() {
        Instant start = new Instant();

        List<LiteYukonPAObject> weatherLocations = paoDao.getLiteYukonPAObjectByType(PaoType.WEATHER_LOCATION.getDeviceTypeId());
        Map<String, WeatherStation> weatherStationMap = noaaWeatherService.getAllWeatherStations();

        log.info("WeatherDataService: # of weather locations being updated: " + weatherLocations.size());
        for (LiteYukonPAObject weatherLocation : weatherLocations) {
            int singlePaoId = weatherLocation.getLiteID();

            String weatherStationId = "";
            try {
                weatherStationId = staticPaoInfoDao.getValue(PaoInfo.WEATHER_LOCATION_STATIONID, singlePaoId);
                WeatherStation weatherStation = weatherStationMap.get(weatherStationId);
                WeatherObservation currentObservation = noaaWeatherService.getCurrentWeatherObservation(weatherStation);

                List<PointBase> points = pointDao.getPointsForPao(singlePaoId);
                updatePointDataForObservation(points, currentObservation);
            } catch(NoaaWeatherDataServiceException e) {
                log.warn("Unable to update weather observation for station: " + weatherStationId, e);
            } catch (DataAccessException e) {
                log.error("No Weather Station Id found for weather location. Pao id: " + singlePaoId + " will not be updated", e);
            }
        }
        Instant finish = new Instant();

        systemEventLogService.systemLogWeatherDataUpdate(weatherLocations.size(), start, finish);
    }

    private void updatePointDataForObservation(List<PointBase> points, WeatherObservation observation) {
        for (PointBase pointBase : points) {
            Point point = pointBase.getPoint();

            PointData newData = new PointData();
            newData.setId(point.getPointID());
            newData.setPointQuality(PointQuality.Normal);
            newData.setType(pointDao.getLitePoint(point.getPointID()).getLiteType());

            if (point.getPointName().equals("Temperature")) {
                newData.setValue(observation.getTemperature());
            } else if (point.getPointName().equals("Relative Humidity")) {
                newData.setValue(observation.getHumidity());
            }

            dynamicDataSource.putValue(newData);
        }
    }
}
