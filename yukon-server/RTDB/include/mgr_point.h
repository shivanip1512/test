#pragma once

#include "pt_base.h"
#include "dlldefs.h"
#include "smartmap.h"


class IM_EX_PNTDB CtiPointManager
{
public:

   typedef CtiLockGuard<CtiMutex>       LockGuard;
   typedef CtiSmartMap <CtiPointBase>   coll_type;              // This is the collection type!
   typedef coll_type::ptr_type          ptr_type;
   typedef coll_type::spiterator        spiterator;
   typedef coll_type::insert_pair       insert_pair;

   typedef std::map<LONG, CtiPointWPtr> WeakPointMap;

private:

    coll_type      _smartMap;

    using lru_timeslice_map     = std::map<time_t, std::set<long>, std::greater<time_t> >;  //  the make sure the map is sorted as newest-first (largest timestamps)
    using pointid_timeslice_map = std::map<long, time_t>;
    using lru_guard_t = CtiLockGuard<CtiCriticalSection>;

    CtiCriticalSection    _lru_mux;
    lru_timeslice_map     _lru_timeslices;
    pointid_timeslice_map _lru_points;

    void updateAccess(long pointid);

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

    bool _all_paoids_loaded = false;
    std::set<long> _paoids_loaded;

    virtual void refreshListByIDs(const std::set<long> &ids, bool paoids);

    void loadPao(const long paoId, const Cti::CallSite cs);

    void removePoint(ptr_type pTempCtiPoint);

protected:
    //  returns true if it encounters any reader errors occur during point loading
    bool refreshPoints(std::set<long> &pointIdsFound, Cti::RowReader& rdr);


    //ONLY used by unit test.
    void setAllPointsLoaded(bool isLoaded) { _all_paoids_loaded = isLoaded; }
    void addPoint(CtiPointBase *point);

    virtual time_t currentTime() { return time(nullptr); };
    virtual int maxPointsAllowed();

    coll_type::lock_t &getLock();
    coll_type::lock_t &getLock() const;

public:

    virtual ~CtiPointManager() = default;

    virtual std::set<long> refreshList(LONG pntID = 0, LONG paoID = 0, CtiPointType_t pntType = InvalidPointType);
    virtual void refreshListByPAOIDs  (const std::set<long> &ids);
    virtual void refreshListByPointIDs(const std::set<long> &ids);

    void processExpired();

    virtual void ClearList(void);

    void     apply(void (*applyFun)(const long, ptr_type, void*), void* d);

    virtual ptr_type getPoint(LONG Pt, LONG pao = 0);
    ptr_type getControlOffsetEqual(LONG pao, INT Offset);
    virtual ptr_type getOffsetTypeEqual(LONG pao, INT Offset, CtiPointType_t Type);
    ptr_type getEqualByName(LONG pao, std::string pname);
    void     getEqualByPAO(long pao, std::vector<ptr_type> &points);
    long     getPAOIdForPointId(long pointid);
    boost::optional<long> getIdForOffsetAndType(long pao, int offset, CtiPointType_t Type);

    virtual void expire (long pid);
    virtual void erase  (long pid);
    virtual void refresh(long pid);

    virtual void erasePao( long paoId );

    spiterator begin();
    spiterator end();

    size_t entries();
};

