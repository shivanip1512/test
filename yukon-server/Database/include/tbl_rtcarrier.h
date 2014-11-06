#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include "row_reader.h"
#include "dbaccess.h"
#include "dllbase.h"
#include "dlldefs.h"
#include "resolvers.h"
#include "types.h"
#include "logger.h"
#include "yukon.h"
#include "dbmemobject.h"

#include <windows.h>
#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>


class IM_EX_CTIYUKONDB CtiTableCarrierRoute : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableCarrierRoute(const CtiTableCarrierRoute&);
    CtiTableCarrierRoute& operator=(const CtiTableCarrierRoute&);

protected:

   LONG        _routeID;
   INT         Bus;                 // This is zero based in all code, 1 based in the DB!
   INT         CCUFixBits;
   INT         CCUVarBits;
   BOOL        _userLocked;
   BOOL        _resetRPTSettings;

public:

   CtiTableCarrierRoute(INT b = 1, INT f = 31, INT v = 7, INT a = RouteAmpUndefined);
   virtual ~CtiTableCarrierRoute();

   static std::string getTableName();

   virtual std::string toString() const override;

   INT getBus() const;
   CtiTableCarrierRoute& setBus( const INT aBus );

   BOOL getUserLocked() const;
   CtiTableCarrierRoute& setUserLocked( const BOOL ul );

   BOOL getResetRPTSettings() const;
   CtiTableCarrierRoute& setResetRPTSettings( const BOOL rs );

   INT  getCCUFixBits() const;
   CtiTableCarrierRoute& setCCUFixBits( const INT aCCUFixBit );

   LONG getRouteID() const;
   CtiTableCarrierRoute& setRouteID( const LONG rid);

   INT  getCCUVarBits() const;
   CtiTableCarrierRoute& setCCUVarBits( const INT aCCUVarBit );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
