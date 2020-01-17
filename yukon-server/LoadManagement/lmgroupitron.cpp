#include "precompiled.h"

#include "lmid.h"
#include "lmgroupitron.h"
#include "logger.h"
#include "amq_connection.h"
#include "amq_queues.h"
#include "LMItronMessages.h"

extern ULONG _LM_DEBUG;



DEFINE_COLLECTABLE( LMGroupItron, LMGROUPITRON_ID )

LMGroupItron::LMGroupItron( Cti::RowReader &rdr )
    :   SmartGroupBase( "Itron", rdr )
{
    // empty
}

CtiLMGroupBase* LMGroupItron::replicate() const
{
    return new LMGroupItron( *this );
}

bool LMGroupItron::sendCycleControl( long controlDurationSeconds,
                                     bool rampInOption,
                                     bool rampOutOption,
                                     long cycleOption,
                                     long dutyCyclePercent,
                                     long dutyCyclePeriod,
                                     long criticality )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;

    ActiveMQConnectionManager::enqueueMessage( 
        OutboundQueue::ItronCyclingControl, 
        std::make_unique<LMItronCyclingControlMessage>( 
            getPAOId(), 
            now.seconds(),
            controlDurationSeconds,
            rampInOption,
            rampOutOption,
            cycleOption,
            dutyCyclePercent,
            dutyCyclePeriod,
            criticality ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Cycle command, LM Group: " << getPAOName() );
    }

    if ( getGroupControlState() != ActiveState )
    {
        setControlStartTime( now );
        incrementDailyOps();
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + controlDurationSeconds );
    setGroupControlState( ActiveState );

    return true;
}

bool LMGroupItron::sendStopControl( bool stopImmediately )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::ItronRestore,
        std::make_unique<LMItronRestoreMessage>(
            getPAOId(),
            now.seconds() ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Stop command, LM Group: " << getPAOName() );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now );
    setGroupControlState( InactiveState );

    return true;
}

bool LMGroupItron::sendShedControl( long controlMinutes )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;

    // shed == cycle at 100% duty cycle with no ramp in/out

    ActiveMQConnectionManager::enqueueMessage( 
        OutboundQueue::ItronCyclingControl, 
        std::make_unique<LMItronCyclingControlMessage>( 
            getPAOId(), 
            now.seconds(),
            controlMinutes * 60,
            false,
            false,
            0,      // hardcoding to 'Standard Cycling' gear type
            100,
            controlMinutes * 60,
            100 ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Shed command, LM Group: " << getPAOName() );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + ( controlMinutes * 60 ) );

    return true;
}

bool LMGroupItron::doesStopRequireCommandAt( const CtiTime & currentTime ) const
{
    // Always send the restore at the end of control.

    return true;
}

