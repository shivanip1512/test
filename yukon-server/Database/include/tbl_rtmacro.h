
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtmacro
*
* Date:   9/30/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_rtmacro.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:19 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_RTMACRO_H__
#define __TBL_RTMACRO_H__

#include <limits.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>

#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <rw/db/nullind.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <windows.h>
#include <limits.h>

#include <rw/db/datetime.h>
#include <rw/rwtime.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "dbaccess.h"
#include "dllbase.h"
#include "desolvers.h"
#include "types.h"
#include "logger.h"
#include "yukon.h"
#include "ctibase.h"


class IM_EX_CTIYUKONDB CtiTableMacroRoute : public CtiMemDBObject
{

protected:

   LONG        RouteID;
   INT         _singleRouteID;
   INT         RouteOrder;

private:

public:

   CtiTableMacroRoute(LONG RouteID = -1L, INT RouteOrder = INT_MAX);

   CtiTableMacroRoute(const CtiTableMacroRoute& aRef);

   virtual ~CtiTableMacroRoute();

   CtiTableMacroRoute& operator=(const CtiTableMacroRoute& aRef);

   void DumpData();

   LONG getRouteID() const;
   CtiTableMacroRoute& setRouteID( const LONG aRouteID );

   INT getRouteOrder() const;
   CtiTableMacroRoute& setRouteOrder( const INT aRouteOrder );

   INT getSingleRouteID() const;
   CtiTableMacroRoute& setSingleRouteID( const INT srid );

   RWBoolean operator<(const CtiTableMacroRoute& t2);

   RWBoolean operator==(const CtiTableMacroRoute& t2);

   static RWCString getTableName();

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual RWDBStatus Restore();  //object from db
   virtual RWDBStatus Insert();   //object into db from mem
   virtual RWDBStatus Update();   //db from object
   virtual RWDBStatus Delete();   //object from db

};
#endif // #ifndef __TBL_RTMACRO_H__
