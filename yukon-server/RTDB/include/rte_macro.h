#pragma once

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
#include "tbl_rtmacro.h"
#include "rte_base.h"
#include "cmdparse.h"
#include "mutex.h"
#include "yukon.h"

namespace Cti {
namespace Routes {

class IM_EX_DEVDB MacroRoute : public CtiRouteBase
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    MacroRoute(const MacroRoute&);
    MacroRoute& operator=(const MacroRoute&);

    typedef CtiRouteBase Inherited;

protected:

   // This route holds a list of route ID mappings...
   std::set<Tables::MacroRouteTable> RouteList;
   // ... and a list of pointers filled in by populateRouteAssocations in Porter
   std::vector<CtiRouteSPtr> RoutePtrList;

   mutable CtiMutex _routeListMux;

public:

   MacroRoute();

   virtual std::string toString() const override;

   CtiMutex& getMacroMux();

   std::vector<long> MacroRoute::getSubrouteIds() const;
   void addSubroute(CtiRouteSPtr rte);
   CtiRouteSPtr getSubroute(const unsigned offset) const;
   void clearSubroutes();

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;
   void DecodeSubrouteReader(Cti::RowReader &rdr);

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList) override;

   bool processAdditionalRoutes( const INMESS &InMessage ) const override;
};

typedef boost::shared_ptr<MacroRoute> MacroRouteSPtr;

}
}
