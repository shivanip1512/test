/*---------------------------------------------------------------------------
        Filename:  lmgroupexpress.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMGroupExpresscom.
                        CtiLMGroupExpresscom maintains the state and handles
                        the persistence of expresscom groups in Load Management.

        Initial Date:  2/9/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmgroupexpresscom.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

extern BOOL _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupExpresscom, CTILMGROUPEXPRESSCOM_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom::CtiLMGroupExpresscom()
{   
}

CtiLMGroupExpresscom::CtiLMGroupExpresscom(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMGroupExpresscom::CtiLMGroupExpresscom(const CtiLMGroupExpresscom& groupexp)
{
    operator=(groupexp);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom::~CtiLMGroupExpresscom()
{
}

/*---------------------------------------------------------------------------
    getRouteId

    Returns the route id of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupExpresscom::getRouteId() const
{

    return _routeid;
}

/*---------------------------------------------------------------------------
    getSerialNumber

    Returns the serial number of the group
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupExpresscom::getSerialNumber() const
{

    return _serialnumber;
}

/*---------------------------------------------------------------------------
    getServiceAddress

    Returns the service address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupExpresscom::getServiceAddress() const
{

    return _serviceaddress;
}

/*---------------------------------------------------------------------------
    getGeoAddress

    Returns the geo address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupExpresscom::getGeoAddress() const
{

    return _geoaddress;
}

/*---------------------------------------------------------------------------
    getSubstationAddress

    Returns the substation address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupExpresscom::getSubstationAddress() const
{

    return _substationaddress;
}

/*---------------------------------------------------------------------------
    getFeederAddress

    Returns the feeder address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupExpresscom::getFeederAddress() const
{

    return _feederaddress;
}

/*---------------------------------------------------------------------------
    getZipCodeAddress

    Returns the zip code address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupExpresscom::getZipCodeAddress() const
{

    return _zipcodeaddress;
}

/*---------------------------------------------------------------------------
    getUDAddress

    Returns the ud address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupExpresscom::getUDAddress() const
{

    return _udaddress;
}

/*---------------------------------------------------------------------------
    getProgramAddress

    Returns the program address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupExpresscom::getProgramAddress() const
{

    return _programaddress;
}

/*---------------------------------------------------------------------------
    getSplinterAddress

    Returns the splinter address of the group
---------------------------------------------------------------------------*/
ULONG CtiLMGroupExpresscom::getSplinterAddress() const
{

    return _splinteraddress;
}

/*---------------------------------------------------------------------------
    getAddressUsage

    Returns the address usage of the group
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupExpresscom::getAddressUsage() const
{

    return _addressusage;
}

/*---------------------------------------------------------------------------
    getRelayUsage

    Returns the relay usage of the group
---------------------------------------------------------------------------*/
const RWCString& CtiLMGroupExpresscom::getRelayUsage() const
{

    return _relayusage;
}


