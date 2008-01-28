/*-----------------------------------------------------------------------------*
*
* File:   signalmanager
*
* Class:  CtiSignalManager
* Date:   8/13/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2008/01/28 16:44:47 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __SIGNALMANAGER_H__
#define __SIGNALMANAGER_H__

#include <map>
#include <set>
#include <utility>
using std::map;
using std::pair;

#include "dlldefs.h"
#include "msg_signal.h"
#include "msg_multi.h"
#include "mutex.h"

#define SIGNAL_MANAGER_MASK (MASK_ANY_ALARM|TAG_ACTIVE_CONDITION)

class IM_EX_CTIVANGOGH CtiSignalManager
{
public:

    typedef map< pair< long, int >, CtiSignalMsg* > SigMgrMap_t;
    typedef std::multimap< long, CtiSignalMsg* > PointSignalMap_t; // Calls that target a point need to be fast

protected:

    SigMgrMap_t _map;
    PointSignalMap_t _pointMap;
    std::set<long> _dirtySignals; //Managed by the setDirty call, used by writeDynamicSignalsToDB
    bool _dirty;
    mutable CtiMutex _mux;

private:
    void removeFromMaps(long pointID, int condition);
public:

    CtiSignalManager();

    CtiSignalManager(const CtiSignalManager& aRef);

    virtual ~CtiSignalManager();

    CtiSignalManager& operator=(const CtiSignalManager& aRef);

    CtiSignalManager& addSignal(const CtiSignalMsg &sig, bool dontMarkDirty = false);                   // The manager adds an active and unacknowledged alarm on this condition for this point.

    CtiSignalMsg* setAlarmActive(long pointid, int alarm_condition, bool active = true);
    CtiSignalMsg* setAlarmAcknowledged(long pointid, int alarm_condition, bool acked = true);
    CtiSignalMsg* clearAlarms(long pointid);

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
#endif // #ifndef __SIGNALMANAGER_H__
