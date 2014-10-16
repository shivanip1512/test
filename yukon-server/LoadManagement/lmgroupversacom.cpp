#include "precompiled.h"

#include "dbaccess.h"
#include "lmgroupbase.h"
#include "lmgroupversacom.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

using std::string;
using std::endl;

using Cti::MacroOffset;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMGroupVersacom, CTILMGROUPVERSACOM_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupVersacom::CtiLMGroupVersacom()
{
}

CtiLMGroupVersacom::CtiLMGroupVersacom(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMGroupVersacom::CtiLMGroupVersacom(const CtiLMGroupVersacom& groupversa)
{
    operator=(groupversa);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMGroupVersacom::~CtiLMGroupVersacom()
{
}


/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupVersacom::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    // Versacom shed times are a maximum of 8 hours (28800 seconds).
    shedTime = std::min(shedTime, 28800L);

    string controlString("control shed ");
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
CtiRequestMsg* CtiLMGroupVersacom::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    char tempchar[64];
    string controlString("control cycle ");
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
    createRotationRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupVersacom::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    // Versacom shed times are a maximum of 8 hours (28800 seconds).
    shedTime = std::min(shedTime, 28800L);

    string controlString("control shed ");
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
CtiRequestMsg* CtiLMGroupVersacom::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    // Versacom shed times are a maximum of 8 hours (28800 seconds).
    offTime = std::min(offTime - 60, 28800L);

    string controlString("control shed ");
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
CtiLMGroupVersacom& CtiLMGroupVersacom::operator=(const CtiLMGroupVersacom& right)
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
CtiLMGroupBase* CtiLMGroupVersacom::replicate() const
{
    return (CTIDBG_new CtiLMGroupVersacom(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMGroupVersacom::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);
}

