/*---------------------------------------------------------------------------
        Filename:  lmgroupemetcon.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMGroupEmetcon.
                        CtiLMGroupEmetcon maintains the state and handles
                        the persistence of emetcon groups in Load Management.

        Initial Date:  2/9/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmgroupemetcon.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupEmetcon, CTILMGROUPEMETCON_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon::CtiLMGroupEmetcon()
{   
}

CtiLMGroupEmetcon::CtiLMGroupEmetcon(RWDBReader& rdr)
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
    if(getGroupControlState() == CtiLMGroupBase::ActiveState)
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
    RWCString controlString = RWCString("control shed ");
    controlString += buildShedString(shedTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending time refresh command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createSmartCycleRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of smart cycle with the appropriate cycle percent, period length,
    and the default count of periods.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupEmetcon::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Can not smart cycle an Emetcon Load Management Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

/*-------------------------------------------------------------------------
    createRotationRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupEmetcon::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control shed ");
    controlString += buildShedString(shedTime);

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending rotation command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createMasterCycleRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of master cycle with the appropriate off time, period length.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupEmetcon::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    RWCString controlString = RWCString("control shed ");
    LONG shedTime = 450;

    //CASE LESS THAN 8.5: 7.5 min shed is ok and warning message
    if( offTime < 510 )
    {
        DOUBLE realizedPercentage = 570.0 / (DOUBLE)period;
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Master Cycle Warning: cannot send a shed of less than 9.5 minutes (including random time-in) to Emetcon groups, LM Group: " << getPAOName()
             << ", given the 9.5 minute minimum shed time and the cycle period specified the group will give a realized control percentage of:" << realizedPercentage << endl;
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*---------------------------------------------------------------------------
    doesMasterCycleNeedToBeUpdated

    
---------------------------------------------------------------------------*/
BOOL CtiLMGroupEmetcon::doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime)
{
    BOOL returnBOOL = FALSE;

    LONG controlTimeLeft = groupControlDone - secondsFrom1901;
    if( !_refreshsent &&
        controlTimeLeft < 572 &&
        controlTimeLeft >= 569 )
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
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - PAOId: " << getPAOId() << " is to be Master Cycle refreshed." << endl;
            }
        }
    }

    return returnBOOL;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMGroupEmetcon::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupEmetcon::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMGroupBase::saveGuts( ostrm );
    return;
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
    operator==
---------------------------------------------------------------------------*/
int CtiLMGroupEmetcon::operator==(const CtiLMGroupEmetcon& right) const
{

    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupEmetcon::operator!=(const CtiLMGroupEmetcon& right) const
{

    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupEmetcon::replicate() const
{
    return (new CtiLMGroupEmetcon(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMGroupEmetcon::restore(RWDBReader& rdr)
{
    CtiLMGroupBase::restore(rdr);

    _refreshsent = FALSE;
}

