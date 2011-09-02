#pragma once

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>


#include "dllbase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceRoute : public CtiMemDBObject
{
protected:

   LONG        RouteID;

public:

   CtiTableDeviceRoute();

   CtiTableDeviceRoute(const CtiTableDeviceRoute& aRef);

   virtual ~CtiTableDeviceRoute();

   CtiTableDeviceRoute& operator=(const CtiTableDeviceRoute& aRef);

   LONG  getRouteID() const;

   LONG  getID() const;

   CtiTableDeviceRoute& setRouteID( const LONG aRouteID );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static std::string getTableName();
};
