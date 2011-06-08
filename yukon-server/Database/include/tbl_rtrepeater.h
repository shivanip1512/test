#pragma once

#include "row_reader.h"

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

   CtiTableRepeaterRoute(Cti::RowReader &rdr);

   void DumpData();

   LONG getDeviceID()      const {  return DeviceID;       };
   LONG getRouteID()       const {  return _routeID;       };
   INT  getVarBit()        const {  return VarBit;         };
   INT  getRepeaterOrder() const {  return RepeaterOrder;  };

   static std::string getSQLCoreStatement();

   RWBoolean operator<( const CtiTableRepeaterRoute& t2 );
   RWBoolean operator==( const CtiTableRepeaterRoute& t2 );

   static std::string getTableName();
};

