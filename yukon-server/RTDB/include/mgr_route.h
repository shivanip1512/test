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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2004/11/17 17:30:51 $
*
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/
#ifndef __ROUTE_MGR_H__
#define __ROUTE_MGR_H__

#include <rw/db/connect.h>
#include <rw/db/db.h>

#include "repeaterrole.h"
#include "rte_base.h"
#include "rtdb.h"
#include "dlldefs.h"
#include "slctdev.h"
#include "smartmap.h"

IM_EX_DEVDB BOOL isARoute(CtiRouteBase*,void*);

class IM_EX_DEVDB CtiRouteManager
{
private:

    CtiSmartMap< CtiRouteBase > _smartMap;

    void RefreshRoutes(bool &rowFound, RWDBReader& rdr, CtiRouteBase* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiRouteBase*,void*), void *arg);
    void RefreshRoutes(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg);
    void RefreshVersacomRoutes(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg);
    void RefreshRepeaterRoutes(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg);
    void RefreshMacroRoutes(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg);

public:

    typedef CtiLockGuard<CtiMutex>                      LockGuard;
    typedef CtiSmartMap< CtiRouteBase >                 coll_type;              // This is the collection type!
    typedef CtiSmartMap< CtiRouteBase >::ptr_type       ptr_type;
    typedef CtiSmartMap< CtiRouteBase >::spiterator     spiterator;
    typedef CtiSmartMap< CtiRouteBase >::insert_pair    insert_pair;


    CtiRouteManager();
    virtual ~CtiRouteManager();

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);

    void DumpList(void);
    void DeleteList(void);

    void RefreshList(CtiRouteBase* (*Factory)(RWDBReader &) = RouteFactory, BOOL (*fn)(CtiRouteBase*,void*) = isARoute, void *d = NULL);
    ptr_type getEqual( LONG rid );
    ptr_type getEqualByName( RWCString &rname );

    spiterator begin();
    spiterator end();

    static spiterator nextPos(spiterator &my_itr); // This is to overcome MS's flaw in the STL.  We MUST use this method in binaries other than this DLL.

    CtiMutex& getMux()
    {
        return _smartMap.getMux();
    }

    bool empty() const;
    bool buildRoleVector( long id, CtiRequestMsg& Req, RWTPtrSlist< CtiMessage > &retList, vector< CtiDeviceRepeaterRole > & roleVector );

};

#endif                  // #ifndef __ROUTE_MGR_H__
