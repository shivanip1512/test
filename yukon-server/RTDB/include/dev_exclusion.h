#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <vector>
#include <queue>
#include <boost/shared_ptr.hpp>
#include "boostutil.h"
#include "dlldefs.h"
#include "mutex.h"
#include "tbl_paoexclusion.h"
#include "yukon.h"
#include "ctitime.h"

class CtiDeviceExclusion;

typedef boost::shared_ptr< CtiDeviceExclusion > CtiExclusionSPtr;

class IM_EX_DEVDB CtiDeviceExclusion
{
public:

    typedef std::vector< CtiTablePaoExclusion > exclusions;
    typedef std::vector< std::pair< unsigned long, CtiTime > > prohibitions;

protected:

    LONG _deviceId;

    CtiTablePaoExclusion *_cycleTimeExclusion;          // Used if this device has a time exclusion?

    mutable CtiMutex    _exclusionMux;            // Used when processing the exclusion logic
    exclusions          _exclusionRecords;        // This is the list of database records identifying the exclusions on or against this pao.
    prohibitions        _executionProhibited;     // Device is currently prohibited from executing because of this list of devids.

    CtiTime              _evalNext;                // Device should be looked at again at this time for exclusion purposes
    CtiTime              _executingUntil;          // Device is currently executing until...
    CtiTime              _executeGrantExpires;     // Device is may execute until...
    CtiTime              _executionGrant;      // This is the last time this device was granted execution priviledges.

    unsigned int        _minTimeInSec;            // This is the minimum time to allow in isTimeSlotOpen;

private:

public:

    CtiDeviceExclusion(LONG id = 0);

    CtiDeviceExclusion(const CtiDeviceExclusion& aRef);

    virtual ~CtiDeviceExclusion();

    CtiDeviceExclusion& operator=(const CtiDeviceExclusion& aRef);


    LONG getId() const;
    CtiDeviceExclusion& setId(LONG id);

    CtiMutex& getExclusionMux();
    bool hasExclusions() const;
    exclusions getExclusions() const;
    void addExclusion(CtiTablePaoExclusion &paox);
    void clearExclusions();
    bool isDeviceExcluded(long id) const;
    bool isExecutionProhibited(const CtiTime &now = CtiTime(), LONG did = 0);

    size_t setExecutionProhibited(unsigned long id, CtiTime& releaseTime = CtiTime(YUKONEOT));
    bool removeInfiniteProhibit(unsigned long id);
    bool removeProhibit(unsigned long id);
    void dumpProhibits(unsigned long id = 0);
    bool hasTimeExclusion() const;


    bool   isExecuting() const;
    void   setExecuting(bool set = true, CtiTime when = CtiTime(YUKONEOT));
    CtiTime getExecutingUntil() const;

    void Dump(void) const;

    CtiTime getEvaluateNextAt() const;
    void setEvaluateNextAt(CtiTime set);
    CtiTime getExecutionGrantExpires() const;
    void setExecutionGrantExpires(CtiTime set);
    CtiTime getExecutionGrant() const;
    void setExecutionGrant(CtiTime set);

    bool isTimeExclusionOpen() const;          // This device has no time slot, or no is in the timeslot.
    bool proximityExcludes(LONG id) const;

    CtiTime getTimeSlotOpen() const;
    CtiTime getNextTimeSlotOpen() const;
    CtiTime getTimeSlotClose() const;
    CtiTablePaoExclusion getCycleTimeExclusion() const;

    unsigned int getMinTimeInSec() const;
    void setMinTimeInSec(unsigned int sec);

};
