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
* REVISION     :  $Revision: 1.24 $
* DATE         :  $Date: 2008/07/17 20:26:39 $
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

    void addPoint(CtiPointBase *point);  //  also used by the unit test
    void updatePointMaps(const CtiPointBase &point, long old_pao, CtiPointType_t old_type, int old_offset, int old_control_offset );

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

    std::map     < pao_offset_t, long >  _control_offsets;  //  this map contains all control point offsets
    std::multimap< pao_offset_t, long >  _type_offsets;     //  this map contains all point offsets
    std::multimap< long, long >          _pao_pointids;     //  this map contains the loaded pointids that belong to each pao

    bool _all_paoids_loaded;
    std::set<long> _paoids_loaded;

    friend class Test_CtiPointManager;

protected:

    virtual void removePoint(ptr_type pTempCtiPoint);
    void refreshAlarming(LONG pntID = 0, LONG paoID = 0);

    coll_type::lock_t &getLock();

public:

    CtiPointManager();
    virtual ~CtiPointManager();

    virtual void refreshListByPAO(const std::vector<long> &paoids, BOOL (*fn)(CtiPointBase*,void*) = isPoint, void *d = NULL);
    virtual void refreshList(BOOL (*fn)(CtiPointBase*,void*) = isPoint, void *d = NULL, LONG pntID = 0, LONG paoID = 0, CtiPointType_t pntType = InvalidPointType);

    virtual void DumpList(void);
    virtual void ClearList(void);

    void apply(void (*applyFun)(const long, ptr_type, void*), void* d);
    ptr_type find(bool (*findFun)(const long, const ptr_type &, void*), void* d);
    ptr_type getControlOffsetEqual(LONG pao, INT Offset);
    ptr_type getOffsetTypeEqual(LONG pao, INT Offset, CtiPointType_t Type);
    ptr_type getEqual(LONG Pt);
    ptr_type getEqualByName(LONG pao, string pname);
    void     getEqualByPAO(long pao, std::vector<ptr_type> &points);
    long     getPAOIdForPointId(long pointid);

    bool orphan(long pid);

    spiterator begin();
    spiterator end();

    size_t entries();
};

class Test_CtiPointManager : public CtiPointManager
{
public:
    void addPoint(CtiPointBase *point)  {  ((CtiPointManager *)this)->addPoint(point);  }
    void updatePointMaps(const CtiPointBase &point, long old_pao, CtiPointType_t old_type, int old_offset, int old_control_offset )  {  ((CtiPointManager *)this)->updatePointMaps(point, old_pao, old_type, old_offset, old_control_offset);  }
};

#endif                  // #ifndef __MGR_POINT_H__
