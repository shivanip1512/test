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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/11/23 15:27:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "logger.h"
#include "tbl_metergrp.h"

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
        _collectionGroup     = aRef.getCollectionGroup();
        _testCollectionGroup = aRef.getTestCollectionGroup();
        _meterNumber         = aRef.getMeterNumber();
        _billingGroup        = aRef.getBillingGroup();
    }
    return *this;
}

RWCString  CtiTableDeviceMeterGroup::getCollectionGroup() const
{

    return _collectionGroup;
}

CtiTableDeviceMeterGroup& CtiTableDeviceMeterGroup::setCollectionGroup( const RWCString &aCycleGroup )
{

    _collectionGroup = aCycleGroup;
    return *this;
}

RWCString CtiTableDeviceMeterGroup::getBillingGroup() const
{

    return _billingGroup;
}

CtiTableDeviceMeterGroup& CtiTableDeviceMeterGroup::setBillingGroup( const RWCString &billGroup )
{

    _billingGroup = billGroup;
    return *this;
}

RWCString CtiTableDeviceMeterGroup::getMeterNumber() const
{

    return _meterNumber;
}

CtiTableDeviceMeterGroup& CtiTableDeviceMeterGroup::setMeterNumber( const RWCString &meterNumber )
{

    _meterNumber = meterNumber;
    return *this;
}

RWCString  CtiTableDeviceMeterGroup::getTestCollectionGroup() const
{

    return _testCollectionGroup;
}

CtiTableDeviceMeterGroup& CtiTableDeviceMeterGroup::setTestCollectionGroup( const RWCString &aAreaCodeGroup )
{

    _testCollectionGroup = aAreaCodeGroup;
    return *this;
}

void CtiTableDeviceMeterGroup::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName());

    selector <<
    devTbl["collectiongroup"] <<
    devTbl["testcollectiongroup"] <<
    devTbl["meternumber"] <<
    devTbl["billinggroup"];

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
    rdr["collectiongroup"] >> _collectionGroup;
    rdr["testcollectiongroup"] >> _testCollectionGroup;
    rdr["meternumber"] >> _meterNumber;
    rdr["billinggroup"] >> _billingGroup;
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

RWCString CtiTableDeviceMeterGroup::getTableName()
{
    return "DeviceMeterGroup";
}

RWDBStatus CtiTableDeviceMeterGroup::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["collectiongroup"] <<
    table["testcollectiongroup"] <<
    table["meternumber"] <<
    table["billinggroup"];

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

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getCollectionGroup() <<
    getTestCollectionGroup() <<
    getMeterNumber() <<
    getBillingGroup();

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

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["collectiongroup"].assign(getCollectionGroup() ) <<
    table["testcollectiongroup"].assign(getTestCollectionGroup() ) <<
    table["meternumber"].assign(getMeterNumber() ) <<
    table["billinggroup"].assign(getBillingGroup() );

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

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}


