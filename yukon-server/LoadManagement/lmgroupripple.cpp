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

extern BOOL _LM_DEBUG;

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
    getRouteId

    Returns the route id of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupRipple::getRouteId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _routeid;
}

/*---------------------------------------------------------------------------
    getShedTime

    Returns the shed time of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupRipple::getShedTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _shedtime;
}

/*---------------------------------------------------------------------------
    getControlValue

    Returns the control value of the group
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupRipple::getControlValue() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlvalue;
}

/*---------------------------------------------------------------------------
    getRestoreValue

    Returns the restore value of the group
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupRipple::getRestoreValue() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _restorevalue;
}


/*---------------------------------------------------------------------------
    setRouteId

    Sets the route id of the group
---------------------------------------------------------------------------*/
CtiLMGroupRipple& CtiLMGroupRipple::setRouteId(ULONG rteid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _routeid = rteid;
    return *this;
}

/*---------------------------------------------------------------------------
    setShedTime

    Sets the shed time of the group
---------------------------------------------------------------------------*/
CtiLMGroupRipple& CtiLMGroupRipple::setShedTime(ULONG shed)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _shedtime = shed;
    return *this;
}

/*---------------------------------------------------------------------------
    setControlValue

    Sets the control value of the group
---------------------------------------------------------------------------*/
CtiLMGroupRipple& CtiLMGroupRipple::setControlValue(const RWCString& control)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlvalue = control;
    return *this;
}

/*---------------------------------------------------------------------------
    setRestoreValue

    Sets the restore value of the group
---------------------------------------------------------------------------*/
CtiLMGroupRipple& CtiLMGroupRipple::setRestoreValue(const RWCString& restore)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _restorevalue = restore;
    return *this;
}


/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupRipple::createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control shed");

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending time refresh command, LM Group: " << getPAOName() << ", string: " << controlString << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createSmartCycleRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of smart cycle with the appropriate cycle percent, period length,
    and the default count of periods.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupRipple::createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Can not smart cycle an Ripple Load Management Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

/*-------------------------------------------------------------------------
    createRotationRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupRipple::createRotationRequestMsg(ULONG sendRate, ULONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control shed");

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending rotation command, LM Group: " << getPAOName() << ", string: " << controlString << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createMasterCycleRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of master cycle with the appropriate off time, period length.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupRipple::createMasterCycleRequestMsg(ULONG offTime, ULONG period, int priority) const
{
    RWCString controlString = RWCString("control shed");

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*---------------------------------------------------------------------------
    doesMasterCycleNeedToBeUpdated

    
---------------------------------------------------------------------------*/
BOOL CtiLMGroupRipple::doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime)
{
    BOOL returnBOOL = FALSE;

    ULONG controlTimeLeft = groupControlDone - secondsFrom1901;
    ULONG trueShedTime = getShedTime()+60;
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
            ULONG numberOfTimesToExtend = (offTime/trueShedTime)-1;
            for(ULONG i=0;i<numberOfTimesToExtend;i++)
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

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLMGroupBase::restoreGuts( istrm );

    istrm >> _routeid
          >> _shedtime
          >> _controlvalue
          >> _restorevalue;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupRipple::saveGuts(RWvostream& ostrm ) const  
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        
    CtiLMGroupBase::saveGuts( ostrm );

    ostrm << _routeid
          << _shedtime
          << _controlvalue
          << _restorevalue;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupRipple& CtiLMGroupRipple::operator=(const CtiLMGroupRipple& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( this != &right )
    {
        CtiLMGroupBase::operator=(right);
        _routeid = right._routeid;
        _shedtime = right._shedtime;
        _controlvalue = right._controlvalue;
        _restorevalue = right._restorevalue;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMGroupRipple::operator==(const CtiLMGroupRipple& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupRipple::operator!=(const CtiLMGroupRipple& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLMGroupBase::restore(rdr);
}

/*---------------------------------------------------------------------------
    restoreRippleSpecificDatabaseEntries
    
    Restores the database entries for a ripple group that are not contained
    in the base table.
---------------------------------------------------------------------------*/
void CtiLMGroupRipple::restoreRippleSpecificDatabaseEntries(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    rdr["routeid"] >> _routeid;
    rdr["shedtime"] >> _shedtime;
    rdr["controlvalue"] >> _controlvalue;
    rdr["restorevalue"] >> _restorevalue;
}

