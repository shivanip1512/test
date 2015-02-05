#pragma once

#include <limits.h>


#include "dllbase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceRoute : public CtiMemDBObject, private boost::noncopyable
{
   LONG        RouteID;

public:

   CtiTableDeviceRoute();
   virtual ~CtiTableDeviceRoute();

   LONG  getRouteID() const;
   LONG  getID() const;

   CtiTableDeviceRoute& setRouteID( const LONG aRouteID );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static std::string getTableName();
};
