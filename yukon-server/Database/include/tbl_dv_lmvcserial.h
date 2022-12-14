#pragma once

#include "row_reader.h"
#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"

#include "vcomdefs.h"

class IM_EX_CTIYUKONDB CtiTableLMGroupVersacomSerial : public CtiMemDBObject, private boost::noncopyable
{
   LONG     _deviceID;
   INT      _serial;
   INT      _groupID;            // DEvice ID of the group this guy belongs to, bookkeeping.
   INT      _addressUsage;       // This IS a mask 3 - 0 bits mean USCD bit fields!
   INT      _relayMask;          // This IS a mask 31 - 0 bits means 32 - 1 relays!
   LONG     _routeID;            // the route (macro) which defines this device.

public:

   CtiTableLMGroupVersacomSerial();
   virtual ~CtiTableLMGroupVersacomSerial();

   INT  getSerial() const;

   CtiTableLMGroupVersacomSerial& setSerial( const INT a_ser );

   INT  getGroupID() const;

   CtiTableLMGroupVersacomSerial& setGroupID( const INT i );

   LONG getDeviceID() const;
   CtiTableLMGroupVersacomSerial& setDeviceID( const LONG did);

   INT  getAddressUsage() const;
   BOOL useUtilityID() const;
   BOOL useSection() const;
   BOOL useClass() const;
   BOOL useDivision() const;

   CtiTableLMGroupVersacomSerial& setAddressUsage( const INT a_addressUsage );

   INT  getRelayMask() const;
   BOOL useRelay(const INT r) const;

   CtiTableLMGroupVersacomSerial& setRelayMask( const INT a_relayMask );

   LONG  getRouteID() const;
   CtiTableLMGroupVersacomSerial& setRouteID( const LONG a_routeID );

   static std::string getTableName();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

};
