#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_versacom
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_versacom.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>

#include "resolvers.h"
#include "tbl_dv_versacom.h"
#include "logger.h"

CtiTableVersacomLoadGroup::CtiTableVersacomLoadGroup() :
_deviceID(-1),
_serial(0),
_utilityID(0),
_section(0),
_class(0),
_division(0),
_addressUsage(0),
_relayMask(0),
_routeID(-1)
{}

CtiTableVersacomLoadGroup::CtiTableVersacomLoadGroup(const CtiTableVersacomLoadGroup& aRef)
{
    *this = aRef;
}

CtiTableVersacomLoadGroup::~CtiTableVersacomLoadGroup() {}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::operator=(const CtiTableVersacomLoadGroup& aRef)
{
    if(this != &aRef)
    {
        _deviceID      = aRef.getDeviceID();
        _serial        = aRef.getSerial();
        _utilityID     = aRef.getUtilityID();
        _section       = aRef.getSection();
        _class         = aRef.getClass();
        _division      = aRef.getDivision();
        _addressUsage  = aRef.getAddressUsage();
        _relayMask     = aRef.getRelayMask();
        _routeID       = aRef.getRouteID();
    }
    return *this;
}

INT  CtiTableVersacomLoadGroup::getSerial() const
{

    return _serial;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setSerial( const INT a_ser )
{

    _serial = a_ser;
    return *this;
}

INT  CtiTableVersacomLoadGroup::getUtilityID() const
{

    return _utilityID;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setUtilityID( const INT a_uid )
{

    _utilityID = a_uid;
    return *this;
}

INT  CtiTableVersacomLoadGroup::getSection() const
{

    return _section;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setSection( const INT aSection )
{

    _section = aSection;
    return *this;
}

INT  CtiTableVersacomLoadGroup::getClass() const
{

    return _class;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setClass( const INT aClass )
{

    _class = aClass;
    return *this;
}

INT  CtiTableVersacomLoadGroup::getDivision() const
{

    return _division;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setDivision( const INT aDivision )
{

    _division = aDivision;
    return *this;
}


INT  CtiTableVersacomLoadGroup::getAddressUsage() const
{

    return _addressUsage;
}

BOOL CtiTableVersacomLoadGroup::useUtilityID() const
{

    return((_addressUsage & U_MASK) ? TRUE : FALSE );
}

BOOL CtiTableVersacomLoadGroup::useSection() const
{

    return((_addressUsage & S_MASK) ? TRUE : FALSE );
}

BOOL CtiTableVersacomLoadGroup::useClass() const
{

    return((_addressUsage & C_MASK) ? TRUE : FALSE );
}

BOOL CtiTableVersacomLoadGroup::useDivision() const
{

    return((_addressUsage & D_MASK) ? TRUE : FALSE );
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setAddressUsage( const INT a_addressUsage )
{

    _addressUsage = a_addressUsage;
    return *this;
}

INT  CtiTableVersacomLoadGroup::getRelayMask() const
{

    return _relayMask;
}

BOOL CtiTableVersacomLoadGroup::useRelay(const INT r) const
{

    return((_relayMask & (0x00000001 << (r - 1))) ? TRUE : FALSE);
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setRelayMask( const INT a_relayMask )
{

    _relayMask = a_relayMask;
    return *this;
}

LONG  CtiTableVersacomLoadGroup::getRouteID() const
{

    return _routeID;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setRouteID( const LONG a_routeID )
{

    _routeID = a_routeID;
    return *this;
}

void CtiTableVersacomLoadGroup::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector <<
    devTbl["serialaddress"] <<
    devTbl["utilityaddress"] <<
    devTbl["sectionaddress"] <<
    devTbl["classaddress"] <<
    devTbl["divisionaddress"] <<
    devTbl["addressusage"] <<
    devTbl["relayusage"] <<
    devTbl["routeid"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
}

void CtiTableVersacomLoadGroup::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["paobjectid"]       >> _deviceID;

    rdr["serialaddress"]    >> rwsTemp;
    _serial = atoi(rwsTemp.data());
    rdr["utilityaddress"]   >> _utilityID;
    rdr["sectionaddress"]   >> _section;
    rdr["classaddress"]     >> _class;
    rdr["divisionaddress"]  >> _division;
    rdr["routeid"]          >> _routeID;

    rdr["addressusage"]     >> rwsTemp;
    rwsTemp.toLower();
    _addressUsage = resolveAddressUsage(rwsTemp, versacomAddressUsage);

    rdr["relayusage"]       >> rwsTemp;
    _relayMask = resolveRelayUsage(rwsTemp);
}

LONG CtiTableVersacomLoadGroup::getDeviceID() const
{
    return _deviceID;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setDeviceID( const LONG deviceID )
{
    _deviceID = deviceID;
    return *this;
}

RWDBStatus CtiTableVersacomLoadGroup::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["routeid"] <<
    table["serialaddress"] <<
    table["utilityaddress"] <<
    table["sectionaddress"] <<
    table["classaddress"] <<
    table["divisionaddress"] <<
    table["addressusage"] <<
    table["relayusage"];

    selector.where( table["deviceid"] == getDeviceID() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
        setDirty( false );
    }
    else
    {
        setDirty( true );
    }
    return reader.status();
}

RWDBStatus CtiTableVersacomLoadGroup::Insert()
{
#if 0


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID()     <<
    getRouteID() <<
    getUtilityID() <<
    getSection() <<
    getClass() <<
    getDivision() <<
    getAddressUsage() <<
    getRelayMask();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
#else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
#endif
}

RWDBStatus CtiTableVersacomLoadGroup::Update()
{
#if 0
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["routeid"].assign(getRouteID() ) <<
    table["utilityaddress"].assign(getUtilityID() ) <<
    table["sectionaddress"].assign(getSection() ) <<
    table["classaddress"].assign(getClass() ) <<
    table["divisionaddress"].assign(getDivision() ) <<
    table["addressusage"].assign(getAddressUsage() ) <<
    table["relayusage"].assign(getRelayMask() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
#else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
#endif
}

RWDBStatus CtiTableVersacomLoadGroup::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

RWCString CtiTableVersacomLoadGroup::getTableName()
{
    return "LMGroupVersacom";
}
