#include "yukon.h"

#include <rw/thr/runnable.h>
#include <rw/thr/mutex.h>
#include <rw/db/datetime.h>
#include <vector>

#include "dbaccess.h"
#include "ctibase.h"
#include "pointtypes.h"
#include "message.h"
#include "msg_multi.h"
#include "logger.h"
#include "guard.h"
#include "utility.h"
#include "cparms.h"
#include "numstr.h"
#include "mgr_holiday.h"

#include "calcthread.h"

extern ULONG _CALC_DEBUG;
extern BOOL  UserQuit;
extern bool _shutdownOnThreadTimeout;
extern bool _runCalcHistorical;
extern bool _runCalcBaseline;

CtiCalculateThread::~CtiCalculateThread( void )
{
    _auAffectedPoints.clear();
    if( _periodicPoints.size() > 0 )
    {
        delete_map(_periodicPoints);
        _periodicPoints.clear();
    }
    if( _onUpdatePoints.size() > 0 )
    {
        delete_map(_onUpdatePoints);
        _onUpdatePoints.clear();
    }
    if( _constantPoints.size() > 0 )
    {
        delete_map(_constantPoints);
        _constantPoints.clear();
    }
    if( _historicalPoints.size() > 0 )
    {
        delete_map(_historicalPoints);
        _historicalPoints.clear();
    }
};

