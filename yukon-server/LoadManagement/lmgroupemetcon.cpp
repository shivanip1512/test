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

    return _goldaddress;
}

/*---------------------------------------------------------------------------
    getSilverAddress

    Returns the silver address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupEmetcon::getSilverAddress() const
{

    return _silveraddress;
}

/*---------------------------------------------------------------------------
    getAddressUsage

    Returns the address usage of the group
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupEmetcon::getAddressUsage() const
{

    return _addressusage;
}

/*---------------------------------------------------------------------------
    getRelayUsage

    Returns the relay usage of the group
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupEmetcon::getRelayUsage() const
{

    return _relayusage;
}

/*---------------------------------------------------------------------------
    getRouteId

    Returns the route id of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupEmetcon::getRouteId() const
{

    return _routeid;
}

/*---------------------------------------------------------------------------
    setGoldAddress

    Sets the gold address of the group
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::setGoldAddress(ULONG goldadd)
{

    _goldaddress = goldadd;
    return *this;
}

/*---------------------------------------------------------------------------
    setSilverAddress

    Sets the silver address of the group
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::setSilverAddress(ULONG silveradd)
{

    _silveraddress = silveradd;
    return *this;
}

/*---------------------------------------------------------------------------
    setAddressUsage

    Sets the address usage of the group
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::setAddressUsage(const RWCString& adduse)
{

    _addressusage = adduse;
    return *this;
}

/*---------------------------------------------------------------------------
    setRelayUsage

    Sets the relay usage of the group
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::setRelayUsage(const RWCString& relayuse)
{

    _relayusage = relayuse;
    return *this;
}

/*---------------------------------------------------------------------------
    setRouteId

    Sets the route id of the group
---------------------------------------------------------------------------*/
CtiLMGroupEmetcon& CtiLMGroupEmetcon::setRouteId(ULONG rteid)
{

    _routeid = rteid;
    return *this;
}


/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupEmetcon::createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control shed ");
    controlString += convertSecondsToEvenTimeString(shedTime);

    if( _LM_DEBUG )
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
CtiRequestMsg* CtiLMGroupEmetcon::createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const
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
CtiRequestMsg* CtiLMGroupEmetcon::createRotationRequestMsg(ULONG sendRate, ULONG shedTime, int priority) const
{
    RWCString controlString = RWCString("control shed ");
    controlString += convertSecondsToEvenTimeString(shedTime);

    if( _LM_DEBUG )
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
CtiRequestMsg* CtiLMGroupEmetcon::createMasterCycleRequestMsg(ULONG offTime, ULONG period, int priority) const
{
    RWCString controlString = RWCString("control shed ");
    ULONG shedTime = 450;
    if( offTime > 570 && offTime <= 1220 )
    {
        shedTime = 900;
    }
    else if( offTime > 1220 && offTime <= 1920 )
    {
        shedTime = 1800;
    }
    else if( offTime > 1920 )
    {
        shedTime = 3600;
    }

    controlString += convertSecondsToEvenTimeString(shedTime);

    if( _LM_DEBUG )
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

    ULONG controlTimeLeft = groupControlDone - secondsFrom1901;
    if( !_refreshsent &&
        controlTimeLeft < 572 &&
        controlTimeLeft >= 569 )
    {
        returnBOOL = TRUE;
        _refreshsent = TRUE;
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - PAOId: " << getPAOId() << " is to be Master Cycle refreshed." << endl;
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


    if( this != &right )
    {
        CtiLMGroupBase::operator=(right);
        _goldaddress = right._goldaddress;
        _silveraddress = right._silveraddress;
        _addressusage = right._addressusage;
        _relayusage = right._relayusage;
        _routeid = right._routeid;
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
}

/*---------------------------------------------------------------------------
    restoreEmetconSpecificDatabaseEntries
    
    Restores the database entries for a emetcon group that are not contained
    in the base table.
---------------------------------------------------------------------------*/
void CtiLMGroupEmetcon::restoreEmetconSpecificDatabaseEntries(RWDBReader& rdr)
{


    rdr["goldaddress"] >> _goldaddress;
    rdr["silveraddress"] >> _silveraddress;
    rdr["addressusage"] >> _addressusage;
    rdr["relayusage"] >> _relayusage;
    rdr["routeid"] >> _routeid;

    _refreshsent = FALSE;
}

