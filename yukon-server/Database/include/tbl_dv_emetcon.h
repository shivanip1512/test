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

#define SILVERADDRESS  0x0000000f
#define GOLDADDRESS    0x00000030

class IM_EX_CTIYUKONDB CtiTableEmetconLoadGroup : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableEmetconLoadGroup(const CtiTableEmetconLoadGroup&);
    CtiTableEmetconLoadGroup& operator=(const CtiTableEmetconLoadGroup&);

protected:

   LONG  _deviceID;
   INT   _silver;             // Zero based once in here!
   INT   _gold;               // Zero based once in here!
   INT   _addressUsage;       // This better be a nice little mask
   INT   _relay;

   LONG  _routeID;            // the route (macro) which defines this device.

public:

   CtiTableEmetconLoadGroup();
   virtual ~CtiTableEmetconLoadGroup();

   INT getEmetconAddress() const;

   INT  getSilver() const;

   CtiTableEmetconLoadGroup& setSilver( const INT a_silver );

   INT  getGold() const;

   CtiTableEmetconLoadGroup& setGold( const INT a_gold );

   INT  getAddressUsage() const;

   CtiTableEmetconLoadGroup& setAddressUsage( const INT a_addressUsage );

   INT  getRelay() const;

   CtiTableEmetconLoadGroup& setRelay( const INT a_relay );

   LONG  getRouteID() const;

   CtiTableEmetconLoadGroup& setRouteID( const LONG a_routeID );

   LONG  getDeviceID() const;

   CtiTableEmetconLoadGroup& setDeviceID( const LONG did );

   static std::string getTableName();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

