/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_idlcremote
*
* Date:   8/13/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_idlcremote.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/04/15 18:28:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_idlcremote.h"

CtiTableDeviceIDLC::CtiTableDeviceIDLC() :
_deviceID(-1),
_address(-1),
_postdelay(-1),
_ccuAmpUseType(RouteAmp1)
{}

CtiTableDeviceIDLC::CtiTableDeviceIDLC(const CtiTableDeviceIDLC& aRef)
{
    *this = aRef;
}

CtiTableDeviceIDLC::~CtiTableDeviceIDLC()
{
}

CtiTableDeviceIDLC& CtiTableDeviceIDLC::operator=(const CtiTableDeviceIDLC& aRef)
{
    if(this != &aRef)
    {
        _deviceID      = aRef.getDeviceID();
        _address       = aRef.getAddress();
        _postdelay     = aRef.getPostDelay();
        _ccuAmpUseType = aRef.getCCUAmpUseType();
    }
    return *this;
}

LONG CtiTableDeviceIDLC::getDeviceID() const
{
    return _deviceID;
}

CtiTableDeviceIDLC& CtiTableDeviceIDLC::setDeviceID( const LONG deviceID )
{
    _deviceID = deviceID;
    return *this;
}

LONG CtiTableDeviceIDLC::getAddress() const
{
    return _address;
}

void CtiTableDeviceIDLC::setAddress(LONG a)
{
    _address = a;
}

INT CtiTableDeviceIDLC::getCCUAmpUseType() const
{
    return _ccuAmpUseType;
}

CtiTableDeviceIDLC& CtiTableDeviceIDLC::setCCUAmpUseType( const INT aAmpUseType )
{
    _ccuAmpUseType = aAmpUseType;

    return *this;
}

INT  CtiTableDeviceIDLC::getPostDelay() const
{
    return _postdelay;
}

void CtiTableDeviceIDLC::setPostDelay(int d)
{
    _postdelay = d;
}

INT CtiTableDeviceIDLC::getAmp() const
{
    INT amp;

    switch(_ccuAmpUseType)
    {
        case RouteAmpUndefined:
        case RouteAmp2:
        case RouteAmpDefault2Fail1:
        {
            amp = 1;
            break;
        }

        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** ACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        case RouteAmpAlternating:
        case RouteAmpAltFail:
        case RouteAmp1:
        case RouteAmpDefault1Fail2:
        {
            amp = 0;
            break;
        }
    }

    return amp;
}

void CtiTableDeviceIDLC::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector <<
    devTbl["address"] <<
    devTbl["postcommwait"] <<
    devTbl["ccuampusetype"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
    // selector.where( selector.where() && keyTable["deviceid"] == devTbl["deviceid"] );
}

void CtiTableDeviceIDLC::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["address"]       >> _address;
    rdr["postcommwait"]  >> _postdelay;
    rdr["ccuampusetype"] >> rwsTemp;

    _ccuAmpUseType = resolveAmpUseType(rwsTemp);
}

RWCString CtiTableDeviceIDLC::getTableName()
{
    return "DeviceIDLCRemote";
}

RWDBStatus CtiTableDeviceIDLC::Restore()
{
    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["address"] <<
    table["postcommwait"] <<
    table["ccuampusetype"];

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

RWDBStatus CtiTableDeviceIDLC::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getAddress() <<
    getPostDelay() <<
    desolveAmpUseType(getCCUAmpUseType() );

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceIDLC::Update()
{
    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["address"].assign(getAddress() ) <<
    table["postcommwait"].assign(getPostDelay() ) <<
    table["ccuampusetype"].assign(desolveAmpUseType(getCCUAmpUseType() ));
//      table["ampusetype"].assign( desolveAmpUseType( getAmpUseType() )) <<
    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceIDLC::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}


