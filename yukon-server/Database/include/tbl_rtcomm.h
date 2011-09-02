#pragma once

#include "row_reader.h"
#include <limits.h>

#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "dbaccess.h"
#include "dllbase.h"
#include "resolvers.h"
#include "desolvers.h"
#include "types.h"
#include "logger.h"
#include "yukon.h"
#include "dbmemobject.h"


#include "ctibase.h"
#include "dlldefs.h"


class IM_EX_CTIYUKONDB CtiTableCommRoute : public CtiMemDBObject
{
protected:

   LONG        _routeID;
   LONG        DeviceID;
   bool        DefaultRoute;

private:

public:

   CtiTableCommRoute(const LONG dID = -1L, const bool aDef = FALSE);

   CtiTableCommRoute(const CtiTableCommRoute& aRef);

   ~CtiTableCommRoute();

   CtiTableCommRoute& operator=(const CtiTableCommRoute& aRef);

   void DumpData();

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
