#include "yukon.h"

#include "include\calc.h"
#include "logger.h"
#include "utility.h"
#include "rwutil.h"

#include <rw/collect.h>
#include <rw/thr/mutex.h>
#include "ctitime.h"

extern ULONG _CALC_DEBUG;

using namespace std;

RWDEFINE_NAMED_COLLECTABLE( CtiCalc, "CtiCalc" );

// static const strings
const CHAR * CtiCalc::UpdateType_Periodic   = "On Timer";
const CHAR * CtiCalc::UpdateType_AllChange  = "On All Change";
const CHAR * CtiCalc::UpdateType_OneChange  = "On First Change";
const CHAR * CtiCalc::UpdateType_Historical = "Historical";
const CHAR * CtiCalc::UpdateType_PeriodicPlusUpdate = "On Timer+Change";
const CHAR * CtiCalc::UpdateType_Constant   = "Constant";

CtiCalc::CtiCalc( long pointId, const string &updateType, int updateInterval )
{
    _valid = TRUE;
    _pointId = pointId;

    if( (!stringCompareIgnoreCase(updateType,UpdateType_Periodic))
        && (updateInterval > 0) )
    {
        _updateInterval = updateInterval;
        setNextInterval (updateInterval);
        _updateType = periodic;
    }
    else if( !stringCompareIgnoreCase(updateType,UpdateType_AllChange))
    {
        _updateInterval = 0;
        _updateType = allUpdate;
    }
    else if( !stringCompareIgnoreCase(updateType,UpdateType_OneChange))
    {
        _updateInterval = 0;
        _updateType = anyUpdate;
    }
    else if( !stringCompareIgnoreCase(updateType,UpdateType_Historical))
    {
        _valid = FALSE;
        _updateInterval = 0;
        _updateType = historical;
    }
    else if( !stringCompareIgnoreCase(updateType,UpdateType_PeriodicPlusUpdate) )
    {
        _updateInterval = updateInterval;
        setNextInterval (updateInterval);
        _updateType = periodicPlusUpdate;
    }
    else if( !stringCompareIgnoreCase(updateType,UpdateType_Constant) )
    {
        _updateInterval = 0;
        _updateType = constant;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Invalid Update Type: " << updateType << endl;
        }
        _valid = FALSE;
    }

    if( _valid )
    {
        _pointCalcWindowEndTime = CtiTime(CtiDate(1,1,1990));
    }
}

CtiCalc &CtiCalc::operator=( CtiCalc &toCopy )
{
    RWSlistCollectablesIterator copyIterator( toCopy._components );

    //  make sure I'm squeaky clean to prevent memory leaks
    this->cleanup( );

    //  must do a deep copy;  components aren't common enough to justify a reference-counted shallow copy
    for( ; copyIterator( ); )
    {
        CtiCalcComponent *tmp;
        tmp = CTIDBG_new  CtiCalcComponent( *((CtiCalcComponent *)copyIterator.key( )) );
        this->appendComponent( tmp );
    }
    _updateInterval = toCopy._updateInterval;
    _pointId = toCopy._pointId;
    _valid = toCopy._valid;
    return *this;
}

void CtiCalc::appendComponent( CtiCalcComponent *componentToAdd )
{
    _components.append( componentToAdd );
    componentToAdd->passParent( this );
}


void CtiCalc::cleanup( void )
{
    _components.clearAndDestroy( );
}

void CtiCalc::clearComponentDependencies( void )
{
    RWSlistCollectablesIterator iter( _components );
    CtiCalcComponent *tmpComponent;

    for( ; iter(); )
    {
        tmpComponent = (CtiCalcComponent *)iter.key( );

        CtiPointStore* pointStore = CtiPointStore::getInstance();
        CtiHashKey componentHashKey(tmpComponent->getComponentPointId());
        CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore).findValue(&componentHashKey));

        if ( componentPointPtr )
		{
            if( componentPointPtr->removeDependent(_pointId) == 0 )
            {//There are no dependents left, no one cares about this guy!
                pointStore->removePointElement( tmpComponent->getComponentPointId() );
            }
        }
    }
}


