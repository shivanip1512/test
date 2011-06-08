/*-----------------------------------------------------------------------------*
*
* File:   rte_macro
*
* Class:  CtiRouteMacro
* Date:   9/30/1999
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/rte_macro.h-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __RTE_MACRO_H__
#define __RTE_MACRO_H__


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
protected:

   // All I really do is hint at REAL Routes.... Which had better exist somewhere else.. I think.
   RWTValOrderedVector< CtiTableMacroRoute >  RouteList;
   RWTValOrderedVector< boost::shared_ptr< CtiRoute > >  RoutePtrList;    // Not responsible for these route pointer's memory...

   mutable CtiMutex _routeListMux;

private:

public:

   typedef CtiRouteBase Inherited;

   typedef RWTValOrderedVector< CtiTableMacroRoute > CtiRouteList_t;
   typedef RWTValOrderedVector< boost::shared_ptr< CtiRoute > > CtiRoutePtrList_t;

   CtiRouteMacro();
   CtiRouteMacro(const CtiRouteMacro& aRef);
   ~CtiRouteMacro();

   CtiRouteMacro& operator=(const CtiRouteMacro& aRef);
   virtual void DumpData();

   CtiMutex& getRouteListMux();

   CtiRouteList_t& CtiRouteMacro::getRouteList();
   CtiRouteList_t   CtiRouteMacro::getRouteList() const;

   CtiRoutePtrList_t & getRoutePtrList();
   CtiRoutePtrList_t   getRoutePtrList() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual void DecodeMacroReader(Cti::RowReader &rdr);

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              std::list< CtiMessage* >      &vgList,
                              std::list< CtiMessage* >      &retList,
                              std::list< OUTMESS* >         &outList);

   virtual bool processAdditionalRoutes( INMESS *InMessage ) const;

};

typedef boost::shared_ptr<CtiRouteMacro> CtiRouteMacroSPtr;

#endif // #ifndef __RTE_MACRO_H__
