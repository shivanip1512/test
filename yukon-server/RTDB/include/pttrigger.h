#pragma once

#include "yukon.h"
#include <boost/shared_ptr.hpp>
#include "boostutil.h"

#include "logger.h"
#include "mgr_point.h"
#include "tbl_pt_trigger.h"

typedef struct
{
    CtiTablePointTrigger dbTriggerData;
    DOUBLE lastTriggerValue;
} PtVerifyTrigger;
typedef boost::shared_ptr< PtVerifyTrigger > PtVerifyTriggerSPtr;

class IM_EX_PNTDB CtiPointTriggerManager
{
public:

    typedef CtiLockGuard<CtiMutex>         LockGuard;
    typedef std::map<long, PtVerifyTriggerSPtr> coll_type;              // This is the collection type!
    typedef std::map<long, coll_type>           trig_coll_type;
    typedef coll_type::iterator            spiterator;

private:

    coll_type _verificationIDMap;
    coll_type _pointIDMap;
    trig_coll_type _triggerIDMap;
    mutable CtiMutex _mapMux;

    void refreshTriggerData(long pointID, Cti::RowReader& rdr, CtiPointManager &pointMgr);

public:

    CtiPointTriggerManager();
    ~CtiPointTriggerManager();
    CtiPointTriggerManager& operator=(const CtiPointTriggerManager &aRef);

    void refreshList(long ptID, CtiPointManager &pointMgr);

    void erase(long ptID);

    bool isATriggerPoint     (long pointID) const;
    bool isAVerificationPoint(long pointID) const;

    coll_type*          getPointIteratorFromTrigger      (long triggerID);
    PtVerifyTriggerSPtr getPointTriggerFromPoint         (long pointID);
    PtVerifyTriggerSPtr getPointTriggerFromVerificationID(long pointID);

    CtiMutex& getMux();
};
