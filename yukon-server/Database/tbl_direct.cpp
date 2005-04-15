/*-----------------------------------------------------------------------------*
*
* File:   tbl_direct
*
* Date:   8/7/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_direct.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/04/15 18:28:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_direct.h"
#include "logger.h"

CtiTableDeviceDirectComm::CtiTableDeviceDirectComm() :
_deviceID(-1),
PortID(-1)
{}

CtiTableDeviceDirectComm::CtiTableDeviceDirectComm(const CtiTableDeviceDirectComm &aRef)
{
    *this = aRef;
}

CtiTableDeviceDirectComm::~CtiTableDeviceDirectComm()
{
}

CtiTableDeviceDirectComm& CtiTableDeviceDirectComm::operator=(const CtiTableDeviceDirectComm &aRef)
{
    RWMutexLock::LockGuard  guard(DirectCommMux);
    if(this != &aRef)
    {
        _deviceID   = aRef.getDeviceID();
        PortID      = aRef.getPortID();
    }

    return *this;
}

LONG CtiTableDeviceDirectComm::getPortID() const
{

    return PortID;
}

void CtiTableDeviceDirectComm::setPortID(LONG id)
{

    PortID = id;
}

void CtiTableDeviceDirectComm::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table( getTableName() );

    selector << devTbl["portid"];

    selector.from(devTbl);

    selector.where( selector.where() &&
                    keyTable["paobjectid"] == devTbl["deviceid"] );
}

void CtiTableDeviceDirectComm::DecodeDatabaseReader(RWDBReader &rdr)
{


    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["portid"] >> PortID;
}

RWDBStatus CtiTableDeviceDirectComm::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["portid"];

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

RWDBStatus CtiTableDeviceDirectComm::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getPortID();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceDirectComm::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["portid"].assign(getPortID());

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceDirectComm::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

LONG CtiTableDeviceDirectComm::getDeviceID() const
{

    return _deviceID;
}

RWCString CtiTableDeviceDirectComm::getTableName()
{
    return "DeviceDirectCommSettings";
}

CtiTableDeviceDirectComm& CtiTableDeviceDirectComm::setDeviceID( const LONG deviceID)
{

    _deviceID = deviceID;
    return *this;
}
