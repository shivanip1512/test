#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

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

void CtiCalculateThread::pointChange( long changedID, double newValue, RWTime &newTime, unsigned newQuality, unsigned newTags )
{
    try
    {
        RWMutexLock::LockGuard msgLock(_pointDataMutex);
    //    RWTValHashSetIterator<depStore, depStore, depStore> *dependentIterator;
        RWTValHashSetIterator<depStore, depStore, depStore> *dependentIterator;
        CtiHashKey hashKey(changedID);
        //not sure about this --> BOOL informDependents = FALSE;

        CtiPointStore* pointStore = CtiPointStore::getInstance();
        CtiPointStoreElement* pointPtr = (CtiPointStoreElement*)((*pointStore)[&hashKey]);
        if( _CALC_DEBUG & CALC_DEBUG_POINTDATA )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - Point Data ID: " << changedID << " Val: " << newValue << " Time: " << newTime << " Quality: " << newQuality << endl;
        }

        if( newTime.seconds() > pointPtr->getPointTime().seconds() ||
            ( pointPtr->getNumUpdates() > 0 &&
              ( newQuality != pointPtr->getPointQuality() ||
                newValue != pointPtr->getPointValue() ||
                newTags != pointPtr->getPointTags() ) ) )
        {
            pointPtr->setPointValue( newValue, newTime, newQuality, newTags );
            dependentIterator = pointPtr->getDependents( );

            // not sure about this --> for( ; (*dependentIterator)( ) && informDependents; )

            for( ; (*dependentIterator)( ); )
            {
        //  currently, we don't have any other types that require knowing about dependencies, but who knows.  I had had this
        //    piece of code in here until i realized that there will likely only be one update type that cares whether a point's
        //    been updated - the allupdate type.
        //        switch( dependentIterator->key( ).updateType )
        //        {
        //            case allUpdate:
                        //  this is a bit of a hack - I'm counting on the probability that there will be fewer components
                        //    to check than possible affected points to insert into, to search for a duplicate in a set.
                        //    either way, I will be doing some extra work for each point that depends on multiple calc
                        //    points.
                        _auAffectedPoints.append( dependentIterator->key( ).dependentID );
        //        }
            }

            delete dependentIterator;
        }
        else if( pointPtr->getNumUpdates() == 0 )
        {
            pointPtr->firstPointValue( newValue, newTime, newQuality, newTags );
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


void CtiCalculateThread::periodicLoop( void )
{
    try
    {
        float msLeftThisSecond;
        RWTime newTime, tempTime;
        RWTPtrHashMapIterator<CtiHashKey, CtiCalc, my_hash<CtiHashKey> , equal_to<CtiHashKey> > periodicIter( _periodicPoints );
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
                    dout << RWTime() << " PeriodicLoop thread active" << endl;
                }
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
            double newPointValue;

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
                    newPointValue = calcPoint->calculate( calcQuality, calcTime );
                }

                calcPoint->setNextInterval(calcPoint->getUpdateInterval());

                //if( calcPoint->getSendFlag() )
                {
                    sprintf( pointDescription, "calc point %ul update", pointId );

                    CtiPointDataMsg *pointData = new CtiPointDataMsg(pointId, newPointValue, calcQuality,
                                                                     CalculatedPointType, pointDescription);
                    pointData->setTime(calcTime);

                    periodicMultiMsg->getData( ).insert( pointData );

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " PeriodCalc setting Calc Point ID: " << pointId << " to New Value: " << newPointValue << endl;
                    }
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
                    dout << RWTime( ) << "  periodicLoop posting a message - took " << (clock( ) - now) << " ticks" << endl;
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
            dout << RWTime( ) << " - Calc periodicLoop interrupted" << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCalculateThread::onUpdateLoop( void )
{
    try
    {
        long pointIDChanged, recalcPointID;
        double recalcValue;
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
                        dout << RWTime() << " OnUpdateLoop thread active" << endl;
                    }
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

                    CtiPointStore* pointStore = CtiPointStore::getInstance();
                    CtiHashKey pointHashKey(calcPoint->getPointId());
                    CtiPointStoreElement* pointPtr = (CtiPointStoreElement*)((*pointStore)[&pointHashKey]);

                    //  if not ready
                    if( calcPoint==NULL || !calcPoint->ready( ) )
                        continue;  // for

                    recalcValue = calcPoint->calculate( calcQuality, calcTime );
                    calcPoint->setNextInterval(calcPoint->getUpdateInterval());     // This only matters for periodicPlusUpdatePoints.

                    // Make sure we do not try to move backwards in time.
                    if( pointPtr->getPointTime() > calcTime )
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

                    if( calcPoint->getPointCalcWindowEndTime().seconds() > RWTime(RWDate(1,1,1991)).seconds() )
                    {// demand average point madness
                        CtiHashKey componentPointHashKey(calcPoint->findDemandAvgComponentPointId());
                        CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentPointHashKey]);

                        /*{
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " - Calc Point Id: " << recalcPointID
                                 << " New Demand Avg Value: " << recalcValue
                                 << " Point Calc Window End Time: " << calcPoint->getPointCalcWindowEndTime() << endl;
                        }*/
                        if( RWTime::now().seconds() >= calcPoint->getPointCalcWindowEndTime().seconds() &&
                            RWTime::now().seconds() < calcPoint->getPointCalcWindowEndTime().seconds() + componentPointPtr->getSecondsSincePreviousPointTime() )
                        {
                            if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " - New Point Data message for Calc Point Id: " << recalcPointID
                                     << " New Demand Avg Value: " << recalcValue << endl;
                            }
                            /*{
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << __FILE__ << " (" << __LINE__ << ") RWTime() >= calcPoint->getPointCalcWindowEndTime()" << endl;
                            }*/
                            pData = new CtiPointDataMsg(recalcPointID, recalcValue, calcQuality, CalculatedPointType, pointDescription);
                            pData->setTime(calcPoint->getPointCalcWindowEndTime());
                        }

                        pointPtr->setPointValue( recalcValue, RWTime(), NormalQuality, 0 );
                    }
                    else
                    {//normal calc point
                        pData = new CtiPointDataMsg(recalcPointID, recalcValue, calcQuality, CalculatedPointType, pointDescription);
                        pData->setTime(calcTime);
                    }

                    if( pData != NULL )
                    {
                        pointsInMulti = TRUE;
                        sprintf( pointDescription, "calc point %ul update", recalcPointID );
                        pChg->getData( ).insert( pData );
                    }

                    if( _CALC_DEBUG & CALC_DEBUG_THREAD_REPORTING )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " onUpdateLoop setting Calc Point ID: " << recalcPointID << " to New Value: " << recalcValue << endl;
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
                    dout << RWTime( ) << "  onUpdateLoop posting a message" << endl;
                }
            }
            else
            {
                delete pChg;
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime( ) << " - Calc onUpdateLoop interrupted" << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}


