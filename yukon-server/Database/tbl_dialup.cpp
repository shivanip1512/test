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
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/12/20 17:16:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dialup.h"
#include "logger.h"

#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"

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

string CtiTableDeviceDialup::getPhoneNumber() const
{

    return PhoneNumber;
}

void CtiTableDeviceDialup::setPhoneNumber(const string &str)
{

    PhoneNumber = str;
}

string CtiTableDeviceDialup::getLineSettings() const
{

    return LineSettings;
}

void CtiTableDeviceDialup::setLineSettings(const string &lstr)
{

    LineSettings = lstr;
}

void CtiTableDeviceDialup::DecodeDatabaseReader(Cti::RowReader &rdr)
{

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["phonenumber"] >> PhoneNumber;
    rdr["minconnecttime"] >> MinConnectTime;
    rdr["maxconnecttime"] >> MaxConnectTime;
    rdr["linesettings"] >> LineSettings;
    rdr["baudrate"] >> BaudRate;
}

string CtiTableDeviceDialup::getTableName()
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

bool CtiTableDeviceDialup::Insert()
{
    static const std::string sql = "insert into " + getTableName() + " values (?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter 
        << getDeviceID()
        << getPhoneNumber()
        << getMinConnectTime()
        << getMaxConnectTime()
        << getLineSettings()
        << getBaudRate();

    bool success = inserter.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceDialup::Update()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "phonenumber = ?, "
                                        "minconnecttime = ?, "
                                        "maxconnecttime = ?, "
                                        "linesettings = ?, "
                                        "baudrate = ?"
                                   " where "
                                        "deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater 
        << getPhoneNumber()
        << getMinConnectTime()
        << getMaxConnectTime()
        << getLineSettings()
        << getBaudRate()
        << getDeviceID();

    bool success = updater.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceDialup::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getDeviceID();

    return deleter.execute();
}


INT CtiTableDeviceDialup::getBits() const
{
   char temp[8];
   memset(temp, '\0', 8);
   temp[0] = getLineSettings()[0];

   return atoi(temp);
}

INT CtiTableDeviceDialup::getParity() const
{
   char paritychar = getLineSettings()[1];
   int  parity = NOPARITY;

   switch(paritychar)
   {
   case 'N':
      {
         parity = NOPARITY;
         break;
      }
   case 'E':
      {
         parity = EVENPARITY;
         break;
      }
   case 'O':
      {
         parity = ODDPARITY;
         break;
      }
   }

   return parity;
}

INT CtiTableDeviceDialup::getStopBits() const
{
   char stopchar = getLineSettings()[2];
   int  stop = ONESTOPBIT;

   switch(stopchar)
   {
   case '1':
      {
         stop = ONESTOPBIT;
         break;
      }
   case '2':
      {
         stop = TWOSTOPBITS;
         break;
      }
   }

   return stop;
}