void CtiCalculateThread::pointChange( long changedID, double newValue, const CtiTime &newTime, unsigned newQuality, unsigned newTags )
{
    try
    {
        RWMutexLock::LockGuard msgLock(_pointDataMutex);
        RWTValHashSetIterator<depStore, depStore, depStore> *dependentIterator;
        CtiHashKey hashKey(changedID);

        CtiPointStore* pointStore = CtiPointStore::getInstance();
        CtiPointStoreElement* pointPtr = (CtiPointStoreElement*)((*pointStore).findValue(&hashKey));
        if( _CALC_DEBUG & CALC_DEBUG_POINTDATA )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - Point Data ID: " << changedID << " Val: " << newValue << " Time: " << newTime.asString() << " Quality: " << newQuality << endl;
        }

        if( pointPtr != rwnil )
        {
            if( newTime.seconds() > pointPtr->getPointTime().seconds() ||       // Point Change is newer than last point data
                ( pointPtr->getNumUpdates() > 0 &&                              // Point has been updated AND
                  ( newQuality != pointPtr->getPointQuality() ||                // quality, tags, or value have changed.
                    newValue != pointPtr->getPointValue() ||
                    newTags != pointPtr->getPointTags() ) ) )
            {
                pointPtr->setPointValue( newValue, newTime, newQuality, newTags );      // Update the pointStore.
                dependentIterator = pointPtr->getDependents( );

                for( ; (*dependentIterator)( ); )
                {
                    if( dependentIterator->key( ).dependentID != 0 )
                    {
                        _auAffectedPoints.append( dependentIterator->key( ).dependentID );
                    }
                }

                delete dependentIterator;
            }
            else if( pointPtr->getNumUpdates() == 0 )
            {
                pointPtr->firstPointValue( newValue, newTime, newQuality, newTags );
            }
        }
        else
        {
            if( changedID != ThreadMonitor.getPointIDFromOffset(ThreadMonitor.Calc) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            else if( _CALC_DEBUG & CALC_DEBUG_POINTDATA )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Recieved thread monitor update." << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCalculateThread::pointSignal( long changedID, unsigned newTags )
{
    try
    {
        RWMutexLock::LockGuard msgLock(_pointDataMutex);
        RWTValHashSetIterator<depStore, depStore, depStore> *dependentIterator;
        CtiHashKey hashKey(changedID);

        CtiPointStore* pointStore = CtiPointStore::getInstance();
        CtiPointStoreElement* pointPtr = (CtiPointStoreElement*)((*pointStore).findValue(&hashKey));
        if( _CALC_DEBUG & CALC_DEBUG_POINTDATA )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - Point Data ID: " << changedID << " Tags: " << newTags << endl;
        }

        if( pointPtr != rwnil)
        {
            if( newTags != pointPtr->getPointTags() )
            {
                pointPtr->setPointTags( newTags );      // Update the pointStore.
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCalculateThread::periodicThread( void )
{
    try
    {
        bool calcValid;
        float msLeftThisSecond;
        CtiTime newTime, tempTime;
        CtiCalcPointMapIterator periodicIter;
        RWRunnableSelf _pSelf = rwRunnable( );
        BOOL interrupted = FALSE, messageInMulti;
        clock_t now;

        CtiTime rwnow, announceTime, tickleTime;


        while( !interrupted )
        {
            rwnow = rwnow.now();
            if(rwnow > tickleTime)
            {
                tickleTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardTickleTime );
                if( rwnow > announceTime )
                {
                    announceTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardMonitorTime );
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " periodicThread thread active. TID: " << rwThreadId() << endl;
                }

                if(!_shutdownOnThreadTimeout)
                {
                    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc periodicThread", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCalculateThread::periodicComplain, 0) );
                }
                else
                {
                    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc periodicThread", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCalculateThread::sendUserQuit, CTIDBG_new string("CalcLogic _inputThread")) );
                }
            }

            //  while it's still the same second /and/ i haven't been interrupted
            for( ; newTime == tempTime && !interrupted; )
            {
                tempTime = CtiTime( );
                if( _pSelf.serviceInterrupt( ) )
                    interrupted = interrupted = ( _interruptReason == Pause ) ? FALSE : TRUE;
                else
                    _pSelf.sleep( 250 );
            }

            if( interrupted )
                continue;

            now = clock( );

            long pointId;
            CtiCalc *calcPoint;
            double newPointValue, oldPointValue;

            CtiMultiMsg *periodicMultiMsg = CTIDBG_new CtiMultiMsg;
            char pointDescription[80];

            messageInMulti = FALSE;

            int calcQuality;
            CtiTime calcTime;
            for( periodicIter = _periodicPoints.begin(); periodicIter != _periodicPoints.end()
                 ; periodicIter++ )
            {
                calcPoint = (*periodicIter).second;
                if( calcPoint==NULL || !calcPoint->ready( ) )
                {
                    continue;  // for
                }

                messageInMulti = TRUE;

                pointId = calcPoint->getPointId( );

                {
                    RWMutexLock::LockGuard msgLock(_pointDataMutex);
                    CtiPointStore* pointStore = CtiPointStore::getInstance();
                    CtiHashKey pointHashKey(calcPoint->getPointId());
                    CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore)[&pointHashKey]);

                    oldPointValue = calcPointPtr->getPointValue();
                    newPointValue = calcPoint->calculate( calcQuality, calcTime, calcValid );
                }

                calcPoint->setNextInterval(calcPoint->getUpdateInterval());

                if(!calcValid)
                {
                    if(_CALC_DEBUG & CALC_DEBUG_POSTCALC_VALUE)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " ### Calculation of point " << calcPoint->getPointId() << " was invalid (ex. div by zero or sqrt(<0))." << endl;
                    }

                    calcQuality = NonUpdatedQuality;
                    newPointValue = oldPointValue;
                }

                sprintf( pointDescription, "calc point %l update", pointId );

                CtiPointDataMsg *pointData = CTIDBG_new CtiPointDataMsg(pointId, newPointValue, calcQuality, InvalidPointType, pointDescription);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself
                pointData->setTime(calcTime);

                periodicMultiMsg->getData( ).push_back( pointData );

                if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " PeriodCalc setting Calc Point ID: " << pointId << " to New Value: " << newPointValue << endl;
                }
            }

            //  post message
            if( messageInMulti )
            {
                {
                    RWMutexLock::LockGuard calcMsgGuard(outboxMux);
                    _outbox.append( periodicMultiMsg );
                }

                if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime( ) << "  periodicThread posting a message - took " << (clock( ) - now) << " ticks" << endl;
                }
            }
            else
            {
                delete periodicMultiMsg;
            }

            newTime = tempTime;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " periodicThread interrupted. TID: " << rwThreadId() << endl;
        }
        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc periodicThread", CtiThreadRegData::LogOut ));
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCalculateThread::onUpdateThread( void )
{
    ThreadMonitor.start(); //ecs 1/5/2005

    try
    {
        bool calcValid;
        long pointIDChanged, recalcPointID;
        double recalcValue, oldCalcValue;
        char pointDescription[80];
        BOOL interrupted = FALSE, pointsInMulti;
        CtiMultiMsg *pChg;
        CtiCalc *calcPoint;
        RWRunnableSelf _auSelf = rwRunnable( );

        int calcQuality;
        CtiTime calcTime;

        CtiTime rwnow, announceTime, tickleTime;

        while( !interrupted )
        {
            //  this is the cleanest way to do it;  i know it's a do-while, but that gets rid of an unnecessary if statement
            do
            {
                rwnow = rwnow.now();
                if(rwnow > tickleTime)
                {
                    tickleTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardTickleTime );
                    if( rwnow > announceTime )
                    {
                        announceTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardMonitorTime );
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " onUpdateThread thread active. TID: " << rwThreadId() << endl;
                    }

                    if(!_shutdownOnThreadTimeout)
                    {
                        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc onUpdateThread", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCalculateThread::periodicComplain, 0) );
                    }
                    else
                    {
                        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc onUpdateThread", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCalculateThread::sendUserQuit, CTIDBG_new string("CalcLogic _inputThread")) );
                    }
                }

                if( _auSelf.serviceInterrupt( ) )
                {
                    interrupted = ( _interruptReason == Pause ) ? FALSE : TRUE;
                }
                else
                {
                    _auSelf.sleep( 250 );
                }

            } while( !_auAffectedPoints.entries( ) && !interrupted );

            //  if I was interrupted while I was sleeping, I don't care about any points I may have received - it's time to go.
            if( interrupted )
                continue;

            pChg = CTIDBG_new CtiMultiMsg;
            pointsInMulti = FALSE;

            //  get the mutex while we're accessing the _auAffectedPoints collection
            //  (it's accessed by pointChange as well)
            {
                RWMutexLock::LockGuard msgLock(_pointDataMutex);
                while( _auAffectedPoints.entries( ) )
                {
                    recalcPointID = _auAffectedPoints.removeFirst( );
                    CtiHashKey recalcKey(recalcPointID);
                    std::map<CtiHashKey*, CtiCalc* >::iterator itr = _onUpdatePoints.find(&recalcKey);

                    //  if not ready
                    if( itr == _onUpdatePoints.end() || !calcPoint->ready( ) )
                         continue;  // All the components are not ready.

                    calcPoint = (*itr).second;

                    CtiPointStore* pointStore = CtiPointStore::getInstance();
                    CtiHashKey pointHashKey(calcPoint->getPointId());
                    CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore)[&pointHashKey]);

                    oldCalcValue = calcPointPtr->getPointValue();
                    recalcValue = calcPoint->calculate( calcQuality, calcTime, calcValid );    // Here is the MATH
                    calcPoint->setNextInterval(calcPoint->getUpdateInterval());     // This only matters for periodicPlusUpdatePoints.

                    if(!calcValid)
                    {
                        if(_CALC_DEBUG & CALC_DEBUG_POSTCALC_VALUE)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ### Calculation of point " << calcPoint->getPointId() << " was invalid (ex. div by zero or sqrt(<0))." << endl;
                        }

                        calcQuality = NonUpdatedQuality;
                        recalcValue = oldCalcValue;
                    }


                    // Make sure we do not try to move backwards in time.
                    if( calcPointPtr->getPointTime() > calcTime )
                    {
                        if( _CALC_DEBUG & CALC_DEBUG_POINTDATA_QUALITY )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Calc point " << calcPoint->getPointId() << " calculation result is attempting to move backward in time." << endl;
                            dout << CtiTime() << "   Update type may be inappropriate for the component device/points." << endl;
                        }
                        calcTime = CtiTime();
                    }

                    CtiPointDataMsg *pData = NULL;

                    if( calcPointPtr->getPointCalcWindowEndTime() > CtiTime(CtiDate(1,1,1991)) )
                    {// demand average point madness

                        long davgpid = calcPoint->findDemandAvgComponentPointId();

                        if(davgpid)
                        {
                            CtiHashKey componentPointHashKey(davgpid);
                            CtiPointStoreElement* componentPointPtr = 0;

                            componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentPointHashKey]);

                            if(componentPointPtr)
                            {

                                CtiTime now;
                                CtiTime et = calcPointPtr->getPointCalcWindowEndTime();
                                CtiTime etplus = (calcPointPtr->getPointCalcWindowEndTime() + componentPointPtr->getSecondsSincePreviousPointTime());

                                if( et <= now &&  now < etplus )    // Are we greater than the end time, but less than the end time + "slop"
                                {
                                    if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " - New Point Data message for Calc Point Id: " << recalcPointID << " New Demand Avg Value: " << recalcValue << endl;
                                    }
                                    pData = CTIDBG_new CtiPointDataMsg(recalcPointID, recalcValue, calcQuality, InvalidPointType);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself
                                    pData->setTime(calcPointPtr->getPointCalcWindowEndTime());
                                }

                                calcPointPtr->setPointValue( recalcValue, CtiTime(), NormalQuality, 0 );
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Error Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Calc Point " << calcPoint->getPointId() << endl;
                                }
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** CONFIG ERROR **** Demand Average points require a point to be identified (no pre-push).  Point ID " << calcPoint->getPointId() << endl;
                            }
                        }
                    }
                    else
                    {//normal calc point
                        pData = CTIDBG_new CtiPointDataMsg(recalcPointID, recalcValue, calcQuality, InvalidPointType);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself
                        pData->setTime(calcTime);
                    }

                    if( pData != NULL )
                    {
                        pointsInMulti = TRUE;
                        sprintf( pointDescription, "calc point %l update", recalcPointID );
                        pData->setString(pointDescription);
                        pChg->getData( ).push_back( pData );
                    }

                    if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " onUpdateThread setting Calc Point ID: " << recalcPointID << " to New Value: " << recalcValue << endl;
                    }
                }
            }

            if( pointsInMulti )
            {
                {
                    RWMutexLock::LockGuard outboxGuard(outboxMux);
                    _outbox.append( pChg );
                }

                //  i kinda want to keep away from having a hold on both of the mutexes, as a little programming error
                //    earlier earned me a touch of the deadlock.
                if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime( ) << "  onUpdateThread posting a message" << endl;
                }
            }
            else
            {
                delete pChg;
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " onUpdateThread interrupted. TID: " << rwThreadId() << endl;
        }
        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc onUpdateThread", CtiThreadRegData::LogOut ) );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCalculateThread::historicalThread( void )
{
    try
    {
        bool calcValid, reloaded = false;
        BOOL interrupted = FALSE, pointsInMulti;
        CtiMultiMsg *pChg;
        CtiCalc *calcPoint;
        CtiCalcPointMapIterator historicIter;
        RWRunnableSelf _auSelf = rwRunnable( );
        PointTimeMap unlistedPoints, updatedPoints;
        DynamicTableData data;
        int frequencyInSeconds;
        int initialDays;
        char var[256];

        int calcQuality;
        CtiTime nextCalcTime;

        CtiTime rwnow, announceTime, tickleTime, lastTime, start;

        strcpy(var, "CALC_HISTORICAL_FREQUENCY_IN_SECONDS");
        if( 0 != (frequencyInSeconds = gConfigParms.getValueAsInt(var,0)) )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << CtiNumStr(frequencyInSeconds).toString() << endl;
            }
        }
        else
        {
            frequencyInSeconds = 60*60;//60 minutes
        }

        strcpy(var, "CALC_HISTORICAL_INITIAL_DAYS_CALCULATED");
        if( 0 != (initialDays = gConfigParms.getValueAsInt(var,0)) )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << var << ":  " << CtiNumStr(initialDays).toString() << endl;
            }
        }
        else
        {
            initialDays = 0;//0 days
        }

        while( !interrupted )
        {
            do
            {
                rwnow = rwnow.now();
                if(rwnow > tickleTime)
                {
                    tickleTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardTickleTime );
                    if( rwnow > announceTime )
                    {
                        announceTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardMonitorTime );
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " HistoricalThread thread active. TID: " << rwThreadId() << endl;
                    }

                    if(!_shutdownOnThreadTimeout)
                    {
                        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc HistoricalThread", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCalculateThread::periodicComplain, 0) );
                    }
                    else
                    {
                        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc HistoricalThread", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCalculateThread::sendUserQuit, CTIDBG_new string("CalcLogic _inputThread")) );
                    }
                }

                if( _auSelf.serviceInterrupt( ) )
                {
                    interrupted = ( _interruptReason == Pause ) ? FALSE : TRUE;
                }
                else
                {
                    _auSelf.sleep( 2000 );//Historical doesnt do much most of the time, it can sleep for several seconds
                }

            } while( !(rwnow >= nextCalcTime) && !interrupted );

            //  if I was interrupted while I was sleeping, I don't care about processing, its time to go.
            if( interrupted )
                continue;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Historical Calculation beginning." << endl;
            }
            start = start.now();

            pChg = CTIDBG_new CtiMultiMsg;
            pointsInMulti = FALSE;

            PointTimeMap dbTimeMap;
            getCalcHistoricalLastUpdatedTime(dbTimeMap);
            PointTimeMap::iterator dbTimeMapIter;
            long pointID;
            long componentCount;
            double newPointValue;
            CtiTime calcTime;
            char pointDescription[80];

            reloaded = false;

            for( historicIter = _historicalPoints.begin(); (historicIter != _historicalPoints.end()) && !reloaded ;  historicIter++ )
            {
                calcPoint = (*historicIter).second;
                if( calcPoint==NULL || !calcPoint->ready( ) )
                {
                    continue;  // for
                }

                if( _auSelf.serviceInterrupt( ) )
                {
                    reloaded = true;//We were interrupted, our historicIter cant be trusted, we need to re-run our loop
                    interrupted = ( _interruptReason == Pause ) ? FALSE : TRUE;//if its a shutdown, set this flag also.
                    continue;//for
                }

                if( calcPoint->isBaselineCalc() )
                {
                    continue;//for
                }

                pointID = calcPoint->getPointId( );

                if( (dbTimeMapIter = dbTimeMap.find( pointID )) != dbTimeMap.end() )//Entry is in the database
                {
                    lastTime = dbTimeMapIter->second;
                }
                else
                {
                    lastTime = CtiTime((unsigned)0, (unsigned)0) - initialDays*60*60*24;//This should return today, -initialDays days.
                    unlistedPoints.insert(PointTimeMap::value_type(pointID, lastTime));
                }

                getHistoricalTableData(calcPoint, lastTime, data);
                componentCount = calcPoint->getComponentCount();

                DynamicTableDataIter iter;
                CtiTime newTime = (unsigned long)0;
                for( iter = data.begin(); iter!=data.end(); iter++ )
                {
                    if( iter->second.size() == componentCount )
                    {
                        //This means all the necessary points in historical have been updated, we can do a calc
                        setHistoricalPointStore(iter->second);//Takes the value/paoid pair and sets the values in the point store

                        CtiPointStore* pointStore = CtiPointStore::getInstance();
                        CtiHashKey pointHashKey(calcPoint->getPointId());
                        CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore)[&pointHashKey]);

                        newPointValue = calcPoint->calculate( calcQuality, calcTime, calcValid );

                        calcPoint->setNextInterval(calcPoint->getUpdateInterval());

                        if(!calcValid)
                        {
                            //even if we were invalid, we need to move on and try the next time, otherwise we will constantly retry
                            if(_CALC_DEBUG & CALC_DEBUG_POSTCALC_VALUE)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " ### Calculation of historical point " << calcPoint->getPointId() << " was invalid (ex. div by zero or sqrt(<0))." << endl;
                            }

                            calcQuality = NonUpdatedQuality;
                            newPointValue = 0;
                        }


                        sprintf( pointDescription, "calc point %l update", pointID );

                        CtiPointDataMsg *pointData = CTIDBG_new CtiPointDataMsg(pointID, newPointValue, NormalQuality, InvalidPointType, pointDescription);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself
                        pointData->setTime(iter->first);//The time these points were entered in the historical log
                        newTime = iter->first;//we do this in order of time, so the last one is the time we want.

                        pChg->getData( ).push_back( pointData );
                        pointsInMulti = TRUE;

                        if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " HistoricCalc setting Calc Point ID: " << pointID << " to New Value: " << newPointValue << endl;
                        }
                    }
                }
                PointTimeMap::iterator Tpair = unlistedPoints.find(calcPoint->getPointId());
                if( newTime > (unsigned long)0 && Tpair == unlistedPoints.end() )
                {
                    updatedPoints.insert(PointTimeMap::value_type(calcPoint->getPointId(), newTime));
                }
            }

            if( !reloaded )
            {
                rwnow = rwnow.now();
                nextCalcTime = nextScheduledTimeAlignedOnRate( rwnow, frequencyInSeconds );
            }

            updateCalcHistoricalLastUpdatedTime(unlistedPoints, updatedPoints);//Write these back out to the database

            updatedPoints.clear();//Next time through these need to be clear.
            unlistedPoints.clear();

            if( pointsInMulti )
            {
                {
                    RWMutexLock::LockGuard outboxGuard(outboxMux);
                    _outbox.append( pChg );
                }

                if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime( ) << "  historical posting a message" << endl;
                }
            }
            else
            {
                delete pChg;
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Historical Calculation completed in "<< start.now().seconds()-start.seconds() << " seconds" << endl;
                dout << CtiTime() << " Next calculation is at " << nextCalcTime << endl;
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " historical interrupted. TID: " << rwThreadId() << endl;
        }
        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc historicalThread", CtiThreadRegData::LogOut ) );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCalculateThread::baselineThread( void )
{
    try
    {
        bool calcValid, reloaded = false;
        BOOL interrupted = FALSE, pointsInMulti;
        CtiMultiMsg *pChg;
        CtiCalc *calcPoint;
        CtiCalcPointMapIterator historicIter;
        RWRunnableSelf _auSelf = rwRunnable( );
        PointTimeMap unlistedPoints, updatedPoints;
        DynamicTableSinglePointData data;
        DynamicTableSinglePointData percentData;
        DatesSet curtailedDates;
        int frequencyInSeconds = 24*60*60;//24 hours;
        int prevDaysToCalc = 30;//30 days
        char var[256];

        int calcQuality;
        CtiTime nextCalcTime(4,16,0);//Today at 4:16 AM
        CtiTime rwnow, announceTime, tickleTime, lastTime, start;

        if( nextCalcTime < rwnow.now() )
        {
            nextCalcTime.addDays(1);
        }
        if( !stringCompareIgnoreCase(gConfigParms.getValueAsString("CALC_LOGIC_RUN_BASELINE_ON_STARTUP"),"true") )
        {
            nextCalcTime.addDays(-1);//Should let us run immediatelly
        }

        while( !interrupted )
        {
            do
            {
                rwnow = rwnow.now();
                if(rwnow > tickleTime)
                {
                    tickleTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardTickleTime );
                    if( rwnow > announceTime )
                    {
                        announceTime = nextScheduledTimeAlignedOnRate( rwnow, CtiThreadMonitor::StandardMonitorTime );
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " BaselineThread thread active. TID: " << rwThreadId() << endl;
                    }

                    if(!_shutdownOnThreadTimeout)
                    {
                        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc BaselineThread", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCalculateThread::periodicComplain, 0) );
                    }
                    else
                    {
                        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc BaselineThread", CtiThreadRegData::Action, CtiThreadMonitor::StandardMonitorTime, &CtiCalculateThread::sendUserQuit, CTIDBG_new string("CalcLogic _inputThread")) );
                    }
                }

                if( _auSelf.serviceInterrupt( ) )
                {
                    interrupted = ( _interruptReason == Pause ) ? FALSE : TRUE;
                }
                else
                {
                    _auSelf.sleep( 5000 );//baseline doesnt do much almost all of the time, it can sleep for as long as we can wait on shutdown
                }

            } while( !(rwnow >= nextCalcTime) && !interrupted );

            //  if I was interrupted while I was sleeping, I don't care about processing, its time to go.
            if( interrupted )
                continue;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Baseline Calculation beginning." << endl;
            }
            start = start.now();

            pChg = CTIDBG_new CtiMultiMsg;
            pointsInMulti = FALSE;

            PointTimeMap dbTimeMap;
            PointBaselineMap calcBaselineMap;
            BaselineMap baselineMap;
            getCalcHistoricalLastUpdatedTime(dbTimeMap);
            getCalcBaselineMap(calcBaselineMap);
            getBaselineMap(baselineMap);
            CtiHolidayManager& holidayManager = CtiHolidayManager::getInstance();
            holidayManager.refresh();
            PointTimeMap::iterator dbTimeMapIter;
            long pointID, baselinePercentID, baselineID;
            double newPointValue;
            CtiTime calcTime;
            char pointDescription[80];

            reloaded = false;

            for( historicIter = _historicalPoints.begin() ; (historicIter != _historicalPoints.end()) && !reloaded ; historicIter++ )
            {
                calcPoint = (*historicIter).second;
                if( calcPoint==NULL || !calcPoint->ready( ) )
                {
                    continue;  // for
                }

                if( _auSelf.serviceInterrupt( ) )
                {
                    reloaded = true;//We were interrupted, our historicIter cant be trusted, we need to re-run our loop
                    interrupted = ( _interruptReason == Pause ) ? FALSE : TRUE;//if its a shutdown, set this flag also.
                    continue;//for
                }

                if( !calcPoint->isBaselineCalc() )
                {
                    continue;//for
                }

                pointID = calcPoint->getPointId( );
                baselineID = calcPoint->getBaselineId();
                baselinePercentID = calcPoint->getBaselinePercentId();

                if( (dbTimeMapIter = dbTimeMap.find( pointID )) != dbTimeMap.end() )//Entry is in the database
                {
                    lastTime = dbTimeMapIter->second;
                    lastTime = CtiTime::CtiTime(lastTime.date(),0,0,0);
                    if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime( ) << "  lastTime for " << pointID << " is " << lastTime << endl;
                    }
                }
                else
                {
                    lastTime = CtiTime((unsigned)0, (unsigned)0);
                    lastTime.addDays(-1*prevDaysToCalc);//This should return 30 days ago, at 00:00:00
                    unlistedPoints.insert(PointTimeMap::value_type(pointID, lastTime));

                    if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime( ) << "  lastTime for " << pointID << " is " << lastTime << endl;
                    }
                }

                PointBaselineMap::iterator pointBaselineIter;
                BaselineMap::iterator baselineIter;
                CtiTime searchTime;
                BaselineData* baselineDataPtr;
                if( (pointBaselineIter = calcBaselineMap.find(pointID)) != calcBaselineMap.end() )
                {
                    if( (baselineIter = baselineMap.find(pointBaselineIter->second)) != baselineMap.end() )
                    {
                        //Either this is given, or we get a new point
                        baselineDataPtr = &(baselineIter->second);
                    }
                    else
                    {
                        //This is really odd
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " No baseline data found for point: "<< pointID << " trying next point" << endl;
                        continue;//move on to next point
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " No baseline ID found for point: "<< pointID << " trying next point" << endl;
                    continue;//move on to another point
                }

                searchTime = lastTime - baselineDataPtr->maxSearchDays*24*60*60;//Go back maxSearchDays days.

                //grab all the data we could ever possibly want, this gives us just 2 db reads.
                getHistoricalTableSinglePointData(baselineID, searchTime, data);
                getHistoricalTableSinglePointData(baselinePercentID, searchTime, percentData);
                getCurtailedDates(curtailedDates, pointID, searchTime);

                CtiTime curCalculatedTime;
                for( ; ; )//Until we break!
                {
                    DynamicTableSinglePointData::iterator lastIter;
                    if( !data.empty() )
                    {
                        lastIter = data.end();
                        lastIter--;//now is the last element in the list
                    }
                    else
                    {
                        break;
                    }
                    //Pick the first day, if re-looping check if we have entries that reach at least our max possible time for this calc
                    if( lastIter->first.seconds() <= lastTime.seconds() )
                    {
                        //we are checking to see if we have a time that is far enough along to do a full calc
                        break;
                    }

                    //If we do have days beyond this, try to calculate that days data, repeat until we have no data then break
                    vector<HourlyValues> dayValues;
                    CtiTime curTime = lastTime;
                    //Until we have enough days
                    while( dayValues.size() < baselineDataPtr->usedDays )
                    {
                        if( curTime < searchTime ) //failure
                        {
                            break;//while
                        }
                        //Check if current day is ok to use (non holiday/other day)
                        //If not, decrement day
                        if( holidayManager.isHoliday(curTime.date(), baselineDataPtr->holidays) )
                        {
                            curTime.addDays(-1);
                            continue;//while loop
                        }
                        else if( baselineDataPtr->excludedWeekDays.length() >= 7 &&
                                 (baselineDataPtr->excludedWeekDays[curTime.date().weekDay()] == 'Y' ||
                                  baselineDataPtr->excludedWeekDays[curTime.date().weekDay()] == 'y') )//Sunday = 0
                        {
                            curTime.addDays(-1);
                            continue;
                        }
                        else if( curtailedDates.find(curTime.date()) != curtailedDates.end() )
                        {
                            curTime.addDays(-1);
                            continue;
                        }
                        else
                        {
                            //If ok, try to get data
                            HourlyValues results;
                            if( processDay(baselineID, curTime, data, percentData, baselineDataPtr->percent, results) )
                            {
                                //If data is ok, store data and decrement day
                                dayValues.push_back(results);
                            }
                            curTime.addDays(-1);
                        }
                    }
                    //Now that we have all our days, calculate and record data., then let loop handle doing the next day's calc
                    if( dayValues.size() == baselineDataPtr->usedDays )
                    {
                        CtiTime pointTime = lastTime;//Our first returned value should return this+1 hour, and so on...
                        for( int a=0; a<24; a++ )
                        {
                            double pointValue = 0;
                            for( int b=0; b<baselineDataPtr->usedDays; b++ )
                            {
                                pointValue += dayValues[b][a];//dayValues[day][hour]
                            }
                            pointValue = pointValue/baselineDataPtr->usedDays;

                            sprintf( pointDescription, "calc point %1 update", pointID );
                            CtiPointDataMsg *pointData = CTIDBG_new CtiPointDataMsg(pointID, pointValue, NormalQuality, InvalidPointType, pointDescription, TAG_POINT_MUST_ARCHIVE);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself
                            pointData->setTime(pointTime.addMinutes(60));//The time these points will appear in historical

                            pChg->getData( ).push_back( pointData );
                            pointsInMulti = TRUE;
                        }

                        PointTimeMap::iterator iter;
                        iter = updatedPoints.find(pointID);
                        if( iter != updatedPoints.end() )
                        {
                            iter->second = pointTime;

                            if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime( ) << "  Sending to point id " << pointID << " and time " << pointTime << endl;
                            }
                        }
                        else if( iter != unlistedPoints.end() )
                        {
                            iter->second = pointTime;

                            if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime( ) << "  Sending to point id " << pointID << " and time " << pointTime << endl;
                            }
                        }
                        else
                        {
                            updatedPoints.insert(PointTimeMap::value_type(pointID, pointTime));

                            if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime( ) << "  Sending to point id " << pointID << " and time " << pointTime << endl;
                            }
                        }

                    }

                    //It is worth noting we could make the calc on future days more efficient, but this should not happen very often
                    //The general case is we are only calculating 1 new day each time baseline is run.
                    lastTime.addDays(1);//Causes us to calc on the next day, yay!
                }
            }

            if( !reloaded )
            {
                rwnow = rwnow.now();
                nextCalcTime = CtiTime(nextCalcTime.seconds() + (unsigned long)frequencyInSeconds);
            }

            updateCalcHistoricalLastUpdatedTime(unlistedPoints, updatedPoints);//Write these back out to the database

            updatedPoints.clear();//Next time through these need to be clear.
            unlistedPoints.clear();

            if( pointsInMulti )
            {
                {
                    RWMutexLock::LockGuard outboxGuard(outboxMux);
                    _outbox.append( pChg );
                }

                if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime( ) << "  historical posting a message" << endl;
                }
            }
            else
            {
                delete pChg;
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Baseline Calculation completed in "<< start.now().seconds()-start.seconds() << " seconds" << endl;
                dout << CtiTime() << " Next calculation is at " << nextCalcTime << endl;
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " historical interrupted. TID: " << rwThreadId() << endl;
        }
        ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc historicalThread", CtiThreadRegData::LogOut ) );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCalculateThread::appendCalcPoint( long pointID )
{
    CtiPointStore::getInstance()->insertPointElement( pointID, 0, undefined );
}

