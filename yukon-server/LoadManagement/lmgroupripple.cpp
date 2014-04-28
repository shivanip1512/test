#include "precompiled.h"

#include "dbaccess.h"
#include "lmgroupripple.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

using std::string;
using std::endl;

using Cti::MacroOffset;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMGroupRipple, CTILMGROUPRIPPLE_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupRipple::CtiLMGroupRipple() :
_shedtime(0),
_refreshsent(false)
{
}

CtiLMGroupRipple::CtiLMGroupRipple(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMGroupRipple::CtiLMGroupRipple(const CtiLMGroupRipple& groupripple)
{
    operator=(groupripple);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMGroupRipple::~CtiLMGroupRipple()
{
}

/*---------------------------------------------------------------------------
    getShedTime

    Returns the shed time of the group
---------------------------------------------------------------------------*/
LONG CtiLMGroupRipple::getShedTime() const
{

    return _shedtime;
}

/*---------------------------------------------------------------------------
    setShedTime

    Sets the shed time of the group
---------------------------------------------------------------------------*/
CtiLMGroupRipple& CtiLMGroupRipple::setShedTime(LONG shed)
{

    _shedtime = shed;
    return *this;
}


/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupRipple::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    string controlString("control shed");

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending time refresh command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
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
    method of smart cycle with the appropriate cycle percent, period length,
    and the default count of periods.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupRipple::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Can not smart cycle an Ripple Load Management Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

/*-------------------------------------------------------------------------
    createRotationRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupRipple::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    string controlString("control shed");

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending rotation command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
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
CtiRequestMsg* CtiLMGroupRipple::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    string controlString("control shed");

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
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
    doesMasterCycleNeedToBeUpdated


---------------------------------------------------------------------------*/
BOOL CtiLMGroupRipple::doesMasterCycleNeedToBeUpdated(CtiTime currentTime, CtiTime controlEndTime, ULONG offTime)
{
    BOOL returnBOOL = FALSE;

    LONG controlTimeLeft = controlEndTime.seconds() - currentTime.seconds();
    LONG trueShedTime = getShedTime()+60;
    if( !_refreshsent &&
        controlTimeLeft < trueShedTime+2 &&
        controlTimeLeft >= trueShedTime-1 )
    {
        returnBOOL = TRUE;
        _refreshsent = TRUE;
        /*{
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - PAOId: " << getPAOId() << " is to be Master Cycle refreshed in: " << __FILE__ << " at:" << __LINE__ << endl;
        }*/
    }
    else if( trueShedTime!=0 )
    {
        if( (offTime/trueShedTime) >= 2 )
        {
            LONG numberOfTimesToExtend = (offTime/trueShedTime)-1;
            for(LONG i=0;i<numberOfTimesToExtend;i++)
            {
                if( currentTime < getLastControlSent()+trueShedTime+2 && // This seems terribly broken, but has been since the beginning of time
                    currentTime >= getLastControlSent()+trueShedTime-1 ) // Please forward all questions to jwolberg circa 2002.
                {
                    returnBOOL = TRUE;
                    /*{
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - PAOId: " << getPAOId() << " is to be Master Cycle extended in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }*/
                }
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Tried to divide by zero in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return returnBOOL;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupRipple& CtiLMGroupRipple::operator=(const CtiLMGroupRipple& right)
{
    if( this != &right )
    {
        CtiLMGroupBase::operator=(right);
        _shedtime = right._shedtime;
        _refreshsent = right._refreshsent;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupRipple::replicate() const
{
    return (CTIDBG_new CtiLMGroupRipple(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMGroupRipple::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);
    _refreshsent = FALSE;
}

