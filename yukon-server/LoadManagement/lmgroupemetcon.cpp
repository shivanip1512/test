#include "precompiled.h"

#include "dbaccess.h"
#include "lmgroupemetcon.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

using std::string;
using std::endl;

using Cti::MacroOffset;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMGroupEmetcon, CTILMGROUPEMETCON_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon::CtiLMGroupEmetcon() :
_refreshsent(false)
{
}

CtiLMGroupEmetcon::CtiLMGroupEmetcon(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMGroupEmetcon::CtiLMGroupEmetcon(const CtiLMGroupEmetcon& groupemet)
{
    operator=(groupemet);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon::~CtiLMGroupEmetcon()
{
}


/*----------------------------------------------------------------------------
  setGroupControlState

  This is overridden so we can reset some internat state when our control
  state changes.
----------------------------------------------------------------------------*/
CtiLMGroupBase& CtiLMGroupEmetcon::setGroupControlState(LONG controlstate)
{
    if( getGroupControlState() == CtiLMGroupBase::InactiveState &&
        CtiLMGroupBase::ActiveState == controlstate )
    {
        _refreshsent = FALSE;
    }
    return CtiLMGroupBase::setGroupControlState(controlstate);
}

/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupEmetcon::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
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
    method of smart cycle with the appropriate cycle percent, period length,
    and the default count of periods.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupEmetcon::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CTILOG_INFO(dout, "Can not smart cycle an Emetcon Load Management Group,");
    return NULL;
}

/*-------------------------------------------------------------------------
    createRotationRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupEmetcon::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
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
CtiRequestMsg* CtiLMGroupEmetcon::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    string controlString("control shed ");
    LONG shedTime = 450;

    //CASE LESS THAN 8.5: 7.5 min shed is ok and warning message
    if( offTime < 510 )
    {
        DOUBLE realizedPercentage = 570.0 / (DOUBLE)period;
        CTILOG_WARN(dout, "Master Cycle: cannot send a shed of less than 9.5 minutes (including random time-in) to Emetcon groups, LM Group: " << getPAOName()
        << ", given the 9.5 minute minimum shed time and the cycle period specified the group will give a realized control percentage of:" << realizedPercentage );
    }
    //CASE 8.5 TO 10.5: 7.5 min shed is ok and no over lap
    else if( offTime >= 510 && offTime <= 630 )
    {
        //shedTime = 450;  already set
    }
    //CASE 16.5 TO 17.5: 15 min shed is ok and no over lap
    else if( offTime > 630 && offTime <= 1220 )
    {
        shedTime = 900;
    }
    //CASE 31.5 TO 32.5: 30 min shed is ok and no over lap
    else if( offTime > 1220 && offTime <= 1920 )
    {
        shedTime = 1800;
    }
    //CASE 59.5 TO 60.5: 60 min shed is ok and no over lap
    else if( offTime > 1920 )
    {
        shedTime = 3600;
    }

    controlString += buildShedString(shedTime);

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
    doesMasterCycleNeedToBeUpdated


---------------------------------------------------------------------------*/
BOOL CtiLMGroupEmetcon::doesMasterCycleNeedToBeUpdated(CtiTime currentTime, CtiTime controlEndTime, ULONG offTime)
{
    BOOL returnBOOL = FALSE;

    LONG controlTimeLeft = controlEndTime.seconds() - currentTime.seconds();
    if( !_refreshsent &&
        controlTimeLeft < 580 &&
        controlTimeLeft >= 560 ) //This function better be evaluated every 20 seconds or we might miss!!
    {
        //CASE 8.5 TO 10.5: 7.5 min shed is ok and no over lap
        //CASE 16.5 TO 17.5: 15 min shed is ok and no over lap
        //CASE 31.5 TO 32.5: 30 min shed is ok and no over lap
        //CASE 59.5 TO 60.5: 60 min shed is ok and no over lap
        if( !( offTime >= 510 && offTime <= 630 ||
               offTime >= 990 && offTime <= 1050 ||
               offTime >= 1890 && offTime <= 1950 ||
               offTime >= 3570 && offTime <= 3630 ) )
        {
            returnBOOL = TRUE;
            _refreshsent = TRUE;
            CTILOG_INFO(dout, "PAOId: " << getPAOId() << " is to be Master Cycle refreshed.");
        }
    }

    return returnBOOL;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::operator=(const CtiLMGroupEmetcon& right)
{
    if( this != &right )
    {
        CtiLMGroupBase::operator=(right);
        _refreshsent = right._refreshsent;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupEmetcon::replicate() const
{
    return(CTIDBG_new CtiLMGroupEmetcon(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMGroupEmetcon::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);

    _refreshsent = FALSE;
}

