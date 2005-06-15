/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_lmvcserial
*
* Date:   8/13/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_lmvcserial.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/06/15 23:56:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_lmvcserial.h"
#include "logger.h"

CtiTableLMGroupVersacomSerial::CtiTableLMGroupVersacomSerial() :
_deviceID(-1),
_serial(0),
_groupID(0),
_addressUsage(0),
_relayMask(0),
_routeID(-1)
{}

CtiTableLMGroupVersacomSerial::CtiTableLMGroupVersacomSerial(const CtiTableLMGroupVersacomSerial& aRef)
{
    *this = aRef;
}

CtiTableLMGroupVersacomSerial::~CtiTableLMGroupVersacomSerial() {}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::operator=(const CtiTableLMGroupVersacomSerial& aRef)
{


    if(this != &aRef)
    {
        _deviceID = aRef.getDeviceID();
        _serial = aRef.getSerial();
        _groupID = aRef.getGroupID();
        _addressUsage = aRef.getAddressUsage();
        _relayMask = aRef.getRelayMask();
        _routeID = aRef.getRouteID();
    }
    return *this;
}

INT  CtiTableLMGroupVersacomSerial::getSerial() const
{

    return _serial;
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setSerial( const INT a_ser )
{

    _serial = a_ser;
    return *this;
}

INT  CtiTableLMGroupVersacomSerial::getGroupID() const
{

    return _groupID;
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setGroupID( const INT i )
{

    _groupID = i;
    return *this;
}

INT  CtiTableLMGroupVersacomSerial::getAddressUsage() const
{

    return _addressUsage;
}

BOOL CtiTableLMGroupVersacomSerial::useUtilityID() const
{

    return((_addressUsage & U_MASK) ? TRUE : FALSE );
}

BOOL CtiTableLMGroupVersacomSerial::useSection() const
{

    return((_addressUsage & S_MASK) ? TRUE : FALSE );
}

BOOL CtiTableLMGroupVersacomSerial::useClass() const
{

    return((_addressUsage & C_MASK) ? TRUE : FALSE );
}

BOOL CtiTableLMGroupVersacomSerial::useDivision() const
{

    return((_addressUsage & D_MASK) ? TRUE : FALSE );
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setAddressUsage( const INT a_addressUsage )
{

    _addressUsage = a_addressUsage;
    return *this;
}

INT  CtiTableLMGroupVersacomSerial::getRelayMask() const
{

    return _relayMask;
}

BOOL CtiTableLMGroupVersacomSerial::useRelay(const INT r) const
{

    return((_relayMask & (0x00000001 << (r - 1))) ? TRUE : FALSE);
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setRelayMask( const INT a_relayMask )
{

    _relayMask = a_relayMask;
    return *this;
}

LONG  CtiTableLMGroupVersacomSerial::getRouteID() const
{

    return _routeID;
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setRouteID( const LONG a_routeID )
{

    _routeID = a_routeID;
    return *this;
}

void CtiTableLMGroupVersacomSerial::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector <<
    devTbl["serialnumber"] <<
    devTbl["deviceidofgroup"] <<
    devTbl["relayusage"] <<
    devTbl["routeid"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
}

RWCString CtiTableLMGroupVersacomSerial::getTableName()
{
    return "LMGroupVersacomSerial";
}

LONG CtiTableLMGroupVersacomSerial::getDeviceID() const
{

    return _deviceID;
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setDeviceID( const LONG deviceID)
{

    _deviceID = deviceID;
    return *this;
}

void CtiTableLMGroupVersacomSerial::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;


    if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"]         >> _deviceID;
    rdr["serialnumber"]     >> _serial;
    rdr["deviceidofgroup"]  >> _groupID;
    rdr["routeid"]          >> _routeID;

    rdr["relayusage"]       >> rwsTemp;
    _relayMask = resolveRelayUsage(rwsTemp);

    _addressUsage = 0;                     // This is a serial number ok!

}

RWDBStatus CtiTableLMGroupVersacomSerial::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["serialnumber"] <<
    table["deviceidofgroup"] <<
    table["routeid"];

    selector.where( table["deviceid"] == getDeviceID() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader  );
        setDirty( false );
    }
    else
    {
        setDirty( true );
    }
    return reader.status();
}

RWDBStatus CtiTableLMGroupVersacomSerial::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getSerial() <<
    getGroupID() <<
    getRouteID();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableLMGroupVersacomSerial::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["serialnumber"].assign(getSerial() ) <<
    table["deviceidofgroup"].assign(getGroupID() ) <<
    table["routeid"].assign(getRouteID() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableLMGroupVersacomSerial::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

