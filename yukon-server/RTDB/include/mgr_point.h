#pragma once

#include "dlldefs.h"
#include "smartmap.h"
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

    typedef std::map<time_t, std::set<long>, std::greater<time_t> > lru_timeslice_map;  //  the make sure the map is sorted as newest-first (largest timestamps)
    typedef std::map<long, lru_timeslice_map::iterator>      lru_point_lookup_map;
    typedef CtiLockGuard<CtiCriticalSection>                 lru_guard_t;

    CtiCriticalSection   _lru_mux;
    lru_timeslice_map    _lru_timeslices;
    lru_point_lookup_map _lru_points;

    void refreshPoints(std::set<long> &pointIdsFound, Cti::RowReader& rdr);

    void updateAccess(long pointid, time_t time_now=time(0));

    void addPoint(CtiPointBase *point);  //  also used by the unit test

    //ONLY used by unit test.
    void setAllPointsLoaded(bool isLoaded) { _all_paoids_loaded = isLoaded; }

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

    void removePoint(ptr_type pTempCtiPoint);

protected:

    coll_type::lock_t &getLock();
    coll_type::lock_t &getLock() const;

public:

    CtiPointManager();
    virtual ~CtiPointManager();

    virtual std::set<long> refreshList(LONG pntID = 0, LONG paoID = 0, CtiPointType_t pntType = InvalidPointType);
    virtual void refreshListByPAOIDs  (const std::set<long> &ids);
    virtual void refreshListByPointIDs(const std::set<long> &ids);

    void processExpired();

    virtual void DumpList(void);
    virtual void ClearList(void);

    void     apply(void (*applyFun)(const long, ptr_type, void*), void* d);

    virtual ptr_type getPoint(LONG Pt, LONG pao = 0);
    ptr_type getControlOffsetEqual(LONG pao, INT Offset);
    ptr_type getOffsetTypeEqual(LONG pao, INT Offset, CtiPointType_t Type);
    ptr_type getEqualByName(LONG pao, std::string pname);
    void     getEqualByPAO(long pao, std::vector<ptr_type> &points);
    long     getPAOIdForPointId(long pointid);

    virtual void expire (long pid);
    virtual void erase  (long pid);
    virtual void refresh(long pid);

    spiterator begin();
    spiterator end();

    size_t entries();
};

class Test_CtiPointManager : public CtiPointManager
{
public:
    Test_CtiPointManager()
    {
        setAllPointsLoaded(true);
    }

    void addPoint(CtiPointBase *point)  {  ((CtiPointManager *)this)->addPoint(point);  }
};

