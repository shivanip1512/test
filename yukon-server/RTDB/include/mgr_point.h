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
* REVISION     :  $Revision: 1.30 $
* DATE         :  $Date: 2008/10/09 16:11:36 $
*
 * (c) 1999 Cannon Technologies Inc. Wayzata Minnesota
 * All Rights Reserved
 *
 ************************************************************************/
#ifndef __MGR_POINT_H__
#define __MGR_POINT_H__
#pragma warning( disable : 4786 )


#include "dlldefs.h"
#include "smartmap.h"
#include "fifo_multiset.h"
#include "pt_base.h"


class IM_EX_PNTDB CtiPointManager
{
public:

   typedef CtiLockGuard<CtiMutex>    LockGuard;
   typedef CtiSmartMap <CtiPoint>    coll_type;              // This is the collection type!
   typedef coll_type::ptr_type       ptr_type;
   typedef coll_type::spiterator     spiterator;
   typedef coll_type::insert_pair    insert_pair;

   typedef std::map<LONG, CtiPointWPtr> WeakPointMap;

private:

    coll_type      _smartMap;

    struct lru_data_t
    {
        long   pointid;
        time_t access_time;

        lru_data_t(long pointid_, time_t access_time_=time(0)) :
            pointid(pointid_)
        { };

        bool operator<(const lru_data_t &rhs) const  {  return access_time < rhs.access_time;  };
    };

    typedef std::map<time_t, set<long>, std::greater<long> > lru_timeslice_map;  //  the make sure the map is sorted as newest-first (largest timestamps)
    typedef std::map<long, lru_timeslice_map::iterator>      lru_point_lookup_map;
    typedef CtiLockGuard<CtiCriticalSection>                 lru_guard_t;

    CtiCriticalSection   _lru_mux;
    lru_timeslice_map    _lru_timeslices;
    lru_point_lookup_map _lru_points;

    void refreshPoints(bool &rowFound, RWDBReader& rdr);

    void updateAccess(long pointid, time_t time_now=time(0));

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

    virtual void refreshListByIDs(const std::set<long> &ids, bool paoids);

    friend class Test_CtiPointManager;

protected:

    virtual void removePoint(ptr_type pTempCtiPoint, bool isExpiration = false);

    coll_type::lock_t &getLock();

public:

    CtiPointManager();
    virtual ~CtiPointManager();

    virtual void refreshList(LONG pntID = 0, LONG paoID = 0, CtiPointType_t pntType = InvalidPointType);
    virtual void refreshListByPAOIDs  (const std::set<long> &ids);
    virtual void refreshListByPointIDs(const std::set<long> &ids);

    void processExpired();

    virtual void DumpList(void);
    virtual void ClearList(void);

    void     apply(void (*applyFun)(const long, ptr_type, void*), void* d);

    virtual ptr_type getEqual(LONG Pt);
    ptr_type getControlOffsetEqual(LONG pao, INT Offset);
    ptr_type getOffsetTypeEqual(LONG pao, INT Offset, CtiPointType_t Type);
    ptr_type getEqualByName(LONG pao, string pname);
    void     getEqualByPAO(long pao, std::vector<ptr_type> &points);
    long     getPAOIdForPointId(long pointid);

    void erase(long pid, bool isExpiration = false);

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
