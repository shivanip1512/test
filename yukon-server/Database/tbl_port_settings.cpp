/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_settings
*
* Date:   9/6/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_port_settings.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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

CtiTablePortSettings::CtiTablePortSettings(const CtiTablePortSettings& aRef)
{
   *this = aRef;
}

CtiTablePortSettings::~CtiTablePortSettings() {}

CtiTablePortSettings& CtiTablePortSettings::operator=(const CtiTablePortSettings& aRef)
{
   if(this != &aRef)
   {
      _baudRate      = aRef.getBaudRate();
      _cdWait        = aRef.getCDWait();
      _lineSettings  = aRef.getLineSettings();
   }

   return *this;
}

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
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["baudrate"]         >> _baudRate;
    rdr["cdwait"]           >> _cdWait;
    rdr["linesettings"]     >> _lineSettings;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << " Baud Rate            = " << _baudRate << endl;
        dout << " CD Wait Time         = " << _cdWait << endl;
        dout << " Line Parameters      = " << _lineSettings << endl;
    }
}