void CtiCalculateThread::calcLoop( void )
{
    RWRunnableSelf _self = rwRunnable( );

    RWThreadFunction periodicThreadFunc = rwMakeThreadFunction( (*this), &CtiCalculateThread::periodicLoop );
    RWThreadFunction onUpdateThreadFunc = rwMakeThreadFunction( (*this), &CtiCalculateThread::onUpdateLoop );
    periodicThreadFunc.start( );
    onUpdateThreadFunc.start( );

    //  while nobody's bothering me...
    while( !_self.serviceInterrupt( ) )
    {
        _self.sleep( 1000 );
    }

    //  scream at the other threads, tell them it's time for dinner
    periodicThreadFunc.requestInterrupt( );
    periodicThreadFunc.releaseInterrupt( );
    onUpdateThreadFunc.requestInterrupt( );
    onUpdateThreadFunc.releaseInterrupt( );
    periodicThreadFunc.join( );
    onUpdateThreadFunc.join( );
}

void CtiCalculateThread::appendCalcPoint( long pointID )
{
    CtiHashKey pointHashKey(pointID);
    CtiPointStoreElement *tmpElementPtr = NULL;

    tmpElementPtr = CtiPointStore::getInstance()->insertPointElement( pointID, 0, undefined );
}

void CtiCalculateThread::appendPoint( long pointid, RWCString &updatetype, int updateinterval )
{
    CtiCalc *newPoint;
    newPoint = new CtiCalc( pointid, updatetype, updateinterval );
    switch( newPoint->getUpdateType( ) )
    {
        case periodic:
            _periodicPoints.insert( new CtiHashKey(pointid), newPoint );
            break;
        case allUpdate:
        case anyUpdate:
        case periodicPlusUpdate:
            _onUpdatePoints.insert( new CtiHashKey(pointid), newPoint );
            break;
        case historical:
            break;
        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << __FILE__ << " (" << __LINE__ << ") Attempt to insert unknown CtiCalc point type \"" << updatetype
                     << "\", value \"" << newPoint->getUpdateType() << "\";  aborting point insert" << endl;
            }
            break;
    }
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
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << __FILE__ << " (" << __LINE__ << ") Can't find calc point \"" << pointID
        << "\" in either point collection;  aborting component insert" << endl;
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

    newComponent = new CtiCalcComponent( componentType, componentPointID, operationType,
                                         constantValue, functionName );
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

    if (foundPoint == FALSE)
    {
        // ID was not found yet look here...
        foundPoint = isAnOnUpdateCalcPointID(aPointID);
    }

    return foundPoint;
}


BOOL CtiCalculateThread::isAPeriodicCalcPointID(const long aPointID)
{
    CtiCalc *calcPoint;

    BOOL foundPoint(FALSE);

    CtiHashKey recalcKey(aPointID);

    calcPoint = (CtiCalc *)(_periodicPoints.find(&recalcKey));

    if (calcPoint != rwnil)
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

    if (calcPoint != rwnil)
    {
        foundPoint = TRUE;
    }

    return foundPoint;
}

