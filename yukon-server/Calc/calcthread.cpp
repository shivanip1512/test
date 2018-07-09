#include "precompiled.h"

#include <vector>
#include <iostream>
#include <sstream>

#include "dbaccess.h"
#include "dllbase.h"
#include "pointtypes.h"
#include "message.h"
#include "msg_multi.h"
#include "logger.h"
#include "guard.h"
#include "utility.h"
#include "cparms.h"
#include "numstr.h"
#include "mgr_holiday.h"
#include "ThreadStatusKeeper.h"

#include "calcthread.h"
#include "database_writer.h"
#include "database_reader.h"
#include "database_util.h"

using namespace std;
using Cti::ThreadStatusKeeper;
using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;
using Cti::Database::DatabaseWriter;

extern DatabaseConnection::QueryTimeout getCalcQueryTimeout();

extern ULONG _CALC_DEBUG;
extern BOOL  UserQuit;
extern bool _shutdownOnThreadTimeout;
extern bool _runCalcBaseline;


CtiCalculateThread::CtiCalculateThread() :
    _periodicThreadFunc  (Cti::WorkerThread::Function([this]{ periodicThread();   }).name("periodicThread")),
    _onUpdateThreadFunc  (Cti::WorkerThread::Function([this]{ onUpdateThread();   }).name("onUpdateThread")),
    _historicalThreadFunc(Cti::WorkerThread::Function([this]{ historicalThread(); }).name("historicalThread")),
    _baselineThreadFunc  (Cti::WorkerThread::Function([this]{ baselineThread();   }).name("baselineThread"))
{
}

