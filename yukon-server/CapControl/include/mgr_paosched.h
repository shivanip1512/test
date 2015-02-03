#pragma once

#include "critical_section.h"

#include <boost/thread.hpp>

class CtiTime;
class CtiPAOSchedule;
class CtiPAOEvent;


class CtiPAOScheduleManager
{
public:

    enum VerificationStrategy
    {
        Undefined = -1,
        AllBanks = 56,
        FailedAndQuestionableBanks,
        FailedBanks,
        QuestionableBanks,
        BanksInactiveForXTime,
        StandAloneBanks,
        SelectedForVerificationBanks = 64
    };

    enum CtiPAOScheduleOperation
    {
        CapControlVerification = 0,
        ConfirmSubstationBus = 1,
        SendTimeSync = 2,
        SomethingElse
    };

    static CtiPAOScheduleManager* getInstance();

    void start();
    void stop();

    bool isValid();
    void setValid(bool valid);

private:

    void mainLoop();
    void doResetThr();

    bool checkSchedules(const CtiTime& currentTime, std::list<CtiPAOSchedule*> &schedules);
    bool getEventsBySchedId(long id, std::list<CtiPAOEvent*> &events);

    void refreshSchedulesFromDB();
    void refreshEventsFromDB();

    void updateRunTimes(CtiPAOSchedule *schedule);
    void updateDataBaseSchedules(std::list<CtiPAOSchedule*> &schedules);
    void runScheduledEvent(CtiPAOEvent *event);
    int parseEvent(const std::string& command, int &strategy, long &secsSinceLastOperation);

    CtiPAOScheduleManager();
    ~CtiPAOScheduleManager();

    static CtiPAOScheduleManager* _instance;

    boost::thread   _scheduleThr;
    boost::thread   _resetThr;

    std::list <CtiPAOSchedule*> _schedules;
    std::list <CtiPAOEvent*>    _events;

    bool _valid;
    bool _initialCapControlStartUp;

    mutable CtiCriticalSection _mutex;
};
