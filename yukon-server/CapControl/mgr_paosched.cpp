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

#include <rw/thr/prodcons.h>
#include <rw/ctoken.h>
#include <rw/re.h>
#include <rw/cstring.h>


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
    RWDBDateTime lastPeriodicDatabaseRefresh = RWDBDateTime();

    while (TRUE)
    {   
        {
            RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        
            rwRunnable().serviceCancellation();

            RWDBDateTime currentTime;
            currentTime.now();
        
            if ( !isValid() && currentTime.seconds() >= lastPeriodicDatabaseRefresh.seconds()+30 )
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Periodic restore of schedule list from the database" << endl;
                }
                refreshSchedulesFromDB();
                refreshEventsFromDB();

                lastPeriodicDatabaseRefresh = RWDBDateTime();
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
        RWDBDateTime currentTime;
        RWDBDateTime nextRunTime;// = getNextRunTime()
        list <CtiPAOSchedule*> mySchedules;
        list <CtiPAOSchedule*> updateSchedules;
        list <CtiPAOEvent*> myEvents;

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
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " WHOA!!!  ScheduleID == "<<currentSched->getScheduleId()<<endl;
                        }
                        myEvents.clear();
                        if (getEventsBySchedId(currentSched->getScheduleId(), myEvents))
                        {
                            while (!myEvents.empty())
                            {
                                currentEvent = new CtiPAOEvent();
                                currentEvent = myEvents.front();
                                myEvents.pop_front();
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << RWTime() << " WHOA!!!  EventID == "<<currentEvent->getEventId()<<endl;
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
                else
                {
                    Sleep(500);
                }
            }
        }
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

