/*-----------------------------------------------------------------------------
    Filename:  mgr_paosched.cpp

    Programmer:  Julie Richter

    Description:

    Initial Date:  1/27/2005

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2005
-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "mgr_paosched.h"
#include "pao_event.h"
#include "capcontroller.h"
#include "ctitokenizer.h"
#include "thread_monitor.h"
#include "utility.h"
#include "msg_signal.h"
#include "ctistring.h"

#include <stdlib.h>

#include <rw/thr/prodcons.h>
#include <rw/ctoken.h>
#include <rw/re.h>

#include <boost/regex.hpp>

extern ULONG _CC_DEBUG;
extern BOOL CC_TERMINATE_THREAD_TEST;
using namespace std;
/* The singleton instance of CtiPAOScheduleManager */
CtiPAOScheduleManager* CtiPAOScheduleManager::_instance = NULL;

/*---------------------------------------------------------------------------
    getInstance

    Returns the single instance of CtiPAOScheduleManager
---------------------------------------------------------------------------*/
CtiPAOScheduleManager* CtiPAOScheduleManager::getInstance()
{
    if ( _instance == NULL )
        _instance = new CtiPAOScheduleManager();

    return _instance;
}

/*---------------------------------------------------------------------------
    Constructor

    Private to ensure that only one CtiPAOScheduleManager is available through the
    instance member function
---------------------------------------------------------------------------*/
CtiPAOScheduleManager::CtiPAOScheduleManager()
{
    _schedules.clear();
    _events.clear();

    _initialCapControlStartUp = TRUE;

    _valid = FALSE;
}

/*---------------------------------------------------------------------------
    Destructor

    Private to ensure that the single instance of CtiPAOScheduleManager is not
    deleted
---------------------------------------------------------------------------*/
CtiPAOScheduleManager::~CtiPAOScheduleManager()
{
    if( _instance != NULL )
    {
        delete _instance;
        _instance = NULL;
    }
}

/*---------------------------------------------------------------------------
    start

    Starts the mainThread thread
---------------------------------------------------------------------------*/
void CtiPAOScheduleManager::start()
{
    RWThreadFunction threadfunc = rwMakeThreadFunction( *this, &CtiPAOScheduleManager::mainLoop );
    _scheduleThread = threadfunc;
    threadfunc.start();

    //Start the reset thread
    RWThreadFunction resetfunc = rwMakeThreadFunction( *this, &CtiPAOScheduleManager::doResetThr );
    _resetThr = resetfunc;
    resetfunc.start();

}

