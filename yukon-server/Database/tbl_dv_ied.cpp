#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_ied
*
* Date:   8/13/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_ied.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_dv_ied.h"
#include "logger.h"

CtiTableDeviceIED::CtiTableDeviceIED() :
_deviceID(-1),
_slaveAddress(0)
{
}

CtiTableDeviceIED::CtiTableDeviceIED(const CtiTableDeviceIED& aRef)
{
    *this = aRef;
}

CtiTableDeviceIED::~CtiTableDeviceIED() {}

CtiTableDeviceIED& CtiTableDeviceIED::operator=(const CtiTableDeviceIED& aRef)
{

    if(this != &aRef)
    {
        _deviceID      = aRef.getDeviceID();
        _password      = aRef.getPassword();
        _slaveAddress  = aRef.getSlaveAddress();
    }
    return *this;
}

INT CtiTableDeviceIED::getSlaveAddress() const
{

    return _slaveAddress;
}

INT& CtiTableDeviceIED::getSlaveAddress()
{

    return _slaveAddress;
}

CtiTableDeviceIED CtiTableDeviceIED::setSlaveAddress(INT &aInt)
{

    _slaveAddress = aInt;
    return *this;
}

RWCString CtiTableDeviceIED::getPassword() const
{

    return _password;
}

RWCString& CtiTableDeviceIED::getPassword()
{

    return _password;
}

CtiTableDeviceIED CtiTableDeviceIED::setPassword(RWCString &aStr)
{

    _password = aStr;
    return *this;
}

void CtiTableDeviceIED::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector <<
    devTbl["password"] <<
    devTbl["slaveaddress"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
    // selector.where( selector.where() && keyTable["deviceid"] == devTbl["deviceid"] );
}

void CtiTableDeviceIED::DecodeDatabaseReader(const INT DeviceType, RWDBReader &rdr)
{
    RWCString temp;



    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["password"]         >> temp;

    if(temp.isNull() ||
       !temp.compareTo("0") ||
       !temp.compareTo("none", RWCString::ignoreCase) ||
       !temp.compareTo("(none)", RWCString::ignoreCase))
    {
        _password = RWCString();
    }
    else
    {
        _password = temp;
    }
    rdr["slaveaddress"]     >> temp;

    _slaveAddress = resolveSlaveAddress(DeviceType, temp);
}

RWCString CtiTableDeviceIED::getTableName()
{
    return "DeviceIED";
}

LONG CtiTableDeviceIED::getDeviceID() const
{

    return _deviceID;
}

CtiTableDeviceIED& CtiTableDeviceIED::setDeviceID( const LONG deviceID )
{

    _deviceID = deviceID;
    return *this;
}

RWDBStatus CtiTableDeviceIED::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["password"] <<
    table["slaveaddress"];

    selector.where( table["deviceid"] == getDeviceID() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( getDeviceID(), reader  );
        setDirty( false );
    }
    else
    {
        setDirty( true );
    }
    return reader.status();
}

RWDBStatus CtiTableDeviceIED::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getPassword() <<
    getSlaveAddress();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceIED::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["password"].assign(getPassword() ) <<
    table["slaveaddress"].assign(getSlaveAddress() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceIED::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

