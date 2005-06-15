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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/06/15 23:56:34 $
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