/*---------------------------------------------------------------------------
    stop

    Stops the mainThread
---------------------------------------------------------------------------*/
void CtiPAOScheduleManager::stop()
{
    try
    {
        if ( _scheduleThread.isValid() && _scheduleThread.requestCancellation() == RW_THR_ABORTED )
        {
            _scheduleThread.terminate();

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Pao Schedule Thread forced to terminate." << endl;
            }
        }
        else
        {
            _scheduleThread.requestCancellation();
            _scheduleThread.join();
        }

        if ( _resetThr.isValid() && _resetThr.requestCancellation() == RW_THR_ABORTED )
        {
            _resetThr.terminate();

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Pao Schedule Reset Thread forced to terminate." << endl;
            }
        }
        else
        {
            _resetThr.requestCancellation();
            _resetThr.join();
        }
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiPAOScheduleManager::doResetThr()
{
    try
    {
        Sleep(1000);
        CtiTime lastPeriodicDatabaseRefresh = CtiTime();

        CtiTime rwnow, announceTime, tickleTime;
        tickleTime.now();
        while (TRUE)
        {
            {
                RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

                CtiTime currentTime = CtiTime();

                if ( !isValid() && currentTime.seconds() >= lastPeriodicDatabaseRefresh.seconds()+30 )
                {
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Periodic restore of schedule list from the database" << endl;
                    }
                    refreshSchedulesFromDB();
                    refreshEventsFromDB();

                    lastPeriodicDatabaseRefresh = CtiTime();
                }
            }


            rwnow = rwnow.now();
            if(rwnow.seconds() > tickleTime.seconds())
            {
                tickleTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardTickleTime );
                if( rwnow.seconds() > announceTime.seconds() )
                {
                    announceTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardMonitorTime );
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " CapControl mgrPAOSchedule doResetThr. TID: " << rwThreadId() << endl;
                }
                ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl mgrPAOSchedule doResetThr", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::periodicComplain, 0) );
            }

            for (int i = 0; i < 10; ++i)
            {
                rwRunnable().serviceCancellation();
                rwRunnable().sleep(500);
            }
        }
        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl mgrPAOSchedule doResetThr", CtiThreadRegData::LogOut ) );
    }
    catch(RWCancellation& )
    {
        throw;
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiPAOScheduleManager::mainLoop()
{
    try
    {
        {
            RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
            refreshSchedulesFromDB();
            refreshEventsFromDB();
        }
        CtiTime currentTime;
        CtiTime nextRunTime;// = getNextRunTime()
        std::list <CtiPAOSchedule*> mySchedules; // I dont own these
        std::list <CtiPAOSchedule*> updateSchedules; // I dont own these
        std::list <CtiPAOEvent*> myEvents; // I do own these

        CtiTime rwnow;
        CtiTime announceTime((unsigned long) 0);
        CtiTime tickleTime((unsigned long) 0);

        CtiPAOSchedule *currentSched = NULL;
        CtiPAOEvent *currentEvent = NULL;
        while(TRUE)
        {
            {
                RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
                currentTime= CtiTime();
                mySchedules.clear();

                try
                {
                    if (checkSchedules(currentTime, mySchedules))
                    {
                        _initialCapControlStartUp = FALSE;
                        updateSchedules.clear();

                        while (!mySchedules.empty())
                        {
                            currentSched = mySchedules.front();
                            mySchedules.pop_front();

                            if( _CC_DEBUG & CC_DEBUG_VERIFICATION )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - ScheduleID "<<currentSched->getScheduleId() << " NextRunTime about to occur (" << CtiTime(currentSched->getNextRunTime().seconds()) << ")." << endl;
                            }
                            myEvents.clear();

                            try
                            {

                                if (getEventsBySchedId(currentSched->getScheduleId(), myEvents))
                                {
                                    while (!myEvents.empty())
                                    {
                                        currentEvent = myEvents.front();
                                        myEvents.pop_front();
                                        if( _CC_DEBUG & CC_DEBUG_VERIFICATION )
                                        {
                                            CtiLockGuard<CtiLogger> logger_guard(dout);
                                            dout << CtiTime() << " -- EventID "<<currentEvent->getEventId()<<" about to run on SubBus "<<currentEvent->getPAOId()<<"."<<endl;
                                        }
                                        runScheduledEvent(currentEvent);
                                        delete currentEvent;
                                    }
                                }
                                updateRunTimes(currentSched);
                                updateSchedules.push_back(currentSched);
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                            }

                        }
                        try
                        {
                            if (!updateSchedules.empty())
                            {
                                updateDataBaseSchedules(updateSchedules);
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                        }
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                }
            }
            try
            {
                rwRunnable().serviceCancellation();
                rwRunnable().sleep( 500 );

                if (CC_TERMINATE_THREAD_TEST)
                {
                    _resetThr.requestCancellation();

                }
            }
            catch(RWCancellation& )
            {
                throw;
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
            rwnow = rwnow.now();
            if(rwnow.seconds() > tickleTime.seconds())
            {
                tickleTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardTickleTime );
                if( rwnow.seconds() > announceTime.seconds() )
                {
                    announceTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardMonitorTime );
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " CapControl mgrPAOSchedule mainLoop TID: " << rwThreadId() << endl;
                }

                ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl mgrPAOSchedule mainLoop", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCCSubstationBusStore::periodicComplain, 0) );
            }

        }

        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CapControl mgrPAOSchedule mainLoop", CtiThreadRegData::LogOut ) );
    }
    catch(RWCancellation& )
    {
        throw;
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

bool CtiPAOScheduleManager::checkSchedules(const CtiTime& currentTime, std::list<CtiPAOSchedule*> &schedules)
{
    bool retVal = false;
    schedules.clear();

    try
    {

        if (!_schedules.empty())
        {

            std::list <CtiPAOSchedule*>::iterator iter = _schedules.begin();
            while (iter != _schedules.end())
            {
                if ((*iter)->getNextRunTime() < currentTime)
                {
                    if ((*iter)->getIntervalRate() > 0 && !(*iter)->isDisabled())
                    {
                        if (_initialCapControlStartUp)
                        {
                            _initialCapControlStartUp = FALSE;
                            //(*iter)->setLastRunTime((*iter)->getNextRunTime());
                            (*iter)->setLastRunTime(currentTime);
                            CtiTime tempNextTime = CtiTime((*iter)->getNextRunTime().seconds() + (*iter)->getIntervalRate());
                            if (tempNextTime < currentTime)
                            {

                                string text = string("ERROR - Schedule: ");
                                text += (*iter)->getScheduleName();

                                string additional = string("Schedule ID ");
                                additional += longToString((*iter)->getScheduleId());
                                additional += " did not run at: ";
                                additional += tempNextTime.asString();

                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,CapControlLogType,SignalEvent, "cap control"));
                                while (tempNextTime < currentTime)
                                {
                                    tempNextTime = CtiTime(tempNextTime.seconds() + (*iter)->getIntervalRate());
                                }
                                (*iter)->setNextRunTime(tempNextTime);
                            }
                            else
                                (*iter)->setNextRunTime(tempNextTime);
                            list <CtiPAOSchedule*> tempList;
                            tempList.clear();
                            tempList.push_back(*iter);
                            updateDataBaseSchedules(tempList);
                        }
                        else
                        {
                            schedules.push_back(*iter);
                            retVal = true;
                        }
                    }
                    else
                    {
                        if ((*iter)->getNextRunTime() != (*iter)->getLastRunTime() && !(*iter)->isDisabled())
                        {
                            schedules.push_back(*iter);
                            retVal = true;
                        }
                    }
                }
                iter++;
            }
        }
        _initialCapControlStartUp = FALSE;

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return retVal;
}

void CtiPAOScheduleManager::runScheduledEvent(CtiPAOEvent *event)
{
    int strategy;
    long secsSinceLastOp = -1;
    switch (parseEvent(event->getEventCommand(), strategy, secsSinceLastOp))
    {
        case CapControlVerification:
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::ENABLE_SUBSTATION_BUS_VERIFICATION, event->getPAOId(), strategy, secsSinceLastOp, event->getDisableOvUvFlag()))->execute();
            break;
        }
        case ConfirmSub:
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::CONFIRM_SUB, event->getPAOId()))->execute();
            break;
        }
        case SendTimeSync:
        {
            CtiCCExecutorFactory::createExecutor(new CtiCCCommand(CtiCCCommand::SEND_TIME_SYNC, event->getPAOId()))->execute();
            break;
        }
        default:
        {
            break;
        }
    }

}
int CtiPAOScheduleManager::parseEvent(const string& _command, int &strategy, long &secsSinceLastOperation)
{
    string command = _command;
    string tempCommand;
    LONG multiplier = 0;
    BOOL disableOvUvFlag = false;

    int retVal = SomethingElse; //0 capbank event...future use could include other devices??  dunno..open to expand on.

    if ( findStringIgnoreCase(command,"CapBanks"))
    {
        retVal = CapControlVerification;
        if (!stringCompareIgnoreCase(command,"Verify ALL CapBanks"))
        {
            strategy = AllBanks;
        }
        else if (!stringCompareIgnoreCase(command,"Verify Failed and Questionable CapBanks"))
        {
            strategy = FailedAndQuestionableBanks;
        }
        else if (!stringCompareIgnoreCase(command,"Verify Failed CapBanks"))
        {
            strategy = FailedBanks;
        }
        else if (!stringCompareIgnoreCase(command,"Verify Questionable CapBanks"))
        {
            strategy = QuestionableBanks;
        }
        else if (!stringCompareIgnoreCase(command,"Verify Selected CapBanks"))
        {
            strategy = SelectedForVerificationBanks;
        }
        else if (stringContainsIgnoreCase(command,"Verify CapBanks that have not operated in"))
        {
            CtiString CmdStr(command);
            CtiString token;
            int val = 0;
            boost::regex re("[0-9]+");
            secsSinceLastOperation = 0;

            if(!(token = CmdStr.match("[0-9]+ min")).empty())
            {
                val = atoi((token.match(re)).c_str());
                multiplier = 60;
                secsSinceLastOperation += val * multiplier;
            }
            if(!(token = CmdStr.match("[0-9]+ hr")).empty())
            {
                val = atoi((token.match(re)).c_str());
                multiplier = 3600;
                secsSinceLastOperation += val * multiplier;
            }
            if(!(token = CmdStr.match("[0-9]+ day")).empty())
            {
                val = atoi((token.match(re)).c_str());
                multiplier = 86400;
                secsSinceLastOperation += val * multiplier;
            }
            if(!(token = CmdStr.match("[0-9]+ wk")).empty())
            {
                val = atoi((token.match(re)).c_str());
                multiplier = 604800;
                secsSinceLastOperation += val * multiplier;
            }

            if (secsSinceLastOperation > 0)
            {
                strategy = BanksInactiveForXTime;
            }
            else
                retVal = SomethingElse;

        }
        else if (!stringCompareIgnoreCase(command,"Verify Standalone CapBanks"))
        {
           strategy = StandAloneBanks;
        }
    }
    else if (findStringIgnoreCase(command,"Confirm Sub"))
    {
        retVal = ConfirmSub;
    }
    else if (findStringIgnoreCase(command,"Send Time Syncs"))
    {
        retVal = SendTimeSync;
    }

    return retVal;
}

