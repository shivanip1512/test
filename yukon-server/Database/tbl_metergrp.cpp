/*-----------------------------------------------------------------------------*
*
* File:   tbl_metergrp
*
* Date:   8/14/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_metergrp.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2007/07/09 21:50:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "logger.h"
#include "tbl_metergrp.h"

#include "rwutil.h"

CtiTableDeviceMeterGroup::CtiTableDeviceMeterGroup():
_deviceID(-1)
{}

CtiTableDeviceMeterGroup::CtiTableDeviceMeterGroup(const CtiTableDeviceMeterGroup& aRef)
{
    *this = aRef;
}

CtiTableDeviceMeterGroup::~CtiTableDeviceMeterGroup() {}

CtiTableDeviceMeterGroup& CtiTableDeviceMeterGroup::operator=(const CtiTableDeviceMeterGroup& aRef)
{
    if(this != &aRef)
    {
        _deviceID            = aRef.getDeviceID();
        _meterNumber         = aRef.getMeterNumber();
    }
    return *this;
}

string CtiTableDeviceMeterGroup::getMeterNumber() const
{
    return _meterNumber;
}

CtiTableDeviceMeterGroup& CtiTableDeviceMeterGroup::setMeterNumber( const string &meterNumber )
{
    _meterNumber = meterNumber;

    return *this;
}

void CtiTableDeviceMeterGroup::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName().c_str());

    selector <<
    devTbl["meternumber"];

    selector.from(devTbl);
    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
}

void CtiTableDeviceMeterGroup::DecodeDatabaseReader(RWDBReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["meternumber"] >> _meterNumber;
}

LONG CtiTableDeviceMeterGroup::getDeviceID() const
{
    return _deviceID;
}

CtiTableDeviceMeterGroup& CtiTableDeviceMeterGroup::setDeviceID( const LONG deviceID )
{
    _deviceID = deviceID;

    return *this;
}

string CtiTableDeviceMeterGroup::getTableName()
{
    return "DeviceMeterGroup";
}

RWDBStatus CtiTableDeviceMeterGroup::Restore()
{
    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["meternumber"];

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

RWDBStatus CtiTableDeviceMeterGroup::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getMeterNumber();

    if( ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceMeterGroup::Update()
{
    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater << table["meternumber"].assign(getMeterNumber().c_str() );

    if( ExecuteUpdater(conn,updater,__FILE__,__LINE__) == RWDBStatus::ok )
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceMeterGroup::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}


