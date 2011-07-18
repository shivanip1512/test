#include "precompiled.h"


/*-----------------------------------------------------------------------------*
*
* File:   mc_scheduler
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_scheduler.cpp-arc  $
* REVISION     :  $Revision: 1.14.4.1 $
* DATE         :  $Date: 2008/11/21 20:56:59 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "mc_scheduler.h"
using namespace std;

static const char* AbsoluteTimeStopPolicy;
static const char* DurationStopPolicy;
static const char* UntilCompleteStopPolicy;
static const char* ManualStopPolicy;

// CtiTime is invalid when seconds are set to 0
CtiTime CtiMCScheduler::_invalid_time((unsigned long) 0);

void CtiMCScheduler::initEvents(const CtiTime& now)
{
    CtiLockGuard<CtiMutex> guard(_schedule_manager.getMux() );

    CtiRTDB< CtiMCSchedule >::MapIterator itr = _schedule_manager.getMap().begin();
    CtiMCSchedule* sched;

    for(; itr != _schedule_manager.getMap().end() ; ++itr)
    {
        sched = (*itr).second;
        initEvents(now,*sched);

    }

}

void CtiMCScheduler::initEvents(const CtiTime& now, CtiMCSchedule& sched)
{
    const string& state = sched.getCurrentState();
    const string& policy = sched.getStartPolicy();

    //Determine whether sched should be running now
    if( state == CtiMCSchedule::Disabled )
        return;

    //automagically restart all running manual start policies
    if( policy == CtiMCSchedule::ManualStartPolicy )
    {
        if( state == CtiMCSchedule::Running )
        {   //CHECK Will a stop event be scheduled in this case? maybe add this
            sched.setCurrentState( CtiMCSchedule::Waiting );
            sched.setManualStartTime( now );
            scheduleManualStart(now, sched);
            return;
        }
        else
        if( state == CtiMCSchedule::Pending )
        {
            CtiTime start;
            if( (start = scheduleManualStart(now,sched)).isValid() )
            {
                if( !scheduleManualStop(start,sched).isValid() )
                {
                    schedulePolicyStop(start,sched);
                }
            }
            return;
            //CHECK maybe have to force back to waiting
        }
    }

    //start any schedule that _should_ have been running now
    sched.setCurrentState(CtiMCSchedule::Waiting);

    CtiDate yesterday(now);
    CtiTime yesterday_time(yesterday);
    CtiTime calc_start = calcPolicyStart(yesterday_time,sched);
    CtiTime calc_stop;

    if( calc_start.isValid() ) {
        /* changed because duration stops were not being restarted */
        calc_stop = calcPolicyStop(calc_start,sched);
    }

    if( calc_start.isValid() && calc_stop.isValid()     &&
        calc_start <= now && now <= calc_stop    )
    {
        CtiTime start;
        if( (start = schedulePolicyStart(yesterday_time,sched)).isValid() )
            schedulePolicyStop(start,sched);
    }
    else //schedule a start/stop normally
    {
        CtiTime start;
        if( (start = schedulePolicyStart(now, sched)).isValid() )
            schedulePolicyStop(start, sched);

    }
}

/*----------------------------------------------------------------------------
  getEvents

  Inserts events to be processed now into a set of events.

  now    - Time used to determine which events are ready to be processed.
  events - The set that getEvents inserts events to be processed into.
----------------------------------------------------------------------------*/
void CtiMCScheduler::getEvents(const CtiTime& now, set< ScheduledEvent >& events)
{
   {
        std::deque< ScheduledEvent >::iterator iter = _event_deque.begin();
        while( iter != _event_deque.end() )
        {
            if( iter->timestamp <= now )
            {
                ScheduledEvent debug_event = *iter;
                events.insert( *iter );
                iter = _event_deque.erase(iter);
            }
            else
            {
                // _event_deque is sorted so the first miss means the rest will miss
                break;
            }
        }
   }

   // Go through each event, find the schedule, update its state,
   // and add any new events if necessary
   {

        CtiLockGuard<CtiMutex> guard( _schedule_manager.getMux() );
        CtiMCSchedule* schedule = NULL;

        for( std::set< ScheduledEvent >::iterator iter = events.begin();
            iter != events.end();
            iter++ )
        {
            schedule = _schedule_manager.findSchedule( iter->sched_id );

            if( schedule != NULL )
            {
                handleEvent(*iter);
            }
            else
            {
                CtiLockGuard< CtiLogger > logGuard(dout);
                dout << CtiTime() << __FILE__ << " (" << __LINE__
                    << ") WARNING - could not find a schedule matching event schedule id: "
                    << iter->sched_id << endl;
            }
        }
   }
   return;
}

