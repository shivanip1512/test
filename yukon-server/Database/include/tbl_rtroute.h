#pragma warning( disable : 4786 )

/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtroute
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_rtroute.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:09 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __TBL_RTROUTE_H__
#define __TBL_RTROUTE_H__

#include "yukon.h"
#include <rw/db/db.h>
#include <rw\thr\mutex.h>


#include "dlldefs.h"
#include "dbmemobject.h"
#include "ctibase.h"


IM_EX_CTIBASE INT getDebugLevel(void);

class IM_EX_CTIYUKONDB CtiTableRoute : public CtiMemDBObject
{
protected:

   LONG        RouteID;
   string   Name;
   INT         Type;


public:

   typedef CtiMemDBObject Inherited;

   CtiTableRoute();
   CtiTableRoute(LONG &aRoute, string aStr, INT aType);
   CtiTableRoute(const CtiTableRoute& aRef);
   virtual ~CtiTableRoute();

   CtiTableRoute& operator=(const CtiTableRoute& aRef);
   void DumpData();

   static string& getSQLColumns(string &str);
   static string& getSQLTables(string &str);
   static string& getSQLConditions(string &str);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static void getSQL(string &Columns, string &Tables, string &Conditions);

   virtual void Insert();
   virtual void Update();
   virtual void Delete();
   virtual void Restore();

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual string getTableName() const;

   INT  getType() const;

   CtiTableRoute& setType( const INT aType );

   string  getName() const;

   CtiTableRoute& setName( const string aName );
   LONG  getID() const;
   LONG  getRouteID() const;

   CtiTableRoute& setRouteID( const LONG aRouteID );
};
#endif // #ifndef __TBL_RTROUTE_H__
