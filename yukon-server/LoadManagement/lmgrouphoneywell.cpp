#include "precompiled.h"

#include "lmid.h"
#include "logger.h"
#include "amq_connection.h"
#include "lmgrouphoneywell.h"
#include "LMHoneywellMessages.h"

//Any commented out lines of code aside from the 'shed == ...' line are to make sure messages are not being sent yet!

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE(LMGroupHoneywell, LMGROUPHONEYWELL_ID)


LMGroupHoneywell::LMGroupHoneywell(Cti::RowReader &rdr)
    : CtiLMGroupBase(rdr)
{
}

LMGroupHoneywell::~LMGroupHoneywell()
{
}

CtiLMGroupBase* LMGroupHoneywell::replicate() const
{
    return new LMGroupHoneywell(*this);
}

bool LMGroupHoneywell::sendCycleControl(long dutyCycle,
                                        long controlDurationSeconds,
                                        bool rampInOption,
                                        bool rampOutOption)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    CtiTime utcNow(now - now.secondOffsetToGMT());

    //ActiveMQConnectionManager::enqueueMessage(
    //    OutboundQueue::HoneywellCyclingControl,
    //    std::make_unique<LMHoneywellCyclingControlMessage>(
    //        getPAOId(),
    //        dutyCycle,
    //        static_cast<int>(utcNow.seconds()),
    //        controlDurationSeconds,
    //        rampInOption,
    //        rampOutOption));

    if (_LM_DEBUG & LM_DEBUG_STANDARD)
    {
        CTILOG_DEBUG(dout, "Sending honeywell Cycle command, LM Group: " << getPAOName() << ", control minutes: "
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

bool LMGroupHoneywell::sendStopControl(bool stopImmediately /* unused */)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    CtiTime now;
    CtiTime utcNow(now - now.secondOffsetToGMT());

    //ActiveMQConnectionManager::enqueueMessage(
    //    OutboundQueue::HoneywellRestore,
    //    std::make_unique<LMHoneywellRestoreMessage>(
    //        getPAOId(),
    //        static_cast<int>(utcNow.seconds())));

    if (_LM_DEBUG & LM_DEBUG_STANDARD)
    {
        CTILOG_DEBUG(dout, "Sending honeywell Stop command, LM Group: " << getPAOName());
    }

    setLastControlSent(now);
    setLastStopTimeSent(now);

    return true;
}

bool LMGroupHoneywell::sendShedControl(long controlMinutes)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    // shed == cycle at 100% duty cycle with no ramp in/out

    CtiTime now;
    CtiTime utcNow(now - now.secondOffsetToGMT());

    //ActiveMQConnectionManager::enqueueMessage(
    //    OutboundQueue::HoneywellCyclingControl,
    //    std::make_unique<LMHoneywellCyclingControlMessage>(
    //        getPAOId(),
    //        100,
    //        static_cast<int>(utcNow.seconds()),
    //        controlMinutes * 60,
    //        false,
    //        false));

    if (_LM_DEBUG & LM_DEBUG_STANDARD)
    {
        CTILOG_DEBUG(dout, "Sending honeywell Shed command, LM Group: " << getPAOName() << ", control minutes: " << controlMinutes);
    }

    setLastControlSent(now);
    setLastStopTimeSent(now + (controlMinutes * 60));

    return true;
}

// borrowed from SEP group...  honeywell devices also know how to stop themselves
bool LMGroupHoneywell::doesStopRequireCommandAt(const CtiTime &currentTime) const
{
    return getLastStopTimeSent() > currentTime + 30 || getLastStopTimeSent() == gInvalidCtiTime;
}

CtiRequestMsg* LMGroupHoneywell::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    CTILOG_INFO(dout, "Can not Time Refresh a honeywell Group,");

    return 0;
}

CtiRequestMsg* LMGroupHoneywell::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CTILOG_INFO(dout, "Can not Smart Cycle a honeywell Group,");

    return 0;
}

CtiRequestMsg* LMGroupHoneywell::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    CTILOG_INFO(dout, "Can not Rotation a honeywell Group,");

    return 0;
}

CtiRequestMsg* LMGroupHoneywell::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    CTILOG_INFO(dout, "Can not Master Cycle a honeywell Group,");

    return 0;
}