/*----------------------------------------------------------------------------
  removeEvents

  Removes all events related to a schedule.

  schedule_id   - ID of the schedule.
----------------------------------------------------------------------------*/
void CtiMCScheduler::removeEvents(long schedule_id)
{
    std::deque< ScheduledEvent >::iterator iter = _event_deque.begin();
    while( iter != _event_deque.end() )
    {
        if( iter->sched_id == schedule_id )
        {
            if( gMacsDebugLevel & MC_DEBUG_EVENTS )
            {
                CtiLockGuard< CtiLogger > guard(dout);
                dout << CtiTime() << " Removing event from the event queue" << endl;
                dout << CtiTime() << " id:  " << iter->sched_id << " type: "       <<
                        iter->event_type     << " timestamp: " << iter->timestamp << endl;
            }

            iter = _event_deque.erase(iter);
        }
        else
        {
            iter++;
        }
    }
}

CtiTime CtiMCScheduler::scheduleManualStart(const CtiTime& now,
                                           CtiMCSchedule& sched)
{
    ScheduledEvent start_event;
    ScheduledEvent pending_event;

    start_event.event_type = StartSchedule;
    start_event.sched_id = sched.getScheduleID();

    pending_event.event_type = StartPending;
    pending_event.sched_id = sched.getScheduleID();


    const string& state = sched.getCurrentState();
    const CtiTime& man_start = sched.getManualStartTime();

    if( man_start.isValid() )
    {
        if ( state == CtiMCSchedule::Waiting || state == CtiMCSchedule::Pending )
        {
            if( man_start > now )
            {
                pending_event.timestamp = now;
                start_event.timestamp = man_start;
            }
            else
            {
                start_event.timestamp = now;
            }

            if( pending_event.timestamp.isValid() ) {
                addEvent(sched, pending_event);
                sched.setCurrentStopTime(_invalid_time);
            }

            if( start_event.timestamp.isValid() ) {
                addEvent(sched, start_event);
                sched.setCurrentStopTime(_invalid_time);
            }

            return start_event.timestamp;
        }
    }

    return _invalid_time;
}

CtiTime CtiMCScheduler::scheduleManualStop(const CtiTime& now,
                          CtiMCSchedule& sched)
{
    ScheduledEvent event;
    const string state = sched.getCurrentState();
    const CtiTime& man_stop = sched.getManualStopTime();

    event.event_type = StopSchedule;
    event.sched_id = sched.getScheduleID();


    if( man_stop.isValid() &&
        ( state == CtiMCSchedule::Running ||
          state == CtiMCSchedule::Pending ||
          state == CtiMCSchedule::Waiting ) )
    {
        if( man_stop < now )
        {
            event.timestamp = now;
        }
        else
        {
            event.timestamp = man_stop;
        }

        sched.setCurrentStopTime( event.timestamp );

        addEvent(sched, event);

        return event.timestamp;
    }
    else
    {
        return _invalid_time;
    }
}

CtiTime CtiMCScheduler::schedulePolicyStart(const CtiTime& now, CtiMCSchedule& sched)
{
    CtiTime start_time = calcPolicyStart(now,sched);

    if( start_time.isValid() )
    {
        ScheduledEvent start_event;
        start_event.sched_id = sched.getScheduleID();
        start_event.event_type = StartSchedule;
        start_event.timestamp = start_time;

        addEvent(sched, start_event);
    }
    return start_time;
}

CtiTime CtiMCScheduler::schedulePolicyStop(const CtiTime& now, CtiMCSchedule& sched)
{
    CtiTime stop_time = calcPolicyStop(now,sched);

    if( stop_time.isValid() )
    {
        ScheduledEvent stop_event;
        stop_event.sched_id = sched.getScheduleID();
        stop_event.event_type = StopSchedule;
        stop_event.timestamp = stop_time;

        addEvent(sched, stop_event);
    }

    return stop_time;
}

CtiTime CtiMCScheduler::scheduleRepeatInterval(const CtiTime& now, const CtiTime& start,
                                  CtiMCSchedule& sched)
{
    long interval = sched.getRepeatInterval();
    ScheduledEvent event;

    if( interval >= 60 )
    {
        event.timestamp =
              now +
            ( interval -
              (now.seconds() - start.seconds()) %
             interval );

        event.sched_id = sched.getScheduleID();
        event.event_type = RepeatInterval;

        addEvent( sched, event );

        return event.timestamp;
    }
    else
    {
        return _invalid_time;
    }
}