void CtiCalculateThread::calcThread( void )
{
    int cnt = 0;
    RWRunnableSelf _self = rwRunnable( );

    startThreads();

    //  while nobody's bothering me...
    while( !_self.serviceInterrupt( ) )
    {
       _self.sleep( 1000 );
    }

    //  scream at the other threads, tell them it's time for dinner
    interruptThreads( CtiCalcThreadInterruptReason::Shutdown );
    joinThreads();
}

void CtiCalculateThread::startThreads(  )
{
    _periodicThreadFunc   = rwMakeThreadFunction( (*this), &CtiCalculateThread::periodicThread );
    _onUpdateThreadFunc   = rwMakeThreadFunction( (*this), &CtiCalculateThread::onUpdateThread );
    _periodicThreadFunc.start( );
    _onUpdateThreadFunc.start( );

    if( _runCalcHistorical )
    {
        _historicalThreadFunc = rwMakeThreadFunction( (*this), &CtiCalculateThread::historicalThread );
        _historicalThreadFunc.start( );
    }

    if( _runCalcBaseline )
    {
        _baselineThreadFunc   = rwMakeThreadFunction( (*this), &CtiCalculateThread::baselineThread );
        _baselineThreadFunc.start( );
    }
    return;
}

void CtiCalculateThread::joinThreads(  )
{
    resumeThreads();

    if(RW_THR_COMPLETED != _periodicThreadFunc.join( 30000 ))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " periodicThread did not shutdown gracefully.  Will attempt a forceful shutdown." << endl;
        }
        _periodicThreadFunc.terminate();
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " periodicThread shutdown correctly." << endl;
        }
    }

    if(RW_THR_COMPLETED != _onUpdateThreadFunc.join( 30000 ))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " onUpdateThread did not shutdown gracefully.  Will attempt a forceful shutdown." << endl;
        }

        _onUpdateThreadFunc.terminate();
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " onUpdateThread shutdown correctly." << endl;
        }
    }

    if( _runCalcHistorical )
    {
        if(RW_THR_COMPLETED != _historicalThreadFunc.join( 30000 ))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " historicalThread did not shutdown gracefully.  Will attempt a forceful shutdown." << endl;
            }

            _historicalThreadFunc.terminate();
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " historicalThread shutdown correctly." << endl;
            }
        }
    }

    if( _runCalcBaseline )
    {
        if(RW_THR_COMPLETED != _baselineThreadFunc.join( 30000 ))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " baselineThread did not shutdown gracefully.  Will attempt a forceful shutdown." << endl;
            }

            _baselineThreadFunc.terminate();
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " baselineThread shutdown correctly." << endl;
            }
        }
    }
    return;
}

