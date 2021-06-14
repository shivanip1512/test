#include "precompiled.h"

#include "lmid.h"
#include "logger.h"
#include "amq_connection.h"
#include "amq_queues.h"
#include "LMGroupEcobee.h"
#include "LMEcobeeMessages.h"

extern ULONG _LM_DEBUG;


DEFINE_COLLECTABLE( LMGroupEcobee, LMGROUPECOBEE_ID )


LMGroupEcobee::LMGroupEcobee( Cti::RowReader &rdr )
    :   CtiLMGroupBase( rdr )
{
}


LMGroupEcobee::~LMGroupEcobee()
{
}


CtiLMGroupBase* LMGroupEcobee::replicate() const
{
    return new LMGroupEcobee( *this );
}


bool LMGroupEcobee::sendCycleControl( long dutyCycle,
                                      long controlDurationSeconds,
                                      bool mandatory,
                                      bool rampInOutOption )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    ctitime_t localSeconds = now.getLocalTimeSeconds();

    ActiveMQConnectionManager::enqueueMessage( 
            OutboundQueue::EcobeeCyclingControl, 
            std::make_unique<LMEcobeeCyclingControlMessage>( 
                    getPAOId(),
                    dutyCycle,
                    static_cast<int>(localSeconds),
                    controlDurationSeconds,
                    mandatory,
                    rampInOutOption ));
    
    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending ecobee Cycle command, LM Group: " << getPAOName() << ", control minutes: "
             << ( controlDurationSeconds / 60 ) << ", percent: " << dutyCycle);
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + controlDurationSeconds );

    if ( getGroupControlState() != CtiLMGroupBase::ActiveState )
    {
        setControlStartTime( now );
        incrementDailyOps();
        setIsRampingOut( false );
    }

    setIsRampingIn( false );
    setGroupControlState( CtiLMGroupBase::ActiveState );

    return true;
}


bool LMGroupEcobee::sendSetpointControl( long controlDurationSeconds,
                                         bool temperatureOption,
                                         bool mandatory,
                                         long temperatureOffset )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    ctitime_t localSeconds = now.getLocalTimeSeconds();

    ActiveMQConnectionManager::enqueueMessage( 
            OutboundQueue::EcobeeSetpointControl, 
            std::make_unique<LMEcobeeSetpointControlMessage>( 
                    getPAOId(),
                    localSeconds,
                    controlDurationSeconds,
                    temperatureOption,
                    mandatory,
                    temperatureOffset ));

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending ecobee Setpoint command, LM Group: " << getPAOName() << ", control minutes: "
             << ( controlDurationSeconds / 60 ) << ", control: " << ( temperatureOption ? "HEAT " : "COOL " )
                     << temperatureOffset << " degrees" );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + controlDurationSeconds );

    if ( getGroupControlState() != CtiLMGroupBase::ActiveState )
    {
        setControlStartTime( now );
        incrementDailyOps();
        setIsRampingOut( false );
    }

    setIsRampingIn( false );
    setGroupControlState( CtiLMGroupBase::ActiveState );

    return true;
}

bool LMGroupEcobee::sendEcobeePlusControl( long controlDurationSeconds,
                                           bool temperatureOption,                                               
                                           long randomTimeSeconds )    
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    ctitime_t localSeconds = now.getLocalTimeSeconds();

    ActiveMQConnectionManager::enqueueMessage(
            OutboundQueue::EcobeePlusControl,
            std::make_unique<LMEcobeePlusControlMessage>(    
                           getPAOId(),
                           localSeconds,
                           controlDurationSeconds,
                           temperatureOption,
                           randomTimeSeconds ));                       

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending ecobee Plus command, LM Group: " << getPAOName() << ", control minutes: "
            << ( controlDurationSeconds / 60 ) << ", heating event: " << ( temperatureOption ? "HEAT " : "COOL " ) << ", random time: " << randomTimeSeconds );
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + controlDurationSeconds );

    if ( getGroupControlState() != CtiLMGroupBase::ActiveState )
    {
        setControlStartTime( now );
        incrementDailyOps();       
    }

    setGroupControlState( CtiLMGroupBase::ActiveState );

    return true;
}

bool LMGroupEcobee::sendStopControl( bool stopImmediately /* unused */ )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    ctitime_t localSeconds = now.getLocalTimeSeconds();

    ActiveMQConnectionManager::enqueueMessage( 
            OutboundQueue::EcobeeRestore, 
            std::make_unique<LMEcobeeRestoreMessage>( 
                    getPAOId(), 
                    static_cast<int>(localSeconds) ) );

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending ecobee Stop command, LM Group: " << getPAOName());
    }

    setLastControlSent( now );
    setLastStopTimeSent( now );

    return true;
}


bool LMGroupEcobee::sendShedControl( long controlMinutes )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    // shed == cycle at 100% duty cycle with no ramp in/out

    CtiTime now;
    ctitime_t localSeconds = now.getLocalTimeSeconds();

    ActiveMQConnectionManager::enqueueMessage( 
            OutboundQueue::EcobeeCyclingControl,
            std::make_unique<LMEcobeeCyclingControlMessage>( 
                    getPAOId(),
                    100,
                    static_cast<int>(localSeconds),
                    controlMinutes * 60,
                    true,
                    false ));

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending ecobee Shed command, LM Group: " << getPAOName() << ", control minutes: " << controlMinutes);
    }

    setLastControlSent( now );
    setLastStopTimeSent( now + ( controlMinutes * 60 ) );

    return true;
}


// borrowed from SEP group...  ecobee devices also know how to stop themselves
bool LMGroupEcobee::doesStopRequireCommandAt(const CtiTime &currentTime) const
{
    return getLastStopTimeSent() > currentTime + 30 || getLastStopTimeSent() == gInvalidCtiTime;
}


CtiRequestMsg* LMGroupEcobee::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    CTILOG_INFO(dout, "Can not Time Refresh an ecobee Group,");

    return 0;
}


CtiRequestMsg* LMGroupEcobee::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CTILOG_INFO(dout, "Can not Smart Cycle an ecobee Group,");

    return 0;
}


CtiRequestMsg* LMGroupEcobee::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    CTILOG_INFO(dout, "Can not Rotation an ecobee Group,");

    return 0;
}


CtiRequestMsg* LMGroupEcobee::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    CTILOG_INFO(dout, "Can not Master Cycle an ecobee Group,");

    return 0;
}

