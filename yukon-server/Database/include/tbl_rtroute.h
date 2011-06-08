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
#include <rw\thr\mutex.h>


#include "dlldefs.h"
#include "dbmemobject.h"
#include "ctibase.h"
#include "row_reader.h"


IM_EX_CTIBASE INT getDebugLevel(void);

class CtiTableRoute : public CtiMemDBObject
{
protected:

   LONG        RouteID;
   std::string   Name;
   INT         Type;


public:

   typedef CtiMemDBObject Inherited;

   CtiTableRoute();
   CtiTableRoute(LONG &aRoute, std::string aStr, INT aType);
   CtiTableRoute(const CtiTableRoute& aRef);
   virtual ~CtiTableRoute();

   CtiTableRoute& operator=(const CtiTableRoute& aRef);
   void DumpData();

   static std::string& getSQLColumns(std::string &str);
   static std::string& getSQLTables(std::string &str);
   static std::string& getSQLConditions(std::string &str);

   static void getSQL(std::string &Columns, std::string &Tables, std::string &Conditions);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   virtual std::string getTableName() const;

   INT  getType() const;

   CtiTableRoute& setType( const INT aType );

   std::string  getName() const;

   CtiTableRoute& setName( const std::string aName );
   LONG  getID() const;
   LONG  getRouteID() const;

   CtiTableRoute& setRouteID( const LONG aRouteID );
};
#endif // #ifndef __TBL_RTROUTE_H__
