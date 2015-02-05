#pragma once

#include <limits.h>

#include "yukon.h"
#include "vcomdefs.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableVersacomLoadGroup : public CtiMemDBObject, private boost::noncopyable
{
   LONG     _deviceID;
   INT      _utilityID;           // 1-256
   INT      _section;             // 1-256
   INT      _class;               // 16-bit bit mask
   INT      _division;            // 16-bit bit mask
   INT      _serial;

   INT      _addressUsage;       // This IS a mask 3 - 0 bits mean USCD bit fields!
   INT      _relayMask;              // This IS a mask 31 - 0 bits means 32 - 1 relays!

   LONG     _routeID;            // the route (macro) which defines this device.

public:

   CtiTableVersacomLoadGroup();
   virtual ~CtiTableVersacomLoadGroup();

   INT  getSerial() const;
   CtiTableVersacomLoadGroup& setSerial( const INT a_ser );

   INT  getUtilityID() const;
   CtiTableVersacomLoadGroup& setUtilityID( const INT a_uid );
   INT  getSection() const;
   CtiTableVersacomLoadGroup& setSection( const INT aSection );

   INT  getClass() const;
   CtiTableVersacomLoadGroup& setClass( const INT aClass );

   INT  getDivision() const;
   CtiTableVersacomLoadGroup& setDivision( const INT aDivision );

   INT  getAddressUsage() const;
   BOOL useUtilityID() const;
   BOOL useSection() const;
   BOOL useClass() const;
   BOOL useDivision() const;

   LONG getDeviceID() const;
   CtiTableVersacomLoadGroup& setDeviceID( const LONG did );

   CtiTableVersacomLoadGroup& setAddressUsage( const INT a_addressUsage );

   INT  getRelayMask() const;
   BOOL useRelay(const INT r) const;
   CtiTableVersacomLoadGroup& setRelayMask( const INT a_relayMask );

   LONG  getRouteID() const;
   CtiTableVersacomLoadGroup& setRouteID( const LONG a_routeID );

   static std::string getTableName();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual bool Insert();
   virtual bool Update();
   virtual bool Delete();

};
