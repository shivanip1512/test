package com.cannontech.common.weather.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoInfo;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.model.CompleteWeatherLocation;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.weather.GeographicCoordinate;
import com.cannontech.common.weather.NoaaWeatherDataService;
import com.cannontech.common.weather.NoaaWeatherDataServiceException;
import com.cannontech.common.weather.WeatherDataService;
import com.cannontech.common.weather.WeatherLocation;
import com.cannontech.common.weather.WeatherObservation;
import com.cannontech.common.weather.WeatherStation;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.pao.dao.StaticPaoInfoDao;
import com.cannontech.yukon.IDatabaseCache;

public class WeatherDataServiceImpl implements WeatherDataService {

    private Logger log = YukonLogManager.getLogger(WeatherDataServiceImpl.class);
    
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private PaoDao paoDao;
    @Autowired private StaticPaoInfoDao staticPaoInfoDao;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private SimplePointAccessDao pointAccessDao;
    @Autowired private NoaaWeatherDataService noaaWeatherDataService;
    private DecimalFormat doubleFormat = new DecimalFormat("#.#####");

    @Override
    public WeatherLocation findWeatherLocationForPao(int paoId) {
        LiteYukonPAObject pao;
        try {
            pao = databaseCache.getAllPaosMap().get(paoId);
        } catch(NotFoundException e) {
            return null;
        }
        return createWeatherLocationFromPao(pao);
    }

    @Override
    public WeatherObservation getCurrentWeatherObservation(WeatherLocation weatherLocation) {
        Double temperature = null;
        Double humidity = null;
        Instant timestamp = null;

        if (weatherLocation.getTempPoint() != null) {
            PointValueQualityHolder tempertureValue =
                asyncDynamicDataSource.getPointValue(weatherLocation.getTempPoint().getLiteID());
            temperature = tempertureValue.getValue();
            timestamp = new Instant(tempertureValue.getPointDataTimeStamp());
            if (tempertureValue.getPointQuality() != PointQuality.Normal
                && tempertureValue.getPointQuality() != PointQuality.Manual) {
                temperature = null;
            }
        }
        if (weatherLocation.getHumidityPoint() != null) {
            PointValueQualityHolder humidityValue =
                asyncDynamicDataSource.getPointValue(weatherLocation.getHumidityPoint().getLiteID());
            humidity = humidityValue.getValue();
            if (humidityValue.getPointQuality() != PointQuality.Normal
                && humidityValue.getPointQuality() != PointQuality.Manual) {
                humidity = null;
            }
            if (timestamp == null) {
                timestamp = new Instant(humidityValue.getPointDataTimeStamp());
            }
        }

        return new WeatherObservation(weatherLocation.getStationId(),
                                      temperature,
                                      humidity,
                                      timestamp);
    }

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public List<WeatherLocation> getAllWeatherLocations() {
      List<LiteYukonPAObject> paos = paoDao.getLiteYukonPAObjectByType(PaoType.WEATHER_LOCATION);
      List<WeatherLocation> weatherDevices = new ArrayList<>();

      primaryWeatherLocation(paos);
      for (LiteYukonPAObject pao : paos) {
          WeatherLocation weatherLocation = createWeatherLocationFromPao(pao);
          if (weatherLocation != null) {
              weatherDevices.add(weatherLocation);
          } else {
              log.error("Pao with id: " + pao.getPaoIdentifier().getPaoId()
                        + " is missing StationId from StaticPaoInfo. Cannot use this pao WEATHER_LOCATION PaoType.");
          }
      }

      return weatherDevices;
    }

    @Override
    public boolean isNameAvailableForWeatherLocation(String name) {
        return paoDao.findYukonPao(name, PaoType.WEATHER_LOCATION) == null;
    }

