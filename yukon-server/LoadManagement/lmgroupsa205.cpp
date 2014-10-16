#include "precompiled.h"

#include "lmgroupsa205.h"
#include "lmid.h"
#include "logger.h"
#include "dbaccess.h"

using std::string;
using std::endl;

using Cti::MacroOffset;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMGroupSA205, CTILMGROUPSA205_ID )

/*---------------------------------------------------------------------------
  Constructors
  ---------------------------------------------------------------------------*/
    CtiLMGroupSA205::CtiLMGroupSA205()
{
}

CtiLMGroupSA205::CtiLMGroupSA205(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMGroupSA205::CtiLMGroupSA205(const CtiLMGroupSA205& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
  Destructor
  ---------------------------------------------------------------------------*/
CtiLMGroupSA205::~CtiLMGroupSA205()
{
}

/*-------------------------------------------------------------------------
  createTimeRefreshRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of time refresh with the appropriate refresh rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA205::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    string controlString("control sa205 shed ");
    controlString += buildShedString(shedTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending time refresh command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString,
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

/*-------------------------------------------------------------------------
  createSmartCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of smart cycle with the appropriate cycle percent, period length
  in minutes, and the default count of periods.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA205::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    char tempchar[64];
    string controlString("control sa205 cycle ");
    _ltoa(percent,tempchar,10);
    controlString += tempchar;
    controlString += " count ";
    _ltoa(defaultCount,tempchar,10);
    controlString += tempchar;
    controlString += " period ";
    controlString += buildPeriodString(period);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending smart cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString,
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

/*-------------------------------------------------------------------------
  createTrueCycleRequestMsg

  Creates true cycle request msg which is exactly like a smart cycle but
  with the "truecycle" string at the end of the control string.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA205::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CTILOG_INFO(dout, "createTrueCycleRequestMsg() not implemented for sa205 LM Groups");
    return NULL;
}

/*-------------------------------------------------------------------------
  createRotationRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of rotation with the appropriate send rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA205::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{

    string controlString("control sa205 shed ");
    controlString += buildShedString(shedTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending rotation command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString,
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

/*-------------------------------------------------------------------------
  createMasterCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of master cycle with the appropriate off time, period length.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA205::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    string controlString("control sa205 shed ");
    controlString += buildShedString(offTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return CTIDBG_new CtiRequestMsg(getPAOId(),
                                    controlString,
                                    0,
                                    0,
                                    0,
                                    MacroOffset::none,
                                    0,
                                    0,
                                    priority);
}

/*---------------------------------------------------------------------------
  operator=
  ---------------------------------------------------------------------------*/
CtiLMGroupSA205& CtiLMGroupSA205::operator=(const CtiLMGroupSA205& right)
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
CtiLMGroupBase* CtiLMGroupSA205::replicate() const
{
    return (new CtiLMGroupSA205(*this));
}

/*---------------------------------------------------------------------------
  restore

  Restores given a Reader
  ---------------------------------------------------------------------------*/
void CtiLMGroupSA205::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);
}