double CtiCalc::calculate( int &calc_quality, CtiTime &calc_time, bool &calcValid )
{
    double retVal = 0.0;
    RWSlistCollectablesIterator iter( _components );

    try
    {
        //  Iterate through all of the calculations in the collection
        if( _CALC_DEBUG & CALC_DEBUG_PRECALC_VALUE )
        {
            CtiPointStore* pointStore = CtiPointStore::getInstance();

            CtiHashKey calcPointHashKey(_pointId);
            CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore)[&calcPointHashKey]);

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - CtiCalc::calculate(); Calc Point ID:" << _pointId << "; Start Value:" << calcPointPtr->getPointValue() << endl;
        }
        _stack.clear();     // Start with a blank stack.
        push( retVal );     // Prime the stack with a zero value (should effectively clear it).

        bool solidTime = false;             // If time is "solid" all components are the same time stamp.
        int componentQuality, qualityFlag = 0;
        CtiTime componentTime, 
            minTime = CtiTime(YUKONEOT), 
            maxTime = rwEpoch;//TS FLAG

        /*
         *  Iterate this calc's components passing in each succesive result (through retVal).
         */
        for( ; iter( ) && _valid; )
        {
            CtiCalcComponent *tmpComponent = (CtiCalcComponent *)iter.key( );
            _valid = _valid & tmpComponent->isValid( );  //  Entire calculation is only valid if each component is valid

            retVal = tmpComponent->calculate( retVal, componentQuality, componentTime, calcValid );  //  Calculate on returned value

            qualityFlag |= (1 << componentQuality);    // Flag each returned quality...
            solidTime = calcTimeFromComponentTime( componentTime, componentQuality, minTime, maxTime );
        }

        calc_time = calcTimeFromComponentTime( minTime, maxTime );
        calc_quality = calcQualityFromComponentQuality( qualityFlag, minTime, maxTime );

        if( !_valid )   //  NOT valid - actually, you should never get here, because the ready( ) back in CalcThread should
        {
            //    detect that you're invalid, and reject you with a "not ready" then.
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ")  ERROR - attempt to calculate invalid point \"" << _pointId << "\" - returning 0.0" << endl;
            retVal = 0.0;
        }

        if( _CALC_DEBUG & CALC_DEBUG_POSTCALC_VALUE )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - CtiCalc::calculate(); Calc Point ID:" << _pointId << "; Return Value:" << retVal << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION in calculate **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "    Calc Point ID " << _pointId << endl;
        }
    }
    return retVal;
}

bool CtiCalc::push( double val )
{
    _stack.push( val );
    return( _stack.entries() == 2 );    // Was this the first push (after the push(retVal = 0) stack primer)?
}


double CtiCalc::pop( void )
{
    double val;
    if( !_stack.isEmpty( ) )
    {
        val = _stack.top( );
        _stack.pop( );
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " ERROR - attempt to pop from empty stack in point \"" << _pointId << "\" - returning 0.0 " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        val = 0.0;
    }
    return val;
}


PointUpdateType CtiCalc::getUpdateType( void )
{
    return _updateType;
}


