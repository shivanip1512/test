#include "yukon.h"
#include "lmgroupdigisep.h"
#include "lmid.h"
#include "logger.h"
#include "amq_connection.h"
#include "lmsepcontrolmessage.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( LMGroupDigiSEP, LMGROUPDIGISEP_ID )

static const char  SEPAverageCycleUnused  = 0x80;
static const char  SEPStandardCycleUnused = 0xFF;
static const short SEPSetPointUnused      = 0x8000;
static const char  SEPTempOffsetUnused    = 0xFF;
// Events are a bitfield.
static const char  SEPEventRandomizeStart = 0x01;
static const char  SEPEventRandomizeStop  = 0x02;

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

bool LMGroupDigiSEP::sendSEPCycleControl(long controlMinutes, long cyclePercent, bool isTrueCycle, bool randomizeStart, bool randomizeStop)
{
    using namespace Cti::Messaging;

    // Average cycle count value is added to 100 to get true value, so -10 = 90% cycle.
    long averageCyclePercent = isTrueCycle ? cyclePercent - 100 : SEPAverageCycleUnused;
    long standardCyclePercent = isTrueCycle ? SEPStandardCycleUnused : cyclePercent;
    unsigned char eventFlags = randomizeStart ? 1 : 0 + randomizeStop ? (1 << 1) : 0;

    std::auto_ptr<StreamableMessage> msg(new LMSepControlMessage(getPAOId(), 0, controlMinutes, SEPTempOffsetUnused, SEPTempOffsetUnused, SEPSetPointUnused, SEPSetPointUnused, averageCyclePercent, standardCyclePercent, eventFlags));
    gActiveMQConnection.enqueueMessage(ActiveMQConnectionManager::Queue_SmartEnergyProfileControl, msg);

    setLastControlSent(CtiTime());
    
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


bool LMGroupDigiSEP::sendStopControl(bool stopImmediatelly)
{
    using namespace Cti::Messaging;

    std::auto_ptr<StreamableMessage> msg(new LMSepControlMessage(getPAOId(), 0, 0, SEPTempOffsetUnused, SEPTempOffsetUnused, SEPSetPointUnused, SEPSetPointUnused, SEPAverageCycleUnused, SEPStandardCycleUnused, 0));
    gActiveMQConnection.enqueueMessage(ActiveMQConnectionManager::Queue_SmartEnergyProfileControl, msg);
    
    setLastControlSent(CtiTime::now());
    return true;
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
