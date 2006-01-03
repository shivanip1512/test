/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_addressing
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_addressing.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2006/01/03 20:23:37 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"
#include "config_parts.h"
#include "rwutil.h"
#include "utility.h"

namespace Cti    {
namespace Config {

MCTAddressing ConfigurationPart<MCTAddressing>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "bronze address")
    {
        return Bronze;
    }
    else if(key == "lead address")
    {
        return Lead;
    }
    else if(key == "collection address")
    {
        return Collection;
    }
    else if(key == "service provider id")
    {
        return ServiceProviderID;
    }
    else
    {
        return MCTAddressingInvalid;
    }
}

MCT_TOU ConfigurationPart<MCT_TOU>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "day table")
    {
        return DayTable;
    }
    else if(key == "day schedule 1")
    {
        return DaySchedule1;
    }
    else if(key == "day schedule 2")
    {
        return DaySchedule2;
    }
    else if(key == "day schedule 3")
    {
        return DaySchedule3;
    }
    else if(key == "day schedule 4")
    {
        return DaySchedule4;
    }
    else if(key == "default rate")
    {
        return DefaultTOURate;
    }
    else
    {
        return MCT_TOUInvalid;
    }
}

MCT_DST ConfigurationPart<MCT_DST>::getResolvedKey(string key)//DO NOT USE ME
{
    CtiToLower(key);
    if(key == "dst begin")
    {
        return DstBegin;
    }
    else if(key == "dst end")
    {
        return DstEnd;
    }
    else if(key == "time zone offset")
    {
        return TimeZoneOffset;
    }
    else
    {
        return MCT_DSTInvalid;
    }
}

MCTVThreshold ConfigurationPart<MCTVThreshold>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "under vthreshold")
    {
        return UnderVoltageThreshold;
    }
    else if(key == "over vthreshold")
    {
        return OverVoltageThreshold;
    }
    else
    {
        return MCTVThresholdInvalid;
    }
}

MCTDemandLoadProfile ConfigurationPart<MCTDemandLoadProfile>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "demand interval")
    {
        return DemandInterval;
    }
    else if(key == "load profile interval" || key == "load profile interval 1")
    {
        return LoadProfileInterval;
    }
    else if(key == "voltage lp interval")
    {
        return VoltageLPInterval;
    }
    else if(key == "demand v interval")
    {
        return VoltageDemandInterval;
    }
    else if(key == "load profile interval 2")
    {
        return LoadProfileInterval2;
    }
    else
    {
        return MCTDemandLoadProfileInvalid;
    }
}

MCTOptions ConfigurationPart<MCTOptions>::getResolvedKey(string key)
{

    CtiToLower(key);
    
    if(key == "time adjust tolerance")
    {
        return TimeAdjustTolerance;
    }
    if(key == "alarm mask event 1")
    {
        return AlarmMaskEvent1;
    }
    if(key == "alarm mask event 2")
    {
        return AlarmMaskEvent2;
    }
    if(key == "alarm mask meter")
    {
        return AlarmMaskMeter;
    }
    if(key == "configuration")
    {
        return Configuration;
    }
    if(key == "options")
    {
        return Options;
    }
    if(key == "outage cycles")
    {
        return OutageCycles;
    }
    else
    {
        return MCTOptionsInvalid;
    }
}

MCTDisconnect ConfigurationPart<MCTDisconnect>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "demand threshold")
    {
        return DemandThreshold;
    }
    if(key == "connect delay")
    {
        return ConnectDelay;
    }
    else
    {
        return MCTDisconnectInvalid;
    }
}

MCTHoliday ConfigurationPart<MCTHoliday>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "holiday date 1")
    {
        return HolidayDate1;
    }
    if(key == "holiday date 2")
    {
        return HolidayDate2;
    }
    if(key == "holiday date 3")
    {
        return HolidayDate3;
    }
    else
    {
        return MCTHolidayInvalid;
    }
}

