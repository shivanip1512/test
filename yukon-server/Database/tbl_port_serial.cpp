#include "yukon.h"


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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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
      if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

      rdr["physicalport"] >> _physicalPort;
      if(getDebugLevel() & 0x0800) dout << " Physical Port Desc.  = " << _physicalPort << endl;
   }
}


