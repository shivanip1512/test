#include "yukon.h"

#include <rw/thr/runnable.h>
#include <rw/thr/mutex.h>

#include "ctibase.h"
#include "pointtypes.h"
#include "message.h"
#include "msg_multi.h"
#include "logger.h"
#include "guard.h"
#include "utility.h"

#include "calcthread.h"

extern ULONG _CALC_DEBUG;
extern BOOL  UserQuit;
extern bool _shutdownOnThreadTimeout;

CtiCalculateThread::~CtiCalculateThread( void )
{
    _auAffectedPoints.clear();
    if( _periodicPoints.entries() > 0 )
    {
        _periodicPoints.clearAndDestroy();
    }
    if( _onUpdatePoints.entries() > 0 )
    {
        _onUpdatePoints.clearAndDestroy();
    }
    if( _constantPoints.entries() > 0 )
    {
        _constantPoints.clearAndDestroy();
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
            dout << CtiTime() << " - Point Data ID: " << changedID << " Val: " << newValue << " Time: " << newTime << " Quality: " << newQuality << endl;
        }

        if(pointPtr)
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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

        if(pointPtr)
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
        CtiCalcPointMapIterator periodicIter( _periodicPoints );
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

            periodicIter.reset( );
            messageInMulti = FALSE;

            int calcQuality;
            CtiTime calcTime;
            for( ; periodicIter( ); )
            {
                calcPoint = (CtiCalc *)(periodicIter.value( ));
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
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc periodicThread", CtiThreadRegData::LogOut ) );
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
                    calcPoint = (CtiCalc *)(_onUpdatePoints[&recalcKey]);

                    //  if not ready
                    if( calcPoint==NULL || !calcPoint->ready( ) )
                        continue;  // All the components are not ready.

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
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc onUpdateThread", CtiThreadRegData::LogOut ) );
}


void CtiCalculateThread::calcThread( void )
{
    int cnt = 0;
    RWRunnableSelf _self = rwRunnable( );

    startThreads();

    //  while nobody's bothering me...
    while( !_self.serviceInterrupt( ) )
    {
       if( cnt++ % 300 == 0 )
       {
           ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc calcThread", CtiThreadRegData::Action, 350, &CtiCalculateThread::calcComplain, 0) );
       }

       _self.sleep( 1000 );
    }

    ThreadMonitor.tickle( CTIDBG_new CtiThreadRegData( rwThreadId(), "CalcLogicSvc calcThread", CtiThreadRegData::LogOut ) );

    //  scream at the other threads, tell them it's time for dinner
    interruptThreads( CtiCalcThreadInterruptReason::Shutdown );
    joinThreads();
}

void CtiCalculateThread::startThreads(  )
{
    _periodicThreadFunc = rwMakeThreadFunction( (*this), &CtiCalculateThread::periodicThread );
    _onUpdateThreadFunc = rwMakeThreadFunction( (*this), &CtiCalculateThread::onUpdateThread );
    _periodicThreadFunc.start( );
    _onUpdateThreadFunc.start( );
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
}

void CtiCalculateThread::resumeThreads(  )
{
    try
    {
        _onUpdateThreadFunc.releaseInterrupt( );
        _periodicThreadFunc.releaseInterrupt( );
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


void CtiCalculateThread::appendCalcPoint( long pointID )
{
    CtiPointStore::getInstance()->insertPointElement( pointID, 0, undefined );
}

bool CtiCalculateThread::appendPoint( long pointid, string &updatetype, int updateinterval, string &qualityFlag )
{
    bool inserted = false;

    CtiCalc *newPoint;
    newPoint = CTIDBG_new CtiCalc( pointid, updatetype, updateinterval, qualityFlag );
    switch( newPoint->getUpdateType( ) )
    {
    case periodic:
        inserted = _periodicPoints.insert( CTIDBG_new CtiHashKey(pointid), newPoint );
        break;
    case allUpdate:
    case anyUpdate:
    case periodicPlusUpdate:
        inserted = _onUpdatePoints.insert( CTIDBG_new CtiHashKey(pointid), newPoint );
        break;
    case constant:
        inserted = _constantPoints.insert( CTIDBG_new CtiHashKey(pointid), newPoint );
        break;
    case historical:
        delete newPoint;
        newPoint = NULL;
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

    if( !inserted && newPoint != NULL )
    {
        delete newPoint;
        newPoint = NULL;
    }
    return inserted;
}


void CtiCalculateThread::appendPointComponent( long pointID, string &componentType, long componentPointID,
                                               string &operationType, double constantValue, string &functionName )
{
    CtiHashKey pointHashKey(pointID);
    CtiCalc *targetCalcPoint;
    CtiCalcComponent *newComponent;
    CtiPointStoreElement *tmpElementPtr = NULL;
    PointUpdateType updateType;

    if( _periodicPoints.contains( &pointHashKey ) )
    {
        targetCalcPoint  = _periodicPoints[&pointHashKey];
        updateType = periodic;
    }
    else if( _onUpdatePoints.contains( &pointHashKey ) )
    {
        targetCalcPoint  = _onUpdatePoints[&pointHashKey];
        updateType = targetCalcPoint->getUpdateType();
    }
    else if( _constantPoints.contains( &pointHashKey ) )
    {
        targetCalcPoint  = _constantPoints[&pointHashKey];
        updateType = constant;
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

    //  insert parameters are (point, dependent, updatetype)
    tmpElementPtr = CtiPointStore::getInstance()->insertPointElement( componentPointID, pointID, updateType );
    newComponent = CTIDBG_new CtiCalcComponent( componentType, componentPointID, operationType, constantValue, functionName );
    targetCalcPoint->appendComponent( newComponent );
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

    return foundPoint;
}


BOOL CtiCalculateThread::isAPeriodicCalcPointID(const long aPointID)
{
    CtiCalc *calcPoint;

    BOOL foundPoint(FALSE);

    CtiHashKey recalcKey(aPointID);

    calcPoint = (CtiCalc *)(_periodicPoints.find(&recalcKey));

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

    calcPoint = (CtiCalc *)(_onUpdatePoints.find(&recalcKey));

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

    calcPoint = (CtiCalc *)(_constantPoints.find(&recalcKey));

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

void CtiCalculateThread::setPeriodicPointMap(const CtiCalcPointMap &points)
{
    if( _periodicPoints.entries() > 0 )
    {
        _periodicPoints.clearAndDestroy();
    }
    _periodicPoints = points;
}

void CtiCalculateThread::setOnUpdatePointMap(const CtiCalcPointMap &points)
{
    if( _onUpdatePoints.entries() > 0 )
    {
        _onUpdatePoints.clearAndDestroy();
    }
    _onUpdatePoints = points;
}

void CtiCalculateThread::setConstantPointMap(const CtiCalcPointMap &points)
{
    if( _constantPoints.entries() > 0 )
    {
        _constantPoints.clearAndDestroy();
    }
    _constantPoints = points;
}

void CtiCalculateThread::clearAndDestroyPointMaps()
{
    if( _constantPoints.entries() > 0 )
    {
        _constantPoints.clearAndDestroy();
    }

    if( _onUpdatePoints.entries() > 0 )
    {
        _onUpdatePoints.clearAndDestroy();
    }

    if( _periodicPoints.entries() > 0 )
    {
        _periodicPoints.clearAndDestroy();
    }
}

void CtiCalculateThread::clearPointMaps()
{
    if( _constantPoints.entries() > 0 )
    {
        _constantPoints.clear();
    }

    if( _onUpdatePoints.entries() > 0 )
    {
        _onUpdatePoints.clear();
    }

    if( _periodicPoints.entries() > 0 )
    {
        _periodicPoints.clear();
    }
}

void CtiCalculateThread::removePointStoreObject( const long aPointID )
{
    CtiHashKey pointHashKey(aPointID);
    CtiCalc *targetCalcPoint = NULL;
    CtiPointStoreElement *tmpElementPtr = NULL;

    if( _periodicPoints.contains( &pointHashKey ) )
    {
        targetCalcPoint  = _periodicPoints[&pointHashKey];
    }
    else if( _onUpdatePoints.contains( &pointHashKey ) )
    {
        targetCalcPoint  = _onUpdatePoints[&pointHashKey];
    }
    else if( _constantPoints.contains( &pointHashKey ) )
    {
        targetCalcPoint  = _constantPoints[&pointHashKey];
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


    CtiCalcPointMapIterator constIter( _constantPoints );

    for( ; constIter( ); )
    {
        calcPoint = (CtiCalc *)(constIter.value( ));

        if( calcPoint!=NULL )
        {
            messageInMulti = TRUE;
            pointId = calcPoint->getPointId( );

            {
                RWMutexLock::LockGuard msgLock(_pointDataMutex);
                CtiPointStore* pointStore = CtiPointStore::getInstance();
                CtiHashKey pointHashKey(calcPoint->getPointId());
                CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore)[&pointHashKey]);

                oldPointValue = calcPointPtr->getPointValue();
                pointValue = calcPoint->calculate( calcQuality, calcTime, calcValid );    // Here is the MATH
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
