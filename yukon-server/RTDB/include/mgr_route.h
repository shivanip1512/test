#ifndef __ROUTE_MGR_H__
#define __ROUTE_MGR_H__
/*************************************************************************
 *
 * mgr_route.h      7/7/99
 *
 *****
 *
 * The class which owns and manages route real time database
 *
 * Originated by:
 *     Corey G. Plender    7/7/99
 *
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_route.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:29 $
*
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/

#include <rw/db/connect.h>
#include <rw/db/db.h>

#include "rte_base.h"
#include "rtdb.h"
#include "dlldefs.h"
#include "slctdev.h"

IM_EX_DEVDB BOOL isARoute(CtiRouteBase*,void*);

class IM_EX_DEVDB CtiRouteManager : public CtiRTDB<CtiRoute>
{
private:

public:
   CtiRouteManager();
   virtual ~CtiRouteManager();


   void DumpList(void);
   void DeleteList(void);


   void RefreshList        (CtiRouteBase* (*Factory)(RWDBReader &) = RouteFactory,
                            BOOL (*fn)(CtiRouteBase*,void*) = isARoute,
                            void *d = NULL);

   void RefreshRoutes      (RWDBReader& rdr,
                            CtiRouteBase* (*Factory)(RWDBReader &),
                            BOOL (*testFunc)(CtiRouteBase*,void*), void *arg);

   void RefreshStatistics  (RWDBReader& rdr,
                            BOOL (*testFunc)(CtiRouteBase*,void*),
                            void *arg);

   void RefreshRoutes      (RWDBReader& rdr,
                            BOOL (*testFunc)(CtiRouteBase*,void*),
                            void *arg);

   void RefreshVersacomRoutes(RWDBReader& rdr,
                              BOOL (*testFunc)(CtiRouteBase*,void*),
                              void *arg);

   void RefreshRepeaterRoutes(RWDBReader& rdr,
                              BOOL (*testFunc)(CtiRouteBase*,void*),
                              void *arg);

   void RefreshMacroRoutes(RWDBReader& rdr,
                           BOOL (*testFunc)(CtiRouteBase*,void*),
                           void *arg);


   CtiRouteBase *getEqual( LONG RID );

   CtiRouteBase *RouteGetEqualByName( RWCString &rname );
};

#endif                  // #ifndef __ROUTE_MGR_H__
