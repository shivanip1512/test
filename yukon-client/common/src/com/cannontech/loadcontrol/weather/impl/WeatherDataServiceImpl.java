package com.cannontech.loadcontrol.weather.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoInfo;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.point.PointQuality;
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

    @Override
    public WeatherObservation getCurrentWeatherObservation(WeatherLocation weatherLocation) {

        PointValueQualityHolder tempValue = dynamicDataSource.getPointValue(weatherLocation.getTempPoint().getLiteID());
        PointValueQualityHolder humidityValue = dynamicDataSource.getPointValue(weatherLocation.getHumidityPoint().getLiteID());

        Instant tempTimestamp = new Instant(tempValue.getPointDataTimeStamp());

        if (tempValue.getPointQuality() != PointQuality.Normal) {
            tempTimestamp = new Instant(0);
        }

        Instant humidityTimestamp = new Instant(humidityValue.getPointDataTimeStamp());

        if (humidityValue.getPointQuality() != PointQuality.Normal) {
            humidityTimestamp = new Instant(0);
        }

        return new WeatherObservation(weatherLocation.getStationId(),
                                      tempValue.getValue(),
                                      tempTimestamp,
                                      humidityValue.getValue(),
                                      humidityTimestamp);
    }

    @Override
    public List<WeatherLocation> getAllWeatherLocations() {
      List<LiteYukonPAObject> paos = paoDao.getLiteYukonPAObjectByType(DeviceTypes.WEATHER_LOCATION);
      List<WeatherLocation> weatherDevices = new ArrayList<>();

      for (LiteYukonPAObject pao : paos) {
          String name = pao.getPaoName();
          String stationId = staticPaoInfoDao.getValue(PaoInfo.WEATHER_LOCATION_STATIONID, pao.getLiteID());
          String lonStr = staticPaoInfoDao.getValue(PaoInfo.WEATHER_LOCATION_LONGITUDE, pao.getLiteID());
          String latStr = staticPaoInfoDao.getValue(PaoInfo.WEATHER_LOCATION_LATITUDE, pao.getLiteID());
          GeographicCoordinate coordinate = new GeographicCoordinate(Double.parseDouble(lonStr), Double.parseDouble(latStr));

          PaoIdentifier paoIdentifier = new PaoIdentifier(pao.getLiteID(), PaoType.WEATHER_LOCATION);
          try {
              LitePoint tempPoint = attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.TEMPERATURE);
              LitePoint humidityPoint = attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.HUMIDITY);

              WeatherLocation weatherLocation
                  = new WeatherLocation(tempPoint, humidityPoint, name, stationId, coordinate);

              weatherDevices.add(weatherLocation);
          } catch(Exception e) {
              log.error("FIX ME", e);
          }
      }
      return weatherDevices;
    }

    @Override
    public boolean isNameAvailableForWeatherLocation(String name) {
        return paoDao.findYukonPao(name, PaoType.WEATHER_LOCATION) == null;
    }

    @Override
    public void createWeatherLocation(WeatherLocation weatherLocation) {
        DecimalFormat doubleFormat = new DecimalFormat("#"); // we don't want Double.toString since this prints scientific notation

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
}
