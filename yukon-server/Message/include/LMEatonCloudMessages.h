#pragma once

#include "dlldefs.h"
#include "ctitime.h"


namespace Cti::Messaging::LoadManagement
{

struct IM_EX_MSG LMEatonCloudStopRequest
{
    enum class StopType
    {
        Restore,
        StopCycle
    };

    LMEatonCloudStopRequest( int      groupId,
                             CtiTime  stopTime,
                             StopType stopType );

    int      _groupId;
    CtiTime  _stopTime;
    StopType _stopType;
};

struct IM_EX_MSG LMEatonCloudCycleRequest
{
    enum class CycleType
    {
        StandardCycle,
        TrueCycle,
        SmartCycle
    };

    enum class RampingState
    {
        Off,
        On
    };

    LMEatonCloudCycleRequest( int          groupId,
                              CtiTime      startTime,
                              CtiTime      stopTime,
                              CycleType    cycleType,
                              RampingState rampIn,
                              RampingState rampOut,
                              long         dutyCyclePercent,
                              long         dutyCyclePeriod,
                              long         criticality );

    int          _groupId;
    CtiTime      _startTime,
                 _stopTime;
    CycleType    _cycleType;
    RampingState _rampIn,
                 _rampOut;
    long         _dutyCyclePercent,
                 _dutyCyclePeriod,
                 _criticality;
};

}

