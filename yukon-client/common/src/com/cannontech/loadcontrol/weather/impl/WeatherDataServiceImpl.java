package com.cannontech.loadcontrol.weather.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.db.pao.dao.StaticPaoInfoDao;
import com.cannontech.loadcontrol.weather.GeographicCoordinate;
import com.cannontech.loadcontrol.weather.WeatherDataService;
import com.cannontech.loadcontrol.weather.WeatherLocation;
import com.cannontech.loadcontrol.weather.WeatherObservation;

public class WeatherDataServiceImpl implements WeatherDataService {

    private Logger log = YukonLogManager.getLogger(WeatherDataServiceImpl.class);

    @Autowired private PaoDao paoDao;
    @Autowired private StaticPaoInfoDao staticPaoInfoDao;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private AttributeService attributeService;
    @Autowired private DynamicDataSource dynamicDataSource;
    private DecimalFormat doubleFormat = new DecimalFormat("#.#####");

    @Override
    public WeatherLocation findWeatherLocationForPao(int paoId) {
        LiteYukonPAObject pao;
        try {
            pao = paoDao.getLiteYukonPAO(paoId);
        } catch(NotFoundException e) {
            return null;
        }
        return createWeatherLocationFromPao(pao);
    }

    @Override
    public WeatherObservation getCurrentWeatherObservation(WeatherLocation weatherLocation) {
        PointValueQualityHolder tempertureValue = dynamicDataSource.getPointValue(weatherLocation.getTempPoint().getLiteID());
        PointValueQualityHolder humidityValue = dynamicDataSource.getPointValue(weatherLocation.getHumidityPoint().getLiteID());

        Double temperature = tempertureValue.getValue();
        Double humidity = humidityValue.getValue();

        Instant timestamp = new Instant(tempertureValue.getPointDataTimeStamp());

        if (tempertureValue.getPointQuality() != PointQuality.Normal) {
            temperature = null;
        }

        if (humidityValue.getPointQuality() != PointQuality.Normal) {
            humidity = null;
        }

        return new WeatherObservation(weatherLocation.getStationId(),
                                      temperature,
                                      humidity,
                                      timestamp);
    }

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public List<WeatherLocation> getAllWeatherLocations() {
      List<LiteYukonPAObject> paos = paoDao.getLiteYukonPAObjectByType(DeviceTypes.WEATHER_LOCATION);
      List<WeatherLocation> weatherDevices = new ArrayList<>();

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
    public void createWeatherLocation(WeatherLocation weatherLocation) {

        CompleteYukonPao weatherPao = new CompleteYukonPao();
        weatherPao.setPaoName(weatherLocation.getName());

        paoPersistenceService.createPaoWithDefaultPoints(weatherPao, PaoType.WEATHER_LOCATION);

        String latitude = doubleFormat.format(weatherLocation.getGeoCoordinate().getLatitude());
        String longitude = doubleFormat.format(weatherLocation.getGeoCoordinate().getLongitude());
        int paoId = weatherPao.getPaoIdentifier().getPaoId();

        staticPaoInfoDao.saveValue(PaoInfo.WEATHER_LOCATION_LATITUDE, paoId, latitude);
        staticPaoInfoDao.saveValue(PaoInfo.WEATHER_LOCATION_LONGITUDE, paoId, longitude);
        staticPaoInfoDao.saveValue(PaoInfo.WEATHER_LOCATION_STATIONID, paoId, weatherLocation.getStationId());
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

        GeographicCoordinate coordinate = new GeographicCoordinate(Double.parseDouble(lonStr), Double.parseDouble(latStr));

        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.WEATHER_LOCATION);

        LitePoint temperaturePoint = attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.TEMPERATURE);
        LitePoint humidityPoint = attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.HUMIDITY);

        WeatherLocation weatherLocation
            = new WeatherLocation(paoIdentifier, temperaturePoint, humidityPoint, name, stationId, coordinate);

        return weatherLocation;
    }
}
