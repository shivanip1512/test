#include "precompiled.h"

#include "lmid.h"
#include "lmgroupnest.h"
#include "logger.h"
#include "amq_connection.h"
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


bool LMGroupNest::sendCriticalCycleControl( long controlDurationSeconds )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    CtiTime utcNow( now - now.secondOffsetToGMT() );

//    ActiveMQConnectionManager::enqueueMessage( 
//            OutboundQueue::NestCriticalCyclingControl, 
//            std::make_unique<LMNestCriticalCyclingControlMessage>( 
//                    getPAOId(), 
//                    static_cast<int>(utcNow.seconds()),
//                    controlDurationSeconds ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Critical Cycle command, LM Group: " << getPAOName() );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now );

    return true;
}

bool LMGroupNest::sendStandardCycleControl( long controlDurationSeconds,
                                            int  prepOption,
                                            int  peakOption,
                                            int  postOption )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    CtiTime utcNow( now - now.secondOffsetToGMT() );

//    ActiveMQConnectionManager::enqueueMessage( 
//            OutboundQueue::NestStandardCyclingControl, 
//            std::make_unique<LMNestStandardCyclingControlMessage>( 
//                    getPAOId(), 
//                    static_cast<int>(utcNow.seconds()),
//                    controlDurationSeconds,
//                    prepOption,
//                    peakOption,
//                    postOption ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG( dout, "Sending " << _groupTypeName << " Standard Cycle command, LM Group: " << getPAOName() );
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

    //  Needs definition of the message and queue name

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

    //  Needs definition of the message and queue name

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending " << _groupTypeName << " Shed command, LM Group: " << getPAOName()
                            << ", control minutes: " << controlMinutes );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + ( controlMinutes * 60 ) );

    return true;
}

