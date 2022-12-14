#include "precompiled.h"

#include "tbl_port_settings.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePortSettings::CtiTablePortSettings() :
_baudRate(0),
_cdWait(0),
_lineSettings("8N1")
{}

CtiTablePortSettings::~CtiTablePortSettings() {}

INT CtiTablePortSettings::getBaudRate() const           { return _baudRate;}
INT& CtiTablePortSettings::getBaudRate()                 { return _baudRate;}
CtiTablePortSettings& CtiTablePortSettings::setBaudRate(const INT r)
{
   _baudRate = r;
   return *this;
}

ULONG CtiTablePortSettings::getCDWait() const             { return _cdWait;}
ULONG& CtiTablePortSettings::getCDWait()                   { return _cdWait;}
CtiTablePortSettings& CtiTablePortSettings::setCDWait(const INT w)
{
   _cdWait = w;
   return *this;
}

string CtiTablePortSettings::getLineSettings() const       { return _lineSettings;}
string& CtiTablePortSettings::getLineSettings()             { return _lineSettings;}
CtiTablePortSettings& CtiTablePortSettings::setLineSettings(const string str)
{
   _lineSettings = str;
   return *this;
}

INT CtiTablePortSettings::getBits() const
{
   char temp[8];
   memset(temp, '\0', 8);
   temp[0] = _lineSettings[0];

   return atoi(temp);
}

INT CtiTablePortSettings::getParity() const
{
   char paritychar = _lineSettings[1];
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

INT CtiTablePortSettings::getStopBits() const
{
   char stopchar = _lineSettings[2];
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

void CtiTablePortSettings::DecodeDatabaseReader(Cti::RowReader &rdr)
{

    rdr["baudrate"]         >> _baudRate;
    rdr["cdwait"]           >> _cdWait;
    rdr["linesettings"]     >> _lineSettings;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        Cti::FormattedList itemList;

        itemList.add("Baud Rate")       << _baudRate;
        itemList.add("CD Wait Time")    << _cdWait;
        itemList.add("Line Parameters") << _lineSettings;

        CTILOG_DEBUG(dout, "Decoding DB read from PortSettings"<<
                itemList
                );
    }
}


