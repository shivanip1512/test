#pragma warning( disable : 4786 )

/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtroute
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_rtroute.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:46 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __TBL_RTROUTE_H__
#define __TBL_RTROUTE_H__

#include <rw/db/db.h>
#include <rw\cstring.h>
#include <rw\thr\mutex.h>

#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "resolvers.h"
#include "ctibase.h"

IM_EX_CTIBASE INT getDebugLevel(void);

class IM_EX_CTIYUKONDB CtiTableRoute : public CtiMemDBObject
{
protected:

   LONG        RouteID;
   RWCString   Name;
   INT         Type;


public:

   typedef CtiMemDBObject Inherited;

   CtiTableRoute();
   CtiTableRoute(LONG &aRoute, RWCString aStr, INT aType);
   CtiTableRoute(const CtiTableRoute& aRef);
   virtual ~CtiTableRoute();

   CtiTableRoute& operator=(const CtiTableRoute& aRef);
   void DumpData();

   static RWCString& getSQLColumns(RWCString &str);
   static RWCString& getSQLTables(RWCString &str);
   static RWCString& getSQLConditions(RWCString &str);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static void getSQL(RWCString &Columns, RWCString &Tables, RWCString &Conditions);

   virtual void Insert();
   virtual void Update();
   virtual void Delete();
   virtual void Restore();

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual RWCString getTableName() const;

   INT  getType() const;

   CtiTableRoute& setType( const INT aType );

   RWCString  getName() const;

   CtiTableRoute& setName( const RWCString aName );
   LONG  getID() const;
   LONG  getRouteID() const;

   CtiTableRoute& setRouteID( const LONG aRouteID );
};
#endif // #ifndef __TBL_RTROUTE_H__
