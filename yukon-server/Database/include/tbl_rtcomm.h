
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtcomm
*
* Date:   9/30/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_rtcomm.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:19 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_RTCOMM_H__
#define __TBL_RTCOMM_H__

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <windows.h>

#include "dbaccess.h"
#include "dllbase.h"
#include "resolvers.h"
#include "desolvers.h"
#include "types.h"
#include "logger.h"
#include "yukon.h"
#include "dbmemobject.h"


#include "ctibase.h"
#include "dlldefs.h"


class IM_EX_CTIYUKONDB CtiTableCommRoute : public CtiMemDBObject
{
protected:

   LONG        _routeID;
   LONG        DeviceID;
   bool        DefaultRoute;

private:

public:

   CtiTableCommRoute(const LONG dID = -1L, const bool aDef = FALSE);

   CtiTableCommRoute(const CtiTableCommRoute& aRef);

   ~CtiTableCommRoute();

   CtiTableCommRoute& operator=(const CtiTableCommRoute& aRef);

   void DumpData();

   LONG  getID() const;
   LONG  getDeviceID() const;
   LONG  getTrxDeviceID() const;
   LONG  getRouteID() const;

   CtiTableCommRoute& setRouteID( const LONG rid );
   CtiTableCommRoute& setDeviceID( const LONG aDeviceID );

   bool  getDefaultRoute() const;
   CtiTableCommRoute& setDefaultRoute( const bool aDefaultRoute );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   static RWCString getTableName();

   virtual RWDBStatus Restore();  //object from db
   virtual RWDBStatus Insert();   //object into db from mem
   virtual RWDBStatus Update();   //db from object
   virtual RWDBStatus Delete();   //object from db

};
#endif // #ifndef __TBL_RTCOMM_H__
