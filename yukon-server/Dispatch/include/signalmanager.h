
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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/08/19 13:47:05 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __SIGNALMANAGER_H__
#define __SIGNALMANAGER_H__

#include <map>
#include <utility>
using namespace std;

#include "dlldefs.h"
#include "msg_signal.h"
#include "msg_multi.h"

class IM_EX_CTIVANGOGH CtiSignalManager
{
public:

    typedef map< pair< long, int >, CtiSignalMsg* > SigMgrMap_t;

protected:

    SigMgrMap_t _map;
    bool _dirty;

private:

public:

    CtiSignalManager();

    CtiSignalManager(const CtiSignalManager& aRef);

    virtual ~CtiSignalManager();

    CtiSignalManager& operator=(const CtiSignalManager& aRef);

    CtiSignalManager& addSignal(const CtiSignalMsg &sig);                   // The manager adds an active and unacknowledged alarm on this condition for this point.

    CtiSignalMsg* setAlarmActive(long pointid, int alarm_condition, bool active = true);
    CtiSignalMsg* setAlarmAcknowledged(long pointid, int alarm_condition, bool acked = true);

    bool isAlarmed(long pointid, int alarm_condition) const;                // The manager has an active and/or unacknowledged alarm on this condition for this point.
    bool isAlarmActive(long pointid, int alarm_condition) const;            // The manager has an active alarm on this condition for this point.  It could be acknowledged or otherwise
    bool isAlarmUnacknowledged(long pointid, int alarm_condition) const;    // The manager has an unacknowledged alarm on this condition for this point.  It could be active or otherwise

    UINT getAlarmMask(long pointid) const;                                  // Returns the bitwise OR of all alarms on this point
    UINT getConditionTags(long pointid, int alarm_condition) const;         // Returns the bits of the point tag representing this condition MASK_ANY_ALARM

    CtiSignalMsg* getAlarm(long pointid, int alarm_condition) const;        // Returns a copy of the alarm for this pointid and alarmcondition.  Could return NULL
    CtiMultiMsg* getPointSignals(long pointid) const;

    size_t entries() const;
    bool empty() const;

    UINT writeDynamicSignalsToDB();

    bool dirty() const;
    void setDirty(bool set = true);
};
#endif // #ifndef __SIGNALMANAGER_H__