void CtiCalculateThread::interruptThreads( CtiCalcThreadInterruptReason reason )
{
    _interruptReason = reason;

    if( RW_THR_TIMEOUT == _periodicThreadFunc.requestInterrupt( 5000 ) )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Unable to interrupt the periodicThread. Calc may become unstable!" << endl;
        }
    }

    if( RW_THR_TIMEOUT == _onUpdateThreadFunc.requestInterrupt( 5000 ) )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Unable to interrupt the onUpdateThread. Calc may become unstable!" << endl;
        }
    }

    if( _runCalcHistorical && RW_THR_TIMEOUT == _historicalThreadFunc.requestInterrupt( 5000 ) )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Unable to interrupt the historicalThread. Calc may become unstable!" << endl;
        }
    }

    if( _runCalcBaseline && RW_THR_TIMEOUT == _baselineThreadFunc.requestInterrupt( 5000 ) )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Unable to interrupt the baselineThread. Calc may become unstable!" << endl;
        }
    }

    if( _CALC_DEBUG & CALC_DEBUG_RELOAD )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " CalcThreads interrupted"<< endl;
    }

}

void CtiCalculateThread::resumeThreads(  )
{
    try
    {
        _onUpdateThreadFunc.releaseInterrupt( );
        _periodicThreadFunc.releaseInterrupt( );

        if( _runCalcHistorical )
        {
            _historicalThreadFunc.releaseInterrupt( );
        }

        if( _runCalcBaseline )
        {
            _baselineThreadFunc.releaseInterrupt( );
        }
    }
    catch(RWTHRIllegalUsage &msg)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << msg.why() <<  " " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

bool CtiCalculateThread::appendPoint( long pointid, string &updatetype, int updateinterval, string &qualityFlag )
{
    std::pair< std::map<CtiHashKey*,CtiCalc*>::iterator , bool > inserted;

    CtiCalc *newPoint;
    newPoint = CTIDBG_new CtiCalc( pointid, updatetype, updateinterval, qualityFlag );
    switch( newPoint->getUpdateType( ) )
    {
    case periodic:
        inserted = _periodicPoints.insert( std::pair<CtiHashKey*,CtiCalc*>(CTIDBG_new CtiHashKey(pointid), newPoint) );
        break;
    case allUpdate:
    case anyUpdate:
    case periodicPlusUpdate:
        inserted = _onUpdatePoints.insert(   std::pair<CtiHashKey*,CtiCalc*> (CTIDBG_new CtiHashKey(pointid), newPoint) );
        break;
    case constant:
        inserted = _constantPoints.insert(   std::pair<CtiHashKey*,CtiCalc*> (CTIDBG_new CtiHashKey(pointid), newPoint) );
        break;
    case historical:
        inserted = _historicalPoints.insert( std::pair<CtiHashKey*,CtiCalc*> (CTIDBG_new CtiHashKey(pointid), newPoint) );
        break;
    default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ") Attempt to insert unknown CtiCalc point type \"" << updatetype
            << "\", value \"" << newPoint->getUpdateType() << "\";  aborting point insert" << endl;
        }

        delete newPoint;
        newPoint = NULL;
        break;
    }
    //was if( !inserted && newPoint != NULL )
    if( !inserted.second && newPoint != NULL )
    {
        delete newPoint;
        newPoint = NULL;
    }
    return inserted.second;
}


