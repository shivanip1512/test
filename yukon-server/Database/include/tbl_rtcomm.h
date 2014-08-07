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


class IM_EX_CTIYUKONDB CtiTableCommRoute : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableCommRoute(const CtiTableCommRoute&);
    CtiTableCommRoute& operator=(const CtiTableCommRoute&);

protected:

   LONG        _routeID;
   LONG        DeviceID;
   bool        DefaultRoute;

public:

   CtiTableCommRoute(const LONG dID = -1L, const bool aDef = FALSE);
   virtual ~CtiTableCommRoute();

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
