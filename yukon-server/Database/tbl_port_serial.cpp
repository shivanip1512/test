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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/04/15 18:28:40 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_port_serial.h"
#include "logger.h"

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

RWCString CtiTablePortLocalSerial::getPhysicalPort() const    { return _physicalPort; }
RWCString& CtiTablePortLocalSerial::getPhysicalPort()          { return _physicalPort; }

CtiTablePortLocalSerial&   CtiTablePortLocalSerial::setPhysicalPort(const RWCString& str )
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
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

      rdr["physicalport"] >> _physicalPort;
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << " Physical Port Desc.  = " << _physicalPort << endl;
   }
}


