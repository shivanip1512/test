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

#include <rw/thr/prodcons.h>
#include <rw/ctoken.h>
#include <rw/re.h>

#include <boost/regex.hpp>

           
     
extern ULONG _CC_DEBUG;
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

}

void CtiPAOScheduleManager::doResetThr()
{
    Sleep(1000);
    CtiTime lastPeriodicDatabaseRefresh = CtiTime();

    while (TRUE)
    {   
        {
            RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        
            rwRunnable().serviceCancellation();

            CtiTime currentTime = CtiTime();
        
            if ( !isValid() && currentTime.seconds() >= lastPeriodicDatabaseRefresh.seconds()+30 )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Periodic restore of schedule list from the database" << endl;
                }
                refreshSchedulesFromDB();
                refreshEventsFromDB();

                lastPeriodicDatabaseRefresh = CtiTime();
            }
        }
        {
            rwRunnable().sleep(500);
        }
        
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
        std::list <CtiPAOSchedule*> mySchedules;
        std::list <CtiPAOSchedule*> updateSchedules;
        std::list <CtiPAOEvent*> myEvents;

        CtiPAOSchedule *currentSched = NULL;
        CtiPAOEvent *currentEvent = NULL;
        while(TRUE)
        {
            {
                RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
                currentTime.now();
                mySchedules.clear();
                if (checkSchedules(currentTime, mySchedules))
                {
                    updateSchedules.clear();
                    while (!mySchedules.empty())
                    {
                        currentSched = new CtiPAOSchedule();
                        currentSched = mySchedules.front();
                        mySchedules.pop_front();

                        if( _CC_DEBUG & CC_DEBUG_VERIFICATION )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - ScheduleID "<<currentSched->getScheduleId() << " NextRunTime about to occur (" << CtiTime(currentSched->getNextRunTime().seconds()) << ")." << endl;
                        }
                        myEvents.clear();
                        if (getEventsBySchedId(currentSched->getScheduleId(), myEvents))
                        {
                            while (!myEvents.empty())
                            {
                                currentEvent = new CtiPAOEvent();
                                currentEvent = myEvents.front();
                                myEvents.pop_front();
                                if( _CC_DEBUG & CC_DEBUG_VERIFICATION )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() << " -- EventID "<<currentEvent->getEventId()<<" about to run on SubBus "<<currentEvent->getPAOId()<<"."<<endl;
                                }
                                runScheduledEvent(currentEvent);
                            }
                        }
                        updateRunTimes(currentSched);
                        updateSchedules.push_back(currentSched);
                    }
                    if (!updateSchedules.empty())
                    {
                        updateDataBaseSchedules(updateSchedules);
                    } 
                }
                /*else
                {
                    Sleep(500);
                }*/
            }
            try
            {
                rwRunnable().serviceCancellation();
                rwRunnable().sleep( 500 );
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

        }
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

    if (!_schedules.empty())
    {

        std::list <CtiPAOSchedule*>::iterator iter = _schedules.begin();
        while (iter != _schedules.end())
        {
            if ((*iter)->getNextRunTime() < currentTime)
            {
                if ((*iter)->getIntervalRate() > 0 && !(*iter)->isDisabled())
                {                               
                    schedules.push_back(*iter);
                    retVal = true;
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
                CtiCCExecutorFactory f;
                CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::ENABLE_SUBSTATION_BUS_VERIFICATION, event->getPAOId(), strategy, secsSinceLastOp));
                executor->Execute();
                delete executor;
            }
            break;
        case 1:
            {
            }
            break;
        case 2:
            {
            }
            break;
        case 4:
            {

            }
            break;
        default:
            break;
    }


    event = NULL;
}
int CtiPAOScheduleManager::parseEvent(const string& _command, int &strategy, long &secsSinceLastOperation)
{
    string command = _command;
    string tempCommand;
    LONG multiplier = 0;
    
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
        else if (stringCompareIgnoreCase(command,"Verify CapBanks that have not operated in"))
        {
            boost::cmatch what;
            boost::regex re("[0-9]+");
            if (boost::regex_search(command, what, re))
            {
                re.assign("minut(e|es)");
                if (boost::regex_search(command, what, re))
                {
                    multiplier = 60;
                }
                re.assign("hou(r|rs)");
                if (boost::regex_search(command, what, re))
                {
                    multiplier = 3600;
                }
                re.assign("da(y|ys)");
                if (boost::regex_search(command, what, re))
                {
                    multiplier = 86400;
                }
                re.assign("wee(k|ks)");
                if (boost::regex_search(command, what, re))
                {
                    multiplier = 604800;
                }
                
                secsSinceLastOperation = atol(tempCommand.c_str()) * multiplier;
            }
            strategy = BanksInactiveForXTime;
        }
        else if (!stringCompareIgnoreCase(command,"Verify Standalone CapBanks"))
        {
           strategy = StandAloneBanks;
        }
    }
    return retVal;
}

void CtiPAOScheduleManager::updateRunTimes(CtiPAOSchedule *schedule)
{
    CtiTime currentTime = CtiTime();
        
    schedule->setLastRunTime(currentTime);
    if (schedule->getIntervalRate() <= 0)
    {                                   
        schedule->setNextRunTime( CtiTime(currentTime.seconds() + 0) );
    }
    else
        schedule->setNextRunTime( CtiTime(currentTime.seconds() + schedule->getIntervalRate()) );


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
    CtiPAOEvent updateEvent;
    if (findEvent(eventId, updateEvent))
    {
        updateEvent = event;
        retVal = true;
    }
    return retVal;
}
bool CtiPAOScheduleManager::deleteEvent(long eventId)
{
    bool retVal = false;
    CtiPAOEvent deleteEvent;
    if (findEvent(eventId, deleteEvent))
    {
        _events.remove(&deleteEvent);
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
                events.push_back(*iter);
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
                        //if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
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
        try
        {
            _schedules.clear();
            _schedules.assign(tempSchedules.begin(), tempSchedules.end());
            setValid(true);
        }
        catch (...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

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
                        << paoEventTable["command"];


                        selector.from(paoEventTable);
                        //if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - " << selector.asString().data() << endl;
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
        try
        {
            _events.clear();
            _events.assign(tempEvents.begin(), tempEvents.end());
            setValid(true);
        }
        catch (...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

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

                CtiPAOSchedule currentSchedule = *(schedules.front());
                schedules.pop_front();

                if ( currentSchedule.isDirty() )
                {
                    try
                    {

                        RWDBUpdater updater = paoSchedule.updater();

                        updater.where(paoSchedule["scheduleid"]==currentSchedule.getScheduleId());

                        updater << paoSchedule["nextruntime"].assign( toRWDBDT(currentSchedule.getNextRunTime()) )
                                << paoSchedule["lastruntime"].assign( toRWDBDT(currentSchedule.getLastRunTime()) );

                        updater.execute( conn );

                        if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
                        {
                            currentSchedule.setDirty(FALSE);
                        }
                        else
                        {
                            currentSchedule.setDirty(TRUE);
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "  " << updater.asString() << endl;
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