MCTLongLoadProfile ConfigurationPart<MCTLongLoadProfile>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "channel 1 length")
    {
        return Channel1Length;
    }
    if(key == "channel 2 length")
    {
        return Channel2Length;
    }
    if(key == "channel 3 length")
    {
        return Channel3Length;
    }
    if(key == "channel 4 length")
    {
        return Channel4Length;
    }
    else
    {
        return MCTLongLoadProfileInvalid;
    }
}

MCTLoadProfileChannels ConfigurationPart<MCTLoadProfileChannels>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "channel config 1")
    {
        return ChannelConfig1;
    }
    else if(key == "channel config 2")
    {
        return ChannelConfig2;
    }
    else if(key == "channel config 3")
    {
        return ChannelConfig3;
    }
    else if(key == "channel config 4")
    {
        return ChannelConfig4;
    }
    else if(key == "meter ratio 1")
    {
        return MeterRatio1;
    }
    else if(key == "meter ratio 2")
    {
        return MeterRatio2;
    }
    else if(key == "meter ratio 3")
    {
        return MeterRatio3;
    }
    else if(key == "meter ratio 4")
    {
        return MeterRatio4;
    }
    else if(key == "k ratio 1")
    {
        return KRatio1;
    }
    else if(key == "k ratio 2")
    {
        return KRatio2;
    }
    else if(key == "k ratio 3")
    {
        return KRatio3;
    }
    else if(key == "k ratio 4")
    {
        return KRatio4;
    }
    else
    {
        return MCTLoadProfileChannelsInvalid;
    }
}

MCTRelays ConfigurationPart<MCTRelays>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "relay a timer")
    {
        return RelayATimer;
    }
    if(key == "relay b timer")
    {
        return RelayBTimer;
    }
    else
    {
        return MCTRelaysInvalid;
    }
}

MCTPrecannedTable ConfigurationPart<MCTPrecannedTable>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "table read interval")
    {
        return TableReadInterval;
    }
    if(key == "meter number")
    {
        return MeterNumber;
    }
    if(key == "table type")
    {
        return TableType;
    }
    else
    {
        return MCTPrecannedTableInvalid;
    }
}

//******************************************************************************
//getType() begins here
CtiConfig_type ConfigurationPart<MCTAddressing>::getType()
{
    return ConfigTypeMCTAddressing;
}

CtiConfig_type ConfigurationPart<MCTVThreshold>::getType()
{
    return ConfigTypeMCTVThreshold;
}

CtiConfig_type ConfigurationPart<MCTDemandLoadProfile>::getType()
{
    return ConfigTypeMCTDemandLP;
}

CtiConfig_type ConfigurationPart<MCTOptions>::getType()
{
    return ConfigTypeMCTOptions;
}

CtiConfig_type ConfigurationPart<MCT_DST>::getType()
{
    return ConfigTypeMCTDST;
}

CtiConfig_type ConfigurationPart<MCT_TOU>::getType()//DO NOT USE ME
{
    return ConfigTypeMCTTOU;
}

CtiConfig_type ConfigurationPart<MCTDisconnect>::getType()
{
    return ConfigTypeMCTDisconnect;
}

CtiConfig_type ConfigurationPart<MCTHoliday>::getType()
{
    return ConfigTypeMCTHoliday;
}

CtiConfig_type ConfigurationPart<MCTLongLoadProfile>::getType()
{
    return ConfigTypeMCTLongLoadProfile;
}

CtiConfig_type ConfigurationPart<MCTLoadProfileChannels>::getType()
{
    return ConfigTypeMCTLoadProfileChannels;
}

CtiConfig_type ConfigurationPart<MCTRelays>::getType()
{
    return ConfigTypeMCTRelays;
}

CtiConfig_type ConfigurationPart<MCTPrecannedTable>::getType()
{
    return ConfigTypeMCTPrecannedTable;
}


}//Config
}//Cti