void CtiCalculateThread::appendPointComponent( long pointID, string &componentType, long componentPointID,
                                               string &operationType, double constantValue, string &functionName )
{
    CtiHashKey pointHashKey(pointID);
    CtiCalc *targetCalcPoint = NULL;
    CtiCalcComponent *newComponent = NULL;
    CtiPointStoreElement *tmpElementPtr = NULL;
    PointUpdateType updateType;

    if( _periodicPoints.find( &pointHashKey ) != _periodicPoints.end() )
    {
        targetCalcPoint  = _periodicPoints[&pointHashKey];
        updateType = periodic;
    }
    else if( _onUpdatePoints.find( &pointHashKey ) != _onUpdatePoints.end() )
    {
        targetCalcPoint  = _onUpdatePoints[&pointHashKey];
        updateType = targetCalcPoint->getUpdateType();
    }
    else if( _constantPoints.find( &pointHashKey ) != _constantPoints.end() )
    {
        targetCalcPoint  = _constantPoints[&pointHashKey];
        updateType = constant;
    }
    else if( _historicalPoints.find( &pointHashKey ) != _historicalPoints.end())
    {
        targetCalcPoint = _historicalPoints[&pointHashKey];
        updateType = historical;
    }
    else if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << __FILE__ << " (" << __LINE__ << ") Can't find calc point \"" << pointID << "\" in either point collection (historical point?)" << endl;
        return;
    }

    //  it's okay to toss this pointer (tmpElementPtr - the newly inserted point element)
    //    around because it is copied into a (const CtiPointStoreElement *) inside the
    //    CtiCalcComponent.  the "messiness" is justified by eliminating the hash
    //    lookup for each CtiCalcComponent point value access.
    //  CtiCalculateThread is going to be doing the cleanup, anyway, so the garbage will all be
    //    collected in one place.

    if( targetCalcPoint != NULL )
    {
        //  insert parameters are (point, dependent, updatetype)
        tmpElementPtr = CtiPointStore::getInstance()->insertPointElement( componentPointID, pointID, updateType );
        newComponent = CTIDBG_new CtiCalcComponent( componentType, componentPointID, operationType, constantValue, functionName );
        targetCalcPoint->appendComponent( newComponent );
    }
}


