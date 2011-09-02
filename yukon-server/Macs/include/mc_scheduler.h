#pragma once

#include <deque>
#include <list>
         
#include "ctitime.h"

#include "mc.h"
#include "mc_sched.h"
#include "mgr_mcsched.h"
#include "mgr_holiday.h"

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
    CtiTime timestamp;

    ScheduledEvent() : timestamp(CtiTime((unsigned long)0))
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

    friend class Test_CtiMCScheduler;

    CtiMCScheduler(CtiMCScheduleManager& mgr) : _schedule_manager(mgr) { }
    ~CtiMCScheduler() { }
    
    CtiTime scheduleManualStart(const CtiTime& now, CtiMCSchedule& sched);
    CtiTime scheduleManualStop(const CtiTime& now,  CtiMCSchedule& sched);
    CtiTime schedulePolicyStart(const CtiTime& now, CtiMCSchedule& sched);
    CtiTime schedulePolicyStop(const CtiTime& start, CtiMCSchedule& sched);
    CtiTime scheduleRepeatInterval(const CtiTime& now, const CtiTime& start,
                                  CtiMCSchedule& sched);

    void getEvents(const CtiTime& now, std::set< ScheduledEvent >& events);


    void initEvents(const CtiTime& now);
    void initEvents(const CtiTime& now, CtiMCSchedule& schedule);  
    void removeEvents(long schedule_id);

   CtiTime getNextEventTime() const
   {
       return   ( _event_deque.size() > 0  ?
                  _event_deque.front().timestamp :
                  _invalid_time );
   }

   void dumpEventQueue() const
    {
        CtiLockGuard<CtiLogger> guard(dout);
        dout << CtiTime() << " Dumping macs event queue" << std::endl;

        for( int i = 0; i < _event_deque.size(); i++ )
        {
            dout << CtiTime() << " id:   " << _event_deque[i].sched_id << std::endl;
            dout << CtiTime() << " type:  " << _event_deque[i].event_type << std::endl;
            dout << CtiTime() << " timestamp:  " << _event_deque[i].timestamp << std::endl;
        }
}

private:

    CtiMCScheduleManager& _schedule_manager;

    // use this for an invalid time
    static CtiTime _invalid_time;

    std::deque< ScheduledEvent > _event_deque;

    void handleEvent(const ScheduledEvent& event);

    void addEvent(CtiMCSchedule& sched, const ScheduledEvent& event);   
    void removeEvents(long schedule_id, ScheduledEventType type);

    CtiTime calcPolicyStart(const CtiTime& now, CtiMCSchedule& sched);
    CtiTime calcPolicyStop(const CtiTime& now, CtiMCSchedule& sched);

    bool calcIntervalEvent(const CtiTime& now, const CtiMCSchedule& sched,
                           ScheduledEvent& event) const;

    bool calcStopEvent(const CtiTime& start, const CtiMCSchedule& sched,
                       ScheduledEvent& event) const;

    void calcDateTimeStart(const CtiTime& now, const CtiMCSchedule& sched,
                           CtiTime& start_time ) const;

    void calcDayOfMonthStart(const CtiTime& now, const CtiMCSchedule& sched,
                             CtiTime& start_time ) const;

    void calcWeekDayStart(const CtiTime& now, const CtiMCSchedule& sched,
                          CtiTime& start_time ) const;

    void calcAbsoluteTimeStop(const CtiTime& start, const CtiMCSchedule& sched,
                              CtiTime& stop_time ) const;

    void calcDurationStop(const CtiTime& start, const CtiMCSchedule& sched,
                          CtiTime& stop_time ) const;



   
    // turns string in 'HH:MM:SS'
    // into the hour, minute, second components
    bool parseTimeString(const std::string& time_str, unsigned int& hour,
                         unsigned int& minute, unsigned int& second) const;


    bool isToday(const CtiTime& t) const
    {
        return ( CtiDate(t).day() == CtiDate::now().day() );
    }

    bool inInterval(const CtiTime& t, const std::pair< CtiTime, CtiTime >& interval) const
    {
        return ( interval.first <= t && interval.second > t );
    }
};

class Test_CtiMCScheduler : public CtiMCScheduler
{
public:
    Test_CtiMCScheduler(CtiMCScheduleManager& mgr) : Inherited(mgr){ }
    typedef CtiMCScheduler Inherited;
    void testCalcDateTimeStart(const CtiTime& now, const CtiMCSchedule& sched,
                           CtiTime& start_time ) const
    {
        Inherited::calcDateTimeStart(now, sched, start_time);
    }
    void testCalcDayOfMonthStart(const CtiTime& now, const CtiMCSchedule& sched,
                           CtiTime& start_time ) const
    {
        Inherited::calcDayOfMonthStart(now, sched, start_time);
    }
};
