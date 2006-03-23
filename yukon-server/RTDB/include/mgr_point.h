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
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_point.h-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2006/03/23 15:29:19 $
*
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/
#ifndef __MGR_POINT_H__
#define __MGR_POINT_H__
#pragma warning( disable : 4786 )


#include <rw/db/connect.h>

#include "dlldefs.h"
#include "smartmap.h"
#include "pt_base.h"
#include "slctpnt.h"

/*
 *  The following functions may be used to create sublists for the points in our database.
 */

class IM_EX_PNTDB CtiPointManager
{
public:

   typedef CtiLockGuard<CtiMutex>      LockGuard;
   typedef CtiSmartMap< CtiPoint >     coll_type;              // This is the collection type!
   typedef coll_type::ptr_type         ptr_type;
   typedef coll_type::spiterator       spiterator;
   typedef coll_type::insert_pair      insert_pair;

private:

    coll_type     _smartMap;
    void refreshPoints(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiPointBase*,void*), void *arg);

    // These are properties of already collected points.
    void refreshPointProperties(LONG pntID = 0, LONG paoID = 0);
    void refreshPointLimits(LONG pntID = 0, LONG paoID = 0);
    void refreshAlarming(LONG pntID = 0, LONG paoID = 0);

public:

    CtiPointManager();
    virtual ~CtiPointManager();
    virtual void refreshList(BOOL (*fn)(CtiPointBase*,void*) = isPoint, void *d = NULL, LONG pntID = 0, LONG paoID = 0);
    virtual void DumpList(void);
    virtual void DeleteList(void);

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);
    ptr_type find(bool (*findFun)(const long, ptr_type, void*), void* d);
    ptr_type getControlOffsetEqual(LONG pao, INT Offset);
    ptr_type getOffsetTypeEqual(LONG pao, INT Offset, INT Type);
    ptr_type getEqual(LONG Pt);
    ptr_type getEqualByName(LONG pao, string pname);

    bool orphan(long pid);

    spiterator begin();
    spiterator end();

    size_t entries();

    CtiMutex & getMux()
    {
        return _smartMap.getMux();
    }

};

#endif                  // #ifndef __MGR_POINT_H__
