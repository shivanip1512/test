#pragma once

#include <rw/db/reader.h>
#include <rw/db/db.h>
#include <rw/db/table.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbaccess.h"

class IM_EX_CTIYUKONDB CtiTableRepeaterRoute
{
private:

   LONG                 _routeID;
   LONG                 DeviceID;
   INT                  VarBit;
   INT                  RepeaterOrder;

public:

   CtiTableRepeaterRoute(RWDBReader &rdr);

   void DumpData();

   LONG getDeviceID()      const {  return DeviceID;       };
   LONG getRouteID()       const {  return _routeID;       };
   INT  getVarBit()        const {  return VarBit;         };
   INT  getRepeaterOrder() const {  return RepeaterOrder;  };

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   RWBoolean operator<( const CtiTableRepeaterRoute& t2 );
   RWBoolean operator==( const CtiTableRepeaterRoute& t2 );

   static string getTableName();
};

