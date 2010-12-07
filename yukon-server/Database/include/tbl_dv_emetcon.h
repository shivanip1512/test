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

class IM_EX_CTIYUKONDB CtiTableEmetconLoadGroup : public CtiMemDBObject
{
protected:

   LONG  _deviceID;
   INT   _silver;             // Zero based once in here!
   INT   _gold;               // Zero based once in here!
   INT   _addressUsage;       // This better be a nice little mask
   INT   _relay;

   LONG  _routeID;            // the route (macro) which defines this device.

public:

   CtiTableEmetconLoadGroup();

   CtiTableEmetconLoadGroup(const CtiTableEmetconLoadGroup& aRef);

   virtual ~CtiTableEmetconLoadGroup();

   CtiTableEmetconLoadGroup& operator=(const CtiTableEmetconLoadGroup& aRef);

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

   static string getTableName();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

