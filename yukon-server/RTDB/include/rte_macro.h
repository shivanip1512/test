#pragma once

#include <rw/tpordvec.h>
#include <rw/tvordvec.h>

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
#include "tbl_rtmacro.h"
#include "rte_base.h"
#include "cmdparse.h"
#include "mutex.h"
#include "yukon.h"

class IM_EX_DEVDB CtiRouteMacro : public CtiRouteBase
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiRouteMacro(const CtiRouteMacro&);
    CtiRouteMacro& operator=(const CtiRouteMacro&);

protected:

   // All I really do is hint at REAL Routes.... Which had better exist somewhere else.. I think.
   RWTValOrderedVector< CtiTableMacroRoute >  RouteList;
   RWTValOrderedVector< boost::shared_ptr< CtiRoute > >  RoutePtrList;    // Not responsible for these route pointer's memory...

   mutable CtiMutex _routeListMux;

public:

   typedef CtiRouteBase Inherited;

   typedef RWTValOrderedVector< CtiTableMacroRoute > CtiRouteList_t;
   typedef RWTValOrderedVector< boost::shared_ptr< CtiRoute > > CtiRoutePtrList_t;

   CtiRouteMacro();

   virtual std::string toString() const override;

   CtiMutex& getRouteListMux();

   CtiRouteList_t& CtiRouteMacro::getRouteList();
   CtiRouteList_t   CtiRouteMacro::getRouteList() const;

   CtiRoutePtrList_t & getRoutePtrList();
   CtiRoutePtrList_t   getRoutePtrList() const;

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;
   void DecodeMacroReader(Cti::RowReader &rdr);

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList) override;

   bool processAdditionalRoutes( const INMESS &InMessage ) const override;

};

typedef boost::shared_ptr<CtiRouteMacro> CtiRouteMacroSPtr;