void CtiMCScheduler::handleEvent(const ScheduledEvent& event)
{
    {

       CtiLockGuard<CtiMutex> guard( _schedule_manager.getMux() );
       CtiMCSchedule* schedule = _schedule_manager.findSchedule( event.sched_id );

       if( schedule == NULL )
       {
           CtiLockGuard< CtiLogger > logGuard(dout);
           dout << CtiTime() << __FILE__ << " (" << __LINE__
                    << ") WARNING - could not find a schedule matching event schedule id: "
                    << event.sched_id << endl;

           return;
       }

        assert( schedule->getScheduleID() == event.sched_id );

        switch( event.event_type )
        {
        case StartPending:
        //    assert( schedule->getCurrentState() == CtiMCSchedule::Waiting );

            schedule->setCurrentState(CtiMCSchedule::Pending);

            // Add a stop event if necessary to get out of pending
            if( !scheduleManualStop(event.timestamp, *schedule).isValid() )
            {
                schedulePolicyStop( schedule->getCurrentStartTime(), *schedule );
            }

            break;
        case StartSchedule:
         //   assert( schedule->getCurrentState() == CtiMCSchedule::Waiting ||
         //           schedule->getCurrentState() == CtiMCSchedule::Pending );
           {
            schedule->setManualStartTime(_invalid_time);
            schedule->setLastRunTime(event.timestamp);
            schedule->setCurrentStartTime(event.timestamp);
            schedule->setCurrentState(CtiMCSchedule::Running);

            CtiTime now;
            now -= now.second();
            scheduleRepeatInterval( now, event.timestamp, *schedule );

            // add or update a stop event
            if( !scheduleManualStop( event.timestamp, *schedule).isValid() )
            {
                schedulePolicyStop( event.timestamp, *schedule );
            }
           }
            break;
        case RepeatInterval:
            //assert( schedule->getCurrentState() == CtiMCSchedule::Running );
            {
            CtiTime now;
            now -= now.second();

            scheduleRepeatInterval( now, schedule->getCurrentStartTime(), *schedule );
            }
            break;
        case StopSchedule:
           // assert( schedule->getCurrentState() == CtiMCSchedule::Running ||
           //         schedule->getCurrentState() == CtiMCSchedule::Pending );

            schedule->setManualStopTime(_invalid_time);
            schedule->setCurrentStopTime(event.timestamp);

            //in case of a forced disable, don't let the schedule
            //back into a waiting state
            if( schedule->getCurrentState() != CtiMCSchedule::Disabled )
                schedule->setCurrentState(CtiMCSchedule::Waiting);

            //Strip any leftover events, maybe a repeat interval left
            removeEvents(schedule->getScheduleID());

            // Schedule new starts and stops, if there is no valid start
            // then don't bother with figuring out a new stop
            // (it would be based on the old start time anyways)
            {
                // Don't calculate any new events if we are disabled
                if(schedule->getCurrentState() != CtiMCSchedule::Disabled)
                {
                    CtiTime start = schedulePolicyStart(event.timestamp, *schedule );

                    if( start.isValid() )
                        schedulePolicyStop( start, *schedule );
                }
            }

        break;
    default:
        break;
    }
    }

}

void CtiMCScheduler::addEvent(CtiMCSchedule& sched, const ScheduledEvent& event)
{
    // only one event of each type per schedule please
    removeEvents( event.sched_id, event.event_type );

    switch( event.event_type )
    {
    case StartSchedule:
        sched.setCurrentStartTime(event.timestamp);
        break;

    case StopSchedule:
        sched.setCurrentStopTime(event.timestamp);
        break;
    }

    //Only one event should be allowed per schedule with a given timestamp
    // If the new event has higher priority then use that
    // if it has a lower priority then forget it.
    deque< ScheduledEvent >::iterator iter = find( _event_deque.begin(), _event_deque.end(), event );

    if( iter != _event_deque.end() )
    {
        if( *iter > event )
            *iter = event; // The new event has a higher priority, replace the old one
        else
            ;// Found another event with the same timestamp and a higher
             // priority, throw away the new event
    }
    else
    {
        _event_deque.push_back(event);
        sort( _event_deque.begin(), _event_deque.end() );
    }
}

void CtiMCScheduler::removeEvents(long schedule_id, ScheduledEventType type)
{
    deque< ScheduledEvent >::iterator iter = _event_deque.begin();
    while( iter != _event_deque.end() )
    {
        if( iter->sched_id == schedule_id &&
            iter->event_type == type )
        {
            if( gMacsDebugLevel & MC_DEBUG_EVENTS )
            {
                CtiLockGuard< CtiLogger > guard(dout);
                dout << CtiTime() << " Removing event from the event queue" << endl;
                dout << CtiTime() << " id:  " << iter->sched_id << " type: "       <<
                        iter->event_type     << " timestamp: " << iter->timestamp << endl;
            }

            iter = _event_deque.erase(iter);
        }
        else
        {
            iter++;
        }
    }
}


