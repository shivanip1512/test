/*---------------------------------------------------------------------------
        Filename:  lmgroupripple.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMGroupRipple.
                        CtiLMGroupRipple maintains the state and handles
                        the persistence of ripple groups in Load Management.

        Initial Date:  2/9/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmgroupripple.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupRipple, CTILMGROUPRIPPLE_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupRipple::CtiLMGroupRipple()
{   
}

CtiLMGroupRipple::CtiLMGroupRipple(RWDBReader& rdr)
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

    Creates a new CtiPILRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiPILRequestMsg* CtiLMGroupRipple::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control shed");

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending time refresh command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiPILRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createSmartCycleRequestMsg

    Creates a new CtiPILRequestMsg pointer for a program gear with a control
    method of smart cycle with the appropriate cycle percent, period length,
    and the default count of periods.
--------------------------------------------------------------------------*/
CtiPILRequestMsg* CtiLMGroupRipple::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Can not smart cycle an Ripple Load Management Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

/*-------------------------------------------------------------------------
    createRotationRequestMsg

    Creates a new CtiPILRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiPILRequestMsg* CtiLMGroupRipple::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control shed");

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending rotation command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiPILRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createMasterCycleRequestMsg

    Creates a new CtiPILRequestMsg pointer for a program gear with a control
    method of master cycle with the appropriate off time, period length.
--------------------------------------------------------------------------*/
CtiPILRequestMsg* CtiLMGroupRipple::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    RWCString controlString = RWCString("control shed");

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiPILRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*---------------------------------------------------------------------------
    doesMasterCycleNeedToBeUpdated

    
---------------------------------------------------------------------------*/
BOOL CtiLMGroupRipple::doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime)
{
    BOOL returnBOOL = FALSE;

    LONG controlTimeLeft = groupControlDone - secondsFrom1901;
    LONG trueShedTime = getShedTime()+60;
    if( !_refreshsent &&
        controlTimeLeft < trueShedTime+2 &&
        controlTimeLeft >= trueShedTime-1 )
    {
        returnBOOL = TRUE;
        _refreshsent = TRUE;
        /*{
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - PAOId: " << getPAOId() << " is to be Master Cycle refreshed in: " << __FILE__ << " at:" << __LINE__ << endl;
        }*/
    }
    else if( trueShedTime!=0 )
    {
        if( (offTime/trueShedTime) >= 2 )
        {
            LONG numberOfTimesToExtend = (offTime/trueShedTime)-1;
            for(LONG i=0;i<numberOfTimesToExtend;i++)
            {
                if( secondsFrom1901 < getLastControlSent().seconds()+trueShedTime+2 &&
                    secondsFrom1901 >= getLastControlSent().seconds()+trueShedTime-1 )
                {
                    returnBOOL = TRUE;
                    /*{
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - PAOId: " << getPAOId() << " is to be Master Cycle extended in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }*/
                }
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Tried to divide by zero in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return returnBOOL;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMGroupRipple::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );

    istrm >> _shedtime;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupRipple::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMGroupBase::saveGuts( ostrm );

    ostrm << _shedtime;
    return;
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
    operator==
---------------------------------------------------------------------------*/
int CtiLMGroupRipple::operator==(const CtiLMGroupRipple& right) const
{

    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupRipple::operator!=(const CtiLMGroupRipple& right) const
{

    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupRipple::replicate() const
{
    return (new CtiLMGroupRipple(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMGroupRipple::restore(RWDBReader& rdr)
{
    CtiLMGroupBase::restore(rdr);

    RWDBNullIndicator isNull;
    rdr["rippleshedtime"] >> isNull;
    if( !isNull )
    {
        rdr["rippleshedtime"] >> _shedtime;
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Unexpected database load issue, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    _refreshsent = FALSE;
}

