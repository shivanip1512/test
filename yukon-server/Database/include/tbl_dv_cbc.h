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

class IM_EX_CTIYUKONDB CtiTableDeviceCBC : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceCBC(const CtiTableDeviceCBC&);
    CtiTableDeviceCBC& operator=(const CtiTableDeviceCBC&);

protected:

   LONG     _deviceID;
   INT      _serial;             // Versacom Serial number
   LONG     _routeID;            // the route (macro) which defines this device.

public:

   CtiTableDeviceCBC();
   virtual ~CtiTableDeviceCBC();

   INT  getSerial() const;

   CtiTableDeviceCBC& setSerial( const INT a_ser );

   LONG  getRouteID() const;

   CtiTableDeviceCBC& setRouteID( const LONG a_routeID );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static std::string getTableName();

   LONG getDeviceID() const;

   CtiTableDeviceCBC& setDeviceID( const LONG did);
};

