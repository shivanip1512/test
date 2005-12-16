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

void CtiCalculateThread::pointChange( long changedID, double newValue, RWTime &newTime, unsigned newQuality, unsigned newTags )
{
    try
    {
        RWMutexLock::LockGuard msgLock(_pointDataMutex);
        RWTValHashSetIterator<depStore, depStore, depStore> *dependentIterator;
        CtiHashKey hashKey(changedID);

        CtiPointStore* pointStore = CtiPointStore::getInstance();
        CtiPointStoreElement* pointPtr = (CtiPointStoreElement*)((*pointStore)[&hashKey]);
        if( _CALC_DEBUG & CALC_DEBUG_POINTDATA )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - Point Data ID: " << changedID << " Val: " << newValue << " Time: " << newTime << " Quality: " << newQuality << endl;
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
                    _auAffectedPoints.append( dependentIterator->key( ).dependentID );
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
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


void CtiCalculateThread::periodicThread( void )
{
    try
    {
        bool calcValid;
        float msLeftThisSecond;
        RWTime newTime, tempTime;
        CtiCalcPointMapIterator periodicIter( _periodicPoints );
        RWRunnableSelf _pSelf = rwRunnable( );
        BOOL interrupted = FALSE, messageInMulti;
        clock_t now;

        RWTime rwnow, announceTime;
        announceTime = nextScheduledTimeAlignedOnRate( RWTime(), 300);

        while( !interrupted )
        {
            rwnow = rwnow.now();
            if(rwnow > announceTime)
            {
                announceTime = nextScheduledTimeAlignedOnRate( rwnow, 300 );
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " periodicThread thread active. TID: " << rwThreadId() << endl;
                }

                ThreadMonitor.tickle( new CtiThreadRegData( rwThreadId(), "CalcLogicSvc periodicThread", CtiThreadRegData::Action1, 350, &CtiCalculateThread::periodicComplain, 0 , 0,  0 ) );
            }

            //  while it's still the same second /and/ i haven't been interrupted
            for( ; newTime == tempTime && !interrupted; )
            {
                tempTime = RWTime( );
                if( _pSelf.serviceInterrupt( ) )
                    interrupted = TRUE;
                else
                    _pSelf.sleep( 250 );
            }

            if( interrupted )
                continue;

            now = clock( );

            long pointId;
            CtiCalc *calcPoint;
            double newPointValue, oldPointValue;

            CtiMultiMsg *periodicMultiMsg = new CtiMultiMsg;
            char pointDescription[80];

            periodicIter.reset( );
            messageInMulti = FALSE;

            int calcQuality;
            RWTime calcTime;
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
                        dout << RWTime() << " ### Calculation of point " << calcPoint->getPointId() << " was invalid (ex. div by zero or sqrt(<0))." << endl;
                    }

                    calcQuality = NonUpdatedQuality;
                    newPointValue = oldPointValue;
                }

                sprintf( pointDescription, "calc point %l update", pointId );

                CtiPointDataMsg *pointData = new CtiPointDataMsg(pointId, newPointValue, calcQuality, InvalidPointType, pointDescription);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself
                pointData->setTime(calcTime);

                periodicMultiMsg->getData( ).insert( pointData );

                if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " PeriodCalc setting Calc Point ID: " << pointId << " to New Value: " << newPointValue << endl;
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
                    dout << RWTime( ) << "  periodicThread posting a message - took " << (clock( ) - now) << " ticks" << endl;
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
            dout << RWTime( ) << " periodicThread interrupted. TID: " << rwThreadId() << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    ThreadMonitor.tickle( new CtiThreadRegData( rwThreadId(), "CalcLogicSvc periodicThread", CtiThreadRegData::LogOut ) );
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
        RWTime calcTime;

        RWTime rwnow, announceTime;
        announceTime = nextScheduledTimeAlignedOnRate( RWTime(), 300);

        while( !interrupted )
        {
            //  this is the cleanest way to do it;  i know it's a do-while, but that gets rid of an unnecessary if statement
            do
            {
                rwnow = rwnow.now();
                if(rwnow > announceTime)
                {
                    announceTime = nextScheduledTimeAlignedOnRate( rwnow, 300 );
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " onUpdateThread thread active. TID: " << rwThreadId() << endl;
                    }

                    ThreadMonitor.tickle( new CtiThreadRegData( rwThreadId(), "CalcLogicSvc onUpdateThread", CtiThreadRegData::Action1, 350, &CtiCalculateThread::onUpdateComplain, 0 , 0, 0 ) );
                }

                if( _auSelf.serviceInterrupt( ) )
                {
                    interrupted = TRUE;
                }
                else
                {
                    _auSelf.sleep( 250 );
                }

            } while( !_auAffectedPoints.entries( ) && !interrupted );

            //  if I was interrupted while I was sleeping, I don't care about any points I may have received - it's time to go.
            if( interrupted )
                continue;

            pChg = new CtiMultiMsg;
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
                            dout << RWTime() << " ### Calculation of point " << calcPoint->getPointId() << " was invalid (ex. div by zero or sqrt(<0))." << endl;
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
                            dout << RWTime() << " Calc point " << calcPoint->getPointId() << " calculation result is attempting to move backward in time." << endl;
                            dout << RWTime() << "   Update type may be inappropriate for the component device/points." << endl;
                        }
                        calcTime = RWTime().now();
                    }

                    CtiPointDataMsg *pData = NULL;

                    if( calcPointPtr->getPointCalcWindowEndTime() > RWTime(RWDate(1,1,1991)) )
                    {// demand average point madness

                        long davgpid = calcPoint->findDemandAvgComponentPointId();

                        if(davgpid)
                        {
                            CtiHashKey componentPointHashKey(davgpid);
                            CtiPointStoreElement* componentPointPtr = 0;

                            componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentPointHashKey]);

                            if(componentPointPtr)
                            {
                                RWTime now;
                                RWTime et = calcPointPtr->getPointCalcWindowEndTime();
                                RWTime etplus = (calcPointPtr->getPointCalcWindowEndTime() + componentPointPtr->getSecondsSincePreviousPointTime());

                                if( et <= now &&  now < etplus )    // Are we greater than the end time, but less than the end time + "slop"
                                {
                                    if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " - New Point Data message for Calc Point Id: " << recalcPointID << " New Demand Avg Value: " << recalcValue << endl;
                                    }
                                    pData = new CtiPointDataMsg(recalcPointID, recalcValue, calcQuality, InvalidPointType);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself
                                    pData->setTime(calcPointPtr->getPointCalcWindowEndTime());
                                }

                                calcPointPtr->setPointValue( recalcValue, RWTime(), NormalQuality, 0 );
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Error Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Calc Point " << calcPoint->getPointId() << endl;
                                }
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** CONFIG ERROR **** Demand Average points require a point to be identified (no pre-push).  Point ID " << calcPoint->getPointId() << endl;
                            }
                        }
                    }
                    else
                    {//normal calc point
                        pData = new CtiPointDataMsg(recalcPointID, recalcValue, calcQuality, InvalidPointType);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself
                        pData->setTime(calcTime);
                    }

                    if( pData != NULL )
                    {
                        pointsInMulti = TRUE;
                        sprintf( pointDescription, "calc point %l update", recalcPointID );
                        pData->setString(pointDescription);
                        pChg->getData( ).insert( pData );
                    }

                    if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " onUpdateThread setting Calc Point ID: " << recalcPointID << " to New Value: " << recalcValue << endl;
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
                    dout << RWTime( ) << "  onUpdateThread posting a message" << endl;
                }
            }
            else
            {
                delete pChg;
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime( ) << " onUpdateThread interrupted. TID: " << rwThreadId() << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    ThreadMonitor.tickle( new CtiThreadRegData( rwThreadId(), "CalcLogicSvc onUpdateThread", CtiThreadRegData::LogOut ) );
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
           ThreadMonitor.tickle( new CtiThreadRegData( rwThreadId(), "CalcLogicSvc calcThread", CtiThreadRegData::Action1, 350, &CtiCalculateThread::calcComplain, 0 , 0, 0 ) );
       }

       _self.sleep( 1000 );
    }

    ThreadMonitor.tickle( new CtiThreadRegData( rwThreadId(), "CalcLogicSvc calcThread", CtiThreadRegData::LogOut ) );

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
            dout << RWTime() << " periodicThread did not shutdown gracefully.  Will attempt a forceful shutdown." << endl;
        }
        _periodicThreadFunc.terminate();
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " periodicThread shutdown correctly." << endl;
        }
    }

    if(RW_THR_COMPLETED != _onUpdateThreadFunc.join( 30000 ))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " onUpdateThread did not shutdown gracefully.  Will attempt a forceful shutdown." << endl;
        }

        _onUpdateThreadFunc.terminate();
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " onUpdateThread shutdown correctly." << endl;
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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Unable to interrupt the periodicThread. Calc may become unstable!" << endl;
        }
    }

    if( RW_THR_TIMEOUT == _onUpdateThreadFunc.requestInterrupt( 5000 ) )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime() << " " << msg.why() <<  " " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