CtiCalculateThread::~CtiCalculateThread( void )
{
    try
    {
        _auAffectedPoints.clear();
        if( !_periodicPoints.empty() )
        {
            delete_assoc_container(_periodicPoints);
            _periodicPoints.clear();
        }
        if( !_onUpdatePoints.empty() )
        {
            delete_assoc_container(_onUpdatePoints);
            _onUpdatePoints.clear();
        }
        if( !_constantPoints.empty() )
        {
            delete_assoc_container(_constantPoints);
            _constantPoints.clear();
        }
        if( !_historicalPoints.empty() )
        {
            delete_assoc_container(_historicalPoints);
            _historicalPoints.clear();
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
};

void CtiCalculateThread::pointChange( long changedID, double newValue, const CtiTime newTime, unsigned newQuality, unsigned newTags )
{
    try
    {
        CtiLockGuard<CtiCriticalSection> msgLock(_pointDataMutex);

        if( _CALC_DEBUG & CALC_DEBUG_POINTDATA )
        {
            CTILOG_DEBUG(dout, "Point Data ID: "<< changedID
                                << " Val: "     << newValue
                                << " Time: "    << newTime
                                << " Quality: " << newQuality
                                << " Tags: " << std::hex << newTags);
        }

        if( CtiPointStoreElement* pointPtr = CtiPointStore::find(changedID) )
        {
            if( newTime > pointPtr->getPointTime() ||               // Point Change is newer than last point data
                ( pointPtr->getNumUpdates() > 0 &&                  // Point has been updated AND
                  ( newQuality != pointPtr->getPointQuality() ||    // quality, tags, or value have changed.
                    newValue != pointPtr->getPointValue() ||
                    newTags != pointPtr->getPointTags() ) ) )
            {
                pointPtr->setPointValue( newValue, newTime, newQuality, newTags );      // Update the pointStore.

                for each( const long pointid in pointPtr->getDependents( ) )
                {
                    if( pointid )
                    {
                        _auAffectedPoints.append(pointid);
                    }
                }
            }
            else if( pointPtr->getNumUpdates() == 0 )
            {
                pointPtr->firstPointValue( newValue, newTime, newQuality, newTags );
            }
        }
        else
        {
            if( changedID != ThreadMonitor.getProcessPointID() )
            {
                CTILOG_ERROR(dout, "Unable to find point id from offset "<< ThreadMonitor.Calc);
            }
            else if( _CALC_DEBUG & CALC_DEBUG_POINTDATA )
            {
                CTILOG_DEBUG(dout, "Received thread monitor update.");
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCalculateThread::pointSignal( long changedID, unsigned newTags )
{
    try
    {
        CtiLockGuard<CtiCriticalSection> msgLock(_pointDataMutex);

        if( _CALC_DEBUG & CALC_DEBUG_POINTDATA )
        {
            CTILOG_DEBUG(dout, "Point Data ID: "<< changedID <<" Tags: "<< newTags);
        }

        if( CtiPointStoreElement* pointPtr = CtiPointStore::find(changedID) )
        {
            if( newTags != pointPtr->getPointTags() )
            {
                pointPtr->setPointTags( newTags );      // Update the pointStore.
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
        BOOL messageInMulti;
        clock_t now;

        ThreadStatusKeeper threadStatus("CalcLogicSvc periodicThread");

        while( true )
        {
            if(!_shutdownOnThreadTimeout)
            {
                threadStatus.monitorCheck();
            }
            else
            {
                threadStatus.monitorCheck(&CtiCalculateThread::sendUserQuit);
            }

            //  while it's still the same second /and/ i haven't been interrupted
            while( newTime == tempTime )
            {
                tempTime = CtiTime( );

                Cti::WorkerThread::sleepFor(Cti::Timing::Chrono::milliseconds(250));
            }

            _periodicThreadFunc.waitForResume();

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
                    CtiLockGuard<CtiCriticalSection> msgLock(_pointDataMutex);
                    CtiPointStoreElement* calcPointPtr = CtiPointStore::find(calcPoint->getPointId());

                    oldPointValue = calcPointPtr->getPointValue();
                    newPointValue = calcPoint->calculate( calcQuality, calcTime, calcValid );
                }

                calcPoint->setNextInterval(calcPoint->getUpdateInterval());

                if(!calcValid)
                {
                    if(_CALC_DEBUG & CALC_DEBUG_POSTCALC_VALUE)
                    {
                        CTILOG_DEBUG(dout, "Calculation of point "<< calcPoint->getPointId() <<" was invalid (ex. div by zero or sqrt(<0)).");
                    }

                    calcQuality = NonUpdatedQuality;
                    newPointValue = oldPointValue;
                }

                sprintf( pointDescription, "calc point %ld update", pointId );

                CtiPointDataMsg *pointData = CTIDBG_new CtiPointDataMsg(pointId, newPointValue, calcQuality, InvalidPointType, pointDescription);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself
                pointData->setTime(calcTime);

                periodicMultiMsg->getData( ).push_back( pointData );

                if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                {
                    CTILOG_DEBUG(dout, "PeriodCalc setting Calc Point ID: "<< pointId <<" to New Value: "<< newPointValue);
                }
            }

            //  post message
            if( messageInMulti )
            {
                {
                    CtiLockGuard<CtiCriticalSection> calcMsgGuard(outboxMux);
                    _outbox.append( periodicMultiMsg );
                }

                if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                {
                    CTILOG_DEBUG(dout, "periodicThread posting a message - took "<< (clock( ) - now) <<" ticks");
                }
            }
            else
            {
                delete periodicMultiMsg;
            }

            newTime = tempTime;
        }
    }
    catch( Cti::WorkerThread::Interrupted & )
    {
        CTILOG_INFO(dout, "periodicThread interrupted.");
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCalculateThread::onUpdateThread( void )
{
    try
    {
        bool calcValid;
        long pointIDChanged, recalcPointID;
        double recalcValue, oldCalcValue;
        char pointDescription[80];
        BOOL pointsInMulti;
        CtiMultiMsg *pChg;
        CtiCalc *calcPoint;

        int calcQuality;
        CtiTime calcTime;

        ThreadStatusKeeper threadStatus("CalcLogicSvc onUpdateThread");

        while( true )
        {
            //  this is the cleanest way to do it;  i know it's a do-while, but that gets rid of an unnecessary if statement
            do
            {
                if(!_shutdownOnThreadTimeout)
                {
                    threadStatus.monitorCheck();
                }
                else
                {
                    threadStatus.monitorCheck(&CtiCalculateThread::sendUserQuit);
                }

                Cti::WorkerThread::sleepFor(Cti::Timing::Chrono::milliseconds(250));
            }
            while( !_auAffectedPoints.entries( ) );

            _onUpdateThreadFunc.waitForResume();

            pChg = CTIDBG_new CtiMultiMsg;
            pointsInMulti = FALSE;

            //  get the mutex while we're accessing the _auAffectedPoints collection
            //  (it's accessed by pointChange as well)
            {
                CtiLockGuard<CtiCriticalSection> msgLock(_pointDataMutex);
                while( _auAffectedPoints.entries( ) )
                {
                    recalcPointID = _auAffectedPoints.removeFirst( );
                    std::map<long, CtiCalc* >::iterator itr = _onUpdatePoints.find(recalcPointID);

                    calcPoint = (*itr).second;

                    //  if not ready
                    if( itr == _onUpdatePoints.end() || calcPoint == NULL || !calcPoint->ready( ) )
                         continue;  // All the components are not ready.

                    CtiPointStoreElement* calcPointPtr = CtiPointStore::find(calcPoint->getPointId());

                    oldCalcValue = calcPointPtr->getPointValue();
                    recalcValue = calcPoint->calculate( calcQuality, calcTime, calcValid );    // Here is the MATH
                    calcPoint->setNextInterval(calcPoint->getUpdateInterval());     // This only matters for periodicPlusUpdatePoints.

                    if(!calcValid)
                    {
                        if(_CALC_DEBUG & CALC_DEBUG_POSTCALC_VALUE)
                        {
                            CTILOG_DEBUG(dout, "Calculation of point " << calcPoint->getPointId() << " was invalid (ex. div by zero or sqrt(<0)).");
                        }

                        calcQuality = NonUpdatedQuality;
                        recalcValue = oldCalcValue;
                    }


                    // Make sure we do not try to move backwards in time.
                    if( calcPointPtr->getPointTime() > calcTime )
                    {
                        if( _CALC_DEBUG & CALC_DEBUG_POINTDATA_QUALITY )
                        {
                            CTILOG_DEBUG(dout, "Calc point "<< calcPoint->getPointId() <<" calculation result is attempting to move backward in time." <<
                                    endl << "Update type may be inappropriate for the component device/points.");
                        }
                        calcTime = CtiTime();
                    }

                    CtiPointDataMsg *pData = NULL;

                    if( calcPointPtr->getPointCalcWindowEndTime() > CtiTime(CtiDate(1,1,1991)) )
                    {// demand average point madness

                        if( const long davgpid = calcPoint->findDemandAvgComponentPointId() )
                        {
                            if( CtiPointStoreElement* componentPointPtr = CtiPointStore::find(davgpid) )
                            {

                                CtiTime now;
                                CtiTime et = calcPointPtr->getPointCalcWindowEndTime();
                                CtiTime etplus = (calcPointPtr->getPointCalcWindowEndTime() + componentPointPtr->getSecondsSincePreviousPointTime());

                                if( et <= now &&  now < etplus )    // Are we greater than the end time, but less than the end time + "slop"
                                {
                                    if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                                    {
                                        CTILOG_DEBUG(dout, "New Point Data message for Calc Point Id: "<< recalcPointID <<" New Demand Avg Value: "<< recalcValue);
                                    }
                                    pData = CTIDBG_new CtiPointDataMsg(recalcPointID, recalcValue, calcQuality, InvalidPointType);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself
                                    pData->setTime(calcPointPtr->getPointCalcWindowEndTime());
                                }

                                calcPointPtr->setPointValue( recalcValue, CtiTime(), NormalQuality, 0 );
                            }
                            else
                            {
                                CTILOG_ERROR(dout, "componentPointPtr is Null, Calc Point "<< calcPoint->getPointId());
                            }
                        }
                        else
                        {
                            CTILOG_ERROR(dout, "CONFIG INVALID: Demand Average points require a point to be identified (no pre-push).  Point ID "<< calcPoint->getPointId());
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
                        sprintf( pointDescription, "calc point %ld update", recalcPointID );
                        pData->setString(pointDescription);
                        pChg->getData( ).push_back( pData );
                    }

                    if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                    {
                        CTILOG_DEBUG(dout, "onUpdateThread setting Calc Point ID: "<< recalcPointID <<" to New Value: "<< recalcValue);
                    }
                }
            }

            if( pointsInMulti )
            {
                {
                    CtiLockGuard<CtiCriticalSection> outboxGuard(outboxMux);
                    _outbox.append( pChg );
                }

                //  i kinda want to keep away from having a hold on both of the mutexes, as a little programming error
                //    earlier earned me a touch of the deadlock.
                if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                {
                    CTILOG_DEBUG(dout, "onUpdateThread posting a message");
                }
            }
            else
            {
                delete pChg;
            }
        }
    }
    catch( Cti::WorkerThread::Interrupted & )
    {
        CTILOG_INFO(dout, "onUpdateThread interrupted.");
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

bool wasPausedOrInterrupted(Cti::CalcLogic::CalcWorkerThread &thread, const size_t previousPauseCount, const Cti::CallSite callSite)
{
    //  This will throw if we've been interrupted or terminated.
    Cti::WorkerThread::interruptionPoint();

    const auto currentPauseCount = thread.getPauseCount();

    if( previousPauseCount < currentPauseCount )
    {
        CTILOG_WARN(dout, "Called from " << callSite << " - "
            << "Thread was paused since calculation began"
            " (previousPauseCount < currentPauseCount; "
            << previousPauseCount << " < " << currentPauseCount << ")"
            ", aborting calculation");

        return true;
    }

    return false;
}

void CtiCalculateThread::historicalThread( void )
{
    try
    {
        bool calcValid, reloaded = false;
        bool pointsInMulti;
        CtiMultiMsg *pChg;
        CtiCalc *calcPoint;
        CtiCalcPointMapIterator historicIter;
        PointTimeMap unlistedPoints, updatedPoints;
        DynamicTableData data;
        int frequencyInSeconds;
        int initialDays;
        char var[256];

        int calcQuality;
        CtiTime nextCalcTime;

        CtiTime now, lastTime, start;
        ThreadStatusKeeper threadStatus("CalcLogicSvc HistoricalThread");

        strcpy(var, "CALC_HISTORICAL_FREQUENCY_IN_SECONDS");
        if( 0 != (frequencyInSeconds = gConfigParms.getValueAsInt(var,0)) )
        {
            CTILOG_INFO(dout, var <<":  "<< frequencyInSeconds);
        }
        else
        {
            frequencyInSeconds = 60*60;//60 minutes
        }

        strcpy(var, "CALC_HISTORICAL_INITIAL_DAYS_CALCULATED");
        if( 0 != (initialDays = gConfigParms.getValueAsInt(var,0)) )
        {
            CTILOG_INFO(dout, var << ":  " << initialDays);
        }
        else
        {
            initialDays = 0;//0 days
        }

        while( true )
        {
            do
            {
                now = CtiTime::now();

                if(!_shutdownOnThreadTimeout)
                {
                    threadStatus.monitorCheck();
                }
                else
                {
                    threadStatus.monitorCheck(&CtiCalculateThread::sendUserQuit);
                }

                //Historical doesnt do much most of the time, it can sleep for several seconds
                Cti::WorkerThread::sleepFor(Cti::Timing::Chrono::seconds(2));
            }
            while( now < nextCalcTime );

            const auto pauseCount = _historicalThreadFunc.waitForResume();

            CTILOG_INFO(dout, "Historical Calculation beginning.");

            start = start.now();

            pChg = CTIDBG_new CtiMultiMsg;
            pointsInMulti = FALSE;

            PointTimeMap dbTimeMap;
            getCalcHistoricalLastUpdatedTime(dbTimeMap);

            //  Check for any outside interference that may have occurred during the DB load
            if( wasPausedOrInterrupted(_historicalThreadFunc, pauseCount, CALLSITE) )
            {
                continue;
            }

            PointTimeMap::iterator dbTimeMapIter;
            long pointID;
            long componentCount;
            double newPointValue;
            CtiTime calcTime;
            char pointDescription[80];

            reloaded = false;

            for( historicIter = _historicalPoints.begin(); !reloaded && (historicIter != _historicalPoints.end()); )
            {
                calcPoint = (*historicIter++).second;
                if( calcPoint==NULL || !calcPoint->ready( ) )
                {
                    continue;  // for
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

                //  Check for any outside interference that may have occurred during the DB load
                if( wasPausedOrInterrupted(_historicalThreadFunc, pauseCount, CALLSITE) )
                {
                    reloaded = true;

                    continue;
                }

                componentCount = calcPoint->getComponentCount();

                DynamicTableDataIter iter;
                CtiTime newTime = (unsigned long)0;
                for( iter = data.begin(); iter!=data.end(); iter++ )
                {
                    if( iter->second.size() == componentCount )
                    {
                        //This means all the necessary points in historical have been updated, we can do a calc
                        setHistoricalPointStore(iter->second);//Takes the value/paoid pair and sets the values in the point store

                        CtiPointStoreElement* calcPointPtr = CtiPointStore::find(calcPoint->getPointId());

                        newPointValue = calcPoint->calculate( calcQuality, calcTime, calcValid );

                        calcPoint->setNextInterval(calcPoint->getUpdateInterval());

                        if(!calcValid)
                        {
                            //even if we were invalid, we need to move on and try the next time, otherwise we will constantly retry
                            if(_CALC_DEBUG & CALC_DEBUG_POSTCALC_VALUE)
                            {
                                CTILOG_DEBUG(dout, "Calculation of historical point "<< calcPoint->getPointId() <<" was invalid (ex. div by zero or sqrt(<0)).");
                            }

                            calcQuality = NonUpdatedQuality;
                            newPointValue = 0;
                        }


                        sprintf( pointDescription, "calc point %ld update", pointID );

                        CtiPointDataMsg *pointData = CTIDBG_new CtiPointDataMsg(pointID, newPointValue, NormalQuality, InvalidPointType, pointDescription);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself
                        pointData->setTime(iter->first);//The time these points were entered in the historical log
                        newTime = iter->first;//we do this in order of time, so the last one is the time we want.

                        pChg->getData( ).push_back( pointData );
                        pointsInMulti = TRUE;

                        if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                        {
                            CTILOG_DEBUG(dout, "HistoricCalc setting Calc Point ID: "<< pointID <<" to New Value: "<< newPointValue);
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
                now = CtiTime::now();
                nextCalcTime = nextScheduledTimeAlignedOnRate( now, frequencyInSeconds );
            }

            updateCalcHistoricalLastUpdatedTime(unlistedPoints, updatedPoints);//Write these back out to the database

            //  Check for any outside interference that may have occurred during the DB write
            Cti::WorkerThread::interruptionPoint();

            updatedPoints.clear();//Next time through these need to be clear.
            unlistedPoints.clear();

            if( pointsInMulti )
            {
                {
                    CtiLockGuard<CtiCriticalSection> outboxGuard(outboxMux);
                    _outbox.append( pChg );
                }

                if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                {
                    CTILOG_DEBUG(dout, "historical posting a message");
                }
            }
            else
            {
                delete pChg;
            }

            CTILOG_INFO(dout, "Historical Calculation completed in "<< start.now().seconds()-start.seconds() << " seconds" <<
                    endl << "Next calculation is at "<< nextCalcTime);
        }
    }
    catch( Cti::WorkerThread::Interrupted & )
    {
        CTILOG_INFO(dout, "historical interrupted.");
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCalculateThread::baselineThread( void )
{
    try
    {
        bool calcValid, reloaded = false;
        BOOL pointsInMulti;
        CtiMultiMsg *pChg;
        CtiCalc *calcPoint;
        CtiCalcPointMapIterator historicIter;
        PointTimeMap unlistedPoints, updatedPoints;
        DynamicTableSinglePointData data;
        DynamicTableSinglePointData percentData;
        DatesSet curtailedDates;
        int frequencyInSeconds = 24*60*60;//24 hours;
        int initialDays = 30;//30 days
        char var[256];

        ThreadStatusKeeper threadStatus("CalcLogicSvc BaselineThread");

        int calcQuality;
        CtiTime nextCalcTime(4,16,0);//Today at 4:16 AM
        CtiTime now, lastTime, start;

        if( nextCalcTime < now.now() )
        {
            nextCalcTime.addDays(1);
        }
        if( gConfigParms.isTrue("CALC_LOGIC_RUN_BASELINE_ON_STARTUP") )
        {
            nextCalcTime.addDays(-1);//Should let us run immediately
        }

        strcpy(var, "CALC_BASELINE_INITIAL_DAYS_CALCULATED");
        if( 30 != (initialDays = gConfigParms.getValueAsInt(var,30)) )
        {
            CTILOG_INFO(dout, var <<":  "<< initialDays);
        }

        while( true )
        {
            do
            {
                now = CtiTime::now();

                if(!_shutdownOnThreadTimeout)
                {
                    threadStatus.monitorCheck();
                }
                else
                {
                    threadStatus.monitorCheck(&CtiCalculateThread::sendUserQuit);
                }

                //baseline doesnt do much almost all of the time, it can sleep for as long as we can wait on shutdown
                Cti::WorkerThread::sleepFor(Cti::Timing::Chrono::seconds(2));
            }
            while( !(now >= nextCalcTime) );

            const auto pauseCount = _baselineThreadFunc.waitForResume();

            CTILOG_INFO(dout, "Baseline Calculation beginning.");

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

            //  Check for any outside interference that may have occurred during the DB load
            if( wasPausedOrInterrupted(_baselineThreadFunc, pauseCount, CALLSITE) )
            {
                continue;
            }

            PointTimeMap::iterator dbTimeMapIter;
            long pointID, baselinePercentID, baselineID;
            double newPointValue;
            CtiTime calcTime;
            char pointDescription[80];

            reloaded = false;

            for( historicIter = _historicalPoints.begin(); !reloaded && (historicIter != _historicalPoints.end()); )
            {
                calcPoint = (*historicIter++).second;
                if( calcPoint==NULL || !calcPoint->ready( ) )
                {
                    continue;  // for
                }

                if( !calcPoint->isBaselineCalc() )
                {
                    continue;//for
                }

                pointID = calcPoint->getPointId( );
                baselineID = calcPoint->getBaselineId();
                baselinePercentID = calcPoint->getBaselinePercentId();

                if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                {
                    CTILOG_DEBUG(dout, "Baseline beginning for "<< pointID <<" with baseline: "<< baselineID <<" and percent: "<< baselinePercentID);
                }

                if( (dbTimeMapIter = dbTimeMap.find( pointID )) != dbTimeMap.end() )//Entry is in the database
                {
                    lastTime = dbTimeMapIter->second;
                    lastTime = CtiTime::CtiTime(lastTime.date(),0,0,0);
                    if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                    {
                        CTILOG_DEBUG(dout, "lastTime for "<< pointID <<" is "<< lastTime);
                    }
                }
                else
                {
                    lastTime = CtiTime((unsigned)0, (unsigned)0);
                    lastTime.addDays(-1*initialDays);//This should return 30 days ago, at 00:00:00
                    unlistedPoints.insert(PointTimeMap::value_type(pointID, lastTime));

                    if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                    {
                        CTILOG_DEBUG(dout, "lastTime for "<< pointID <<" is "<< lastTime);
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
                        CTILOG_INFO(dout, "No baseline data found for point: "<< pointID <<" trying next point");
                        continue;//move on to next point
                    }
                }
                else
                {
                    CTILOG_INFO(dout, "No baseline ID found for point: "<< pointID <<" trying next point");
                    continue;//move on to another point
                }

                searchTime = lastTime - baselineDataPtr->maxSearchDays*24*60*60;//Go back maxSearchDays days.

                //grab all the data we could ever possibly want, this gives us just 2 db reads.
                getHistoricalTableSinglePointData(baselineID, searchTime, data);
                getHistoricalTableSinglePointData(baselinePercentID, searchTime, percentData);
                getCurtailedDates(curtailedDates, pointID, searchTime);

                //  Check for any outside interference that may have occurred during the DB load
                if( wasPausedOrInterrupted(_baselineThreadFunc, pauseCount, CALLSITE) )
                {
                    reloaded = true;

                    continue;
                }

                CtiTime curCalculatedTime;
                for( ; ; )//Until we break!
                {
                    //searchTime limits how many days back we can check.
                    searchTime = lastTime;
                    searchTime.addDays(-1*baselineDataPtr->maxSearchDays);

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
                        if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                        {
                            CTILOG_DEBUG(dout, "Baseline not enough time to calculate");
                        }
                        break;
                    }

                    //If we do have days beyond this, try to calculate that days data, repeat until we have no data then break
                    vector<HourlyValues> dayValues;
                    CtiTime curTime = lastTime;
                    //Until we have enough days
                    while( dayValues.size() < baselineDataPtr->usedDays )
                    {
                        if( curTime <= searchTime ) //failure
                        {
                            break;//while
                        }
                        //Check if current day is ok to use (non holiday/other day)
                        //If not, decrement day
                        if( holidayManager.isHoliday(curTime.date(), baselineDataPtr->holidays) )
                        {
                            if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                            {
                                CTILOG_DEBUG(dout, "Today is a holiday. "<< curTime.date());
                            }
                            curTime.addDays(-1);
                            continue;//while loop
                        }
                        else if( baselineDataPtr->excludedWeekDays.length() >= 7 &&
                                 (baselineDataPtr->excludedWeekDays[curTime.date().weekDay()] == 'Y' ||
                                  baselineDataPtr->excludedWeekDays[curTime.date().weekDay()] == 'y') )//Sunday = 0
                        {
                            if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                            {
                                CTILOG_DEBUG(dout, "Today is a excluded. "<< curTime.date());
                            }
                            curTime.addDays(-1);
                            continue;
                        }
                        else if( curtailedDates.find(curTime.date()) != curtailedDates.end() )
                        {
                            if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                            {
                                CTILOG_DEBUG(dout, "Today is a curtailed date. " << curTime.date());
                            }
                            curTime.addDays(-1);
                            continue;
                        }
                        else
                        {
                            //If ok, try to get data
                            HourlyValues results;
                            if( processDay(baselineID, curTime, data, percentData, baselinePercentID > 0 ? baselineDataPtr->percent : 0, results) )
                            {
                                //If data is ok, store data and decrement day
                                if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                                {
                                    CTILOG_DEBUG(dout, "Today was ACCEPTED. "<< curTime.date());
                                }
                                dayValues.push_back(results);
                            }
                            else
                            {
                                if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                                {
                                    CTILOG_DEBUG(dout, "Today was NOT accepted. "<< curTime.date());
                                }
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

                            sprintf( pointDescription, "calc point %ld update", pointID );
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
                                CTILOG_DEBUG(dout, "Sending to point id "<< pointID <<" and time "<< pointTime);
                            }
                        }
                        else if( (iter = unlistedPoints.find(pointID)) != unlistedPoints.end() )
                        {
                            iter->second = pointTime;

                            if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                            {
                                CTILOG_DEBUG(dout, "Sending to point id "<< pointID <<" and time "<< pointTime);
                            }
                        }
                        else
                        {
                            updatedPoints.insert(PointTimeMap::value_type(pointID, pointTime));

                            if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                            {
                                CTILOG_DEBUG(dout, "Sending to point id "<< pointID <<" and time "<< pointTime);
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
                now = CtiTime::now();
                nextCalcTime = CtiTime(nextCalcTime.seconds() + (unsigned long)frequencyInSeconds);
            }

            updateCalcHistoricalLastUpdatedTime(unlistedPoints, updatedPoints);//Write these back out to the database

            //  Check for any outside interference that may have occurred during the DB write
            Cti::WorkerThread::interruptionPoint();

            updatedPoints.clear();//Next time through these need to be clear.
            unlistedPoints.clear();

            if( pointsInMulti )
            {
                {
                    CtiLockGuard<CtiCriticalSection> outboxGuard(outboxMux);
                    _outbox.append( pChg );
                }

                if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                {
                    CTILOG_DEBUG(dout, "baseline posting a message");
                }
            }
            else
            {
                delete pChg;
            }

            CTILOG_INFO(dout, "Baseline Calculation completed in "<< start.now().seconds()-start.seconds() <<" seconds"<<
                    endl << "Next calculation is at "<< nextCalcTime);
        }
    }
    catch( Cti::WorkerThread::Interrupted & )
    {
        CTILOG_INFO(dout, "baseline interrupted.");
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCalculateThread::appendCalcPoint( long pointID )
{
    CtiPointStore::insert( pointID, 0, undefined );
}

std::vector<long> CtiCalculateThread::getPointDependencies() const
{
    const auto &pointIds = CtiPointStore::getPointIds();

    return { pointIds.cbegin(), pointIds.cend() };
}

void CtiCalculateThread::calcThread( void )
{
    startThreads();

    try
    {
        while( true )
        {
           Cti::WorkerThread::sleepFor(Cti::Timing::Chrono::seconds(30));
        }
    }
    catch( Cti::WorkerThread::Interrupted & )
    {
        interruptThreads();
        joinThreads();
    }
}

/**
  * Called when the calc-logic service is started.
  * This method starts the periodic, onUpdate, historical and calc Baseline threads.
  * The historical thread is only started if there are calc points defined.
  */
void CtiCalculateThread::startThreads()
{
    CTILOG_DEBUG( dout, "Starting threads: numberOfHistoricalPoints=" << _historicalPoints.size() <<
        ", historical thread is " << (_historicalThreadFunc.isRunning() ? "" : "not ") << "currently running" );

    _periodicThreadFunc.start();
    _onUpdateThreadFunc.start();

    if (!_historicalPoints.empty())
    {
        _historicalThreadFunc.start();
    }

    if( _runCalcBaseline )
    {
        _baselineThreadFunc.start();
    }
}

/**
  * Called when the calc-logic service is stopped.
  * This method stops (joins) the periodic, onUpdate, historical and calc Baseline threads.
  * The historical thread is only stopped if it has been running.
  */
void CtiCalculateThread::joinThreads()
{
    CTILOG_DEBUG( dout, "Stopping threads: numberOfHistoricalPoints=" << _historicalPoints.size() <<
        ", historical thread is " << (_historicalThreadFunc.isRunning() ? "" : "not ") << "currently running" );

    _periodicThreadFunc.tryJoinOrTerminateFor( Cti::Timing::Chrono::seconds( 30 ) );
    _onUpdateThreadFunc.tryJoinOrTerminateFor(Cti::Timing::Chrono::seconds(30));

    if (_historicalThreadFunc.isRunning())
    {
        _historicalThreadFunc.tryJoinOrTerminateFor(Cti::Timing::Chrono::seconds(30));
    }

    if( _runCalcBaseline )
    {
        _baselineThreadFunc.tryJoinOrTerminateFor(Cti::Timing::Chrono::seconds(30));
    }
}

/**
  * Called when the calc-logic service is stopped.
  * This method interupts (signals to stop) the periodic, onUpdate, historical and calc Baseline threads.
  * The historical thread is only interrupted if it has been running.
  */
void CtiCalculateThread::interruptThreads()
{
    CTILOG_DEBUG( dout, "Interrupting threads: numberOfHistoricalPoints=" << _historicalPoints.size() <<
        ", historical thread is " << (_historicalThreadFunc.isRunning() ? "" : "not ") << "currently running" );

    _periodicThreadFunc.interrupt();
    _onUpdateThreadFunc.interrupt();

    if (_historicalThreadFunc.isRunning())
    {
        _historicalThreadFunc.interrupt();
    }

    if( _runCalcBaseline )
    {
        _baselineThreadFunc.interrupt();
    }

    if( _CALC_DEBUG & CALC_DEBUG_RELOAD )
    {
        CTILOG_DEBUG(dout, "CalcThreads interruption attempt completed");
    }

}

/**
  * Called when the calc-logic service is updated.
  * This method pauses the periodic, onUpdate, historical and calc Baseline threads.
  * The historical thread is only paused if it has been running.
  */
void CtiCalculateThread::pauseThreads()
{
    try
    {
        CTILOG_DEBUG( dout, "Pausing threads: numberOfHistoricalPoints=" << _historicalPoints.size() <<
            ", historical thread is " << (_historicalThreadFunc.isRunning() ? "" : "not ") << "currently running" );

        _onUpdateThreadFunc.pause();
        _periodicThreadFunc.pause();

        if (_historicalThreadFunc.isRunning())
        {
            _historicalThreadFunc.pause();
        }

        if( _runCalcBaseline )
        {
            _baselineThreadFunc.pause();
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/**
  * Called when the calc-logic service is updated.
  * This method restarts the periodic, onUpdate, historical and calc Baseline threads.
  * The historical thread is started if it has not been running and there are now calculation points, or
  * Stopped if it has been running and there are no longer calculation points to process.
  */
void CtiCalculateThread::resumeThreads()
{
    try
    {
        CTILOG_DEBUG( dout, "Resuming threads: numberOfHistoricalPoints=" << _historicalPoints.size() <<
            ", historical thread is " << (_historicalThreadFunc.isRunning() ? "" : "not ") << "currently running" );

        _onUpdateThreadFunc.resume();
        _periodicThreadFunc.resume();

        if (_historicalThreadFunc.isRunning())
        {
            if (_historicalPoints.empty())
            {
                _historicalThreadFunc.interrupt();
                _historicalThreadFunc.tryJoinOrTerminateFor( Cti::Timing::Chrono::seconds( 30 ) );
            }
            else
            {
                _historicalThreadFunc.resume();
            }
        }
        else
        {
            if (!_historicalPoints.empty())
            {
                _historicalThreadFunc.start();
            }
        }

        if( _runCalcBaseline )
        {
            _baselineThreadFunc.resume();
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

bool CtiCalculateThread::appendPoint( long pointid, string &updatetype, int updateinterval, string &qualityFlag )
{
    std::pair< std::map<long,CtiCalc*>::iterator , bool > inserted;

    CtiCalc *newPoint;
    newPoint = CTIDBG_new CtiCalc( pointid, updatetype, updateinterval, qualityFlag );
    switch( newPoint->getUpdateType( ) )
    {
    case periodic:
        inserted = _periodicPoints.insert( std::pair<long,CtiCalc*>(pointid, newPoint) );
        break;
    case allUpdate:
    case anyUpdate:
    case periodicPlusUpdate:
        inserted = _onUpdatePoints.insert(   std::pair<long,CtiCalc*> (pointid, newPoint) );
        break;
    case constant:
        inserted = _constantPoints.insert(   std::pair<long,CtiCalc*> (pointid, newPoint) );
        break;
    case historical:
        inserted = _historicalPoints.insert( std::pair<long,CtiCalc*> (pointid, newPoint) );
        break;
    default:
        {
           CTILOG_ERROR(dout, "Attempt to insert unknown CtiCalc point type \""<< updatetype<< "\", "
                   "value \""<< newPoint->getUpdateType() <<"\";  aborting point insert");
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
    CtiCalc *targetCalcPoint = NULL;
    CtiCalcComponent *newComponent = NULL;
    CtiPointStoreElement *tmpElementPtr = NULL;
    PointUpdateType updateType;

    if( _periodicPoints.find( pointID ) != _periodicPoints.end() )
    {
        targetCalcPoint  = _periodicPoints[pointID];
        updateType = periodic;
    }
    else if( _onUpdatePoints.find( pointID ) != _onUpdatePoints.end() )
    {
        targetCalcPoint  = _onUpdatePoints[pointID];
        updateType = targetCalcPoint->getUpdateType();
    }
    else if( _constantPoints.find( pointID ) != _constantPoints.end() )
    {
        targetCalcPoint  = _constantPoints[pointID];
        updateType = constant;
    }
    else if( _historicalPoints.find( pointID ) != _historicalPoints.end())
    {
        targetCalcPoint = _historicalPoints[pointID];
        updateType = historical;
    }
    else if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
    {
        CTILOG_DEBUG(dout, "Can't find calc point \""<< pointID <<"\" in either point collection (historical point?)");
        return;
    }

    if( ciStringEqual(operationType, "Get Interval Minutes") || ciStringEqual(operationType, "Get Point Limit") )
    {
        pointID = 0;
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
        tmpElementPtr = CtiPointStore::insert( componentPointID, pointID, updateType );
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
    return _periodicPoints.count(aPointID)
        || _onUpdatePoints.count(aPointID)
        || _constantPoints.count(aPointID)
        || _historicalPoints.count(aPointID);
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
        delete_assoc_container(_periodicPoints);
        _periodicPoints.clear();
    }
    _periodicPoints = points;
}

void CtiCalculateThread::setOnUpdatePointMap(const CtiCalcPointMap &points)
{
    if( _onUpdatePoints.size() > 0 )
    {
        delete_assoc_container(_onUpdatePoints);
        _onUpdatePoints.clear();
    }
    _onUpdatePoints = points;
}

void CtiCalculateThread::setConstantPointMap(const CtiCalcPointMap &points)
{
    if( _constantPoints.size() > 0 )
    {
        delete_assoc_container(_constantPoints);
        _constantPoints.clear();
    }
    _constantPoints = points;
}

void CtiCalculateThread::setHistoricalPointMap(const CtiCalcPointMap &points)
{
    if( _historicalPoints.size() > 0 )
    {
        delete_assoc_container(_historicalPoints);
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
            delete_assoc_container(_constantPoints);
            _constantPoints.clear();
        }

        if( _onUpdatePoints.size() > 0 )
        {
            delete_assoc_container(_onUpdatePoints);
            _onUpdatePoints.clear();
        }

        if( _periodicPoints.size() > 0 )
        {
            delete_assoc_container(_periodicPoints);
            _periodicPoints.clear();
        }

        if( _historicalPoints.size() > 0 )
        {
            delete_assoc_container(_historicalPoints);
            _historicalPoints.clear();
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
    CtiCalc *targetCalcPoint = NULL;
    CtiPointStoreElement *tmpElementPtr = NULL;

    if( _periodicPoints.find( aPointID ) != _periodicPoints.end() )
    {
        targetCalcPoint  = _periodicPoints[aPointID];
    }
    else if( _onUpdatePoints.find( aPointID ) != _onUpdatePoints.end() )
    {
        targetCalcPoint  = _onUpdatePoints[aPointID];
    }
    else if( _constantPoints.find( aPointID ) != _constantPoints.end() )
    {
        targetCalcPoint  = _constantPoints[aPointID];
    }
    else if( _historicalPoints.find( aPointID ) != _historicalPoints.end())
    {
        targetCalcPoint = _historicalPoints[aPointID];
    }
    else if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
    {
        CTILOG_DEBUG(dout, "Can't find calc point \""<< aPointID <<"\" in either point collection (historical point?)");
        return;
    }

    if( targetCalcPoint )
    {
        targetCalcPoint->clearComponentDependencies();

        CtiPointStore::remove( aPointID );
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
                CtiLockGuard<CtiCriticalSection> msgLock(_pointDataMutex);

                if( CtiPointStoreElement* calcPointPtr = CtiPointStore::find(calcPoint->getPointId()) )
                {
                    oldPointValue = calcPointPtr->getPointValue();
                    pointValue = calcPoint->calculate( calcQuality, calcTime, calcValid );    // Here is the MATH
                }
                else
                {
                    calcValid = false;
                    CTILOG_ERROR(dout, "Point "<< pointId <<" constant not in pointstore");
                }
            }

            if(!calcValid)
            {
                if(_CALC_DEBUG & CALC_DEBUG_POSTCALC_VALUE)
                {
                    CTILOG_DEBUG(dout, "Calculation of point "<< calcPoint->getPointId() <<" was invalid (ex. div by zero or sqrt(<0)).");
                }

                calcQuality = NonUpdatedQuality;
                pointValue = oldPointValue;
            }

            sprintf( pointDescription, "calc point %ld update", pointId );

            CtiPointDataMsg *pointData = CTIDBG_new CtiPointDataMsg(pointId, pointValue, ConstantQuality, InvalidPointType, pointDescription);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself

            pMultiMsg->getData( ).push_back( pointData );

            CTILOG_INFO(dout, "Setting Calc Point ID: "<< pointId <<" to CONSTANT Value: "<< pointValue);
        }
    }

    if( messageInMulti )
    {
        {
            CtiLockGuard<CtiCriticalSection> calcMsgGuard(outboxMux);
            _outbox.append( pMultiMsg );
        }
    }
    else
    {
        delete pMultiMsg;
    }
}

void CtiCalculateThread::sendUserQuit( const std::string & who )
{
    CTILOG_INFO(dout, who <<" has asked for shutdown.");
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
        static const string sql = "SELECT DCH.POINTID, DCH.LASTUPDATE "
                                  "FROM DYNAMICCALCHISTORICAL DCH";

        DatabaseConnection connection { getCalcQueryTimeout() };
        DatabaseReader rdr { connection };

        rdr.setCommandText(sql);

        rdr.execute();

        //  iterate through the components
        while( rdr() )
        {

            //  read 'em in, and append to the class
            rdr["POINTID"] >> pointid;
            rdr["LASTUPDATE"] >> updateTime;

            dbTimeMap.insert(PointTimeMap::value_type(pointid, updateTime));
        }

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

}

void CtiCalculateThread::getHistoricalTableData(CtiCalc *calcPoint, CtiTime &lastTime, DynamicTableData &data)
{
    data.clear();
    long pointid;
    CtiTime timeStamp;
    double value;
    DynamicTableDataIter iter;

    if( calcPoint->getComponentIDList().empty() )
    {
        return;
    }

    try
    {
        set<long> compIDList = calcPoint->getComponentIDList();

        static const string sqlIds = "SELECT RPH.POINTID, RPH.TIMESTAMP, RPH.VALUE "
                                     "FROM RAWPOINTHISTORY RPH "
                                     "WHERE RPH.TIMESTAMP > ?";

        DatabaseConnection connection { getCalcQueryTimeout() };
        DatabaseReader rdr { connection };

        std::stringstream ss;

        ss << sqlIds << " AND RPH.POINTID IN (";

        for( set<long>::iterator idIter = compIDList.begin(); idIter != compIDList.end(); idIter++ )
        {
            if( idIter != compIDList.begin() )
            {
                ss << ", " << *idIter;
            }
            else
            {
                ss << *idIter;
            }
        }

        ss << ")";

        rdr.setCommandText(ss.str());
        rdr << lastTime;

        rdr.execute();

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
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            static const string sqlCore =  "SELECT RPH.POINTID, RPH.TIMESTAMP, RPH.VALUE "
                                           "FROM RAWPOINTHISTORY RPH "
                                           "WHERE RPH.TIMESTAMP > ? AND RPH.POINTID = ? "
                                           "ORDER BY RPH.TIMESTAMP DESC";

            DatabaseConnection connection { getCalcQueryTimeout() };
            DatabaseReader rdr { connection, sqlCore };

            rdr << lastTime
                << calcPoint;

            rdr.execute();

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
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }
}

void CtiCalculateThread::setHistoricalPointStore(HistoricalPointValueMap &valueMap)
{
    HistoricalPointValueMap::iterator iter;
    for( iter = valueMap.begin(); iter != valueMap.end(); iter++ )
    {
        CtiPointStoreElement* calcPointPtr = CtiPointStore::find(iter->first);
        calcPointPtr->setHistoricValue(iter->second);
    }
}

void CtiCalculateThread::updateCalcHistoricalLastUpdatedTime(PointTimeMap &unlistedPoints, PointTimeMap &updatedPoints)
{
    //The plan is to update the updated points and add the unlisted points.
    long pointid;
    CtiTime updateTime;
    PointTimeMap::iterator iter;

    try
    {
        static const string insertSql = "insert into DYNAMICCALCHISTORICAL values (?, ?)";
        DatabaseConnection connection { getCalcQueryTimeout() };
        DatabaseWriter writer { connection, insertSql };

        for( iter = unlistedPoints.begin(); iter != unlistedPoints.end(); iter++ )
        {
            writer << iter->first << iter->second;
            if( ! Cti::Database::executeCommand( writer, CALLSITE ))
            {
                CTILOG_ERROR(dout, "Failed to insert in CalcHistoricalUpdatedTime");
                break;
            }
        }

        static const string updateSql = "update DYNAMICCALCHISTORICAL set LASTUPDATE = ? where POINTID = ?";
        writer.setCommandText(updateSql);

        for( iter = updatedPoints.begin(); iter != updatedPoints.end(); iter++ )
        {
            writer << iter->second << iter->first;
            Cti::Database::executeCommand( writer, CALLSITE );
        }

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

}

void CtiCalculateThread::getCalcBaselineMap(PointBaselineMap &pointBaselineMap)
{
    pointBaselineMap.clear();
    long pointid, baselineID;

    try
    {
        static const string sql = "SELECT CPB.POINTID, CPB.BASELINEID "
                                  "FROM CALCPOINTBASELINE CPB";

        DatabaseConnection connection { getCalcQueryTimeout() };
        DatabaseReader rdr { connection };

        rdr.setCommandText(sql);

        rdr.execute();

        //  iterate through the components
        while( rdr() )
        {

            //  read 'em in, and append to the class
            rdr["POINTID"] >> pointid;
            rdr["BASELINEID"] >> baselineID;

            pointBaselineMap.insert(PointBaselineMap::value_type(pointid, baselineID));
        }

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCalculateThread::getBaselineMap(BaselineMap &baselineMap)
{
    baselineMap.clear();
    long baselineID;
    BaselineData baseline;

    try
    {
        static const string sql =  "SELECT BL.BASELINEID, BL.DAYSUSED, BL.PERCENTWINDOW, BL.CALCDAYS, BL.EXCLUDEDWEEKDAYS, "
                                     "BL.HOLIDAYSCHEDULEID "
                                   "FROM BASELINE BL";

        DatabaseConnection connection { getCalcQueryTimeout() };
        DatabaseReader rdr { connection };

        rdr.setCommandText(sql);

        rdr.execute();

        //  iterate through the components
        while( rdr() )
        {

            //  read 'em in, and append to the class
            rdr["BASELINEID"] >> baselineID;
            rdr["DAYSUSED"] >> baseline.maxSearchDays;
            rdr["PERCENTWINDOW"] >> baseline.percent;
            rdr["CALCDAYS"] >> baseline.usedDays;
            rdr["EXCLUDEDWEEKDAYS"] >> baseline.excludedWeekDays;
            rdr["HOLIDAYSCHEDULEID"] >> baseline.holidays;

            //The copy is unfortunate, but relatively inexpensive for a small number of objects
            baselineMap.insert(BaselineMap::value_type(baselineID, baseline));
        }

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

//This function could be much better then it is right now, but thats how it goes.
void CtiCalculateThread::getCurtailedDates(DatesSet &curtailedDates, long pointID, CtiTime &searchTime)
{
    CtiTime newTime;
    curtailedDates.clear();
    try
    {
        static const string sql =  "SELECT PO.OFFERDATE "
                                   "FROM LMENERGYEXCHANGEPROGRAMOFFER PO, LMENERGYEXCHANGECUSTOMERREPLY CR, "
                                     "DEVICECUSTOMERLIST DCL, POINT PT "
                                   "WHERE DCL.CUSTOMERID = CR.CUSTOMERID AND CR.OFFERID = PO.OFFERID AND "
                                     "DCL.DEVICEID = PT.PAOBJECTID AND CR.ACCEPTSTATUS= 'Accepted' AND "
                                     "PT.POINTID = ? AND PO.OFFERDATE > ?";

        DatabaseConnection connection { getCalcQueryTimeout() };
        DatabaseReader rdr { connection, sql };

        rdr << pointID
            << searchTime;

        rdr.execute();

        //  iterate through the components
        while( rdr() )
        {
            //  read 'em in, and append to the data structure
            rdr["OFFERDATE"] >> newTime;
            curtailedDates.insert(newTime.date());
        }

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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

    if( CtiPointStoreElement* calcPointPtr = CtiPointStore::find(baselineID) )
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
            while( iter != data.end() && iter->first <= curTime )
            {
                value += iter->second.second;
                count++;
                iter++;
            }

            if( count == 0 ) //There we no entries that met our criteria
            {
                retVal = false;
            }

            if( isKW && count != 0)
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
            if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
            {
                CTILOG_DEBUG(dout, "No data found at all for day. "<< curTime.date());
            }
            retVal = false;
            break; //break out of the for loop. no point in continuing.
        }
    }

    if( percent > 0 && retVal != false )
    {
        if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
        {
            CTILOG_DEBUG(dout, "Percent processing beginning on day " << curTime.date());
        }
        HourlyValues percentResults;
        float decimalPercent = (float)percent/100;
        //we need to compare to values in the percent baseline component
        retVal = processDay(baselineID, startTime, percentData, percentData, 0, percentResults); //get percentResults for use
        for( int i = 0; i < 24 && retVal != false; i++ )
        {
            if( results[i] < (decimalPercent*percentResults[i]) )
            {
                if( _CALC_DEBUG & CALC_DEBUG_BASELINE)
                {
                    CTILOG_DEBUG(dout, "Baseline Percent Failed, percent, results, percentResults: "<< decimalPercent <<" "<< results[i] <<" "<< percentResults[i]);
                }
                retVal = false;
            }
        }
    }

    return retVal;
}

