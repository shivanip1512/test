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
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2007/11/01 16:39:06 $
*
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/
#ifndef __MGR_POINT_H__
#define __MGR_POINT_H__
#pragma warning( disable : 4786 )


#include <rw/db/connect.h>
#include <map>

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

   typedef CtiLockGuard<CtiMutex>    LockGuard;
   typedef CtiSmartMap <CtiPoint>    coll_type;              // This is the collection type!
   typedef coll_type::ptr_type       ptr_type;
   typedef coll_type::spiterator     spiterator;
   typedef coll_type::insert_pair    insert_pair;

private:

    coll_type     _smartMap;
    void refreshPoints(bool &rowFound, RWDBReader& rdr, BOOL (*testFunc)(CtiPointBase*,void*), void *arg);
    void addPoint(CtiPointBase *point);

    // These are properties of already collected points.
    void refreshPointProperties(LONG pntID = 0, LONG paoID = 0);
    void refreshPointLimits(LONG pntID = 0, LONG paoID = 0);
    void refreshAlarming(LONG pntID = 0, LONG paoID = 0);

    struct pao_offset_t
    {
        long paobjectid;
        long offset;

        pao_offset_t( long p, long o ) : paobjectid(p), offset(o)  { }

        bool operator<(const pao_offset_t &rhs) const
        {
            return (paobjectid == rhs.paobjectid)?(offset < rhs.offset):(paobjectid < rhs.paobjectid);
        }
    };

    map< pao_offset_t, long >           _control_offsets;  //  this map contains all control point offsets mapped to point ids
    std::multimap< pao_offset_t, long > _type_offsets;     //  this map contains all point offsets mapped to point ids

    friend class Test_CtiPointManager;

public:

    CtiPointManager();
    virtual ~CtiPointManager();
    virtual void refreshList(BOOL (*fn)(CtiPointBase*,void*) = isPoint, void *d = NULL, LONG pntID = 0, LONG paoID = 0);

    virtual void removeSinglePoint(ptr_type pTempCtiPoint);

    virtual void DumpList(void);
    virtual void DeleteList(void);

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);
    ptr_type find(bool (*findFun)(const long, const ptr_type &, void*), void* d);
    ptr_type getControlOffsetEqual(LONG pao, INT Offset);
    ptr_type getOffsetTypeEqual(LONG pao, INT Offset, CtiPointType_t Type);
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

struct PointDeviceMapping
{
    std::map<long, long> point_device_map;
    typedef CtiLockGuard<CtiMutex>    LockGuard;
    CtiMutex mux;
};

class Test_CtiPointManager : public CtiPointManager
{
public:
    void addPoint(CtiPointBase *point)  {  ((CtiPointManager *)this)->addPoint(point);  }
};

#endif                  // #ifndef __MGR_POINT_H__
