#include "precompiled.h"

#include "mgr_paosched.h"
#include "pao_schedule.h"
#include "pao_event.h"
#include "capcontroller.h"
#include "ctitokenizer.h"
#include "utility.h"
#include "msg_signal.h"
#include "database_reader.h"
#include "database_util.h"
#include "ThreadStatusKeeper.h"
#include "ExecutorFactory.h"
#include "MsgVerifyBanks.h"
#include "MsgVerifyInactiveBanks.h"
#include "MsgTriggerDmvTest.h"
#include "utility.h"

#include <regex>

extern unsigned long _CC_DEBUG;
extern bool CC_TERMINATE_THREAD_TEST;
extern bool _DMV_TEST_ENABLED;
using namespace std;

using Cti::ThreadStatusKeeper;

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

    _initialCapControlStartUp = true;

    _valid = false;
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
        delete_container( _schedules );
        _schedules.clear();

        delete_container( _events );
        _events.clear();

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
    CTILOG_DEBUG(dout, "Starting the Schedule Manager");

    _scheduleThr = boost::thread( &CtiPAOScheduleManager::mainLoop, this );
    _resetThr    = boost::thread( &CtiPAOScheduleManager::doResetThr, this );

    CTILOG_DEBUG(dout, "Schedule Manager is running");
}

