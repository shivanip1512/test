#include "precompiled.h"

#include "lmid.h"
#include "logger.h"
#include "amq_connection.h"
#include "amq_queues.h"
#include "lmgrouphoneywell.h"
#include "LMHoneywellMessages.h"

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE(LMGroupHoneywell, LMGROUPHONEYWELL_ID)


LMGroupHoneywell::LMGroupHoneywell(Cti::RowReader &rdr)
    :   SmartGroupBase( "Honeywell", rdr )
{
}

LMGroupHoneywell::~LMGroupHoneywell()
{
}

CtiLMGroupBase* LMGroupHoneywell::replicate() const
{
    return new LMGroupHoneywell(*this);
}

bool LMGroupHoneywell::sendCycleControl( const long programID,
                                         const long dutyCycle,
                                         const long controlDurationSeconds,
                                         const bool mandatory,
                                         const bool rampInOutOption )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::Qpid::Queues::OutboundQueue;

    CtiTime now;

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::HoneywellCyclingControl,
        std::make_unique<LMHoneywellCyclingControlMessage>(
            programID,
            getPAOId(),
            dutyCycle,
            static_cast<int>(now.seconds()),
            controlDurationSeconds,
            mandatory,
            rampInOutOption));

    if (_LM_DEBUG & LM_DEBUG_STANDARD)
    {
        CTILOG_DEBUG(dout, "Sending " << _groupTypeName << " Cycle command, LM Group: " << getPAOName() << ", control minutes: "
            << (controlDurationSeconds / 60) << ", percent: " << dutyCycle);
    }

    setLastControlSent(now);
    setLastStopTimeSent(now + controlDurationSeconds);

    if (getGroupControlState() != CtiLMGroupBase::ActiveState)
    {
        setControlStartTime(now);
        incrementDailyOps();
        setIsRampingOut(false);
    }

    setIsRampingIn(false);
    setGroupControlState(CtiLMGroupBase::ActiveState);

    return true;
}

bool LMGroupHoneywell::sendSetpointControl( const long programID,
                                            const bool temperatureOption,
                                            const bool mandatory,
                                            const int  temperatureOffset,
                                            const int  controlDurationSeconds )
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::Qpid::Queues::OutboundQueue;

    CtiTime now;

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::HoneywellSetpointControl,
        std::make_unique<LMHoneywellSetpointControlMessage>(
            programID,
            getPAOId(),
            temperatureOption,
            mandatory,
            temperatureOffset,
            now.seconds(),
            controlDurationSeconds ) );

    if ( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending " << _groupTypeName << " Setpoint command, LM Group: " << getPAOName()
                                << ", control minutes: " << ( controlDurationSeconds / 60 )
                                << ", control: "
                                << ( temperatureOption ? "HEAT " : "COOL " ) << temperatureOffset << " degrees" );
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

bool LMGroupHoneywell::sendStopControl(bool stopImmediately /* unused */)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::Qpid::Queues::OutboundQueue;

    CtiTime now;

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::HoneywellRestore,
        std::make_unique<LMHoneywellRestoreMessage>(
            getPAOId(),
            static_cast<int>(now.seconds())));

    if (_LM_DEBUG & LM_DEBUG_STANDARD)
    {
        CTILOG_DEBUG(dout, "Sending " << _groupTypeName << " Stop command, LM Group: " << getPAOName());
    }

    setLastControlSent(now);
    setLastStopTimeSent(now);

    return true;
}

bool LMGroupHoneywell::sendShedControl(long controlMinutes)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::Qpid::Queues::OutboundQueue;

    // shed == mandatory cycle at 100% duty cycle with no ramp in/out

    CtiTime now;

    ActiveMQConnectionManager::enqueueMessage(
        OutboundQueue::HoneywellCyclingControl,
        std::make_unique<LMHoneywellCyclingControlMessage>(
            0,      // no program
            getPAOId(),
            100,
            static_cast<int>(now.seconds()),
            controlMinutes * 60,
            true,
            false));

    if (_LM_DEBUG & LM_DEBUG_STANDARD)
    {
        CTILOG_DEBUG(dout, "Sending " << _groupTypeName << " Shed command, LM Group: " << getPAOName() << ", control minutes: " << controlMinutes);
    }

    setLastControlSent(now);
    setLastStopTimeSent(now + (controlMinutes * 60));

    return true;
}
