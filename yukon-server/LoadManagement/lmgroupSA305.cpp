#include "precompiled.h"

#include "lmgroupsa305.h"
#include "lmid.h"
#include "logger.h"
#include "dbaccess.h"

using std::string;
using std::endl;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMGroupSA305, CTILMGROUPSA305_ID )

/*---------------------------------------------------------------------------
  Constructors
  ---------------------------------------------------------------------------*/
    CtiLMGroupSA305::CtiLMGroupSA305()
{
}

CtiLMGroupSA305::CtiLMGroupSA305(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMGroupSA305::CtiLMGroupSA305(const CtiLMGroupSA305& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
  Destructor
  ---------------------------------------------------------------------------*/
CtiLMGroupSA305::~CtiLMGroupSA305()
{
}

/*-------------------------------------------------------------------------
  createTimeRefreshRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of time refresh with the appropriate refresh rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA305::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    string controlString("control sa305 shed ");
    controlString += buildShedString(shedTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending time refresh command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return buildRequestMessage( controlString, priority );
}

/*-------------------------------------------------------------------------
  createSmartCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of smart cycle with the appropriate cycle percent, period length
  in minutes, and the default count of periods.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA305::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    char tempchar[64];
    string controlString("control sa305 cycle ");
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
    return buildRequestMessage( controlString, priority );
}

/*-------------------------------------------------------------------------
  createTrueCycleRequestMsg

  Creates true cycle request msg which is exactly like a smart cycle but
  with the "truecycle" string at the end of the control string.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA305::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    char tempchar[64];
    string controlString("control sa305 cycle ");
    _ltoa(percent,tempchar,10);
    controlString += tempchar;
    controlString += " count ";
    _ltoa(defaultCount,tempchar,10);
    controlString += tempchar;
    controlString += " period ";
    controlString += buildPeriodString(period);
    controlString += " truecycle";

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending adaptive alg command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return buildRequestMessage( controlString, priority );
}

/*-------------------------------------------------------------------------
  createRotationRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of rotation with the appropriate send rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA305::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    string controlString("control sa305 shed ");
    controlString += buildShedString(shedTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending rotation command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return buildRequestMessage( controlString, priority );
}

/*-------------------------------------------------------------------------
  createMasterCycleRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of master cycle with the appropriate off time, period length.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSA305::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    string controlString("control sa305 shed ");
    controlString += buildShedString(offTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return buildRequestMessage( controlString, priority );
}

/*---------------------------------------------------------------------------
  operator=
  ---------------------------------------------------------------------------*/
CtiLMGroupSA305& CtiLMGroupSA305::operator=(const CtiLMGroupSA305& right)
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
CtiLMGroupBase* CtiLMGroupSA305::replicate() const
{
    return (CTIDBG_new CtiLMGroupSA305(*this));
}

/*---------------------------------------------------------------------------
  restore

  Restores given a Reader
  ---------------------------------------------------------------------------*/
void CtiLMGroupSA305::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);
}

