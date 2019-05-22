#include "precompiled.h"

#include "lmid.h"
#include "lmgroupmeterdisconnect.h"
#include "logger.h"
#include "amq_connection.h"
#include "amq_queues.h"
#include "LMMeterDisconnectMessages.h"

extern ULONG _LM_DEBUG;



DEFINE_COLLECTABLE( LMGroupMeterDisconnect, LMGROUPMETERDISCONNECT_ID )

LMGroupMeterDisconnect::LMGroupMeterDisconnect( Cti::RowReader &rdr )
    :   SmartGroupBase( "MeterDisconnect", rdr )
{
    // empty
}

CtiLMGroupBase* LMGroupMeterDisconnect::replicate() const
{
    return new LMGroupMeterDisconnect( *this );
}

bool LMGroupMeterDisconnect::sendControl( long controlSeconds )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    CtiTime utcNow( now - now.secondOffsetToGMT() );

    ActiveMQConnectionManager::enqueueMessage( 
        OutboundQueue::MeterDisconnectControl, 
        std::make_unique<LMMeterDisconnectControlMessage>( 
            getPAOId(), 
            utcNow.seconds(),
            controlSeconds ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Control command, LM Group: " << getPAOName() );
    }

    if ( getGroupControlState() != ActiveState )
    {
        setControlStartTime( now );
        incrementDailyOps();
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + controlSeconds );
    setGroupControlState( ActiveState );

    return true;
}

bool LMGroupMeterDisconnect::sendStopControl( bool stopImmediately )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    CtiTime utcNow( now - now.secondOffsetToGMT() );

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::MeterDisconnectRestore,
        std::make_unique<LMMeterDisconnectRestoreMessage>(
            getPAOId(),
            utcNow.seconds() ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Restore command, LM Group: " << getPAOName() );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now );
    setGroupControlState( InactiveState );

    return true;
}

bool LMGroupMeterDisconnect::sendShedControl( long controlMinutes )
{
    CTILOG_WARN( dout, _groupTypeName << " Shed command is unsupported for LM Group: " << getPAOName() );

    return true;
}

bool LMGroupMeterDisconnect::doesStopRequireCommandAt( const CtiTime & currentTime ) const
{
    // Always send the restore at the end of control.

    return true;
}

