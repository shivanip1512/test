#pragma once

#include <map>
#include <set>
#include <utility>

#include "dlldefs.h"
#include "msg_signal.h"
#include "msg_multi.h"
#include "mutex.h"

#define SIGNAL_MANAGER_MASK (MASK_ANY_ALARM|TAG_ACTIVE_CONDITION)

class IM_EX_CTIVANGOGH CtiSignalManager : private boost::noncopyable
{
public:

    typedef std::map< std::pair< long, int >, CtiSignalMsg* > SigMgrMap_t;
    typedef std::multimap< long, CtiSignalMsg* > PointSignalMap_t; // Calls that target a point need to be fast

protected:

    SigMgrMap_t _map;
    PointSignalMap_t _pointMap;
    std::set<long> _dirtySignals; //Managed by the setDirty call, used by writeDynamicSignalsToDB
    bool _dirty;
    mutable CtiMutex _mux;

    virtual void deleteDynamicPointAlarming(long pointID, int condition);

private:
    void removeFromMaps(long pointID, int condition);
public:

    CtiSignalManager();
    virtual ~CtiSignalManager();

    CtiSignalManager& addSignal(const CtiSignalMsg &sig, bool markDirty = true);                   // The manager adds an active and unacknowledged alarm on this condition for this point.

    CtiSignalMsg* setAlarmActive(long pointid, int alarm_condition, bool active = true);
    CtiSignalMsg* setAlarmAcknowledged(long pointid, int alarm_condition, bool acked = true);
    CtiSignalMsg* clearAlarms(long pointid);

    bool isAlarmedOrConditionActive(long pointid, int alarm_condition) const;// The manager has an active "condition" on this point, it may or may not have an active alarm.
    bool isAlarmed(long pointid, int alarm_condition) const;                // The manager has an active and/or unacknowledged alarm on this condition for this point.
    bool isAlarmActive(long pointid, int alarm_condition) const;            // The manager has an active alarm on this condition for this point.  It could be acknowledged or otherwise
    bool isAlarmUnacknowledged(long pointid, int alarm_condition) const;    // The manager has an unacknowledged alarm on this condition for this point.  It could be active or otherwise

    UINT getTagMask(long pointid) const;                                  // Returns the bitwise OR of all alarms on this point
    UINT getConditionTags(long pointid, int alarm_condition) const;         // Returns the bits of the point tag representing this condition MASK_ANY_ALARM

    CtiSignalMsg* getAlarm(long pointid, int alarm_condition) const;        // Returns a copy of the alarm for this pointid and alarmcondition.  Could return NULL
    CtiMultiMsg* getPointSignals(long pointid) const;
    CtiMultiMsg* getAllAlarmSignals() const;
    CtiMultiMsg* getCategorySignals(unsigned category) const;

    size_t entries() const;
    size_t pointMapEntries() const;
    bool empty() const;

    UINT writeDynamicSignalsToDB();

    bool dirty() const;
    void setDirty(bool set, long paoID);
};
