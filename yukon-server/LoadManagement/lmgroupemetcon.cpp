/*---------------------------------------------------------------------------
        Filename:  lmgroupemetcon.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMGroupEmetcon.
                        CtiLMGroupEmetcon maintains the state and handles
                        the persistence of emetcon groups in Load Management.

        Initial Date:  2/9/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmgroupemetcon.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

extern BOOL _LM_DEBUG;

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

/*---------------------------------------------------------------------------
    getGoldAddress

    Returns the gold address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupEmetcon::getGoldAddress() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _goldaddress;
}

/*---------------------------------------------------------------------------
    getSilverAddress

    Returns the silver address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupEmetcon::getSilverAddress() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _silveraddress;
}

/*---------------------------------------------------------------------------
    getAddressUsage

    Returns the address usage of the group
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupEmetcon::getAddressUsage() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _addressusage;
}

/*---------------------------------------------------------------------------
    getRelayUsage

    Returns the relay usage of the group
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupEmetcon::getRelayUsage() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _relayusage;
}

/*---------------------------------------------------------------------------
    getRouteId

    Returns the route id of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupEmetcon::getRouteId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _routeid;
}

/*---------------------------------------------------------------------------
    setGoldAddress

    Sets the gold address of the group
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::setGoldAddress(ULONG goldadd)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _goldaddress = goldadd;
    return *this;
}

/*---------------------------------------------------------------------------
    setSilverAddress

    Sets the silver address of the group
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::setSilverAddress(ULONG silveradd)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _silveraddress = silveradd;
    return *this;
}

/*---------------------------------------------------------------------------
    setAddressUsage

    Sets the address usage of the group
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::setAddressUsage(const RWCString& adduse)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _addressusage = adduse;
    return *this;
}

/*---------------------------------------------------------------------------
    setRelayUsage

    Sets the relay usage of the group
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::setRelayUsage(const RWCString& relayuse)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _relayusage = relayuse;
    return *this;
}

/*---------------------------------------------------------------------------
    setRouteId

    Sets the route id of the group
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::setRouteId(ULONG rteid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _routeid = rteid;
    return *this;
}


/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupEmetcon::createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime) const
{
    char tempchar[64];
    RWCString controlString = RWCString("control shed ");
    _ultoa(shedTime,tempchar,10);
    controlString += tempchar;
    controlString += "s";

    return new CtiRequestMsg(getPAOId(), controlString);
}

/*-------------------------------------------------------------------------
    createSmartCycleRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of smart cycle with the appropriate cycle percent, period length,
    and the default count of periods.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupEmetcon::createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount) const
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
CtiRequestMsg* CtiLMGroupEmetcon::createRotationRequestMsg(ULONG sendRate, ULONG shedTime) const
{
    char tempchar[64];
    RWCString controlString = RWCString("control shed ");
    _ultoa(shedTime,tempchar,10);
    controlString += tempchar;
    controlString += "s";

    return new CtiRequestMsg(getPAOId(), controlString);
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMGroupEmetcon::restoreGuts(RWvistream& istrm)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLMGroupBase::restoreGuts( istrm );

    istrm >> _goldaddress
          >> _silveraddress
          >> _addressusage
          >> _relayusage
          >> _routeid;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupEmetcon::saveGuts(RWvostream& ostrm ) const  
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        
    CtiLMGroupBase::saveGuts( ostrm );

    ostrm << _goldaddress
          << _silveraddress
          << _addressusage
          << _relayusage
          << _routeid;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::operator=(const CtiLMGroupEmetcon& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( this != &right )
    {
        CtiLMGroupBase::operator=(right);
        _goldaddress = right._goldaddress;
        _silveraddress = right._silveraddress;
        _addressusage = right._addressusage;
        _relayusage = right._relayusage;
        _routeid = right._routeid;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMGroupEmetcon::operator==(const CtiLMGroupEmetcon& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupEmetcon::operator!=(const CtiLMGroupEmetcon& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLMGroupBase::restore(rdr);
}

/*---------------------------------------------------------------------------
    restoreEmetconSpecificDatabaseEntries
    
    Restores the database entries for a emetcon group that are not contained
    in the base table.
---------------------------------------------------------------------------*/
void CtiLMGroupEmetcon::restoreEmetconSpecificDatabaseEntries(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    rdr["goldaddress"] >> _goldaddress;
    rdr["silveraddress"] >> _silveraddress;
    rdr["addressusage"] >> _addressusage;
    rdr["relayusage"] >> _relayusage;
    rdr["routeid"] >> _routeid;
}

