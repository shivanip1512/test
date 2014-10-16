#include "precompiled.h"

#include "lmgroupsadigital.h"
#include "lmid.h"
#include "logger.h"
#include "dbaccess.h"

using std::string;
using std::endl;

using Cti::MacroOffset;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMGroupSADigital, CTILMGROUPSADIGITAL_ID )

/*---------------------------------------------------------------------------
  Constructors
  ---------------------------------------------------------------------------*/
CtiLMGroupSADigital::CtiLMGroupSADigital() :
_nominal_timeout(0)
{
}

CtiLMGroupSADigital::CtiLMGroupSADigital(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMGroupSADigital::CtiLMGroupSADigital(const CtiLMGroupSADigital& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
  Destructor
  ---------------------------------------------------------------------------*/
CtiLMGroupSADigital::~CtiLMGroupSADigital()
{
}

/*-------------------------------------------------------------------------
  createTimeRefreshRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of time refresh with the appropriate refresh rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigital::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    string controlString("control sadigital shed ");

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
CtiRequestMsg* CtiLMGroupSADigital::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CTILOG_WARN(dout, "createSmartCycleRequestMsg() not implemented for saDigital LM Groups");
    return NULL;
}

/*-------------------------------------------------------------------------
  createTrueCycleRequestMsg

  Creates true cycle request msg which is exactly like a smart cycle but
  with the "truecycle" string at the end of the control string.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigital::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CTILOG_WARN(dout, "createTrueCycleRequestMsg() not implemented for saDigital LM Groups");

    return NULL;
}

/*-------------------------------------------------------------------------
  createRotationRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of rotation with the appropriate send rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupSADigital::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    string controlString("control sadigital shed ");

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
CtiRequestMsg* CtiLMGroupSADigital::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    string controlString("control sadigital shed ");

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


/*----------------------------------------------------------------------------
  getNominalTimeout

  Returns the groups nominal timeout
----------------------------------------------------------------------------*/
int CtiLMGroupSADigital::getNominalTimeout() const
{
    return _nominal_timeout;
}

/*----------------------------------------------------------------------------
  setNominalTimeout

  Sets this groups nominal timeout
----------------------------------------------------------------------------*/
CtiLMGroupSADigital& CtiLMGroupSADigital::setNominalTimeout(int nominal_timeout)
{
    _nominal_timeout = nominal_timeout;
    return *this;
}

/*---------------------------------------------------------------------------
  operator=
  ---------------------------------------------------------------------------*/
CtiLMGroupSADigital& CtiLMGroupSADigital::operator=(const CtiLMGroupSADigital& right)
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
CtiLMGroupBase* CtiLMGroupSADigital::replicate() const
{
    return (new CtiLMGroupSADigital(*this));
}

/*---------------------------------------------------------------------------
  restore

  Restores given a Reader
  ---------------------------------------------------------------------------*/
void CtiLMGroupSADigital::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);
}

