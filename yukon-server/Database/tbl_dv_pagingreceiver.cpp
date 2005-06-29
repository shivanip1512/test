
/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_pagingreceiver
*
* Date:   8/14/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_pagingreceiver.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/06/29 19:49:49 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_pagingreceiver.h"
#include "logger.h"

CtiTableDevicePagingReceiver::CtiTableDevicePagingReceiver() :
_frequency(0),
_capcode1(0), _capcode2(0),
_capcode3(0), _capcode4(0), _capcode5(0),
_capcode6(0), _capcode7(0), _capcode8(0), _capcode9(0),
_capcode10(0), _capcode11(0), _capcode12(0), _capcode13(0),
_capcode14(0), _capcode15(0), _capcode16(0)
{}

CtiTableDevicePagingReceiver::CtiTableDevicePagingReceiver(const CtiTableDevicePagingReceiver& aRef)
{
    *this = aRef;
}

CtiTableDevicePagingReceiver::~CtiTableDevicePagingReceiver()
{}

CtiTableDevicePagingReceiver& CtiTableDevicePagingReceiver::operator=(const CtiTableDevicePagingReceiver& aRef)
{


    if(this != &aRef)
    {
        _frequency    = aRef.getFrequency();
        _capcode1 = aRef.getCapcode(1);
        _capcode2 = aRef.getCapcode(2);
        _capcode3 = aRef.getCapcode(3);
        _capcode4 = aRef.getCapcode(4);
        _capcode5 = aRef.getCapcode(5);
        _capcode6 = aRef.getCapcode(6);
        _capcode7 = aRef.getCapcode(7);
        _capcode8 = aRef.getCapcode(8);
        _capcode9 = aRef.getCapcode(9);
        _capcode10 = aRef.getCapcode(10);
        _capcode11 = aRef.getCapcode(11);
        _capcode12 = aRef.getCapcode(12);
        _capcode13 = aRef.getCapcode(13);
        _capcode14 = aRef.getCapcode(14);
        _capcode15 = aRef.getCapcode(15);
        _capcode16 = aRef.getCapcode(16);
    }
    return *this;
}

float CtiTableDevicePagingReceiver::getFrequency() const
{

    return _frequency;
}

float CtiTableDevicePagingReceiver::getCapcode(int codeNumber) const
{
    switch(codeNumber)
    {
        case 1:
            return _capcode1;
        case 2:
            return _capcode2;
        case 3:
            return _capcode3;
        case 4:
            return _capcode4;
        case 5:
            return _capcode5;
        case 6:
            return _capcode6;
        case 7:
            return _capcode7;
        case 8:
            return _capcode8;
        case 9:
            return _capcode9;
        case 10:
            return _capcode10;
        case 11:
            return _capcode11;
        case 12:
            return _capcode12;
        case 13:
            return _capcode13;
        case 14:
            return _capcode14;
        case 15:
            return _capcode15;
        case 16:
            return _capcode16;
        default:
            return -1;
    }
}


void CtiTableDevicePagingReceiver::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector << devTbl ["deviceid"] << devTbl["frequency"] << devTbl["capcode1"] << devTbl["capcode2"] << devTbl["capcode3"]
        << devTbl["capcode4"] << devTbl["capcode5"] << devTbl["capcode6"]
        << devTbl["capcode7"] << devTbl["capcode8"] << devTbl["capcode9"]
        << devTbl["capcode10"] << devTbl["capcode11"] << devTbl["capcode12"]
        << devTbl["capcode13"] << devTbl["capcode14"] << devTbl["capcode15"]
        << devTbl["capcode16"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
}

void CtiTableDevicePagingReceiver::DecodeDatabaseReader(RWDBReader &rdr)
{


    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["frequency"] >> _frequency;
    rdr["capcode1"] >> _capcode1;
    rdr["capcode2"] >> _capcode2;
    rdr["capcode3"] >> _capcode3;
    rdr["capcode4"] >> _capcode4;
    rdr["capcode5"] >> _capcode5;
    rdr["capcode6"] >> _capcode6;
    rdr["capcode7"] >> _capcode7;
    rdr["capcode8"] >> _capcode8;
    rdr["capcode9"] >> _capcode9;
    rdr["capcode10"] >> _capcode10;
    rdr["capcode11"] >> _capcode11;
    rdr["capcode12"] >> _capcode12;
    rdr["capcode13"] >> _capcode13;
    rdr["capcode14"] >> _capcode14;
    rdr["capcode15"] >> _capcode15;
    rdr["capcode16"] >> _capcode16;
}

RWCString CtiTableDevicePagingReceiver::getTableName()
{
    return "DevicePagingReceiverSettings";
}

LONG CtiTableDevicePagingReceiver::getDeviceID() const
{
    return _deviceID;
}


RWDBStatus CtiTableDevicePagingReceiver::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();
/*
    selector << devTbl["deviceid"] << devTbl["frequency"] << devTbl["capcode1"] << devTbl["capcode2"] << devTbl["capcode3"]
        << devTbl["capcode4"] << devTbl["capcode5"] << devTbl["capcode6"]
        << devTbl["capcode7"] << devTbl["capcode8"] << devTbl["capcode9"]
        << devTbl["capcode10"] << devTbl["capcode11"] << devTbl["capcode12"]
        << devTbl["capcode13"] << devTbl["capcode14"] << devTbl["capcode15"]
        << devTbl["capcode16"];
*/
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

RWDBStatus CtiTableDevicePagingReceiver::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

  //  inserter <<
  //  getDeviceID() <<
  //  getPagerNumber();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDevicePagingReceiver::Update()
{
    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

   // updater <<
   // table["pagernumber"].assign(getPagerNumber() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDevicePagingReceiver::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}



