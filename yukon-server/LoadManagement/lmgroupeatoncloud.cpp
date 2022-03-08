#include "precompiled.h"

#include "lmid.h"
#include "lmgroupeatoncloud.h"
#include "logger.h"
#include "amq_connection.h"
#include "amq_queues.h"
#include "LMEatonCloudMessages.h"
#include "message_factory.h"
#include "std_helper.h"

extern ULONG _LM_DEBUG;


DEFINE_COLLECTABLE( LMGroupEatonCloud, LMGROUPEATONCLOUD_ID )

LMGroupEatonCloud::LMGroupEatonCloud( Cti::RowReader &rdr )
    :   SmartGroupBase( "EatonCloud", rdr ),
        _vRelayID{ 0 }
{
    if ( rdr[ "RelayUsage" ].isNotNull() )
    {
        _vRelayID = std::clamp( rdr[ "RelayUsage" ].as<unsigned>(), 0u, 255u );
    }
}

CtiLMGroupBase* LMGroupEatonCloud::replicate() const
{
    return new LMGroupEatonCloud( *this );
}

bool LMGroupEatonCloud::sendStopControl( bool stopImmediately )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::Qpid::Queues::OutboundQueue;

    // Send the stop only if we are currently active, the idea being that if we get here via a no control gear
    //  then we don't need to send a message since we are already stopped.

    if ( getGroupControlState() == ActiveState )
    {
        // HACK - the UI only has 'Restore' as an option, but it should be only 'StopCycle'...
        // jmoc -- do we need the 'Restore' at all, future functionality??
        stopImmediately = false;
        //

        const CtiTime stopTime; // now

        const auto serializedMessage = 
            Serialization::MessageSerializer<LMEatonCloudStopRequest>::serialize( 
                {
                    getPAOId(),
                    stopTime,
                    stopImmediately
                        ? LMEatonCloudStopRequest::StopType::Restore
                        : LMEatonCloudStopRequest::StopType::StopCycle,
                    _vRelayID
                } );

        if ( serializedMessage.empty() )
        {
            CTILOG_ERROR( dout, "Serialization error for " << _groupTypeName << " Stop command, LM Group: " << getPAOName() );

            return false;
        }

        ActiveMQConnectionManager::enqueueMessage(
            OutboundQueue::EatonCloudStopRequest,
            serializedMessage );

        if ( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Stop command, LM Group: " << getPAOName() );
        }

        setLastControlSent( stopTime );
        setLastStopTimeSent( stopTime );

        setGroupControlState( InactiveState );
    }

    return true;
}

bool LMGroupEatonCloud::sendShedControl( long controlMinutes )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::Qpid::Queues::OutboundQueue;

    const long controlSeconds = 60 * controlMinutes;

    const CtiTime
        startTime,  // now
        stopTime = startTime + controlSeconds;

    // shed == cycle at 100% duty cycle with no ramp in/out

    const auto serializedMessage = 
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
                100,
                _vRelayID
            } );

    if ( serializedMessage.empty() )
    {
        CTILOG_ERROR( dout, "Serialization error for " << _groupTypeName << " Shed command, LM Group: " << getPAOName() );

        return false;
    }

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::EatonCloudScheduledCyclingRequest,
        serializedMessage );

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
    using Cti::Messaging::Qpid::Queues::OutboundQueue;
    using Cti::LoadManagement::SmartGearCyclingOption;

    const static std::map<SmartGearCyclingOption, LMEatonCloudCycleRequest::CycleType>   supportedCycleTypes
    {
        {   SmartGearCyclingOption::StandardCycle,  LMEatonCloudCycleRequest::CycleType::StandardCycle  },
        {   SmartGearCyclingOption::TrueCycle,      LMEatonCloudCycleRequest::CycleType::TrueCycle      },
        {   SmartGearCyclingOption::SmartCycle,     LMEatonCloudCycleRequest::CycleType::SmartCycle     }
    };

    const auto cycleTypeToSend = Cti::mapFind( supportedCycleTypes, parameters.cyclingOption );

    // Do not send a control if we are an unsupported cycle type or we fail to get the send type from the map
    if ( parameters.cyclingOption == SmartGearCyclingOption::Unsupported || ! cycleTypeToSend )
    {
        CTILOG_ERROR( dout, "Unsupported cycle type. No control will be sent." );
        return false;
    }

    const CtiTime
        startTime,  // now
        stopTime = startTime + parameters.controlDurationSeconds;

    const auto serializedMessage = 
        Serialization::MessageSerializer<LMEatonCloudCycleRequest>::serialize( 
            {
                getPAOId(),
                startTime,
                stopTime,
                *cycleTypeToSend,
                parameters.rampIn
                    ? LMEatonCloudCycleRequest::RampingState::On
                    : LMEatonCloudCycleRequest::RampingState::Off,
                parameters.rampOut
                    ? LMEatonCloudCycleRequest::RampingState::On
                    : LMEatonCloudCycleRequest::RampingState::Off,
                parameters.dutyCyclePercent,
                parameters.dutyCyclePeriod,
                parameters.criticality,
                _vRelayID
            } );

    if ( serializedMessage.empty() )
    {
        CTILOG_ERROR( dout, "Serialization error for " << _groupTypeName << " Cycle command, LM Group: " << getPAOName() );

        return false;
    }

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::EatonCloudScheduledCyclingRequest,
        serializedMessage );

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

bool LMGroupEatonCloud::sendNoControl( bool doRestore )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::Qpid::Queues::OutboundQueue;

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "No Control gear for " << _groupTypeName << " LM Group: " << getPAOName() );
    }

    // Send the stop only if we are currently active, the idea being that if we get here via some sort of
    //  start - we will be Inactive and send no message.  But, if we are currently running and we get a
    //  gear change to this gear, then we will send a stop request.

    if ( getGroupControlState() == ActiveState )
    {
        sendStopControl( doRestore );
    }

    return false;
}
