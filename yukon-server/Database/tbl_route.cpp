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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
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
   RWDBTable devTbl = db.table(getTableName().c_str() );

   selector << devTbl["routeid"];
   selector.from(devTbl);
   selector.where( selector.where() && keyTable["paobjectid"].leftOuterJoin(devTbl["deviceid"]) );
}

void CtiTableDeviceRoute::DecodeDatabaseReader(RWDBReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    rdr["routeid"] >> RouteID;
}

string CtiTableDeviceRoute::getTableName()
{
   return "DeviceRoutes";
}