/*-----------------------------------------------------------------------------*
* Function Name: isACalcPointID()
*
* Description: returns true if the PointID is a Calc Point ID
*
*
*-----------------------------------------------------------------------------*
*/
BOOL CtiCalculateThread::isACalcPointID(const long aPointID)
{
    BOOL foundPoint(FALSE);

    foundPoint = isAPeriodicCalcPointID(aPointID);

    if(foundPoint == FALSE)
    {
        // ID was not found yet look here...
        foundPoint = isAnOnUpdateCalcPointID(aPointID);
    }

    if(foundPoint == FALSE)
    {
        // ID was not found yet look here...
        foundPoint = isAConstantCalcPointID(aPointID);
    }

    if(foundPoint == FALSE)
    {
        // ID was not found yet look here...
        foundPoint = isAHistoricalCalcPointID(aPointID);
    }

    return foundPoint;
}


BOOL CtiCalculateThread::isAPeriodicCalcPointID(const long aPointID)
{
    CtiCalc *calcPoint;

    BOOL foundPoint(FALSE);

    CtiHashKey recalcKey(aPointID);

    calcPoint = (CtiCalc *)(*(_periodicPoints.find(&recalcKey))).second;

    if(calcPoint != rwnil)
    {
        foundPoint = TRUE;
    }

    return foundPoint;
}


BOOL CtiCalculateThread::isAnOnUpdateCalcPointID(const long aPointID)
{

    CtiCalc *calcPoint;

    BOOL foundPoint(FALSE);

    CtiHashKey recalcKey(aPointID);

    calcPoint = (CtiCalc *)(*_onUpdatePoints.find(&recalcKey)).second;

    if(calcPoint != rwnil)
    {
        foundPoint = TRUE;
    }

    return foundPoint;
}

BOOL CtiCalculateThread::isAConstantCalcPointID(const long aPointID)
{

    CtiCalc *calcPoint;

    BOOL foundPoint(FALSE);

    CtiHashKey recalcKey(aPointID);

    calcPoint = (* _constantPoints.find(&recalcKey)).second ;

    if(calcPoint != rwnil)
    {
        foundPoint = TRUE;
    }

    return foundPoint;
}

BOOL CtiCalculateThread::isAHistoricalCalcPointID(const long aPointID)
{

    CtiCalc *calcPoint;

    BOOL foundPoint(FALSE);

    CtiHashKey recalcKey(aPointID);

    calcPoint = (CtiCalc *)(*_historicalPoints.find(&recalcKey)).second;

    if(calcPoint != rwnil)
    {
        foundPoint = TRUE;
    }

    return foundPoint;
}

CtiCalculateThread::CtiCalcPointMap CtiCalculateThread::getPeriodicPointMap() const
{
    return _periodicPoints;
}
CtiCalculateThread::CtiCalcPointMap CtiCalculateThread::getOnUpdatePointMap() const
{
    return _onUpdatePoints;
}
CtiCalculateThread::CtiCalcPointMap CtiCalculateThread::getConstantPointMap() const
{
    return _constantPoints;
}
CtiCalculateThread::CtiCalcPointMap CtiCalculateThread::getHistoricalPointMap() const
{
    return _historicalPoints;
}

void CtiCalculateThread::setPeriodicPointMap(const CtiCalcPointMap &points)
{
    if( _periodicPoints.size() > 0 )
    {
        delete_map(_periodicPoints);
        _periodicPoints.clear();
    }
    _periodicPoints = points;
}

void CtiCalculateThread::setOnUpdatePointMap(const CtiCalcPointMap &points)
{
    if( _onUpdatePoints.size() > 0 )
    {
        delete_map(_onUpdatePoints);
        _onUpdatePoints.clear();
    }
    _onUpdatePoints = points;
}

void CtiCalculateThread::setConstantPointMap(const CtiCalcPointMap &points)
{
    if( _constantPoints.size() > 0 )
    {
        delete_map(_constantPoints);
        _constantPoints.clear();
    }
    _constantPoints = points;
}

void CtiCalculateThread::setHistoricalPointMap(const CtiCalcPointMap &points)
{
    if( _historicalPoints.size() > 0 )
    {
        delete_map(_historicalPoints);
        _historicalPoints.clear();
    }
    _historicalPoints = points;
}

