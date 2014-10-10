package com.cannontech.web.maintenance.tasks;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.pao.PaoInfo;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.weather.NoaaWeatherDataService;
import com.cannontech.common.weather.NoaaWeatherDataServiceException;
import com.cannontech.common.weather.WeatherDataService;
import com.cannontech.common.weather.WeatherLocation;
import com.cannontech.common.weather.WeatherObservation;
import com.cannontech.common.weather.WeatherStation;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.pao.dao.StaticPaoInfoDao;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonTaskBase;

public class RepeatingWeatherDataTask extends YukonTaskBase {

    private Logger log = YukonLogManager.getLogger(RepeatingWeatherDataTask.class);

    @Autowired private JobManager jobManager;
    @Autowired private PaoDao paoDao;
    @Autowired private NoaaWeatherDataService noaaWeatherService;
    @Autowired private StaticPaoInfoDao staticPaoInfoDao;
    @Autowired private WeatherDataService weatherDataService;
    @Autowired private SystemEventLogService systemEventLogService;

    private Duration oldWeatherDataDuration = Duration.standardMinutes(59);

    @Override
    public void start() {
        Instant start = new Instant();
        List<LiteYukonPAObject> weatherLocations = paoDao.getLiteYukonPAObjectByType(PaoType.WEATHER_LOCATION);
        Map<String, WeatherStation> weatherStationMap = noaaWeatherService.getAllWeatherStations();

        for (LiteYukonPAObject weatherPao : weatherLocations) {
            int singlePaoId = weatherPao.getPaoIdentifier().getPaoId();

            String weatherStationId = "";
            try {
                weatherStationId = staticPaoInfoDao.getValue(PaoInfo.WEATHER_LOCATION_STATIONID, singlePaoId);
                if (weatherStationMap.containsKey(weatherStationId)) {
                    if (shouldUpdateWeatherPoints(singlePaoId)) {
                        WeatherStation weatherStation = weatherStationMap.get(weatherStationId);

                        updateWeatherPoints(weatherStation, singlePaoId);

                        log.debug("Weather data for " + weatherStationId + " is old. We will check with NOAA for updated weather data.");
                    } else {
                        log.debug("Weather data for " + weatherStationId + " is current and will not be updated.");
                    }
                } else {
                    log.warn("Unable to update weather observation for station: " + weatherStationId + ". NOAA weather service did not return information for this station.");
                }
            } catch (DynamicDataAccessException e) {
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

    /**
     * Retrieves points and checks the timestamp to see if the data is older than 59 min
     */
    private boolean shouldUpdateWeatherPoints(int weatherLocationPaoId) {
        WeatherLocation weatherLocation = weatherDataService.findWeatherLocationForPao(weatherLocationPaoId);
        WeatherObservation weatherObs = weatherDataService.getCurrentWeatherObservation(weatherLocation);
        Instant updateTime = weatherObs.getTimestamp().plus(oldWeatherDataDuration);
        return Instant.now().isAfter(updateTime);
    }
    /**
     * Update weather points for any weather location
     */
    private void updateWeatherPoints(WeatherStation weatherStation, int paoId) throws NoaaWeatherDataServiceException {
        WeatherObservation updatedObservation = noaaWeatherService.getCurrentWeatherObservation(weatherStation);

        WeatherLocation weatherLocation = weatherDataService.findWeatherLocationForPao(paoId);
        WeatherObservation currentObservation = weatherDataService.getCurrentWeatherObservation(weatherLocation);

        if (!currentObservation.getTimestamp().isEqual(updatedObservation.getTimestamp())) {
            weatherDataService.updateWeatherPoints(updatedObservation, paoId);
        }
    }
}