/*
    Determine when the next policy start based on now should be
*/
CtiTime CtiMCScheduler::calcPolicyStart(const CtiTime& now, CtiMCSchedule& sched)
{
    CtiTime start_time(_invalid_time);
    const string& policy = sched.getStartPolicy();

    if( policy == CtiMCSchedule::DateTimeStartPolicy )
    {
        calcDateTimeStart(now,sched,start_time);
    }
    else
    if( policy == CtiMCSchedule::DayOfMonthStartPolicy )
    {
        calcDayOfMonthStart(now,sched,start_time);
    }
    else
    if( policy == CtiMCSchedule::WeekDayTimeStartPolicy )
    {
        calcWeekDayStart(now,sched,start_time);
    }
    else
    if (policy == CtiMCSchedule::ManualStartPolicy )
    {
        ; // by definition no policy start
    }

    return start_time;
}

/*
    Determine when the next policy stop based on now should be
*/
CtiTime CtiMCScheduler::calcPolicyStop(const CtiTime& now, CtiMCSchedule& sched)
{
    CtiTime stop_time(_invalid_time);
    const string& policy = sched.getStopPolicy();

    if( policy == CtiMCSchedule::AbsoluteTimeStopPolicy )
    {
        calcAbsoluteTimeStop(now,sched,stop_time);
    }
    else
    if( policy == CtiMCSchedule::DurationStopPolicy )
    {
        calcDurationStop(now,sched,stop_time);
    }
    else
    if( policy == CtiMCSchedule::UntilCompleteStopPolicy ||
        policy == CtiMCSchedule::ManualStopPolicy )
    {
        ; // nothing to do by definition
    }
    else
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << __FILE__ << " (" << __LINE__
                   << ") WARNING - unknown stop policy: " << policy << endl;
    }

    return stop_time;
}

bool CtiMCScheduler::calcIntervalEvent(const CtiTime& now, const CtiMCSchedule& sched,
                           ScheduledEvent& event) const
{
    long interval = sched.getRepeatInterval();

    if( interval >= 60 )
    {
        event.timestamp =
              now +
            ( interval -
              (now.seconds() - sched.getCurrentStartTime().seconds()) %
             interval );

        event.sched_id = sched.getScheduleID();
        event.event_type = RepeatInterval;

        return true;
    }
    else
    {
        return false;
    }
}

bool CtiMCScheduler::calcStopEvent(const CtiTime& start, const CtiMCSchedule& sched,
                       ScheduledEvent& event) const
{
    ScheduledEvent next_event;
    bool result = false;
    next_event.timestamp = _invalid_time;

    if( sched.getManualStopTime().isValid() )
    {
        next_event.timestamp = sched.getManualStopTime();
        result = true;
    }
    else
    {
        const string& policy = sched.getStartPolicy();
        if( policy == CtiMCSchedule::AbsoluteTimeStopPolicy )
        {
            calcAbsoluteTimeStop(start, sched, next_event.timestamp);
        }
        else
        if( policy == CtiMCSchedule::DurationStopPolicy )
        {
            calcDurationStop(start, sched, next_event.timestamp);
        }
        if( policy == CtiMCSchedule::ManualStopPolicy ||
            policy == CtiMCSchedule::UntilCompleteStopPolicy )
        {
            //ignore
        }
        else
        {
            // something really wrong happened.
            CtiLockGuard< CtiLogger > guard(dout);
            dout << "ERROR:  " << __FILE__ << " (" << __LINE__
                 << ") Unknown schedule stop policy"
                 << endl;
        }

        result = next_event.timestamp.isValid();
    }

    if( result )
    {
        next_event.sched_id = sched.getScheduleID();
        next_event.event_type = StopSchedule;

        event = next_event;
    }

    return result;
}

void CtiMCScheduler::calcDateTimeStart(const CtiTime& now, const CtiMCSchedule& sched,
                           CtiTime& start_time ) const
{
    unsigned hour, minute, second, year, day, month;

    parseTimeString( sched.getStartTime(), hour, minute, second );

    // A specific year
    if( sched.getStartYear() != 0 )
    {
        year = sched.getStartYear();
    }
    else
    {
        year = CtiDate(now).year();
    }

    day   = sched.getStartDay();
    month = sched.getStartMonth();

    CtiDate tempDate(day, month, year);

    CtiTime tempTime(tempDate, hour, minute, second);

    if( tempTime < now && sched.getStartYear() == 0 )
    {
        year++;
        tempDate   = CtiDate(day, month, year);
        start_time = CtiTime(tempDate, hour, minute, second);
    }
    else if( tempTime < now )
    {
        start_time = CtiTime(CtiTime::not_a_time);
    }
    else
    {
        start_time = tempTime;
    }

    return;
}

