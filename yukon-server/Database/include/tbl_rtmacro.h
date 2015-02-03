#pragma once

#include "dlldefs.h"
#include "dbmemobject.h"
#include "logger.h"

namespace Cti {

class RowReader;

namespace Tables {

class IM_EX_CTIYUKONDB MacroRouteTable : public CtiMemDBObject, public Cti::Loggable
{
   long       RouteID;
   int        _singleRouteID;
   int        RouteOrder;

public:

   MacroRouteTable(Cti::RowReader &rdr);

   virtual std::string toString() const override;

   long getRouteID() const;
   int  getRouteOrder() const;
   int  getSingleRouteID() const;

   bool operator<(const MacroRouteTable& t2) const;

   static std::string getSQLCoreStatement();
};

}
}
