
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtrepeater
*
* Date:   9/30/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_rtrepeater.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_RTREPEATER_H__
#define __TBL_RTREPEATER_H__

//#include <rw/tvordvec.h>
//RWTValOrderedVector  CtiRepeater;

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

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"
#include "types.h"
#include "logger.h"

class IM_EX_CTIYUKONDB CtiTableRepeaterRoute : public CtiMemDBObject
{

protected:

   LONG                 _routeID;
   LONG                 DeviceID;
   INT                  VarBit;
   INT                  RepeaterOrder;

private:

public:

   CtiTableRepeaterRoute(LONG dID = -1L, INT vb = 7, INT ro = 8);

   CtiTableRepeaterRoute(const CtiTableRepeaterRoute& aRef);

   ~CtiTableRepeaterRoute();

   CtiTableRepeaterRoute& operator=(const CtiTableRepeaterRoute& aRef);

   void DumpData();

   LONG  getDeviceID() const;
   CtiTableRepeaterRoute& setDeviceID( const LONG aDeviceID );

   LONG  getRouteID() const;
   CtiTableRepeaterRoute& setRouteID( const LONG rid );

   INT  getVarBit() const;
   CtiTableRepeaterRoute& setVarBit( const INT aVarBit );

   INT  getRepeaterOrder() const;
   CtiTableRepeaterRoute& setRepeaterOrder( const INT aRepeaterOrder );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   RWBoolean operator<( const CtiTableRepeaterRoute& t2 );
   RWBoolean operator==( const CtiTableRepeaterRoute& t2 );

   static RWCString getTableName();

   virtual RWDBStatus Restore();  //object from db
   virtual RWDBStatus Insert();   //object into db from mem
   virtual RWDBStatus Update();   //db from object
   virtual RWDBStatus Delete();   //object from db
};
#endif // #ifndef __TBL_RTREPEATER_H__