/*---------------------------------------------------------------------------
    setRouteId

    Sets the route id of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setRouteId(ULONG rteid)
{
    _routeid = rteid;
    return *this;
}

/*---------------------------------------------------------------------------
    setSerialNumber

    Sets the serial number of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setSerialNumber(const RWCString& sernum)
{
    _serialnumber = sernum;
    return *this;
}

/*---------------------------------------------------------------------------
    setServiceAddress

    Sets the service address of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setServiceAddress(ULONG add)
{
    _serviceaddress = add;
    return *this;
}

/*---------------------------------------------------------------------------
    setGeoAddress

    Sets the geo address of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setGeoAddress(ULONG add)
{
    _geoaddress = add;
    return *this;
}

/*---------------------------------------------------------------------------
    setSubstationAddress

    Sets the substation address of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setSubstationAddress(ULONG add)
{
    _substationaddress = add;
    return *this;
}

/*---------------------------------------------------------------------------
    setFeederAddress

    Sets the feeder address of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setFeederAddress(ULONG add)
{
    _feederaddress = add;
    return *this;
}

/*---------------------------------------------------------------------------
    setZipCodeAddress

    Sets the zip code address of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setZipCodeAddress(ULONG add)
{
    _zipcodeaddress = add;
    return *this;
}

/*---------------------------------------------------------------------------
    setUDAddress

    Sets the ud address of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setUDAddress(ULONG add)
{
    _udaddress = add;
    return *this;
}

/*---------------------------------------------------------------------------
    setProgramAddress

    Sets the program address of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setProgramAddress(ULONG add)
{
    _programaddress = add;
    return *this;
}

/*---------------------------------------------------------------------------
    setSplinterAddress

    Sets the splinter address of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setSplinterAddress(ULONG add)
{
    _splinteraddress = add;
    return *this;
}

/*---------------------------------------------------------------------------
    setAddressUsage

    Sets the address usage of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setAddressUsage(const RWCString& adduse)
{
    _addressusage = adduse;
    return *this;
}

/*---------------------------------------------------------------------------
    setRelayUsage

    Sets the relay usage of the group
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::setRelayUsage(const RWCString& relayuse)
{
    _relayusage = relayuse;
    return *this;
}


/*-------------------------------------------------------------------------
    createTimeRefreshRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of time refresh with the appropriate refresh rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createTimeRefreshRequestMsg(ULONG refreshRate, ULONG shedTime, int priority) const
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
CtiRequestMsg* CtiLMGroupExpresscom::createSmartCycleRequestMsg(ULONG percent, ULONG period, ULONG defaultCount, int priority) const
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Can not smart cycle an Expresscom Load Management Group, in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return NULL;
}

/*-------------------------------------------------------------------------
    createRotationRequestMsg

    Creates a new CtiRequestMsg pointer for a program gear with a control
    method of rotation with the appropriate send rate and shed time.
--------------------------------------------------------------------------*/
CtiRequestMsg* CtiLMGroupExpresscom::createRotationRequestMsg(ULONG sendRate, ULONG shedTime, int priority) const
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
CtiRequestMsg* CtiLMGroupExpresscom::createMasterCycleRequestMsg(ULONG offTime, ULONG period, int priority) const
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
BOOL CtiLMGroupExpresscom::doesMasterCycleNeedToBeUpdated(ULONG secondsFrom1901, ULONG groupControlDone, ULONG offTime)
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
void CtiLMGroupExpresscom::restoreGuts(RWvistream& istrm)
{
    CtiLMGroupBase::restoreGuts( istrm );

    istrm >> _routeid
          >> _serialnumber
          >> _serviceaddress
          >> _geoaddress
          >> _substationaddress
          >> _feederaddress
          >> _zipcodeaddress
          >> _udaddress
          >> _programaddress
          >> _splinteraddress
          >> _addressusage
          >> _relayusage;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupExpresscom::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMGroupBase::saveGuts( ostrm );

    ostrm << _routeid          
          << _serialnumber     
          << _serviceaddress   
          << _geoaddress       
          << _substationaddress
          << _feederaddress    
          << _zipcodeaddress   
          << _udaddress        
          << _programaddress   
          << _splinteraddress  
          << _addressusage     
          << _relayusage;      

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupExpresscom& CtiLMGroupExpresscom::operator=(const CtiLMGroupExpresscom& right)
{
    if( this != &right )
    {
        CtiLMGroupBase::operator=(right);
        _routeid           = right._routeid          ;
        _serialnumber      = right._serialnumber     ;
        _serviceaddress    = right._serviceaddress   ;
        _geoaddress        = right._geoaddress       ;
        _substationaddress = right._substationaddress;
        _feederaddress     = right._feederaddress    ;
        _zipcodeaddress    = right._zipcodeaddress   ;
        _udaddress         = right._udaddress        ;
        _programaddress    = right._programaddress   ;
        _splinteraddress   = right._splinteraddress  ;
        _addressusage      = right._addressusage     ;
        _relayusage        = right._relayusage       ;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMGroupExpresscom::operator==(const CtiLMGroupExpresscom& right) const
{

    return CtiLMGroupBase::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupExpresscom::operator!=(const CtiLMGroupExpresscom& right) const
{

    return CtiLMGroupBase::operator!=(right);
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupExpresscom::replicate() const
{
    return (new CtiLMGroupExpresscom(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMGroupExpresscom::restore(RWDBReader& rdr)
{
    CtiLMGroupBase::restore(rdr);
}

/*---------------------------------------------------------------------------
    restoreExpresscomSpecificDatabaseEntries
    
    Restores the database entries for a expresscom group that are not contained
    in the base table.
---------------------------------------------------------------------------*/
void CtiLMGroupExpresscom::restoreExpresscomSpecificDatabaseEntries(RWDBReader& rdr)
{
    rdr["routeid"] >> _routeid          ;
    rdr["serialnumber"] >> _serialnumber     ;
    rdr["serviceaddress"] >> _serviceaddress   ;
    rdr["geoaddress"] >> _geoaddress       ;
    rdr["substationaddress"] >> _substationaddress;
    rdr["feederaddress"] >> _feederaddress    ;
    rdr["zipcodeaddress"] >> _zipcodeaddress   ;
    rdr["udaddress"] >> _udaddress        ;
    rdr["programaddress"] >> _programaddress   ;
    rdr["splinteraddress"] >> _splinteraddress  ;
    rdr["addressusage"] >> _addressusage     ;
    rdr["relayusage"] >> _relayusage       ;

    _refreshsent = FALSE;
}