void CtiPAOScheduleManager::updateRunTimes(CtiPAOSchedule *schedule)
{
    CtiTime currentTime = CtiTime();

    //schedule->setLastRunTime(schedule->getNextRunTime());
    schedule->setLastRunTime(currentTime);
    if (schedule->getIntervalRate() <= 0)
    {
        schedule->setNextRunTime( currentTime );
    }
    else
    {
        CtiTime tempNextTime = CtiTime(schedule->getNextRunTime().seconds() + schedule->getIntervalRate());
        if (tempNextTime < currentTime)
        {

            string text = string("ERROR - Schedule: ");
            text += schedule->getScheduleName();

            string additional = string("Schedule ID ");
            additional += longToString(schedule->getScheduleId());
            additional += " did not run at: ";
            additional += tempNextTime.asString();

            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,CapControlLogType,SignalEvent, "cap control"));
            while (tempNextTime < currentTime)
            {
                tempNextTime = CtiTime(tempNextTime.seconds() + schedule->getIntervalRate());
            }

        }
        schedule->setNextRunTime(tempNextTime);
    }


}

void CtiPAOScheduleManager::addSchedule(const CtiPAOSchedule &sched)
{

}
bool CtiPAOScheduleManager::updateSchedule(const CtiPAOSchedule &sched)
{
    bool retVal = false;

    return retVal;
}
bool CtiPAOScheduleManager::deleteSchedule(long schedId)
{
    bool retVal = false;

    CtiPAOSchedule sched;
    if (!_schedules.empty())
    {
        if (findSchedule(schedId, sched))
        {
            _schedules.remove(&sched);
            retVal = true;
        }
    }

    return retVal;
}
bool CtiPAOScheduleManager::findSchedule(long id, CtiPAOSchedule &sched)
{
    bool retVal = false;

    if (!_schedules.empty())
    {
        std::list <CtiPAOSchedule*>::iterator iter = _schedules.begin();
        while (iter != _schedules.end())
        {
            if ((*iter)->getScheduleId() == id)
            {
                sched = *(*iter);
                retVal = true;
                break;
            }
            iter++;
        }
    }

    return retVal;
}

