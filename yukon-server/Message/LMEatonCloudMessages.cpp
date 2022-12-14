#include "precompiled.h"

#include "LMEatonCloudMessages.h"


namespace Cti::Messaging::LoadManagement
{

LMEatonCloudStopRequest::LMEatonCloudStopRequest( int      groupId,
                                                  CtiTime  stopTime,
                                                  StopType stopType )
    :   _groupId( groupId ),
        _stopTime( stopTime ),
        _stopType( stopType )
{
    // empty
}

LMEatonCloudCycleRequest::LMEatonCloudCycleRequest( int          groupId,
                                                    CtiTime      startTime,
                                                    CtiTime      stopTime,
                                                    CycleType    cycleType,
                                                    RampingState rampIn,
                                                    RampingState rampOut,
                                                    long         dutyCyclePercent,
                                                    long         dutyCyclePeriod,
                                                    long         criticality )
    :   _groupId( groupId ),
        _startTime( startTime ),
        _stopTime( stopTime ),
        _cycleType( cycleType ),
        _rampIn( rampIn ),
        _rampOut( rampOut ),
        _dutyCyclePercent( dutyCyclePercent ),
        _dutyCyclePeriod( dutyCyclePeriod ),
        _criticality( criticality )
{
    // empty
}

}

