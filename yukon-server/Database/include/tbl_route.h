#pragma once

#include <limits.h>


#include "dllbase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceRoute : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceRoute(const CtiTableDeviceRoute&);
    CtiTableDeviceRoute& operator=(const CtiTableDeviceRoute&);

protected:

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
