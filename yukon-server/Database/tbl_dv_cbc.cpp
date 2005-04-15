/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_cbc
*
* Date:   8/7/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/04/15 18:28:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_cbc.h"
#include "logger.h"

CtiTableDeviceCBC::CtiTableDeviceCBC() :
_deviceID(-1),
_serial(0),
_routeID(-1)
{}

CtiTableDeviceCBC::CtiTableDeviceCBC(const CtiTableDeviceCBC& aRef)
{
    *this = aRef;
}

CtiTableDeviceCBC::~CtiTableDeviceCBC()
{}

CtiTableDeviceCBC& CtiTableDeviceCBC::operator=(const CtiTableDeviceCBC& aRef)
{
    if(this != &aRef)
    {
        _deviceID = aRef.getDeviceID();
        _serial = aRef.getSerial();
        _routeID = aRef.getRouteID();
    }
    return *this;
}

INT  CtiTableDeviceCBC::getSerial() const
{

    return _serial;
}

CtiTableDeviceCBC& CtiTableDeviceCBC::setSerial( const INT a_ser )
{

    _serial = a_ser;
    return *this;
}

LONG  CtiTableDeviceCBC::getRouteID() const
{

    return _routeID;
}

CtiTableDeviceCBC& CtiTableDeviceCBC::setRouteID( const LONG a_routeID )
{

    _routeID = a_routeID;
    return *this;
}

void CtiTableDeviceCBC::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table( getTableName() );

    selector <<
    devTbl["serialnumber"] <<
    devTbl["routeid"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
    // selector.where( selector.where() && keyTable["deviceid"] == devTbl["deviceid"] );
}

void CtiTableDeviceCBC::DecodeDatabaseReader(RWDBReader &rdr)
{


    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["serialnumber"]     >> _serial;
    rdr["routeid"]          >> _routeID;
}

RWDBStatus CtiTableDeviceCBC::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["serialnumber"] <<
    table["routeid"];

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

RWDBStatus CtiTableDeviceCBC::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getSerial() <<
    getRouteID();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceCBC::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["serialnumber"].assign(getSerial() ) <<
    table["routeid"].assign(getRouteID() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceCBC::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

LONG CtiTableDeviceCBC::getDeviceID() const
{

    return _deviceID;
}

RWCString CtiTableDeviceCBC::getTableName()
{
    return "DeviceCBC";
}

CtiTableDeviceCBC& CtiTableDeviceCBC::setDeviceID( const LONG deviceID)
{

    _deviceID = deviceID;
    return *this;
}

