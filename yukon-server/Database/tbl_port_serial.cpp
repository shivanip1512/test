/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_serial
*
* Date:   9/6/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_port_serial.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "tbl_port_serial.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePortLocalSerial::CtiTablePortLocalSerial()
{}

CtiTablePortLocalSerial::CtiTablePortLocalSerial(const CtiTablePortLocalSerial& aRef)
{
   *this = aRef;
}

CtiTablePortLocalSerial::~CtiTablePortLocalSerial() {}

CtiTablePortLocalSerial& CtiTablePortLocalSerial::operator=(const CtiTablePortLocalSerial& aRef)
{
   if(this != &aRef)
   {
      _physicalPort = aRef.getPhysicalPort();
   }
   return *this;
}

string CtiTablePortLocalSerial::getPhysicalPort() const    { return _physicalPort; }
string& CtiTablePortLocalSerial::getPhysicalPort()          { return _physicalPort; }

CtiTablePortLocalSerial&   CtiTablePortLocalSerial::setPhysicalPort(const string& str )
{
   _physicalPort = str;
   return *this;
}

void CtiTablePortLocalSerial::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   rdr["physicalport"] >> _physicalPort;

   if(getDebugLevel() & DEBUGLEVEL_DATABASE)
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      dout << " Physical Port Desc.  = " << _physicalPort << endl;
   }
}


