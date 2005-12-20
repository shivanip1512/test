/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_tappaging
*
* Date:   8/14/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_tappaging.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_tappaging.h"
#include "logger.h"

#include "rwutil.h"

CtiTableDeviceTapPaging::CtiTableDeviceTapPaging(string pn) :
_deviceID(-1),
_pagerNumber(pn)
{}

CtiTableDeviceTapPaging::CtiTableDeviceTapPaging(const CtiTableDeviceTapPaging& aRef)
{
    *this = aRef;
}

CtiTableDeviceTapPaging::~CtiTableDeviceTapPaging()
{}

CtiTableDeviceTapPaging& CtiTableDeviceTapPaging::operator=(const CtiTableDeviceTapPaging& aRef)
{


    if(this != &aRef)
    {
        _deviceID    = aRef.getDeviceID();
        _pagerNumber = aRef.getPagerNumber();
        _senderID = aRef.getSenderID();
        _securityCode = aRef.getSecurityCode();
        _postPath = aRef.getPOSTPath();
    }
    return *this;
}

string CtiTableDeviceTapPaging::getPagerNumber() const
{

    return _pagerNumber;
}

string& CtiTableDeviceTapPaging::getPagerNumber()
{

    return _pagerNumber;
}

CtiTableDeviceTapPaging&   CtiTableDeviceTapPaging::setPagerNumber(const string &aStr)
{


    _pagerNumber = aStr;
    return *this;
}

void CtiTableDeviceTapPaging::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName().c_str() );

    selector << devTbl["pagernumber"]
        << devTbl["sender"]
        << devTbl["securitycode"]
        << devTbl["postpath"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
}

void CtiTableDeviceTapPaging::DecodeDatabaseReader(RWDBReader &rdr)
{

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["pagernumber"] >> _pagerNumber;
    rdr["sender"] >> _senderID;
    rdr["securitycode"] >> _securityCode;
    rdr["postpath"] >> _postPath;
    if( _securityCode.find("none")!=string::npos || _securityCode.compare("0")==string::npos )
    {
        _securityCode = string();    // Make it NULL.
    }
}

string CtiTableDeviceTapPaging::getTableName()
{
    return "DeviceTapPagingSettings";
}

LONG CtiTableDeviceTapPaging::getDeviceID() const
{

    return _deviceID;
}

CtiTableDeviceTapPaging& CtiTableDeviceTapPaging::setDeviceID( const LONG deviceID )
{

    _deviceID = deviceID;
    return *this;
}

RWDBStatus CtiTableDeviceTapPaging::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["pagernumber"];

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

RWDBStatus CtiTableDeviceTapPaging::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getPagerNumber();

    if( ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceTapPaging::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["pagernumber"].assign(getPagerNumber().c_str() );

    if( ExecuteUpdater(conn,updater,__FILE__,__LINE__) == RWDBStatus::ok )
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceTapPaging::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}


string CtiTableDeviceTapPaging::getSenderID() const
{
    return _senderID;
}

string CtiTableDeviceTapPaging::getSecurityCode() const
{
    return _securityCode;
}
string CtiTableDeviceTapPaging::getPOSTPath() const
{
    return _postPath;
}


