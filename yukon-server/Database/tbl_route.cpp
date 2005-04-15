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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/04/15 18:28:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

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
   RWDBTable devTbl = db.table(getTableName() );

   selector << devTbl["routeid"];
   selector.from(devTbl);
   selector.where( selector.where() && keyTable["paobjectid"].leftOuterJoin(devTbl["deviceid"]) );
}

void CtiTableDeviceRoute::DecodeDatabaseReader(RWDBReader &rdr)
{
   {
      CtiLockGuard<CtiLogger> logger_guard(dout);
      if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   rdr["routeid"] >> RouteID;
}

RWCString CtiTableDeviceRoute::getTableName()
{
   return "DeviceRoutes";
}
