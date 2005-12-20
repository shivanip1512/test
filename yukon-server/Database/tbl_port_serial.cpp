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
#include "yukon.h"

#include "tbl_port_serial.h"
#include "logger.h"
#include "rwutil.h"

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

void CtiTablePortLocalSerial::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   RWDBTable portLocalSerial    = db.table("PortLocalSerial");

   selector << portLocalSerial["physicalport"];

   selector.from(portLocalSerial);

   selector.where( selector.where() && keyTable["paobjectid"] == portLocalSerial["portid"] );
}


void CtiTablePortLocalSerial::DecodeDatabaseReader(RWDBReader &rdr)
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


