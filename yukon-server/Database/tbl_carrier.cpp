/*-----------------------------------------------------------------------------*
*
* File:   tbl_carrier
*
* Date:   8/7/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_carrier.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/04/15 18:28:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_carrier.h"
#include "logger.h"

CtiTableDeviceCarrier::CtiTableDeviceCarrier() :
_deviceID(-1),
_address(-1)
{
}

CtiTableDeviceCarrier::CtiTableDeviceCarrier(const CtiTableDeviceCarrier& aRef)
{
    *this = aRef;
}

CtiTableDeviceCarrier::~CtiTableDeviceCarrier()
{
}

CtiTableDeviceCarrier& CtiTableDeviceCarrier::operator=(const CtiTableDeviceCarrier& aRef)
{
    if(this != &aRef)
    {
        _deviceID = aRef.getDeviceID();
        _address = aRef.getAddress();
    }
    return *this;
}

INT CtiTableDeviceCarrier::getAddress() const
{
    return _address;
}

CtiTableDeviceCarrier& CtiTableDeviceCarrier::setAddress( const INT aAddress )
{
    _address = aAddress;
    return *this;
}

RWCString CtiTableDeviceCarrier::getTableName()
{
    return "DeviceCarrierSettings";
}

void CtiTableDeviceCarrier::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table( getTableName() );

    selector << devTbl["address"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
}

void CtiTableDeviceCarrier::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    RWDBNullIndicator isNull;
    RWCString   rwsTemp;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["paobjectid"] >> _deviceID;
    rdr["address"] >> _address;
}

LONG CtiTableDeviceCarrier::getDeviceID() const
{

    return _deviceID;
}

CtiTableDeviceCarrier& CtiTableDeviceCarrier::setDeviceID( const LONG deviceID )
{

    _deviceID = deviceID;
    return *this;
}

RWDBStatus CtiTableDeviceCarrier::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["address"];

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

RWDBStatus CtiTableDeviceCarrier::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getAddress();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceCarrier::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater << table["address"].assign(getAddress() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceCarrier::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}


