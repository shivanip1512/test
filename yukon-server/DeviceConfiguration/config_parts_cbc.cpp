/*-----------------------------------------------------------------------------*
*
* File:   config_parts_cbc
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_addressing.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2006/03/30 16:04:38 $
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
using namespace CBC;

CBCVoltage ConfigurationPart<CBCVoltage>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "active setting")
    {
        return ActiveSettings;
    }
    else if(key == "under voltage close point")
    {
        return UVClosePoint;
    }
    else if(key == "over voltage trip point")
    {
        return OVTripPoint;
    }
    else if(key == "over under voltage control trig time")
    {
        return OVUVControlTriggerTime;
    }
    else if(key == "adaptive voltage hysteresis")
    {
        return AdaptiveVoltageHysteresis;
    }
    else if(key == "adaptive voltage flag")
    {
        return AdaptiveVoltageFlag;
    }
    else if(key == "emergency under voltage point")
    {
        return EmergencyUVPoint;
    }
    else if(key == "emergency over voltage point")
    {
        return EmergencyOVPoint;
    }
    else if(key == "emergency voltage time")
    {
        return EmergencyVoltageTime;
    }
    else
    {
        return CBCVoltageInvalid;
    }
}

CBCCommsLost ConfigurationPart<CBCCommsLost>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "under voltage close point")
    {
        return CommsLostUVClosePoint;
    }
    else if(key == "over voltage trip point")
    {
        return CommsLostOVTripPoint;
    }
    else if(key == "comms lost time")
    {
        return CommsLostTime;
    }
    else if(key == "comms lost action")
    {
        return CommsLostAction;
    }
    else
    {
        return CBCCommsLostInvalid;
    }
}

CBCNeutralCurrent ConfigurationPart<CBCNeutralCurrent>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "fault current set point")
    {
        return FaultCurrentSetPoint;
    }
    else if(key == "state change set point")
    {
        return StateChangeSetPoint;
    }
    else if(key == "neutral current retry")
    {
        return NeutralCurrentRetryCount;
    }
    else
    {
        return CBCNeutralCurrentInvalid;
    }
}

CBCFaultDetection ConfigurationPart<CBCFaultDetection>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "fault detection active")
    {
        return FaultDetectionActive;
    }
    else if(key == "ai1 average time")
    {
        return AI1AverageTime;
    }
    else if(key == "ai2 average time")
    {
        return AI2AverageTime;
    }
    else if(key == "ai3 average time")
    {
        return AI3AverageTime;
    }
    else if(key == "ai1 peak samples")
    {
        return AI1PeakSamples;
    }
    else if(key == "ai2 peak samples")
    {
        return AI2PeakSamples;
    }
    else if(key == "ai3 peak samples")
    {
        return AI3PeakSamples;
    }
    else if(key == "ai1 ratio threshold")
    {
        return AI1RatioThreshold;
    }
    else if(key == "ai2 ratio threshold")
    {
        return AI2RatioThreshold;
    }
    else if(key == "ai3 ratio threshold")
    {
        return AI3RatioThreshold;
    }
    else if(key == "battery on time")
    {
        return BatteryOnTime;
    }
    else
    {
        return CBCFaultDetectionInvalid;
    }
}

CBCSeason1TimeAndTemp ConfigurationPart<CBCSeason1TimeAndTemp>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "season start 1")
    {
        return Season1Start;
    }
    else if(key == "weekday timed control close 1")
    {
        return WeekdayTimedControlClose1;
    }
    else if(key == "weekend timed control close 1")
    {
        return WeekendTimedControlClose1;
    }
    else if(key == "weekday timed control trip 1")
    {
        return WeekdayTimedControlTrip1;
    }
    else if(key == "weekend timed control trip 1")
    {
        return WeekendTimedControlTrip1;
    }
    else if(key == "off time state 1")
    {
        return OffTimeState1;
    }
    else if(key == "temp min threshold 1")
    {
        return TempMinThreshold1;
    }
    else if(key == "temp min threshold action 1")
    {
        return TempMinThresholdAction1;
    }
    else if(key == "temp min hysterisis 1")
    {
        return TempMinHysterisis1;
    }
    else if(key == "temp min threshold trig time 1")
    {
        return TempMinThresholdTrigTime1;
    }
    else if(key == "temp max threshold 1")
    {
        return TempMaxThreshold1;
    }
    else if(key == "temp max threshold action 1")
    {
        return TempMaxThresholdAction1;
    }
    else if(key == "temp max hysterisis 1")
    {
        return TempMaxHysterisis1;
    }
    else if(key == "temp max threshold trig time 1")
    {
        return TempMaxThresholdTrigTime1;
    }
    else
    {
        return CBCSeason1TimeAndTempInvalid;
    }
}

CBCSeason2TimeAndTemp ConfigurationPart<CBCSeason2TimeAndTemp>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "season start 2")
    {
        return Season2Start;
    }
    else if(key == "weekday timed control close 2")
    {
        return WeekdayTimedControlClose2;
    }
    else if(key == "weekend timed control close 2")
    {
        return WeekendTimedControlClose2;
    }
    else if(key == "weekday timed control trip 2")
    {
        return WeekdayTimedControlTrip2;
    }
    else if(key == "weekend timed control trip 2")
    {
        return WeekendTimedControlTrip2;
    }
    else if(key == "off time state 2")
    {
        return OffTimeState2;
    }
    else if(key == "temp min threshold 2")
    {
        return TempMinThreshold2;
    }
    else if(key == "temp min threshold action 2")
    {
        return TempMinThresholdAction2;
    }
    else if(key == "temp min hysterisis 2")
    {
        return TempMinHysterisis2;
    }
    else if(key == "temp min threshold trig time 2")
    {
        return TempMinThresholdTrigTime2;
    }
    else if(key == "temp max threshold 2")
    {
        return TempMaxThreshold2;
    }
    else if(key == "temp max threshold action 2")
    {
        return TempMaxThresholdAction2;
    }
    else if(key == "temp max hysterisis 2")
    {
        return TempMaxHysterisis2;
    }
    else if(key == "temp max threshold trig time 2")
    {
        return TempMaxThresholdTrigTime2;
    }
    else
    {
        return CBCSeason2TimeAndTempInvalid;
    }
}

CBCControlTimes ConfigurationPart<CBCControlTimes>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "contact closure time")
    {
        return ContactClosureTime;
    }
    else if(key == "manual control delay trip")
    {
        return ManualControlDelayTrip;
    }
    else if(key == "manual control delay close")
    {
        return ManualControlDelayClose;
    }
    else if(key == "reclose delay time")
    {
        return RecloseDelayTime;
    }
    else
    {
        return CBCControlTimesInvalid;
    }
}

CBCDataLogging ConfigurationPart<CBCDataLogging>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "data log flags")
    {
        return DataLogFlags;
    }
    else if(key == "log time interval")
    {
        return LogTimeInterval;
    }
    else
    {
        return CBCDataLoggingInvalid;
    }
}

CBCAddressing ConfigurationPart<CBCAddressing>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "geo address")
    {
        return Geo;
    }
    else if(key == "substation address")
    {
        return Substation;
    }
    else if(key == "feeder address")
    {
        return Feeder;
    }
    else if(key == "zip address")
    {
        return Zip;
    }
    else if(key == "user defined address")
    {
        return UserDefined;
    }
    else if(key == "program")
    {
        return Program;
    }
    else if(key == "splinter")
    {
        return Splinter;
    }
    else if(key == "required address level")
    {
        return RequiredAddressLevel;
    }
    else
    {
        return MCTAddressingInvalid;
    }
}

CBC_DNP ConfigurationPart<CBC_DNP>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "line voltage dead band")
    {
        return LineVoltageDeadBand;
    }
    else if(key == "delta voltage dead band")
    {
        return DeltaVoltageDeadBand;
    }
    else if(key == "analog value dead band")
    {
        return AnalogDeadBand;
    }
    else
    {
        return CBC_DNPInvalid;
    }
}

CBC_UDP ConfigurationPart<CBC_UDP>::getResolvedKey(string key)
{
    CtiToLower(key);
    if(key == "retry delay")
    {
        return RetryDelay;
    }
    else if(key == "poll timeout")
    {
        return PollTimeout;
    }
    else
    {
        return CBC_UDPInvalid;
    }
}

//******************************************************************************
//getType() begins here
CtiConfig_type ConfigurationPart<CBCVoltage>::getType()
{
    return ConfigTypeCBCVoltage;
}

CtiConfig_type ConfigurationPart<CBCCommsLost>::getType()
{
    return ConfigTypeCBCCommsLost;
}

CtiConfig_type ConfigurationPart<CBCNeutralCurrent>::getType()
{
    return ConfigTypeCBCNeutralCurrent;
}

CtiConfig_type ConfigurationPart<CBCFaultDetection>::getType()
{
    return ConfigTypeCBCFaultDetection;
}

CtiConfig_type ConfigurationPart<CBCSeason1TimeAndTemp>::getType()
{
    return ConfigTypeCBCSeason1TimeAndTemp;
}

CtiConfig_type ConfigurationPart<CBCSeason2TimeAndTemp>::getType()
{
    return ConfigTypeCBCSeason2TimeAndTemp;
}

CtiConfig_type ConfigurationPart<CBCControlTimes>::getType()
{
    return ConfigTypeCBCControlTimes;
}

CtiConfig_type ConfigurationPart<CBCDataLogging>::getType()
{
    return ConfigTypeCBCDataLogging;
}

CtiConfig_type ConfigurationPart<CBCAddressing>::getType()
{
    return ConfigTypeCBCAddressing;
}

CtiConfig_type ConfigurationPart<CBC_DNP>::getType()
{
    return ConfigTypeCBC_DNP;
}

CtiConfig_type ConfigurationPart<CBC_UDP>::getType()
{
    return ConfigTypeCBC_UDP;
}

}//Config
}//Cti


