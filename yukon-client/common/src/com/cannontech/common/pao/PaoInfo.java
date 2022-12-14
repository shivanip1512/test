package com.cannontech.common.pao;

/**
 * Enum values to represent PaoInfo (ex. StaticPaoInfo.value)
 */
public enum PaoInfo {

    // RDS Transmitter PaoInfo values
    RDS_TRANSMITTER_IP_PORT,
    RDS_TRANSMITTER_IP_ADDRESS,
    RDS_TRANSMITTER_SITE_ADDRESS,
    RDS_TRANSMITTER_ENCODER_ADDRESS,
    RDS_TRANSMITTER_TRANSMIT_SPEED,
    RDS_TRANSMITTER_GROUP_TYPE,
    RDS_TRANSMITTER_SPID,
    RDS_TRANSMITTER_AID_REPEAT_PERIOD,
    
    // Other....
    CPS_ONE_WAY_ENCRYPTION_KEY,
    
    // WeatherLocation info
    WEATHER_LOCATION_STATIONID,
    WEATHER_LOCATION_LONGITUDE,
    WEATHER_LOCATION_LATITUDE,
    PRIMARY_WEATHER_LOCATION,
    ;
}
