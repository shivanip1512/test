/*---------------------------------------------------------------------------
        Filename:  lmgroupversacom.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMGroupVersacom.
                        CtiLMGroupVersacom maintains the state and handles
                        the persistence of versacom groups in Load Management.

        Initial Date:  2/9/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmgroupbase.h"
#include "lmgroupversacom.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

extern BOOL _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupVersacom, CTILMGROUPVERSACOM_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupVersacom::CtiLMGroupVersacom()
{   
}

CtiLMGroupVersacom::CtiLMGroupVersacom(RWDBReader& rdr)
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

/*---------------------------------------------------------------------------
    getUtilityAddress

    Returns the tility address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupVersacom::getUtilityAddress() const
{

    return _utilityaddress;
}

/*---------------------------------------------------------------------------
    getSectionAddress

    Returns the section address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupVersacom::getSectionAddress() const
{

    return _sectionaddress;
}

/*---------------------------------------------------------------------------
    getClassAddress

    Returns the class address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupVersacom::getClassAddress() const
{

    return _classaddress;
}

/*---------------------------------------------------------------------------
    getDivisionAddress

    Returns the division address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupVersacom::getDivisionAddress() const
{

    return _divisionaddress;
}

/*---------------------------------------------------------------------------
    getAddressUsage

    Returns the address usage of the group
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupVersacom::getAddressUsage() const
{

    return _addressusage;
}

/*---------------------------------------------------------------------------
    getRelayUsage

    Returns the relay usage of the group
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupVersacom::getRelayUsage() const
{

    return _relayusage;
}

/*---------------------------------------------------------------------------
    getRouteId

    Returns the route id of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupVersacom::getRouteId() const
{

    return _routeid;
}

/*---------------------------------------------------------------------------
    setUtilityAddress

    Sets the utility address of the group
---------------------------------------------------------------------------*/
CtiLMGroupVersacom& CtiLMGroupVersacom::setUtilityAddress(ULONG utiladd)
{

    _utilityaddress = utiladd;
    return *this;
}

/*---------------------------------------------------------------------------
    setSectionAddress

    Sets the section address of the group
---------------------------------------------------------------------------*/
CtiLMGroupVersacom& CtiLMGroupVersacom::setSectionAddress(ULONG sectadd)
{

    _sectionaddress = sectadd;
    return *this;
}

/*---------------------------------------------------------------------------
    setClassAddress

    Sets the class address of the group
---------------------------------------------------------------------------*/
CtiLMGroupVersacom& CtiLMGroupVersacom::setClassAddress(ULONG classadd)
{

    _classaddress = classadd;
    return *this;
}

/*---------------------------------------------------------------------------
    setDivisionAddress

    Sets the division address of the group
---------------------------------------------------------------------------*/
CtiLMGroupVersacom& CtiLMGroupVersacom::setDivisionAddress(ULONG divadd)
{

    _divisionaddress = divadd;
    return *this;
}

/*---------------------------------------------------------------------------
    setAddressUsage

    Sets the address usage of the group
---------------------------------------------------------------------------*/
CtiLMGroupVersacom& CtiLMGroupVersacom::setAddressUsage(const RWCString& adduse)
{

    _addressusage = adduse;
    return *this;
}

/*---------------------------------------------------------------------------
    setRelayUsage

    Sets the relay usage of the group
---------------------------------------------------------------------------*/
CtiLMGroupVersacom& CtiLMGroupVersacom::setRelayUsage(const RWCString& relayuse)
{

    _relayusage = relayuse;
    return *this;
}

/*---------------------------------------------------------------------------
    setRouteId

    Sets the route id address of the group
---------------------------------------------------------------------------*/
CtiLMGroupVersacom& CtiLMGroupVersacom::setRouteId(ULONG rteid)
{

    _routeid = rteid;
    return *this;
}


/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupVersacom::createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime, int priority) const
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
    method of smart cycle with the appropriate cycle percent, period length
    in minutes, and the default count of periods.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupVersacom::createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const
{
    char tempchar[64];
    RWCString controlString = RWCString("control cycle ");
    _ultoa(percent,tempchar,10);
    controlString += tempchar;
    controlString += " count ";
    _ultoa(defaultCount,tempchar,10);
    controlString += tempchar;
    controlString += " period ";
    controlString += convertSecondsToEvenTimeString(period);

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending smart cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    createRotationRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupVersacom::createRotationRequestMsg(ULONG sendRate, ULONG shedTime, int priority) const
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
CtiRequestMsg* CtiLMGroupVersacom::createMasterCycleRequestMsg(ULONG offTime, ULONG period, int priority) const
{
    RWCString controlString = RWCString("control shed ");
    controlString += convertSecondsToEvenTimeString(offTime-60);

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Sending master cycle command, LM Group: " << getPAOName() << ", string: " << controlString << ", priority: " << priority << endl;
    }
    return new CtiRequestMsg(getPAOId(), controlString,0,0,0,0,0,0,priority);
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMGroupVersacom::restoreGuts(RWvistream& istrm)
{



    CtiLMGroupBase::restoreGuts( istrm );

    istrm >> _utilityaddress
          >> _sectionaddress
          >> _classaddress
          >> _divisionaddress
          >> _addressusage
          >> _relayusage
          >> _routeid;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupVersacom::saveGuts(RWvostream& ostrm ) const  
{


        
    CtiLMGroupBase::saveGuts( ostrm );

    ostrm << _utilityaddress
          << _sectionaddress
          << _classaddress
          << _divisionaddress
          << _addressusage
          << _relayusage
          << _routeid;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupVersacom& CtiLMGroupVersacom::operator=(const CtiLMGroupVersacom& right)
{


    if( this != &right )
    {
        CtiLMGroupBase::operator=(right);
        _utilityaddress = right._utilityaddress;
        _sectionaddress = right._sectionaddress;
        _classaddress = right._classaddress;
        _divisionaddress = right._divisionaddress;
        _addressusage = right._addressusage;
        _relayusage = right._relayusage;
        _routeid = right._routeid;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMGroupVersacom::operator==(const CtiLMGroupVersacom& right) const
{

    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupVersacom::operator!=(const CtiLMGroupVersacom& right) const
{

    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupVersacom::replicate() const
{
    return (new CtiLMGroupVersacom(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMGroupVersacom::restore(RWDBReader& rdr)
{


    CtiLMGroupBase::restore(rdr);
}

/*---------------------------------------------------------------------------
    restoreVersacomSpecificDatabaseEntries
    
    Restores the database entries for a versacom group that are not contained
    in the base table.
---------------------------------------------------------------------------*/
void CtiLMGroupVersacom::restoreVersacomSpecificDatabaseEntries(RWDBReader& rdr)
{


    rdr["utilityaddress"] >> _utilityaddress;
    rdr["sectionaddress"] >> _sectionaddress;
    rdr["classaddress"] >> _classaddress;
    rdr["divisionaddress"] >> _divisionaddress;
    rdr["addressusage"] >> _addressusage;
    rdr["relayusage"] >> _relayusage;
    rdr["routeid"] >> _routeid;
}

