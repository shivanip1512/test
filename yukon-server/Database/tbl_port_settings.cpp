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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/04/15 18:28:40 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_port_settings.h"
#include "logger.h"

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

RWCString CtiTablePortSettings::getLineSettings() const       { return _lineSettings;}
RWCString& CtiTablePortSettings::getLineSettings()             { return _lineSettings;}
CtiTablePortSettings& CtiTablePortSettings::setLineSettings(const RWCString str)
{
   _lineSettings = str;
   return *this;
}

INT CtiTablePortSettings::getBits() const
{
   char temp[8];
   memset(temp, '\0', 8);
   temp[0] = _lineSettings.data()[0];

   return atoi(temp);
}

INT CtiTablePortSettings::getParity() const
{
   char paritychar = _lineSettings.data()[1];
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
   char stopchar = _lineSettings.data()[2];
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


void CtiTablePortSettings::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   RWDBTable portTbl  = db.table("PortSettings");

   selector <<
   portTbl["baudrate"] <<
   portTbl["cdwait"]   <<
   portTbl["linesettings"];

   selector.from(portTbl);

   selector.where( selector.where() && keyTable["paobjectid"] == portTbl["portid"] );
}

void CtiTablePortSettings::DecodeDatabaseReader(RWDBReader &rdr)
{
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

      rdr["baudrate"]         >> _baudRate;
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << " Baud Rate            = " << _baudRate << endl;

      rdr["cdwait"]           >> _cdWait;
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << " CD Wait Time         = " << _cdWait << endl;

      rdr["linesettings"]     >> _lineSettings;
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << " Line Parameters      = " << _lineSettings << endl;
   }
}