void CtiCalculateThread::appendCalcPoint( long pointID )
{
    CtiHashKey pointHashKey(pointID);
    CtiPointStoreElement *tmpElementPtr = NULL;

    tmpElementPtr = CtiPointStore::getInstance()->insertPointElement( pointID, 0, undefined );
}

bool CtiCalculateThread::appendPoint( long pointid, RWCString &updatetype, int updateinterval )
{
    bool inserted = false;

    CtiCalc *newPoint;
    newPoint = new CtiCalc( pointid, updatetype, updateinterval );
    switch( newPoint->getUpdateType( ) )
    {
    case periodic:
        inserted = _periodicPoints.insert( new CtiHashKey(pointid), newPoint );
        break;
    case allUpdate:
    case anyUpdate:
    case periodicPlusUpdate:
        inserted = _onUpdatePoints.insert( new CtiHashKey(pointid), newPoint );
        break;
    case constant:
        inserted = _constantPoints.insert( new CtiHashKey(pointid), newPoint );
        break;
    case historical:
        delete newPoint;
        break;
    default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ") Attempt to insert unknown CtiCalc point type \"" << updatetype
            << "\", value \"" << newPoint->getUpdateType() << "\";  aborting point insert" << endl;
        }

        delete newPoint;
        break;
    }
    return inserted;
}


