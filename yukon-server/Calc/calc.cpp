#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/collect.h>
#include <rw/thr/mutex.h>
#include <rw/rwtime.h>

#include "calc.h"
#include "logger.h"

extern BOOL _CALC_DEBUG;

RWDEFINE_NAMED_COLLECTABLE( CtiCalc, "CtiCalc" );

// static const strings
const CHAR * CtiCalc::UpdateType_Periodic   = "On Timer";
const CHAR * CtiCalc::UpdateType_AllChange  = "On All Change";
const CHAR * CtiCalc::UpdateType_OneChange  = "On First Change";
const CHAR * CtiCalc::UpdateType_Historical = "Historical";


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
//        _updateInterval = 0;
//        _updateType = historical;

        // XXX  invalid for now
//        {
//NO NEED TO PRINT ANYTHING OUT RIGHT NOW...JUST MAKING HUGE FILES OF NOTHING
//            CtiLockGuard<CtiLogger> doubt_guard(dout);
//            dout << "Historical Update Type not supported in calc and logic server." << endl;
//        }
        _valid = FALSE;
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


double CtiCalc::calculate( void )
{
    double retVal = 0.0;
    RWSlistCollectablesIterator iter( _components );

//    _countdown = _updateInterval;

    //  Iterate through all of the calculations in the collection
    if( _CALC_DEBUG )
    {
        CtiPointStore* pointStore = CtiPointStore::getInstance();

        CtiHashKey calcPointHashKey(_pointId);
        CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore)[&calcPointHashKey]);

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " - CtiCalc::calculate(); Calc Point ID:" << _pointId << "; Start Value:" << calcPointPtr->getPointValue() << endl;
    }
    for( ; iter( ) && _valid; )
    {
        CtiCalcComponent *tmpComponent = (CtiCalcComponent *)iter.key( );
        _valid = _valid & tmpComponent->isValid( );  //  Entire calculation is only valid if each component is valid
        retVal = tmpComponent->calculate( retVal );  //  Calculate on returned value
    }

    if( !_valid )   //  NOT valid - actually, you should never get here, because the ready( ) back in CalcThread should
    {               //    detect that you're invalid, and reject you with a "not ready" then.
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << __FILE__ << " (" << __LINE__ << ")  ERROR - attempt to calculate invalid point \"" << _pointId << "\" - returning 0.0" << endl;
        retVal = 0.0;
    }

    if( _CALC_DEBUG )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CtiCalc::calculate(); Calc Point ID:" << _pointId << "; Return Value:" << retVal << endl;
    }
    return retVal;
}

/*  FIX_ME:  I can't manage to dump the guts of a Calc object without using an iterator - and that
               doesn't take a const parameter.
               I don't know how to work around that.  and I'm not too familiar with the whole idea of
               polymorphic persistence in the first place, so...
               Will this even be used?

void CtiCalc::restoreGuts( RWvistream& aStream )
{
   int entries;
   CtiCalcComponent scratchPad;

   aStream >> entries;

   for( int i = 0; i < entries; i++ )
   {
      aStream >> scratchPad;
      (*this) << scratchPad;
   }
}


void CtiCalc::saveGuts(RWvostream &aStream) const
{
   RWSlistCollectablesIterator iter( _components );

   aStream << _components.entries( );

   //  Iterate through all of the calculations in the collection
   for( ; iter( ); )
   {
      aStream << *((CtiCalcComponent *)iter.key( ));
   }
}
*/


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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << __FILE__ << " (" << __LINE__ << ")  ERROR - attempt to pop from empty stack in point \"" << _pointId << "\" - returning 0.0" << endl;
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
    if( !_valid )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        isReady = FALSE;
        dout << RWTime( ) << " - CtiCalc::ready( ) - Point " << _pointId << " is INVALID." << endl;
    }
    else
    {
        switch( _updateType )
        {
            case periodic:
                if (RWTime().seconds() > getNextInterval())
                {
                    isReady = TRUE;
                }
                else
                {
                    isReady = FALSE;
                }
//                isReady = !(--_countdown);  //  NOTE!  do NOT check if ready more than once before calculating
                break;                      //    it decrements the timer for the periodic points
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
   ULONG secondsPastHour;

   // check where we sit
   secondsPastHour = timeNow.seconds() % 3600L;

   // if we are on the interval, go now
   if ((secondsPastHour % aInterval) == 0)
      _nextInterval = timeNow.seconds();
    else
        _nextInterval = timeNow.seconds() + (aInterval - (secondsPastHour % aInterval));

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

