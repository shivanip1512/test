


#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtcarrier
*
* Date:   9/30/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_rtcarrier.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_RTCARRIER_H__
#define __TBL_RTCARRIER_H__

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
#include <windows.h>

#include "dbaccess.h"
#include "dllbase.h"
#include "dlldefs.h"
#include "resolvers.h"
#include "desolvers.h"
#include "types.h"
#include "logger.h"
#include "yukon.h"
#include "dbmemobject.h"


class IM_EX_CTIYUKONDB CtiTableCarrierRoute : public CtiMemDBObject
{
protected:

   LONG        _routeID;
   INT         Bus;                 // This is zero based in all code, 1 based in the DB!
   INT         CCUFixBits;
   INT         CCUVarBits;
   BOOL        _userLocked;
   BOOL        _resetRPTSettings;

private:

public:

   CtiTableCarrierRoute(INT b = 1, INT f = 31, INT v = 7, INT a = RouteAmpUndefined);

   CtiTableCarrierRoute(const CtiTableCarrierRoute& aRef);

   virtual ~CtiTableCarrierRoute();

   static RWCString getTableName();

   CtiTableCarrierRoute& operator=(const CtiTableCarrierRoute& aRef);

   void DumpData();

   INT getBus() const;
   CtiTableCarrierRoute& setBus( const INT aBus );

   BOOL getUserLocked() const;
   CtiTableCarrierRoute& setUserLocked( const BOOL ul );

   BOOL getResetRPTSettings() const;
   CtiTableCarrierRoute& setResetRPTSettings( const BOOL rs );

   INT  getCCUFixBits() const;
   CtiTableCarrierRoute& setCCUFixBits( const INT aCCUFixBit );

   LONG getRouteID() const;
   CtiTableCarrierRoute& setRouteID( const LONG rid);

   INT  getCCUVarBits() const;
   CtiTableCarrierRoute& setCCUVarBits( const INT aCCUVarBit );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual RWDBStatus Restore();  //object from db
   virtual RWDBStatus Insert();   //object into db from mem
   virtual RWDBStatus Update();   //db from object
   virtual RWDBStatus Delete();   //object from db

};
#endif // #ifndef __TBL_RTCARRIER_H__