void CtiCalculateThread::clearAndDestroyPointMaps()
{
    try
    {
        if( _constantPoints.size() > 0 )
        {
            delete_map(_constantPoints);
            _constantPoints.clear();
        }

        if( _onUpdatePoints.size() > 0 )
        {
            delete_map(_onUpdatePoints);
            _onUpdatePoints.clear();
        }

        if( _periodicPoints.size() > 0 )
        {
            delete_map(_periodicPoints);
            _periodicPoints.clear();
        }

        if( _historicalPoints.size() > 0 )
        {
            delete_map(_historicalPoints);
            _historicalPoints.clear();
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void CtiCalculateThread::clearPointMaps()
{
    if( _constantPoints.size() > 0 )
    {
        _constantPoints.clear();
    }

    if( _onUpdatePoints.size() > 0 )
    {
        _onUpdatePoints.clear();
    }

    if( _periodicPoints.size() > 0 )
    {
        _periodicPoints.clear();
    }

    if( _historicalPoints.size() > 0 )
    {
        _historicalPoints.clear();
    }
}

void CtiCalculateThread::removePointStoreObject( const long aPointID )
{
    CtiHashKey pointHashKey(aPointID);
    CtiCalc *targetCalcPoint = NULL;
    CtiPointStoreElement *tmpElementPtr = NULL;

    if( _periodicPoints.find( &pointHashKey ) != _periodicPoints.end() )
    {
        targetCalcPoint  = _periodicPoints[&pointHashKey];
    }
    else if( _onUpdatePoints.find( &pointHashKey ) != _onUpdatePoints.end() )
    {
        targetCalcPoint  = _onUpdatePoints[&pointHashKey];
    }
    else if( _constantPoints.find( &pointHashKey ) != _constantPoints.end() )
    {
        targetCalcPoint  = _constantPoints[&pointHashKey];
    }
    else if( _historicalPoints.find( &pointHashKey ) != _historicalPoints.end())
    {
        targetCalcPoint = _historicalPoints[&pointHashKey];
    }
    else if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << __FILE__ << " (" << __LINE__ << ") Can't find calc point \"" << aPointID << "\" in either point collection (historical point?)" << endl;
        return;
    }

    if( targetCalcPoint )
    {
        targetCalcPoint->clearComponentDependencies();

        CtiPointStore* pointStore = CtiPointStore::getInstance();
        pointStore->removePointElement( aPointID );
    }
}

void CtiCalculateThread::sendConstants()
{
    long pointId;
    CtiCalc *calcPoint;
    double pointValue, oldPointValue;

    CtiMultiMsg *pMultiMsg = CTIDBG_new CtiMultiMsg;
    char pointDescription[80];
    BOOL messageInMulti = FALSE;

    bool calcValid;
    int calcQuality;
    CtiTime calcTime;


    CtiCalcPointMapIterator constIter;

    for( constIter = _constantPoints.begin() ; constIter != _constantPoints.end(); constIter++ )
    {
        calcPoint = (*constIter).second;

        if( calcPoint!=NULL )
        {
            messageInMulti = TRUE;
            pointId = calcPoint->getPointId( );

            {
                RWMutexLock::LockGuard msgLock(_pointDataMutex);
                CtiPointStore* pointStore = CtiPointStore::getInstance();
                CtiHashKey pointHashKey(calcPoint->getPointId());
                CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore)[&pointHashKey]);

                if( calcPointPtr != NULL ) //Exception preventative
                {
                    oldPointValue = calcPointPtr->getPointValue();
                    pointValue = calcPoint->calculate( calcQuality, calcTime, calcValid );    // Here is the MATH
                }
                else
                {
                    calcValid = false;
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** Point " << pointId << " constant not in pointstore" << endl;
                }
            }

            if(!calcValid)
            {
                if(_CALC_DEBUG & CALC_DEBUG_POSTCALC_VALUE)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " ### Calculation of point " << calcPoint->getPointId() << " was invalid (ex. div by zero or sqrt(<0))." << endl;
                }

                calcQuality = NonUpdatedQuality;
                pointValue = oldPointValue;
            }

            sprintf( pointDescription, "calc point %l update", pointId );

            CtiPointDataMsg *pointData = CTIDBG_new CtiPointDataMsg(pointId, pointValue, ConstantQuality, InvalidPointType, pointDescription);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself

            pMultiMsg->getData( ).push_back( pointData );

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " sendConstants() setting Calc Point ID: " << pointId << " to CONSTANT Value: " << pointValue << endl;
            }
        }
    }

    if( messageInMulti )
    {
        {
            RWMutexLock::LockGuard calcMsgGuard(outboxMux);
            _outbox.append( pMultiMsg );
        }
    }
    else
    {
        delete pMultiMsg;
    }
}


void CtiCalculateThread::onUpdateComplain( void *la )
{
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << CtiTime( ) << " CalcLogicSvc onUpdate thread is AWOL" << endl;
   }
}

void CtiCalculateThread::periodicComplain( void *la )
{
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << CtiTime( ) << " CalcLogicSvc periodic thread is AWOL" << endl;
   }
}

void CtiCalculateThread::calcComplain( void *la )
{
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << CtiTime( ) << " CalcLogicSvc calc thread thread is AWOL" << endl;
   }
}

void CtiCalculateThread::sendUserQuit(void *who)
{
    string *strPtr = (string *) who;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << *strPtr << " has asked for shutdown."<< endl;
    }
    UserQuit = TRUE;
}

