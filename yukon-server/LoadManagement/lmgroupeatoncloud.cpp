#include "precompiled.h"

#include "lmid.h"
#include "lmgroupeatoncloud.h"
#include "logger.h"
#include "amq_connection.h"
#include "amq_queues.h"
#include "LMEatonCloudMessages.h"
#include "message_factory.h"

extern ULONG _LM_DEBUG;


DEFINE_COLLECTABLE( LMGroupEatonCloud, LMGROUPEATONCLOUD_ID )

LMGroupEatonCloud::LMGroupEatonCloud( Cti::RowReader &rdr )
    :   SmartGroupBase( "EatonCloud", rdr )
{
    // empty
}

CtiLMGroupBase* LMGroupEatonCloud::replicate() const
{
    return new LMGroupEatonCloud( *this );
}

bool LMGroupEatonCloud::sendStopControl( bool stopImmediately )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    const CtiTime stopTime; // now

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::EatonCloudStopRequest,
        Serialization::MessageSerializer<LMEatonCloudStopRequest>::serialize( 
            {
                getPAOId(),
                stopTime,
                stopImmediately
                    ? LMEatonCloudStopRequest::StopType::Restore
                    : LMEatonCloudStopRequest::StopType::StopCycle
            } ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Stop command, LM Group: " << getPAOName() );
    }

    setLastControlSent( stopTime );
    setLastStopTimeSent( stopTime );
    setGroupControlState( InactiveState );

    return true;
}

bool LMGroupEatonCloud::sendShedControl( long controlMinutes )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    const long controlSeconds = 60 * controlMinutes;

    const CtiTime
        startTime,  // now
        stopTime = startTime + controlSeconds;

    // shed == cycle at 100% duty cycle with no ramp in/out

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::EatonCloudCyclingRequest,
        Serialization::MessageSerializer<LMEatonCloudCycleRequest>::serialize( 
            {
                getPAOId(),
                startTime,
                stopTime,
                LMEatonCloudCycleRequest::CycleType::StandardCycle,
                LMEatonCloudCycleRequest::RampingState::Off,
                LMEatonCloudCycleRequest::RampingState::Off,
                100,
                controlSeconds,
                100
            } ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Shed command, LM Group: " << getPAOName() );
    }

    setLastControlSent( startTime );
    setLastStopTimeSent( stopTime );

    return true;
}

bool LMGroupEatonCloud::sendCycleControl( CycleControlParameters parameters )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    const static std::map<long, LMEatonCloudCycleRequest::CycleType>   cycleLookup
    {
        {   0,  LMEatonCloudCycleRequest::CycleType::StandardCycle  },
        {   1,  LMEatonCloudCycleRequest::CycleType::TrueCycle      },
        {   2,  LMEatonCloudCycleRequest::CycleType::SmartCycle     }
    };

    const CtiTime
        startTime,  // now
        stopTime = startTime + parameters.controlDurationSeconds;

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::EatonCloudCyclingRequest,
        Serialization::MessageSerializer<LMEatonCloudCycleRequest>::serialize( 
            {
                getPAOId(),
                startTime,
                stopTime,
                cycleLookup.at(parameters.cyclingOption ),
                parameters.rampIn
                    ? LMEatonCloudCycleRequest::RampingState::On
                    : LMEatonCloudCycleRequest::RampingState::Off,
                parameters.rampOut
                    ? LMEatonCloudCycleRequest::RampingState::On
                    : LMEatonCloudCycleRequest::RampingState::Off,
                parameters.dutyCyclePercent,
                parameters.dutyCyclePeriod,
                parameters.criticality
            } ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Cycle command, LM Group: " << getPAOName() );
    }

    if ( getGroupControlState() != ActiveState )
    {
        setControlStartTime( startTime );
        incrementDailyOps();
    }

    setLastControlSent( startTime );
    setLastStopTimeSent( stopTime );
    setGroupControlState( ActiveState );

    return true;
}

bool LMGroupEatonCloud::sendNoControl()
{
    // No message to send

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "No Control gear for " << _groupTypeName << " LM Group: " << getPAOName() );
    }

    //setGroupControlState( InactiveState );  // TODO -- jmoc - do we need to do this here?

    return false;
}

bool LMGroupEatonCloud::doesStopRequireCommandAt( const CtiTime & currentTime ) const
{
    // TODO -- jmoc - is this true?
    // Always send the restore at the end of control.

    return true;
}