    @Override
    @Transactional
    public WeatherLocation createWeatherLocation(String name, String stationId, GeographicCoordinate geoCoordinate) {

        CompleteWeatherLocation weatherPao = new CompleteWeatherLocation();
        weatherPao.setPaoName(name);

        paoPersistenceService.createPaoWithDefaultPoints(weatherPao, PaoType.WEATHER_LOCATION);

        String latitude = doubleFormat.format(geoCoordinate.getLatitude());
        String longitude = doubleFormat.format(geoCoordinate.getLongitude());
        int paoId = weatherPao.getPaoIdentifier().getPaoId();
        String primaryLocation = Boolean.FALSE.toString();

        staticPaoInfoDao.saveValue(PaoInfo.WEATHER_LOCATION_LATITUDE, paoId, latitude);
        staticPaoInfoDao.saveValue(PaoInfo.WEATHER_LOCATION_LONGITUDE, paoId, longitude);
        staticPaoInfoDao.saveValue(PaoInfo.WEATHER_LOCATION_STATIONID, paoId, stationId);
        staticPaoInfoDao.saveValue(PaoInfo.PRIMARY_WEATHER_LOCATION, paoId, primaryLocation);
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.WEATHER_LOCATION);
        LitePoint temperaturePoint = attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.TEMPERATURE);
        LitePoint humidityPoint = attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.HUMIDITY);

        WeatherLocation weatherLocation =
            new WeatherLocation(paoIdentifier, temperaturePoint, humidityPoint, name, stationId, geoCoordinate, Boolean.parseBoolean(primaryLocation));
        return weatherLocation;
    }

    @Override
    public void deleteWeatherLocation(int paoId) {
        paoPersistenceService.deletePao(new PaoIdentifier(paoId, PaoType.WEATHER_LOCATION));
    }

    private WeatherLocation createWeatherLocationFromPao(LiteYukonPAObject pao) {
        int paoId = pao.getPaoIdentifier().getPaoId();

        String stationId = null;
        try {
            stationId = staticPaoInfoDao.getValue(PaoInfo.WEATHER_LOCATION_STATIONID, paoId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Pao with id: " + paoId + " is not a WEATHER_LOCATION PaoType. Missing StationId from StaticPaoInfo");
        }

        if (stationId == null) {
            return null;
        }

        String name = pao.getPaoName();
        String lonStr = staticPaoInfoDao.getValue(PaoInfo.WEATHER_LOCATION_LONGITUDE, pao.getLiteID());
        String latStr = staticPaoInfoDao.getValue(PaoInfo.WEATHER_LOCATION_LATITUDE, pao.getLiteID());

        String primaryLocation = null;
        try {
            primaryLocation = staticPaoInfoDao.getValue(PaoInfo.PRIMARY_WEATHER_LOCATION, paoId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Unable to get primary weather location for Pao with id: " + paoId);
        }
        GeographicCoordinate coordinate = new GeographicCoordinate(Double.parseDouble(lonStr), Double.parseDouble(latStr));

        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.WEATHER_LOCATION);

        LitePoint temperaturePoint = null;
        LitePoint humidityPoint = null;
        try {
            temperaturePoint = attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.TEMPERATURE);
        } catch (IllegalUseOfAttribute e) {
            log.error("Unable to get temperature attribute for paoIdentifier: " + paoIdentifier
                + ". Resolve this error by adding the temperature point to this weather station.");
        }

        try {
            humidityPoint = attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.HUMIDITY);
        } catch (IllegalUseOfAttribute e) {
            log.error("Unable to get humidity attribute for paoIdentifier: " + paoIdentifier
                + ". Resolve this error by adding the humidity point to this weather station.");
        }

        WeatherLocation weatherLocation
            = new WeatherLocation(paoIdentifier, temperaturePoint, humidityPoint, name, stationId, coordinate, Boolean.parseBoolean(primaryLocation));

        return weatherLocation;
    }

    
    /**
     * This method will be used to select existing weather location as primary.
     * As we may have existing weather location in system so we will be making the first weather location
     * as primary. Also in case the primary location is deleted then again the weather location created first
     * will be made primary. 
     */
    private void primaryWeatherLocation(List<LiteYukonPAObject> paos) {
        boolean primary = true;
        boolean atleastOnePrimaryLocation = false;
        for (LiteYukonPAObject pao : paos) {
            int paoId = pao.getPaoIdentifier().getPaoId();

            String primaryLocation = null;
            try {
                primaryLocation = staticPaoInfoDao.getValue(PaoInfo.PRIMARY_WEATHER_LOCATION, paoId);
                if ("true".equalsIgnoreCase(primaryLocation)) {
                    atleastOnePrimaryLocation = true;
                }
            } catch (EmptyResultDataAccessException e) {
                if (primaryLocation == null) {
                    staticPaoInfoDao.saveValue(PaoInfo.PRIMARY_WEATHER_LOCATION, paoId, String.valueOf(primary));
                    primary = false;
                }
            }
        }
        if (!atleastOnePrimaryLocation) {
            staticPaoInfoDao.saveValue(PaoInfo.PRIMARY_WEATHER_LOCATION, paos.get(0).getPaoIdentifier().getPaoId(), String.valueOf(true));
        }
    }

    @Override
    public void updatePointsForNewWeatherLocation(WeatherLocation weatherLocation) {
        
        Map<String, WeatherStation> weatherStationMap = noaaWeatherDataService.getAllWeatherStations();
        int weatherPaoId = weatherLocation.getPaoIdentifier().getPaoId();
        WeatherStation weatherStation = weatherStationMap.get(weatherLocation.getStationId());
        try {
            WeatherObservation weatherObservation = noaaWeatherDataService.getCurrentWeatherObservation(weatherStation);
            if (weatherObservation.isTimestampValid()) {
                updateWeatherPoints(weatherObservation, weatherPaoId);
            } else {
                log.warn("Ignoring update weather data for station: " + weatherLocation.getStationId()
                    + " as the timestamp" + weatherObservation.getTimestamp() + " is more than +24hrs hours");
            }
        } catch (NoaaWeatherDataServiceException e) {
            log.warn("Unable to get points(humidity and temperature) for weather station: "
                     + weatherLocation.getStationId(), e);
        }
    }

    @Override
    @Transactional
    public void updateWeatherPoints(WeatherObservation weatherObservation, int paoId) {

        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.WEATHER_LOCATION);
        if (weatherObservation.getHumidity() != null) {
            try {
                LitePoint humidityPoint =
                    attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.HUMIDITY);
                pointAccessDao.setPointValue(humidityPoint, weatherObservation.getTimestamp(),
                    weatherObservation.getHumidity());
            } catch (IllegalUseOfAttribute e) {
                log.error("Unable to get humidity attribute for paoIdentifier: " + paoIdentifier
                    + ". Resolve this error by adding the humidity point to this weather station.");
            }

        }

        if (weatherObservation.getTemperature() != null) {
            try {
                LitePoint temperaturePoint =
                    attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.TEMPERATURE);
                pointAccessDao.setPointValue(temperaturePoint, weatherObservation.getTimestamp(),
                    weatherObservation.getTemperature());
            } catch (IllegalUseOfAttribute e) {
                log.error("Unable to get temperature attribute for paoIdentifier: " + paoIdentifier
                    + ". Resolve this error by adding the temperature point to this weather station.");
            }

        }
    }

    @Transactional
    @Override
    public void updatePrimaryWeatherLocation(int paoId) {
        List<LiteYukonPAObject> paos = paoDao.getLiteYukonPAObjectByType(PaoType.WEATHER_LOCATION);
        List<LiteYukonPAObject> liteYukonPAObject = paos.stream().filter(pao -> pao.getPaoIdentifier().getPaoId() != paoId)
                                                                 .filter(pao -> "true".equalsIgnoreCase(staticPaoInfoDao
                                                                 .getValue(PaoInfo.PRIMARY_WEATHER_LOCATION,pao.getPaoIdentifier().getPaoId())))
                                                                 .collect(Collectors.toList());

        liteYukonPAObject.stream().forEach(litePao -> staticPaoInfoDao.saveValue(PaoInfo.PRIMARY_WEATHER_LOCATION,
            litePao.getPaoIdentifier().getPaoId(), String.valueOf(false)));

        staticPaoInfoDao.saveValue(PaoInfo.PRIMARY_WEATHER_LOCATION, paoId, String.valueOf(true));
    }
}