void CtiCalculateThread::appendPointComponent( long pointID, RWCString &componentType, long componentPointID,
                                               RWCString &operationType, double constantValue, RWCString &functionName )
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
    newComponent = new CtiCalcComponent( componentType, componentPointID, operationType, constantValue, functionName );
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

void CtiCalculateThread::sendConstants()
{
    long pointId;
    CtiCalc *calcPoint;
    double pointValue, oldPointValue;

    CtiMultiMsg *pMultiMsg = new CtiMultiMsg;
    char pointDescription[80];
    BOOL messageInMulti = FALSE;

    bool calcValid;
    int calcQuality;
    RWTime calcTime;


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
                    dout << RWTime() << " ### Calculation of point " << calcPoint->getPointId() << " was invalid (ex. div by zero or sqrt(<0))." << endl;
                }

                calcQuality = NonUpdatedQuality;
                pointValue = oldPointValue;
            }

            sprintf( pointDescription, "calc point %l update", pointId );

            CtiPointDataMsg *pointData = new CtiPointDataMsg(pointId, pointValue, ConstantQuality, InvalidPointType, pointDescription);  // Use InvalidPointType so dispatch solves the Analog/Status nature by itself

            pMultiMsg->getData( ).insert( pointData );

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " sendConstants() setting Calc Point ID: " << pointId << " to CONSTANT Value: " << pointValue << endl;
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
       dout << RWTime( ) << " CalcLogicSvc onUpdate thread is AWOL" << endl;
   }
}

void CtiCalculateThread::periodicComplain( void *la )
{
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << RWTime( ) << " CalcLogicSvc periodic thread is AWOL" << endl;
   }
}

void CtiCalculateThread::calcComplain( void *la )
{
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << RWTime( ) << " CalcLogicSvc calc thread thread is AWOL" << endl;
   }
}

