#include "yukon.h"

#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/collect.h>
#include <rw/thr/mutex.h>
#include <rw/rwtime.h>

#include "calc.h"
#include "logger.h"
#include "numstr.h"

extern ULONG _CALC_DEBUG;

RWDEFINE_NAMED_COLLECTABLE( CtiCalc, "CtiCalc" );

// static const strings
const CHAR * CtiCalc::UpdateType_Periodic   = "On Timer";
const CHAR * CtiCalc::UpdateType_AllChange  = "On All Change";
const CHAR * CtiCalc::UpdateType_OneChange  = "On First Change";
const CHAR * CtiCalc::UpdateType_Historical = "Historical";
const CHAR * CtiCalc::UpdateType_PeriodicPlusUpdate = "On Timer+Change";

CtiCalc::CtiCalc( long pointId, const RWCString &updateType, int updateInterval )
{
    _valid = TRUE;
    _pointId = pointId;

    if( (!updateType.compareTo(UpdateType_Periodic, RWCString::ignoreCase))
        && (updateInterval > 0) )
    {
        _updateInterval = updateInterval;
        setNextInterval (updateInterval);
        _updateType = periodic;
    }
    else if( !updateType.compareTo(UpdateType_AllChange, RWCString::ignoreCase))
    {
        _updateInterval = 0;
        _updateType = allUpdate;
    }
    else if( !updateType.compareTo(UpdateType_OneChange, RWCString::ignoreCase))
    {
        _updateInterval = 0;
        _updateType = anyUpdate;
    }
    else if( !updateType.compareTo(UpdateType_Historical, RWCString::ignoreCase))
    {
        _valid = FALSE;
    }
    else if( !updateType.compareTo(UpdateType_PeriodicPlusUpdate, RWCString::ignoreCase) )
    {
        _updateInterval = updateInterval;
        setNextInterval (updateInterval);
        _updateType = periodicPlusUpdate;
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
        _pointCalcWindowEndTime = RWTime(RWDate(1,1,1990));
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
        tmp = new CtiCalcComponent( *((CtiCalcComponent *)copyIterator.key( )) );
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


double CtiCalc::calculate( int &calc_quality, RWTime &calc_time )
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
            dout << RWTime() << " - CtiCalc::calculate(); Calc Point ID:" << _pointId << "; Start Value:" << calcPointPtr->getPointValue() << endl;
        }

        push( retVal );     // Prime the stack with a zero value (should effectively clear it).

        bool solidTime = false;             // If time is "solid" all components are the same time stamp.
        int componentQuality, qualityFlag = 0;
        RWTime componentTime, minTime = RWTime(ULONG_MAX - 86400 * 2), maxTime = rwEpoch;

        for( ; iter( ) && _valid; )
        {
            CtiCalcComponent *tmpComponent = (CtiCalcComponent *)iter.key( );
            _valid = _valid & tmpComponent->isValid( );  //  Entire calculation is only valid if each component is valid

            retVal = tmpComponent->calculate( retVal, componentQuality, componentTime );  //  Calculate on returned value

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
            dout << RWTime() << " - CtiCalc::calculate(); Calc Point ID:" << _pointId << "; Return Value:" << retVal << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION in calculate **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "    Calc Point ID " << _pointId << endl;
        }
    }
    return retVal;
}

void CtiCalc::push( double val )
{
    _stack.push( val );
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
            dout << RWTime() << " ERROR - attempt to pop from empty stack in point \"" << _pointId << "\" - returning 0.0 " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << RWTime( ) << " - CtiCalc::ready( ) - Point " << _pointId << " is INVALID." << endl;
        }
        else
        {
            switch( _updateType )
            {
            case periodic:
                if(RWTime::now().seconds() > getNextInterval())
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
                    if(RWTime::now().seconds() >= getNextInterval())
                    {
                        for( ; iter( ); )
                            isReady &= ((CtiCalcComponent *)(iter.key( )))->isUpdated( _updateType, RWTime(getNextInterval()) );
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
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    RWTime timeNow;

    if(aInterval > 0)
    {
        ULONG secondsPastHour;

        // check where we sit
        secondsPastHour = timeNow.seconds() % 3600L;

        // if we are on the interval, go now
        if((secondsPastHour % aInterval) == 0)
            _nextInterval = timeNow.seconds();
        else
            _nextInterval = timeNow.seconds() + (aInterval - (secondsPastHour % aInterval));
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

const RWTime& CtiCalc::getPointCalcWindowEndTime() const
{
    return _pointCalcWindowEndTime;
}

CtiCalc& CtiCalc::setPointCalcWindowEndTime(const RWTime& endTime)
{
    _pointCalcWindowEndTime = endTime;
    return *this;
}

long CtiCalc::findDemandAvgComponentPointId()
{
    long returnPointId = 0;
    RWSlistCollectablesIterator iter( _components );

    //  Iterate through all of the calculations in the collection
    for(;iter();)
    {
        CtiCalcComponent* tmpComponent = (CtiCalcComponent*)iter.key();
        const RWCString& functionName = tmpComponent->getFunctionName();
        if( !functionName.compareTo("DemandAvg15",RWCString::ignoreCase) ||
            !functionName.compareTo("DemandAvg30",RWCString::ignoreCase) ||
            !functionName.compareTo("DemandAvg60",RWCString::ignoreCase) )
        {
            returnPointId = tmpComponent->getComponentPointId();
            break;
        }
    }

    return returnPointId;
}

RWTime CtiCalc::calcTimeFromComponentTime( const RWTime &minTime, const RWTime &maxTime )
{
    RWTime rtime;

    if(getUpdateType() != periodic)
    {
        if( minTime == maxTime)  // If all components are the same.
        {
            rtime = minTime;
        }
    }

    return rtime;
}

bool CtiCalc::calcTimeFromComponentTime( RWTime &componentTime, int componentQuality, RWTime &minTime, RWTime &maxTime )
{
    if(componentQuality != NonUpdatedQuality)
    {
        if(minTime > componentTime)
            minTime = componentTime;

        if(maxTime < componentTime)
            maxTime = componentTime;
    }

    return minTime == maxTime;
}

int CtiCalc::calcQualityFromComponentQuality( int qualityFlag, const RWTime &minTime, const RWTime &maxTime )
{
    int component_quality = NormalQuality;

    if(qualityFlag & (1 << ManualQuality) )
    {
        component_quality = ManualQuality;
        qualityFlag &= ~(1 << ManualQuality);
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


