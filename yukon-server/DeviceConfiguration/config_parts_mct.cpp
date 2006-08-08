/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_addressing
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_addressing.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2006/08/08 13:36:09 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"
#include "config_parts.h"
#include "rwutil.h"
#include "utility.h"
#pragma warning( disable : 4661)//This lets us split the config_parts_xxx.cpp files up without getting warnings

namespace Cti    {
namespace Config {
using namespace MCT;

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

MCT_TOU ConfigurationPart<MCT_TOU>::getResolvedKey(string key)//DO NOT USE ME
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

MCT_DST ConfigurationPart<MCT_DST>::getResolvedKey(string key)
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
    if(key == "cycling disconnect minutes")
    {
        return CyclingDisconnectMinutes;
    }
    if(key == "cycling connect minutes")
    {
        return CyclingConnectMinutes;
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

MCTSystemOptions ConfigurationPart<MCTSystemOptions>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "demand meters to scan")
    {
        return DemandMetersToScan;
    }
    else
    {
        return MCTSystemOptionsInvalid;
    }
}

MCTCentron ConfigurationPart<MCTCentron>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "parameters")
    {
        return CentronParameters;
    }
    if(key == "transformer ratio")
    {
        return CentronTransformerRatio;
    }
    else
    {
        return MCTCentronInvalid;
    }
}

MCT_DNP ConfigurationPart<MCT_DNP>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key.find("collection") != string::npos)
    {
        if(key == "collection 1 binary a")
        {
            return DNPCollection1BinaryA;
        }
        if(key == "collection 1 binary b")
        {
            return DNPCollection1BinaryB;
        }
        if(key == "collection 2 binary a")
        {
            return DNPCollection2BinaryA;
        }
        if(key == "collection 2 binary b")
        {
            return DNPCollection2BinaryB;
        }
        if(key == "collection 1 analog")
        {
            return DNPCollection1Analog;
        }
        if(key == "collection 2 analog")
        {
            return DNPCollection2Analog;
        }
        if(key == "collection 1 accumulator")
        {
            return DNPCollection1Accumulator;
        }
        if(key == "collection 2 accumulator")
        {
            return DNPCollection2Accumulator;
        }
    }
    else if(key.find("analog") != string::npos)
    {
        if(key == "analog 1")
        {
            return DNPAnalog1;
        }
        if(key == "analog 2")
        {
            return DNPAnalog2;
        }
        if(key == "analog 3")
        {
            return DNPAnalog3;
        }
        if(key == "analog 4")
        {
            return DNPAnalog4;
        }
        if(key == "analog 5")
        {
            return DNPAnalog5;
        }
        if(key == "analog 6")
        {
            return DNPAnalog6;
        }
        if(key == "analog 7")
        {
            return DNPAnalog7;
        }
        if(key == "analog 8")
        {
            return DNPAnalog8;
        }
        if(key == "analog 9")
        {
            return DNPAnalog9;
        }
        if(key == "analog 10")
        {
            return DNPAnalog10;
        }
    }
    else if(key.find("accumulator") != string::npos)
    {
        if(key == "accumulator 1")
        {
            return DNPAccumulator1;
        }
        if(key == "accumulator 2")
        {
            return DNPAccumulator2;
        }
        if(key == "accumulator 3")
        {
            return DNPAccumulator3;
        }
        if(key == "accumulator 4")
        {
            return DNPAccumulator4;
        }
        if(key == "accumulator 5")
        {
            return DNPAccumulator5;
        }
        if(key == "accumulator 6")
        {
            return DNPAccumulator6;
        }
        if(key == "accumulator 7")
        {
            return DNPAccumulator7;
        }
        if(key == "accumulator 8")
        {
            return DNPAccumulator8;
        }
    }
    else
    {
        if(key == "binary byte 1a")
        {
            return DNPBinaryByte1A;
        }
        if(key == "binary byte 1b")
        {
            return DNPBinaryByte1B;
        }
    }

    return MCT_DNPInvalid;
    
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

CtiConfig_type ConfigurationPart<MCT_TOU>::getType()
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

CtiConfig_type ConfigurationPart<MCTSystemOptions>::getType()
{
    return ConfigTypeMCTSystemOptions;
}

CtiConfig_type ConfigurationPart<MCTCentron>::getType()
{
    return ConfigTypeMCTCentron;
}

CtiConfig_type ConfigurationPart<MCT_DNP>::getType()
{
    return ConfigTypeMCTDNP;
}

}//Config
}//Cti


