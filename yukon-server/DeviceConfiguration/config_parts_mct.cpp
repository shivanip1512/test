/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_addressing
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_addressing.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/10/17 16:39:52 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"
#include "config_parts.h"

namespace Cti    {
namespace Config {

MCTAddressing ConfigurationPart<MCTAddressing>::getResolvedKey(RWCString key)
{
    key.toLower();
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
    else
    {
        return MCTAddressingInvalid;
    }
}

MCT_TOU ConfigurationPart<MCT_TOU>::getResolvedKey(RWCString key)
{
    key.toLower();
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
    else if(key == "rate")//both curent and default rates
    {
        return CurAndDefRate;
    }
    else if(key == "switch")//schedule and time
    {
        return SwtchSchdAndTime;
    }
    else if(key == "crit peak end")
    {
        return CritPkEndTime;
    }
    else
    {
        return MCT_TOUInvalid;
    }
}

MCT_DST ConfigurationPart<MCT_DST>::getResolvedKey(RWCString key)
{
    key.toLower();
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

MCTVThreshold ConfigurationPart<MCTVThreshold>::getResolvedKey(RWCString key)
{
    key.toLower();
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

MCTDemandLoadProfile ConfigurationPart<MCTDemandLoadProfile>::getResolvedKey(RWCString key)
{
    key.toLower();
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

MCTConfiguration ConfigurationPart<MCTConfiguration>::getResolvedKey(RWCString key)
{
    key.toLower();
    if(key == "configuration")
    {
        return Configuration;
    }
    else
    {
        return MCTConfigurationInvalid;
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

CtiConfig_type ConfigurationPart<MCTConfiguration>::getType()
{
    return ConfigTypeMCTConfiguration;
}

CtiConfig_type ConfigurationPart<MCT_DST>::getType()
{
    return ConfigTypeMCTDST;
}

CtiConfig_type ConfigurationPart<MCT_TOU>::getType()
{
    return ConfigTypeMCTTOU;
}

}//Config
}//Cti


