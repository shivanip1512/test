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