void CtiPAOScheduleManager::addEvent(long schedId, const CtiPAOEvent &event)
{
}
bool CtiPAOScheduleManager::updateEvent(long eventId, const CtiPAOEvent &event)
{
    bool retVal = false;
    CtiPAOEvent currentUpdateEvent;
    if (findEvent(eventId, currentUpdateEvent))
    {
        currentUpdateEvent = event;
        retVal = true;
    }
    return retVal;
}
bool CtiPAOScheduleManager::deleteEvent(long eventId)
{
    bool retVal = false;
    CtiPAOEvent currentDeleteEvent;
    if (findEvent(eventId, currentDeleteEvent))
    {
        _events.remove(&currentDeleteEvent);
        retVal = true;
    }

    return retVal;
}
bool CtiPAOScheduleManager::findEvent(long id, CtiPAOEvent &event)
{
    bool retVal = false;
    if (!_events.empty())
    {
        std::list <CtiPAOEvent*>::iterator iter = _events.begin();
        while (iter != _events.end())
        {
            if ((*iter)->getEventId() == id)
            {
                event = *(*iter);
                retVal = true;
                break;
            }
            iter++;
        }
    }
    return retVal;
}
bool CtiPAOScheduleManager::getEventsBySchedId(long id, std::list<CtiPAOEvent*> &events)
{
    bool retVal = false;
    events.clear();
    if (!_events.empty())
    {
        std::list <CtiPAOEvent*>::iterator iter = _events.begin();
        while (iter != _events.end())
        {
            if ((*iter)->getScheduleId() == id)
            {
                CtiPAOEvent* thisEvent = new CtiPAOEvent((*iter)->getEventId(),
                                                         (*iter)->getScheduleId(),
                                                         (*iter)->getPAOId(),
                                                         (*iter)->getEventCommand(),
                                                         (*iter)->getDisableOvUvFlag());
                events.push_back(thisEvent);
                retVal = true;
            }
            iter++;
        }
    }
    return retVal;
}


