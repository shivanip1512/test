
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_route
*
* Date:   8/16/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_route.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:38 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_route.h"
#include "logger.h"

CtiTableDeviceRoute::CtiTableDeviceRoute() :
   RouteID(-1)
{}

CtiTableDeviceRoute::CtiTableDeviceRoute(const CtiTableDeviceRoute& aRef)
{
   *this = aRef;
}

CtiTableDeviceRoute::~CtiTableDeviceRoute()
{}

CtiTableDeviceRoute& CtiTableDeviceRoute::operator=(const CtiTableDeviceRoute& aRef)
{
   if(this != &aRef)
   {
      RouteID = aRef.getRouteID();
   }
   return *this;
}

LONG  CtiTableDeviceRoute::getRouteID() const
{

   return RouteID;
}

LONG  CtiTableDeviceRoute::getID() const
{

   return RouteID;
}

CtiTableDeviceRoute& CtiTableDeviceRoute::setRouteID( const LONG aRouteID )
{

   RouteID = aRouteID;
   return *this;
}

void CtiTableDeviceRoute::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   keyTable = db.table("Device");
   RWDBTable devTbl = db.table(getTableName() );

   selector <<
      keyTable["deviceid"] <<
      devTbl["routeid"];

   selector.from(keyTable);
   selector.from(devTbl);

   selector.where( selector.where() && keyTable["deviceid"] == devTbl["deviceid"] );
}

void CtiTableDeviceRoute::DecodeDatabaseReader(RWDBReader &rdr)
{
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   rdr["routeid"] >> RouteID;
}

RWCString CtiTableDeviceRoute::getTableName()
{
   return "DeviceRoutes";
}
