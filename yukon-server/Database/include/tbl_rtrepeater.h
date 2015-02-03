#pragma once

namespace Cti {
   class RowReader;
}

#include "dlldefs.h"
#include "loggable.h"

class IM_EX_CTIYUKONDB CtiTableRepeaterRoute : public Cti::Loggable
{
   long _routeID;
   long _deviceID;
   int  _varBit;
   int  _repeaterOrder;

public:

   CtiTableRepeaterRoute(Cti::RowReader &rdr);

   virtual std::string toString() const override;

   LONG getDeviceID()      const {  return _deviceID;       };
   LONG getRouteID()       const {  return _routeID;        };
   INT  getVarBit()        const {  return _varBit;         };
   INT  getRepeaterOrder() const {  return _repeaterOrder;  };

   static std::string getSQLCoreStatement();

   bool operator<(const CtiTableRepeaterRoute& t2) const;
};

