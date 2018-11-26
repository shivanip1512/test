#include "precompiled.h"

#include "lmid.h"
#include "lmgroupnest.h"
#include "logger.h"
#include "amq_connection.h"
#include "amq_queues.h"
#include "LMNestMessages.h"

extern ULONG _LM_DEBUG;



DEFINE_COLLECTABLE( LMGroupNest, LMGROUPNEST_ID )

LMGroupNest::LMGroupNest( Cti::RowReader &rdr )
    :   SmartGroupBase( "nest", rdr )
{
    // empty
}


LMGroupNest::~LMGroupNest()
{
    // empty
}


CtiLMGroupBase* LMGroupNest::replicate() const
{
    return new LMGroupNest( *this );
}


bool LMGroupNest::sendCycleControl( long controlDurationSeconds )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    CtiTime utcNow( now - now.secondOffsetToGMT() );

    ActiveMQConnectionManager::enqueueMessage( 
        OutboundQueue::NestCyclingControl, 
        std::make_unique<LMNestCyclingControlMessage>( 
            getPAOId(), 
            static_cast<int>( utcNow.seconds() ),
            controlDurationSeconds ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Cycle command, LM Group: " << getPAOName() );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now );

    return true;
}

bool LMGroupNest::sendStopControl( bool stopImmediately )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    CtiTime utcNow( now - now.secondOffsetToGMT() );

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::NestRestore,
        std::make_unique<LMNestRestoreMessage>(
            getPAOId(),
            static_cast<int>( utcNow.seconds() ) ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Stop command, LM Group: " << getPAOName() );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now );

    return true;
}

bool LMGroupNest::sendShedControl( long controlMinutes )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    CtiTime utcNow( now - now.secondOffsetToGMT() );

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::NestRestore,
        std::make_unique<LMNestRestoreMessage>(
            getPAOId(),
            static_cast<int>( utcNow.seconds() ) ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending " << _groupTypeName << " Shed command, LM Group: " << getPAOName()
                            << ", control minutes: " << controlMinutes );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + ( controlMinutes * 60 ) );

    return true;
}