/*---------------------------------------------------------------------------
    stop

    Stops the mainThread
---------------------------------------------------------------------------*/
void CtiPAOScheduleManager::stop()
{
    try
    {
        CTILOG_DEBUG(dout, "Stopping the Schedule Manager");

        _scheduleThr.interrupt();
        _resetThr.interrupt();

        if ( ! _scheduleThr.timed_join( boost::posix_time::seconds( 30 ) ) )
        {
            CTILOG_WARN( dout, "Schedule Manager main thread did not shutdown gracefully. "
                               "Attempting a forced shutdown" );

            TerminateThread( _scheduleThr.native_handle(), EXIT_SUCCESS );
        }

        if ( ! _resetThr.timed_join( boost::posix_time::seconds( 30 ) ) )
        {
            CTILOG_WARN( dout, "Schedule Manager reset thread did not shutdown gracefully. "
                               "Attempting a forced shutdown" );

            TerminateThread( _resetThr.native_handle(), EXIT_SUCCESS );
        }

        CTILOG_DEBUG(dout, "Schedule Manager is stopped");
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiPAOScheduleManager::doResetThr()
{
    CTILOG_DEBUG(dout, "Schedule Manager reset thread is starting");

    try
    {
        boost::this_thread::sleep( boost::posix_time::milliseconds( 1000 ) );

        CtiTime lastPeriodicDatabaseRefresh = CtiTime();

        ThreadStatusKeeper threadStatus("CapControl mgrPAOSchedule doResetThr");

        while (true)
        {
            {
                CTILOCKGUARD( CtiCriticalSection, guard, _mutex );

                CtiTime currentTime = CtiTime();

                if ( !isValid() && currentTime.seconds() >= lastPeriodicDatabaseRefresh.seconds()+30 )
                {
                    CTILOG_INFO(dout, "Periodic restore of schedule list from the database");
                    refreshSchedulesFromDB();
                    refreshEventsFromDB();

                    lastPeriodicDatabaseRefresh = CtiTime();
                }
            }

            threadStatus.monitorCheck();

            boost::this_thread::sleep( boost::posix_time::milliseconds( 5000 ) );
        }
    }
    catch ( boost::thread_interrupted & )
    {
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_DEBUG(dout, "Schedule Manager reset thread is stopping");
}

void CtiPAOScheduleManager::mainLoop()
{
    CTILOG_DEBUG(dout, "Schedule Manager main thread is starting");

    try
    {
        ThreadStatusKeeper threadStatus("CapControl mgrPAOSchedule mainLoop");

        {
            CTILOCKGUARD( CtiCriticalSection, guard, _mutex );
            refreshSchedulesFromDB();
            refreshEventsFromDB();
        }
        CtiTime currentTime;
        std::list <CtiPAOSchedule*> mySchedules; // I dont own these
        std::list <CtiPAOSchedule*> updateSchedules; // I dont own these
        std::list <CtiPAOEvent*> myEvents; // I do own these

        while(true)
        {
            {
                CTILOCKGUARD( CtiCriticalSection, guard, _mutex );
                currentTime= CtiTime();
                mySchedules.clear();

                try
                {
                    if (checkSchedules(currentTime, mySchedules))
                    {
                        _initialCapControlStartUp = false;
                        updateSchedules.clear();

                        while (!mySchedules.empty())
                        {
                            CtiPAOSchedule * currentSched = mySchedules.front();
                            mySchedules.pop_front();

                            if( _CC_DEBUG & CC_DEBUG_VERIFICATION )
                            {
                                CTILOG_DEBUG(dout, "ScheduleID "<<currentSched->getScheduleId() << " NextRunTime about to occur (" << CtiTime(currentSched->getNextRunTime().seconds()) << ").");
                            }
                            myEvents.clear();

                            try
                            {

                                if (getEventsBySchedId(currentSched->getScheduleId(), myEvents))
                                {
                                    while (!myEvents.empty())
                                    {
                                        CtiPAOEvent * currentEvent = myEvents.front();
                                        myEvents.pop_front();
                                        if( _CC_DEBUG & CC_DEBUG_VERIFICATION )
                                        {
                                            CTILOG_DEBUG(dout, "EventID "<<currentEvent->getEventId()<<" about to run on SubBus "<<currentEvent->getPAOId()<<".");
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
                                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                        }
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
            }

            threadStatus.monitorCheck();

            boost::this_thread::sleep( boost::posix_time::milliseconds( 500 ) );
        }
    }
    catch ( boost::thread_interrupted & )
    {
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_DEBUG(dout, "Schedule Manager main thread is stopping");
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
                            _initialCapControlStartUp = false;
                            //(*iter)->setLastRunTime((*iter)->getNextRunTime());
                            (*iter)->setLastRunTime(currentTime);
                            CtiTime tempNextTime = CtiTime((*iter)->getNextRunTime().seconds() + (*iter)->getIntervalRate());
                            if (tempNextTime < currentTime)
                            {

                                string text = string("ERROR - Schedule: ");
                                text += (*iter)->getScheduleName();

                                string additional = string("Schedule ID ");
                                additional += CtiNumStr((*iter)->getScheduleId()).toString();
                                additional += " did not run at: ";
                                additional += tempNextTime.asString();

                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,CapControlLogType,SignalEvent, "cap control"), CALLSITE);
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
        _initialCapControlStartUp = false;

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return retVal;
}

void CtiPAOScheduleManager::runScheduledEvent(CtiPAOEvent *paoEvent)
{
    int strategy;
    long secsSinceLastOp = -1;
    switch (parseEvent(paoEvent->getEventCommand(), strategy, secsSinceLastOp))
    {
        case CapControlVerification:
        {
            //What type of Verification?
            if (strategy == BanksInactiveForXTime)
            {
               CtiCCExecutorFactory::createExecutor(new VerifyInactiveBanks(paoEvent->getPAOId(),secsSinceLastOp,paoEvent->getDisableOvUvFlag()))->execute();
            }
            else //The rest
            {
               CtiCCExecutorFactory::createExecutor(new VerifyBanks(paoEvent->getPAOId(),paoEvent->getDisableOvUvFlag(), strategy))->execute();
            }
            // Verify Selected Banks cannot be scheduled. It is executed from the Web only.
            break;
        }
        case ConfirmSubstationBus:
        {
            CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::CONFIRM_SUBSTATIONBUS, paoEvent->getPAOId()))->execute();
            break;
        }
        case SendTimeSync:
        {
            CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::SEND_TIME_SYNC, paoEvent->getPAOId()))->execute();
            break;
        }
        case DmvTestExecution:
        {
            // regex away the "DMV Test: " at the front...
            std::smatch results;

            if ( std::regex_search( paoEvent->getEventCommand(), results, std::regex { "^DMV Test: (.*)$" } ) )
            {
                CtiCCExecutorFactory::createExecutor(
                    new MsgTriggerDmvTest( paoEvent->getPAOId(), results[ 1 ].str() ) )->execute();
            }
            else
            {
                CTILOG_ERROR( dout, " Invalid DMV Test Execution Command: " << paoEvent->getEventCommand() );
            }
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
    long multiplier = 0;

    int retVal = SomethingElse; //0 capbank event...future use could include other devices??  dunno..open to expand on.

    if ( findStringIgnoreCase(command,"CapBanks"))
    {
        retVal = CapControlVerification;
        if (ciStringEqual(command,"Verify ALL CapBanks"))
        {
            strategy = AllBanks;
        }
        else if (ciStringEqual(command,"Verify Failed and Questionable CapBanks"))
        {
            strategy = FailedAndQuestionableBanks;
        }
        else if (ciStringEqual(command,"Verify Failed CapBanks"))
        {
            strategy = FailedBanks;
        }
        else if (ciStringEqual(command,"Verify Questionable CapBanks"))
        {
            strategy = QuestionableBanks;
        }
        else if (ciStringEqual(command,"Verify Selected CapBanks"))
        {
            strategy = SelectedForVerificationBanks;
        }
        else if (stringContainsIgnoreCase(command,"Verify CapBanks that have not operated in"))
        {
            std::string CmdStr(command);
            std::string token;
            int val = 0;
            boost::regex re("[0-9]+");
            secsSinceLastOperation = 0;

            if(!(token = Cti::matchRegex(CmdStr, "[0-9]+ +min")).empty())
            {
                val = atoi(Cti::matchRegex(token, re).c_str());
                multiplier = 60;
                secsSinceLastOperation += val * multiplier;
            }
            if(!(token = Cti::matchRegex(CmdStr, "[0-9]+ +hr")).empty())
            {
                val = atoi(Cti::matchRegex(token, re).c_str());
                multiplier = 3600;
                secsSinceLastOperation += val * multiplier;
            }
            if(!(token = Cti::matchRegex(CmdStr, "[0-9]+ +day")).empty())
            {
                val = atoi(Cti::matchRegex(token, re).c_str());
                multiplier = 86400;
                secsSinceLastOperation += val * multiplier;
            }
            if(!(token = Cti::matchRegex(CmdStr, "[0-9]+ +wk")).empty())
            {
                val = atoi(Cti::matchRegex(token, re).c_str());
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
        else if (ciStringEqual(command,"Verify Standalone CapBanks"))
        {
           strategy = StandAloneBanks;
        }
    }
    else if (findStringIgnoreCase(command,"Confirm Sub"))
    {
        retVal = ConfirmSubstationBus;
    }
    else if (findStringIgnoreCase(command,"Send Time Syncs"))
    {
        retVal = SendTimeSync;
    }
    else if (stringContainsIgnoreCase(command,"DMV Test: "))
    {
        if ( _DMV_TEST_ENABLED )
        {
            retVal = DmvTestExecution;
        }
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
            additional += CtiNumStr(schedule->getScheduleId()).toString();
            additional += " did not run at: ";
            additional += tempNextTime.asString();

            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,5,text,additional,CapControlLogType,SignalEvent, "cap control"), CALLSITE);
            while (tempNextTime < currentTime)
            {
                tempNextTime = CtiTime(tempNextTime.seconds() + schedule->getIntervalRate());
            }

        }
        schedule->setNextRunTime(tempNextTime);
    }
}

bool CtiPAOScheduleManager::getEventsBySchedId(long id, std::list<CtiPAOEvent*> &events)
{
    events.clear();

    for ( CtiPAOEvent * event : _events )
    {
        if ( event->getScheduleId() == id )
        {
            events.push_back( new CtiPAOEvent( *event ) );
        }
    }
    
    return ! events.empty();
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
        std::list <CtiPAOSchedule *> tempSchedules;

        try
        {
            //if( _CC_DEBUG )
            {
                CTILOG_DEBUG(dout, "Resetting PAOSchedules - sync with database...");
            }

            static const string sql =  "SELECT PAS.scheduleid, PAS.schedulename, PAS.nextruntime, PAS.lastruntime, "
                                         "PAS.intervalrate, PAS.disabled "
                                       "FROM paoschedule PAS";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            while ( rdr() )
            {
                CtiPAOSchedule * currentPAOSchedule = new CtiPAOSchedule(rdr);

                if( _CC_DEBUG & CC_DEBUG_VERIFICATION )
                {
                    CTILOG_DEBUG(dout, "-currentPAOSchedule.getScheduleId()" << currentPAOSchedule->getScheduleId());
                }
                tempSchedules.push_back(currentPAOSchedule);
            }
        }
        catch (...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }

        {
            CTILOCKGUARD( CtiCriticalSection, guard, _mutex );

            std::swap( _schedules, tempSchedules );     // swap out the old for the new
        }

        delete_container( tempSchedules );              // delete the old
        setValid(true);
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiPAOScheduleManager::refreshEventsFromDB()
{
    try
    {
        std::list <CtiPAOEvent *> tempEvents;

        try
        {
            //if( _CC_DEBUG )
            {
                CTILOG_DEBUG(dout, "Resetting PAOEvents - sync with database...");
            }

            static const string sql = "SELECT PSA.eventid, PSA.scheduleid, PSA.paoid, PSA.command, PSA.disableovuv "
                                      "FROM paoscheduleassignment PSA";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            rdr.execute();

            if ( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CTILOG_INFO(dout, rdr.asString());
            }

            while ( rdr() )
            {
                CtiPAOEvent * currentPAOEvent = new CtiPAOEvent(rdr);

                if( _CC_DEBUG & CC_DEBUG_VERIFICATION )
                {
                    CTILOG_DEBUG(dout, "-currentPAOEvent.getEventId()" << currentPAOEvent->getEventId());
                }
                tempEvents.push_back(currentPAOEvent);
            }
        }
        catch (...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }

        {
            CTILOCKGUARD( CtiCriticalSection, guard, _mutex );

            std::swap( _events, tempEvents );           // swap out the old for the new
        }

        delete_container( tempEvents );                 // delete the old
        setValid(true);
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiPAOScheduleManager::updateDataBaseSchedules(std::list<CtiPAOSchedule*> &schedules)
{
    try
    {
        CTILOCKGUARD( CtiCriticalSection, guard, _mutex );

        if ( !schedules.empty() )
        {
            Cti::Database::DatabaseConnection   conn;

            if ( ! conn.isValid() )
            {
                CTILOG_ERROR(dout, "Invalid Connection to Database");

                return;
            }

            do
            {
                CtiPAOSchedule *currentSchedule = schedules.front();
                schedules.pop_front();

                if ( currentSchedule->isDirty() )
                {
                    static const std::string sql_update = "update paoschedule"
                                                           " set "
                                                                "nextruntime = ?, "
                                                                "lastruntime = ?"
                                                           " where "
                                                                "scheduleid = ?";

                    Cti::Database::DatabaseWriter   updater(conn, sql_update);

                    updater
                        << currentSchedule->getNextRunTime()
                        << currentSchedule->getLastRunTime()
                        << currentSchedule->getScheduleId();

                    if( Cti::Database::executeUpdater( updater, CALLSITE ))
                    {
                        currentSchedule->setDirty(false);
                    }
                }
                //delete currentSchedule;
            }
            while(!schedules.empty());
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

