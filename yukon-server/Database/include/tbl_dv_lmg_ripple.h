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

class IM_EX_CTIYUKONDB CtiTableRippleLoadGroup : public CtiMemDBObject
{
protected:

   LONG           _deviceID;
   std::string    _controlBits;     // The control command transmitted to this group.
   std::string    _restoreBits;     // The restore command transmitted to this group.
   LONG           _routeID;         // the route
   LONG           _shedTime;

public:

   CtiTableRippleLoadGroup();

   CtiTableRippleLoadGroup(const CtiTableRippleLoadGroup& aRef);

   virtual ~CtiTableRippleLoadGroup();

   CtiTableRippleLoadGroup& operator=(const CtiTableRippleLoadGroup& aRef);

   LONG  getRouteID() const;

   CtiTableRippleLoadGroup& setRouteID( const LONG a_routeID );

   std::string getControlBits() const;
   BYTE  getControlBit(INT i);

   CtiTableRippleLoadGroup& setControlBits( const std::string str );
   CtiTableRippleLoadGroup& setControlBit( INT pos, const BYTE ch );

   std::string getRestoreBits() const;
   BYTE getRestoreBit(INT i);

   CtiTableRippleLoadGroup& setRestoreBits( const std::string str );
   CtiTableRippleLoadGroup& setRestoreBit( INT pos, const BYTE ch );

   LONG getDeviceID() const;
   LONG getShedTime() const;

   static std::string getTableName();

   CtiTableRippleLoadGroup& setShedTime( const LONG stm);
   CtiTableRippleLoadGroup& setDeviceID( const LONG did);

   //corey's original .h has no decodedatabasereader()...
   void DecodeDatabaseReader(Cti::RowReader &rdr);

   bool copyMessage(BYTE *bptr, bool shed) const;
};

