
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_route
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_route.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/08/05 20:43:23 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_ROUTE_H__
#define __TBL_ROUTE_H__

#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>


#include "dllbase.h"
#include "dlldefs.h"
#include "dbmemobject.h"


class IM_EX_CTIYUKONDB CtiTableDeviceRoute : public CtiMemDBObject
{
protected:

   LONG        RouteID;

public:

   CtiTableDeviceRoute();

   CtiTableDeviceRoute(const CtiTableDeviceRoute& aRef);

   virtual ~CtiTableDeviceRoute();

   CtiTableDeviceRoute& operator=(const CtiTableDeviceRoute& aRef);

   LONG  getRouteID() const;

   LONG  getID() const;

   CtiTableDeviceRoute& setRouteID( const LONG aRouteID );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   static RWCString getTableName();
};
#endif // #ifndef __TBL_ROUTE_H__