bool CtiPAOScheduleManager::checkSchedules(RWDBDateTime currentTime, list<CtiPAOSchedule*> &schedules)
{
    bool retVal = false;
    schedules.clear();

    if (!_schedules.empty())
    {

        list <CtiPAOSchedule*>::iterator iter = _schedules.begin();
        while (iter != _schedules.end())
        {
            if ((*iter)->getNextRunTime() < currentTime)
            {
                if ((*iter)->getIntervalRate() > 0)
                {                               
                    schedules.push_back(*iter);
                    retVal = true;
                }
                else
                {
                    if ((*iter)->getNextRunTime() != (*iter)->getLastRunTime())
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
        case 0:
            {
                CtiCCExecutorFactory f;
                CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationVerificationMsg(0, event->getPAOId(), strategy, secsSinceLastOp));
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
int CtiPAOScheduleManager::parseEvent(RWCString command, int &strategy, long &secsSinceLastOperation)
{
    RWCString tempCommand, temp2;
    LONG multiplier = 0;

    if (!command.compareTo("Verify ALL CapBanks",RWCString::ignoreCase))
    {
        strategy = 0;
    }
    else if (!command.compareTo("Verify Failed CapBanks",RWCString::ignoreCase))
    {
        strategy = 1;
    }
    else if (!command.compareTo("Verify Questionable CapBanks",RWCString::ignoreCase))
    {
        strategy = 2;
    }
    else if (!command.compareTo("Verify Failed and Questionable CapBanks",RWCString::ignoreCase))
    {
        strategy = 3;
    }
    else if (!command.compareTo("Verify Selected CapBanks",RWCString::ignoreCase))
    {
        strategy = 4;
    }
    else if (command.contains("Verify CapBanks that have not operated in",RWCString::ignoreCase))
    {
        if (!(tempCommand = command.match(RWCRExpr("[0-9]+"))).isNull())
        {
            if (!(temp2 = command.match(RWCRExpr("minut(e|es)"))).isNull())
            {
                multiplier = 60;
            }
            if (!(temp2 = command.match(RWCRExpr("hou(r|rs)"))).isNull())
            {
                multiplier = 3600;
            }
            if (!(temp2 = command.match(RWCRExpr("da(y|ys)"))).isNull())
            {
                multiplier = 86400;
            }
            if (!(temp2 = command.match(RWCRExpr("wee(k|ks)"))).isNull())
            {
                multiplier = 604800;
            }

            secsSinceLastOperation = atol(tempCommand) * multiplier;
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - multiplier "<<multiplier<< endl;
                dout << RWTime() << " - secsSinceLastOperation "<<secsSinceLastOperation<< endl;
            }
        }
        strategy = 5;
    }
    else if (!(tempCommand = command.match(RWCRExpr("[0-9]+-[0-9]+-[0-9]+\ [0-9]+:[0-9]+:[0-9]+\.[0-9]+"))).isNull())
    {
        int year, month, day, hour, minute, second;
        RWCTokenizer next(tempCommand);
        RWCString token;
        token = next("-");
        year = atoi(token);
        token = next("-");
        month = atoi(token);
        token = next("-");
        day = atoi(token);
        RWCTokenizer next1(tempCommand);
        next1(" ");
        token = next1(":");
        hour = atoi(token);
        token = next1(":");
        minute = atoi(token);
        token = next1(":");
        second = atoi(token);
        
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - PARSED Operated Since!  "<<tempCommand << endl;
            dout << RWTime() << " - year:   "<<year<<" month: "<<month<<" day: "<<day << endl;
            dout << RWTime() << " - hour:   "<<hour<<" minute: "<<minute<<" second: "<<second << endl;
        }

        strategy = 5;
    }
    return 0;
}

void CtiPAOScheduleManager::updateRunTimes(CtiPAOSchedule *schedule)
{
    RWDBDateTime currentTime;
    currentTime.now();
        
    schedule->setLastRunTime(currentTime);
    schedule->setNextRunTime(RWDBDateTime(RWTime(currentTime.seconds() + schedule->getIntervalRate())));

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

    return retVal;
}
bool CtiPAOScheduleManager::findSchedule(long id, CtiPAOSchedule &sched)
{
    bool retVal = false;

    if (!_schedules.empty())
    {
        list <CtiPAOSchedule*>::iterator iter = _schedules.begin();
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
        list <CtiPAOEvent*>::iterator iter = _events.begin();
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
bool CtiPAOScheduleManager::getEventsBySchedId(long id, list<CtiPAOEvent*> &events)
{
    bool retVal = false;
    events.clear();
    if (!_events.empty())
    {
        list <CtiPAOEvent*>::iterator iter = _events.begin();
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


        list <CtiPAOSchedule *> tempSchedules;
        tempSchedules.clear();
        try
        {     

            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Resetting PAOSchedules - sync with database..." << endl;
                }

                if ( conn.isValid() )
                {   
                    RWDBDatabase db = getDatabase();
                    RWDBTable paoScheduleTable = db.table("paoschedule");
                    {
                        RWDBSelector selector = db.selector();
                        selector << paoScheduleTable["scheduleid"]
                        << paoScheduleTable["nextruntime"]
                        << paoScheduleTable["lastruntime"]
                        << paoScheduleTable["intervalrate"];


                        selector.from(paoScheduleTable);
                        //if ( _CC_DEBUG & CC_DEBUG_DATABASE )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                        CtiPAOSchedule* currentPAOSchedule = NULL;

                        while ( rdr() )
                        {
                            currentPAOSchedule = new CtiPAOSchedule(rdr);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " -currentPAOSchedule.getScheduleId()" << currentPAOSchedule->getScheduleId()<<endl;
                            }
                            tempSchedules.push_back(currentPAOSchedule);
                        }
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Unable to get valid database connection." << endl;
                    setValid(FALSE);
                    return;
                }
            }
        }
        catch (...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        try
        {
            _schedules.assign(tempSchedules.begin(), tempSchedules.end());
            setValid(true);
        }
        catch (...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return;
}
void CtiPAOScheduleManager::refreshEventsFromDB()
{

    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        
        bool wasAlreadyRunning = false;


        list <CtiPAOEvent *> tempEvents;
        tempEvents.clear();
        try
        {     

            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            {
                //if( _CC_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Resetting PAOEvents - sync with database..." << endl;
                }

                if ( conn.isValid() )
                {   
                    RWDBDatabase db = getDatabase();
                    RWDBTable paoEventTable = db.table("paoassignment");
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
                            dout << RWTime() << " - " << selector.asString().data() << endl;
                        }
                        RWDBReader rdr = selector.reader(conn);
                        CtiPAOEvent* currentPAOEvent = NULL;

                        while ( rdr() )
                        {
                            currentPAOEvent = new CtiPAOEvent(rdr);
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << RWTime() << " -currentPAOEvent.getEventId()" << currentPAOEvent->getEventId()<<endl;
                            }
                            tempEvents.push_back(currentPAOEvent);
                        }
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Unable to get valid database connection." << endl;
                    setValid(FALSE);
                    return;
                }
            }
        }
        catch (...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
        try
        {
            _events.assign(tempEvents.begin(), tempEvents.end());
            setValid(true);
        }
        catch (...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }

    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return;

}


void CtiPAOScheduleManager::updateDataBaseSchedules(list<CtiPAOSchedule*> &schedules)
{

    try
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);


        if ( !schedules.empty() )
        {
            RWDBDateTime currentDateTime = RWDBDateTime();
            RWCString schedulerPAO("schedulerPAO");
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWDBTable paoSchedule = getDatabase().table( "paoschedule" );
            conn.beginTransaction(schedulerPAO);

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

                        updater << paoSchedule["nextruntime"].assign( currentSchedule.getNextRunTime() )
                                << paoSchedule["lastruntime"].assign( currentSchedule.getLastRunTime() );

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
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "  " << updater.asString() << endl;
                            }
                        }

                        updater.clear();
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                    }
                }
            }while(!schedules.empty());

            conn.commitTransaction(schedulerPAO);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }


}
