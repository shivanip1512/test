
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mc_scheduler
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc_scheduler.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*-----------------------------------------------------------------------------
    Filename:  mc_scheduler.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiMCScheduler
                    CtiMCScheduler keeps track of macs scheduled events
                    and contains most* of the logic as to when/how scheduled
                    events will happen.

                    It relies on state info in CtiMCSchedule and there
                    their details are married enough to cause side effects.
                    Modifying this class can be a bit like riding a snake.

    Initial Date:  2/20/01

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#ifndef __MC_SCHEDULER_H__
#define __MC_SCHEDULER_H__

#pragma warning( disable : 4786)

#include <deque>
#include <list>

#include <rw/rwtime.h>

#include "mc.h"
#include "mc_sched.h"
#include "mgr_mcsched.h"
#include "mgr_holiday.h"

using namespace std;

enum ScheduledEventType
{
    StopSchedule,
    StartSchedule,
    RepeatInterval,
    StartPending
};

struct ScheduledEvent
{
    long sched_id;
    ScheduledEventType event_type;
    RWTime timestamp;

    ScheduledEvent() : timestamp(RWTime((unsigned long)0))
    { }
    // These will need to be sorted
    bool operator==(const ScheduledEvent& event) const
    {
        return (timestamp == event.timestamp &&
                sched_id == event.sched_id );
    }

    bool operator<(const ScheduledEvent& event) const
    {       
        return ( timestamp < event.timestamp ||
               ( timestamp == event.timestamp && event_type < event.event_type ) ||
               ( timestamp == event.timestamp && event_type == event.event_type && sched_id < event.sched_id ));
    }

    bool operator>(const ScheduledEvent& event) const
    {
        return !( *this < event );
    }
};


class CtiMCScheduler
{
public:

    CtiMCScheduler(CtiMCScheduleManager& mgr) : _schedule_manager(mgr) { }
    ~CtiMCScheduler() { }
    
    RWTime scheduleManualStart(const RWTime& now, CtiMCSchedule& sched);
    RWTime scheduleManualStop(const RWTime& now,  CtiMCSchedule& sched);
    RWTime schedulePolicyStart(const RWTime& now, CtiMCSchedule& sched);
    RWTime schedulePolicyStop(const RWTime& start, CtiMCSchedule& sched);
    RWTime scheduleRepeatInterval(const RWTime& now, const RWTime& start,
                                  CtiMCSchedule& sched);

    void getEvents(const RWTime& now, set< ScheduledEvent >& events);


    void initEvents(const RWTime& now);
    void initEvents(const RWTime& now, CtiMCSchedule& schedule);  
    void removeEvents(long schedule_id);

   RWTime getNextEventTime() const
   {
       return   ( _event_deque.size() > 0  ?
                  _event_deque.front().timestamp :
                  _invalid_time );
   }

   void dumpEventQueue() const
    {
        CtiLockGuard<CtiLogger> guard(dout);
        dout << RWTime() << " Dumping macs event queue" << endl;

        for( int i = 0; i < _event_deque.size(); i++ )
        {
            dout << RWTime() << " id:   " << _event_deque[i].sched_id << endl;
            dout << RWTime() << " type:  " << _event_deque[i].event_type << endl;
            dout << RWTime() << " timestamp:  " << _event_deque[i].timestamp << endl;
        }
}

private:

    CtiMCScheduleManager& _schedule_manager;

    // use this for an invalid time
    static RWTime _invalid_time;

    deque< ScheduledEvent > _event_deque;

    void handleEvent(const ScheduledEvent& event);

    void addEvent(CtiMCSchedule& sched, const ScheduledEvent& event);   
    void removeEvents(long schedule_id, ScheduledEventType type);

    RWTime calcPolicyStart(const RWTime& now, CtiMCSchedule& sched);
    RWTime calcPolicyStop(const RWTime& now, CtiMCSchedule& sched);

    bool calcIntervalEvent(const RWTime& now, const CtiMCSchedule& sched,
                           ScheduledEvent& event) const;

    bool calcStopEvent(const RWTime& start, const CtiMCSchedule& sched,
                       ScheduledEvent& event) const;

    void calcDateTimeStart(const RWTime& now, const CtiMCSchedule& sched,
                           RWTime& start_time ) const;

    void calcDayOfMonthStart(const RWTime& now, const CtiMCSchedule& sched,
                             RWTime& start_time ) const;

    void calcWeekDayStart(const RWTime& now, const CtiMCSchedule& sched,
                          RWTime& start_time ) const;

    void calcAbsoluteTimeStop(const RWTime& start, const CtiMCSchedule& sched,
                              RWTime& stop_time ) const;

    void calcDurationStop(const RWTime& start, const CtiMCSchedule& sched,
                          RWTime& stop_time ) const;



   
    // turns string in 'HH:MM:SS'
    // into the hour, minute, second components
    bool parseTimeString(const string& time_str, unsigned int& hour,
                         unsigned int& minute, unsigned int& second) const;


    bool isToday(const RWTime& t) const
    {
        return ( RWDate(t).day() == RWDate::now().day() );
    }

    bool inInterval(const RWTime& t, const pair< RWTime, RWTime >& interval) const
    {
        return ( interval.first <= t && interval.second > t );
    }
};

#endif
