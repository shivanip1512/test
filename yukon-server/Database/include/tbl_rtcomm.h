#pragma once

#include "row_reader.h"
#include "dbaccess.h"
#include "dllbase.h"
#include "resolvers.h"
#include "types.h"
#include "logger.h"
#include "yukon.h"
#include "dbmemobject.h"
#include "dlldefs.h"

#include <windows.h>
#include <limits.h>


class IM_EX_CTIYUKONDB CtiTableCommRoute : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
   LONG        _routeID;
   LONG        DeviceID;
   bool        DefaultRoute;

public:

   CtiTableCommRoute(const LONG dID = -1L, const bool aDef = FALSE);
   virtual ~CtiTableCommRoute();

   virtual std::string toString() const override;

   LONG  getID() const;
   LONG  getDeviceID() const;
   LONG  getTrxDeviceID() const;
   LONG  getRouteID() const;

   CtiTableCommRoute& setRouteID( const LONG rid );
   CtiTableCommRoute& setDeviceID( const LONG aDeviceID );

   bool  getDefaultRoute() const;
   CtiTableCommRoute& setDefaultRoute( const bool aDefaultRoute );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   static std::string getTableName();
};
