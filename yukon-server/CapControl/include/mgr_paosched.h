#pragma once
#pragma warning( disable : 4786)

#include <rw/thr/thread.h>

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
    
    bool checkSchedules(const CtiTime& currentTime, std::list<CtiPAOSchedule*> &schedules);
    void addSchedule(const CtiPAOSchedule &sched);
    bool updateSchedule(const CtiPAOSchedule &sched);
    bool deleteSchedule(long schedId);
    bool findSchedule(long id, CtiPAOSchedule &sched);

    void addEvent(long schedId, const CtiPAOEvent &event);
    bool updateEvent(long eventId, const CtiPAOEvent &event);
    bool deleteEvent(long eventId);
    bool findEvent(long id, CtiPAOEvent &event);
    bool getEventsBySchedId(long id, std::list<CtiPAOEvent*> &events);

    void refreshSchedulesFromDB();
    void refreshEventsFromDB();
    RWRecursiveLock<RWMutexLock> & getMux() { return _mutex; };

    bool isValid();
    void setValid(bool valid);

    void updateRunTimes(CtiPAOSchedule *schedule);
    void updateDataBaseSchedules(std::list<CtiPAOSchedule*> &schedules);
    void runScheduledEvent(CtiPAOEvent *event);
    int parseEvent(const std::string& command, int &strategy, long &secsSinceLastOperation);
    std::string longToString(LONG val);


private:
    CtiPAOScheduleManager();
    ~CtiPAOScheduleManager();


    static CtiPAOScheduleManager* _instance;
    RWThread _scheduleThread;
    RWThread _resetThr;

    std::list <CtiPAOSchedule*> _schedules;
    std::list <CtiPAOEvent*>    _events;

    bool _valid;      
    bool _initialCapControlStartUp;
    mutable RWRecursiveLock<RWMutexLock> _mutex;

};
