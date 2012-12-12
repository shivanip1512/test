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

RWDEFINE_COLLECTABLE( LMGroupDigiSEP, LMGROUPDIGISEP_ID )

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
    operator==
---------------------------------------------------------------------------*/
int LMGroupDigiSEP::operator==(const LMGroupDigiSEP& right) const
{
    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int LMGroupDigiSEP::operator!=(const LMGroupDigiSEP& right) const
{
    return CtiLMGroupBase::operator!=(right);
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

    std::auto_ptr<StreamableMessage> msg(LMSepControlMessage::createCycleMessage(getPAOId(),
                                                                                 0,
                                                                                 controlMinutes,
                                                                                 criticality,
                                                                                 cyclePercent,
                                                                                 isTrueCycle,
                                                                                 randomizeStart,
                                                                                 randomizeStop));

    gActiveMQConnection.enqueueMessage(ActiveMQConnectionManager::Queue_SmartEnergyProfileControl, msg);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending SEP Cycle command, LM Group: " << getPAOName() << ", control minutes: " << controlMinutes << ", percent: " << cyclePercent << endl;
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Invalid temperature settings, both heat and cool offset have non zero values, defaulting to cool: " << getPAOName() << endl;
    }

    std::auto_ptr<StreamableMessage> msg(LMSepControlMessage::createTempOffsetMessage(getPAOId(),
                                                                                      0,
                                                                                      controlMinutes,
                                                                                      criticality,
                                                                                      tempOffset,
                                                                                      isCoolOffset,
                                                                                      randomizeStart,
                                                                                      randomizeStop));

    gActiveMQConnection.enqueueMessage(ActiveMQConnectionManager::Queue_SmartEnergyProfileControl, msg);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending SEP Thermostat command, LM Group: " << getPAOName() << ", control minutes: " << controlMinutes << ", heat: " << heatString << " cool: "  << coolString << endl;
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

    std::auto_ptr<StreamableMessage> msg(new LMSepRestoreMessage(getPAOId(), 0, stopImmediately ? 0 : 1));
    gActiveMQConnection.enqueueMessage(ActiveMQConnectionManager::Queue_SmartEnergyProfileRestore, msg);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending SEP Stop command, LM Group: " << getPAOName() << ", stop immediately: " << (stopImmediately ? "TRUE" : "FALSE") << endl;
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

    std::auto_ptr<StreamableMessage> msg(LMSepControlMessage::createSimpleShedMessage(getPAOId(), 0, controlMinutes));
    gActiveMQConnection.enqueueMessage(ActiveMQConnectionManager::Queue_SmartEnergyProfileControl, msg);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending SEP Shed command, LM Group: " << getPAOName() << ", control minutes: " << CtiNumStr(controlMinutes) << endl;
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
    return getLastStopTimeSent() > currentTime || getLastStopTimeSent() == gInvalidCtiTime;
}

CtiRequestMsg* LMGroupDigiSEP::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Can not Time Refresh a Digi SEP Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

CtiRequestMsg* LMGroupDigiSEP::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Can not Smart Cycle a Digi SEP Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

CtiRequestMsg* LMGroupDigiSEP::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Can not Rotation a Digi SEP Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

CtiRequestMsg* LMGroupDigiSEP::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Can not Master Cycle a Digi SEP Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}
