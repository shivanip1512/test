
/*-----------------------------------------------------------------------------*
*
* File:   mgr_paosched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mgr_mcsched.h-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2008/03/11 16:43:17 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#ifndef __MGR_PAOSCHED_H__
#define __MGR_PAOSCHED_H__

#include <list>
using std::list;

#include "pao_schedule.h"
#include "pao_event.h"


class CtiPAOScheduleManager 
{
public:

    typedef enum 
    {
        AllBanks = 0,
        FailedAndQuestionableBanks,
        FailedBanks,
        QuestionableBanks,
        SelectedForVerificationBanks,
        BanksInactiveForXTime,
        StandAloneBanks
    } CtiVerificationStrategy;

    typedef enum
    {
        CapControlVerification = 0,
        ConfirmSub = 1,
        SendTimeSync = 2,
        SomethingElse
    } CtiPAOScheduleOperation;

    static CtiPAOScheduleManager* getInstance();

    void start();
    void stop();

    void mainLoop();
    void doResetThr();
    
    bool checkSchedules(const CtiTime& currentTime, list<CtiPAOSchedule*> &schedules);
    void addSchedule(const CtiPAOSchedule &sched);
    bool updateSchedule(const CtiPAOSchedule &sched);
    bool deleteSchedule(long schedId);
    bool findSchedule(long id, CtiPAOSchedule &sched);

    void addEvent(long schedId, const CtiPAOEvent &event);
    bool updateEvent(long eventId, const CtiPAOEvent &event);
    bool deleteEvent(long eventId);
    bool findEvent(long id, CtiPAOEvent &event);
    bool getEventsBySchedId(long id, list<CtiPAOEvent*> &events);

    void refreshSchedulesFromDB();
    void refreshEventsFromDB();
    RWRecursiveLock<RWMutexLock> & getMux() { return _mutex; };

    bool isValid();
    void setValid(bool valid);

    void updateRunTimes(CtiPAOSchedule *schedule);
    void updateDataBaseSchedules(list<CtiPAOSchedule*> &schedules);
    void runScheduledEvent(CtiPAOEvent *event);
    int parseEvent(const string& command, int &strategy, long &secsSinceLastOperation);
    string longToString(LONG val);


private:
    CtiPAOScheduleManager();
    ~CtiPAOScheduleManager();


    static CtiPAOScheduleManager* _instance;
    RWThread _scheduleThread;
    RWThread _resetThr;

    list <CtiPAOSchedule*> _schedules;
    list <CtiPAOEvent*>    _events;

    bool _valid;      
    bool _initialCapControlStartUp;
    mutable RWRecursiveLock<RWMutexLock> _mutex;

};


#endif