//Function to load a map with the data from DynamicCalcHistorical.
void CtiCalculateThread::getCalcHistoricalLastUpdatedTime(PointTimeMap &dbTimeMap)
{
    dbTimeMap.clear();
    long pointid;
    CtiTime updateTime;

    try
    {
        //  connect to the database
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection( );

        RWDBDatabase db             = conn.database();
        RWDBTable    table     = db.table("DYNAMICCALCHISTORICAL");
        RWDBSelector selector  = db.selector();

        selector << table["POINTID"]
        << table["LASTUPDATE"];

        selector.from( table );

        RWDBReader  rdr = selector.reader( conn );

        //  iterate through the components
        while( rdr() )
        {

            //  read 'em in, and append to the class
            rdr["POINTID"] >> pointid;
            rdr["LASTUPDATE"] >> updateTime;

            dbTimeMap.insert(PointTimeMap::value_type(pointid, updateTime));
        }

    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception while reading calc last updated time from database: " << msg.why( ) << endl;
        exit( -1 );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}

void CtiCalculateThread::getHistoricalTableData(CtiCalc *calcPoint, CtiTime &lastTime, DynamicTableData &data)
{
    data.clear();
    long pointid;
    CtiTime timeStamp;
    double value;
    DynamicTableDataIter iter;

    try
    {
        //  connect to the database
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection( );

        RWDBDatabase db             = conn.database();
        RWDBTable    table     = db.table("RAWPOINTHISTORY");
        RWDBSelector selector  = db.selector();

        selector << table["POINTID"]
        << table["TIMESTAMP"]
        << table["VALUE"];

        set<long> compIDList = calcPoint->getComponentIDList();

        selector.from( table );

        for( set<long>::iterator idIter = compIDList.begin(); idIter != compIDList.end(); idIter++ )
        {
            selector.where( selector["POINTID"] == *idIter || selector.where() );
        }

        if( compIDList.size() <= 0 )
        {
            selector.where( selector["TIMESTAMP"] == toRWDBDT(CtiTime()) && selector["POINTID"] == -124 );//Dont allow us to get any, theres no components on our point!
        }
        selector.where( selector["TIMESTAMP"] > toRWDBDT(lastTime) && selector.where() );

        RWDBReader  rdr = selector.reader( conn );

        //  iterate through the components
        while( rdr() )
        {
            //  read 'em in, and append to the data structure
            rdr["POINTID"] >> pointid;
            rdr["TIMESTAMP"] >> timeStamp;
            rdr["VALUE"] >> value;

            if( (iter = data.find(timeStamp)) != data.end() )
            {
                iter->second.insert(HistoricalPointValueMap::value_type(pointid, value));
            }
            else
            {
                HistoricalPointValueMap insertMap;
                insertMap.insert(HistoricalPointValueMap::value_type(pointid, value));
                data.insert(DynamicTableData::value_type(timeStamp, insertMap));
            }
        }

    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception while reading calc last updated time from database: " << msg.why( ) << endl;
        exit( -1 );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCalculateThread::getHistoricalTableSinglePointData(long calcPoint, CtiTime &lastTime, DynamicTableSinglePointData &data)
{
    data.clear();
    long pointid;
    CtiTime timeStamp;
    double value;

    data.clear();

    if( calcPoint != 0 )
    {
        try
        {
            //  connect to the database
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection( );

            RWDBDatabase db             = conn.database();
            RWDBTable    table     = db.table("RAWPOINTHISTORY");
            RWDBSelector selector  = db.selector();

            selector << table["POINTID"]
            << table["TIMESTAMP"]
            << table["VALUE"];

            selector.from( table );

            selector.where( selector["POINTID"] == calcPoint );
            selector.where( selector["TIMESTAMP"] > toRWDBDT(lastTime) && selector.where() );
            selector.orderByDescending( selector["TIMESTAMP"] );

            RWDBReader  rdr = selector.reader( conn );

            //  iterate through the components
            while( rdr() )
            {
                //  read 'em in, and append to the data structure
                rdr["POINTID"] >> pointid;
                rdr["TIMESTAMP"] >> timeStamp;
                rdr["VALUE"] >> value;

                PointValuePair insertPair(pointid, value);
                data.insert(DynamicTableSinglePointData::value_type(timeStamp, insertPair));
            }

        }
        catch( RWxmsg &msg )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Exception while reading calc last updated time from database: " << msg.why( ) << endl;
            exit( -1 );
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
}

void CtiCalculateThread::setHistoricalPointStore(HistoricalPointValueMap &valueMap)
{
    CtiPointStore *pointStore = CtiPointStore::getInstance();
    HistoricalPointValueMap::iterator iter;
    for( iter = valueMap.begin(); iter != valueMap.end(); iter++ )
    {
        CtiHashKey calcPointHashKey(iter->first);
        CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore)[&calcPointHashKey]);
        calcPointPtr->setHistoricValue(iter->second);
    }
}

void CtiCalculateThread::updateCalcHistoricalLastUpdatedTime(PointTimeMap &unlistedPoints, PointTimeMap &updatedPoints)
{
    //The plan is to update the updated points and add the unlisted points.
    long pointid;
    CtiTime updateTime;
    PointTimeMap::iterator iter;
    RWDBStatus::ErrorCode err;

    try
    {
        //  connect to the database
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection( );

        RWDBDatabase db     = conn.database();
        RWDBTable    table  = db.table("DYNAMICCALCHISTORICAL");

        RWDBInserter inserter  = table.inserter();

        for( iter = unlistedPoints.begin(); iter != unlistedPoints.end(); iter++ )
        {
            inserter << iter->first << toRWDBDT(iter->second);
            if( err = ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - error \"" << err << "\" while inserting in CalcHistoricalUpdatedTime **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                break;//for
                //  dout << "Data: ";
            }
        }

        RWDBUpdater updater = table.updater();

        for( iter = updatedPoints.begin(); iter != updatedPoints.end(); iter++ )
        {
            updater.where(table["POINTID"] == iter->first);
            updater << table["LASTUPDATE"].assign( toRWDBDT(iter->second) );
            updater.execute( conn );
        }

    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception while writing calc last updated time to database: " << msg.why( ) << endl;
        exit( -1 );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}

void CtiCalculateThread::getCalcBaselineMap(PointBaselineMap &pointBaselineMap)
{
    pointBaselineMap.clear();
    long pointid, baselineID;

    try
    {
        //  connect to the database
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection( );

        RWDBDatabase db             = conn.database();
        RWDBTable    table     = db.table("CALCPOINTBASELINE");
        RWDBSelector selector  = db.selector();

        selector << table["POINTID"]
        << table["BASELINEID"];

        selector.from( table );

        RWDBReader  rdr = selector.reader( conn );

        //  iterate through the components
        while( rdr() )
        {

            //  read 'em in, and append to the class
            rdr["POINTID"] >> pointid;
            rdr["BASELINEID"] >> baselineID;

            pointBaselineMap.insert(PointBaselineMap::value_type(pointid, baselineID));
        }

    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception while reading calc last updated time from database: " << msg.why( ) << endl;
        exit( -1 );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCalculateThread::getBaselineMap(BaselineMap &baselineMap)
{
    baselineMap.clear();
    long baselineID;
    BaselineData baseline;

    try
    {
        //  connect to the database
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection( );

        RWDBDatabase db             = conn.database();
        RWDBTable    table     = db.table("BASELINE");
        RWDBSelector selector  = db.selector();

        selector << table["BASELINEID"]
        << table["DAYSUSED"]
        << table["PERCENTWINDOW"]
        << table["CALCDAYS"]
        << table["EXCLUDEDWEEKDAYS"]
        << table["HOLIDAYSUSED"];

        selector.from( table );

        RWDBReader  rdr = selector.reader( conn );

        //  iterate through the components
        while( rdr() )
        {

            //  read 'em in, and append to the class
            rdr["BASELINEID"] >> baselineID;
            rdr["DAYSUSED"] >> baseline.maxSearchDays;
            rdr["PERCENTWINDOW"] >> baseline.percent;
            rdr["CALCDAYS"] >> baseline.usedDays;
            rdr["EXCLUDEDWEEKDAYS"] >> baseline.excludedWeekDays;
            rdr["HOLIDAYSUSED"] >> baseline.holidays;

            //The copy is unfortunate, but relatively inexpensive for a small number of objects
            baselineMap.insert(BaselineMap::value_type(baselineID, baseline));
        }

    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception while reading calc last updated time from database: " << msg.why( ) << endl;
        exit( -1 );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

//This function could be much better then it is right now, but thats how it goes.
void CtiCalculateThread::getCurtailedDates(DatesSet &curtailedDates, long pointID, CtiTime &searchTime)
{
    CtiTime newTime;
    curtailedDates.clear();
    try
    {
        //  connect to the database
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection( );

        RWDBDatabase db             = conn.database();
        RWDBTable    lmOffer   = db.table("LMENERGYEXCHANGEPROGRAMOFFER");
        RWDBTable    lmReply   = db.table("LMENERGYEXCHANGECUSTOMERREPLY");
        RWDBTable    dcl       = db.table("DEVICECUSTOMERLIST");
        RWDBTable    point     = db.table("POINT");
        RWDBSelector selector  = db.selector();

        selector << lmOffer["OFFERDATE"];

        selector.from( lmOffer );
        selector.from( lmReply );
        selector.from( dcl );
        selector.from( point );

        selector.where( dcl["CUSTOMERID"] == lmReply["CUSTOMERID"] && lmReply["OFFERID"] == lmOffer["OFFERID"] &&
                        dcl["DEVICEID"] == point["PAOBJECTID"] && lmReply["ACCEPTSTATUS"] == "Accepted" &&
                        point["POINTID"] == pointID && lmOffer["OFFERDATE"] > toRWDBDT(searchTime));

        string a = selector.asString();
        RWDBReader  rdr = selector.reader( conn );

        //  iterate through the components
        while( rdr() )
        {
            //  read 'em in, and append to the data structure
            rdr["OFFERDATE"] >> newTime;
            curtailedDates.insert(newTime.date());
        }

    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception while reading calc last updated time from database: " << msg.why( ) << endl;
        exit( -1 );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}

//Anything from 00:00:01 to 25:00:00 (next day) is counted for today, when the final values are recorded,
//They are placed with a timestamp of 25:00:00 (hh:mm:ss)
bool CtiCalculateThread::processDay(long baselineID, CtiTime curTime, DynamicTableSinglePointData &data, DynamicTableSinglePointData &percentData, int percent, HourlyValues &results)
{
    results.clear();
    results.resize(24,0);
    bool retVal = true;
    bool isKW = false;// FIX_ME Sloppy
    const CtiTime startTime = curTime;

    CtiPointStore* pointStore = CtiPointStore::getInstance();
    CtiHashKey pointHashKey(baselineID);
    CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore).find(&pointHashKey));
    if( calcPointPtr != rwnil )
    {
        if( calcPointPtr->getUOMID() == 0 )
        {
            isKW = true;
        }
    }

    //This assumes that curTime is of the order DDDD:00:00:00
    for( int i = 0; i < 24; i++ )
    {
        double value = 0;
        int count = 0;
        DynamicTableSinglePointData::iterator iter;
        if( (iter = data.lower_bound(curTime)) != data.end() )
        {
            if( iter->first == curTime ) //at 00:00
            {
                iter++;
            }
            curTime.addMinutes(60);
            while( iter->first <= curTime && iter != data.end() )
            {
                value += iter->second.second;
                count++;
                iter++;
            }

            if( count == 0 ) //There we no entries that met our criteria
            {
                retVal = false;
            }

            if( isKW )
            {
                results[i] = value/count;
            }
            else
            {
                results[i] = value;
            }
        }
        else
        {
            retVal = false;
        }
    }

    if( percent > 0 && retVal != false )
    {
        HourlyValues percentResults;
        float decimalPercent = (float)percent/100;
        //we need to compare to values in the percent baseline component
        processDay(baselineID, startTime, percentData, percentData, 0, percentResults); //get percentResults for use
        for( int i = 0; i < 24; i++ )
        {
            if( results[i] < decimalPercent*percentResults[i] || results[i] > (float)percentResults[i]/decimalPercent );
            {
                retVal = false;
            }
        }
    }

    return retVal;
}