bool CtiPAOScheduleManager::isValid()
{
    return _valid;
}
void CtiPAOScheduleManager::setValid(bool valid)
{
    _valid = valid;
}


void CtiPAOScheduleManager::refreshSchedulesFromDB()
{

    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

        bool wasAlreadyRunning = false;


        std::list <CtiPAOSchedule *> tempSchedules;
        tempSchedules.clear();
        try
        {

            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Resetting PAOSchedules - sync with database..." << endl;
                }

                if ( conn.isValid() )
                {
                    RWDBDatabase db = getDatabase();
                    RWDBTable paoScheduleTable = db.table("paoschedule");
                    {
                        RWDBSelector selector = db.selector();
                        selector << paoScheduleTable["scheduleid"]
                        << paoScheduleTable["schedulename"]
                        << paoScheduleTable["nextruntime"]
                        << paoScheduleTable["lastruntime"]
                        << paoScheduleTable["intervalrate"]
                        << paoScheduleTable["disabled"];


                        selector.from(paoScheduleTable);
                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            string loggedSQLstring = selector.asString();
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - " << loggedSQLstring << endl;
                            }
                        }
                        RWDBReader rdr = selector.reader(conn);
                        CtiPAOSchedule* currentPAOSchedule = NULL;

                        while ( rdr() )
                        {
                            currentPAOSchedule = new CtiPAOSchedule(rdr);

                            if( _CC_DEBUG & CC_DEBUG_VERIFICATION )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " -currentPAOSchedule.getScheduleId()" << currentPAOSchedule->getScheduleId()<<endl;
                            }
                            tempSchedules.push_back(currentPAOSchedule);
                        }
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Unable to get valid database connection." << endl;
                    setValid(FALSE);
                    return;
                }
            }
        }
        catch (...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        _schedules.clear();
        _schedules.assign(tempSchedules.begin(), tempSchedules.end());
        setValid(true);
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return;
}
void CtiPAOScheduleManager::refreshEventsFromDB()
{

    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

        bool wasAlreadyRunning = false;


        std::list <CtiPAOEvent *> tempEvents;
        tempEvents.clear();
        try
        {

            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Resetting PAOEvents - sync with database..." << endl;
                }

                if ( conn.isValid() )
                {
                    RWDBDatabase db = getDatabase();
                    RWDBTable paoEventTable = db.table("paoscheduleassignment");
                    {
                        RWDBSelector selector = db.selector();
                        selector << paoEventTable["eventid"]
                        << paoEventTable["scheduleid"]
                        << paoEventTable["paoid"]
                        << paoEventTable["command"]
                        << paoEventTable["disableovuv"];


                        selector.from(paoEventTable);
                        if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            string loggedSQLstring = selector.asString();
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - " << loggedSQLstring << endl;
                            }
                        }
                        RWDBReader rdr = selector.reader(conn);
                        CtiPAOEvent* currentPAOEvent = NULL;

                        while ( rdr() )
                        {
                            currentPAOEvent = new CtiPAOEvent(rdr);

                            if( _CC_DEBUG & CC_DEBUG_VERIFICATION )
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " -currentPAOEvent.getEventId()" << currentPAOEvent->getEventId()<<endl;
                            }
                            tempEvents.push_back(currentPAOEvent);
                        }
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Unable to get valid database connection." << endl;
                    setValid(FALSE);
                    return;
                }
            }
        }
        catch (...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

        _events.clear();
        _events.assign(tempEvents.begin(), tempEvents.end());
        setValid(true);
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return;

}


