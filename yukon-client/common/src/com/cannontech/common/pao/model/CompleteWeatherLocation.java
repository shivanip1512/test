package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;

@YukonPao(paoTypes = PaoType.WEATHER_LOCATION, tableBacked = false)
public final class CompleteWeatherLocation extends CompleteDevice {

    // TODO - at some point the 3 staticPaoInfo entries should be part of this object. 
    //      - PaoInfo.WEATHER_LOCATION_LATITUDE, PaoInfo.WEATHER_LOCATION_LONGITUDE, PaoInfo.WEATHER_LOCATION_STATIONID
    //      - For now, they are handled in WeatherDataServiceImpl using staticPaoInfoDao.
    
    @Override
    public String toString() {
        return super.toString() + " WeatherLocation]";
    }
}