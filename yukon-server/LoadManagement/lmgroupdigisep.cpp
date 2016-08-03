#include "precompiled.h"
#include "lmgroupdigisep.h"
#include "lmid.h"
#include "logger.h"
#include "amq_connection.h"
#include "lmsepcontrolmessage.h"
#include "lmseprestoremessage.h"

using std::string;
using std::endl;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( LMGroupDigiSEP, LMGROUPDIGISEP_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
LMGroupDigiSEP::LMGroupDigiSEP()
{
}

LMGroupDigiSEP::LMGroupDigiSEP(Cti::RowReader &rdr)
{
    restore(rdr);
}

LMGroupDigiSEP::LMGroupDigiSEP(const LMGroupDigiSEP& groupe)
{
    operator=(groupe);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
LMGroupDigiSEP::~LMGroupDigiSEP()
{
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
LMGroupDigiSEP& LMGroupDigiSEP::operator=(const LMGroupDigiSEP& right)
{
    if( this != &right )
    {
        CtiLMGroupBase::operator=(right);
    }

    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* LMGroupDigiSEP::replicate() const
{
    return(CTIDBG_new LMGroupDigiSEP(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void LMGroupDigiSEP::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);
}

bool LMGroupDigiSEP::sendSEPCycleControl(long controlMinutes, long cyclePercent, long criticality, bool isTrueCycle, bool randomizeStart, bool randomizeStop)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    ActiveMQConnectionManager::enqueueMessage(
            OutboundQueue::SmartEnergyProfileControl, 
            LMSepControlMessage::createCycleMessage(
                    getPAOId(),
                    0,
                    controlMinutes,
                    criticality,
                    cyclePercent,
                    isTrueCycle,
                    randomizeStart,
                    randomizeStop));

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending SEP Cycle command, LM Group: " << getPAOName() << ", control minutes: " << controlMinutes << ", percent: " << cyclePercent);
    }

    setLastControlSent(CtiTime());
    setLastStopTimeSent( CtiTime::now() + 60*controlMinutes );

    if( getGroupControlState() != CtiLMGroupBase::ActiveState )
    {
        setControlStartTime(CtiTime());
        incrementDailyOps();
        setIsRampingOut(false);
    }

    setIsRampingIn(false);
    setGroupControlState(CtiLMGroupBase::ActiveState);    // This is updated from dispatch.
    return true;
}

bool LMGroupDigiSEP::sendSEPTempOffsetControl(long controlMinutes, long heatOffset, long coolOffset, bool isCelsius, long criticality, bool randomizeStart, bool randomizeStop)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    unsigned char eventFlags = randomizeStart ? 1 : 0 + randomizeStop ? (1 << 1) : 0;

    if( !isCelsius )
    {
        // Note we are converting Deltas here.
        heatOffset = heatOffset / 1.8;
        coolOffset = coolOffset / 1.8;
    }

    string heatString = CtiNumStr(heatOffset);
    string coolString = CtiNumStr(coolOffset);

    unsigned char tempOffset = coolOffset;
    bool isCoolOffset = true;

    if( heatOffset == 0 && coolOffset > 0 )
    {
        heatString = "unused";
    }
    else if( coolOffset == 0 && heatOffset > 0 )
    {
        coolString = "unused";

        tempOffset = heatOffset;
        isCoolOffset = false;
    }
    else
    {
        CTILOG_INFO(dout, "Invalid temperature settings, both heat and cool offset have non zero values, defaulting to cool: " << getPAOName());
    }

    ActiveMQConnectionManager::enqueueMessage(
            OutboundQueue::SmartEnergyProfileControl,
            LMSepControlMessage::createTempOffsetMessage(
                    getPAOId(),
                    0,
                    controlMinutes,
                    criticality,
                    tempOffset,
                    isCoolOffset,
                    randomizeStart,
                    randomizeStop));

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending SEP Thermostat command, LM Group: " << getPAOName() << ", control minutes: " << controlMinutes << ", heat: " << heatString << " cool: "  << coolString);
    }

    setLastControlSent(CtiTime());
    setLastStopTimeSent( CtiTime::now() + 60*controlMinutes );

    if( getGroupControlState() != CtiLMGroupBase::ActiveState )
    {
        setControlStartTime(CtiTime());
        incrementDailyOps();
        setIsRampingOut(false);
    }

    setIsRampingIn(false);
    setGroupControlState(CtiLMGroupBase::ActiveState);    // This is updated from dispatch.
    return true;
}

bool LMGroupDigiSEP::sendStopControl(bool stopImmediately)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    ActiveMQConnectionManager::enqueueMessage(
            OutboundQueue::SmartEnergyProfileRestore, 
            std::make_unique<LMSepRestoreMessage>(getPAOId(), 0, stopImmediately ? 0 : 1));

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending SEP Stop command, LM Group: " << getPAOName() << ", stop immediately: " << (stopImmediately ? "TRUE" : "FALSE"));
    }

    setLastControlSent(CtiTime::now());
    if(stopImmediately)
    {
        setLastStopTimeSent(CtiTime::now());
    }
    return true;
}

bool LMGroupDigiSEP::sendShedControl(long controlMinutes)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::LoadManagement;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    ActiveMQConnectionManager::enqueueMessage(
            OutboundQueue::SmartEnergyProfileControl, 
            LMSepControlMessage::createSimpleShedMessage(getPAOId(), 0, controlMinutes));

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending SEP Shed command, LM Group: " << getPAOName() << ", control minutes: " << controlMinutes);
    }

    setLastControlSent(CtiTime::now());
    setLastStopTimeSent( CtiTime::now() + 60*controlMinutes );
    return true;
}

/*
  SEP Devices know to stop themselves at Control Complete time.
  If we are at or after that time, there is no need to send a restore command.
*/
bool LMGroupDigiSEP::doesStopRequireCommandAt(const CtiTime &currentTime) const
{
    return getLastStopTimeSent() > currentTime + 30 || getLastStopTimeSent() == gInvalidCtiTime;
}

CtiRequestMsg* LMGroupDigiSEP::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    CTILOG_INFO(dout, "Can not Time Refresh a Digi SEP Group,");
    return NULL;
}

CtiRequestMsg* LMGroupDigiSEP::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CTILOG_INFO(dout, "Can not Smart Cycle a Digi SEP Group,");
    return NULL;
}

CtiRequestMsg* LMGroupDigiSEP::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    CTILOG_INFO(dout, "Can not Rotation a Digi SEP Group,");
    return NULL;
}

CtiRequestMsg* LMGroupDigiSEP::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    CTILOG_INFO(dout, "Can not Master Cycle a Digi SEP Group,");
    return NULL;
}