BOOL CtiCalc::ready( void )
{
    RWSlistCollectablesIterator iter( _components );
    BOOL isReady = TRUE;

    try
    {
        if( !_valid )
        {
            setNextInterval(getUpdateInterval());       // It is not valid, do not harp about it so often!
            isReady = FALSE;

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " - CtiCalc::ready( ) - Point " << _pointId << " is INVALID." << endl;
        }
        else
        {
            switch( _updateType )
            {
            case periodic:
                if(CtiTime::now().seconds() > getNextInterval())
                {
                    isReady = TRUE;
                }
                else
                {
                    isReady = FALSE;
                }
                break;
            case allUpdate:
                for( ; iter( ); )
                    isReady &= ((CtiCalcComponent *)(iter.key( )))->isUpdated( );
                break;
            case constant:
                isReady = TRUE;
                break;
            case anyUpdate:
                for( ; iter( ); )
                {
                    isReady |= ((CtiCalcComponent *)(iter.key( )))->isUpdated( );
                    if( isReady )
                    {
                        break;
                    }
                }
                break;
            case periodicPlusUpdate:
                {
                    if(CtiTime::now().seconds() >= getNextInterval())
                    {
                        for( ; iter( ); )
                            isReady &= ((CtiCalcComponent *)(iter.key( )))->isUpdated( _updateType, CtiTime(getNextInterval()) );
                    }
                    else
                    {
                        isReady = FALSE;
                    }
                    break;
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return isReady;
}

/*******************************
*
*   Takes the interval requested in seconds
*   and calculates when the top of the next interval
*   would be
*   ie.  60 seconds interval should be updating
*       once a minute at the top of the minute
********************************
*/
CtiCalc& CtiCalc::setNextInterval( int aInterval )
{
    CtiTime timeNow;
    if(aInterval > 0)
    {
        _nextInterval = nextScheduledTimeAlignedOnRate(timeNow, aInterval).seconds();
    }
    else
        _nextInterval = timeNow.seconds();

    return *this;
}


ULONG CtiCalc::getNextInterval( ) const
{
    return _nextInterval;
}

int CtiCalc::getUpdateInterval( ) const
{
    return _updateInterval;
}

long CtiCalc::findDemandAvgComponentPointId()
{
    long returnPointId = 0;
    RWSlistCollectablesIterator iter( _components );

    //  Iterate through all of the calculations in the collection
    for(;iter();)
    {
        CtiCalcComponent* tmpComponent = (CtiCalcComponent*)iter.key();

        // 20050202 CGP // If the push operator was used, the point we choose will be the LAST one on the stack!
        if(tmpComponent->getComponentPointId() > 0)
        {
            returnPointId = tmpComponent->getComponentPointId();
        }

        const string& functionName = tmpComponent->getFunctionName();
        if( findStringIgnoreCase(functionName, "DemandAvg") )
        {
            if(tmpComponent->getComponentPointId() > 0)
                returnPointId = tmpComponent->getComponentPointId();    // else, it is the last point id found!
            break;
        }
    }

    return returnPointId;
}

CtiTime CtiCalc::calcTimeFromComponentTime( const CtiTime &minTime, const CtiTime &maxTime )
{
    CtiTime rtime;

    if(getUpdateType() != periodic)
    {
        rtime = maxTime;
    }

    return rtime;
}

/*
 *  Determines if all valid calc components have the same timestamp.  Nonupdated and Constant Qualities do not affect the timestamp output.
 */
bool CtiCalc::calcTimeFromComponentTime( CtiTime &componentTime, int componentQuality, CtiTime &minTime, CtiTime &maxTime )
{
    if( componentQuality != NonUpdatedQuality &&        // Timestamps are ignored on non-updated or constant quality points.
        componentQuality != ConstantQuality )
    {
        if(minTime > componentTime)
            minTime = componentTime;

        if(maxTime < componentTime)
            maxTime = componentTime;
    }

    return minTime == maxTime;
}

int CtiCalc::calcQualityFromComponentQuality( int qualityFlag, const CtiTime &minTime, const CtiTime &maxTime )
{
    int component_quality = NormalQuality;

    if(qualityFlag & (1 << ManualQuality) )
    {
        component_quality = ManualQuality;
        qualityFlag &= ~(1 << ManualQuality);
    }

    if(qualityFlag & (1 << ConstantQuality) )
    {
        component_quality = ConstantQuality;
        qualityFlag &= ~(1 << ConstantQuality);
    }

    if(qualityFlag & (1 << NonUpdatedQuality) )
    {
        component_quality = NonUpdatedQuality;
    }
    else if(qualityFlag & ~(1 << NormalQuality))    // There is a bit set other than Normal or NonUpdated.
    {
        component_quality = QuestionableQuality;
    }

    if(getUpdateType() == periodicPlusUpdate)
    {
        if(component_quality == NormalQuality && maxTime.seconds() - minTime.seconds() > getUpdateInterval())
        {
            component_quality = QuestionableQuality;
        }
    }

    return component_quality;
}



double CtiCalc::figureDemandAvg(long secondsInAvg)
{
    double retVal = 0.0;

    try
    {
        CtiTime currenttime;
        // Use the value of TOS.  This will be the pointvalue, but should be the result of any preceeding calculations.
        double componentPointValue = pop();
        long componentId = findDemandAvgComponentPointId();
        long updatesInCurrentAvg = 0;

        if(componentId > 0)
        {
            CtiPointStore* pointStore = CtiPointStore::getInstance();

            CtiHashKey componentHashKey(componentId);
            CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentHashKey]);
            CtiHashKey parentHashKey(getPointId());
            CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore)[&parentHashKey]);

            if( calcPointPtr )
            {
                updatesInCurrentAvg = calcPointPtr->getUpdatesInCurrentAvg();

                // CGP 20050302 We use this count as an indicator that we are beginning a new interval.
                if( updatesInCurrentAvg == 0 )
                {
                    calcPointPtr->setPointCalcWindowEndTime(nextScheduledTimeAlignedOnRate(currenttime, secondsInAvg));
                }
            }

            if(componentPointPtr && calcPointPtr)
            {
                /*
                 *  This is an odd little decision.  Apparently we are using the time between the last two component point
                 *   changes (componentPointPtr->getSecondsSincePreviousPointTime()) as the "slop" value defining the interval ending time.
                 *
                 *   if resolves to if pointdata->time is greater than the windowBEGIN + slop and pointdata->time < windowEND + slop
                 *
                 *   SLOP is very very SLOPPY!  If the data is not coming in on a schedule, the results are questionable.
                 *   If the results are coming in at the rate of the DA, this will compute every other interval.
                 */
                if( componentPointPtr->getPointTime().seconds() >= (calcPointPtr->getPointCalcWindowEndTime().seconds() + componentPointPtr->getSecondsSincePreviousPointTime() - secondsInAvg) &&
                    componentPointPtr->getPointTime().seconds() < (calcPointPtr->getPointCalcWindowEndTime().seconds() + componentPointPtr->getSecondsSincePreviousPointTime()) )
                {//is the last point data received in the average or not
                    if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << endl;
                        dout << "Current Component Point Time: " << componentPointPtr->getPointTime().asString() << endl;
                        dout << "Current Point Calc Window End Time: " << calcPointPtr->getPointCalcWindowEndTime().asString() << endl;
                    }
                    double currentCalcPointValue = calcPointPtr->getPointValue();

                    double currentTotal = currentCalcPointValue * updatesInCurrentAvg;
                    updatesInCurrentAvg++;
                    retVal = (currentTotal + componentPointValue) / updatesInCurrentAvg;

                    if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "Current Calc Point Value: " << currentCalcPointValue << endl;
                        //dout << "Current Total: " << currentTotal << endl;
                        dout << "Updates In Current Avg: " << updatesInCurrentAvg << endl;
                        dout << "Component Point Value: " << componentPointValue << endl;
                        dout << "New Calc Point Value: " << retVal << endl;
                        dout << "Will Send point change at: " << calcPointPtr->getPointCalcWindowEndTime() << endl;
                    }
                }
                else
                {
                    if( updatesInCurrentAvg > 0 )
                    {
                        retVal = componentPointValue;
                        updatesInCurrentAvg = 1;
                        if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << "***********NEW DEMAND AVERAGE BEGUN**************: " << endl;
                            //dout << "Current Component Point Time: " << componentPointPtr->getPointTime().asString() << endl;
                            dout << "Current Point Calc Window End Time: " << calcPointPtr->getPointCalcWindowEndTime().asString() << endl;
                            //dout << "Seconds Since Previous Point Time: " << componentPointPtr->getSecondsSincePreviousPointTime() << endl;
                            dout << "New Initial Demand Avg: " << retVal << endl;
                            dout << "Updates In Current Avg: " << updatesInCurrentAvg << endl;
                            dout << "Previous demand average has a timestamp of: " << calcPointPtr->getPointCalcWindowEndTime() << endl;
                            dout << "Next demand average will have timestamp of: " << CtiTime(calcPointPtr->getPointCalcWindowEndTime().seconds()+secondsInAvg) << endl;
                        }
                    }
                    else
                    {
                        retVal = componentPointValue;
                        updatesInCurrentAvg = 1;
                        if( _CALC_DEBUG & CALC_DEBUG_DEMAND_AVG )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " - Calc Point Id: " << getPointId() << " Demand Avg Reset!" << endl;
                        }
                    }
                    calcPointPtr->setPointCalcWindowEndTime(nextScheduledTimeAlignedOnRate(currenttime, secondsInAvg));
                }

                calcPointPtr->setUpdatesInCurrentAvg(updatesInCurrentAvg);
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Failed point: " << getPointId() << endl;
        }
    }

    return retVal;
}
