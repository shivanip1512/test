#pragma once

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceCBC : public CtiMemDBObject
{

protected:

   LONG     _deviceID;
   INT      _serial;             // Versacom Serial number
   LONG     _routeID;            // the route (macro) which defines this device.

public:

   CtiTableDeviceCBC();

   CtiTableDeviceCBC(const CtiTableDeviceCBC& aRef);

   virtual ~CtiTableDeviceCBC();

   CtiTableDeviceCBC& operator=(const CtiTableDeviceCBC& aRef);

   INT  getSerial() const;

   CtiTableDeviceCBC& setSerial( const INT a_ser );

   LONG  getRouteID() const;

   CtiTableDeviceCBC& setRouteID( const LONG a_routeID );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static string getTableName();

   LONG getDeviceID() const;

   CtiTableDeviceCBC& setDeviceID( const LONG did);
};