void CtiMCScheduler::calcDayOfMonthStart(const CtiTime& now, const CtiMCSchedule& sched,
                             CtiTime& start_time ) const
{
    unsigned hour, minute, second;

    parseTimeString( sched.getStartTime(), hour, minute, second );

    CtiDate nowDate = CtiDate(now);
    start_time = CtiTime::CtiTime(nowDate, hour, minute, second);

    int month = nowDate.month();
    int year = nowDate.year();
    int day = std::min((unsigned int)sched.getStartDay(), CtiDate::daysInMonthYear(month, year));

    const int days_remaining = day - start_time.day();

    if( days_remaining > 0 ) {
        // if the scheduled day has not yet happened in this month
        start_time.addDays(days_remaining);
    }
    else
    {
        if( days_remaining != 0 || now > start_time ) {
            // scheduled day is next month
            if( month == 12 )
            {
                month = 1;
                year++;
            }
            else
            {
                month++;
            }

            day = std::min((unsigned int)sched.getStartDay(), CtiDate::daysInMonthYear(month, year));
            start_time = CtiTime(CtiDate::CtiDate(day, month, year), hour, minute, second);
        }
    }
}

void CtiMCScheduler::calcWeekDayStart(const CtiTime& now, const CtiMCSchedule& sched,
                          CtiTime& start_time ) const
{
    CtiHolidayManager& holiday_mgr = CtiHolidayManager::getInstance();
    const string& wdays = sched.getValidWeekDays();
    bool on_holidays = false;

    struct tm start_tm;
    unsigned hour, minute, second;
    CtiTime temp;

    if( wdays.length() < 8 || wdays.find('Y') == string::npos) {
        return;
    }

    on_holidays = ( wdays[7] == 'Y' || wdays[7] == 'y' );

    parseTimeString( sched.getStartTime(), hour, minute, second );

    now.extract(&start_tm);
    start_tm.tm_hour = hour;
    start_tm.tm_min  = minute;
    start_tm.tm_sec  = second;

    temp = CtiTime(&start_tm);
    temp -= 86400;

    int i;
    for( i = start_tm.tm_wday; i <= 6; i++ ) {
        temp += 86400;
        if( temp > now && wdays[i] == 'Y' &&
           (on_holidays ||
            !holiday_mgr.isHoliday( temp.date(), sched.getHolidayScheduleID() ))) {

            start_time = temp;
            return;
        }
    }

    for( i = 0; i <= start_tm.tm_wday; i++ ) {
        temp += 86400;
        if( wdays[i] == 'Y' &&
            (on_holidays ||
            !holiday_mgr.isHoliday( temp.date(), sched.getHolidayScheduleID()))) {

            start_time = temp;
            return;
        }
    }
}

void CtiMCScheduler::calcAbsoluteTimeStop(const CtiTime& start, const CtiMCSchedule& sched,
                              CtiTime& stop_time ) const
{
    struct tm stop_tm;
    unsigned hour, minute, second;

    if( !start.isValid() )
        return;

    start.extract(&stop_tm);
    parseTimeString( sched.getStopTime(), hour, minute, second );
    stop_tm.tm_hour = hour;
    stop_tm.tm_min  = minute;
    stop_tm.tm_sec  = second;

    stop_time = CtiTime(&stop_tm);

    // If the stop time is passed today,
    // consider it tomorrow
    if( stop_time < start )
    {
        stop_time += 86400;
    }

    return;
}

void CtiMCScheduler::calcDurationStop(const CtiTime& start, const CtiMCSchedule& sched,
                          CtiTime& stop_time ) const
{
    long duration = sched.getDuration();
    if( duration >= 60 &&
        duration % 60 == 0 )
    {
        stop_time = start + duration;
    }
}

bool CtiMCScheduler::parseTimeString(   const string& time_str,
                                        unsigned int& hour,
                                        unsigned int& minute,
                                        unsigned int& second) const
{
    try
    {
        string h = time_str.substr( 0, 2 );
        string m = time_str.substr( 3, 2 );
        string s = time_str.substr( 6, 2 );

        hour = atoi(h.c_str());
        minute = atoi(m.c_str());
        second = atoi(s.c_str());
    }
    catch(...)
    {
        return false;
    }
    return true;
}
