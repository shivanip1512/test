/*-----------------------------------------------------------------------------*
*
* File:   tbl_scanrate
*
* Date:   8/16/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_scanrate.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/04/15 18:28:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_scanrate.h"

CtiTableDeviceScanRate::CtiTableDeviceScanRate() :
_deviceID(-1),
_scanGroup(0),
_scanRate(-1L),
_scanType(0),
_alternateRate(-1L),
_updated(FALSE)
{}

CtiTableDeviceScanRate::CtiTableDeviceScanRate(const CtiTableDeviceScanRate &aRef)
{
    *this = aRef;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::operator=(const CtiTableDeviceScanRate &aRef)
{
    if(this != &aRef)
    {
        _deviceID      = aRef.getDeviceID();
        _scanRate      = aRef.getScanRate();
        _scanGroup     = aRef.getScanGroup();
        _scanType      = aRef.getScanType();
        _alternateRate = aRef.getAlternateRate();

        _updated = FALSE;
    }

    return *this;
}

LONG CtiTableDeviceScanRate::getScanType() const
{

    return _scanType;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::setScanType( const LONG aScanType )
{

    _scanType = aScanType;
    return *this;
}

INT CtiTableDeviceScanRate::getScanGroup() const
{

    return _scanGroup;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::setScanGroup( const INT aInt )
{

    _scanGroup = aInt;
    return *this;
}

INT CtiTableDeviceScanRate::getAlternateRate() const
{

    return _alternateRate;
}

LONG CtiTableDeviceScanRate::getScanRate() const
{

    return _scanRate;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::setScanRate( const LONG rate )
{

    _scanRate = rate;
    return *this;
}

BOOL CtiTableDeviceScanRate::getUpdated() const
{

    return _updated;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::setUpdated( const BOOL aBool )
{

    _updated = aBool;
    return *this;
}

void CtiTableDeviceScanRate::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table("Device");
    RWDBTable devTbl = db.table(getTableName());

    selector <<
    keyTable["deviceid"] <<
    devTbl["scantype"] <<
    devTbl["intervalrate"] <<
    devTbl["scangroup"] <<
    devTbl["alternaterate"];

    selector.from(keyTable);
    selector.from(devTbl);

    selector.where( selector.where() && keyTable["deviceid"] == devTbl["deviceid"] );
}

void CtiTableDeviceScanRate::DecodeDatabaseReader(RWDBReader &rdr)
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    RWCString rwstemp;

    rdr["scantype"] >> rwstemp;

    _scanType = resolveScanType( rwstemp ); //??????
        rdr["intervalrate"] >> _scanRate;
        rdr["scangroup"] >> _scanGroup;
        rdr["alternaterate"] >> _alternateRate;

        _updated = TRUE;                    // _ONLY_ _ONLY_ place this is set.
    }

void CtiTableDeviceScanRate::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);

    dout << " Scan Type                                   : " << desolveScanType( _scanType) << endl;
    dout << " Scan Rate                                   : " << _scanRate  << endl;
    dout << " Scan Group                                  : " << _scanGroup << endl;
    dout << " Alternate Rate                              : " << _alternateRate << endl;
}

LONG CtiTableDeviceScanRate::getDeviceID() const
{

    return _deviceID;
}

CtiTableDeviceScanRate& CtiTableDeviceScanRate::setDeviceID( const LONG deviceID )
{

    _deviceID = deviceID;
    return *this;
}

RWCString CtiTableDeviceScanRate::getTableName()
{
    return "DeviceScanRate";
}

RWDBStatus CtiTableDeviceScanRate::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["scantype"] <<
    table["intervalrate"] <<
    table["scangroup"] <<
    table["alternaterate"];

    selector.where( (table["deviceid"] == getDeviceID() ) && (table["scantype"] == desolveScanType(getScanType())) );

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

RWDBStatus CtiTableDeviceScanRate::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    desolveScanType(getScanType() ) <<
    getScanRate() <<
    getScanGroup() <<
    getAlternateRate();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceScanRate::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["scantype"].assign(desolveScanType(getScanType() ) ) <<
    table["intervalrate"].assign(getScanRate() ) <<
    table["scangroup"].assign(getScanGroup() ) <<
    table["alternaterate"].assign(getAlternateRate() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceScanRate::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() && (table["scantype"] == desolveScanType(getScanType())));
    deleter.execute( conn );
    return deleter.status();
}