void CtiPAOScheduleManager::updateDataBaseSchedules(std::list<CtiPAOSchedule*> &schedules)
{

    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);


        if ( !schedules.empty() )
        {
            CtiTime currentDateTime = CtiTime();
            string schedulerPAO("schedulerPAO");
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWDBTable paoSchedule = getDatabase().table( "paoschedule" );
            conn.beginTransaction(string2RWCString(schedulerPAO));

            do
            {

                CtiPAOSchedule *currentSchedule = schedules.front();
                schedules.pop_front();

                if ( currentSchedule->isDirty() )
                {
                    try
                    {

                        RWDBUpdater updater = paoSchedule.updater();

                        updater.where(paoSchedule["scheduleid"]==currentSchedule->getScheduleId());

                        updater << paoSchedule["nextruntime"].assign( toRWDBDT(currentSchedule->getNextRunTime()) )
                                << paoSchedule["lastruntime"].assign( toRWDBDT(currentSchedule->getLastRunTime()) );

                        updater.execute( conn );

                        if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
                        {
                            currentSchedule->setDirty(FALSE);
                        }
                        else
                        {
                            currentSchedule->setDirty(TRUE);
                            {
                                string loggedSQLstring = updater.asString();
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << "  " << loggedSQLstring << endl;
                                }
                            }
                        }

                        updater.clear();
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
                //delete currentSchedule;
            }while(!schedules.empty());

            conn.commitTransaction(string2RWCString(schedulerPAO));
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }


}

string CtiPAOScheduleManager::longToString(LONG val)
{
    char tempchar[20] = "";
    string retString = string("");
    _snprintf(tempchar,20,"%d", 0, val);
    retString += tempchar;

    return retString;
}

