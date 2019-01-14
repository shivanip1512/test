#include "precompiled.h"

#include "lmid.h"
#include "lmgroupitron.h"
#include "logger.h"
#include "amq_connection.h"
#include "amq_queues.h"
//#include "LMNestMessages.h"

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

bool LMGroupItron::sendCycleControl( long controlDurationSeconds )
{
    using namespace Cti::Messaging;
//    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    CtiTime utcNow( now - now.secondOffsetToGMT() );

//    ActiveMQConnectionManager::enqueueMessage( 
//        OutboundQueue::NestCyclingControl, 
//        std::make_unique<LMNestCyclingControlMessage>( 
//            getPAOId(), 
//            utcNow.seconds(),
//            controlDurationSeconds ) );


    CTILOG_INFO( dout, "**********" );
    CTILOG_INFO( dout, "**********" );
    CTILOG_INFO( dout, "     Sending " << _groupTypeName << " Cycle command, LM Group: " << getPAOName() );
    CTILOG_INFO( dout, "**********" );
    CTILOG_INFO( dout, "**********" );


    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
//        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Cycle command, LM Group: " << getPAOName() );
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
//    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    CtiTime utcNow( now - now.secondOffsetToGMT() );

//    ActiveMQConnectionManager::enqueueMessage(
//        OutboundQueue::NestRestore,
//        std::make_unique<LMNestRestoreMessage>(
//            getPAOId(),
//            utcNow.seconds() ) );

    CTILOG_INFO( dout, "**********" );
    CTILOG_INFO( dout, "**********" );
    CTILOG_INFO( dout, "     Sending " << _groupTypeName << " Stop command, LM Group: " << getPAOName() );
    CTILOG_INFO( dout, "**********" );
    CTILOG_INFO( dout, "**********" );


    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
//        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Stop command, LM Group: " << getPAOName() );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now );
    setGroupControlState( InactiveState );

    return true;
}

bool LMGroupItron::sendShedControl( long controlMinutes )
{


    CTILOG_INFO( dout, "**********" );
    CTILOG_INFO( dout, "**********" );
    CTILOG_INFO( dout, "     Sending " << _groupTypeName << " Shed command, LM Group: " << getPAOName() );
    CTILOG_INFO( dout, "**********" );
    CTILOG_INFO( dout, "**********" );



//    CTILOG_INFO( dout, "Shed command is unsupported for " << _groupTypeName << " load groups. LM Group: " << getPAOName() );

    return false;
}

bool LMGroupItron::doesStopRequireCommandAt( const CtiTime & currentTime ) const
{


    return true;

//    return getControlStartTime() < currentTime && currentTime <= getLastStopTimeSent();
}

