#include "precompiled.h"

#include "lmgroupgolay.h"
#include "lmid.h"
#include "logger.h"
#include "dbaccess.h"

using std::string;
using std::endl;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMGroupGolay, CTILMGROUPGOLAY_ID )

/*---------------------------------------------------------------------------
  Constructors
  ---------------------------------------------------------------------------*/
CtiLMGroupGolay::CtiLMGroupGolay() :
_nominal_timeout(0)
{
}

CtiLMGroupGolay::CtiLMGroupGolay(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMGroupGolay::CtiLMGroupGolay(const CtiLMGroupGolay& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
  Destructor
  ---------------------------------------------------------------------------*/
CtiLMGroupGolay::~CtiLMGroupGolay()
{
}

/*-------------------------------------------------------------------------
  createTimeRefreshRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of time refresh with the appropriate refresh rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupGolay::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    string controlString("control golay shed ");

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
CtiRequestMsg* CtiLMGroupGolay::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CTILOG_WARN(dout, "createSmartCycleRequestMsg() not implemented for golay LM Groups");
    return NULL;
}

/*-------------------------------------------------------------------------
  createTrueCycleRequestMsg

  Creates true cycle request msg which is exactly like a smart cycle but
  with the "truecycle" string at the end of the control string.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupGolay::createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CTILOG_WARN(dout, "createTrueCycleRequestMsg() not implemented for golay LM Groups");
    return NULL;
}

/*-------------------------------------------------------------------------
  createRotationRequestMsg

  Creates a new CtiRequestMsg pointer for a program gear with a control
  method of rotation with the appropriate send rate and shed time.
  --------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupGolay::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    string controlString("control golay shed ");

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
CtiRequestMsg* CtiLMGroupGolay::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    string controlString("control golay shed ");

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority);
    }
    return buildRequestMessage( controlString, priority );
}

/*----------------------------------------------------------------------------
  getNominalTimeout

  Returns the groups nominal timeout
----------------------------------------------------------------------------*/
int CtiLMGroupGolay::getNominalTimeout() const
{
    return _nominal_timeout;
}

/*----------------------------------------------------------------------------
  setNominalTimeout

  Sets this groups nominal timeout
----------------------------------------------------------------------------*/
CtiLMGroupGolay& CtiLMGroupGolay::setNominalTimeout(int nominal_timeout)
{
    _nominal_timeout = nominal_timeout;
    return *this;
}

/*---------------------------------------------------------------------------
  operator=
  ---------------------------------------------------------------------------*/
CtiLMGroupGolay& CtiLMGroupGolay::operator=(const CtiLMGroupGolay& right)
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
CtiLMGroupBase* CtiLMGroupGolay::replicate() const
{
    return (CTIDBG_new CtiLMGroupGolay(*this));
}

/*---------------------------------------------------------------------------
  restore

  Restores given a Reader
  ---------------------------------------------------------------------------*/
void CtiLMGroupGolay::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);
}

