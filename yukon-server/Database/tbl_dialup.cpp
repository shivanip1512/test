
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dialup
*
* Date:   8/7/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dialup.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_dialup.h"
#include "logger.h"

CtiTableDeviceDialup::CtiTableDeviceDialup() :
   _deviceID(-1),
   MinConnectTime(0),
   MaxConnectTime(INT_MAX),
   BaudRate(0)
{}

CtiTableDeviceDialup::CtiTableDeviceDialup(const CtiTableDeviceDialup &aRef)
{
   *this = aRef;
}

CtiTableDeviceDialup& CtiTableDeviceDialup::operator=(const CtiTableDeviceDialup &aRef)
{

   if(this != &aRef)
   {
      _deviceID      = aRef.getDeviceID();
      PhoneNumber    = aRef.getPhoneNumber();
      MinConnectTime = aRef.getMinConnectTime();
      MaxConnectTime = aRef.getMaxConnectTime();
      LineSettings   = aRef.getLineSettings();
      BaudRate       = aRef.getBaudRate();
   }

   return *this;
}

INT  CtiTableDeviceDialup::getMinConnectTime() const
{

   return MinConnectTime;
}

void CtiTableDeviceDialup::setMinConnectTime(INT  i)
{

   MinConnectTime = i;
}

INT CtiTableDeviceDialup::getMaxConnectTime() const
{

   return MaxConnectTime;
}

void CtiTableDeviceDialup::setMaxConnectTime(INT  i)
{

   MaxConnectTime = i;
}

INT CtiTableDeviceDialup::getBaudRate() const
{

   return BaudRate;
}

CtiTableDeviceDialup& CtiTableDeviceDialup::setBaudRate(INT baud)
{

   BaudRate = baud;
   return *this;
}

RWCString CtiTableDeviceDialup::getPhoneNumber() const
{

   return PhoneNumber;
}

void CtiTableDeviceDialup::setPhoneNumber(const RWCString &str)
{

   PhoneNumber = str;
}

RWCString CtiTableDeviceDialup::getLineSettings() const
{

   return LineSettings;
}

void CtiTableDeviceDialup::setLineSettings(const RWCString &lstr)
{

   LineSettings = lstr;
}

void CtiTableDeviceDialup::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   RWDBTable devTbl = db.table( getTableName() );

   selector <<
      devTbl["phonenumber"] <<
      devTbl["minconnecttime"] <<
      devTbl["maxconnecttime"] <<
      devTbl["linesettings"] <<
      devTbl["baudrate"];

   selector.from(devTbl);

   // selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
   selector.where( keyTable["paobjectid"].leftOuterJoin(devTbl["deviceid"]) && selector.where() );
}

void CtiTableDeviceDialup::DecodeDatabaseReader(RWDBReader &rdr)
{


   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   rdr["deviceid"] >> _deviceID;
   rdr["phonenumber"] >> PhoneNumber;
   rdr["minconnecttime"] >> MinConnectTime;
   rdr["maxconnecttime"] >> MaxConnectTime;
   rdr["linesettings"] >> LineSettings;
   rdr["baudrate"] >> BaudRate;
}

RWCString CtiTableDeviceDialup::getTableName()
{
   return "DeviceDialupSettings";
}

CtiTableDeviceDialup& CtiTableDeviceDialup::setDeviceID(const LONG deviceID)
{

   _deviceID = deviceID;
   return *this;
}

LONG CtiTableDeviceDialup::getDeviceID() const
{

   return _deviceID;
}

RWDBStatus CtiTableDeviceDialup::Restore()
{

   char temp[32];

   RWDBConnection conn = getConnection();
   RWLockGuard<RWDBConnection> conn_guard(conn);

   RWDBTable table = getDatabase().table( getTableName() );
   RWDBSelector selector = getDatabase().selector();

   selector <<
      table["deviceid"] <<
      table["phonenumber"] <<
      table["minconnecttime"] <<
      table["maxconnecttime"] <<
      table["linesettings"] <<
      table["baudrate"];

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

RWDBStatus CtiTableDeviceDialup::Insert()
{


   RWDBConnection conn = getConnection();
   RWLockGuard<RWDBConnection> conn_guard(conn);

   RWDBTable table = getDatabase().table( getTableName() );
   RWDBInserter inserter = table.inserter();

   inserter <<
      getDeviceID() <<
      getPhoneNumber() <<
      getMinConnectTime() <<
      getMaxConnectTime() <<
      getLineSettings() <<
      getBaudRate();

   if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
   {
      setDirty(false);
   }

   return inserter.status();
}

RWDBStatus CtiTableDeviceDialup::Update()
{
   char temp[32];



   RWDBConnection conn = getConnection();
   RWLockGuard<RWDBConnection> conn_guard(conn);

   RWDBTable table = getDatabase().table( getTableName() );
   RWDBUpdater updater = table.updater();

   updater.where( table["deviceid"] == getDeviceID() );

   updater <<
      table["phonenumber"].assign(getPhoneNumber() ) <<
      table["minconnecttime"].assign(getMinConnectTime() ) <<
      table["maxconnecttime"].assign(getMaxConnectTime() ) <<
      table["linesettings"].assign(getLineSettings() ) <<
      table["baudrate"].assign(getBaudRate() );

   if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
   {
      setDirty(false);
   }

   return updater.status();
}

RWDBStatus CtiTableDeviceDialup::Delete()
{


   RWDBConnection conn = getConnection();
   RWLockGuard<RWDBConnection> conn_guard(conn);

   RWDBTable table = getDatabase().table( getTableName() );
   RWDBDeleter deleter = table.deleter();

   deleter.where( table["deviceid"] == getDeviceID() );
   deleter.execute( conn );
   return deleter.status();
}

